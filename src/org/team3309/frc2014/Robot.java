/*----------------------------------------------------------------------------*/
/* Copyright (c) FIRST 2008. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.team3309.frc2014;

import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Gyro;
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
    private Gyro gyro;
    private Toggle intakeRetractedToggle;
    private Toggle drivePIDControllerToggle;
    private Toggle gyroToggle;
    private Timer launchTimer;
    private Timer autoDriveTimer;
    private Timer intakeTimer;
    private Timer hotGoalTimer;
    private Timer autoRotateTimer;
    private DigitalInput hotGoalSensor;
    private double deadband;
    private double launchTime;
    private boolean robotInitialized;
    private boolean constantIntakeSpeed;
    private boolean breaking = false;
    private boolean intakeRetracted;
    private boolean shouldLaunch;
    private boolean autoDebug;
    private boolean notFirstTime;
    private String autonomousPosition;
    private int autonomousNumberOfBalls;
    private int autoStatus;
    private static final int choosingStatus = 1;
    private static final int noBall = 2;
    private static final int oneBallLeft = 3;
    private static final int oneBallMiddle = 4;
    private static final int oneBallRight = 5;
    private static final int twoBallLeft = 6;
    private static final int twoBallMiddle = 7;
    private static final int twoBallRight = 8;
    private static final int threeBallLeft = 9;
    private static final int threeBallMiddle = 10;
    private static final int threeBallRight = 11;
    private static final int movingForward = 12;
    private static final int rotatingRight = 13;
    private int testSelection;
    private int testDrive = 0;
    private int testIntake = 1;
    private int testShooting = 2;
    private int testSensor = 3;


    /**
     * This function is run when the robot is first started up and should be
     * used for any initialization code.
     */
    public void robotInit() {
        driveXbox = new XboxController(1);
        operatorXbox = new XboxController(2);
        // Initialize all subsystems
        //CommandBase.init();
        double[] pressureSwitch = ((double[]) ConstantTable.getConstantTable().getValue("Compressor.pressureSwitch"));
        double[] compressorRelay = ((double[]) ConstantTable.getConstantTable().getValue("Compressor.relay"));
        double[] ports = ((double[]) ConstantTable.getConstantTable().getValue("Gyro.ports"));

        // Create gyro here because of delay in spinning up
        // If done later robot will have a delay in starting
        gyro = new Gyro((int) ports[0], (int) ports[1]);
        gyroToggle = new Toggle();
        drivePIDControllerToggle = new Toggle();
        intakeRetractedToggle = new Toggle();
        
        Compressor compressor = new Compressor((int) pressureSwitch[0], (int) pressureSwitch[1], (int) compressorRelay[0], (int) compressorRelay[1]);
        compressor.start();
        System.out.println("Robot ready");
    }
    
    public void robotEnable(){
        if (!robotInitialized){
            driveTrain = new DriveTrain(gyro);
            launcher = new Launcher();
            intake = new Intake();
            robotInitialized = true;
            deadband = ((Double) ConstantTable.getConstantTable().getValue("Controller.deadband")).doubleValue();
            constantIntakeSpeed = ((Boolean) ConstantTable.getConstantTable().getValue("Controller.constantIntakeSpeed")).booleanValue();
            
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
        autonomousNumberOfBalls = ((Integer) ConstantTable.getConstantTable().getValue("Autonomous.numberOfBalls")).intValue();
        //autonomousPosition = ((String) ConstantTable.getConstantTable().getValue("Autonomous.position"));
        autoDebug = ((Boolean) ConstantTable.getConstantTable().getValue("Autonomous.debug")).booleanValue();
        launchTime = ((Double) ConstantTable.getConstantTable().getValue("Launcher.launchTime")).doubleValue();
        double[] hotGoalSensorArray = ((double[]) ConstantTable.getConstantTable().getValue("Autonomous.hotGoalSensor"));

        hotGoalSensor = new DigitalInput((int) hotGoalSensorArray[0],(int) hotGoalSensorArray[1]);
        hotGoalTimer = new Timer();
        autoRotateTimer = new Timer();
    }

    /**
     * This function is called periodically during autonomous
     */
    public void autonomousPeriodic() {
        Scheduler.getInstance().run();

        autonomousStateMachine();
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
        boolean driverYButton = driveXbox.getYButton();
        boolean driverLeftBumper = driveXbox.getLeftBumper();
        boolean driverRightBumper = driveXbox.getRightBumper();
        
        driverRightX = applyDeadband(driverRightX);
        driverLeftX = applyDeadband(driverLeftX);
        driverLeftY = applyDeadband(driverLeftY);

        // Checks to see if button was pressed
        if (drivePIDControllerToggle.toggle(driverXButton)){
            driveTrain.togglePIDControl();
        }

        //checks to see if button was pressed
        if (gyroToggle.toggle(driverYButton)){
            driveTrain.toggleGyroOnOff();
        }

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
        boolean OperatorRightBumper = operatorXbox.getRightBumper();
        boolean OperatorAButton = operatorXbox.getAButton();
        boolean OperatorYButton = operatorXbox.getYButton();
        double OperatorLeftY = operatorXbox.getLeftY();

        OperatorLeftY = applyDeadband(OperatorLeftY);

        if (OperatorRightBumper) {
            launcher.enablePocketPiston();
        }
        else {
            launcher.disengagePocketPiston();
        }

        // Swaps controls between constant speed or adjusted speed
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
            if (intakeRetractedToggle.toggle(OperatorBButton)){
                intakeRetracted = !intakeRetracted;
                if (intakeRetracted){
                    intake.retractIntake();
                }
                else {
                    intake.extendIntake();
                }
            }

        }

        // Launcher is checking to see if the intake is physically in place
        // because the launcher takes time to extend
        launcher.stateMachine(OperatorLeftBumper, OperatorAButton, OperatorYButton, intake.isExtended());
    }
    
    public void testInit(){
        
        
        robotEnable();
   
    }

    public void testPeriodic() {
        // LiveWindow.run();
        boolean driverXButton = driveXbox.getXButton();        
        boolean driverYButton = driveXbox.getYButton();
        boolean driverAButton = driveXbox.getAButton();
        boolean driverBButton = driveXbox.getBButton();
        boolean driverRightBumper = driveXbox.getRightBumper();
        boolean driverLeftBumper = driveXbox.getLeftBumper();
        boolean driverDup = driveXbox.getDPadUp();
        boolean driverDleft = driveXbox.getDPadLeft();
        boolean driverDright = driveXbox.getDPadRight();
        boolean driverDdown = driveXbox.getDPadDown();
        double driverLeftJoystick = driveXbox.getLeftY();
        
        
        
        
        if (testSelection == testDrive){
            if (d)
        }
        
        else if (testSelection == testIntake){
        
            if (driverXButton){
               intake.extendIntake();
            }
            
            if (driverYButton){
                intake.retractIntake();
            }
            
            if (driverAButton){
                intake.pullIn();
            }
            
            if (driverBButton){
                intake.pushOut();
            }
            
            if (driverRightBumper){
                intake.stopMotors();
            }
            
        } 
        
        else if (testSelection == testShooting){
            
            if (driverXButton){
            launcher.openLatch();
            }
            
            if (driverYButton){
            launcher.closeLatch();
            }
            
            if (driverAButton){
            launcher.engageDog();
            }
            
            if (driverBButton){
            launcher.disengageDog();
            }
            
            if (driverRightBumper){
                launcher.enablePocketPiston();
            }
            
            if (driverLeftBumper){
                launcher.disengagePocketPiston();
            }
            
            if (driverLeftJoystick > 0){
                launcher.loweringLauncher();
            }
            
            if (driverLeftJoystick < 0){
                launcher.stoppingLowering();
            }
        }
            
        else if(testSelection == testSensor){
            
        }
        
        
    
        if (driverDup){
            testSelection = testDrive;
            
        }
        
        if (driverDleft){
            testSelection = testIntake;
            
        }
       
        if (driverDright){
            testSelection = testShooting;
           
        }
        
        if (driverDdown){
            testSelection = testSensor;
        }

        
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
    
    private void autonomousStateMachine(){

        if (autoStatus == choosingStatus){

            if (autonomousNumberOfBalls == 1){
                if (autonomousPosition.equals("left")){
                    if (autoDebug){
                        System.out.println("One ball left");
                    }
                    autoStatus = oneBallLeft;
                }
                else if (autonomousPosition.equals("middle")){
                    if (autoDebug){
                        System.out.println("One ball middle");
                    }
                    autoStatus = oneBallMiddle;
                }
                else {
                    if (autoDebug){
                        System.out.println("One ball right");
                    }
                    autoStatus = oneBallRight;
                }
            }
            else if (autonomousNumberOfBalls == 2){
                if (autonomousPosition.equals("left")){
                    if (autoDebug){
                        System.out.println("Two ball left");
                    }
                    autoStatus = twoBallLeft;
                }
                else if (autonomousPosition.equals("middle")){
                    if (autoDebug){
                        System.out.println("Two ball middle");
                    }
                    autoStatus = twoBallMiddle;
                }
                else {
                    if (autoDebug){
                        System.out.println("Two ball right");
                    }
                    autoStatus = twoBallRight;
                }
            }
            else if (autonomousNumberOfBalls == 3){
                if (autonomousPosition.equals("left")){
                    if (autoDebug){
                        System.out.println("Three ball left");
                    }
                    autoStatus = threeBallLeft;
                }
                else if (autonomousPosition.equals("middle")){
                    if (autoDebug){
                        System.out.println("Three ball middle");
                    }
                    autoStatus = threeBallMiddle;
                }
                else {
                    if (autoDebug){
                        System.out.println("Three ball right");
                    }
                    autoStatus = threeBallRight;
                }
            }
            else {
                if (autoDebug){
                    System.out.println("No ball");
                }
                autoStatus = noBall;
            }
            
            if (autoStatus == noBall){
                intake.extendIntake();
            }

            if (!notFirstTime){
                hotGoalTimer.setTimer(0.2);
                notFirstTime = true;
            }

        }
        
        if (autoStatus == noBall){
            autoDriveTimer.setTimer(1.4);
            autoStatus = movingForward;
        }

        if (autoStatus == oneBallLeft){

            if (hotGoalTimer.isExpired()){
                hotGoalTimer.disableTimer();
                if (hotGoalSensor.get()){
                    shouldLaunch = true;
                    launchTimer.setTimer(launchTime);
                }
                else {
                    autoRotateTimer.setTimer(.5);
                    autoStatus = rotatingRight;

                }
            }
            else if (launchTimer.isExpired()){
                autoDriveTimer.setTimer(1.4);
                autoStatus = movingForward;
            }

        }

        if (autoStatus == rotatingRight){
            driveTrain.drive(0, 0.75, 0);
            if (autoRotateTimer.isExpired()){
                autoRotateTimer.disableTimer();
                driveTrain.drive(0, 0, 0);
            }
        }

        if (autoStatus == movingForward){
            if (autoDebug){
                System.out.println("Moving Forward");
            }
            driveTrain.drive(0.75, 0, 0);
            if (autoDriveTimer.isExpired()){
                autoDriveTimer.disableTimer();
                driveTrain.drive(0, 0, 0);
            }
        }

        launcher.stateMachine(shouldLaunch, false, false, intake.isExtended());
    }



    
    // Subclassed because only Robot.java uses toggles
    public class Toggle{
        boolean lastButtonValue;
        
        public boolean toggle(boolean button){
            
            boolean shouldToggle = button && !lastButtonValue;
            lastButtonValue = button;
            
            return shouldToggle;
        }
    }
}

    
    
    
    
    