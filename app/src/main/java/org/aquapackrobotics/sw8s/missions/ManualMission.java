package org.aquapackrobotics.sw8s.missions;

import java.util.concurrent.ScheduledThreadPoolExecutor;

import org.aquapackrobotics.sw8s.states.State;

import java.util.concurrent.*;

/**
 * Competition mission, fully autonomous.
 */
public class ManualMission extends Mission {

    public ManualMission(ScheduledThreadPoolExecutor pool) {
        super(pool);
    }

    // TODO: implement
    @Override
    protected State initialState() {
        return null;
    }

    // TODO: implement
    @Override
    protected void executeState(State state)  throws ExecutionException, InterruptedException  {
    }

    // TODO: implement
    @Override
    protected State nextState(State state) {
        return null;
    }
}
