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

    private final Octagon target;
    private final File Dir;
    private double depth;
    private double combinedAngle;

    public OctagonYUVFollowState(CommsThreadManager manager, String missionName, double MISSION_DEPTH) {
        super(manager);
        target = new Octagon();
        Dir = new File("/mnt/data/" + missionName + "/path");
        Dir.mkdir();
        this.depth = MISSION_DEPTH;
        this.combinedAngle = manager.getYaw();
    }

    public void onEnter() throws ExecutionException, InterruptedException {
        try {
            var mreturn = manager.setStability2Speeds(0, 0, 0, 0, manager.getYaw(), this.depth);
            while (!mreturn.isDone())
                ;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean onPeriodic() {
        Mat frame = CameraFeedSender.getFrame(Camera.BOTTOM);
        try {
            /*
             * VisualObject footage = target.relativePosition(frame,
             * Dir.toString() + "/" + Instant.now().toString() + ".jpeg");
             * double x = (footage.horizontal_offset / Math.abs(footage.horizontal_offset))
             * * 0.2;
             * System.out.println("X: " + String.valueOf(x));
             * double y = -(footage.vertical_offset / Math.abs(footage.vertical_offset)) *
             * 0.2;
             * System.out.println("Y: " + String.valueOf(y));
             * depth += 0.02;
             * if (depth >= -0.5)
             * return true;
             * System.out.println("Depth: " + String.valueOf(depth));
             * var mreturn = manager.setStability2Speeds(x, y, 0, 0, manager.getYaw(),
             * depth);
             * while (!mreturn.isDone())
             */
            VisualObject footage = target.relativePosition(frame,
                    Dir.toString() + "/" + Instant.now().toString());
            double x = -(footage.horizontal_offset / Math.abs(footage.horizontal_offset)) * 0.2;
            System.out.println("Horizontal Offset: " + String.valueOf(footage.horizontal_offset));
            System.out.println("X: " + String.valueOf(x));
            double y = -(footage.vertical_offset / Math.abs(footage.vertical_offset)) *
                    0.2;
            System.out.println("Vertical Offset: " + String.valueOf(footage.vertical_offset));
            System.out.println("Y: " + String.valueOf(y));
            double angle = Math.toDegrees(footage.angle);
            // angle = angle > 360 ? angle % 360 : angle;
            System.out.println("Angle: " + String.valueOf(angle));
            System.out.println("System Angle: " + String.valueOf(manager.getYaw()));
            // double combinedAngle = (manager.getYaw() + angle) % 360;
            combinedAngle = manager.getYaw();
            if (angle > 10.0)
                combinedAngle -= 5.0;
            else if (angle < -10)
                combinedAngle += 5.0;
            System.out.println("Combined Angle: " + String.valueOf(combinedAngle));

            if (Math.abs(footage.horizontal_offset) < 20
                    && Math.abs(footage.vertical_offset) < 20) {
                depth += 0.05;
            }

            var mreturn = manager.setStability2Speeds(x, y, 0, 0,
                    combinedAngle,
                    depth);
            while (!mreturn.isDone())
                ;
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
