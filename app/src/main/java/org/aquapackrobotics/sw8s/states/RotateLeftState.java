package org.aquapackrobotics.sw8s.states;

import java.util.concurrent.ScheduledThreadPoolExecutor;

import org.aquapackrobotics.sw8s.trainingsim.SimWindow;

public class RotateLeftState extends SimState {
	
	private final double kError = 1.0;
	private double targetAngle;
	
    public RotateLeftState(ScheduledThreadPoolExecutor pool, SimWindow sim) {
        super(pool, sim);
    }

    // TODO: implement
    public void onEnter() {
    	window.setRobotSpeed(0.0, 0.0, 0.0);
    	targetAngle = window.getRobotAngle() - 90;
    }

    // TODO: implement
    public boolean onPeriodic() {
    	double yawSpeed = -2.0 * (window.getRobotAngle() - targetAngle);
    	window.setRobotSpeed(0.0, 0.0, yawSpeed);
    	
    	if (Math.abs(window.getRobotAngle() - targetAngle) < kError)
    		return false;
    	else
    		return true;
    		
    }

    // TODO: implement
    public void onExit() {
    	window.setRobotSpeed(0.0, 0.0, 0.0);
    }

    // TODO: implement
    public State nextState() {
    	//If robot is facing left, returns an instance of RotateState
    	//Else, returns an instance of ForwardXState
    	if (targetAngle == -90)
    		return new RotateState(pool, window, 100);
    	else
    		return new ForwardXState(pool, window, -100);
    }
}
