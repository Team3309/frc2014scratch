/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.team3309.frc2014.drive;

import org.team3309.friarlib.constants.Constant;
import org.team3309.friarlib.constants.ConstantsManager;

/**
 *
 * @author Ben
 */
public class DriveTrain {
    
    
    private OctonumModule[] driveTrainWheels;
    
    private static final Constant topLeftSolenoidPort = new Constant("Octonum.topleft.solenoid", 0);
    private static final Constant topLeftMotorPort = new Constant("Octonum.topleft.motor", 3);
    private static final Constant topLeftEncoderAPort = new Constant("Octonum.topleft.encoderA", 0);
    private static final Constant topLeftEncoderBPort = new Constant("Octonum.topleft.encoderB", 0);
    private static final Constant topLeftFlipped = new Constant("Octonum.topleft.flipped", true);
    
    private static final Constant topRightSolenoidPort = new Constant("Octonum.topright.solenoid", 0);
    private static final Constant topRightMotorPort = new Constant("Octonum.topright.motor", 4);
    private static final Constant topRightEncoderAPort = new Constant("Octonum.topright.encoderA", 0);
    private static final Constant topRightEncoderBPort = new Constant("Octonum.topright.encoderB", 0);
    private static final Constant topRightFlipped = new Constant("Octonum.topright.flipped", false);
    
    private static final Constant bottomLeftSolenoidPort = new Constant("Octonum.bottomleft.solenoid", 0);
    private static final Constant bottomLeftMotorPort = new Constant("Octonum.bottomleft.motor", 1);
    private static final Constant bottomLeftEncoderAPort = new Constant("Octonum.bottomleft.encoderA", 0);
    private static final Constant bottomLeftEncoderBPort = new Constant("Octonum.bottomleft.encoderB", 0);
    private static final Constant bottomLeftFlipped = new Constant("Octonum.bottomleft.flipped", true);
    
    private static final Constant bottomRightSolenoidPort = new Constant("Octonum.bottomright.solenoid", 0);
    private static final Constant bottomRightMotorPort = new Constant("Octonum.bottomright.motor", 2);
    private static final Constant bottomRightEncoderAPort = new Constant("Octonum.bottomright.encoderA", 0);
    private static final Constant bottomRightEncoderBPort = new Constant("Octonum.bottomright.encoderB", 0);
    private static final Constant bottomRightFlipped = new Constant("Octonum.bottomright.flipped", false);
    
    public DriveTrain() {

        this.driveTrainWheels = new OctonumModule[4];
        driveTrainWheels[0] = new OctonumModule(    
                    topLeftSolenoidPort.getInt(), 
                    topLeftMotorPort.getInt(), 
                    topLeftEncoderAPort.getInt(),
                    topLeftEncoderBPort.getInt(),
                    topLeftFlipped.getBoolean() ,topleftmultiplier );
        
        driveTrainWheels[1] = new OctonumModule(
                    topRightSolenoidPort.getInt(), 
                    topRightMotorPort.getInt(), 
                    topRightEncoderAPort.getInt(),
                    topRightEncoderBPort.getInt(),
                    topRightFlipped.getBoolean() ,toprightmultiplier);
        
        driveTrainWheels[2] = new OctonumModule(
                    bottomLeftSolenoidPort.getInt(), 
                    bottomLeftMotorPort.getInt(), 
                    bottomLeftEncoderAPort.getInt(),
                    bottomLeftEncoderBPort.getInt(),
                    bottomLeftFlipped.getBoolean() ,bottomleftmultiplier);
        
        driveTrainWheels[3] = new OctonumModule(
                   bottomRightSolenoidPort.getInt(), 
                    bottomRightMotorPort.getInt(), 
                    bottomRightEncoderAPort.getInt(),
                    bottomRightEncoderBPort.getInt(),
                    bottomRightFlipped.getBoolean(), bottomrightmultiplier);
        
    }
  
    //right multipliers flipped for 2012, may need to flip back for 2014 bot!!!
    static final double[] topleftmultiplier = {1, 1, 1};
    static final double[] toprightmultiplier = {-1, 1, 1};
    static final double[] bottomleftmultiplier = {1, 1, -1};
    static final double[] bottomrightmultiplier = {-1, 1, -1};
    
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
