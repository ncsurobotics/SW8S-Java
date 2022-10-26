package org.aquapackrobotics.sw8s.missions;

import java.util.concurrent.ScheduledThreadPoolExecutor;

import org.aquapackrobotics.sw8s.states.State;

import java.util.concurrent.*;

/**
 * Mission for navigating gates
 */
public class Gate extends Mission {

    public Gate(ScheduledThreadPoolExecutor pool) {
        super(pool);
    }

    // TODO: implement
    @Override
    protected State initialState() {
        return null;
    }

    // TODO: implement
    @Override
    protected void executeState(State state) throws ExecutionException, InterruptedException  {
    }

    // TODO: implement
    @Override
    protected State nextState(State state) {
        return null;
    }
}
