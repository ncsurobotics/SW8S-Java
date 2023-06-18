package org.aquapackrobotics.sw8s.missions;

import org.aquapackrobotics.sw8s.comms.*;

import org.aquapackrobotics.sw8s.states.State;
import org.aquapackrobotics.sw8s.states.StabilityGateStates.*;

import java.util.concurrent.*;
import java.util.Arrays;

/**
 * Mission for navigating gates
 */
public class StabilityGate extends Mission {

    public StabilityGate(ControlBoardThreadManager manager) {
        super(manager);
    }

    // TODO: implement
    @Override
    protected State initialState() {
        return new StabilityGateInitState(manager);
    }

    // TODO: implement
    @Override
    protected void executeState(State state) throws ExecutionException, InterruptedException  {
        while (! state.onPeriodic()) {
            System.out.println("State: " + state.getClass().getName());
            System.out.println("Depth: " + Double.toString(manager.getDepth()));
            System.out.println("Gyro: " + Arrays.toString(manager.getGyro()));
        }
        System.out.println("FINISH STATE");
    }

    // TODO: implement
    @Override
    protected State nextState(State state) {
        return state.nextState();
    }
}