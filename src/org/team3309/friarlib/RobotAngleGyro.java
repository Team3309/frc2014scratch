/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.team3309.friarlib;

import com.sun.squawk.util.MathUtils;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.Gyro;
import org.team3309.frc2014.constantmanager.ConstantTable;
import org.team3309.frc2014.drive.DriveHeading;
import java.lang.Math;

/**
 *
 * @author Jon
 */
public class RobotAngleGyro {
    private Gyro gyro;
    private PIDController gyroPIDcontroller;
    private long lastUpdate;
    private double maxRotation;
    private DriveHeading driveHeading;
    private double pGyro;
    private double iGyro;
    private double dGyro;
    private double[] ports;
    private double desiredPosition;
    private double desiredVelocity;
    private double desiredAccleration;
    private double modifiedRotation;

    public RobotAngleGyro() {
        ports = ((double[]) ConstantTable.getConstantTable().getValue("Gyro.port"));
        gyro = new Gyro((int) ports[0], (int) ports[1]);
        
        pGyro = ((Double) ConstantTable.getConstantTable().getValue("Gyro.pGyro")).doubleValue();
        iGyro = ((Double) ConstantTable.getConstantTable().getValue("Gyro.iGyro")).doubleValue();
        dGyro = ((Double) ConstantTable.getConstantTable().getValue("Gyro.dGyro")).doubleValue();
        
        gyroPIDcontroller = new PIDController(pGyro, iGyro, dGyro, gyro, driveHeading);
    }
    
    public double getDesiredRotation(double joystickRotation){
        if (lastUpdate == 0){
            lastUpdate = System.currentTimeMillis();
        }
        
        maxRotation = ((Double)ConstantTable.getConstantTable().getValue("Robot.maxRotation")).doubleValue();
    
        long currentUpdate = System.currentTimeMillis();
        double timeElapsed = (currentUpdate - lastUpdate) / 1000d;
    
        desiredAccleration = joystickRotation * maxRotation;
        desiredVelocity += timeElapsed * desiredAccleration;
        desiredPosition += timeElapsed * desiredVelocity;
                
        gyroPIDcontroller.setSetpoint(desiredPosition);
        
        modifiedRotation = driveHeading.getPIDvalue();
        return modifiedRotation;
    }
    
    public void free(){
        gyroPIDcontroller.free();
        gyro.free();
    }
    
    //DONT REMOVE FOR FIELD CENTRIC INCOMPLETE
    //public double[] calculateFieldCentricDriveStrafe(double drive, double strafe){
        //double r = Math.sqrt(drive*drive + strafe*strafe);
        //double theta = MathUtils.atan(strafe/drive);
        //double[] fieldCentricDriveStrafe;
       // fieldCentricDriveStrafe[0]
                
       // return fieldCentricDriveStrafe;
  //  }
}

