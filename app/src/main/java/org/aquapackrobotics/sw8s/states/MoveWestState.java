package org.aquapackrobotics.sw8s.states;

import org.aquapackrobotics.sw8s.states.*;
import org.aquapackrobotics.sw8s.trainingsim.SimWindow;
import java.util.concurrent.ScheduledThreadPoolExecutor;

public class MoveWestState extends SimState {
    public MoveWestState(ScheduledThreadPoolExecutor pool, SimWindow window) {
        super(pool, window);
    }

    // TODO: implement
    public void onEnter() {
        window.setRobotSpeed(5, 0, 0);
    }

    // TODO: implement
    public boolean onPeriodic() {
        if (window.getXPos() == 300) {
            return false;
        }
        return true;
    }

    // TODO: implement
    public void onExit() {
        window.setRobotSpeed(0, 0, 0);
    }

    // TODO: implement
    public State nextState() {
        return new MoveNorthState(pool, window);
    }
}
