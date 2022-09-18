package org.aquapackrobotics.sw8s.states;

import org.aquapackrobotics.sw8s.states.*;
import org.aquapackrobotics.sw8s.trainingsim.SimWindow;
import java.util.concurrent.ScheduledThreadPoolExecutor;

public class MoveNorthState extends SimState {
    public MoveNorthState(ScheduledThreadPoolExecutor pool, SimWindow window) {
        super(pool, window);
    }

    public int getOrder() {
        return 1;
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
