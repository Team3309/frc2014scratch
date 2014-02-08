/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.team3309.frc2014.subsystems;
import edu.wpi.first.wpilibj.command.Subsystem;
import org.team3309.frc2014.drive.OctonumModule;


/**
 *
 * @author Ben(90%)/Jon(5%)/luck(5%)
 */
public class DriveTrain extends Subsystem{
    
    
    private OctonumModule[] driveTrainWheels;    
    
    public DriveTrain() {

        this.driveTrainWheels = new OctonumModule[4];
        driveTrainWheels[0] = new OctonumModule("Octonum.topleft");    
        driveTrainWheels[1] = new OctonumModule("Octonum.topright"); 
        driveTrainWheels[2] = new OctonumModule("Octonum.bottomleft");            
        driveTrainWheels[3] = new OctonumModule("Octonum.bottomright");
  
    }

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

    protected void initDefaultCommand() {
    }
    
}
