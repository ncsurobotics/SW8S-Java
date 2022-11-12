package org.aquapackrobotics.sw8s.missions;

import java.util.concurrent.ScheduledThreadPoolExecutor;

import org.aquapackrobotics.sw8s.states.State;
import org.aquapackrobotics.sw8s.states.BuoyStates.*;
import org.aquapackrobotics.sw8s.states.BuoyStates.BuoyInitState;

import java.util.concurrent.*;

public class Buoys extends Mission {

    public Buoys (ScheduledThreadPoolExecutor pool) {
       super(pool); 
    }

    @Override
    protected State initialState() {
        return new BuoyInitState(pool);
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
