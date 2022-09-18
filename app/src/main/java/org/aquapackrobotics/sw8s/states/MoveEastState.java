package org.aquapackrobotics.sw8s.states;

import org.aquapackrobotics.sw8s.states.*;
import org.aquapackrobotics.sw8s.trainingsim.SimWindow;
import java.util.concurrent.ScheduledThreadPoolExecutor;

public class MoveEastState extends SimState {
    public MoveEastState(ScheduledThreadPoolExecutor pool, SimWindow window) {
        super(pool, window);
    }
 
    // TODO: implement
    public void onEnter() {
    }

    // TODO: implement
    public boolean onPeriodic() {
        window.setRobotSpeed(-5, 0, 0);
        if (window.getXPos() <= 50) {
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
        return new MoveSouthState(pool, window);
    }
}
