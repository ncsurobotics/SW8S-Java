package org.aquapackrobotics.sw8s.states;

import java.util.concurrent.ScheduledThreadPoolExecutor;

import org.aquapackrobotics.sw8s.trainingsim.SimWindow;

public class ForwardYState extends SimState {
	
	double initialY;
	double targetDistance;
	
    public ForwardYState(ScheduledThreadPoolExecutor pool, SimWindow sim, double distance) {
        super(pool, sim);
        targetDistance = distance;
    }

    // TODO: implement
    public void onEnter() {
    	window.setRobotSpeed(0.0, 0.0, 0.0);
    	initialY = window.getYPos();
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
