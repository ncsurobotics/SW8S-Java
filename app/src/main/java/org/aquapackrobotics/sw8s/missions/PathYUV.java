package org.aquapackrobotics.sw8s.missions;

import java.util.concurrent.ExecutionException;

import org.aquapackrobotics.sw8s.comms.Camera;
import org.aquapackrobotics.sw8s.comms.CameraFeedSender;
import org.aquapackrobotics.sw8s.comms.CommsThreadManager;
import org.aquapackrobotics.sw8s.states.State;
import org.aquapackrobotics.sw8s.states.PathYUVStates.PathYUVSubmergeState;

/**
 * Mission for navigating gates
 */
public class PathYUV extends Mission {
    private static final double MISSION_DEPTH = -1.0;

    private String missionName;
    private double initialYaw;

    public PathYUV(CommsThreadManager manager, String missionName) {
        super(manager);
        CameraFeedSender.openCapture(Camera.BOTTOM, missionName);
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
        return new PathYUVSubmergeState(manager, missionName, initialYaw, MISSION_DEPTH);
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
