/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.team3309.frc2014.drive;


import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.SpeedController;
import org.team3309.friarlib.constants.ConstantsManager;
import edu.wpi.first.wpilibj.PIDController;

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
    
    public OctonumModule( Solenoid modePiston,SpeedController speedMotor, Encoder encoder, double[] multipliers ) {
        
        this.modePiston = modePiston;
        this.speedMotor = speedMotor;
        this.encoder = encoder;
        this.multipliers = multipliers;
        this.pidControl = new PIDController(configPMecc, configIMecc, configDMecc, encoder, speedMotor);
                
    }
            
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
                pidControl.setSetpoint(setpoint);
                
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
    
    void enable( boolean active ){
        enabled = active;
    }
    
    void enableMechanum(boolean pistonStatus){
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
