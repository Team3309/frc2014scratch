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
    private double desiredPosition;
    private double pidOutput;
    private double lastDebugMillis;
    private double movement;
    private boolean debug;
    private boolean voltageControl;
    private double[] voltagePIDOutputRange;

    public RobotAngleGyro(Gyro gyro) {

        double pGyro = ((Double) ConstantTable.getConstantTable().getValue("Gyro.pGyro")).doubleValue();
        double iGyro = ((Double) ConstantTable.getConstantTable().getValue("Gyro.iGyro")).doubleValue();
        double dGyro = ((Double) ConstantTable.getConstantTable().getValue("Gyro.dGyro")).doubleValue();
        debug = ((Boolean) ConstantTable.getConstantTable().getValue("Gyro.debug")).booleanValue();
        maxRotation = ((Double)ConstantTable.getConstantTable().getValue("Gyro.maxRotation")).doubleValue();
        voltagePIDOutputRange = ((double []) ConstantTable.getConstantTable().getValue("Gyro.voltagePIDoutputRange"));

        this.gyro = gyro;
        gyroPIDcontroller = new PIDController(pGyro, iGyro, dGyro, gyro, this);

        gyro.reset();
        gyroPIDcontroller.enable();
    }
    
    public double getDesiredRotation(double joystickRotation, double movement){

        this.movement = movement;

        if (lastUpdate == 0){
            lastUpdate = System.currentTimeMillis();
        }
    
        long currentUpdate = System.currentTimeMillis();
        double timeElapsed = (currentUpdate - lastUpdate) / 1000d;
        lastUpdate = currentUpdate;
    
        double desiredVelocity = joystickRotation * maxRotation;
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
    }

    public void reset(){
        gyro.reset();
        desiredPosition = 0;
    }

    public void setVoltageControlMode(){
        // Anything less than .3 is not enough to move the bot, this
        // scales it from .3 to 1
        voltageControl = true;
    }

    public void setWheelSpeedControlMode(){
        voltageControl = false;
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
        // Stopping rotation when error is 0
        if (voltageControl){
            if (d > 0 && movement == 0){
                pidOutput = voltagePIDOutputRange[0] + d * (voltagePIDOutputRange[1] - voltagePIDOutputRange[0]);
            }
            else if (d < 0 && movement == 0){
                pidOutput = -voltagePIDOutputRange[0] + d * (voltagePIDOutputRange[1] - voltagePIDOutputRange[0]);
            }
            else {
                pidOutput = d;
            }
        }
        else pidOutput = d;
    }
}

