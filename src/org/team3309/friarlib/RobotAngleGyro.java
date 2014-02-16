/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.team3309.friarlib;

import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.Gyro;
import edu.wpi.first.wpilibj.PIDOutput;
import org.team3309.frc2014.constantmanager.ConstantTable;

/**
 *
 * @author Jon
 */
public class RobotAngleGyro implements PIDOutput{
    private Gyro gyro;
    private PIDController gyroPIDcontroller;
    private long lastUpdate;
    private double maxRotation;
    private double pGyro;
    private double iGyro;
    private double dGyro;
    private double[] ports;
    private double desiredPosition;
    private double desiredVelocity;
    private double pidOutput;
    private boolean debug;
    private double lastDebugMillis;

    public RobotAngleGyro() {
        ports = ((double[]) ConstantTable.getConstantTable().getValue("Gyro.ports"));
        gyro = new Gyro((int) ports[0], (int) ports[1]);
        gyro.reset();
        
        pGyro = ((Double) ConstantTable.getConstantTable().getValue("Gyro.pGyro")).doubleValue();
        iGyro = ((Double) ConstantTable.getConstantTable().getValue("Gyro.iGyro")).doubleValue();
        dGyro = ((Double) ConstantTable.getConstantTable().getValue("Gyro.dGyro")).doubleValue();
        
        gyroPIDcontroller = new PIDController(pGyro, iGyro, dGyro, gyro, this);
        gyroPIDcontroller.enable();
        
        maxRotation = ((Double)ConstantTable.getConstantTable().getValue("Gyro.maxRotation")).doubleValue();
        
        debug = ((Boolean) ConstantTable.getConstantTable().getValue("Gyro.debug")).booleanValue();
    }
    
    public double getDesiredRotation(double joystickRotation){
        if (lastUpdate == 0){
            lastUpdate = System.currentTimeMillis();
        }
    
        long currentUpdate = System.currentTimeMillis();
        double timeElapsed = (currentUpdate - lastUpdate) / 1000d;
        lastUpdate = currentUpdate;
    
        desiredVelocity = joystickRotation * maxRotation;
        desiredPosition += timeElapsed * desiredVelocity;
                
        gyroPIDcontroller.setSetpoint(desiredPosition);
        
        if (debug && lastDebugMillis + 250 < currentUpdate){
            System.out.println(" desired position: " + String.valueOf((float) desiredPosition) +
                    " gyro angle: " + String.valueOf((float) gyro.getAngle() +
                    " pidOutput: " + String.valueOf((float) pidOutput))); 
            lastDebugMillis = currentUpdate;
        }
       
        return pidOutput;
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

    public void pidWrite(double d) {
        pidOutput = d;
    }
}

