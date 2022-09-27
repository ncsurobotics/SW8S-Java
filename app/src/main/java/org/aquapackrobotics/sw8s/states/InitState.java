package org.aquapackrobotics.sw8s.states;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import org.aquapackrobotics.sw8s.trainingsim.SimWindow;

public class InitState extends SimState {
    public InitState(ScheduledThreadPoolExecutor pool, SimWindow window) {
        super(pool, window);
    }

    // TODO: implement
    public void onEnter() {
        window.setRobotSpeed(0, 0, 0);
    }

    // TODO: implement
    public boolean onPeriodic() {
        window.setRobotSpeed(.4, -.8, .05);
        if (window.getYPos() < 180){
            return true;
        }
        return false;
    }

    // TODO: implement
    public void onExit() {
        window.setRobotSpeed(0, 0, 0);
    }

    // TODO: implement
    public State nextState() {
        return new State1(pool, window);
    }
}
