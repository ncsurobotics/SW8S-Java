package org.aquapackrobotics.sw8s.missions;

import org.aquapackrobotics.sw8s.comms.*;

import org.aquapackrobotics.sw8s.states.State;
import org.aquapackrobotics.sw8s.states.BuoyStates.*;
import org.aquapackrobotics.sw8s.states.BuoyStates.BuoyInitState;

import org.opencv.videoio.VideoCapture;

import java.util.concurrent.*;

public class Buoys extends Mission {
    String missionName;

    public Buoys(CommsThreadManager manager, String missionName) {
        super(manager);
        this.missionName = missionName;
    }

    @Override
    protected State initialState() {
        return new BuoyInitState(manager, missionName);
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
