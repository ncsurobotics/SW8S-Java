package org.aquapackrobotics.sw8s.missions;

import org.aquapackrobotics.sw8s.comms.*;
import org.aquapackrobotics.sw8s.states.*;

import java.util.Arrays;
import java.util.concurrent.*;

/**
 * State machine for testing motors
 */
public class DropperTest extends Mission {

    public DropperTest(ControlBoardThreadManager manager) {
        super(manager);
    }

    @Override
    protected State initialState() {
        try {
            manager.resetMSB();
            System.out.println("Resetting MSB");
            Thread.sleep(1000);
            for (var handle : manager.fireDroppers()) {
                while (!handle.isDone())
                    ;
                System.out.println("MEB Response for Dropper: " + Arrays.toString(handle.get()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void executeState(State state) throws ExecutionException, InterruptedException {
        while (!state.onPeriodic()) {
        }
    }

    @Override
    protected State nextState(State state) {
        return state.nextState();
    }
}
