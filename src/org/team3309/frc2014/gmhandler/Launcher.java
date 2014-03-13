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
    private SolenoidBase pocketPiston;
    private Timer catapultTimer;
    private Timer stoppingMotorTimer;
    private Timer dogTimer;
    private Timer winchTimer;
    private Timer pocketPistonTimer;
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
    private boolean doublePocketPiston;
    private boolean sensorDebug;
    private boolean overrideOperatorPocketPiston;
    private boolean pocketPistonEngaged;
    private boolean autoReset;
    private boolean startup;
    private static final int unknown = 0;
    private static final int readyToLaunch = 1;
    private static final int launching = 2;
    private static final int engagingDog = 3;
    private static final int winching = 4;
    private static final int stoppingMotors = 5;
    private static final int disengagingDog = 6;
    private static final int disengagingPocketPiston = 7;
    private static final int errorResetting = 8;
    private static final int disabled = 9;
    private static final int launchNow = 10;

    public Launcher() {

        double [] launcherWinchMotorBot = ((double[]) ConstantTable.getConstantTable().getValue("Launcher.winchMotorBot"));
        double [] launcherWinchMotorTop = ((double[]) ConstantTable.getConstantTable().getValue("Launcher.winchMotorTop"));
        double [] launcherLatchPiston = ((double[]) ConstantTable.getConstantTable().getValue("Launcher.latchSolenoid"));
        double [] launcherDogPiston = ((double[]) ConstantTable.getConstantTable().getValue("Launcher.dogSolenoid"));
        double [] launcherPocketPiston = ((double[]) ConstantTable.getConstantTable().getValue("Launcher.pocketPiston"));
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
        pocketPistonTimer = new Timer();
        
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
        closeLatch();
        if (launcherDogPiston[2] == 0){
            dogPiston = new Solenoid((int) launcherDogPiston[0], (int) launcherDogPiston[1]);
        }
        else {
            dogPiston = new DoubleSolenoid((int) launcherDogPiston[0], (int) launcherDogPiston[1], (int) launcherDogPiston[2]);
            doubleDogSolenoid = true;
        }
        if (launcherPocketPiston[2] == 0){
            pocketPiston = new Solenoid((int) launcherPocketPiston[0], (int) launcherPocketPiston[1]);
        }
        else {
            pocketPiston = new DoubleSolenoid((int) launcherPocketPiston[0], (int) launcherPocketPiston[1], (int) launcherPocketPiston[2]);
            doublePocketPiston = true;
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
    public void stateMachine(boolean[] launcherParameterArray) {

        boolean launchAndReset = launcherParameterArray[0];
        boolean manualLaunch = launcherParameterArray[1];
        boolean manualReset = launcherParameterArray[2];
        boolean safeToMove = launcherParameterArray[3];
        boolean renableLauncher = launcherParameterArray[4];

        if (sensorDebug){
            System.out.println("Catapult in Position: " + String.valueOf(isCatapultInPos()) + " Latched: " + String.valueOf(isCatapultLatched()));
        }


        //Status unknown
        if (catapultStatus == unknown){

            if (launcherEnabled){
                disengageDog();
                autoReset = false;
                startup = true;

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
        if ((catapultStatus == readyToLaunch || catapultStatus == launchNow) && safeToMove) {
            if (launchAndReset || manualLaunch || catapultStatus == launchNow) {
                if (!pocketPistonEngaged){

                    //Launch
                    catapultStatus = launching;
                    catapultTimer.setTimer(launchTime);
                    safeToRetractIntake = false;
                    openLatch();
                    launchErrorCount = 0;
                    if (launcherDebug){
                        System.out.println("Catapult status: launching");
                    }

                    //Determines if the launcher should use old or new reset value
                    if (catapultStatus != launchNow){
                        autoReset = launchAndReset;
                    }
                }
                else {

                    //Disengaging pocket piston before launch
                    pocketPistonTimer.setTimer(0.2);
                    overrideOperatorPocketPiston = true;
                    autoReset = launchAndReset;
                    catapultStatus = disengagingPocketPiston;
                    if (launcherDebug){
                        System.out.println("Catapult status: disengaging pocket piston");
                    }
                }
            }
            else {

                //Not yet time to launch
                safeToRetractIntake = true;
                overrideOperatorPocketPiston = false;
            }
        }

        //Status launching
        if (catapultStatus == launching) {
            if (catapultTimer.isExpired()) {

                //check for good launch
                if (!isCatapultInPos() && (!isCatapultLatched() || startup)){

                    //Check to see if time to reset
                    if (autoReset || manualReset){
                        catapultTimer.disableTimer();
                        closeLatch();
                        engageDog();
                        dogTimer.setTimer(dogTime);
                        startup = false;
                        autoReset = false;
                        catapultStatus = engagingDog;

                        if (launcherDebug){
                            System.out.println("Catapult status: engaging dog");
                        }
                    }
                }
                else {

                    //Launcher error
                    launchErrorCount++;

                    //Makes sure to print error launch only once and not again on second time through
                    if (launchErrorCount == 0){
                        System.out.println("Catapult status: error launch");
                    }

                    //Disables Launcher if count gets to 2
                    if (launchErrorCount >= 2) {
                        catapultStatus = disabled;
                        System.out.println("Catapult status: disabled");
                    }
                }
            }
        }

        //Status engaging dog
        if (catapultStatus == engagingDog){
            if (dogTimer.isExpired()){
                loweringLauncher();
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
                stoppingLowering();
                stoppingMotorTimer.setTimer(stoppingMotorTime);
                catapultStatus = stoppingMotors;
                if (launcherDebug){
                    System.out.println("Catapult status: stopping motors");
                }
            }
            else if (winchTimer.isExpired()){
                catapultStatus = errorResetting;
                System.out.println("Catapult status: error winching");
            }
        }

        //Status stopping Motors
        if (catapultStatus == stoppingMotors){
            if (stoppingMotorTimer.isExpired()){
                disengageDog();
                dogTimer.setTimer(dogTime);
                catapultStatus = disengagingDog;
                if (launcherDebug){
                    System.out.println("Catapult status: disengaging dog");
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

        //Status disengaging Pocket Piston
        if (catapultStatus == disengagingPocketPiston){
            safeToRetractIntake = false;
            overrideOperatorPocketPiston = true;
            disengagePocketPiston();
            if (pocketPistonTimer.isExpired()){
                pocketPistonTimer.disableTimer();
                catapultStatus = launchNow;
            }
        }

        //Status errorResetting
        if (catapultStatus == errorResetting){

            catapultStatus = disabled;
            if (launcherDebug){
                System.out.println("Catapult status: disabled");
            }
        }

        //Status Disabled
        if (catapultStatus == disabled) {
            winchTopMotor.set(0);
            winchBottomMotor.set(0);
            autoReset = false;
            catapultTimer.disableTimer();
            stoppingMotorTimer.disableTimer();
            dogTimer.disableTimer();
            winchTimer.disableTimer();
            pocketPistonTimer.disableTimer();
            safeToRetractIntake = true;
            launcherEnabled = false;
            startup = false;
            if (renableLauncher){
                launcherEnabled = true;
                catapultStatus = unknown;
            }
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
        pocketPiston.free();
    }
    
    public void openLatch (){
        if (launcherDebug){
            System.out.println("Opening latch");
        }
        if (doubleLatchSolenoid){
            ((DoubleSolenoid) latchPiston).set(DoubleSolenoid.Value.kForward);
        }
        else ((Solenoid) latchPiston).set(true);    
    }
    
    public void closeLatch(){
        if (doubleLatchSolenoid){
            ((DoubleSolenoid) latchPiston).set(DoubleSolenoid.Value.kReverse);
        }
        else ((Solenoid) latchPiston).set(false);    
    }
    
    public void engageDog(){
        if (doubleDogSolenoid){
            ((DoubleSolenoid) dogPiston).set(DoubleSolenoid.Value.kForward);
        }
        else ((Solenoid)dogPiston).set(true);
    }
    
    public void disengageDog(){
        if (doubleDogSolenoid){
            ((DoubleSolenoid) dogPiston).set(DoubleSolenoid.Value.kReverse);
        }
        else ((Solenoid) dogPiston).set(false);
    }

    public void togglePocketPiston(){
        if (!overrideOperatorPocketPiston){
            pocketPistonEngaged = !pocketPistonEngaged;

            if (pocketPistonEngaged){
                engagePocketPiston();
            }
            else {
                disengagePocketPiston();
            }
        }
    }
    
    public void disengagePocketPiston(){
        if (doublePocketPiston){
            ((DoubleSolenoid) pocketPiston).set(DoubleSolenoid.Value.kReverse);
        }
        else ((Solenoid) pocketPiston).set(false);

        if (launcherDebug && pocketPistonEngaged){
            System.out.println("disengaging pocket piston");
        }

        pocketPistonEngaged = false;
    }
    
    public void engagePocketPiston(){
        if (!overrideOperatorPocketPiston){
            if (doublePocketPiston){
                ((DoubleSolenoid) pocketPiston).set(DoubleSolenoid.Value.kForward);
            }
            else ((Solenoid) pocketPiston).set(true);

            if (launcherDebug && !pocketPistonEngaged){
                System.out.println("engaging pocket piston");
            }

            pocketPistonEngaged = true;
        }
    }
    
    public void loweringLauncher(){
        winchBottomMotor.set(motorSpeed);
        winchTopMotor.set(motorSpeed);
    }
    
    public void stoppingLowering(){
        winchBottomMotor.set(0);
        winchTopMotor.set(0);
    }
}
