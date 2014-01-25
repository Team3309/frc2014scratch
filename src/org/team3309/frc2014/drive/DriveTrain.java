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
import org.team3309.friarlib.constants.ConstantsManager;

/**
 *
 * @author Ben
 */
public class DriveTrain {
    
    
    private OctonumModule[] driveTrainWheels;
    
    
    public DriveTrain() {

        this.driveTrainWheels = new OctonumModule[4];
        driveTrainWheels[0] = new OctonumModule(    
                    ConstantsManager.getConstant("Octonum.topleft.solenoid").getInt(), 
                    ConstantsManager.getConstant("Octonum.topleft.motor").getInt(), 
                    ConstantsManager.getConstant("Octonum.topleft.encoderA").getInt(),
                    ConstantsManager.getConstant("Octonum.topleft.encoderB").getInt(),
                    ConstantsManager.getConstant("Octonum.topleft.flipped").getBoolean() ,topleftmultiplier );
        
        driveTrainWheels[1] = new OctonumModule(
                    ConstantsManager.getConstant("Octonum.topright.solenoid").getInt(), 
                    ConstantsManager.getConstant("Octonum.topright.motor").getInt(), 
                    ConstantsManager.getConstant("Octonum.topright.encoderA").getInt(),
                    ConstantsManager.getConstant("Octonum.topright.encoderB").getInt(),
                    ConstantsManager.getConstant("Octonum.topright.flipped").getBoolean() ,toprightmultiplier);
        
        driveTrainWheels[2] = new OctonumModule(
                    ConstantsManager.getConstant("Octonum.bottomleft.solenoid").getInt(), 
                    ConstantsManager.getConstant("Octonum.bottomleft.motor").getInt(), 
                    ConstantsManager.getConstant("Octonum.bottomleft.encoderA").getInt(),
                    ConstantsManager.getConstant("Octonum.bottomleft.encoderB").getInt(),
                    ConstantsManager.getConstant("Octonum.bottomleft.flipped").getBoolean() ,bottomleftmultiplier);
        
        driveTrainWheels[3] = new OctonumModule(ConstantsManager.getConstant("Octonum.bottomright.solenoid").getInt(), 
                    ConstantsManager.getConstant("Octonum.bottomright.motor").getInt(), 
                    ConstantsManager.getConstant("Octonum.bottomright.encoderA").getInt(),
                    ConstantsManager.getConstant("Octonum.bottomright.encoderB").getInt(),
                    ConstantsManager.getConstant("Octonum.bottomright.flipped").getBoolean(),bottomrightmultiplier);
        
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
