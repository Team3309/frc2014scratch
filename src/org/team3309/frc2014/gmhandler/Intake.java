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
    private Solenoid reverseIntakePiston;
    private Victor topRightMotor;
    private Victor topLeftMotor;
    private Victor sideLeftMotor;
    private Victor sideRightMotor;
    private Timer intakeTimer;
    private double topMotorSpeed;
    private double sideMotorSpeed;
    private double extendTime;
    private boolean lastPosition;
    private boolean intakePosition;
    private boolean practiceBot;
    private boolean debug;

    //creates intake
    public Intake(){

        double[] intakeTopLeftMotor = ((double[]) ConstantTable.getConstantTable().getValue("Intake.topleft.motor"));
        double[] intakeTopRightMotor = ((double[]) ConstantTable.getConstantTable().getValue("Intake.topright.motor"));
        double[] intakeSideLeftMotor = ((double[]) ConstantTable.getConstantTable().getValue("Intake.sideleft.motor"));
        double[] intakeSideRightMotor = ((double[]) ConstantTable.getConstantTable().getValue("Intake.sideright.motor"));
        double[] loweringPiston = ((double[]) ConstantTable.getConstantTable().getValue("Intake.solenoid"));
        double[] raisingPiston = ((double[]) ConstantTable.getConstantTable().getValue("Intake.reverseSolenoid"));
        extendTime = ((Double) ConstantTable.getConstantTable().getValue("Intake.extendTime")).doubleValue();
        topMotorSpeed = ((Double) ConstantTable.getConstantTable().getValue("Intake.topMotorSpeed")).doubleValue();
        sideMotorSpeed = ((Double) ConstantTable.getConstantTable().getValue("Intake.sideMotorSpeed")).doubleValue();
        practiceBot = ((Boolean) ConstantTable.getConstantTable().getValue("Robot.practiceBot")).booleanValue();
        debug = ((Boolean) ConstantTable.getConstantTable().getValue("Intake.debug")).booleanValue();
        
        sideRightMotor = new Victor ((int) intakeSideRightMotor[0], (int) intakeSideRightMotor[1]);
        sideLeftMotor = new Victor ((int) intakeSideLeftMotor [0], (int) intakeSideLeftMotor[1]);
        intakeTimer = new Timer();
        intakePiston = new Solenoid((int) loweringPiston[0], (int) loweringPiston [1]);
        if (practiceBot){
            reverseIntakePiston = new Solenoid((int) raisingPiston[0], (int) raisingPiston[1]);
            reverseIntakePiston.set(true);
        }
      
      if (intakeTopRightMotor[1] != 0){
            topRightMotor = new Victor((int) intakeTopRightMotor[0], (int) intakeTopRightMotor[1]);
      }
      
      if (intakeTopLeftMotor[1] != 0){
            topLeftMotor = new Victor((int) intakeTopLeftMotor[0], (int) intakeTopLeftMotor[1]);
      }
    }

    public void moveBall(double joystickValue){

        if (topLeftMotor != null){
            topLeftMotor.set(joystickValue);
        }
        if (topRightMotor != null){
            topRightMotor.set(-joystickValue);
        }
        if (sideLeftMotor != null){
            sideLeftMotor.set(-joystickValue);
        }
        if (sideRightMotor != null){
            sideRightMotor.set(joystickValue);
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
        //detect release of intake toggle button
        if (debug){
            System.out.println("Shift intake called");
        }
        if (lastPosition && !position){
            if (debug){
                System.out.println("shifting intake");
            }
            intakePosition = !intakePosition;
            if (!intakePosition){
                intakePiston.set(false);
                if (practiceBot){
                    reverseIntakePiston.set(true);
                }
            }
            else {
                if (practiceBot){
                    reverseIntakePiston.set(false);
                }
                intakePiston.set(true);
            }

            if (intakePosition){
                intakeTimer.setTimer(extendTime);
            }
            else {
                intakeTimer.disableTimer();
            }
        }
        lastPosition = position;
    }
    
    public boolean isExtended(){
        return intakeTimer.isExpired();
    }
    
    //resets motors and solenoids so that the constants can be reloaded
    public void free(){
        intakePiston.free();
        if (practiceBot){
            reverseIntakePiston.free();
        }
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
