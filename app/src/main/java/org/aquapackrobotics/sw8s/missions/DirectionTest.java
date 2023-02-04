package org.aquapackrobotics.sw8s.missions;

import java.util.concurrent.ExecutionException;
import org.aquapackrobotics.sw8s.comms.*;

import org.aquapackrobotics.sw8s.states.State;
import org.aquapackrobotics.sw8s.states.DirectionalStates.*;

/**
 * State machine to test directional movement. "n" to advance to next state.
 */
public class DirectionTest extends Mission {

    public DirectionTest(ControlBoardThreadManager manager) {
        super(manager);
    }

    @Override
    protected State initialState() {
        return new StationaryInitState(manager);
    }

    @Override
    protected void executeState(State state) throws ExecutionException, InterruptedException {
        while (state.onPeriodic()) {
            
        }
    }

    @Override
    protected State nextState(State state) {
        return state.nextState();
    }

}
