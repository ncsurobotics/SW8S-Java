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
        window.setRobotSpeed(0, 0, 0);
    }

    // TODO: implement
    public boolean onPeriodic() {
        window.setRobotSpeed(0, -1, 0);
        if (window.getXPos() > 521 && window.getYPos() < 180){
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
        return new State3(pool, window);
    }
}
