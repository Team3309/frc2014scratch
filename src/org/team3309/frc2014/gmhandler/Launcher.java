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
    private static final int resettingWinch = 3;
    private static final int errorLaunch = 4;
    private static final int errorResetting = 5;
    private static final int disabled = 6;
    private Timer catapultTimer;
    private Timer winchTimer;
    private Timer dogTimer;
    private int launchErrorCount;
    private int winchErrorCount;
    private double launchTime;
    private double winchTime;
    private double dogTime;
    private DigitalInput catapultSensor;
    private DigitalInput latchSensor;

    public Launcher() {
        double [] launcherWinchMotorBot = ((double[]) ConstantTable.getConstantTable().getValue("Launcher.winchMotorBot"));
        double [] launcherWinchMotorTop = ((double[]) ConstantTable.getConstantTable().getValue("Launcher.winchMotorTop"));
        double [] launcherLatchPiston = ((double[]) ConstantTable.getConstantTable().getValue("Launcher.latchSolenoid"));
        double [] launcherDogPiston = ((double[]) ConstantTable.getConstantTable().getValue("Launcher.dogSolenoid"));
        double [] launcherCatapultSensor = ((double[]) ConstantTable.getConstantTable().getValue("Launcher.catapultSensor"));
        double [] launcherLatchSensor = ((double[]) ConstantTable.getConstantTable().getValue("Launcher.latchSensor"));
        motorSpeed = ((Double) ConstantTable.getConstantTable().getValue("Launcher.motorSpeed")).doubleValue();

        //Times are in seconds
        launchTime = ((Double) ConstantTable.getConstantTable().getValue("Launcher.launchTime")).doubleValue();
        winchTime = ((Double) ConstantTable.getConstantTable().getValue("Launcher.winchTime")).doubleValue();
        dogTime = ((Double) ConstantTable.getConstantTable().getValue("Launcher.dogTime")).doubleValue();

        winchBottomMotor = new Victor((int) launcherWinchMotorBot[0], (int) launcherWinchMotorBot[1]);
        winchTopMotor = new Victor((int) launcherWinchMotorTop[0], (int) launcherWinchMotorTop[1]);
        latchPiston = new Solenoid((int) launcherLatchPiston[0], (int) launcherLatchPiston[1]);
        dogPiston = new Solenoid((int) launcherDogPiston[0], (int) launcherDogPiston[1]);
        catapultSensor = new DigitalInput((int) launcherCatapultSensor[0], (int) launcherCatapultSensor[1]);
        latchSensor = new DigitalInput((int) launcherLatchSensor[0], (int) launcherLatchSensor[1]);

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
            if (buttonPressed) {
                catapultStatus = launching;
                catapultTimer.setTimer(launchTime);
                latchPiston.set(true);
                launchErrorCount = 0;
            }
        }

        //Status launching
        if (catapultStatus == launching) {
            if (catapultTimer.isExpired()) {
                //check for good launch
                if (!isCatapultLatched() && !isCatapultInPos()) {
                    catapultStatus = resettingWinch;
                    latchPiston.set(false);
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

        //Status resetting Winch
        if (catapultStatus == resettingWinch) {
            if (!winchTimer.isExpired()) {
                dogPiston.set(true);
                dogTimer.setTimer(dogTime);
                if (dogTimer.isExpired()){
                    winchBottomMotor.set(motorSpeed);
                    winchTopMotor.set(motorSpeed);
                }
                    if (isCatapultInPos() && isCatapultLatched()) {
                        winchBottomMotor.set(0);
                        winchTopMotor.set(0);
                        winchTimer.setTimer(winchTime);
                        if (winchTimer.isExpired()) {
                            dogPiston.set(false);
                            catapultStatus = readyToLaunch;
                        }
                    }
                }
            }
            else {
                winchErrorCount++;
                if (winchErrorCount > 2) {
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
            winchTopMotor.disable();
            winchBottomMotor.disable();
        }

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
