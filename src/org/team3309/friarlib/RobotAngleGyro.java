/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.team3309.friarlib;

import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.Gyro;
import edu.wpi.first.wpilibj.PIDOutput;
import edu.wpi.first.wpilibj.PIDSource;
import org.team3309.frc2014.constantmanager.ConstantTable;

/**
 *
 * @author Jon
 */
public class RobotAngleGyro implements PIDOutput{
    private Gyro gyro;
    private PIDController gyroPIDController;
    private long lastUpdate;
    private boolean debug;
    private boolean voltageControl;
    private boolean isTank;
    private double maxRotation;
    private double desiredPosition;
    private double pidOutput;
    private double lastDebugMillis;
    private double movement;
    private double desiredVelocity;
    private double[] voltagePIDOutputRange;
    private double[] gyroPIDArrayMecanum;
    private double[] gyroPIDArrayTank;

    public RobotAngleGyro(Gyro gyro) {

        gyroPIDArrayMecanum = ((double[]) ConstantTable.getConstantTable().getValue("Gyro.pidValuesMecanum"));
        gyroPIDArrayTank = ((double[]) ConstantTable.getConstantTable().getValue("Gyro.pidValuesTank"));
        debug = ((Boolean) ConstantTable.getConstantTable().getValue("Gyro.debug")).booleanValue();
        maxRotation = ((Double)ConstantTable.getConstantTable().getValue("Gyro.maxRotation")).doubleValue();
        voltagePIDOutputRange = ((double []) ConstantTable.getConstantTable().getValue("Gyro.voltagePIDOutputRange"));

        this.gyro = gyro;
        gyroPIDController = new PIDController(gyroPIDArrayMecanum[0], gyroPIDArrayMecanum[1], gyroPIDArrayMecanum[2], gyroPIDArrayMecanum[3], gyro, this);

        gyro.reset();
        gyroPIDController.enable();
    }

    //Returns desired angular velocity
    public double getDesiredRotationVelocity(double joystickRotation, double movement){

        if (isTank){
            gyroPIDController.setPID(gyroPIDArrayTank[0], gyroPIDArrayTank[1], gyroPIDArrayTank[2], gyroPIDArrayTank[3]);
        }
        else {
            gyroPIDController.setPID(gyroPIDArrayMecanum[0], gyroPIDArrayMecanum[1], gyroPIDArrayMecanum[2], gyroPIDArrayMecanum[3]);
        }

        //Sets gyro to send the angular velocity to the PID controller versus current angle
        gyro.setPIDSourceParameter(PIDSource.PIDSourceParameter.kRate);
        this.movement = movement;
    
        long currentUpdate = System.currentTimeMillis();
        desiredVelocity = joystickRotation * maxRotation;

        gyroPIDController.setSetpoint(desiredVelocity);
        
        if (debug && lastDebugMillis + 250 < currentUpdate){
            System.out.println(" desired velocity: " + String.valueOf((float) desiredVelocity) +
                    " gyro velocity: " + String.valueOf((float) gyro.getRate() +
                    " pidOutput: " + String.valueOf((float) pidOutput))); 
            lastDebugMillis = currentUpdate;
        }
       
        return pidOutput;
    }

    //Saved for use in freeze in place button to maintain current angle position
    public double getDesiredRotationPosition(double joystickRotation, double movement){

        if (isTank){
            gyroPIDController.setPID(gyroPIDArrayTank[0], gyroPIDArrayTank[1], gyroPIDArrayTank[2], gyroPIDArrayTank[3]);
        }
        else {
            gyroPIDController.setPID(gyroPIDArrayMecanum[0], gyroPIDArrayMecanum[1], gyroPIDArrayMecanum[2], gyroPIDArrayMecanum[3]);
        }

        gyro.setPIDSourceParameter(PIDSource.PIDSourceParameter.kAngle);
        this.movement = movement;

        if (lastUpdate == 0){
            lastUpdate = System.currentTimeMillis();
        }

        long currentUpdate = System.currentTimeMillis();
        double timeElapsed = (currentUpdate - lastUpdate) / 1000d;
        lastUpdate = currentUpdate;

        double desiredVelocity = joystickRotation * maxRotation;
        desiredPosition += timeElapsed * desiredVelocity;

        gyroPIDController.setSetpoint(desiredPosition);

        if (debug && lastDebugMillis + 250 < currentUpdate){
            System.out.println(" desired position: " + String.valueOf((float) desiredPosition) +
                    " gyro angle: " + String.valueOf((float) gyro.getAngle() +
                    " pidOutput: " + String.valueOf((float) pidOutput)));
            lastDebugMillis = currentUpdate;
        }

        return pidOutput;
    }
    
    public void free(){
        gyroPIDController.free();
    }

    public void reset(){
        gyro.reset();
        desiredVelocity = 0;
    }

    public void setTank(){
        isTank = true;
    }

    public void setMecanum(){
        isTank = false;
    }

    public void setVoltageControlMode(){
        voltageControl = true;
    }

    public void setWheelSpeedControlMode(){
        voltageControl = false;
    }

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

    //DONT REMOVE FOR FIELD CENTRIC INCOMPLETE
    //public double[] calculateFieldCentricDriveStrafe(double drive, double strafe){
    //double r = Math.sqrt(drive*drive + strafe*strafe);
    //double theta = MathUtils.atan(strafe/drive);
    //double[] fieldCentricDriveStrafe;
    // fieldCentricDriveStrafe[0]

    // return fieldCentricDriveStrafe;
    //  }
}

