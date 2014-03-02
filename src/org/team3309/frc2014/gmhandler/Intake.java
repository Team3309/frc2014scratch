/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.team3309.frc2014.gmhandler;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.SolenoidBase;
import edu.wpi.first.wpilibj.Victor;
import org.team3309.frc2014.constantmanager.ConstantTable;
import org.team3309.frc2014.timer.Timer;

/**
 *
 * @author Ben/Jon
 */
public class Intake {
    
    private SolenoidBase intakePiston;
    private Victor topRightMotor;
    private Victor topLeftMotor;
    private Victor sideLeftMotor;
    private Victor sideRightMotor;
    private Timer intakeTimer;
    private double topMotorSpeed;
    private double sideMotorSpeed;
    private double extendTime;
    private boolean debug;
    private boolean doubleIntakeSolenoid;

    //creates intake
    public Intake(){

        double[] intakeTopLeftMotor = ((double[]) ConstantTable.getConstantTable().getValue("Intake.topleft.motor"));
        double[] intakeTopRightMotor = ((double[]) ConstantTable.getConstantTable().getValue("Intake.topright.motor"));
        double[] intakeSideLeftMotor = ((double[]) ConstantTable.getConstantTable().getValue("Intake.sideleft.motor"));
        double[] intakeSideRightMotor = ((double[]) ConstantTable.getConstantTable().getValue("Intake.sideright.motor"));
        double[] intakePistonArray = ((double[]) ConstantTable.getConstantTable().getValue("Intake.solenoid"));
        extendTime = ((Double) ConstantTable.getConstantTable().getValue("Intake.extendTime")).doubleValue();
        topMotorSpeed = ((Double) ConstantTable.getConstantTable().getValue("Intake.topMotorSpeed")).doubleValue();
        sideMotorSpeed = ((Double) ConstantTable.getConstantTable().getValue("Intake.sideMotorSpeed")).doubleValue();
        debug = ((Boolean) ConstantTable.getConstantTable().getValue("Intake.debug")).booleanValue();
        
        sideRightMotor = new Victor ((int) intakeSideRightMotor[0], (int) intakeSideRightMotor[1]);
        sideLeftMotor = new Victor ((int) intakeSideLeftMotor [0], (int) intakeSideLeftMotor[1]);
        intakeTimer = new Timer();
        if (intakePistonArray[2] == 0){
            intakePiston = new Solenoid((int) intakePistonArray[0], (int) intakePistonArray[1]);
        }
        else {
            intakePiston = new DoubleSolenoid((int) intakePistonArray[0], (int) intakePistonArray[1], (int) intakePistonArray[2]);
            doubleIntakeSolenoid = true;
        }
        if (intakeTopRightMotor[1] != 0){
            topRightMotor = new Victor((int) intakeTopRightMotor[0], (int) intakeTopRightMotor[1]);
        }

        if (intakeTopLeftMotor[1] != 0){
            topLeftMotor = new Victor((int) intakeTopLeftMotor[0], (int) intakeTopLeftMotor[1]);
        }
    }

    public void moveBall(double joystickValue){

        // Some motors start at different voltages, this insures
        // that all motors start spinning at the same time
        if (joystickValue > 0){
            joystickValue = .3 + (.7 * joystickValue);
        }
        else if (joystickValue < 0){
            joystickValue = -.3 + (.7 * joystickValue);
        }

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

    public void retractIntake(){
        if (debug){
            System.out.println("Retracting Intake");
        }
        intakeTimer.disableTimer();

        if (doubleIntakeSolenoid){
            ((DoubleSolenoid) intakePiston).set(DoubleSolenoid.Value.kReverse);
        }
        else ((Solenoid) intakePiston).set(false);
    }

    public void extendIntake(){
        if (debug){
            System.out.println("Extending Intake");
        }

        if (doubleIntakeSolenoid){
            ((DoubleSolenoid) intakePiston).set(DoubleSolenoid.Value.kForward);
        }
        else ((Solenoid) intakePiston).set(true);

        intakeTimer.setTimer(extendTime);
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
