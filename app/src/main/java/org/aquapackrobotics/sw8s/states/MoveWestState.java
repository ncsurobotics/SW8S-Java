package org.aquapackrobotics.sw8s.states;

import org.aquapackrobotics.sw8s.states.*;
import org.aquapackrobotics.sw8s.trainingsim.SimWindow;
import java.util.concurrent.ScheduledThreadPoolExecutor;

public class MoveWestState extends SimState {
    public MoveWestState(ScheduledThreadPoolExecutor pool, SimWindow window) {
        super(pool, window);
    }

    public int getOrder() {
        return 3;
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
