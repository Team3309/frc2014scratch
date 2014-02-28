/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.team3309.frc2014.drive;


import edu.wpi.first.wpilibj.CounterBase;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.PIDOutput;
import edu.wpi.first.wpilibj.PIDSource.PIDSourceParameter;
import edu.wpi.first.wpilibj.Victor;
import org.team3309.frc2014.constantmanager.ConstantTable;


/**
 *
 * @author Jon
 */
public class OctonumModule implements PIDOutput{
    
    private Victor driveMotor;
    private Encoder encoder;
    private PIDController pidControl;
    private String wheelName;
    private double[] multipliers;
    private double totalError;
    private double pTank;
    private double iTank;
    private double dTank;
    private double fTank;
    private double pMecanum;
    private double iMecanum;
    private double dMecanum;
    private double fMecanum;
    private double pulsesPerInchTank;
    private double pulsesPerInchMecanum;
    private double inchesPerSecondMaxTank;
    private double inchesPerSecondMaxMecanum;
    private double wheelSpeed;
    private boolean isTank;
    private boolean noEncoders;
    private boolean debug;
    private boolean front;
    
    public OctonumModule(String wheelName, boolean isFront){
        this.wheelName = wheelName;
        front = isFront;
        double[] driveMotorArray = ((double[]) ConstantTable.getConstantTable().getValue(wheelName + ".motor"));
        double[] encoderArrayA = ((double[]) ConstantTable.getConstantTable().getValue(wheelName + ".encoderA"));
        double[] encoderArrayB = ((double[]) ConstantTable.getConstantTable().getValue(wheelName + ".encoderB"));
        boolean isEncoderFlipped = ((Boolean) ConstantTable.getConstantTable().getValue(wheelName + ".flipped")).booleanValue();
        debug = ((Boolean) ConstantTable.getConstantTable().getValue(wheelName + ".debug")).booleanValue();
        multipliers = ((double[]) ConstantTable.getConstantTable().getValue(wheelName + ".multipliers"));     
        
        driveMotor = new Victor ((int) driveMotorArray[0],(int) driveMotorArray[1]);

        if (encoderArrayA[0] == 0){
            noEncoders = true;
        } else{

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
            
            pMecanum = ((Double) ConstantTable.getConstantTable().getValue("Octonum.pMecanum")).doubleValue();
            iMecanum = ((Double) ConstantTable.getConstantTable().getValue("Octonum.iMecanum")).doubleValue();
            dMecanum = ((Double) ConstantTable.getConstantTable().getValue("Octonum.dMecanum")).doubleValue();
            fMecanum = ((Double) ConstantTable.getConstantTable().getValue("Octonum.fMecanum")).doubleValue();
            pTank = ((Double) ConstantTable.getConstantTable().getValue("Octonum.pTank")).doubleValue();
            iTank = ((Double) ConstantTable.getConstantTable().getValue("Octonum.iTank")).doubleValue();
            dTank = ((Double) ConstantTable.getConstantTable().getValue("Octonum.dTank")).doubleValue();
            fTank = ((Double) ConstantTable.getConstantTable().getValue("Octonum.fTank")).doubleValue();
            
            pidControl = new PIDController(pMecanum, iMecanum, dMecanum, fMecanum, encoder, this);
            pidControl.enable();
        }
        
    }
    
    public void free(){
        driveMotor.free();
        if (encoder != null){
                encoder.free();
        }        
        pidControl.free();               
    }
    
    /**
     * calculate how the motor should move based on the joystick input
     * @param drive - forward/backward
     * @param rot - rotation
     * @param strafe - left/right (only for Meccanum)
     */
    
    
    public double setRawSpeed(double drive, double rot, double strafe){

        double rotModified = rot * multipliers[1];
        double driveModified = drive * multipliers[0];
        double strafeModified = strafe * multipliers[2];

        if (!front){
            driveModified = driveModified - 1;
        }

        wheelSpeed = driveModified + rotModified;

        if (!isTank){
            wheelSpeed += strafeModified;
        }                         

        return wheelSpeed;
    }
    
    public void setNormalizationFactor(double factor){
        double setpoint = wheelSpeed * factor;
        
        if (noEncoders){
            driveMotor.set(setpoint);
        }
        else{
            if (isTank){
                setpoint *= inchesPerSecondMaxTank;
            }
            else setpoint *= inchesPerSecondMaxMecanum;

            if (setpoint == 0){
                pidControl.disable();
                pidControl.setSetpoint(0);
            }
            else {
                if (!pidControl.isEnable()){
                    //clear the integral term
                    resetPID();
                    pidControl.enable();
                }
                pidControl.setSetpoint(setpoint);
            }
        }
        
    }

    public void enableTank(){
        pidControl.setPID(pTank, iTank, dTank, fTank);
        isTank = true;
        encoder.setDistancePerPulse(pulsesPerInchTank);  
    }
    
    public void enableMecanum(){
        pidControl.setPID(pMecanum, iMecanum, dMecanum, fMecanum);
        isTank = false;
        encoder.setDistancePerPulse(pulsesPerInchMecanum);
    }

    public void disablePIDController(){
        pidControl.disable();
        noEncoders = true;
    }

    public void stopMoving(){
        resetPID();
        pidControl.enable();
        pidControl.setSetpoint(0);
    }

    /*public void breaking(double breakingPower){

    }*/

    private void resetPID(){
        pidControl.reset();
        totalError = 0;
    }

    public void pidWrite(double pidOutput) {        
        totalError += pidControl.getError() * pidControl.getI();
        if (totalError > 1){
            totalError = 1;
        }
        else if (totalError < -1){
            totalError = -1;
        }
        if (debug && encoder.getRate() != 0){
            System.out.println(wheelName + " totalError: " + String.valueOf((float) totalError) + 
                " PIDOutput: " + String.valueOf((float) pidOutput) +
                " Encoder: " + String.valueOf((float) encoder.getRate()) +
                " Setpoint: " + String.valueOf((float) pidControl.getSetpoint()));
          
        }

        driveMotor.set(pidOutput);
    }
}