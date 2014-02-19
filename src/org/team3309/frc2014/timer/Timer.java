/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.team3309.frc2014.timer;

/**
 *
 * @author jon
 */
public class Timer {
    
    private double startTimer;
    private double millis;
    private boolean enabled;
    
    public void setTimer(double secs){
        millis = secs * 1000;
        startTimer = System.currentTimeMillis();
        enabled = true;
    }
    
    public boolean isExpired(){
        return enabled && System.currentTimeMillis() - startTimer >= millis;
    }

    public void disableTimer(){
        enabled = false;
    }
}
