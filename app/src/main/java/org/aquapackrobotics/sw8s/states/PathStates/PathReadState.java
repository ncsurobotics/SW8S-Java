package org.aquapackrobotics.sw8s.states.PathStates;

import java.util.concurrent.*;
import java.time.Instant;

import org.opencv.videoio.VideoCapture;
import org.opencv.core.Mat;

import org.aquapackrobotics.sw8s.comms.*;
import org.aquapackrobotics.sw8s.states.State;
import org.aquapackrobotics.sw8s.states.State;
import org.aquapackrobotics.sw8s.vision.Path;

import org.opencv.imgcodecs.Imgcodecs;

public class PathReadState extends State {

    private ScheduledFuture<byte[]> depthRead;
    private final VideoCapture cap;
    private final Path target;

    public PathReadState(ControlBoardThreadManager manager, VideoCapture cap) {
        super(manager);
        this.cap = cap;
        target = new Path();
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
        Mat frame = new Mat();
        if ( cap.read(frame) ) {
            Imgcodecs.imwrite("/tmp/data/" + Instant.now().toString() + ".jpeg", target.processFrame(frame));
        }
        return true;
    }

    public void onExit() throws ExecutionException, InterruptedException {}

    public State nextState() {
        return null;
    }
}
