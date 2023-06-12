package org.aquapackrobotics.sw8s.missions;

import org.aquapackrobotics.sw8s.comms.*;

import org.aquapackrobotics.sw8s.states.State;
import org.aquapackrobotics.sw8s.states.BuoyStates.*;
import org.aquapackrobotics.sw8s.states.BuoyStates.BuoyInitState;

import org.opencv.videoio.VideoCapture;

import java.util.concurrent.*;

public class Buoys extends Mission {
    private final VideoCapture cap;

    public Buoys (ControlBoardThreadManager manager) {
       super(manager); 
        cap = CameraFeedSender.openCapture();
    }

    @Override
    protected State initialState() {
        return new BuoyInitState(manager, cap);
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
