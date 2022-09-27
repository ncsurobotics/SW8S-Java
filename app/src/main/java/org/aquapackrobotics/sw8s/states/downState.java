package org.aquapackrobotics.sw8s.states;

import java.util.concurrent.ScheduledThreadPoolExecutor;

import org.aquapackrobotics.sw8s.trainingsim.SimWindow;

public class downState extends SimState{
    public downState(ScheduledThreadPoolExecutor pool, SimWindow window){
        super(pool, window);
    }

    @Override
    public boolean onPeriodic() {
        window.setRobotSpeed(0, 1, 0);
        if (window.getYPos() <= 500) {
            window.setRobotSpeed(0, 1, 0);
            return false;
        }
    
        return true;
    }
    
    @Override
    public void onExit() {
        nextState();
        
    }
    
    @Override
    public void onEnter() {
        while (onPeriodic()==false){
            onPeriodic();
        }
        
        onExit();
    }
    
    @Override
    public State nextState() {
        return new rightState(pool, window);
    }
}
    