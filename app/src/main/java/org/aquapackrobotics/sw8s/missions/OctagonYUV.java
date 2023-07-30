package org.aquapackrobotics.sw8s.missions;

import java.util.concurrent.ExecutionException;

import org.aquapackrobotics.sw8s.comms.CameraFeedSender;
import org.aquapackrobotics.sw8s.comms.CommsThreadManager;
import org.aquapackrobotics.sw8s.states.State;
import org.aquapackrobotics.sw8s.states.OctagonYUVStates.OctagonYUVSubmergeState;

/**
 * Mission for navigating gates
 */
public class OctagonYUV extends Mission {
    private String missionName;

    public OctagonYUV(CommsThreadManager manager, String missionName) {
        super(manager);
        CameraFeedSender.openCapture(0);
        this.missionName = missionName;
    }

    @Override
    protected State initialState() {
        return new OctagonYUVSubmergeState(manager, missionName);
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
