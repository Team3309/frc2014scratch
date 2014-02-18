/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.team3309.frc2014.gmhandler;

import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Victor;
import org.team3309.frc2014.constantmanager.ConstantTable;
import org.team3309.frc2014.timer.Timer;

/**
 *
 * @author Ben/Jon
 */
public class Intake {
    
    private Solenoid intakePiston;
    private Victor topRightMotor;
    private Victor topLeftMotor;
    private Victor sideLeftMotor;
    private Victor sideRightMotor;
    private double topMotorSpeed;
    private double sideMotorSpeed;
    private Timer intakeTimer;
    private double extendTime;
    private boolean firstTime;

    //creates intake
    public Intake(){

        System.out.println("Intake constructed");

        double[] intakeTopLeftMotor = ((double[]) ConstantTable.getConstantTable().getValue("Intake.topleft.motor"));
        double[] intakeTopRightMotor = ((double[]) ConstantTable.getConstantTable().getValue("Intake.topright.motor"));
        double[] intakeSideLeftMotor = ((double[]) ConstantTable.getConstantTable().getValue("Intake.sideleft.motor"));
        double[] intakeSideRightMotor = ((double[]) ConstantTable.getConstantTable().getValue("Intake.sideright.motor"));
        double[] loweringPiston = ((double[]) ConstantTable.getConstantTable().getValue("Intake.Solenoid"));
        extendTime = ((Double) ConstantTable.getConstantTable().getValue("Intake.extendTime")).doubleValue();
        topMotorSpeed = ((Double) ConstantTable.getConstantTable().getValue("Intake.topMotorSpeed")).doubleValue();
        sideMotorSpeed = ((Double) ConstantTable.getConstantTable().getValue("Intake.sideMotorSpeed")).doubleValue();
        
        sideRightMotor = new Victor ((int) intakeSideRightMotor[0], (int) intakeSideRightMotor[1]);
        sideLeftMotor = new Victor ((int) intakeSideLeftMotor [0], (int) intakeSideLeftMotor[1]);
        intakePiston = new Solenoid((int) loweringPiston[0], (int) loweringPiston [1]);
      
      if (intakeTopRightMotor[1] != 0){
            topRightMotor = new Victor((int) intakeTopRightMotor[0], (int) intakeTopRightMotor[1]);
      }
      
      if (intakeTopLeftMotor[1] != 0){
            topLeftMotor = new Victor((int) intakeTopLeftMotor[0], (int) intakeTopLeftMotor[1]);
      }
    }

    private void setMotorSpeed(int scaleFactor){
        double correctTopMotorSpeed = scaleFactor * topMotorSpeed;
        double correctSideMotorSpeed = scaleFactor * sideMotorSpeed;

        if (topLeftMotor != null){
            topLeftMotor.set(correctTopMotorSpeed);
        }
        if (topRightMotor != null){
            topRightMotor.set(-correctTopMotorSpeed);
        }
        if (sideLeftMotor != null){
            sideLeftMotor.set(correctSideMotorSpeed);
        }
        if (sideRightMotor != null){
            sideRightMotor.set(-correctSideMotorSpeed);
        }
    }
    public void pullIn(){
        //pulls in ball
        setMotorSpeed(1);
    }

    public void pushOut(){
        //pushes out ball
        setMotorSpeed(-1);
    }

    public void stopMotors(){
        //stops motors holding ball in place
        setMotorSpeed(0);
    }

    public void shiftIntakePos(boolean position){
        if (position){
            //extends Intake
            intakePiston.set(true);
        }
        else {
            //retracts Intake
            intakePiston.set(false);
            intakeTimer.disableTimer();
            firstTime = false;
        }
        if (position && !firstTime){
            intakeTimer.setTimer(extendTime);
            firstTime = true;
        }
    }
    
    public boolean isExtended(){
        return intakeTimer.isExpired();
    }
    
    //resets motors and solenoids so that the constants can be reloaded
    public void free(){
        intakePiston.free();
        if (topRightMotor != null){
            topRightMotor.free();
        }
        if (topLeftMotor != null){
            topLeftMotor.free();
        }

        sideRightMotor.free();
        sideLeftMotor.free();
    }
}
