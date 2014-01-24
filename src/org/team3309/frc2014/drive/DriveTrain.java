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
import edu.wpi.first.wpilibj.Victor;
import org.team3309.frc2014.RobotMap;

/**
 *
 * @author Ben
 */
public class DriveTrain {
    
    
    private OctonumModule[] driveTrainWheels;
    
    
    public DriveTrain() {

        this.driveTrainWheels = new OctonumModule[4];
        driveTrainWheels[0] = new OctonumModule(RobotMap.topleftsolenoid,RobotMap.topleftmotor,
                RobotMap.topleftencoderA,RobotMap.topleftencoderB,true,topleftmultiplier );
        driveTrainWheels[1] = new OctonumModule(RobotMap.toprightsolenoid,RobotMap.toprightmotor,
                RobotMap.toprightencoderA,RobotMap.toprightencoderB,false,toprightmultiplier);
        driveTrainWheels[2] = new OctonumModule(RobotMap.bottomleftsolenoid,RobotMap.bottomleftmotor,
                RobotMap.bottomleftencoderA,RobotMap.bottomleftencoderB,true,bottomleftmultiplier);
        driveTrainWheels[3] = new OctonumModule(RobotMap.bottomrightsolenoid,RobotMap.bottomrightmotor,
                RobotMap.bottomrightencoderA,RobotMap.bottomrightencoderB,false,bottomrightmultiplier);
        
    }
  
    
    static final double[] topleftmultiplier = {1, 1, 1};
    static final double[] toprightmultiplier = {1, -1, -1};
    static final double[] bottomleftmultiplier = {1, 1, -1};
    static final double[] bottomrightmultiplier = {1, -1, 1};
    
    public void drive(double drive, double rot, double strafe) {
        for (int i = 0; i < 4; i++) {
            driveTrainWheels[i].drive(drive, rot, strafe);
            
            
        }
            
        
        
    
    }
    
    public void enable (boolean active){
        
        for (int i = 0; i < 4; i++){
            driveTrainWheels[i].enable(active);
        }
    }
    
    public void enableTank (boolean active){
        for (int i = 0; i < 4; i++){
            driveTrainWheels[i].enableTank(active);
        }
    }
    
}
