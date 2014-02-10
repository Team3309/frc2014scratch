/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.team3309.friarlib;

import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.Gyro;
import org.team3309.frc2014.constantmanager.ConstantTable;

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
        //need to find way to cast object as double, idk why its not working
    //maxRotation = ConstantTable.getConstantTable().getValue("Robot.maxRotation");
    long currentUpdate = System.currentTimeMillis();
    double timeElapsed = (currentUpdate - lastUpdate) / 1000d;
    integratedRotation += rotationToAdd * timeElapsed * maxRotation;
    
    }
}
