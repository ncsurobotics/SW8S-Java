package org.aquapackrobotics.sw8s.states.StabilityGateStates;

import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledFuture;

import org.aquapackrobotics.sw8s.comms.CommsThreadManager;
import org.aquapackrobotics.sw8s.states.State;

public class StabilityGateSubmergeState extends State {

    ScheduledFuture<byte[]> depthRead;
    double yaw;

    public StabilityGateSubmergeState(CommsThreadManager manager) {
        super(manager);
    }

    public void onEnter() throws ExecutionException, InterruptedException {
        try {
            depthRead = manager.MSPeriodicRead((byte) 1);
            yaw = manager.getYaw();
            // var mreturn = manager.setStability1Speeds(0, 0, 0, 0, 0, -1.5);
            var mreturn = manager.setStability2Speeds(0, 0, 0, 0, yaw, -2.1);
            while (!mreturn.isDone())
                ;
            System.out.println("DONE");
            System.out.println(Arrays.toString(mreturn.get()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean onPeriodic() {
        try {
            if (depthRead.isDone()) {
                if (manager.getDepth() < -1.8) {
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
        // manager.setGlobalSpeeds(0, 0, 0, 0, 0, 0);
    }

    public State nextState() {
        return new StabilityGateHoldState(manager, yaw);
    }
}
