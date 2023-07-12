package org.aquapackrobotics.sw8s.states.PathYUVStates;

import java.util.concurrent.*;

import org.opencv.videoio.VideoCapture;

import org.aquapackrobotics.sw8s.comms.*;
import org.aquapackrobotics.sw8s.states.State;
import org.aquapackrobotics.sw8s.states.PathYUVStates.*;

public class PathYUVSubmergeState extends State {

    private ScheduledFuture<byte[]> depthRead;
    private String missionName;
    private double initialYaw;

    public PathYUVSubmergeState(ControlBoardThreadManager manager, String missionName, double initialYaw) {
        super(manager);
        this.missionName = missionName;
        this.initialYaw = initialYaw;
    }

    public void onEnter() throws ExecutionException, InterruptedException {
        try {
            depthRead = manager.MSPeriodicRead((byte) 1);
            var mreturn = manager.setStability2Speeds(0, 0, 0, 0, initialYaw,
                    -2.0);
            while (!mreturn.isDone())
                ;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean onPeriodic() {
        try {
            if (depthRead.isDone()) {
                System.out.println("Depth: " + String.valueOf(manager.getDepth()));
                if (manager.getDepth() < -1.5) {
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