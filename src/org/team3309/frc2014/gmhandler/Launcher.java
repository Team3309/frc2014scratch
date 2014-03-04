/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.team3309.frc2014.gmhandler;

import edu.wpi.first.wpilibj.*;
import org.team3309.frc2014.constantmanager.ConstantTable;
import org.team3309.frc2014.timer.Timer;

/**
 *
 * @author JKav
 */
public class Launcher {

    private Victor winchTopMotor;
    private Victor winchBottomMotor;
    private SolenoidBase latchPiston;
    private SolenoidBase dogPiston;
    private Timer catapultTimer;
    private Timer stoppingMotorTimer;
    private Timer dogTimer;
    private Timer winchTimer;
    private Timer launchErrorTimer;
    private DigitalInput catapultSensor;
    private DigitalInput latchSensor;
    private int catapultStatus;
    private int launchErrorCount;
    private double motorSpeed;
    private double launchTime;
    private double stoppingMotorTime;
    private double dogTime;
    private double winchTime;
    private boolean safeToRetractIntake;
    private boolean launcherEnabled;
    private boolean launcherDebug;
    private boolean doubleLatchSolenoid;
    private boolean doubleDogSolenoid;
    private boolean sensorDebug;
    private static final int unknown = 0;
    private static final int readyToLaunch = 1;
    private static final int launching = 2;
    private static final int engagingDog = 3;
    private static final int winching = 4;
    private static final int stoppingMotors = 5;
    private static final int disengagingDog = 6;
    private static final int errorLaunch = 7;
    private static final int errorResetting = 8;
    private static final int disabled = 9;

    public Launcher() {

        double [] launcherWinchMotorBot = ((double[]) ConstantTable.getConstantTable().getValue("Launcher.winchMotorBot"));
        double [] launcherWinchMotorTop = ((double[]) ConstantTable.getConstantTable().getValue("Launcher.winchMotorTop"));
        double [] launcherLatchPiston = ((double[]) ConstantTable.getConstantTable().getValue("Launcher.latchSolenoid"));
        double [] launcherDogPiston = ((double[]) ConstantTable.getConstantTable().getValue("Launcher.dogSolenoid"));
        double [] launcherCatapultSensor = ((double[]) ConstantTable.getConstantTable().getValue("Launcher.catapultSensor"));
        double [] launcherLatchSensor = ((double[]) ConstantTable.getConstantTable().getValue("Launcher.latchSensor"));
        motorSpeed = ((Double) ConstantTable.getConstantTable().getValue("Launcher.motorSpeed")).doubleValue();
        launcherEnabled = ((Boolean) ConstantTable.getConstantTable().getValue("Launcher.enabled")).booleanValue();
        launcherDebug = ((Boolean) ConstantTable.getConstantTable().getValue("Launcher.debug")).booleanValue();
        sensorDebug = ((Boolean) ConstantTable.getConstantTable().getValue("Launcher.sensorDebug")).booleanValue();

        //Times are in seconds
        launchTime = ((Double) ConstantTable.getConstantTable().getValue("Launcher.launchTime")).doubleValue();
        stoppingMotorTime = ((Double) ConstantTable.getConstantTable().getValue("Launcher.stoppingMotorTime")).doubleValue();
        dogTime = ((Double) ConstantTable.getConstantTable().getValue("Launcher.dogTime")).doubleValue();
        winchTime = ((Double) ConstantTable.getConstantTable().getValue("Launcher.winchTime")).doubleValue();

        catapultTimer = new Timer();
        stoppingMotorTimer = new Timer();
        dogTimer = new Timer();
        winchTimer = new Timer();
        launchErrorTimer = new Timer();
        winchBottomMotor = new Victor((int) launcherWinchMotorBot[0], (int) launcherWinchMotorBot[1]);
        winchTopMotor = new Victor((int) launcherWinchMotorTop[0], (int) launcherWinchMotorTop[1]);
        catapultSensor = new DigitalInput((int) launcherCatapultSensor[0], (int) launcherCatapultSensor[1]);
        latchSensor = new DigitalInput((int) launcherLatchSensor[0], (int) launcherLatchSensor[1]);
        if (launcherLatchPiston[2] == 0){
            latchPiston = new Solenoid((int) launcherLatchPiston[0], (int) launcherLatchPiston[1]);
        }
        else {
            latchPiston = new DoubleSolenoid((int) launcherLatchPiston[0], (int) launcherLatchPiston[1], (int) launcherLatchPiston[2]);
            doubleLatchSolenoid = true;
        }
        if (launcherDogPiston[2] == 0){
            dogPiston = new Solenoid((int) launcherDogPiston[0], (int) launcherDogPiston[1]);
        }
        else {
            dogPiston = new DoubleSolenoid((int) launcherDogPiston[0], (int) launcherDogPiston[1], (int) launcherDogPiston[2]);
            doubleDogSolenoid = true;
        }

        catapultStatus = unknown;
        if (launcherDebug){
            System.out.println("Catapult status: unknown");
        }
    }

    public boolean isCatapultInPos() {
        return !catapultSensor.get();
    }

