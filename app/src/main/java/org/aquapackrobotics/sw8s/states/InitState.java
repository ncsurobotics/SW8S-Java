package org.aquapackrobotics.sw8s.states;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import org.aquapackrobotics.sw8s.trainingsim.SimWindow;

public class InitState extends SimState {
    public InitState(ScheduledThreadPoolExecutor pool, SimWindow window) {
        super(pool, window);
    }

    // TODO: implement
    public void onEnter() {
        window.setRobotSpeed(1, 1, 0);
    }

    // TODO: implement
    public boolean onPeriodic() {
        if (window.getXPos() == 4 && window.getYPos() == 5) {
            return true;
        }
        return false;
    }

    // TODO: implement
    public void onExit() {
        window.setRobotSpeed(1, 1, 1);
    }

    // TODO: implement
    public State nextState() {
        return new State2(pool, window);
    }
}
