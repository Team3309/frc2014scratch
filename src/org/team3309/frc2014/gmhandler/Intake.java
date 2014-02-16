/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.team3309.frc2014.gmhandler;

import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Victor;
import org.team3309.frc2014.constantmanager.ConstantTable;

/**
 *
 * @author Ben
 */
public class Intake {
    
    private static Solenoid intakePiston;
    private static Victor topRightMotor;
    private static Victor topLeftMotor;
    private static Victor sideLeftMotor;
    private static Victor sideRightMotor;
    private double[] intakeTopLeftMotor;
    private double[] intakeTopRightMotor;
    private double[] intakeSideLeftMotor;
    private double[] intakeSideRightMotor;
    private double[] loweringPiston;
    private static double topMotorSpeed;
    private static double sideMotorSpeed;
    
    public void intake(){
        intakeTopLeftMotor = ((double[]) ConstantTable.getConstantTable().getValue("Intake.topleft.motor"));
        intakeTopRightMotor = ((double[]) ConstantTable.getConstantTable().getValue("Intake.topright.motor"));
        intakeSideLeftMotor = ((double[]) ConstantTable.getConstantTable().getValue("Intake.sideleft.motor"));
        intakeSideRightMotor = ((double[]) ConstantTable.getConstantTable().getValue("Intake.sideright.motor"));
        loweringPiston = ((double[]) ConstantTable.getConstantTable().getValue("Intake.Solenoid")); 
        topMotorSpeed = ((Double) ConstantTable.getConstantTable().getValue("Intake.topMotorSpeed")).doubleValue();
        sideMotorSpeed = ((Double) ConstantTable.getConstantTable().getValue("Intake.sideMotorSpeed")).doubleValue();
        
 
        
        sideRightMotor = new Victor ((int) intakeSideRightMotor[0], (int) intakeSideRightMotor[1]);
        sideLeftMotor = new Victor ((int) intakeSideLeftMotor [0], (int) intakeSideLeftMotor[1]);
        intakePiston = new Solenoid((int) loweringPiston[0], (int) loweringPiston [1]);
      
      if (intakeTopRightMotor[1] != 0){
                topRightMotor = new Victor((int) intakeTopRightMotor[0], (int) intakeTopRightMotor[1]);
      }  
      
      
      
      if (intakeTopLeftMotor[1] != 0){
                topLeftMotor = new Victor((int) intakeTopLeftMotor[0], (int) intakeTopLeftMotor[1]);
      }
      
      
      
      
    }
    
    
    
    public static void intakeEnable (boolean pull){
        if (pull = true){
            if (topRightMotor == null){
                topRightMotor.set(topMotorSpeed);
        }    
            
            if (topLeftMotor == null){
                topLeftMotor.set(topMotorSpeed);
        }
                          
                sideRightMotor.set(sideMotorSpeed);
                sideLeftMotor.set(sideMotorSpeed);
                intakePiston.set(true);
        }
        else {
            if (topRightMotor == null){
                topRightMotor.set(0);
        }
            if (topLeftMotor == null){
                topRightMotor.set(0);
        }
                
                sideRightMotor.set(0);
                sideLeftMotor.set(0);
                intakePiston.set(false);
        }
        
  
        
        
    }
    
    
    public void free(){
        intakePiston.free();
        if (topRightMotor == null){
            topRightMotor.free();
        }
        
       if (topLeftMotor == null){
           topLeftMotor.free();
       }
       
        sideRightMotor.free();
        sideLeftMotor.free();
        
    }
    
}
