package org.aquapackrobotics.sw8s.states.PathStates;

import java.util.concurrent.*;
import java.io.File;
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
    private final File Dir;

    public PathReadState(ControlBoardThreadManager manager, VideoCapture cap) {
        super(manager);
        this.cap = cap;
        target = new Path();
        Dir = new File(new File(System.getProperty("java.io.tmpdir")), "path");
        Dir.mkdir();
    }

    public void onEnter() throws ExecutionException, InterruptedException {
        /*
         * try {
         * depthRead = manager.MSPeriodicRead((byte) 1);
         * var mreturn = manager.setStability2Speeds(0, 0, 0, 0, manager.getYaw(),
         * -2.1);
         * while (!mreturn.isDone())
         * ;
         * } catch (Exception e) {
         * e.printStackTrace();
         * }
         */
    }

    public boolean onPeriodic() {
        Mat frame = new Mat();
        if (cap.read(frame)) {
            target.processFrame(frame, Dir.toString() + Instant.now().toString() + ".jpeg");
            try {
                System.out.println(target.relativePosition(frame));
                System.out.println("Updated");
            } catch (Exception e) {
            }
        }
        return false;
    }

    public void onExit() throws ExecutionException, InterruptedException {
    }

    public State nextState() {
        return null;
    }
}
