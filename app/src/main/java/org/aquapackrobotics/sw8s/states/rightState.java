package org.aquapackrobotics.sw8s.states;

import java.util.concurrent.ScheduledThreadPoolExecutor;

import org.aquapackrobotics.sw8s.trainingsim.SimWindow;

public class rightState extends SimState{
    public rightState(ScheduledThreadPoolExecutor pool, SimWindow window){
        super(pool, window);
    }

    @Override
    public boolean onPeriodic() {
        window.setRobotSpeed(1, 0, 0);
        if (window.getXPos() <= 400) {
            window.setRobotSpeed(1, 0, 0);
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
       return new upState2(pool, window);
    }
}
