package org.team3309.friarlib;
/**
* Created by jon on 3/10/14.
*/

/*----------------------------------------------------------------------------*/
/* Copyright (c) FIRST 2008-2012. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

import edu.wpi.first.wpilibj.PIDOutput;
import edu.wpi.first.wpilibj.PIDSource;
import edu.wpi.first.wpilibj.communication.UsageReporting;
import edu.wpi.first.wpilibj.parsing.IUtility;
import edu.wpi.first.wpilibj.util.BoundaryException;
import java.util.TimerTask;

/**
 * Class implements a PID Control Loop.
 *
 * Creates a separate thread which reads the given PIDSource and takes
 * care of the integral calculations, as well as writing the given
 * PIDOutput
 */

public class FriarPIDController implements IUtility{

    public static final double kDefaultPeriod = .05;
    private static int instances = 0;
    private double m_maximumOutput = 1.0;	// |maximum output|
    private double m_minimumOutput = -1.0;	// |minimum output|
    private double m_maximumInput = 0.0;		// maximum input - limit setpoint to this
    private double m_minimumInput = 0.0;		// minimum input - limit setpoint to this
    private boolean m_continuous = false;	// do the endpoints wrap around? eg. Absolute encoder
    private boolean m_enabled = false; 			//is the pid controller enabled
    private double m_prevError = 0.0;	// the prior sensor input (used to compute velocity)
    private double m_totalError = 0.0; //the sum of the errors for use in the integral calc
    private Tolerance m_tolerance;	//the tolerance object used to check if on target
    private double m_setpoint = 0.0;
    private double m_error = 0.0;
    private double m_result = 0.0;
    private double scaledPIDInput;
    private double m_period = kDefaultPeriod;
    private double maxUnits = 1;
    private double lastResult;
    PIDSource m_pidInput;
    PIDOutput m_pidOutput;
    java.util.Timer m_controlLoop;
    private boolean m_usingPercentTolerance;

    private double accelerationKP;
    private double accelerationKI;
    private double accelerationKD;
    private double accelerationKF;
    private double accelerationSkid;
    private double decelerationKP;
    private double decelerationKI;
    private double decelerationKD;
    private double decelerationKF;
    private double decelerationSkid;

    /**
     * Tolerance is the type of tolerance used to specify if the PID controller is on target.
     * The various implementations of this class such as PercentageTolerance and AbsoluteTolerance
     * specify types of tolerance specifications to use.
     */
    public interface Tolerance {
        public boolean onTarget();
    }

    public class PercentageTolerance implements Tolerance {
        double percentage;

        PercentageTolerance(double value) {
            percentage = value;
        }

        public boolean onTarget() {
            return (Math.abs(getError()) < percentage / 100
                    * (m_maximumInput - m_minimumInput));
        }
    }

    public class AbsoluteTolerance implements Tolerance {
        double value;

        AbsoluteTolerance(double value) {
            this.value = value;
        }

        public boolean onTarget() {
            return Math.abs(getError()) < value;
        }
    }

    public class NullTolerance implements Tolerance {

        public boolean onTarget() {
            throw new RuntimeException("No tolerance value set when using PIDController.onTarget()");
        }
    }

    private class PIDTask extends TimerTask {

        private FriarPIDController m_controller;

        public PIDTask(FriarPIDController controller) {
            if (controller == null) {
                throw new NullPointerException("Given PIDController was null");
            }
            m_controller = controller;
        }

        public void run() {
            m_controller.calculate();
        }
    }

    /**
     * Allocate a PID object with the given constants for P, I, D, and F
     * Kp= the proportional coefficient
     * Ki= the integral coefficient
     * Kd= the derivative coefficient
     * Kf= the feed forward term
     * source= The PIDSource object that is used to get values
     * output= The PIDOutput object that is set to the output percentage
     * period= the loop time for doing calculations. This particularly effects calculations of the
     * integral and differential terms. The default is 50ms.
     */

    public FriarPIDController(double[] accelerationPID, double[] decelerationPID,
                              PIDSource source, PIDOutput output,
                              double period){

        setPIDValues(accelerationPID, decelerationPID);

        if (source == null) {
            throw new NullPointerException("Null PIDSource was given");
        }
        if (output == null) {
            throw new NullPointerException("Null PIDOutput was given");
        }

        m_controlLoop = new java.util.Timer();

        m_pidInput = source;
        m_pidOutput = output;
        m_period = period;

        m_controlLoop.schedule(new PIDTask(this), 0L, (long) (m_period * 1000));

        instances++;
        UsageReporting.report(UsageReporting.kResourceType_PIDController, instances);
        m_tolerance = new NullTolerance();
    }

    public FriarPIDController(double[] accelerationPID,
                              double[] decelerationPID,
                              PIDSource source,
                              PIDOutput output){
        this(accelerationPID, decelerationPID, source, output,  kDefaultPeriod);
    }

    /**
     * Free the PID object
     */
    public void free() {
        m_controlLoop.cancel();
        m_controlLoop = null;
    }

