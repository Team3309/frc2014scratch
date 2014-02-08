/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.team3309.frc2014.gmhandler;

import edu.wpi.first.wpilibj.SpeedController;

/**
 *
 * @author Jon
 */
public class Launcher {
    
      private static SpeedController launcherMotor;
      private static boolean enable;
      
      public static void pullback(boolean pull){
          if (pull == true){
              launcherMotor.set(1);
          }
          else {
              launcherMotor.set(0);
          }
      }
      
      public static void release(boolean release){
          
      }
}

      
      
