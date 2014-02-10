/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.team3309.frc2014.subsystems;
import org.team3309.frc2014.drive.OctonumModule;
import edu.wpi.first.wpilibj.Solenoid;
import org.team3309.frc2014.constantmanager.ConstantTable;
import java.lang.String;

/**
 *
 * @author Jon/Ben
 */
public class DriveTrain{
    
    private Solenoid modePiston;   
    private OctonumModule[] driveTrainWheels;
    private boolean noSolenoids;
    
    public DriveTrain() {
        
        double[] solenoidArray = ((double[]) ConstantTable.getConstantTable().getValue("Octonum.solenoid"));
        if (solenoidArray[1] == 0){
            noSolenoids = true;
        }
        
        if (!noSolenoids){
            modePiston = new Solenoid((int) solenoidArray[0], (int) solenoidArray[1]);
        }
        
        driveTrainWheels = new OctonumModule[4];
        driveTrainWheels[0] = new OctonumModule("Octonum.topleft");    
        driveTrainWheels[1] = new OctonumModule("Octonum.topright"); 
        driveTrainWheels[2] = new OctonumModule("Octonum.bottomleft");            
        driveTrainWheels[3] = new OctonumModule("Octonum.bottomright");
  
        enableMecanum();
    }

    public void drive(double drive, double rot, double strafe) {
        System.out.println("drive: " + String.valueOf(drive) + " rot: " + String.valueOf(rot) + " strafe: " + String.valueOf(strafe));
        
        
        for (int i = 0; i < 4; i++) {
            driveTrainWheels[i].drive(drive, rot, strafe);
            
            
        }
                     
    }
    
        /**
     * enable TankMode by enabling Piston
     */
    
    public void enableTank(){
        if (!noSolenoids){
            modePiston.set(true);
            for (int i = 0; i < 4; i++) {
                driveTrainWheels[i].enableTank();
            }
        }
    }
    
    public void enableMecanum(){
        if (!noSolenoids){
            modePiston.set(false);
        }
        for (int i = 0; i < 4; i++) {
            driveTrainWheels[i].enableMecanum();
        }
    }
}