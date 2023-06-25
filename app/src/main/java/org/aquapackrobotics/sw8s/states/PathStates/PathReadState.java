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
    private final File Dir;
    private final double[] candidates = { 0.1, 0.15, 0.2, 0.25, 0.3, 0.35, 0.4, 0.45, 0.5 };
    private File[] cand_files = new File[9];

    public PathReadState(ControlBoardThreadManager manager, VideoCapture cap) {
        super(manager);
        this.cap = cap;
        Dir = new File(new File(System.getProperty("java.io.tmpdir")), "path");
        Dir.mkdir();
        for (int i = 0; i < candidates.length; i++) {
            cand_files[i] = new File(Dir, "/" + String.valueOf(i));
            cand_files[i].mkdir();
        }
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
            for (int i = 0; i < candidates.length; i++) {
                Path target = new Path(candidates[i]);
                target.processFrame(frame, cand_files[i].toString() + "/" + Instant.now().toString() + ".jpeg");
                try {
                    System.out.println(target.relativePosition(frame));
                } catch (Exception e) {
                }
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
