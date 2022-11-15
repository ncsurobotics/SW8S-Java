package org.aquapackrobotics.sw8s.missions;

import java.util.concurrent.ScheduledThreadPoolExecutor;

import org.aquapackrobotics.sw8s.states.State;
import org.aquapackrobotics.sw8s.states.BuoyStates.*;
import java.util.concurrent.*;

public class Bins extends Mission {

    public Bins (ScheduledThreadPoolExecutor pool) {
       super(pool); 
    }

    @Override
    protected State initialState() {
        return new BinsInitState(pool);
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
