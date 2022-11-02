package org.aquapackrobotics.sw8s.missions;

import java.util.concurrent.ScheduledThreadPoolExecutor;

import org.aquapackrobotics.sw8s.states.State;

import java.util.concurrent.*;

public class Buoys extends Mission {
    public Buoys (ScheduledThreadPoolExecutor pool) {
       super(pool); 
    }

    @Override
    protected initialState() {
        return new GateInitState(pool);
    }

    @Override
    protected void executeState(State state)  throws ExecutionException, InterruptedException  {
        while (state.onPeriodic()) {}
    }

    @Override
    protected State nextState(State state) {
        return state.nextState();
    }
}
