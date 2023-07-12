package org.aquapackrobotics.sw8s.states.PathYUVStates;

import java.util.concurrent.*;
import java.io.File;
import java.lang.Math;
import java.time.Instant;

import org.opencv.videoio.VideoCapture;
import org.opencv.core.Mat;

import org.aquapackrobotics.sw8s.comms.*;
import org.aquapackrobotics.sw8s.states.State;
import org.aquapackrobotics.sw8s.vision.*;

public class PathYUVDetectState extends State {

    private ScheduledFuture<byte[]> depthRead;
    private PathYUV target;
    private final File Dir;

    private double[] PathYUVOpts = { 0.5, 0.4, 0.35, 0.3, 0.2 };
    private int PathYUVidx = 0;
    private String missionName;
    private double initialYaw;

    public PathYUVDetectState(ControlBoardThreadManager manager, String missionName, double initialYaw) {
        super(manager);
        this.PathYUVidx = 0;
        target = new PathYUV(this.PathYUVOpts[this.PathYUVidx]);
        Dir = new File("/mnt/data/" + missionName + "/pathYUV");
        Dir.mkdir();
        this.missionName = missionName;
        this.initialYaw = initialYaw;
    }

    public void onEnter() throws ExecutionException, InterruptedException {
        try {
            depthRead = manager.MSPeriodicRead((byte) 1);
            var mreturn = manager.setStability2Speeds(0, 0.4, 20, 0, initialYaw, -2.0);
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
                    Dir.toString() + "/" + Instant.now().toString());

            double x = (footage.horizontal_offset / Math.abs(footage.horizontal_offset)) * 0.2;
            System.out.println("X: " + String.valueOf(x));

            double angle = Math.toDegrees(footage.angle);
            double combined_angle = manager.getYaw();
            if (angle > 10.0)
                combined_angle -= 5.0;
            else if (angle < -10)
                combined_angle += 5.0;
            System.out.println("Combined Angle: " + String.valueOf(combined_angle));
            var mreturn = manager.setStability2Speeds(x, 0.2, 20, 0, combined_angle,
                    -2.0);
            System.out.println("Decimation level: " + String.valueOf(this.PathYUVOpts[this.PathYUVidx]));
            System.out.println("DETECT");

            this.target = new PathYUV(this.PathYUVOpts[this.PathYUVidx++]);
            while (!mreturn.isDone())
                ;
            if (this.PathYUVidx == this.PathYUVOpts.length)
                return true;
        } catch (Exception e) {
            this.PathYUVidx = 0;
            System.out.println("NO DETECT");
            try {
                var mreturn = manager.setStability2Speeds(0, 0.4, 20, 0, manager.getYaw(), -2.0);
                while (!mreturn.isDone())
                    ;
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return false;
    }

    public void onExit() throws ExecutionException, InterruptedException {
        System.out.println("EXIT DETECT STATE");
    }

    public State nextState() {
        return new PathYUVThroughState(manager, missionName);
    }
}