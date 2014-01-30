/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.team3309.frc2014.drive;

import org.team3309.frc2014.constantmanager.ConstantTable;
import org.team3309.friarlib.constants.Constant;
import org.team3309.friarlib.constants.ConstantsManager;

/**
 *
 * @author Jon/Ben
 */
public class DriveTrain {
    
    
    private OctonumModule[] driveTrainWheels;    
    
    public DriveTrain() {

        this.driveTrainWheels = new OctonumModule[4];
        driveTrainWheels[0] = new OctonumModule(    
                ((Integer) ConstantTable.getConstantTable().getValue("Octonum.topleft.solenoid")).intValue(),
                ((Integer) ConstantTable.getConstantTable().getValue("Octonum.topleft.motor")).intValue(),
                ((Integer) ConstantTable.getConstantTable().getValue("Octonum.topleft.encoderA")).intValue(),
                ((Integer) ConstantTable.getConstantTable().getValue("Octonum.topleft.encoderB")).intValue(),
                ((Boolean) ConstantTable.getConstantTable().getValue("Octonum.topleft.flipped")).booleanValue(),
                ((double[]) ConstantTable.getConstantTable().getValue("Octonum.topleft.multipliers")));
                
        driveTrainWheels[1] = new OctonumModule(
                ((Integer) ConstantTable.getConstantTable().getValue("Octonum.topright.solenoid")).intValue(),
                ((Integer) ConstantTable.getConstantTable().getValue("Octonum.topright.motor")).intValue(),
                ((Integer) ConstantTable.getConstantTable().getValue("Octonum.topright.encoderA")).intValue(),
                ((Integer) ConstantTable.getConstantTable().getValue("Octonum.topright.encoderB")).intValue(),
                ((Boolean) ConstantTable.getConstantTable().getValue("Octonum.topright.flipped")).booleanValue(),
                ((double[]) ConstantTable.getConstantTable().getValue("Octonum.topright.multipliers")));
        
        driveTrainWheels[2] = new OctonumModule(
                ((Integer) ConstantTable.getConstantTable().getValue("Octonum.bottomleft.solenoid")).intValue(),
                ((Integer) ConstantTable.getConstantTable().getValue("Octonum.bottomleft.motor")).intValue(),
                ((Integer) ConstantTable.getConstantTable().getValue("Octonum.bottomleft.encoderA")).intValue(),
                ((Integer) ConstantTable.getConstantTable().getValue("Octonum.bottomleft.encoderB")).intValue(),
                ((Boolean) ConstantTable.getConstantTable().getValue("Octonum.bottomleft.flipped")).booleanValue(),
                ((double[]) ConstantTable.getConstantTable().getValue("Octonum.bottomleft.multipliers")));
        
        driveTrainWheels[3] = new OctonumModule(
                ((Integer) ConstantTable.getConstantTable().getValue("Octonum.bottomright.solenoid")).intValue(),
                ((Integer) ConstantTable.getConstantTable().getValue("Octonum.bottomright.motor")).intValue(),
                ((Integer) ConstantTable.getConstantTable().getValue("Octonum.bottomright.encoderA")).intValue(),
                ((Integer) ConstantTable.getConstantTable().getValue("Octonum.bottomright.encoderB")).intValue(),
                ((Boolean) ConstantTable.getConstantTable().getValue("Octonum.bottomright.flipped")).booleanValue(),
                ((double[]) ConstantTable.getConstantTable().getValue("Octonum.bottomright.multipliers")));
        
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