    /**
     * Read the input, calculate the output accordingly, and write to the output.
     * This should only be called by the PIDTask
     * and is created during initialization.
     */
    protected void calculate() {

        boolean enabled;
        PIDSource pidInput;

        synchronized (this) {
            if (m_pidInput == null) {
                return;
            }
            if (m_pidOutput == null) {
                return;
            }
            enabled = m_enabled; // take snapshot of these values...
            pidInput = m_pidInput;
        }

        if (enabled) {
            scaledPIDInput = (pidInput.pidGet() / maxUnits);

            double result;
            PIDOutput pidOutput;

            if (Math.abs(scaledPIDInput) <= 0.01 && m_setpoint == 0){
                // silence motor humming when stopped
                m_totalError = 0;
            }

            synchronized (this) {

                //If pidGet gives a NaN it jams total error and pidOutput as NaN
                if (Double.isNaN(scaledPIDInput)){
                    m_result = 0;
                }

                else {
                    m_error = m_setpoint - scaledPIDInput;

                    //Code from PIDController for continuous mode
                    if (m_continuous) {
                        if (Math.abs(m_error)
                                > (m_maximumInput - m_minimumInput) / 2) {
                            if (m_error > 0) {
                                m_error = m_error - m_maximumInput + m_minimumInput;
                            } else {
                                m_error = m_error
                                        + m_maximumInput - m_minimumInput;
                            }
                        }
                    }

                    m_totalError += m_error * m_period;

                    double kSkid;

                    if ((scaledPIDInput >= 0 && scaledPIDInput <= m_setpoint) ||
                        (scaledPIDInput <= 0 && scaledPIDInput >= m_setpoint)) {

                        // bot is accelerating
                        m_result = calculatePID(accelerationKP, accelerationKI, accelerationKD, accelerationKF);
                        // limit amount of forward power to prevent wheel spin
                        kSkid = accelerationSkid * m_period;
                    }
                    else {

                        // bot is decelerating
                        m_result = calculatePID(decelerationKP, decelerationKI, decelerationKD, decelerationKF);

                        if ((m_result > 0 && m_setpoint < 0) ||
                                (m_result < 0 && m_setpoint > 0)){
                            kSkid = Math.abs(lastResult) + (Math.max(decelerationSkid, 0.05) * m_period);
                        }
                        else {
                            // allow output to drop to zero + limit amount of reverse power to control skidding
                            kSkid = Math.abs(lastResult) + decelerationSkid * m_period;
                        }
                    }

                    if (m_result - lastResult > kSkid){
                        m_result = lastResult + kSkid;
                    }
                    else if (lastResult - m_result > kSkid){
                        m_result = lastResult - kSkid;
                    }

                    m_prevError = m_error;

                    if (m_result > m_maximumOutput) {
                        m_result = m_maximumOutput;
                    } else if (m_result < m_minimumOutput) {
                        m_result = m_minimumOutput;
                    }
                }

                lastResult = m_result;
                pidOutput = m_pidOutput;
                result = m_result;
            }
            pidOutput.pidWrite(result);
        }
    }

    public double calculatePID(double kP, double kI, double kD, double kF){

        if (kI != 0){
            double maxTotalError = (1 - (kF * Math.abs(m_setpoint))) / kI;
            if (m_totalError >= maxTotalError) m_totalError = maxTotalError;
            else if (m_totalError <= -maxTotalError) m_totalError = -maxTotalError;
        }
        else m_totalError = 0;

        double P = kP * m_error;
        double I = (kI * m_totalError) + (kF * m_setpoint);
        double D = kD * (m_error - m_prevError) / m_period;

        return P + I + D;
    }

    public synchronized double getTotalError() {
        return m_totalError;
    }

    public synchronized void setPIDValues(double[] accelerationPID, double[] decelerationPID){

        accelerationKP = accelerationPID[0];
        accelerationKI = accelerationPID[1];
        accelerationKD = accelerationPID[2];
        accelerationKF = accelerationPID[3];
        accelerationSkid = accelerationPID[4];
        decelerationKP = decelerationPID[0];
        decelerationKI = decelerationPID[1];
        decelerationKD = decelerationPID[2];
        decelerationKF = decelerationPID[3];
        decelerationSkid = decelerationPID[4];

    }

    /**
     * Return the current PID result
     * This is always centered on zero and constrained the the max and min outs
     * @return the latest calculated output
     */
    public synchronized double get() {
        return m_result;
    }

    /**
     *  Set the PID controller to consider the input to be continuous,
     *  Rather then using the max and min in as constraints, it considers them to
     *  be the same point and automatically calculates the shortest route to
     *  the setpoint.
     * @param continuous Set to true turns on continuous, false turns off continuous
     */
    public synchronized void setContinuous(boolean continuous) {
        m_continuous = continuous;
    }

