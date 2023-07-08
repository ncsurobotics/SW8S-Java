package org.aquapackrobotics.sw8s.states.OctagonStates;

import java.util.concurrent.*;
import java.io.File;
import java.lang.Math;
import java.time.Instant;

import org.opencv.videoio.VideoCapture;
import org.opencv.core.Mat;

import org.aquapackrobotics.sw8s.comms.*;
import org.aquapackrobotics.sw8s.states.State;
import org.aquapackrobotics.sw8s.vision.*;

public class OctagonFollowState extends State {

    private ScheduledFuture<byte[]> depthRead;
    private final Path target;
    private final File Dir;
    private double depth = -1.7;

    public OctagonFollowState(ControlBoardThreadManager manager, String missionName) {
        super(manager);
        // target = new Path(70, 230, 30, 100, 0.15);
        target = new Path(70, 230, 30, 200, 0.2);
        Dir = new File("/mnt/data/" + missionName + "/path");
        Dir.mkdir();
    }

    public void onEnter() throws ExecutionException, InterruptedException {
        try {
            depthRead = manager.MSPeriodicRead((byte) 1);
            var mreturn = manager.setStability2Speeds(0, 0, 0, 0, manager.getYaw(), -1.0);
            while (!mreturn.isDone())
                ;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean onPeriodic() {
        Mat frame = CameraFeedSender.getFrame(1);
        try {
            VisualObject footage = target.relativePosition(frame,
                    Dir.toString() + "/" + Instant.now().toString() + ".jpeg");
            double x = (footage.horizontal_offset / Math.abs(footage.horizontal_offset)) * 0.2;
            System.out.println("X: " + String.valueOf(x));
            double y = -(footage.vertical_offset / Math.abs(footage.vertical_offset)) *
                    0.2;
            System.out.println("Y: " + String.valueOf(y));
            // depth += 0.05;
            // if (depth >= -0.3)
            // return true;
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
