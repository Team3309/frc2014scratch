/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.team3309.frc2014.gmhandler;

import org.team3309.frc2014.*;

import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Victor;
import org.team3309.frc2014.constantmanager.ConstantTable;
import org.team3309.frc2014.timer.Timer;

/**
 *
 * @author Jon
 */
public class Launcher {

    private static Victor topMotor;
    private static Victor bottomMotor;
    private static Solenoid latchPiston;
    private static Solenoid dogPiston;
    private double[] launcherWinchMotorBot;
    private double[] launcherWinchMotorTop;
    private double[] launcherLatchPiston;
    private double[] launcherDogPiston;
    private static double motorSpeed;
    private static int catapultStatus;
    private static final int readyToLaunch = 1;
    private static final int launching = 2;
    private static final int resettingWinch = 3;
    private static final int errorLaunch = 4;
    private static final int errorResetting = 5;
    private static final int disabled = 6;
    private static final int manualOverride = 7;
    private Timer catapultTimer;
    private Timer stopMotorTimer;
    private int launchErrorCount;
    private int winchErrorCount;
    private double launchTime;
    private double winchTime;
    private _____ catapultSensor;
    private _____ latchSensor;

    public void Launcher() {
        launcherWinchMotorBot = ((double[]) ConstantTable.getConstantTable().getValue("Launcher.winchMotorBot"));
        launcherWinchMotorTop = ((double[]) ConstantTable.getConstantTable().getValue("Launcher.winchMotorTop"));
        launcherLatchPiston = ((double[]) ConstantTable.getConstantTable().getValue("Launcher.big.solenoid"));
        launcherDogPiston = ((double[]) ConstantTable.getConstantTable().getValue("Launcher.small.solenoid"));
        motorSpeed = ((Double) ConstantTable.getConstantTable().getValue("Launcher.motorSpeed")).doubleValue();

        //Times are in seconds
        launchTime = ((Double) ConstantTable.getConstantTable().getValue("Launcher.launchTime")).doubleValue();
        winchTime = ((Double) ConstantTable.getConstantTable().getValue("Launcher.winchTime")).doubleValue();

        bottomMotor = new Victor((int) launcherWinchMotorBot[0], (int) launcherWinchMotorBot[1]);
        topMotor = new Victor((int) launcherWinchMotorTop[0], (int) launcherWinchMotorTop[1]);
        latchPiston = new Solenoid((int) launcherLatchPiston[0], (int) launcherLatchPiston[1]);
        dogPiston = new Solenoid((int) launcherDogPiston[0], (int) launcherDogPiston[1]);

        catapultStatus = readyToLaunch;
    }

    public boolean isCatapultInPos() {
        if (catapultSensor = true) {
            return true;
    }
        else {
            return false;
        }
    }
    
    public boolean isCatapultLatched() {
        if (latchSensor = true) {
            return true;
        }
        else {
            return false;
        }
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
                if ((isCatapultInPos() == true && this.isCatapultLatched() == false)
                        || (this.isCatapultInPos() == false && this.isCatapultLatched() == true)) {
                    catapultStatus = errorLaunch;
                    launchErrorCount++;
                    if (launchErrorCount > 2) {
                        catapultStatus = manualOverride;
                    }
                }
            }
            else {
                catapultStatus = resettingWinch;
                latchPiston.set(false);
            }
        }

        //Status resetting Winch
        if (catapultStatus == resettingWinch) {
            if (catapultTimer.isExpired() == false) {
                if (bottomMotor.get() == motorSpeed || topMotor.get() == motorSpeed) {
                    if (this.isCatapultInPos() && this.isCatapultLatched()) {
                        bottomMotor.set(0);
                        topMotor.set(0);
                        stopMotorTimer.setTimer(winchTime);
                        if (stopMotorTimer.isExpired()) {
                            dogPiston.set(false);
                            catapultStatus = readyToLaunch;
                        }
                    }
                }
                else {
                    dogPiston.set(true);
                    bottomMotor.set(motorSpeed);
                    topMotor.set(motorSpeed);
                }
            }
            else {
                winchErrorCount++;
                if (winchErrorCount > 2) {
                    catapultStatus = manualOverride;
                }
            }
        }

        //Status errorLaunch
        if (catapultStatus == errorLaunch) {
            System.out.println("Error Launch");
            catapultStatus = disabled;
        }

        //Status errorResetting
        if (catapultStatus == errorResetting) {
            System.out.println("Error Resetting");
            catapultStatus = disabled;
        }

        //Status Disabled
        if (catapultStatus == disabled) {
            topMotor.disable();
            bottomMotor.disable();
        }

    }

    public void free(){
        bottomMotor.free();
        topMotor.free();
        dogPiston.free();
        latchPiston.free();
    }
}
