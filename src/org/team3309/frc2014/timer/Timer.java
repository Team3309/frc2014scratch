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
    
    public void setTimer(double secs){
        millis = secs * 1000;
        startTimer = System.currentTimeMillis();
    }
    
    public boolean isExpired(){
        if (System.currentTimeMillis() - startTimer >= millis){
            return true;
        }
        else {
            return false;
        }
    }
}
