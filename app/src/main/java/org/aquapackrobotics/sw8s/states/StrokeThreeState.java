package org.aquapackrobotics.sw8s.states;

import java.util.concurrent.ScheduledThreadPoolExecutor;

import org.aquapackrobotics.sw8s.trainingsim.SimWindow;

public class StrokeThreeState extends SimState {

    private SimWindow sim;
    public StrokeThreeState(ScheduledThreadPoolExecutor pool, SimWindow sim) {
        super(pool, sim);
        this.sim = sim;
    }

    @Override
    public void onEnter() {
        
    }

    @Override
    public boolean onPeriodic() {
        sim.setRobotSpeed(-1, 0, 0);
        System.out.println(sim.getXPos());
        if (sim.getXPos() < 100) {
            sim.setRobotSpeed(0, 0, 0);
            return true;
        }
        return false;
    }

    @Override
    public void onExit() {
        
    }

    @Override
    public State nextState() {
        return new StrokeFourState(pool, sim);
    }
    
}
