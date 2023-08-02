package org.aquapackrobotics.sw8s.states.OctagonYUVStates;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledFuture;

import org.aquapackrobotics.sw8s.comms.CommsThreadManager;
import org.aquapackrobotics.sw8s.states.State;

public class OctagonYUVSubmergeState extends State {

    private ScheduledFuture<byte[]> depthRead;
    private String missionName;
    private double targetYaw;

    public OctagonYUVSubmergeState(CommsThreadManager manager, String missionName, double targetYaw) {
        super(manager);
        this.missionName = missionName;
        this.targetYaw = targetYaw;
    }

    public void onEnter() throws ExecutionException, InterruptedException {
        try {
            depthRead = manager.MSPeriodicRead((byte) 1);
            var mreturn = manager.setStability2Speeds(0, 0, 0, 0, targetYaw,
                    -1.5);
            while (!mreturn.isDone())
                ;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean onPeriodic() {
        try {
            if (depthRead.isDone()) {
                if (manager.getDepth() < -0.8) {
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
        return new OctagonYUVForwardState(manager, missionName, targetYaw);
    }
}
