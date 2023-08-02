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

public class PathYUVFollowState extends State {

    private ScheduledFuture<byte[]> depthRead;
    private PathYUV target;
    private final File Dir;

    private double[] PathYUVOpts = { 0.5, 0.45, 0.4, 0.35, 0.3, 0.25, 0.2, 0.15 };
    private int PathYUVidx = 0;
    private final double MISSION_DEPTH;

    public PathYUVFollowState(CommsThreadManager manager, String missionName, double MISSION_DEPTH) {
        super(manager);
        this.PathYUVidx = 0;
        target = new PathYUV(this.PathYUVOpts[this.PathYUVidx]);
        // target = new PathYUV(0.25);
        Dir = new File("/mnt/data/" + missionName + "/pathYUV");
        Dir.mkdir();
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
            double x = (footage.horizontal_offset / Math.abs(footage.horizontal_offset)) * 0.2;
            System.out.println("X: " + String.valueOf(x));
            double y = -(footage.vertical_offset / Math.abs(footage.vertical_offset)) *
                    0.2;
            System.out.println("Y: " + String.valueOf(y));
            double angle = Math.toDegrees(footage.angle);
            // angle = angle > 360 ? angle % 360 : angle;
            System.out.println("Angle: " + String.valueOf(angle));
            System.out.println("System Angle: " + String.valueOf(manager.getYaw()));
            // double combined_angle = (manager.getYaw() + angle) % 360;
            double combined_angle = manager.getYaw();
            if (angle > 10.0)
                combined_angle -= 5.0;
            else if (angle < -10)
                combined_angle += 5.0;
            System.out.println("Combined Angle: " + String.valueOf(combined_angle));
            var mreturn = manager.setStability2Speeds(x, y, 0, 0, combined_angle,
                    MISSION_DEPTH);
            System.out.println("Decimation level: " + String.valueOf(this.PathYUVOpts[this.PathYUVidx]));
            if (this.PathYUVidx < this.PathYUVOpts.length) {
                this.target = new PathYUV(this.PathYUVOpts[this.PathYUVidx++]);
            }
            while (!mreturn.isDone())
                ;

            if (Math.abs(angle) < 20 && Math.abs(footage.horizontal_offset) < 20
                    && Math.abs(footage.vertical_offset) < 20) {
                return true;
            }

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
