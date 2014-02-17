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
      private boolean catapultPos;
      private boolean latched;
      private Timer catapultTimer;
      private Timer stopMotorTimer;
      private int launchErrorCount;
      private int winchErrorCount;
      
      
      public void Launcher(){
          launcherWinchMotorBot = ((double[]) ConstantTable.getConstantTable().getValue("Launcher.winchMotorBot"));
          launcherWinchMotorTop = ((double[]) ConstantTable.getConstantTable().getValue("Launcher.winchMotorTop"));
          launcherLatchPiston = ((double[]) ConstantTable.getConstantTable().getValue("Launcher.big.solenoid"));
          launcherDogPiston = ((double[]) ConstantTable.getConstantTable().getValue("Launcher.small.solenoid"));
          motorSpeed = ((Double) ConstantTable.getConstantTable().getValue("Launcher.motorSpeed")).doubleValue();
          
          bottomMotor = new Victor ((int) launcherWinchMotorBot[0], (int) launcherWinchMotorBot[1]);
          topMotor = new Victor ((int) launcherWinchMotorTop[0], (int) launcherWinchMotorTop[1]);
          latchPiston = new Solenoid((int) launcherLatchPiston[0], (int) launcherLatchPiston [1]);
          dogPiston = new Solenoid((int) launcherDogPiston[0], (int) launcherDogPiston [1]);
          
          catapultStatus = readyToLaunch;
      }
      
      public void launch(boolean buttonPressed){
          
          //Status ready to launch
          if (catapultStatus == readyToLaunch){
              if (buttonPressed){
                  catapultStatus = launching;
                  catapultTimer.setTimer(1.5);
                  latchPiston.set(true);
                  launchErrorCount = 0;
              }
          }

          //Status launching
          if (catapultStatus == launching){
                  if (catapultTimer.isExpired()){
                      if ((catapultPos == true && latched == false) ||
                         (catapultPos == false && latched == true)){
                           catapultStatus = errorLaunch;
                           launchErrorCount ++;
                           if (launchErrorCount > 2){
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
          if (catapultStatus == resettingWinch){
              if (catapultTimer.isExpired() == false){
                  if (bottomMotor.get() == motorSpeed || topMotor.get() == motorSpeed){
                      if (catapultPos && latched){
                          bottomMotor.set(0);
                          topMotor.set(0);
                          stopMotorTimer.setTimer(.2);
                          if (stopMotorTimer.isExpired()){
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
                  winchErrorCount ++;
                  if (winchErrorCount > 2){
                      catapultStatus = manualOverride;
                  }
              }
          }
          
          //Status errorLaunch
          if (catapultStatus == errorLaunch){
              System.out.println("Error Launch");
              catapultStatus = disabled;
          }
          
          //Status errorResetting
          if (catapultStatus == errorResetting){
              System.out.println("Error Resetting");
              catapultStatus = disabled;
          }
          
          //Disabled      
          if (catapultStatus == disabled){
              topMotor.disable();
              bottomMotor.disable();
          }
          
        }
}

      

      
      
