package org.aquapackrobotics.sw8s.missions;

import java.util.concurrent.*;
import java.util.Arrays;

import org.opencv.videoio.VideoCapture;

import org.aquapackrobotics.sw8s.comms.*;
import org.aquapackrobotics.sw8s.states.State;
import org.aquapackrobotics.sw8s.states.OctagonStates.*;

/**
 * Mission for navigating gates
 */
public class Octagon extends Mission {
    private String missionName;

    public Octagon(ControlBoardThreadManager manager, String missionName) {
        super(manager);
        CameraFeedSender.openCapture(0);
        this.missionName = missionName;
    }

    @Override
    protected State initialState() {
        return new OctagonSubmergeState(manager, missionName);
    }

    @Override
    protected void executeState(State state) throws ExecutionException, InterruptedException {
        while (!state.onPeriodic()) {
            System.out.println("State: " + state.getClass().getName());
        }
    }

    @Override
    protected State nextState(State state) {
        return state.nextState();
    }
}
