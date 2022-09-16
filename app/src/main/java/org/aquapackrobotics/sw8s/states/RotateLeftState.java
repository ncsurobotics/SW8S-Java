package org.aquapackrobotics.sw8s.states;

import java.util.concurrent.ScheduledThreadPoolExecutor;

import org.aquapackrobotics.sw8s.trainingsim.SimWindow;

public class RotateLeftState extends SimState {
	
	private final double kError = 1.0;
	
    public RotateLeftState(ScheduledThreadPoolExecutor pool, SimWindow sim) {
        super(pool, sim);
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
