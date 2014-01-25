/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.team3309.frc2014.drive;


import edu.wpi.first.wpilibj.CounterBase;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.SpeedController;
import org.team3309.friarlib.constants.ConstantsManager;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.Victor;

/**
 *
 * @author Ben
 */
public class OctonumModule {
    
    private Solenoid modePiston;
    private SpeedController speedMotor;
    private Encoder encoder;
    private double[] multipliers;
    private boolean isTank;
    private boolean enabled;
    private static final double configPTank = ConstantsManager.getConstant("Octonum.configPTank").getDouble(); 
    private static final double configPMecc = ConstantsManager.getConstant("Octonum.configPMecc").getDouble(); 
    private static final double configITank = ConstantsManager.getConstant("Octonum.configITank").getDouble(); 
    private static final double configIMecc = ConstantsManager.getConstant("Octonum.configIMecc").getDouble(); 
    private static final double configDTank = ConstantsManager.getConstant("Octonum.configDTank").getDouble(); 
    private static final double configDMecc = ConstantsManager.getConstant("Octonum.configDMecc").getDouble(); 
    //private double integral;
    //private double lastRate;
    private long lastTime;
    private PIDController pidControl;
    private boolean mode2012;
    
    public OctonumModule( Solenoid modePiston,SpeedController speedMotor, Encoder encoder, double[] multipliers ) {
     
        this.modePiston = modePiston;
        this.speedMotor = speedMotor;
        this.encoder = encoder;
        this.multipliers = multipliers;
        this.pidControl = new PIDController(configPMecc, configIMecc, configDMecc, encoder, speedMotor);
                
    }
    public OctonumModule( int SolenoidPortNumber, int speedMotorPortNumber, int encoderPortNumberA, 
            int encoderPortNumberB, boolean isEncoderFlipped,double[] multipliers){
        if (SolenoidPortNumber == 0){
            mode2012 = true;
        } else{
            this.modePiston = new Solenoid(SolenoidPortNumber);
            this.encoder = new Encoder (encoderPortNumberA, encoderPortNumberB, isEncoderFlipped, CounterBase.EncodingType.k1X);
        }
        
        
        this.speedMotor = new Victor (speedMotorPortNumber);
        
        this.multipliers = multipliers;
    }
    /**
     * calculate how the motor should move based on the joystick input
     * @param drive - forward/backward
     * @param rot - rotation
     * @param strafe - left/right (only for Meccanum)
     */
    
    void drive(double drive, double rot, double strafe){
        
      
     
       if(enabled){
               
                
               if(lastTime != 0){
                   
                double driveModified = drive * multipliers[0];
                double rotModified = rot * multipliers[1];
                double strafeModified = strafe * multipliers[2];
                   
                double setpoint = driveModified + rotModified + strafeModified;
                //double current = encoder.getRate();
                if (isTank){
                    setpoint = (drive + rot);
                }
                
                if (mode2012){
                    speedMotor.set(setpoint);
                
                }else{
                    pidControl.setSetpoint(setpoint); 
                }
                
                
               
                
                /*long currentTime = System.currentTimeMillis();
                double err = current - setpoint;
                integral = integral + err;
                

                double output = err * configP + integral * configI + ((current - lastRate) / (currentTime - lastTime)) * configD;
                speedMotor.set(output);
                lastRate = current; 
                lastTime = currentTime;*/
                
               } else {
                   lastTime = System.currentTimeMillis();
               } 
               
            }
        
    }
    /** 
     * enable OctonumModule
     * @param active 
     */
    
    
    void enable( boolean active ){
        enabled = active;
    }
    
    /**
     * enable TankMode by enabling Piston
     * @param pistonStatus 
     */
    
    void enableTank (boolean pistonStatus){
        
        if (!mode2012){
            if (enabled){
            modePiston.set(pistonStatus);
            //extend = tank
            if (pistonStatus)
            {
                pidControl.setPID(configPTank, configITank, configDTank);
        
            }
            else {
                pidControl.setPID(configPMecc, configIMecc, configDMecc);
            }
            
        }
            
        }
        
    }
    
    
    
        
}
