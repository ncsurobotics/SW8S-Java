package org.aquapackrobotics.sw8s.states.OctagonYUVStates;

import java.util.concurrent.*;
import java.io.File;
import java.lang.Math;
import java.time.Instant;

import org.opencv.videoio.VideoCapture;
import org.opencv.core.Mat;

import org.aquapackrobotics.sw8s.comms.*;
import org.aquapackrobotics.sw8s.states.State;
import org.aquapackrobotics.sw8s.vision.*;

public class OctagonYUVFollowState extends State {

    private ScheduledFuture<byte[]> depthRead;
    private final PathYUV target;
    private final File Dir;
    private double depth = -1.7;

    public OctagonYUVFollowState(CommsThreadManager manager, String missionName) {
        super(manager);
        // target = new PathYUV(new IntPair(Integer.MIN_VALUE, 127), new
        // IntPair(Integer.MAX_VALUE, Integer.MAX_VALUE), 10,
        target = new PathYUV(new IntPair(Integer.MIN_VALUE, 178), new IntPair(178, Integer.MAX_VALUE), 10,
                800, 0.25);
        Dir = new File("/mnt/data/" + missionName + "/path");
        Dir.mkdir();
    }

    public void onEnter() throws ExecutionException, InterruptedException {
        try {
            depthRead = manager.MSPeriodicRead((byte) 1);
            var mreturn = manager.setStability2Speeds(0, 0, 0, 0, manager.getYaw(), this.depth);
            while (!mreturn.isDone())
                ;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean onPeriodic() {
        Mat frame = CameraFeedSender.getFrame(0);
        try {
            VisualObject footage = target.relativePosition(frame,
                    Dir.toString() + "/" + Instant.now().toString() + ".jpeg");
            double x = (footage.horizontal_offset / Math.abs(footage.horizontal_offset)) * 0.2;
            System.out.println("X: " + String.valueOf(x));
            double y = -(footage.vertical_offset / Math.abs(footage.vertical_offset)) *
                    0.2;
            System.out.println("Y: " + String.valueOf(y));
            depth += 0.02;
            if (depth >= -0.5)
                return true;
            System.out.println("Depth: " + String.valueOf(depth));
            var mreturn = manager.setStability2Speeds(x, y, 0, 0, manager.getYaw(), depth);
            while (!mreturn.isDone())
                ;
        } catch (Exception e) {
        }
        return false;
    }

    public void onExit() throws ExecutionException, InterruptedException {
    }

    public State nextState() {
        return null;
    }
}
