/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.team3309.frc2014.drive;


import edu.wpi.first.wpilibj.CounterBase;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.Victor;
import org.team3309.frc2014.constantmanager.ConstantTable;

/**
 *
 * @author Jon
 */
public class OctonumModule {
    
    private SpeedController driveMotor;
    private Encoder encoder;
    private double[] multipliers;
    private boolean isTank;
    private static final double configPTank = 1;
    private static final double configPMecc = 1; 
    private static final double configITank = 0; 
    private static final double configIMecc = 0; 
    private static final double configDTank = 0; 
    private static final double configDMecc = 0; 
    //private double integral;
    //private double lastRate;
    private long lastTime;
    private PIDController pidControl;
    private boolean noEncoders;
    
    public OctonumModule(String wheelName){
       
        double[] driveMotorArray = ((double[]) ConstantTable.getConstantTable().getValue(wheelName + ".motor"));
        double[] encoderArrayA = ((double[]) ConstantTable.getConstantTable().getValue(wheelName + ".encoderA"));
        double[] encoderArrayB = ((double[]) ConstantTable.getConstantTable().getValue(wheelName + ".encoderB"));
        boolean isEncoderFlipped = ((Boolean) ConstantTable.getConstantTable().getValue(wheelName + ".flipped")).booleanValue();
        multipliers = ((double[]) ConstantTable.getConstantTable().getValue(wheelName + ".multipliers"));     
        
        driveMotor = new Victor ((int) driveMotorArray[0],(int) driveMotorArray[1]);
                
        if (encoderArrayA[1] == 0){
            noEncoders = true;
        } else{

            encoder = new Encoder (
                    (int) encoderArrayA[0], 
                    (int) encoderArrayA[1], 
                    (int) encoderArrayB[0], 
                    (int) encoderArrayB[1],
                    isEncoderFlipped, 
                    CounterBase.EncodingType.k1X);
            pidControl = new PIDController(configPMecc, configIMecc, configDMecc, encoder, driveMotor);
        }
        
    }
    /**
     * calculate how the motor should move based on the joystick input
     * @param drive - forward/backward
     * @param rot - rotation
     * @param strafe - left/right (only for Meccanum)
     */
    
    public void drive(double drive, double rot, double strafe){               

        if (lastTime != 0){

            double driveModified = drive * multipliers[0];
            double rotModified = rot * multipliers[1];
            double strafeModified = strafe * multipliers[2];

            double setpoint = driveModified + rotModified + strafeModified;
            //double current = encoder.getRate();
            if (isTank){
                setpoint = (drive + rot);
            }

            if (noEncoders){
                driveMotor.set(setpoint);

            }
            else{
                pidControl.setSetpoint(setpoint); 
            }


            /*long currentTime = System.currentTimeMillis();
            double err = current - setpoint;
            integral = integral + err;


            double output = err * configP + integral * configI + ((current - lastRate) / (currentTime - lastTime)) * configD;
            speedMotor.set(output);
            lastRate = current; 
            lastTime = currentTime;*/

        }
        else {
           lastTime = System.currentTimeMillis();
        } 

    }

    public void enableTank(){
        pidControl.setPID(configPTank, configITank, configDTank);
    }
    
    public void enableMecanum(){
        pidControl.setPID(configPMecc, configIMecc, configDMecc);
    }    
}