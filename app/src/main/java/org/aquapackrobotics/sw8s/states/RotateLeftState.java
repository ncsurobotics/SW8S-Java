package org.aquapackrobotics.sw8s.states;

import java.util.concurrent.ScheduledThreadPoolExecutor;

import org.aquapackrobotics.sw8s.trainingsim.SimWindow;

/**
 * Rotates robot left by 90 degrees
 */
public class RotateLeftState extends SimState {
	
	//Target angle to be rotated to by the robot
	private double targetAngle;
	//Multiplier for setting yawVelocity
	private final double kP = -2.0;
	//Minimum degrees from target angle to be considered done
	private final double kError = 0.5;
	
    public RotateLeftState(ScheduledThreadPoolExecutor pool, SimWindow sim) {
        super(pool, sim);
    }

    public void onEnter() {
    	window.setRobotSpeed(0.0, 0.0, 0.0);
    	targetAngle = window.getRobotAngle() - 90;
    	//Rounds targetAngle to nearest multiple of 90
    	targetAngle = Math.round(targetAngle/(int)90) * 90;
    }

    /**
     * Turns robot at a speed of 1.0 to the target angle.
     * Once robot is within kError of the target angle, returns false to signal completion
     */
    public boolean onPeriodic() {
    	double yawVelocity = kP * (window.getRobotAngle() - targetAngle);
    	window.setRobotSpeed(0.0, 0.0, yawVelocity);
    	
    	if (Math.abs(window.getRobotAngle() - targetAngle) < kError)
    		return false;
    	else
    		return true;
    }

    public void onExit() {
    	window.setRobotSpeed(0.0, 0.0, 0.0);
    }

    /**
     *  If robot is facing left, returns an instance of RotateState.
     *  Else, returns an instance of ForwardXState
     */
    public State nextState() {
    	if (targetAngle == -90)
    		return new RotateState(pool, window, 100);
    	else
    		return new ForwardXState(pool, window, -100);
    }
}