    public boolean isCatapultLatched() {
        return !latchSensor.get();
    }
    public void stateMachine(boolean buttonPressed, boolean safeToMove) {

        if (sensorDebug){
            System.out.println("Catapult in Position: " + String.valueOf(isCatapultInPos()) + " Latched: " + String.valueOf(isCatapultLatched()));
        }

        //Status unknown
        if (catapultStatus == unknown){

            if (launcherEnabled){
                if (doubleDogSolenoid){
                    ((DoubleSolenoid) dogPiston).set(DoubleSolenoid.Value.kReverse);
                }
                else ((Solenoid) dogPiston).set(false);

                if (isCatapultInPos() && isCatapultLatched()){
                    catapultStatus = readyToLaunch;
                    if (launcherDebug){
                        System.out.println("Catapult status: Ready to Launch");
                    }
                }
                else if (safeToMove) {
                    safeToRetractIntake = false;
                    catapultTimer.setTimer((launchTime));
                    catapultStatus = launching;
                    if (launcherDebug){
                        System.out.println("Catapult status: launching");
                    }
                }
            }
            else {
                safeToRetractIntake = true;
            }

        }

        //Status ready to launch
        if (catapultStatus == readyToLaunch) {
            if (launcherEnabled && buttonPressed && safeToMove) {
                catapultStatus = launching;
                catapultTimer.setTimer(launchTime);
                safeToRetractIntake = false;
                if (doubleLatchSolenoid){
                    ((DoubleSolenoid) latchPiston).set(DoubleSolenoid.Value.kForward);
                }
                else ((Solenoid) latchPiston).set(true);
                launchErrorCount = 0;
                if (launcherDebug){
                    System.out.println("Catapult status: launching");
                }
            }
            else safeToRetractIntake = true;
        }

        //Status launching
        if (catapultStatus == launching) {
            if (catapultTimer.isExpired()) {
                //check for good launch
                if (!isCatapultLatched() && !isCatapultInPos()) {
                    if (doubleLatchSolenoid){
                        ((DoubleSolenoid) latchPiston).set(DoubleSolenoid.Value.kReverse);
                    }
                    else ((Solenoid) latchPiston).set(false);
                    catapultTimer.disableTimer();
                    if (doubleDogSolenoid){
                        ((DoubleSolenoid) dogPiston).set(DoubleSolenoid.Value.kForward);
                    }
                    else ((Solenoid)dogPiston).set(true);
                    dogTimer.setTimer(dogTime);
                    catapultStatus = engagingDog;
                    if (launcherDebug){
                        System.out.println("Catapult status: engagingDog");
                    }
                }
                else {
                    launchErrorCount++;
                    launchErrorTimer.setTimer(0.5);
                    catapultStatus = errorLaunch;
                    if (launcherDebug){
                        System.out.println("Catapult status: error launch");
                    }
                    if (launchErrorCount >= 2) {
                        catapultStatus = disabled;
                        if (launcherDebug){
                            System.out.println("Catapult status: disabled");
                        }
                    }
                }
            }
        }

        //Status engaging dog
        if (catapultStatus == engagingDog){
            if (dogTimer.isExpired()){
                winchBottomMotor.set(motorSpeed);
                winchTopMotor.set(motorSpeed);
                winchTimer.setTimer(winchTime);
                catapultStatus = winching;
                if (launcherDebug){
                    System.out.println("Catapult status: winching");
                }
            }
        }

        //Status winching
        if (catapultStatus == winching){
            if (isCatapultInPos() && isCatapultLatched()) {
                winchBottomMotor.set(0);
                winchTopMotor.set(0);
                stoppingMotorTimer.setTimer(stoppingMotorTime);
                catapultStatus = stoppingMotors;
                if (launcherDebug){
                    System.out.println("Catapult status: stoppingMotors");
                }
            }
            else if (winchTimer.isExpired()){
                catapultStatus = errorResetting;
            }
        }

        //Status stopping Motors
        if (catapultStatus == stoppingMotors){
            if (stoppingMotorTimer.isExpired()){
                if (doubleDogSolenoid){
                    ((DoubleSolenoid) dogPiston).set(DoubleSolenoid.Value.kReverse);
                }
                else ((Solenoid) dogPiston).set(false);
                dogTimer.setTimer(dogTime);
                catapultStatus = disengagingDog;
                if (launcherDebug){
                    System.out.println("Catapult status: disengaging Dog");
                }
            }
        }

        //Status disengaging Dog
        if (catapultStatus == disengagingDog){
            if (dogTimer.isExpired()){
                dogTimer.disableTimer();
                if (isCatapultInPos() && isCatapultLatched()) {
                    catapultStatus = readyToLaunch;
                    if (launcherDebug){
                        System.out.println("Catapult status: Ready to launch");
                    }
                }
                else {
                    catapultStatus = errorResetting;
                }
            }
        }

        //Status errorLaunch
        if (catapultStatus == errorLaunch){
            System.out.println("Error Launch");
            if (doubleLatchSolenoid){
                ((DoubleSolenoid) latchPiston).set(DoubleSolenoid.Value.kForward);
            }
            else ((Solenoid) latchPiston).set(true);
            if (launchErrorTimer.isExpired()){
                catapultStatus = launching;
            }
        }

        //Status errorResetting
        if (catapultStatus == errorResetting){
            System.out.println("Error Resetting");
            catapultStatus = disabled;
            if (launcherDebug){
                System.out.println("Catapult status: disabled");
            }
        }

        //Status Disabled
        if (catapultStatus == disabled) {
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
