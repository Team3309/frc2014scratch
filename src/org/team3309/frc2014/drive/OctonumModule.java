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
    private double[] multipliers;
    private boolean isTank;
    private double pTank;
    private double pMecanum;
    private double iTank;
    private double iMecanum;
    private double dTank;
    private double dMecanum;
    private PIDController pidControl;
    private boolean noEncoders;
    private double pulsesPerInchTank;
    private double pulsesPerInchMecanum;
    private String wheelName;
    private double inchesPerSecondMaxTank;
    private double inchesPerSecondMaxMecanum;
    private double wheelSpeed;
    private boolean coastMode;
    
    public OctonumModule(String wheelName){
        this.wheelName = wheelName;
        double[] driveMotorArray = ((double[]) ConstantTable.getConstantTable().getValue(wheelName + ".motor"));
        double[] encoderArrayA = ((double[]) ConstantTable.getConstantTable().getValue(wheelName + ".encoderA"));
        double[] encoderArrayB = ((double[]) ConstantTable.getConstantTable().getValue(wheelName + ".encoderB"));
        boolean isEncoderFlipped = ((Boolean) ConstantTable.getConstantTable().getValue(wheelName + ".flipped")).booleanValue();
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
            pTank = ((Double) ConstantTable.getConstantTable().getValue("Octonum.pTank")).doubleValue();
            iTank = ((Double) ConstantTable.getConstantTable().getValue("Octonum.iTank")).doubleValue();
            dTank = ((Double) ConstantTable.getConstantTable().getValue("Octonum.dTank")).doubleValue();
            
            pidControl = new PIDController(pMecanum, iMecanum, dMecanum, encoder, this);
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
        
        

        double driveModified = drive * multipliers[0];
        double rotModified = rot * multipliers[1];
        double strafeModified = strafe * multipliers[2];

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
            else {
                setpoint *= inchesPerSecondMaxMecanum;
            }
            pidControl.setSetpoint(setpoint);
        }
        
    }

    public void enableTank(){
        pidControl.setPID(pTank, iTank, dTank);
        isTank = true;
        encoder.setDistancePerPulse(pulsesPerInchTank);  
    }
    
    public void enableMecanum(){
        pidControl.setPID(pMecanum, iMecanum, dMecanum);
        isTank = false;
        encoder.setDistancePerPulse(pulsesPerInchMecanum);
    }
    
    public void setCoastMode(boolean coast){
        //detect release of coast button
        if (coastMode && !coast){
            pidControl.reset();
            pidControl.enable();
        }
        coastMode = coast;
    }

    public void pidWrite(double pidOutput) {
        double motorSpeed = driveMotor.getSpeed();
        if ("Octonum.topleft".equals(wheelName) && encoder.getRate() != 0){
            System.out.println("Wheel: " + wheelName + " MotorSpeed: " + String.valueOf(motorSpeed) + 
                " PIDOutput: " + String.valueOf(pidOutput) +
                " Encoder: " + String.valueOf(encoder.getRate()) +
                " Setpoint: " + String.valueOf(pidControl.getSetpoint()));
        }
        motorSpeed = pidOutput;
        if (coastMode){
            motorSpeed = 0;
        }
        driveMotor.set(motorSpeed);
    }
    
}