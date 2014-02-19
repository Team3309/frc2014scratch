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
import edu.wpi.first.wpilibj.DigitalInput;

/**
 *
 * @author JKav
 */
public class Launcher {

    private Victor winchTopMotor;
    private Victor winchBottomMotor;
    private Solenoid latchPiston;
    private Solenoid dogPiston;
    private double motorSpeed;
    private int catapultStatus;
    private static final int readyToLaunch = 1;
    private static final int launching = 2;
    private static final int engagingDog = 3;
    private static final int winching = 4;
    private static final int stoppingMotors = 5;
    private static final int disengagingDog = 6;
    private static final int errorLaunch = 7;
    private static final int errorResetting = 8;
    private static final int disabled = 9;
    private Timer catapultTimer;
    private Timer stoppingMotorTimer;
    private Timer dogTimer;
    private int launchErrorCount;
    private double launchTime;
    private double stoppingMotorTime;
    private double dogTime;
    private DigitalInput catapultSensor;
    private DigitalInput latchSensor;
    private boolean safeToRetractIntake;
    private boolean launcherEnabled;

    public Launcher() {
        double [] launcherWinchMotorBot = ((double[]) ConstantTable.getConstantTable().getValue("Launcher.winchMotorBot"));
        double [] launcherWinchMotorTop = ((double[]) ConstantTable.getConstantTable().getValue("Launcher.winchMotorTop"));
        double [] launcherLatchPiston = ((double[]) ConstantTable.getConstantTable().getValue("Launcher.latchSolenoid"));
        double [] launcherDogPiston = ((double[]) ConstantTable.getConstantTable().getValue("Launcher.dogSolenoid"));
        double [] launcherCatapultSensor = ((double[]) ConstantTable.getConstantTable().getValue("Launcher.catapultSensor"));
        double [] launcherLatchSensor = ((double[]) ConstantTable.getConstantTable().getValue("Launcher.latchSensor"));
        motorSpeed = ((Double) ConstantTable.getConstantTable().getValue("Launcher.motorSpeed")).doubleValue();
        launcherEnabled = ((Boolean) ConstantTable.getConstantTable().getValue("Launcher.enabled")).booleanValue();

        //Times are in seconds
        launchTime = ((Double) ConstantTable.getConstantTable().getValue("Launcher.launchTime")).doubleValue();
        stoppingMotorTime = ((Double) ConstantTable.getConstantTable().getValue("Launcher.stoppingMotorTime")).doubleValue();
        dogTime = ((Double) ConstantTable.getConstantTable().getValue("Launcher.dogTime")).doubleValue();

        winchBottomMotor = new Victor((int) launcherWinchMotorBot[0], (int) launcherWinchMotorBot[1]);
        winchTopMotor = new Victor((int) launcherWinchMotorTop[0], (int) launcherWinchMotorTop[1]);
        latchPiston = new Solenoid((int) launcherLatchPiston[0], (int) launcherLatchPiston[1]);
        dogPiston = new Solenoid((int) launcherDogPiston[0], (int) launcherDogPiston[1]);
        catapultSensor = new DigitalInput((int) launcherCatapultSensor[0], (int) launcherCatapultSensor[1]);
        latchSensor = new DigitalInput((int) launcherLatchSensor[0], (int) launcherLatchSensor[1]);
        catapultTimer = new Timer();
        stoppingMotorTimer = new Timer();
        dogTimer = new Timer();

        catapultStatus = readyToLaunch;
    }

    public boolean isCatapultInPos() {
        return catapultSensor.get();
    }
    
    public boolean isCatapultLatched() {
        return latchSensor.get();
    }
    public void launch(boolean buttonPressed) {

        //Status ready to launch
        if (catapultStatus == readyToLaunch) {
            if (launcherEnabled && buttonPressed) {
                catapultStatus = launching;
                catapultTimer.setTimer(launchTime);
                safeToRetractIntake = false;
                latchPiston.set(true);
                launchErrorCount = 0;
            }
            safeToRetractIntake = true;
        }

        //Status launching
        if (catapultStatus == launching) {
            if (catapultTimer.isExpired()) {
                //check for good launch
                if (!isCatapultLatched() && !isCatapultInPos()) {
                    latchPiston.set(false);
                    catapultTimer.disableTimer();
                    dogPiston.set(true);
                    dogTimer.setTimer(dogTime);
                    catapultStatus = engagingDog;
                }
                else {
                    launchErrorCount++;
                    catapultStatus = errorLaunch;
                    if (launchErrorCount >= 2) {
                        catapultStatus = disabled;
                    }
                }
            }
        }

        //Status engaging dog
        if (catapultStatus == engagingDog){
            if (dogTimer.isExpired()){
                winchBottomMotor.set(motorSpeed);
                winchTopMotor.set(motorSpeed);
                catapultStatus = winching;
            }
        }

        //Status winching
        if (catapultStatus == winching){
            if (isCatapultInPos() && isCatapultLatched()) {
                winchBottomMotor.set(0);
                winchTopMotor.set(0);
                stoppingMotorTimer.setTimer(stoppingMotorTime);
                catapultStatus = stoppingMotors;
            }
        }

        //Status stopping Motors
        if (catapultStatus == stoppingMotors){
            if (stoppingMotorTimer.isExpired()){
                dogPiston.set(false);
                dogTimer.setTimer(dogTime);
                catapultStatus = disengagingDog;
            }
        }

        //Status disengaging Dog
        if (catapultStatus == disengagingDog){
            if (dogTimer.isExpired()){
                dogTimer.disableTimer();
                if (isCatapultInPos() && isCatapultLatched()) {
                    catapultStatus = readyToLaunch;
                }
                else {
                    catapultStatus = errorResetting;
                }
            }
        }

        //Status errorLaunch
        if (catapultStatus == errorLaunch) {
            System.out.println("Error Launch");
            latchPiston.set(true);
            catapultStatus = launching;
        }

        //Status errorResetting
        if (catapultStatus == errorResetting) {
            System.out.println("Error Resetting");
            catapultStatus = disabled;
        }

        //Status Disabled
        if (catapultStatus == disabled) {
            System.out.println("Disabling Launcher");
            winchTopMotor.disable();
            winchBottomMotor.disable();
        }
    }

    public boolean isSafeToRetractIntake(){
        return safeToRetractIntake;
    }

    public void free(){
        winchBottomMotor.free();
        winchTopMotor.free();
        dogPiston.free();
        latchPiston.free();
        latchSensor.free();
        catapultSensor.free();
    }
}
