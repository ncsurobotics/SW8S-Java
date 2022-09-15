package org.aquapackrobotics.sw8s.missions;

import java.util.concurrent.ScheduledThreadPoolExecutor;

import org.aquapackrobotics.sw8s.states.RightLineState;
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
        return new RightLineState(pool, sim);
    }

    @Override
    protected void executeState(State state) {
        state.onPeriodic();
    }

    // TODO: implement
    @Override
    protected State nextState(State state) {
        return null;
    }
}
