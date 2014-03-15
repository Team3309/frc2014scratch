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
    private Timer hotGoalTimer;
    private Timer stateTimer;
    private DigitalInput hotGoalSensor;
    private double deadband;
    private double waitTime;
    private double hotTime;
    private double turnTime;
    private double midTurnTime;
    private double pullInTime;
    private double fireTime;
    private double moveTime;
    private double endTime;
    private boolean robotInitialized;
    private boolean constantIntakeSpeed;
    private boolean breaking = false;
    private boolean intakeRetracted;
    private boolean autoDebug;
    private boolean driveTopLeftEnabled;
    private boolean driveTopRightEnabled;
    private boolean driveBottomLeftEnabled;
    private boolean driveBottomRightEnabled;
    private boolean autoStart;
    private boolean isSecondBall;
    private boolean isThirdBall;
    private boolean stateComplete;
    private int stateNum = 0;
    private int autoMode;
    private int autoState;
    private int testSelection;
    private int numberOfAutoModes;
    private int numberOfSteps;
    private final int noBall = 0;
    private final int oneBall = 1;
    private final int twoBall = 2;
    private final int threeBall = 3;

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
        autoDebug = ((Boolean) ConstantTable.getConstantTable().getValue("Autonomous.debug")).booleanValue();
        waitTime = ((Double) ConstantTable.getConstantTable().getValue("Autonomous.waitTime")).doubleValue();
        double[] hotGoalSensorArray = ((double[]) ConstantTable.getConstantTable().getValue("Autonomous.hotGoalSensor"));
        hotTime = ((Double) ConstantTable.getConstantTable().getValue("Autonomous.hotTime")).doubleValue();
        turnTime = ((Double) ConstantTable.getConstantTable().getValue("Autonomous.turnTime")).doubleValue();
        midTurnTime = ((Double) ConstantTable.getConstantTable().getValue("Autonomous.midTurnTime")).doubleValue();
        pullInTime = ((Double) ConstantTable.getConstantTable().getValue("Autonomous.pullInTime")).doubleValue();
        fireTime = ((Double) ConstantTable.getConstantTable().getValue("Autonomous.fireTime")).doubleValue();
        moveTime = ((Double) ConstantTable.getConstantTable().getValue("Autonomous.moveTime")).doubleValue();
        endTime = ((Double) ConstantTable.getConstantTable().getValue("Autonomous.endTime")).doubleValue();
        numberOfAutoModes = ((Integer) ConstantTable.getConstantTable().getValue("Autonomous.numberOfAutoModes")).intValue();
        numberOfSteps = ((Integer) ConstantTable.getConstantTable().getValue("Autonomous.numberOfSteps")).intValue();

        if (hotGoalSensorArray[0] != 0){
            hotGoalSensor = new DigitalInput((int) hotGoalSensorArray[0],(int) hotGoalSensorArray[1]);
        }
        hotGoalTimer = new Timer();
        stateTimer = new Timer();

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

        autoStart = true;
    }

    /**
     * This function is called periodically during autonomous
     */
    public void autonomousPeriodic() {
        Scheduler.getInstance().run();


        //autonomousStateMachine();
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

        boolean shouldLaunch = false;

        double time;
        int[][] steps = new int[numberOfAutoModes][numberOfSteps];
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
            stateComplete = true;
            stateNum = 0;
            hotGoalTimer.setTimer(hotTime);
        }

        if (hotGoalTimer.isExpired()){
            hotGoalTimer.disableTimer();

            if (hotGoalSensor != null){
                if (hotGoalSensor.get()){
                    steps[noBall] = new int[]{move, end};
                    steps[oneBall] = new int[]{fire, move, end};
                    steps[twoBall] = new int[]{fire, pullIn, fire, move, end};
                    steps[threeBall] = new int[]{left, fire, pullIn, fire, right, pullIn, fire, move, end};
                }
                else {
                    steps[noBall] = new int[]{move, end};
                    steps[oneBall] = new int[]{wait, fire, move, end};
                    steps[twoBall] = new int[]{wait, fire, pullIn, fire, move, end};
                    steps[threeBall] = new int[]{right, fire, pullIn, fire, left, pullIn, fire, move, end};
                }
            }
            else {
                steps[noBall] = new int[]{move, end};
                steps[oneBall] = new int[]{fire, move, end};
                steps[twoBall] = new int[]{fire, pullIn, fire, move, end};
                steps[threeBall] = new int[]{left, fire, pullIn, fire, right, pullIn, fire, move, end};
            }
        }

        if (stateComplete){
            stateComplete = false;
            autoState = steps[autoMode][stateNum];
            stateNum ++;

            if (autoState == fire) {
                if (autoDebug){
                    System.out.println("Auto state: fire");
                }
                shouldLaunch = true;
                time = fireTime;
            }
            else if (autoState == left) {
                if (autoDebug){
                    System.out.println("Auto state: left");
                }
                driveTrain.drive(0,-0.4, 0);
                time = turnTime;
            }
            else if (autoState == right) {
                if (autoDebug){
                    System.out.println("Auto state: right");
                }
                driveTrain.drive(0,0.4, 0);
                time = turnTime;
            }
            else if (autoState == midToRight) {
                if (autoDebug){
                    System.out.println("Auto state: mid to right");
                }
                driveTrain.drive(0, 0.2, 0);
                time = midTurnTime;
            }
            else if (autoState == midToLeft) {
                if (autoDebug){
                    System.out.println("Auto state: mid to left");
                }
                driveTrain.drive(0, -0.2, 0);
                time = midTurnTime;
            }
            else if (autoState == wait) {
                if (autoDebug){
                    System.out.println("Auto state: wait");
                }
                time = waitTime;
            }
            else if (autoState == move) {
                if (autoDebug){
                    System.out.println("Auto state: move");
                }
                driveTrain.drive(1, 0, 0);
                time = moveTime;
            }
            else if (autoState == pullIn) {
                if (autoDebug){
                    System.out.println("Auto state: pull in");
                }
                intake.pullIn();
                time = pullInTime;
            }
            else {
                if (autoDebug){
                    System.out.println("Auto state: end");
                }
                time = endTime;
            }

            stateTimer.disableTimer();
            stateTimer.setTimer(time);
        }

        if (autoState == fire){
            if (stateTimer.isExpired()){
                stateComplete = true;
            }
        }

        if (autoState == left){
            if (stateTimer.isExpired()){
                driveTrain.drive(0,0,0);
                stateComplete = true;
            }
        }
        
        if (autoState == right){
            if (stateTimer.isExpired()){
                driveTrain.drive(0,0,0);
                stateComplete = true;
            }
        }
        if (autoState == midToRight){
            if (stateTimer.isExpired()){
                driveTrain.drive(0,0,0);
                stateComplete = true;
            }
        }
        if (autoState == midToLeft){
            if (stateTimer.isExpired()){
                driveTrain.drive(0,0,0);
                stateComplete = true;
            }
        }
        if (autoState == wait){
            if (stateTimer.isExpired()){
                stateComplete = true;
            }
        }
        if (autoState == move){
            if (stateTimer.isExpired()){
                driveTrain.drive(0, 0, 0);
                stateComplete = true;
            }
        }
        if (autoState == pullIn){
            if (stateTimer.isExpired()){
                launcher.engagePocketPiston();
                stateComplete = true;
            }
        }
        if (autoState == end){
            System.out.println("Autonomous complete");
        }

        //Parameters pulled out to help with documentation
        boolean manualLaunch = false;
        boolean manualReset = false;
        boolean renableLauncher = false;
        boolean[] launcherParameterArray = {shouldLaunch, manualLaunch, manualReset, intake.isExtended(), renableLauncher};
        launcher.stateMachine(launcherParameterArray);

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