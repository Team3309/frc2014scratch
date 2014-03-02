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
import org.team3309.frc2014.timer.Timer;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Robot extends IterativeRobot {

    private XboxController driveXbox;
    private XboxController operatorXbox;
    private Intake intake;
    private DriveTrain driveTrain;
    private Launcher launcher;
    private double deadband;
    private boolean robotInitialized;
    private boolean constantIntakeSpeed;
    private boolean breaking = false;
    private boolean pastYButton;
    private boolean autonomousOneBallLeft;
    private boolean autonomousOneBallMiddle;
    private boolean autonomousOneBallRight;
    private boolean autonomousTwoBallLeft;
    private boolean autonomousTwoBallMiddle;
    private boolean autonomousTwoBallRight;
    private boolean autonomousThreeBallLeft;
    private boolean autonomousThreeBallMiddle;
    private boolean autonomousThreeBallRight;
    private Timer launchTimer;
    private Timer driveTimer;
    private Timer intakeTimer;
    private int autoStatus;
    private static final int startingAutonomous = 1;
    private static final int launching = 2;
    private static final int turning = 3;
    
    

    /**
     * This function is run when the robot is first started up and should be
     * used for any initialization code.
     */
    public void robotInit() {
        driveXbox = new XboxController(1);
        operatorXbox = new XboxController(2);
        // Initialize all subsystems
        //CommandBase.init();
        double[] pressureSwitch = ((double[]) ConstantTable.getConstantTable().getValue("Compressor.pressureswitch"));
        double[] compressorRelay = ((double[]) ConstantTable.getConstantTable().getValue("Compressor.relay"));
        
        Compressor compressor = new Compressor((int) pressureSwitch[0], (int) pressureSwitch[1], (int) compressorRelay[0], (int) compressorRelay[1]);
        compressor.start();
        System.out.println("Robot ready");
    }
    
    public void robotEnable(){
        if (!robotInitialized){
            driveTrain = new DriveTrain();
            launcher = new Launcher();
            intake = new Intake();
            robotInitialized = true;
            deadband = ((Double) ConstantTable.getConstantTable().getValue("Controller.deadband")).doubleValue();
            constantIntakeSpeed = ((Boolean) ConstantTable.getConstantTable().getValue("Controller.constantIntakeSpeed")).booleanValue();
            autonomousOneBallLeft= ((Boolean) ConstantTable.getConstantTable().getValue("Autonomous.oneBallLeft")).booleanValue();
            autonomousOneBallMiddle= ((Boolean) ConstantTable.getConstantTable().getValue("Autonomous.oneBallMiddle")).booleanValue();
            autonomousOneBallRight= ((Boolean) ConstantTable.getConstantTable().getValue("Autonomous.oneBallRight")).booleanValue();
            autonomousTwoBallLeft= ((Boolean) ConstantTable.getConstantTable().getValue("Autonomous.twoBallLeft")).booleanValue();
            autonomousTwoBallMiddle= ((Boolean) ConstantTable.getConstantTable().getValue("Autonomous.twoBallMiddle")).booleanValue();
            autonomousTwoBallRight= ((Boolean) ConstantTable.getConstantTable().getValue("Autonomous.twoBallRight")).booleanValue();
            autonomousThreeBallLeft= ((Boolean) ConstantTable.getConstantTable().getValue("Autonomous.threeBallLeft")).booleanValue();
            autonomousThreeBallMiddle= ((Boolean) ConstantTable.getConstantTable().getValue("Autonomous.threeBallMiddle")).booleanValue();
            autonomousThreeBallRight= ((Boolean) ConstantTable.getConstantTable().getValue("Autonomous.threeBallRight")).booleanValue();

        }
    }

    public void disabledInit(){
        //System.out.println("DisabledInit() called");
        if (robotInitialized){
            robotInitialized = false;
            driveTrain.free();
            intake.free();
            launcher.free();
            ConstantTable.free();
        }
    }
    
    public void disabledPeriodic(){
        //System.out.println("DisabledPeriodic() called");        
    }
    
    public void autonomousInit() {
        robotEnable();

        // Checking to see which autonomous game plan we are doing
        if (autonomousOneBallLeft){
            oneBallLeft();
        }

        if (autonomousOneBallMiddle){
            oneBallMiddle();
        }

        if (autonomousOneBallRight){
            oneBallRight();
        }

        if (autonomousTwoBallLeft){
            twoBallsLeft();
        }

        if (autonomousTwoBallMiddle){
            twoBallsMiddle();
        }

        if (autonomousTwoBallRight){
            twoBallsRight();
        }

        if (autonomousThreeBallLeft){
            threeBallsLeft();
        }

        if (autonomousThreeBallMiddle){
            threeBallsMiddle();
        }

        if (autonomousThreeBallRight){
            threeBallsRight();
        }
    }

    /**
     * This function is called periodically during autonomous
     */
    public void autonomousPeriodic() {
        Scheduler.getInstance().run();
        robotEnable();
        
          
        
        
    }

    public void teleopInit() {
        
    }

    /**
     * This function is called periodically during operator control
     */
    public void teleopPeriodic() {
        Scheduler.getInstance().run();

        /*DRIVER BUTTONS*/

        double driverRightX = driveXbox.getRightX();
        double driverLeftX = driveXbox.getLeftX();
        double driverLeftY = driveXbox.getLeftY();
        //double driverRightTrigger = driveXbox.getRightTrigger();
        boolean driverXButton = driveXbox.getXButton();
        boolean driverLeftBumper = driveXbox.getLeftBumper();
        boolean driverRightBumper = driveXbox.getRightBumper();
        
        driverRightX = applyDeadband(driverRightX);
        driverLeftX = applyDeadband(driverLeftX);
        driverLeftY = applyDeadband(driverLeftY);

        if (driverXButton){
            driveTrain.togglePIDControl();
        }

        //checks to see if button was released
        if (!driveXbox.getYButton() && pastYButton){
            driveTrain.toggleGyroOnOff();
        }
        pastYButton = driveXbox.getYButton();

        //breaking = driveTrain.breaking(driverRightTrigger);
        if (!breaking){
            if (driverLeftBumper){
                driveTrain.minimizeMovement();
            }
            else {
                if (driverRightBumper){
                    driveTrain.enableTank();
                }
                else {
                    driveTrain.enableMecanum();
                }
                driveTrain.drive(driverLeftY, driverRightX, driverLeftX);
            }
        }

        /*OPERATOR BUTTONS*/

        boolean OperatorLeftBumper = operatorXbox.getLeftBumper();
        boolean OperatorBButton = operatorXbox.getBButton();
        double OperatorLeftY = operatorXbox.getLeftY();

        OperatorLeftY = applyDeadband(OperatorLeftY);

        //swaps controls between constant speed or adjusted speed
        if (!constantIntakeSpeed){
            intake.moveBall(OperatorLeftY);
        }
        else {
            if (OperatorLeftY < 0){
                intake.pullIn();
            }
            else if (OperatorLeftY > 0){
                intake.pushOut();
            }
            else {
                intake.stopMotors();
            }
        }

        //prevents intake from retracting when launcher is resetting
        if (launcher.isSafeToRetractIntake()){
            intake.shiftIntakePos(OperatorBButton);
        }

        launcher.stateMachine(OperatorLeftBumper, intake.isExtended());
    }

    public void testPeriodic() {
        robotEnable();
        // LiveWindow.run();
        
    }


    private double applyDeadband(double joystickValue){

        // Sometimes the xBox controller can give us a value greater than one, so this caps it at 1
        if (joystickValue > 1){
            joystickValue = 1;
        }
        else if (joystickValue < -1){
            joystickValue = -1;
        }
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
    
    private void oneBallLeft(){

    if (autoStatus == startingAutonomous){
            intake.shiftIntakePos(false);
            intake.shiftIntakePos(true);
            launcher.stateMachine(true, intake.isExtended());
            launchTimer.setTimer(3.0);

            
              
            }           
        }

    private void oneBallMiddle(){

    }

    private void oneBallRight(){

    }

    private void twoBallsLeft(){
        
    }

    private void twoBallsMiddle(){

    }

    private void twoBallsRight(){

    }

    private void threeBallsLeft(){
        
    }

    private void threeBallsMiddle(){

    }

    private void threeBallsRight(){

    }
}

    
    
    
    
    