package org.aquapackrobotics.sw8s.states;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import org.aquapackrobotics.sw8s.trainingsim.SimWindow;

public class State12 extends SimState {
    public State12(ScheduledThreadPoolExecutor pool, SimWindow window) {
        super(pool, window);
        //TODO Auto-generated constructor stub
    }

    // TODO: implement
    public void onEnter() {
        window.setRobotSpeed(0, 0, 0);
    }

    // TODO: implement
    public boolean onPeriodic() {
        window.setRobotSpeed(0, 0, 1);
        if (window.getRobotAngle() > -540){
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
        return new State13(pool, window);
    }
}
