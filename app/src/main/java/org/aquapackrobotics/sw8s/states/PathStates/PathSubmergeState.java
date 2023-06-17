package org.aquapackrobotics.sw8s.states.PathStates;

import java.util.concurrent.*;

import org.opencv.videoio.VideoCapture;

import org.aquapackrobotics.sw8s.comms.*;
import org.aquapackrobotics.sw8s.states.State;
import org.aquapackrobotics.sw8s.states.PathStates.*;

public class PathSubmergeState extends State {

    private ScheduledFuture<byte[]> depthRead;
    private final VideoCapture cap;

    public PathSubmergeState(ControlBoardThreadManager manager, VideoCapture cap) {
        super(manager);
        this.cap = cap;
    }

    public void onEnter() throws ExecutionException, InterruptedException {
        try {
            depthRead = manager.MSPeriodicRead((byte)1);
            var mreturn = manager.setStability2Speeds(0, 0, 0, 0, manager.getYaw(), -2.1);
            while (! mreturn.isDone());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean onPeriodic() {
        try {
            if ( depthRead.isDone() ) {
                if ( manager.getDepth() < -1.8 ) {
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
        //return new PathReadState(manager, cap);
        return new PathFollowState(manager, cap);
    }
}
