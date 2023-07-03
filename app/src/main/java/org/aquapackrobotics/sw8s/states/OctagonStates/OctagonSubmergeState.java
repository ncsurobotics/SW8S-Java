package org.aquapackrobotics.sw8s.states.OctagonStates;

import java.util.concurrent.*;

import org.opencv.videoio.VideoCapture;

import org.aquapackrobotics.sw8s.comms.*;
import org.aquapackrobotics.sw8s.states.State;
import org.aquapackrobotics.sw8s.states.OctagonStates.*;

public class OctagonSubmergeState extends State {

    private ScheduledFuture<byte[]> depthRead;

    public OctagonSubmergeState(ControlBoardThreadManager manager) {
        super(manager);
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
        return new OctagonFollowState(manager);
    }
}
