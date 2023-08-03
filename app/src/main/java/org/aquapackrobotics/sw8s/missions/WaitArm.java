package org.aquapackrobotics.sw8s.missions;

import java.util.concurrent.ExecutionException;

import org.aquapackrobotics.sw8s.comms.Camera;
import org.aquapackrobotics.sw8s.comms.CameraFeedSender;
import org.aquapackrobotics.sw8s.comms.CommsThreadManager;
import org.aquapackrobotics.sw8s.states.State;

/**
 * Mission for navigating gates
 */
public class WaitArm extends Mission {
    public WaitArm(CommsThreadManager manager, String missionName) {
        super(manager);
        CameraFeedSender.openCapture(Camera.BOTTOM, missionName);
        CameraFeedSender.openCapture(Camera.FRONT, missionName);
        try {
            while (!manager.getArm())
                ;
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected State initialState() {
        return null;
    }

    @Override
    protected void executeState(State state) throws ExecutionException, InterruptedException {
    }

    @Override
    protected State nextState(State state) {
        return null;
    }
}
