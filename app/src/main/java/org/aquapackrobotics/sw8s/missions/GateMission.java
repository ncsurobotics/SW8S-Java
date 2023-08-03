package org.aquapackrobotics.sw8s.missions;

import java.util.concurrent.ExecutionException;

import org.aquapackrobotics.sw8s.comms.Camera;
import org.aquapackrobotics.sw8s.comms.CameraFeedSender;
import org.aquapackrobotics.sw8s.comms.CommsThreadManager;
import org.aquapackrobotics.sw8s.comms.Linux;
import org.aquapackrobotics.sw8s.states.State;
import org.aquapackrobotics.sw8s.states.GateStates.GateInitState;

/**
 * Mission for navigating gates
 */
public class GateMission extends Mission {
    private static final double MISSION_DEPTH = -1.5;
    String missionName;
    double initialYaw;

    public GateMission(CommsThreadManager manager, String missionName) {
        super(manager);
        CameraFeedSender.openCapture(Camera.FRONT, missionName);
        this.missionName = missionName;
        try {
            Linux.changeExposure(Camera.FRONT, 18);
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
        return new GateInitState(manager, missionName, initialYaw, MISSION_DEPTH);
    }

    @Override
    protected void executeState(State state) throws ExecutionException, InterruptedException {
        while (!state.onPeriodic()) {
        }
    }

    @Override
    public State nextState(State state) {
        return state.nextState();
    }
}
