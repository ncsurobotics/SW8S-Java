package org.aquapackrobotics.sw8s.states.BuoyStates;

import org.aquapackrobotics.sw8s.comms.*;
import org.aquapackrobotics.sw8s.states.*;
import java.util.concurrent.*;

import org.opencv.videoio.VideoCapture;

public class BuoyInitState extends State {

    private ScheduledFuture<byte[]> depthRead;
    private final VideoCapture cap;

    public BuoyInitState(ControlBoardThreadManager manager, VideoCapture cap) {
        super(manager);
        this.cap = cap;
    }

    public void onEnter() throws ExecutionException, InterruptedException {
        try {
            depthRead = manager.MSPeriodicRead((byte)1);
            var mreturn = manager.setStability2Speeds(0, 0, 0, 0, manager.getYaw(), -1.5);
            while (! mreturn.isDone());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }


    public boolean onPeriodic() {
        try {
            if ( depthRead.isDone() ) {
                if ( manager.getDepth() < -1.4 ) {
                    return true;
                }
            }

            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void onExit() throws ExecutionException, InterruptedException{
    }

    public State nextState() {
        return new BuoyReadState(manager, cap);
    }
}
