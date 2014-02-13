/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.team3309.frc2014.gmhandler;

import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.SpeedController;

import edu.wpi.first.wpilibj.Victor;
import org.team3309.frc2014.constantmanager.ConstantTable;

/**
 *
 * @author Ben
 */
public class Intake {
    
    private static Solenoid intakePiston;
    private static Victor MotorFrontRight;
    private static Victor MotorFrontLeft;
    private static Victor MotorSideRight;
    private static Victor MotorSideLeft;
    private double[] intakeMotorFrontLeft;
    private double[] intakeMotorFrontRight;
    private double[] intakeMotorSideLeft;
    private double[] intakeMotorSideRight;
    private double[] loweringPiston;
    
    public void intakeModule(){
        intakeMotorFrontLeft = ((double[]) ConstantTable.getConstantTable().getValue("Intake.MotorFrontLeft"));
        intakeMotorFrontRight = ((double[]) ConstantTable.getConstantTable().getValue("Intake.MotorFrontRight"));
        intakeMotorSideRight = ((double[]) ConstantTable.getConstantTable().getValue("Intake.MotorSideLeft"));
        intakeMotorSideLeft = ((double[]) ConstantTable.getConstantTable().getValue("Intake.MotorSideRight"));
        loweringPiston = ((double[]) ConstantTable.getConstantTable().getValue("Intake.Solenoid"));
        
        MotorFrontRight = new Victor ((int) intakeMotorFrontRight[0], (int) intakeMotorFrontRight[1]);
        MotorFrontLeft = new Victor ((int) intakeMotorFrontLeft[0], (int) intakeMotorFrontLeft[1]);
        MotorSideRight = new Victor ((int) intakeMotorSideRight[0], (int) intakeMotorSideRight[1]);
        MotorSideLeft = new Victor ((int) intakeMotorSideLeft [0], (int) intakeMotorSideLeft[1]);
        intakePiston = new Solenoid((int) loweringPiston[0], (int) loweringPiston [1]);
      
        
    }
    public void lowerIntake(boolean push){
        if(push){
            intakePiston.set(true);
        }
        else{
            intakePiston.set(false);
        }
        
    }
    
    public void pullIn (boolean pull){
        if (pull){
                MotorFrontRight.set(1);
                MotorFrontLeft.set(1);
                MotorSideRight.set(1);
                MotorSideLeft.set(1);
        }
        else {
                MotorFrontRight.set(0);
                MotorFrontLeft.set(0);
                MotorSideRight.set(0);
                MotorSideLeft.set(0);
        }
        
        
        
        
    }
    
    
    
}
