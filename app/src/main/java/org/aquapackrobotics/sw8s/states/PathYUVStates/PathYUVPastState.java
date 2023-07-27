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

public class PathYUVPastState extends State {

    private ScheduledFuture<byte[]> depthRead;
    private PathYUV target;
    private final File Dir;

    private double[] PathYUVOpts = { 0.5, 0.45, 0.4, 0.35, 0.3, 0.25, 0.2, 0.15 };
    private int PathYUVidx = 0;
    private int noDetectCount;
    private double curAngle;

    public PathYUVPastState(CommsThreadManager manager, String missionName) {
        super(manager);
        this.PathYUVidx = 0;
        target = new PathYUV(this.PathYUVOpts[this.PathYUVidx]);
        // target = new PathYUV(0.25);
        Dir = new File("/mnt/data/" + missionName + "/pathYUV");
        Dir.mkdir();
        noDetectCount = 0;
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
        Mat frame = CameraFeedSender.getFrame(0);
        try {
            VisualObject footage = target.relativePosition(frame,
                    Dir.toString() + "/" + Instant.now().toString());
            this.noDetectCount = 0;

            // double x = (footage.horizontal_offset / Math.abs(footage.horizontal_offset))
            // * 0.2;
            double x = 0;
            if (Math.abs(footage.horizontal_offset) > 0.2) {
                x = footage.horizontal_offset > 0 ? 0.15 : -0.15;
            }
            System.out.println("X: " + String.valueOf(x));

            double angle = Math.toDegrees(footage.angle);
            System.out.println("Angle: " + String.valueOf(angle));
            System.out.println("System Angle: " + String.valueOf(manager.getYaw()));
            double combined_angle = manager.getYaw();
            if (angle > 10.0)
                combined_angle -= 1.0;
            else if (angle < -10)
                combined_angle += 1.0;
            System.out.println("Combined Angle: " + String.valueOf(combined_angle));
            this.curAngle = combined_angle;

            System.out.println("Y: FORWARD");
            var mreturn = manager.setStability2Speeds(x, 0.3, 0, 0, combined_angle,
                    -1.0);
            System.out.println("Decimation level: " + String.valueOf(this.PathYUVOpts[this.PathYUVidx]));
            if (this.PathYUVidx < this.PathYUVOpts.length) {
                this.target = new PathYUV(this.PathYUVOpts[this.PathYUVidx++]);
            }
            while (!mreturn.isDone())
                ;
        } catch (Exception e) {
            // this.PathYUVidx = this.PathYUVOpts.length / 2;
            if (++this.noDetectCount > 15)
                return true;
            try {
                var mreturn = manager.setStability2Speeds(0, 0.3, 0, 0, this.curAngle,
                        -1.0);
            } catch (Exception e2) {
                e2.printStackTrace();
                System.exit(1);
            }
            System.out.println("NO DETECT: " + String.valueOf(this.noDetectCount));
        }
        return false;
    }

    public void onExit() throws ExecutionException, InterruptedException {
        System.out.println("PAST PATH");
    }

    public State nextState() {
        return null;
    }
}
