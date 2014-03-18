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
import org.team3309.frc2014.Testmodes;

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
    private Compressor compressor;
    private Intake intake;
    private DriveTrain driveTrain;
    private Launcher launcher;
    private Gyro gyro;
    private Toggle intakeRetractedToggle;
    private Toggle drivePIDControllerToggle;
    private Toggle gyroToggle;
    private Toggle pocketPistonToggle;
    private Timer hotGoalTimer;
    private Timer stateTimer;
    private DigitalInput hotGoalSensor;
    private Testmodes testModes;
    private double deadband;
    private double waitTime; // Autonomous variables
    private double hotTime;
    private double turnTime;
    private double midTurnTime;
    private double pullInTime;
    private double fireTime;
    private double moveTime;
    private double endTime;
    private double pocketTime;
    private double rotation;
    private double midRotation;
    private double driveSpeed;
    private double drive = 0;
    private double angle = 0;
    private double strafe = 0; //
    private boolean robotInitialized;
    private boolean constantIntakeSpeed;
    private boolean braking = false;
    private boolean intakeRetracted;
    private boolean autoDebug;
    private boolean autoStart;
    private boolean stateComplete;
    private int stateNum = 0;
    private int autoMode;
    private int autoState;
    private int numberOfAutoModes;
    private final int noBall = 0;
    private final int oneBall = 1;
    private final int twoBall = 2;
    private final int threeBall = 3;
    private int[][] steps;

    /*
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
        
        
        compressor = new Compressor((int) pressureSwitch[0], (int) pressureSwitch[1], (int) compressorRelay[0], (int) compressorRelay[1]);
        compressor.start();
        System.out.println("Robot ready");
    }
    
    public void robotEnable(){
        if (!robotInitialized){
            driveTrain = new DriveTrain(gyro);
            launcher = new Launcher(compressor);
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
        driveTrain.setPositionControl();
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
        rotation = ((Double) ConstantTable.getConstantTable().getValue("Autonomous.rotation")).doubleValue();
        midRotation = ((Double) ConstantTable.getConstantTable().getValue("Autonomous.midRotation")).doubleValue();
        pocketTime = ((Double) ConstantTable.getConstantTable().getValue("Autonomous.pocketTime")).doubleValue();
        driveSpeed = ((Double) ConstantTable.getConstantTable().getValue("Autonomous.driveSpeed")).doubleValue();
        numberOfAutoModes = ((Integer) ConstantTable.getConstantTable().getValue("Autonomous.numberOfAutoModes")).intValue();

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

        autonomousStateMachine();
    }

    public void teleopInit() {
        robotEnable();
        driveTrain.setVelocityControl();
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
        if (!braking){

            if (driverRightBumper && !driverLeftBumper){
                driveTrain.enableTank();
            }
            else {
                driveTrain.enableMecanum();
            }
            driveTrain.drive(driverLeftY, driverRightX, driverLeftX, driverLeftBumper);
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
                    launcher.disengagePocketPiston();
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
        testModes = new Testmodes(driveTrain , launcher, intake);
        
        
        
        
   
    }

    public void testPeriodic() {
        robotEnable();
        testModes.enterTest();
        
        
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
        final int fire = 1;
        final int left = 2;
        final int right = 3;
        final int midToRight = 4;
        final int midToLeft = 5;
        final int wait = 6;
        final int move = 7;
        final int pullIn = 8;
        final int end = 9;
        final int pocket = 10;

        if (autoStart){
            autoStart = false;
            stateNum = 0;
            hotGoalTimer.setTimer(hotTime);
            steps = new int[numberOfAutoModes][];
        }

        //Determining autonomous plan
        if (hotGoalTimer.isExpired()){
            hotGoalTimer.disableTimer();

            if (hotGoalSensor == null || hotGoalSensor.get()){
                    steps[noBall] = new int[]{move, end};
                    steps[oneBall] = new int[]{pocket, fire, move, end};
                    steps[twoBall] = new int[]{pocket, fire, pullIn, pocket, fire, move, end};
                    steps[threeBall] = new int[]{left, pocket, fire, pullIn, pocket, fire, right, pullIn, pocket, fire, move, end};
            }
            else {
                steps[noBall] = new int[]{move, end};
                steps[oneBall] = new int[]{wait, fire, move, end};
                steps[twoBall] = new int[]{wait, fire, pullIn, fire, move, end};
                steps[threeBall] = new int[]{right, fire, pullIn, fire, left, pullIn, fire, move, end};
            }

            stateComplete = true;
        }
        //Going to next step
        if (stateComplete){
            stateComplete = false;
            autoState = steps[autoMode][stateNum++];

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
                angle += -rotation;
                time = turnTime;
            }
            else if (autoState == right) {
                if (autoDebug){
                    System.out.println("Auto state: right");
                }
                angle += rotation;
                time = turnTime;
            }
            else if (autoState == midToRight) {
                if (autoDebug){
                    System.out.println("Auto state: mid to right");
                }
                angle += midRotation;
                time = midTurnTime;
            }
            else if (autoState == midToLeft) {
                if (autoDebug){
                    System.out.println("Auto state: mid to left");
                }
                angle += -midRotation;
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
                drive = driveSpeed;
                time = moveTime;
            }
            else if (autoState == pullIn) {
                if (autoDebug){
                    System.out.println("Auto state: pull in");
                }
                intake.pullIn();
                time = pullInTime;
            }
            else if(autoState == pocket) {
                if (autoDebug){
                    System.out.println("Auto state: pocket");
                }
                launcher.engagePocketPiston();
                time = pocketTime;
            }
            else {
                if (autoDebug){
                    System.out.println("Auto state: end");
                }
                time = endTime;
            }

            stateTimer.setTimer(time);
        }

        if (stateTimer.isExpired()){

            stateComplete = true;

            if (autoState == move){
                drive = 0;
            }
            if (autoState == pullIn){
                intake.stopMotors();
                launcher.engagePocketPiston();
            }
            if (autoState == end){
                System.out.println("Autonomous complete");
                stateComplete = false;
                stateTimer.disableTimer();
            }
        }

        driveTrain.drive(drive, angle, strafe, false);

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