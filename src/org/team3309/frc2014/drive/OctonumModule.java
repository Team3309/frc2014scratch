/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.team3309.frc2014.drive;


import edu.wpi.first.wpilibj.CounterBase;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.PIDOutput;
import edu.wpi.first.wpilibj.PIDSource.PIDSourceParameter;
import edu.wpi.first.wpilibj.Victor;
import org.team3309.frc2014.constantmanager.ConstantTable;
import org.team3309.friarlib.FriarPIDController;


/**
 *
 * @author Jon
 */
public class OctonumModule implements PIDOutput{
    
    private Victor driveMotor;
    private Encoder encoder;
    private FriarPIDController pidControl;
    private String wheelName;
    private double[] multipliers;
    private double[] mecanumAccelPIDValues;
    private double[] mecanumDecelPIDValues;
    private double[] tankAccelPIDValues;
    private double[] tankDecelPIDValues;
    private double pulsesPerInchTank;
    private double pulsesPerInchMecanum;
    private double inchesPerSecondMaxTank;
    private double inchesPerSecondMaxMecanum;
    private double wheelSpeed;
    private double lastTime;
    private double desiredPosition;
    private double arcFactor;
    private boolean isTank;
    private boolean ignoreEncoders;
    private boolean debug;
    private boolean front;
    private boolean testMode;
    private boolean velocityControl;
    private int lineNum;

    public OctonumModule(String wheelName, boolean isFront){
        this.wheelName = wheelName;
        front = isFront;
        double[] driveMotorArray = ((double[]) ConstantTable.getConstantTable().getValue(wheelName + ".motor"));
        double[] encoderArrayA = ((double[]) ConstantTable.getConstantTable().getValue(wheelName + ".encoderA"));
        double[] encoderArrayB = ((double[]) ConstantTable.getConstantTable().getValue(wheelName + ".encoderB"));
        boolean isEncoderFlipped = ((Boolean) ConstantTable.getConstantTable().getValue(wheelName + ".flipped")).booleanValue();
        debug = ((Boolean) ConstantTable.getConstantTable().getValue(wheelName + ".debug")).booleanValue();
        multipliers = ((double[]) ConstantTable.getConstantTable().getValue(wheelName + ".multipliers"));
        arcFactor = ((Double) ConstantTable.getConstantTable().getValue("Octonum.arcFactor")).doubleValue();
        
        driveMotor = new Victor ((int) driveMotorArray[0],(int) driveMotorArray[1]);

        if (encoderArrayA[0] != 0){

            encoder = new Encoder (
                    (int) encoderArrayA[0], 
                    (int) encoderArrayA[1], 
                    (int) encoderArrayB[0], 
                    (int) encoderArrayB[1],
                    isEncoderFlipped, 
                    CounterBase.EncodingType.k1X);

            pulsesPerInchTank = ((Double) ConstantTable.getConstantTable().getValue("Encoder.pulsesPerInch.Tank")).doubleValue();
            pulsesPerInchMecanum = ((Double) ConstantTable.getConstantTable().getValue("Encoder.pulsesPerInch.Mecanum")).doubleValue();
         
            inchesPerSecondMaxTank = ((Double) ConstantTable.getConstantTable().getValue("Encoder.inchesPerSecondMax.Tank")).doubleValue();
            inchesPerSecondMaxMecanum = ((Double) ConstantTable.getConstantTable().getValue("Encoder.inchesPerSecondMax.Mecanum")).doubleValue();
        
            encoder.setPIDSourceParameter(PIDSourceParameter.kRate);
            encoder.setReverseDirection(isEncoderFlipped);
            encoder.start();

            mecanumAccelPIDValues = ((double[]) ConstantTable.getConstantTable().getValue("Octonum.mecanumAccelPIDValues"));
            mecanumDecelPIDValues = ((double[]) ConstantTable.getConstantTable().getValue("Octonum.mecanumDecelPIDValues"));
            tankAccelPIDValues = ((double[]) ConstantTable.getConstantTable().getValue("Octonum.tankAccelPIDValues"));
            tankDecelPIDValues = ((double[]) ConstantTable.getConstantTable().getValue("Octonum.tankDecelPIDValues"));

            pidControl = new FriarPIDController(mecanumAccelPIDValues, mecanumDecelPIDValues, encoder, this);
            pidControl.setMaxUnits(inchesPerSecondMaxMecanum);
            pidControl.enable();
        }

    }
    
