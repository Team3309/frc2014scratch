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
    private Toggle pocketPistonToggle;
    private Toggle driveToggle;
    private Timer launchTimer;
    private Timer autoDriveTimer;
    private Timer intakeTimer;
    private Timer hotGoalTimer;
    private Timer autoRotateTimer;
    private Timer autoWaitTimer;
    private DigitalInput hotGoalSensor;
    private double deadband;
    private double launchTime;
    private double waitTime;
    private double driveTime;
    private double intakeTime;
    private double resetLauncherTime;
    private boolean robotInitialized;
    private boolean constantIntakeSpeed;
    private boolean breaking = false;
    private boolean intakeRetracted;
    private boolean autoDebug;
    private boolean notFirstTime;
    private boolean pocketPistonExtended;
    private boolean driveTopLeftEnabled;
    private boolean driveTopRightEnabled;
    private boolean driveBottomLeftEnabled;
    private boolean driveBottomRightEnabled;
    private boolean autoStart;
    private boolean isRobotPosLeft;
    private boolean isSecondBall;
    private boolean isThirdBall;
    private boolean hot;
    private boolean stateComplete;
    private int stateNum;
    private int autoMode;
    private static final int noBall = 0;
    private static final int oneBall = 1;
    private static final int twoBall = 2;
    private static final int threeBall = 3;

    private int autoState;
    private static final int movingForward = 6;
    private static final int rotatingRight = 7;
    private static final int rotatingLeft = 8;
    private static final int rotatingLeftFromMiddle = 9;
    private static final int rotatingRightFromMiddle = 10;
    private static final int pullIn = 11;
    private int testSelection;


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
        pocketPistonToggle = new Toggle();
        
        Compressor compressor = new Compressor((int) pressureSwitch[0], (int) pressureSwitch[1], (int) compressorRelay[0], (int) compressorRelay[1]);
        compressor.start();
        System.out.println("Robot ready");
    }
    
    public void robotEnable(){
        if (!robotInitialized){
            driveTrain = new DriveTrain(gyro);
            launcher = new Launcher();
            intake = new Intake();
            intake.extendIntake();
            robotInitialized = true;
            deadband = ((Double) ConstantTable.getConstantTable().getValue("Controller.deadband")).doubleValue();
            constantIntakeSpeed = ((Boolean) ConstantTable.getConstantTable().getValue("Controller.constantIntakeSpeed")).booleanValue();
        }
    }

    public void disabledInit(){
        //System.out.println("DisabledInit() called");
        if (robotInitialized){
            launcher.closeLatch();
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
        intake.extendIntake();
        int autonomousNumberOfBalls = ((Integer) ConstantTable.getConstantTable().getValue("Autonomous.numberOfBalls")).intValue();
        //autonomousPosition = ((String) ConstantTable.getConstantTable().getValue("Autonomous.position"));
        autoDebug = ((Boolean) ConstantTable.getConstantTable().getValue("Autonomous.debug")).booleanValue();
        launchTime = ((Double) ConstantTable.getConstantTable().getValue("Autonomous.launchTime")).doubleValue();
        resetLauncherTime = ((Double) ConstantTable.getConstantTable().getValue("Autonomous.resetLauncherTime")).doubleValue();
        driveTime = ((Double) ConstantTable.getConstantTable().getValue("Autonomous.driveTime")).doubleValue();
        waitTime = ((Double) ConstantTable.getConstantTable().getValue("Autonomous.waitTime")).doubleValue();
        intakeTime = ((Double) ConstantTable.getConstantTable().getValue("Autonomous.intakeTime")).doubleValue();
        double[] hotGoalSensorArray = ((double[]) ConstantTable.getConstantTable().getValue("Autonomous.hotGoalSensor"));

        if (hotGoalSensorArray[0] != 0){
            hotGoalSensor = new DigitalInput((int) hotGoalSensorArray[0],(int) hotGoalSensorArray[1]);
        }
        hotGoalTimer = new Timer();
        autoRotateTimer = new Timer();
        autoDriveTimer = new Timer();
        launchTimer = new Timer();
        intakeTimer = new Timer();
        autoWaitTimer= new Timer();

        if (autonomousNumberOfBalls == 1){
            if (autoDebug){
                System.out.println("Auto mode: One ball");
            }
            autoMode = oneBall;
        }
        else if (autonomousNumberOfBalls == 2){
            if (autoDebug){
                System.out.println("Auto mode: Two Ball");
            }
            autoMode = twoBall;
        }
        else if (autonomousNumberOfBalls == 3){
            if (autoDebug){
                System.out.println("Auto mode: Three Ball");
            }
            autoMode = threeBall;
        }
        else {
            if (autoDebug){
                System.out.println("Auto mode: No ball");
            }
            autoMode = noBall;
        }
    }

    /**
     * This function is called periodically during autonomous
     */
    public void autonomousPeriodic() {
        Scheduler.getInstance().run();

        autonomousStateMachine();
    }

    public void teleopInit() {
        robotEnable();
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
        boolean driverLeftBumper = false;
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
                driveTrain.normalMovement();

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
        boolean OperatorXButton = operatorXbox.getXButton();
        double OperatorLeftY = operatorXbox.getLeftY();
        

        OperatorLeftY = applyDeadband(OperatorLeftY);

        if (pocketPistonToggle.toggle(OperatorRightBumper)) {
            launcher.togglePocketPiston();
        }

        if (launcher.isCatapultInPos() && launcher.isCatapultLatched()){
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
        }
        else {
            intake.stopMotors();
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
        boolean[] launcherParameterArray = {OperatorLeftBumper, OperatorAButton, OperatorYButton, intake.isExtended(), OperatorXButton};
        launcher.stateMachine(launcherParameterArray);
    }
    
    public void testInit(){

        robotEnable();
        driveToggle = new Toggle();
   
    }

    public void testPeriodic() {
        robotEnable();
        // LiveWindow.run();

        final int testDrive = 0;
        final int testIntake = 1;
        final int testShooting = 2;
        final int testSensor = 3;

        boolean driverDownPad = driveXbox.getDPadDown();
        boolean driverUpPad = driveXbox.getDPadUp();
        boolean driverRightPad = driveXbox.getDPadRight();
        boolean driverLeftPad = driveXbox.getDPadLeft();

        //Disables and renables each wheel accordingly
        if (driverDownPad) driveTrain.enableTestMode("topLeft");
        else driveTrain.disableTestMode("topLeft");

        if (driverUpPad) driveTrain.enableTestMode("topRight");
        else driveTrain.disableTestMode("topRight");

        if (driverLeftPad) driveTrain.enableTestMode("bottomLeft");
        else driveTrain.disableTestMode("bottomLeft");

        if (driverRightPad) driveTrain.enableTestMode("bottomRight");
        else driveTrain.disableTestMode("bottomRight");

        // LiveWindow.run();        
        //Disables and renables each wheel accordingly
        
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
            
            if (driveToggle.toggle(driverDup)) {
                driveTopLeftEnabled = !driveTopLeftEnabled;
                
                if (driveTopLeftEnabled){
                    driveTrain.enableTestMode("topLeft");
                    
                } 
                else {
                    driveTrain.disableTestMode("topLeft");
                }
                
            }
        

        if (driveToggle.toggle(driverDright)) {
                driveTopRightEnabled = !driveTopRightEnabled;
                
                if (driveTopRightEnabled){
                    driveTrain.enableTestMode("topRight");
                    
                } 
                else {
                    driveTrain.disableTestMode("topRight");
                }
                
            }
        
        if (driveToggle.toggle(driverDdown)) {
                driveBottomLeftEnabled = !driveBottomLeftEnabled;
                
                if (driveBottomLeftEnabled){
                    driveTrain.enableTestMode("bottomLeft");
                    
                } 
                else {
                    driveTrain.disableTestMode("bottomLeft");
                }
                
            }
        
        if (driveToggle.toggle(driverDright)) {
                driveBottomRightEnabled = !driveBottomRightEnabled;
                
                if (driveBottomRightEnabled){
                    driveTrain.enableTestMode("bottomRight");
                    
                } 
                else {
                    driveTrain.disableTestMode("bottomRight");
                }
                
            }
            
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
                launcher.engagePocketPiston();
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
/*
        double time;
        final int fire = 0;
        final int left = 1;
        final int right = 2;
        final int midToRight = 3;
        final int midToLeft = 4;
        final int wait = 5;
        final int move = 6;
        final int pullIn = 7;
        final int end = 8;

        if (autoStart){
            autoStart = false;
            hotGoalTimer.setTimer(hotTime);
        }

        if (hotGoalTimer.isExpired()){
            hotGoalTimer.disableTimer();
            if (hotGoalSensor.get()){
                int[][] stepArray = {{move, fire, fire, left},
                                    {end, move, pullIn, fire},
                                    {end, end, fire, pullIn},
                                    {end, end, move, fire},
                                    {end, end, end, right},
                                    {end, end, end, pullIn},
                                    {end, end, end, fire},
                                    {end, end, end, move},
                                    {end, end, end, end},
                                    {end, end, end, end}};
                }
            else {
                int[][] stepArray = {{move, wait, wait, right},
                                    {end, fire, fire, fire},
                                    {end, move, pullIn, pullIn},
                                    {end, end, fire, fire},
                                    {end, end, move, left},
                                    {end, end, end, pullIn},
                                    {end, end, end, fire},
                                    {end, end, end, move},
                                    {end, end, end, end},
                                    {end, end, end, end}};
            }
        }

        if (stateComplete){
            stateComplete = false;
            autoState = stepArray[autoMode][stateNum];
            stateNum ++;
            if (autoState == fire) time = fireTime;
            else if (autoState == left) time = turnTime;
            else if (autoState == right) time = turnTime;
            else if (autoState == midToRight) time = midTurnTime;
            else if (autoState == midToLeft) time = midTurnTime;
            else if (autoState == wait) time = waitTime;
            else if (autoState == move) time = moveTime;
            else if (autoState == pullIn) time = pullInTime;
            else time = endTime;

            stateTimer.disableTimer();
            stateTimer.setTimer(time);
        }

        if (autoState == fire){
            if (stateTimer.isExpired){
                stateComplete = true;
            }
            else {
                shouldLaunch = true;
            }
        }
        if (autoState == left){

        }
        if (autoState == right){

        }
        if (autoState == midToRight){

        }
        if (autoState == midToLeft){

        }
        if (autoState == wait){

        }
        if (autoState == move){

        }
        if (autoState == pullIn){

        }
        if (autoState == end){

        }

        //Parameters pulled out to help with documentation
        boolean manualLaunch = false;
        boolean manualReset = false;
        boolean renableLauncher = false;
        boolean[] launcherParameterArray = {shouldLaunch, manualLaunch, manualReset, intake.isExtended(), renableLauncher};
        launcher.stateMachine(launcherParameterArray);
        boolean shouldLaunch = false;

        if (hotGoalSensor != null){

            if (autoState == oneBall){

                //Checks to see if the hot goal has dropped
                if (hotGoalTimer.isExpired()){
                    hotGoalTimer.disableTimer();
                    //Checks to see if goal is hot
                    if (hotGoalSensor.get()){
                        shouldLaunch = true;
                        launchTimer.setTimer(launchTime);
                    }
                    else {
                        //Goal is not hot so waits 5 seconds
                        autoWaitTimer.setTimer(waitTime);
                    }
                }
                //Checks to see if the robot had to wait then launches if time has passed
                else if (autoWaitTimer.isExpired()){
                    shouldLaunch = true;
                    autoWaitTimer.disableTimer();
                    launchTimer.setTimer(launchTime);
                }
                //Checks to see if launched
                else if (launchTimer.isExpired()){
                    autoDriveTimer.setTimer(driveTime);
                    autoState = movingForward;
                }

            }

            if (autoState == twoBall){
                if (autoStart){
                    //Checks to see if goal is hot
                    if (hotGoalSensor.get()){
                        autoState = rotatingLeftFromMiddle;
                    }
                    else {
                        //Goal is not hot so waits
                        autoState = rotatingRightFromMiddle;
                    }
                }
                //Checks to see if finished launching
                else if (launchTimer.isExpired()){
                    //Pulls ball in
                    launchTimer.disableTimer();
                    intakeTimer.setTimer(intakeTime);
                    autoState = pullIn;
                }

                else if (intakeTimer.isExpired()){
                    intakeTimer.disableTimer();
                    shouldLaunch = true;
                    autoState = movingForward;
                }
            }

            if (autoState == threeBall){
                //Checks to see if goal is hot
                if (hotGoalTimer.isExpired()){
                    hotGoalTimer.disableTimer();
                    if (hotGoalSensor.get()){
                        autoState = rotatingLeftFromMiddle;
                    }
                    else {
                        autoState = rotatingRightFromMiddle;
                    }
                }
                //Checks to see if finished launching
                else if (launchTimer.isExpired()){
                    //Pulls ball in
                    launchTimer.disableTimer();
                    shouldLaunch = false;
                    intakeTimer.setTimer(intakeTime);
                    autoState = pullIn;
                }
                else if (intakeTimer.isExpired()){
                    if (isSecondBall){
                        if (isRobotPosLeft){
                            autoState = rotatingRight;
                        }
                        else {
                            autoState = rotatingLeft;
                        }
                    }
                    else {
                        autoState = movingForward;
                    }
                }
            }
        }
        else {
            if (autoState == oneBall){
                if (autoStart){
                    shouldLaunch = true;
                    launchTimer.setTimer(launchTime);
                    autoStart = false;
                }
                else if (launchTimer.isExpired()){
                    shouldLaunch = false;
                    launchTimer.disableTimer();
                    autoDriveTimer.setTimer(driveTime);
                    autoState = movingForward;
                }
            }

            if (autoState == twoBall){
                if (autoStart){
                    shouldLaunch = true;
                    launchTimer.setTimer(launchTime);
                    autoStart = false;
                }
                else if (launchTimer.isExpired()){
                    shouldLaunch = false;
                    launchTimer.disableTimer();
                    autoState = pullIn;
                    intakeTimer.setTimer(intakeTime);
                }
                else if (intakeTimer.isExpired()){
                    shouldLaunch = true;
                    autoDriveTimer.setTimer(driveTime);
                    autoState = movingForward;
                }
            }

            if (autoState == threeBall){
                if (autoStart){
                    autoState = rotatingLeftFromMiddle;
                    autoStart = false;
                    isSecondBall = true;
                }
                else if (autoRotateTimer.isExpired()){
                    autoRotateTimer.disableTimer();
                    intakeTimer.setTimer(intakeTime);
                    autoState = pullIn;
                }
                else if (intakeTimer.isExpired() && isSecondBall){
                    intakeTimer.disableTimer();
                    shouldLaunch = true;
                    launchTimer.setTimer(launchTime);
                    autoState = rotatingRight;
                    isSecondBall = false;
                    isThirdBall = true;
                }
                else if (launchTimer.isExpired()){
                    launchTimer.disableTimer();
                    intakeTimer.setTimer(intakeTime);
                    autoState = pullIn;
                }
                else if (intakeTimer.isExpired() && isThirdBall){
                    intakeTimer.disableTimer();
                    shouldLaunch = true;
                    autoDriveTimer.setTimer(driveTime);
                    autoState = movingForward;
                }
            }
        }

        if (autoState == rotatingRight){
            driveTrain.drive(0, 0.75, 0);
            if (autoRotateTimer.isExpired()){
                autoRotateTimer.disableTimer();
                driveTrain.drive(0, 0, 0);
                autoState = choosingStatus;
            }
        }

        if (autoState == rotatingLeft){
            driveTrain.drive(0, -0.75, 0);
            if (autoRotateTimer.isExpired()){
                autoRotateTimer.disableTimer();
                driveTrain.drive(0, 0, 0);
                autoState = choosingStatus;
            }
        }

        if (autoState == rotatingLeftFromMiddle){

        }

        if (autoState == rotatingRightFromMiddle){

        }

        if (autoState == movingForward){
            if (autoDebug){
                System.out.println("Moving Forward");
            }
            driveTrain.drive(1, 0, 0);
            if (autoDriveTimer.isExpired()){
                autoDriveTimer.disableTimer();
                driveTrain.drive(0, 0, 0);
            }
        }
*/
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