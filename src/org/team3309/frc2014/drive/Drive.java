/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.team3309.frc2014.drive;

import edu.wpi.first.wpilibj.CounterBase;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.Victor;
import org.team3309.frc2014.RobotMap;

/**
 *
 * @author Ben
 */
public class Drive {
    
    private OctonumModule topleftwheel; 
    private OctonumModule toprightwheel;
    private OctonumModule bottomleftwheel;
    private OctonumModule bottomrightwheel;
    
    
    public Drive() {
        Solenoid topleftsolenoid = new Solenoid (RobotMap.topleftsolenoid);
        Solenoid toprightsolenoid = new Solenoid (RobotMap.toprightsolenoid);
        Solenoid bottomleftsolenoid = new Solenoid (RobotMap.bottomleftsolenoid);
        Solenoid bottomrightsolenoid = new Solenoid (RobotMap.bottomrightsolenoid);
        SpeedController topleftmotor = new Victor (RobotMap.topleftmotor);
        SpeedController toprightmotor = new Victor (RobotMap.toprightmotor);
        SpeedController bottomleftmotor = new Victor (RobotMap.bottomleftmotor);
        SpeedController bottomrightmotor = new Victor (RobotMap.bottomrightmotor);
        Encoder topleftencoder = new Encoder (RobotMap.topleftencoderA,RobotMap.topleftencoderB,true, CounterBase.EncodingType.k1X);
        Encoder toprightencoder = new Encoder (RobotMap.toprightencoderA,RobotMap.toprightencoderB,false, CounterBase.EncodingType.k1X);
        Encoder bottomleftencoder = new Encoder (RobotMap.bottomleftencoderA,RobotMap.bottomleftencoderB,true, CounterBase.EncodingType.k1X);
        Encoder bottomrightencoder = new Encoder (RobotMap.bottomrightencoderA,RobotMap.bottomrightencoderB,false, CounterBase.EncodingType.k1X);
        this.topleftwheel = new OctonumModule(topleftsolenoid, topleftmotor, topleftencoder, topleftmultiplier);
        this.toprightwheel = new OctonumModule(toprightsolenoid, toprightmotor, toprightencoder, toprightmultiplier);
        this.bottomleftwheel = new OctonumModule(bottomleftsolenoid, bottomleftmotor, bottomleftencoder, bottomleftmultiplier);
        this.bottomrightwheel = new OctonumModule(bottomrightsolenoid, bottomrightmotor, bottomrightencoder, bottomrightmultiplier);
    }
  
    
    static final double[] topleftmultiplier = {1, 1, 1};
    static final double[] toprightmultiplier = {1, -1, -1};
    static final double[] bottomleftmultiplier = {1, 1, -1};
    static final double[] bottomrightmultiplier = {1, -1, 1};
    
    void drive(double drive, double rot, double strafe) {
        topleftwheel.drive(drive, rot, strafe);
        toprightwheel.drive(drive, rot, strafe);
        bottomleftwheel.drive(drive, rot, strafe);
        bottomrightwheel.drive(drive, rot, strafe);
        
        
    
    }
    
    public void enable (boolean active){
        
        topleftwheel.enable(active);
        toprightwheel.enable(active);
        bottomleftwheel.enable(active);
        bottomrightwheel.enable(active);
    }
    
    public void enableMeccanum (boolean active){
        topleftwheel.enable(active);
        toprightwheel.enable(active);
        bottomleftwheel.enable(active);
        bottomrightwheel.enable(active);
    }
    
}