    public void free(){
        driveMotor.free();
        if (encoder != null){
            encoder.free();
            pidControl.free();
        }
    }
    
    /**
     * calculate how the motor should move based on the joystick input
     * @param drive - forward/backward
     * @param rot - rotation
     * @param strafe - left/right (only for Mecanum)
     */
    

    public double setRawSpeed(double drive, double rot, double strafe){

        double rotModified = rot * multipliers[1];
        double driveModified = drive * multipliers[0];
        double strafeModified = strafe * multipliers[2];

        // For smooth arc turns, reduce rotation of wheels at the rear of the direction
        // the bot is driving at. Amount of reduction is proportional to the drive speed.

        if (!front && drive > 0){
            rotModified = rotModified * (1 - (arcFactor * drive));
        }
        if (front && drive < 0) {
            rotModified = rotModified * (1 + (arcFactor * drive));
        }

        wheelSpeed = driveModified + rotModified;

        if (!isTank){
            wheelSpeed += strafeModified;
        }                         

        return wheelSpeed;
    }
    
    public void setNormalizationFactor(double factor){

        double desiredSpeed = wheelSpeed * factor;

        double timeElapsed = lastTime - System.currentTimeMillis();
        lastTime = System.currentTimeMillis();

        desiredPosition += desiredSpeed * timeElapsed;

        if (testMode){
            driveMotor.set(0);
        }

        if (encoder == null || ignoreEncoders){
            driveMotor.set(desiredSpeed);
        }
        else{

            if (!pidControl.isEnable()){
                //clear the integral term
                resetPID();
                pidControl.enable();
            }
            if (velocityControl){
                pidControl.setSetpoint(desiredSpeed);
            }
            else {
                pidControl.setSetpoint(desiredPosition);
            }

        }
    }

    public void enableTank(){
        if (encoder != null){
            pidControl.setPIDValues(tankAccelPIDValues, tankDecelPIDValues);
            encoder.setDistancePerPulse(pulsesPerInchTank);
            pidControl.setMaxUnits(inchesPerSecondMaxTank);
        }
        isTank = true;
    }
    
    public void enableMecanum(){
        if (encoder != null){
            pidControl.setPIDValues(mecanumAccelPIDValues, mecanumDecelPIDValues);
            encoder.setDistancePerPulse(pulsesPerInchMecanum);
            pidControl.setMaxUnits(inchesPerSecondMaxMecanum);
        }
        isTank = false;
    }

    public void togglePIDController(){

        ignoreEncoders = !ignoreEncoders;

        if (ignoreEncoders){
            if (encoder != null){
                pidControl.reset();
            }
        }
        else {
            if (encoder != null){
                pidControl.enable();
            }
        }
    }



    public void freezeInPlace(){
        if (encoder != null && !ignoreEncoders){
            encoder.setPIDSourceParameter(PIDSourceParameter.kDistance);
            pidControl.setSetpoint(0);
            velocityControl = false;
        }
    }

    public void normalMovement(){
        if (encoder != null && !ignoreEncoders){
            encoder.setPIDSourceParameter(PIDSourceParameter.kRate);
            velocityControl = true;
        }
    }

    /*public void breaking(double breakingPower){

    }*/

    private void resetPID(){
        if (encoder != null){
            pidControl.reset();
        }
    }

    public boolean areEncodersEnabled(){
        return (encoder != null && !ignoreEncoders);
    }

    public void pidWrite(double pidOutput) {

        double PIDInput = pidControl.getPIDInput();

        if (debug && (PIDInput != 0 || pidOutput != 0)){
            System.out.println(wheelName + " totalError: " + String.valueOf((float) pidControl.getTotalError()) +
                " PIDOutput: " + String.valueOf((float) pidOutput) +
                " Encoder: " + String.valueOf((float) PIDInput) +
                " Setpoint: " + String.valueOf((float) pidControl.getSetpoint()) + " Ln: " + String.valueOf(lineNum));
            lineNum ++;
        }

        driveMotor.set(pidOutput);
    }

    public void enableTestMode(){
        testMode = true;
    }

    public void disableTestMode(){
        testMode = false;
    }

    /*public void breaking(double breakingPower){

    }*/
}