package org.aquapackrobotics.sw8s.states;

import java.util.concurrent.ScheduledThreadPoolExecutor;

import org.aquapackrobotics.sw8s.trainingsim.SimWindow;

public class RotateState extends SimState {
	
	double targetRadius;
	
    public RotateState(ScheduledThreadPoolExecutor pool, SimWindow sim, double radius) {
        super(pool, sim);
        targetRadius = radius;
    }

    // TODO: implement
    public void onEnter() {
    	window.setRobotSpeed(0.0, 0.0, 0.0);
    }

    // TODO: implement
    public boolean onPeriodic() {
    	return false;
    }

    // TODO: implement
    public void onExit() {
    	window.setRobotSpeed(0.0, 0.0, 0.0);
    }

    // TODO: implement
    public State nextState() {
        return null;
    }
}
