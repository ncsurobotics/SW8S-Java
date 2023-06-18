package org.aquapackrobotics.sw8s.missions;

import java.util.concurrent.*;
import java.util.Arrays;

import org.opencv.videoio.VideoCapture;

import org.aquapackrobotics.sw8s.comms.*;
import org.aquapackrobotics.sw8s.states.State;
import org.aquapackrobotics.sw8s.states.PathStates.*;

/**
 * Mission for navigating gates
 */
public class Path extends Mission {
    private final VideoCapture cap;

    public Path(ControlBoardThreadManager manager) {
        super(manager);
        cap = CameraFeedSender.openCapture();
    }

    @Override
    protected State initialState() {
        return new PathSubmergeState(manager, cap);
    }

    @Override
    protected void executeState(State state) throws ExecutionException, InterruptedException  {
        while (! state.onPeriodic()) {
            System.out.println("State: " + state.getClass().getName());
        }
    }

    @Override
    protected State nextState(State state) {
        return state.nextState();
    }
}