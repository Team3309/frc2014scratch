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
import org.team3309.frc2014.gmhandler.Intake;
import org.team3309.frc2014.gmhandler.Launcher;
import org.team3309.frc2014.constantmanager.ConstantTable;
import org.team3309.frc2014.gmhandler.Intake;

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
    private Compressor compressor;
    private Intake intake;
    private DriveTrain driveTrain;
    private boolean robotInitialized;

    /**
     * This function is run when the robot is first started up and should be
     * used for any initialization code.
     */
    public void robotInit() {
        driveXbox = new XboxController(1);
        // Initialize all subsystems
        //CommandBase.init();
        double[] pressureSwitch = ((double[]) ConstantTable.getConstantTable().getValue("Compressor.pressureswitch"));
        double[] compressorRelay = ((double[]) ConstantTable.getConstantTable().getValue("Compressor.relay"));
        
        compressor = new Compressor((int) pressureSwitch[0], (int) pressureSwitch[1], (int) compressorRelay[0], (int) compressorRelay[1]);
        compressor.start();
    }
    
    public void robotEnable(){
        if (!robotInitialized){
            driveTrain = new DriveTrain();
            robotInitialized = true;
        }
    }

    public void disableInit(){
        if (robotInitialized){
            robotInitialized = false;
            driveTrain.free();
        }
        
    }
    
    public void autonomousInit() {
        // schedule the autonomous command (example)
       robotEnable();
    }

    /**
     * This function is called periodically during autonomous
     */
    public void autonomousPeriodic() {
        Scheduler.getInstance().run();
    
        
    }

    public void teleopInit() {
        robotEnable();
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
        if (driveXbox.getRightBumper()){
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
        //intake.lowerIntake(rightBumper);
        boolean getAButton = operatorXbox.getAButton();
        //intake.pullIn(AButton);
        boolean Xbutton = operatorXbox.getXButton();       
        boolean leftBumper = operatorXbox.getLeftBumper();
        //Launcher.pullback(Xbutton);
        //Launcher.release(leftBumper);
        
        
    }
    
    /**
     * This function is called periodically during test mode
     */
    public void testPeriodic() {
        robotEnable();
        LiveWindow.run();
        
    }
}
