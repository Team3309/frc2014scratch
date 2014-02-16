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
      private static Solenoid bigPiston;
      private static Solenoid smallPiston;
      private double[] launcherTopMotor;
      private double[] launcherBottomMotor;
      private double[] launcherBigPiston;
      private double[] launcherSmallPiston;
      private static double motorSpeed;
      private int CatapultStatus;
      
      
      public void Launcher(){
          launcherTopMotor = ((double[]) ConstantTable.getConstantTable().getValue("Launcher.top.motor"));
          launcherBottomMotor = ((double[]) ConstantTable.getConstantTable().getValue("Launcher.bottom.motor"));
          launcherBigPiston = ((double[]) ConstantTable.getConstantTable().getValue("Launcher.big.solenoid"));
          launcherSmallPiston = ((double[]) ConstantTable.getConstantTable().getValue("Launcher.small.solenoid"));
          motorSpeed = ((Double) ConstantTable.getConstantTable().getValue("Launcher.motorSpeed")).doubleValue();
          
          topMotor = new Victor ((int) launcherTopMotor[0], (int) launcherTopMotor[1]);
          bottomMotor = new Victor ((int) launcherBottomMotor[0], (int) launcherBottomMotor[1]);
          bigPiston = new Solenoid((int) launcherBigPiston[0], (int) launcherBigPiston [1]);
          smallPiston = new Solenoid((int) launcherSmallPiston[0], (int) launcherSmallPiston [1]);
      }
      
      private static void charging(boolean down){
          if (down == true){
              topMotor.set(motorSpeed);
              bottomMotor.set(motorSpeed);
          }
          else{
              topMotor.set(0);
              bottomMotor.set(0);
          }          
      }
      
      private static void shoot(boolean start){
          if (start == false){
              bigPiston.set(false);
          }
          else{
              bigPiston.set(true);
          }
      }
      
}

      
      
