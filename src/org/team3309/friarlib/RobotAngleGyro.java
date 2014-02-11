/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.team3309.friarlib;

import com.sun.squawk.util.MathUtils;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.Gyro;
import org.team3309.frc2014.constantmanager.ConstantTable;
import java.lang.Math;

/**
 *
 * @author Jon
 */
public class RobotAngleGyro {
    private Gyro gyro;
    private PIDController gyroPIDcontroller;
    private long lastUpdate;
    private double integratedRotation;
    private double maxRotation;
    
    public void update(double rotationToAdd){
        if (lastUpdate == 0){
            lastUpdate = System.currentTimeMillis();
        }
        
    maxRotation = ((Double)ConstantTable.getConstantTable().getValue("Robot.maxRotation")).doubleValue();
    
    long currentUpdate = System.currentTimeMillis();
    double timeElapsed = (currentUpdate - lastUpdate) / 1000d;
    integratedRotation += rotationToAdd * timeElapsed * maxRotation;
    
    }
    //TODO remove commentors
    //public double[] calculateFieldCentricDriveStrafe(double drive, double strafe){
        //double r = Math.sqrt(drive*drive + strafe*strafe);
        //double theta = MathUtils.atan(strafe/drive);
        //double[] fieldCentricDriveStrafe;
       // fieldCentricDriveStrafe[0]
                
       // return fieldCentricDriveStrafe;
  //  }
}

