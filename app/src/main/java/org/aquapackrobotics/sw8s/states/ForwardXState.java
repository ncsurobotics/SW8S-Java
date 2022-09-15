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

    // TODO: implement
    public void onEnter() {
    	window.setRobotSpeed(0.0, 0.0, 0.0);
    	initialX = window.getXPos();
    }

    // TODO: implement
    public boolean onPeriodic() {
    	return false;
    }

    // TODO: implement
    public void onExit() {
    }

    // TODO: implement
    public State nextState() {
    	return null;
    }
}
