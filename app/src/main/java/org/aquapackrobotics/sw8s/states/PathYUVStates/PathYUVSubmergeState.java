package org.aquapackrobotics.sw8s.states.PathYUVStates;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledFuture;

import org.aquapackrobotics.sw8s.comms.CommsThreadManager;
import org.aquapackrobotics.sw8s.states.State;

public class PathYUVSubmergeState extends State {

    private ScheduledFuture<byte[]> depthRead;
    private String missionName;
    private double initialYaw;
    private double prevTime;

    public PathYUVSubmergeState(CommsThreadManager manager, String missionName, double initialYaw) {
        super(manager);
        this.missionName = missionName;
        this.initialYaw = initialYaw;
        this.prevTime = System.currentTimeMillis();
    }

    public void onEnter() throws ExecutionException, InterruptedException {
        try {
            depthRead = manager.MSPeriodicRead((byte) 1);
            var mreturn = manager.setStability2Speeds(0, 0, 30, 0, initialYaw,
                    -1.5);
            while (!mreturn.isDone())
                ;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean onPeriodic() {
        try {
            if (System.currentTimeMillis() - this.prevTime > 100) {
                this.prevTime = System.currentTimeMillis();
                System.out.println("Depth: " + String.valueOf(manager.getDepth()));
                System.out.println("Current Angle: " + String.valueOf(manager.getYaw()));
                System.out.println("Target Angle: " + String.valueOf(initialYaw));
            }
            if (depthRead.isDone()) {
                if (manager.getDepth() < -0.4 && (Math.abs(manager.getYaw() - initialYaw) < 5)) {
                    Thread.sleep(2000); // sleep two seconds
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
        // return new PathYUVFollowState(manager, missionName);
        // return new PathYUVThroughState(manager, missionName);
        return new PathYUVDetectState(manager, missionName, initialYaw);
    }
}
