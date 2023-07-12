package org.aquapackrobotics.sw8s.missions;

import java.util.concurrent.*;
import java.util.Arrays;

import org.opencv.videoio.VideoCapture;

import org.aquapackrobotics.sw8s.comms.*;
import org.aquapackrobotics.sw8s.states.State;
import org.aquapackrobotics.sw8s.states.PathYUVStates.*;

/**
 * Mission for navigating gates
 */
public class PathYUV extends Mission {
    private String missionName;
    private double initialYaw;

    public PathYUV(ControlBoardThreadManager manager, String missionName) {
        super(manager);
        CameraFeedSender.openCapture(0, missionName);
        this.missionName = missionName;
        try {
            var mreturn = manager.BNO055PeriodicRead((byte) 1);
            while (!mreturn.isDone())
                ;
            Thread.sleep(500); // Give sensor time to get itself ready
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.initialYaw = manager.getYaw();
    }

    @Override
    protected State initialState() {
        return new PathYUVSubmergeState(manager, missionName, initialYaw);
    }

    @Override
    protected void executeState(State state) throws ExecutionException, InterruptedException {
        while (!state.onPeriodic()) {
            // System.out.println("State: " + state.getClass().getName());
        }
    }

    @Override
    protected State nextState(State state) {
        return state.nextState();
    }
}
