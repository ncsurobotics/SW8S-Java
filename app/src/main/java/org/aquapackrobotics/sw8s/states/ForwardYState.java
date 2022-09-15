package org.aquapackrobotics.sw8s.states;

import java.util.concurrent.ScheduledThreadPoolExecutor;

import org.aquapackrobotics.sw8s.trainingsim.SimWindow;

public class ForwardYState extends SimState {
	
	double initialY;
	double targetDistance;
	final double kError = 0.5;
	
    public ForwardYState(ScheduledThreadPoolExecutor pool, SimWindow sim, double distance) {
        super(pool, sim);
        targetDistance = distance;
    }

    public void onEnter() {
    	window.setRobotSpeed(0.0, 0.0, 0.0);
    	initialY = window.getYPos();
    }

    public boolean onPeriodic() {
    	window.setRobotSpeed(0.0, -1.0, 0.0);
    	if (Math.abs(window.getYPos() - (initialY + targetDistance)) < kError)
    		return false;
    	else
    		return true;
    }

    public void onExit() {
    	window.setRobotSpeed(0.0, 0.0, 0.0);
    }

    public State nextState() {
        return new RotateState(pool, window, 100);
    }
}
