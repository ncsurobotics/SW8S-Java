package org.aquapackrobotics.sw8s.states.BinStates;

import java.util.concurrent.*;
import java.io.File;
import java.lang.Math;
import java.time.Instant;

import org.opencv.videoio.VideoCapture;
import org.opencv.core.Mat;

import org.aquapackrobotics.sw8s.comms.*;
import org.aquapackrobotics.sw8s.states.State;
import org.aquapackrobotics.sw8s.vision.*;

public class BinPathState extends State {

    private ScheduledFuture<byte[]> depthRead;
    private PathYUV target;
    private final File Dir;
    private String missionName;
    private double initialYaw;
    private double combinedAngle;

    private double[] PathYUVOpts = { 0.5, 0.45, 0.4, 0.35, 0.3, 0.25, 0.2, 0.15 };
    private int PathYUVidx = 0;

    public BinPathState(ControlBoardThreadManager manager, String missionName, double initialYaw) {
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
            var mreturn = manager.setStability2Speeds(0, 0.2, 30, 0, initialYaw, -1.0);
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
            var mreturn = manager.setStability2Speeds(x, y, 5 * (this.PathYUVOpts.length - this.PathYUVidx), 0,
                    combinedAngle,
                    -1.0);
            System.out.println("Decimation level: " + String.valueOf(this.PathYUVOpts[this.PathYUVidx]));
            if (this.PathYUVidx < this.PathYUVOpts.length - 1) {
                this.target = new PathYUV(this.PathYUVOpts[this.PathYUVidx++]);
            }

            System.out.println("Pre command");
            while (!mreturn.isDone())
                ;
            System.out.println("Post command");

            System.out.println("Exit conditions: " + String.valueOf(Math.abs(angle) < 20) + ", "
                    + String.valueOf(Math.abs(footage.horizontal_offset) < 20) + ", "
                    + String.valueOf(Math.abs(footage.vertical_offset) < 20));
            if (Math.abs(angle) < 20 && Math.abs(footage.horizontal_offset) < 20
                    && Math.abs(footage.vertical_offset) < 20) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public void onExit() throws ExecutionException, InterruptedException {
        System.out.println("EXITING");
        var mreturn = manager.setStability2Speeds(0, 0.2, 0, 0, combinedAngle, -1.0);
        while (!mreturn.isDone())
            ;
    }

    public State nextState() {
        return new BinTargetState(manager, missionName);
    }
}
