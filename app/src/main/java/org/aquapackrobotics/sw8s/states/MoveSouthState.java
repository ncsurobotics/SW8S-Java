package org.aquapackrobotics.sw8s.states;

import org.aquapackrobotics.sw8s.states.*;
import org.aquapackrobotics.sw8s.trainingsim.SimWindow;
import java.util.concurrent.ScheduledThreadPoolExecutor;

public class MoveSouthState extends SimState {
    public MoveSouthState(ScheduledThreadPoolExecutor pool, SimWindow window) {
        super(pool, window);
    }

    // TODO: implement
    public void onEnter() {
        window.setRobotSpeed(0, 5, 0);
    }

    // TODO: implement
    public boolean onPeriodic() {
        if (window.getYPos() == 500) {
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
        return new MoveWestState(pool, window);
    }
}
