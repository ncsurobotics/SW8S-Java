package org.aquapackrobotics.sw8s.states;

import java.util.concurrent.ScheduledThreadPoolExecutor;

import org.aquapackrobotics.sw8s.trainingsim.SimWindow;

public class InitState extends SimState {
    private SimWindow sim;
    public InitState(ScheduledThreadPoolExecutor pool, SimWindow sim) {
        super(pool, sim);
        this.sim = sim;
    }

    // TODO: implement
    public void onEnter() {
        
    }

    // TODO: implement
    public boolean onPeriodic() {
        return true;
    }

    // TODO: implement
    public void onExit() {
    }

    // TODO: implement
    public State nextState() {
        return new StrokeOneState(pool, sim);
    }
}
