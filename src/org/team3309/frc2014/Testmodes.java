/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.team3309.frc2014;

import org.team3309.frc2014.Robot.Toggle;
import org.team3309.frc2014.gmhandler.Intake;
import org.team3309.frc2014.gmhandler.Launcher;
import org.team3309.frc2014.subsystems.DriveTrain;
import org.team3309.frc2014.timer.Timer;
import org.team3309.friarlib.XboxController;

/**
 *
 * @author Ben
 */
    public class Testmodes {
        
    private XboxController driveXbox;
    private Timer testDriveTimer;
    private Timer testIntakeTimer;
    private Timer testLauncherTimer;
    private Timer stopTimer;
    private DriveTrain driveTrain;
    private Intake intake;
    private Launcher launcher;
    private Toggle driveToggle;
    private Toggle intakeToggle;
    private Toggle launcherToggle;
    private boolean driveTest;
    private boolean intakeTest;
    private boolean launcherTest;
    private boolean testComplete;
    
    
    
    public Testmodes(DriveTrain driveTrain, Launcher launcher, Intake intake){
        this.driveTrain = driveTrain;
        this.launcher = launcher;
        this.intake = intake; 
    }
        
        private void driveTest (){      
            
            System.out.println("Starting DriveTest");
            System.out.println("Turning wheels forward");
            driveTrain.drive(1.0, 0, 0, false);
            testDriveTimer.setTimer(2.0);
            
            if (testDriveTimer.isExpired()){
                testDriveTimer.disableTimer();
                System.out.println("Stopping wheels");
                driveTrain.drive(0, 0, 0, false);
                testDriveTimer.setTimer(2.0);               
            }
            
            if (testDriveTimer.isExpired()){
                testDriveTimer.disableTimer();
                System.out.println("Turning wheels backward");
                driveTrain.drive(-1.0, 0, 0, false);
                testDriveTimer.setTimer(2.0);
            }
                                    
            if (testDriveTimer.isExpired()){
                testDriveTimer.disableTimer();
                System.out.println("Stopping wheels");
                driveTrain.drive(0, 0, 0, false);                        
               }
            
            testComplete = true;
}
        
       private void intakeTest (){
           
            System.out.println("Starting intake test");
            intake.extendIntake();
            testIntakeTimer.setTimer(1.0);
            
            if (testIntakeTimer.isExpired()){
                testIntakeTimer.disableTimer();
                System.out.println("Retracting Intake piston");
                intake.retractIntake();
                testIntakeTimer.setTimer(1.0);
            }
                                                                   
            if (testIntakeTimer.isExpired()){
                testIntakeTimer.disableTimer();
                System.out.println("Extending intake piston");
                intake.extendIntake();
                testIntakeTimer.setTimer(1.0);
                System.out.println("Testing intake motors");
            }
                            
              
               
            if (testIntakeTimer.isExpired()){
                testIntakeTimer.disableTimer();
                System.out.println("extending intake piston");
                intake.extendIntake();
                testIntakeTimer.setTimer(1.0);
            }

            if (testIntakeTimer.isExpired()){
                testIntakeTimer.disableTimer();
                System.out.println("Retracting intake piston");
                intake.retractIntake();
                testIntakeTimer.setTimer(1.0);
            }            

            if (testIntakeTimer.isExpired()){
                testIntakeTimer.disableTimer();
                System.out.println("Extending intake piston");
                intake.extendIntake();
                testIntakeTimer.setTimer(1.0);
                System.out.println("Testing intake motors");
            }                                                  
                                 

            if (testIntakeTimer.isExpired()){
                testIntakeTimer.disableTimer();
                System.out.println("Pulling balls in");
                intake.pullIn();
                testIntakeTimer.setTimer(1.0);
            }

            if (testIntakeTimer.isExpired()){
                testIntakeTimer.disableTimer();
                System.out.println("Stopping intake motors");
                intake.stopMotors();
                testIntakeTimer.setTimer(1.0);
            }

            if (testIntakeTimer.isExpired()){
                testIntakeTimer.disableTimer();
                System.out.println("Pushing balls out");
                intake.pushOut();
                testIntakeTimer.setTimer(1.0);
            }

            if (testIntakeTimer.isExpired()){
                testIntakeTimer.disableTimer();
                System.out.println("Stopping intake motors");
                intake.stopMotors();
               }            
            
            testComplete = true;
       } 
       
       private void launcherTest (){
           System.out.println("Starting Launcher Test");
           System.out.println("Engaging dog piston");
           launcher.engageDog();           
           testLauncherTimer.setTimer(1.0);
           
           if (testLauncherTimer.isExpired()){
               testLauncherTimer.disableTimer();
               System.out.println("Disengaging dog piston");
               launcher.disengageDog();
               testLauncherTimer.setTimer(1.0);
           }
                                          
           if (testLauncherTimer.isExpired()){
               testLauncherTimer.disableTimer();
               System.out.println("Running Catapult motor");
               launcher.loweringLauncher();
               testLauncherTimer.setTimer(1.0);
           }
                                 
           if (testLauncherTimer.isExpired()){
                testLauncherTimer.disableTimer();
                System.out.println("Stopping Catapult motor");
                launcher.stoppingLowering();
                testLauncherTimer.setTimer(1.0);                                               
            }                
                
           if (testLauncherTimer.isExpired()){
                testLauncherTimer.disableTimer();
                System.out.println("Engaging pocket piston");
                launcher.engagePocketPiston();     
                testLauncherTimer.setTimer(1.0);
            }
                          
           if (testLauncherTimer.isExpired()){
                testLauncherTimer.disableTimer();
                System.out.println("Disengaging pocket piston");
                launcher.disengagePocketPiston();
                testLauncherTimer.setTimer(1.0);
            }    
                                                           
           if (testLauncherTimer.isExpired()){
                testLauncherTimer.disableTimer();
                System.out.println("Opening Latch piston");
                launcher.openLatch();
                testLauncherTimer.setTimer(1.0);
            }
                               
           if (testLauncherTimer.isExpired()){
                testLauncherTimer.disableTimer();
                System.out.println("Closing Latch piston ");
                launcher.closeLatch();    
            }
                                     
           testComplete = true;
       }
       
       private void sensorTest(){
           
       }
       
       private void stopTest (){
           
           System.out.println("Stopping the test");
           System.out.println("Checking if all systems are shut down");
           System.out.println("Back to originial position");
           testDriveTimer.disableTimer();
           testIntakeTimer.disableTimer();
           testLauncherTimer.disableTimer();
           System.out.println("Stopping driveTrain");
           driveTrain.drive(0, 0, 0, false);
           stopTimer.setTimer(1.0);
           
           if (stopTimer.isExpired()){
           stopTimer.disableTimer();
           System.out.println("Extending intake piston");
           intake.extendIntake();
           intake.stopMotors();
           stopTimer.setTimer(1.0);
           }
           
           if (stopTimer.isExpired()){
               stopTimer.disableTimer();
               launcher.closeLatch();
               stopTimer.setTimer(1.0);
           }
                                                           
           if (stopTimer.isExpired()){
               stopTimer.disableTimer();
               launcher.disengagePocketPiston();
               stopTimer.setTimer(1.0);
           }
                     
           if (stopTimer.isExpired()){
               stopTimer.disableTimer();
               launcher.disengageDog();
           }
           
       }
       
       public boolean isTestComplete(){
            return testComplete;      
       }
       
       public void enterTest(){
            boolean driverXButton = driveXbox.getXButton();        
            boolean driverYButton = driveXbox.getYButton();
            boolean driverAButton = driveXbox.getAButton();
            boolean driverBButton = driveXbox.getBButton();
            
            if (driveToggle.toggle(driverXButton)){           
            driveTest = !driveTest;
            
            if (driveTest){
                driveTest();
            }
            
            else {
                stopTest();
            }

            if (isTestComplete()){
                driveTest = false;
            }            
        }
            
          if (intakeToggle.toggle(driverYButton)){
              intakeTest = !intakeTest;
              
          if (intakeTest){
              intakeTest();
          }
          
          if (isTestComplete()){
              intakeTest = false;
          }
          }
          
          if (launcherToggle.toggle(driverAButton)){
              launcherTest = !launcherTest;
              
          if (launcherTest){
              launcherTest();
          }
          
          if (isTestComplete()){
              launcherTest = false;
          }
          
          }
          
            
       }
}
