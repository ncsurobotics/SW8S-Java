package org.aquapackrobotics.sw8s.states;

import java.util.concurrent.ScheduledThreadPoolExecutor;

import org.aquapackrobotics.sw8s.trainingsim.SimWindow;

public class ForwardXState extends SimState {
	
	double initialX;
	double targetDistance;
	final double kError = 0.5;
	
    public ForwardXState(ScheduledThreadPoolExecutor pool, SimWindow sim, double distance) {
        super(pool, sim);
        targetDistance = distance;        
    }

    public void onEnter() {
    	window.setRobotSpeed(0.0, 0.0, 0.0);
    	initialX = window.getXPos();
    	System.out.println(targetDistance);
    }

    public boolean onPeriodic() {
    	window.setRobotSpeed(Math.signum(targetDistance) * 1.0, 0.0, 0.0);
    	if (Math.abs(window.getXPos() - (initialX + targetDistance)) < kError)
    		return false;
    	else
    		return true;
    }

    public void onExit() {
    	window.setRobotSpeed(0.0, 0.0, 0.0);
    }

    public State nextState() {
    	return null;
    }
}
