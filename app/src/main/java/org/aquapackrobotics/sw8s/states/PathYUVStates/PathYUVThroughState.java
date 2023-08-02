package org.aquapackrobotics.sw8s.states.PathYUVStates;

import java.io.File;
import java.time.Instant;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledFuture;

import org.aquapackrobotics.sw8s.comms.CameraFeedSender;
import org.aquapackrobotics.sw8s.comms.CommsThreadManager;
import org.aquapackrobotics.sw8s.states.State;
import org.aquapackrobotics.sw8s.vision.PathYUV;
import org.aquapackrobotics.sw8s.vision.VisualObject;
import org.opencv.core.Mat;

public class PathYUVThroughState extends State {

    private ScheduledFuture<byte[]> depthRead;
    private PathYUV target;
    private final File Dir;

    private double[] PathYUVOpts = { 0.5, 0.45, 0.4, 0.35, 0.3, 0.25, 0.2, 0.15 };
    private int PathYUVidx = 0;
    private int inAngleCount;
    private String missionName;
    private final double MISSION_DEPTH;

    public PathYUVThroughState(CommsThreadManager manager, String missionName, double MISSION_DEPTH) {
        super(manager);
        this.PathYUVidx = 0;
        target = new PathYUV(this.PathYUVOpts[this.PathYUVidx]);
        this.missionName = missionName;
        Dir = new File("/mnt/data/" + missionName + "/pathYUV");
        Dir.mkdir();
        inAngleCount = 0;
        this.MISSION_DEPTH = MISSION_DEPTH;
    }

    public void onEnter() throws ExecutionException, InterruptedException {
        try {
            depthRead = manager.MSPeriodicRead((byte) 1);
            var mreturn = manager.setStability2Speeds(0, 0, 0, 0, manager.getYaw(), MISSION_DEPTH);
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
                    Dir.toString() + "/" + Instant.now().toString());

            // double x = (footage.horizontal_offset / Math.abs(footage.horizontal_offset))
            // * 0.2;
            double x = 0;
            if (Math.abs(footage.horizontal_offset) > 0.2) {
                x = footage.horizontal_offset > 0 ? 0.2 : -0.2;
            }
            System.out.println("Horizontal Offset: " + String.valueOf(footage.horizontal_offset));
            System.out.println("X: " + String.valueOf(x));

            // double y = -(footage.vertical_offset / Math.abs(footage.vertical_offset)) *
            // 0.2;
            double y = 0;
            if (Math.abs(footage.vertical_offset) > 0.2) {
                y = footage.vertical_offset > 0 ? -0.2 : 0.2;
            }
            System.out.println("Vertical Offset: " + String.valueOf(footage.vertical_offset));
            System.out.println("Y: " + String.valueOf(y));

            double angle = Math.toDegrees(footage.angle);
            System.out.println("Angle: " + String.valueOf(angle));
            System.out.println("System Angle: " + String.valueOf(manager.getYaw()));
            double combined_angle = manager.getYaw();
            if (angle > 10.0)
                combined_angle -= 5.0;
            else if (angle < -10)
                combined_angle += 5.0;
            System.out.println("Combined Angle: " + String.valueOf(combined_angle));

            if (Math.abs(angle) < 15) {
                if (++this.inAngleCount >= 10 && Math.abs(footage.horizontal_offset) < 3)
                    return true;
                System.out.println("IN ANGLE: " + String.valueOf(this.inAngleCount));
            }

            var mreturn = manager.setStability2Speeds(x, y, 0, 0, combined_angle,
                    MISSION_DEPTH);
            System.out.println("Decimation level: " + String.valueOf(this.PathYUVOpts[this.PathYUVidx]));
            if (this.PathYUVidx < this.PathYUVOpts.length) {
                this.target = new PathYUV(this.PathYUVOpts[this.PathYUVidx++]);
            }
            while (!mreturn.isDone())
                ;
        } catch (Exception e) {
        }
        return false;
    }

    public void onExit() throws ExecutionException, InterruptedException {
        System.out.println("EXIT PATH STATE");
    }

    public State nextState() {
        // return null;
        return new PathYUVPastState(manager, missionName, MISSION_DEPTH);
    }
}
