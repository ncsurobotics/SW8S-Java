package org.aquapackrobotics.sw8s.states;

import java.util.concurrent.ScheduledThreadPoolExecutor;

import org.aquapackrobotics.sw8s.trainingsim.SimWindow;

public class upState2 extends SimState{
    public upState2(ScheduledThreadPoolExecutor pool, SimWindow window){
        super(pool, window);
    }

    @Override
    public boolean onPeriodic() {
        window.setRobotSpeed(0, -1, 0);
        if (window.getYPos() >= 350) {
            window.setRobotSpeed(0, -1, 0);
            return false;
        }

        window.setRobotSpeed(0, 0, 0);
        return true;
    }

    @Override
    public void onExit() {
        nextState();  
    }

    @Override
    public void onEnter() {
        window.setRobotSpeed(0, 0, 0);
    }

    @Override
    public State nextState() {
       return new leftState2(pool, window);
    }
}
