/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.team3309.frc2014.drive;


import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.SpeedController;


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
    static final double configP = 1;
    static final double configI = 0;
    static final double configD = 0;
    private double integral;
    private double lastRate;
    private long lastTime;
    
    public OctonumModule( Solenoid modePiston,SpeedController speedMotor, Encoder encoder, double[] multipliers ) {
        
        this.modePiston = modePiston;
        this.speedMotor = speedMotor;
        this.encoder = encoder;
        this.multipliers = multipliers;
        
    }
            
    void drive(double drive, double rot, double strafe){
        
      
     
       if(enabled){
               
                
               if(lastTime != 0){
                   
                double driveModified = drive * multipliers[0];
                double rotModified = rot * multipliers[1];
                double strafeModified = strafe * multipliers[2];
                   
                double setpoint = driveModified + rotModified + strafeModified;
                double current = encoder.getRate();
                if (isTank){
                    setpoint = (drive + rot);
                }
                
                long currentTime = System.currentTimeMillis();
                double err = current - setpoint;
                integral = integral + err;
                

                double output = err * configP + integral * configI + ((current - lastRate) / (currentTime - lastTime)) * configD;
                speedMotor.set(output);
                lastRate = current; 
                lastTime = currentTime;
               } else {
                   lastTime = System.currentTimeMillis();
               }
               
            }
        
    }
    
    void enable( boolean active ){
        enabled = active;
    }
    
    void enableMechanum(boolean pistonStatus){
        modePiston.set(pistonStatus);
        
    }
    
    
    
        
}
