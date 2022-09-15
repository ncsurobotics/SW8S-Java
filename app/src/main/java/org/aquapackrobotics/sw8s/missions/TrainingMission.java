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

    @Override
    protected State initialState() {
        return new ForwardYState(pool, sim, -400);
    }

    @Override
    protected void executeState(State state) {
    	state.onEnter();
    	while(state.onPeriodic()) {}
    	state.onExit();
    }

    @Override
    protected State nextState(State state) {
        return state.nextState();
    }
}
