package org.aquapackrobotics.sw8s.missions;

import java.util.concurrent.ExecutionException;

import org.aquapackrobotics.sw8s.comms.CommsThreadManager;
import org.aquapackrobotics.sw8s.states.State;
import org.aquapackrobotics.sw8s.states.SubmergeTestStates.SubmergeTestInitState;

/**
 * State machine for testing submerging
 */
public class SubmergeTest extends Mission {

    public SubmergeTest(CommsThreadManager manager) {
        super(manager);

    }

    @Override
    protected State initialState() {
        return new SubmergeTestInitState(manager);
    }

    @Override
    protected void executeState(State state) throws ExecutionException, InterruptedException {
        state.onEnter();
        while (state.onPeriodic()) {

        }
        state.onExit();
    }

    @Override
    protected State nextState(State state) {
        return state.nextState();
    }
}
