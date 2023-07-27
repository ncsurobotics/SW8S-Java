package org.aquapackrobotics.sw8s.missions;

import org.aquapackrobotics.sw8s.comms.*;

import org.aquapackrobotics.sw8s.states.State;

import java.util.concurrent.*;

/**
 * Competition mission, fully autonomous.
 */
public class AutoMission extends Mission {

    public AutoMission(CommsThreadManager manager) {
        super(manager);
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
