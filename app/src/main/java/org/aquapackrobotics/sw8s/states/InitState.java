package org.aquapackrobotics.sw8s.states;

import java.util.concurrent.ScheduledThreadPoolExecutor;

import org.aquapackrobotics.sw8s.trainingsim.SimWindow;
import org.aquapackrobotics.sw8s.constants.GShapeConstants;

public class InitState extends SimState {
    public InitState(ScheduledThreadPoolExecutor pool, SimWindow sim) {
        super(pool, sim);
    }

    public void onEnter() { }

    public boolean onPeriodic() { return false; }

    public void onExit() { }

    public State nextState()
     {
        return new MoveState(pool, this.window, GShapeConstants.coords[0]);
    }
}
