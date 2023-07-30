package org.aquapackrobotics.sw8s.missions;

import java.util.concurrent.ExecutionException;

import org.aquapackrobotics.sw8s.comms.CommsThreadManager;
import org.aquapackrobotics.sw8s.states.State;
import org.aquapackrobotics.sw8s.states.ReceiveTestStates.Depth;

public class ReceiveTest extends Mission {
    public ReceiveTest(CommsThreadManager manager) {
        super(manager);
    }

    // TODO: implement
    @Override
    protected State initialState() {
        return new Depth(manager);
    }

    // TODO: implement
    @Override
    protected void executeState(State state) throws ExecutionException, InterruptedException {
        while (!state.onPeriodic()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        ;
    }

    // TODO: implement
    @Override
    protected State nextState(State state) {
        return state.nextState();
    }
}
