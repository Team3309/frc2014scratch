/*----------------------------------------------------------------------------*/
/* Copyright (c) FIRST 2008. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.team3309.frc2014;

import edu.wpi.first.wpilibj.Compressor;
import org.team3309.frc2014.gmhandler.Launcher;
import org.team3309.frc2014.subsystems.DriveTrain;
import org.team3309.friarlib.XboxController;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.command.Scheduler;
import org.team3309.frc2014.constantmanager.ConstantTable;
import org.team3309.frc2014.gmhandler.Intake;
import org.team3309.frc2014.gmhandler.TestLauncher;
import sun.launcher.resources.launcher;

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
    private Launcher launcher;
    private boolean robotInitialized;
    private double deadband;

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
            launcher = new Launcher();
            robotInitialized = true;
            deadband = ((Double) ConstantTable.getConstantTable().getValue("DriveController.deadband")).doubleValue();
        }
    }

    public void disabledInit(){
        //System.out.println("DisabledInit() called");
        if (robotInitialized){
            robotInitialized = false;
            driveTrain.free();
            ConstantTable.free();
            intake.free();
            launcher.free();
        }
    }
    
    public void disabledPeriodic(){
        //System.out.println("DisabledPeriodic() called");        
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
        
        driveTrain.setCoastMode(driveXbox.getLeftBumper());
        //driver buttons
        //Code for driving around
        double driverRightX = driveXbox.getRightX();
        double driverLeftX = driveXbox.getLeftX();
        double driverLeftY = driveXbox.getLeftY();
        
        driverRightX = applyDeadband(driverRightX);
        driverLeftX = applyDeadband(driverLeftX);
        driverLeftY = applyDeadband(driverLeftY);
        
        driveTrain.drive(driverLeftY, driverRightX, driverLeftX);

        //operator buttons
        
        boolean OperatorRightBumper = operatorXbox.getRightBumper();
        launcher.launch(OperatorRightBumper);
                
        boolean OperatorXButton = operatorXbox.getXButton();
        boolean OperatorLeftBumper = operatorXbox.getLeftBumper();
        
        
        
    }
    
    /**
     * This function is called periodically during test mode
     */
    public void testPeriodic() {
        robotEnable();
        // LiveWindow.run();
        
    }
    private double applyDeadband(double joystickValue){
        if (joystickValue >= -deadband && joystickValue <= deadband){
            joystickValue = 0;
        }
        else if (joystickValue > deadband){
            joystickValue = (joystickValue - deadband) / (1 - deadband);
        }
        else {
            joystickValue = (joystickValue + deadband) / (1 - deadband);
        }
        return joystickValue;
    }
}
