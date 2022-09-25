package org.aquapackrobotics.sw8s.missions;

import java.util.concurrent.ScheduledThreadPoolExecutor;

import org.aquapackrobotics.sw8s.states.State;

/**
 * Competition mission, fully autonomous.
 */
public class AutoMission extends Mission {

    public AutoMission(ScheduledThreadPoolExecutor pool) {
        super(pool);
    }

    @Override
    protected State initialState() {
        return null;
    }

    @Override
    protected void executeState(State state) {
    }

    @Override
    protected State nextState(State state) {
        return null;
    }
}
