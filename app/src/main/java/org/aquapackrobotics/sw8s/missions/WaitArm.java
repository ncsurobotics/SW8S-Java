package org.aquapackrobotics.sw8s.missions;

import java.util.concurrent.ExecutionException;

import org.aquapackrobotics.sw8s.comms.CameraFeedSender;
import org.aquapackrobotics.sw8s.comms.CommsThreadManager;
import org.aquapackrobotics.sw8s.states.State;

/**
 * Mission for navigating gates
 */
public class WaitArm extends Mission {
    public WaitArm(CommsThreadManager manager, String missionName) {
        super(manager);
        Runnable armSignalWait = new Runnable() {
            @Override
            public void run() {
                while (!manager.getArm()) {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        };
        CameraFeedSender.openCapture(0, missionName);
        CameraFeedSender.openCapture(1, missionName);
        try {
            manager.scheduleRunnable(armSignalWait).get(); // .get() blocks until complete
        } catch (Exception e) {
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
