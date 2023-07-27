package org.aquapackrobotics.sw8s.states.OctagonYUVStates;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledFuture;

import org.aquapackrobotics.sw8s.comms.CommsThreadManager;
import org.aquapackrobotics.sw8s.states.State;

public class OctagonYUVSubmergeState extends State {

    private ScheduledFuture<byte[]> depthRead;
    private String missionName;

    public OctagonYUVSubmergeState(CommsThreadManager manager, String missionName) {
        super(manager);
        this.missionName = missionName;
    }

    public void onEnter() throws ExecutionException, InterruptedException {
        try {
            depthRead = manager.MSPeriodicRead((byte) 1);
            var mreturn = manager.setStability2Speeds(0, 0, 0, 0, manager.getYaw(),
                    -1.7);
            while (!mreturn.isDone())
                ;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean onPeriodic() {
        try {
            if (depthRead.isDone()) {
                if (manager.getDepth() < -1.2) {
                    return true;
                }
            }

            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void onExit() throws ExecutionException, InterruptedException {
    }

    public State nextState() {
        return new OctagonYUVForwardState(manager, missionName);
    }
}
