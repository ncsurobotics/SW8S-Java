package org.aquapackrobotics.sw8s.missions;

import java.util.concurrent.ScheduledThreadPoolExecutor;

import org.aquapackrobotics.sw8s.states.ForwardYState;
import org.aquapackrobotics.sw8s.states.State;
import org.aquapackrobotics.sw8s.trainingsim.SimWindow;

/**
 * Competition mission, fully autonomous.
 */
public class TrainingMission extends Mission {
     SimWindow sim;

    public TrainingMission(ScheduledThreadPoolExecutor pool) {
        super(pool);
        sim = new SimWindow();
    }
    
    /*
     * Returns an instance of ForwardYState as the first state
     * Passes a distance of -400 to move the robot up 400 pixels 
     */
    @Override
    protected State initialState() {
        return new ForwardYState(pool, sim, -400);
    }
    
    /*
     * Executes current state
     * Runs onEnter() first, then onPeriodic() while it returns true, then onExit()
     */
    @Override
    protected void executeState(State state) {
    	state.onEnter();
    	while(state.onPeriodic()) {}
    	state.onExit();
    }
    
    /**
     * Returns the next State from the current State's nextState() method
     */
    @Override
    protected State nextState(State state) {
        return state.nextState();
    }
}
