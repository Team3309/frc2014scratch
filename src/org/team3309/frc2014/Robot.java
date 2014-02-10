/*----------------------------------------------------------------------------*/
/* Copyright (c) FIRST 2008. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.team3309.frc2014;

import edu.wpi.first.wpilibj.Compressor;
import org.team3309.frc2014.subsystems.DriveTrain;
import org.team3309.friarlib.XboxController;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import org.team3309.frc2014.commands.CommandBase;
import org.team3309.frc2014.gmhandler.Pickup;
import org.team3309.frc2014.gmhandler.Launcher;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Robot extends IterativeRobot {

    private XboxController driveXbox;
    private XboxController operatorXbox = new XboxController(2);
    private Compressor compressor = null;
    private Pickup pickup;
    private DriveTrain driveTrain;
    /**
     * This function is run when the robot is first started up and should be
     * used for any initialization code.
     */
    public void robotInit() {
        driveTrain = new DriveTrain();
        driveXbox = new XboxController(1);
        // Initialize all subsystems
        //CommandBase.init();
    }

    public void autonomousInit() {
        // schedule the autonomous command (example)
       
    }

    /**
     * This function is called periodically during autonomous
     */
    public void autonomousPeriodic() {
        Scheduler.getInstance().run();
        
    }

    public void teleopInit() {
        
    }

    public void disabledTeleop(){
        
    }
    /**
     * This function is called periodically during operator control
     */
    public void teleopPeriodic() {
        Scheduler.getInstance().run();
        //Changes drives, only when held
        //cause Michael wants it that way
        if (Math.abs(driveXbox.getRightTrigger()) >= .5){
            driveTrain.enableTank();
        }
        else {
            driveTrain.enableMecanum();
        }
         
        //Code for driving around
        double rightX = driveXbox.getRightX();
        double leftX = driveXbox.getLeftX();
        double leftY = driveXbox.getLeftY();
        driveTrain.drive(leftY, rightX, leftX);
        boolean rightBumper = operatorXbox.getRightBumper();
        //pickup.pickup(rightBumper);
        
        boolean Xbutton = operatorXbox.getXButton();       
        boolean leftBumper = operatorXbox.getLeftBumper();
        Launcher.pullback(Xbutton);
        Launcher.release(leftBumper);

    }
    
    /**
     * This function is called periodically during test mode
     */
    public void testPeriodic() {
        LiveWindow.run();
    }
}
