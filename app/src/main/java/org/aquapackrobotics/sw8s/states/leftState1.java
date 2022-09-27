package org.aquapackrobotics.sw8s.states;

import java.util.concurrent.ScheduledThreadPoolExecutor;

import org.aquapackrobotics.sw8s.trainingsim.SimWindow;

public class leftState1 extends SimState{
    public leftState1(ScheduledThreadPoolExecutor pool, SimWindow window){
        super(pool, window);
    }

    @Override
    public boolean onPeriodic() {
        window.setRobotSpeed(-1, 0, 0);
        if (window.getXPos() >= 100) {
            window.setRobotSpeed(-1, 0, 0);
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
       return new downState(pool, window);
    }   
}
