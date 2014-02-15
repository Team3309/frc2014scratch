/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.team3309.frc2014.drive;
import edu.wpi.first.wpilibj.PIDOutput;
/**
 *
 * @author jon
 */

public class DriveHeading implements PIDOutput{

    private double pidValue;
    
    public void pidWrite(double d) {
        pidValue = d;
    }
    
    public double getPIDvalue(){
        return pidValue;
    }    
}
