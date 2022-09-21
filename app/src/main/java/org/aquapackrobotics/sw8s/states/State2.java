package org.aquapackrobotics.sw8s.states;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import org.aquapackrobotics.sw8s.trainingsim.SimWindow;

public class State2 extends SimState {
    public State2(ScheduledThreadPoolExecutor pool, SimWindow window) {
        super(pool, window);
        //TODO Auto-generated constructor stub
    }

    // TODO: implement
    public void onEnter() {
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
