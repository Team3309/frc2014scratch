/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.team3309.frc2014.subsystems;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Gyro;
import edu.wpi.first.wpilibj.SolenoidBase;
import org.team3309.frc2014.drive.OctonumModule;
import edu.wpi.first.wpilibj.Solenoid;
import org.team3309.frc2014.constantmanager.ConstantTable;
import org.team3309.friarlib.RobotAngleGyro;

/**
 *
 * @author Jon/Ben
 */
public class DriveTrain{
    
    private SolenoidBase modePiston;
    private OctonumModule[] driveTrainWheels;
    private RobotAngleGyro robotAngleGyro;
    private boolean noSolenoids;
    private boolean gyroEnabled;
    private double maxStrafe;
    private boolean doubleSolenoid;
    private int wheelNum;

    public DriveTrain(Gyro gyro) {

        double[] driveModePiston = ((double[]) ConstantTable.getConstantTable().getValue("DriveTrain.solenoid"));

        if (driveModePiston[1] == 0){
            noSolenoids = true;
        }
        else {
            if (driveModePiston[2] == 0){
                modePiston = new Solenoid((int) driveModePiston[0], (int) driveModePiston[1]);
            }
            else{
                modePiston = new DoubleSolenoid((int) driveModePiston[0], (int) driveModePiston[1], (int) driveModePiston[2]);
                doubleSolenoid = true;
            }
        }

        driveTrainWheels = new OctonumModule[4];
        driveTrainWheels[0] = new OctonumModule("Octonum.topleft", true);
        driveTrainWheels[1] = new OctonumModule("Octonum.topright", true);
        driveTrainWheels[2] = new OctonumModule("Octonum.bottomleft", false);
        driveTrainWheels[3] = new OctonumModule("Octonum.bottomright", false);

        robotAngleGyro = new RobotAngleGyro(gyro);

        maxStrafe = ((Double) ConstantTable.getConstantTable().getValue("DriveTrain.maxStrafe")).doubleValue();

        gyroEnabled = ((Boolean) ConstantTable.getConstantTable().getValue("Gyro.enabled")).booleanValue();

        setGyroMode();
        enableMecanum();
    }

    public void free(){
        modePiston.free();
        robotAngleGyro.free();
        for (int i = 0; i < 4; i++){
            driveTrainWheels[i].free();
        }

    }

    public void drive(double drive, double rot, double strafe) {

        double highestWheelSpeed = 1.0;
        double wheelSpeed;
        double adjustedRotation;

        if (strafe <= -maxStrafe){
            strafe = -maxStrafe;
        }
        if (strafe >= maxStrafe){
            strafe = maxStrafe;
        }
        if (gyroEnabled){
            adjustedRotation = robotAngleGyro.getDesiredRotationVelocity(rot, drive + strafe);
        }
        else {
            adjustedRotation = rot;
        }


        for (int i = 0; i < 4; i++) {
            wheelSpeed = driveTrainWheels[i].setRawSpeed(drive, adjustedRotation, strafe);
            if (wheelSpeed > highestWheelSpeed){
                highestWheelSpeed = wheelSpeed;
            }
        }
        for (int i = 0; i < 4; i++) {
            driveTrainWheels[i].setNormalizationFactor(1 / highestWheelSpeed);
        }
    }

    public void enableTank(){
        if (!noSolenoids){
            if (doubleSolenoid){
                ((DoubleSolenoid) modePiston).set(DoubleSolenoid.Value.kForward);
            }
            else{
                ((Solenoid) modePiston).set(true);
            }
            for (int i = 0; i < 4; i++) {
                driveTrainWheels[i].enableTank();
            }
        }

        robotAngleGyro.setTank();
    }

    public void enableMecanum(){
        if (!noSolenoids){
            if (doubleSolenoid){
                ((DoubleSolenoid) modePiston).set(DoubleSolenoid.Value.kReverse);
            }
            else{
                ((Solenoid) modePiston).set(false);
            }
        }
        for (int i = 0; i < 4; i++) {
            driveTrainWheels[i].enableMecanum();
        }

        robotAngleGyro.setMecanum();
    }

    public void togglePIDControl(){

        for (int i = 0; i < 4; i++) {
            driveTrainWheels[i].togglePIDController();
        }

        setGyroMode();
    }

    private void setGyroMode(){

        if (driveTrainWheels[1].areEncodersEnabled()){
            robotAngleGyro.setWheelSpeedControlMode();
        }
        else {
            robotAngleGyro.setVoltageControlMode();
        }
    }

    public void toggleGyroOnOff(){
        if (!gyroEnabled){
            robotAngleGyro.reset();
            gyroEnabled = true;
        }
        else {
            gyroEnabled = false;
        }
    }

    public void minimizeMovement(){
        for (int i = 0; i < 4; i++){
            driveTrainWheels[i].freezeInPlace();
            enableTank();
        }
    }

    public void normalMovement(){
        for (int i = 0; i < 4; i++){
            driveTrainWheels[i].normalMovement();
        }
    public void enableTestMode(String wheelName){
        if (wheelName.equals("topLeft")){
        wheelNum = 0;        
        }
        
        if (wheelName.equals("topRight")){
        wheelNum = 1;        
        }
        
        if (wheelName.equals("bottomLeft")){
        wheelNum = 2;        
        }
        
        if (wheelName.equals("bottomRight")){
        wheelNum = 3;        
        }
        
        driveTrainWheels[wheelNum].enableTestMode();
    }

    public void enableTestMode(){
    }

    public void disableTestMode(int wheelNum){
    
    
    public void disableTestMode(String wheelName){
        if (wheelName.equals("topLeft")){
            wheelNum = 0;
        }
        
        if (wheelName.equals("topRight")){
        wheelNum = 1;        
        }
        
        if (wheelName.equals("bottomLeft")){
        wheelNum = 2;        
        }
        
        if (wheelName.equals("bottomRight")){
        wheelNum = 3;        
        }
        
        driveTrainWheels[wheelNum].disableTestMode();
    }


    /*public boolean breaking(double breakingPower){
        for (int i = 0; i < 4; i++) {
            driveTrainWheels[i].breaking(breakingPower);
            return true;
        }
        return false;
    }*/

    /**
     * enable TankMode by enabling Piston
     */
}