package org.aquapackrobotics.sw8s.states;

import java.util.concurrent.ScheduledThreadPoolExecutor;

import org.aquapackrobotics.sw8s.trainingsim.SimWindow;

public class InitState extends State {
    public InitState(ScheduledThreadPoolExecutor pool) {
        super(pool);
    }

    // TODO: implement
    public void onEnter() {
    	SimWindow sim = new SimWindow();
    	sim.setRobotSpeed(0, 0, 0);
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
