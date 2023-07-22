package org.aquapackrobotics.sw8s.states.GatePathStates;

import java.util.concurrent.*;

import org.opencv.videoio.VideoCapture;

import org.aquapackrobotics.sw8s.comms.*;
import org.aquapackrobotics.sw8s.states.State;
import org.aquapackrobotics.sw8s.states.GatePathStates.*;

public class GatePathSubmergeState extends State {

    private ScheduledFuture<byte[]> depthRead;
    private String missionName;
    private double initialYaw;

    public GatePathSubmergeState(ControlBoardThreadManager manager, String missionName, double initialYaw) {
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
                if (manager.getDepth() < -1.5) {
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
        // return new GatePathReadState(manager, missionName);
        return new GatePathFollowState(manager, missionName, initialYaw);
    }
}
