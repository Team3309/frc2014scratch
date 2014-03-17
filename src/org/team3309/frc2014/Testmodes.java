/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.team3309.frc2014;

import org.team3309.frc2014.gmhandler.Intake;
import org.team3309.frc2014.gmhandler.Launcher;
import org.team3309.frc2014.subsystems.DriveTrain;
import org.team3309.frc2014.timer.Timer;

/**
 *
 * @author Ben
 */
    public class Testmodes {
        
    private Timer testDriveTimer;
    private Timer testIntakeTimer;
    private Timer testLauncherTimer;
    private DriveTrain driveTrain;
    private Intake intake;
    private Launcher launcher;
    
    public Testmodes(DriveTrain driveTrain, Launcher launcher, Intake intake){
        this.driveTrain = driveTrain;
        this.launcher = launcher;
        this.intake = intake; 
    }
        
        public void driveTest (){        
    
            driveTrain.drive(1.0, 0, 0, false);
            testDriveTimer.setTimer(2.0);
            
            if (testDriveTimer.isExpired()){
                driveTrain.drive(0, 0, 0, false);
                testDriveTimer.disableTimer();
            }
            
            driveTrain.drive(-1.0, 0, 0, false);
            testDriveTimer.setTimer(2.0);
            
            if (testDriveTimer.isExpired()){
                driveTrain.drive(0, 0, 0, false);
                testDriveTimer.disableTimer();          
               }
}
        
       public void IntakeTest (){
           
           if (intake.isExtended()){
                intake.retractIntake();
                intake.extendIntake();
            } 
           
            else{
            intake.extendIntake();
            }
            
            testIntakeTimer.setTimer(2.0);
            
            if (testIntakeTimer.isExpired()){
                intake.pullIn();
                testIntakeTimer.disableTimer();
                testIntakeTimer.setTimer(2.0);
            }
                
            if (testIntakeTimer.isExpired()){
               intake.stopMotors();
               testIntakeTimer.disableTimer();
               testIntakeTimer.setTimer(2.0);
            }
                   
            if (testIntakeTimer.isExpired()){
                intake.pushOut();
                testIntakeTimer.disableTimer();
                testIntakeTimer.setTimer(2.0);
            }
                   
                       
            if (testIntakeTimer.isExpired()){
                testIntakeTimer.disableTimer();
                intake.stopMotors();
            }
                 
       } 
       
       public void LauncherTest (){
           
            launcher.loweringLauncher();
            testLauncherTimer.setTimer(1.0);
            
            if (testLauncherTimer.isExpired()){
                launcher.stoppingLowering();
                testLauncherTimer.disableTimer();
                testLauncherTimer.setTimer(1.0);
                
            }
            
            if (testLauncherTimer.isExpired()){
                launcher.engagePocketPiston();
                testLauncherTimer.disableTimer();
                testLauncherTimer.setTimer(1.0);
                launcher.disengagePocketPiston();
                testLauncherTimer.disableTimer();
                testLauncherTimer.setTimer(1.0);

            }

            if (testLauncherTimer.isExpired()){
                launcher.openLatch();
                testLauncherTimer.disableTimer();
                testLauncherTimer.setTimer(1.0);

            }

            if (testLauncherTimer.isExpired()){
                launcher.closeLatch();
                testLauncherTimer.disableTimer();
                testLauncherTimer.setTimer(1.0);

            }

            if (testLauncherTimer.isExpired()){
                launcher.engageDog();
                testLauncherTimer.disableTimer();
                testLauncherTimer.setTimer(1.0);

            }

            if (testLauncherTimer.isExpired()){
                launcher.disengageDog();
                testLauncherTimer.disableTimer();
            }
                                 
       }
}
