/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.team3309.frc2014.gmhandler;

import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Victor;
import org.team3309.frc2014.constantmanager.ConstantTable;



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
      private double startingTime;
      private boolean catapultPos;
      private boolean latched;
      private int launchErrorCount;
      
      
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
                  launcherTimerStart();
                  latchPiston.set(false);
                  launchErrorCount = 0;
              }
          }

          //Status launching
          if (catapultStatus == launching){
                  if (checkIfLauncherTimerDone()){
                      if (catapultPos || latched){
                            
                      }
                  }
                  else {
                      catapultStatus = resettingWinch;
                      latchPiston.set(true);

                  }
          }          
          
          
        }
    
        public void launcherTimerStart(){
            startingTime = System.currentTimeMillis();
        }
    
        public boolean checkIfLauncherTimerDone(){
            if (System.currentTimeMillis() - startingTime >= 3000){
                return true;
            }
            else{
                return false;
            }
        }
}

      

      
      