    /**
     *  Set the PID controller to consider the input to be continuous,
     *  Rather then using the max and min in as constraints, it considers them to
     *  be the same point and automatically calculates the shortest route to
     *  the setpoint.
     */
    public synchronized void setContinuous() {
        this.setContinuous(true);
    }

    /**
     * Sets the maximum and minimum values expected from the input.
     *
     * @param minimumInput the minimum percentage expected from the input
     * @param maximumInput the maximum percentage expected from the output
     */
    public synchronized void setInputRange(double minimumInput, double maximumInput) {
        if (minimumInput > maximumInput) {
            throw new BoundaryException("Lower bound is greater than upper bound");
        }
        m_minimumInput = minimumInput;
        m_maximumInput = maximumInput;
        setSetpoint(m_setpoint);
    }

    /**
     * Sets the minimum and maximum values to write.
     *
     * @param minimumOutput the minimum percentage to write to the output
     * @param maximumOutput the maximum percentage to write to the output
     */
    public synchronized void setOutputRange(double minimumOutput, double maximumOutput) {
        if (minimumOutput > maximumOutput) {
            throw new BoundaryException("Lower bound is greater than upper bound");
        }
        m_minimumOutput = minimumOutput;
        m_maximumOutput = maximumOutput;
    }

    /**
     * Set the setpoint for the PIDController
     * @param setpoint the m_setpointd setpoint
     */
    public synchronized void setSetpoint(double setpoint) {
        if (m_maximumInput > m_minimumInput) {
            if (setpoint > m_maximumInput) {
                m_setpoint = m_maximumInput;
            } else if (setpoint < m_minimumInput) {
                m_setpoint = m_minimumInput;
            } else {
                m_setpoint = setpoint;
            }
        } else {
            m_setpoint = setpoint;
        }
    }

    /**
     * Returns the current setpoint of the PIDController
     * @return the current setpoint
     */
    public synchronized double getSetpoint() {
        return m_setpoint;
    }

    /**
     * Returns the current difference of the input from the setpoint
     * @return the current error
     */
    public synchronized double getError() {
        //return m_error;
        return getSetpoint() - m_pidInput.pidGet();
    }

    /**
     * Set the percentage error which is considered tolerable for use with
     * OnTarget. (Input of 15.0 = 15 percent)
     * @param percent error which is tolerable
     * @deprecated Use setTolerance(Tolerance), i.e. setTolerance(new PIDController.PercentageTolerance(15))
     */
    public synchronized void setTolerance(double percent) {
        m_tolerance = new PercentageTolerance(percent);
    }

    /** Set the PID tolerance using a Tolerance object.
     * Tolerance can be specified as a percentage of the range or as an absolute
     * value. The Tolerance object encapsulates those options in an object. Use it by
     * creating the type of tolerance that you want to use: setTolerance(new PIDController.AbsoluteTolerance(0.1))
     * @param tolerance a tolerance object of the right type, e.g. PercentTolerance
     * or AbsoluteTolerance
     */
    private void setTolerance(Tolerance tolerance) {
        m_tolerance = tolerance;
    }

    /**
     * Set the absolute error which is considered tolerable for use with
     * OnTarget.
     * @param absvalue error which is tolerable in the units of the input object
     */
    public synchronized void setAbsoluteTolerance(double absvalue) {
        m_tolerance = new AbsoluteTolerance(absvalue);
    }

    /**
     * Set the percentage error which is considered tolerable for use with
     * OnTarget. (Input of 15.0 = 15 percent)
     * @param percentage error which is tolerable
     */
    public synchronized void setPercentTolerance(double percentage) {
        m_tolerance = new PercentageTolerance(percentage);
    }

    /**
     * Return true if the error is within the percentage of the total input range,
     * determined by setTolerance. This assumes that the maximum and minimum input
     * were set using setInput.
     * @return true if the error is less than the tolerance
     */
    public synchronized boolean onTarget() {
        return m_tolerance.onTarget();
    }

    /**
     * Begin running the PIDController
     */
    public synchronized void enable() {
        m_enabled = true;
    }

    /**
     * Stop running the PIDController, this sets the output to zero before stopping.
     */
    public synchronized void disable() {
        m_pidOutput.pidWrite(0);
        m_enabled = false;
    }

    /**
     * Return true if PIDController is enabled.
     */
    public synchronized boolean isEnable() {
        return m_enabled;
    }

    /**
     * Reset the previous error,, the integral term, and disable the controller.
     */
    public synchronized void reset() {
        disable();
        m_prevError = 0;
        m_totalError = 0;
        m_result = 0;
    }

    public String getSmartDashboardType(){
        return "PIDController";
    }

    public synchronized void setMaxUnits(double units){
        maxUnits = units;
    }

   public synchronized double getPIDInput(){
       return scaledPIDInput;
   }

    /**
     * {@inheritDoc}
     */
    public void updateTable() {
    }

    /**
     * {@inheritDoc}
     */
    public void startLiveWindowMode() {
        disable();
    }

    /**
     * {@inheritDoc}
     */
    public void stopLiveWindowMode() {
    }
}
