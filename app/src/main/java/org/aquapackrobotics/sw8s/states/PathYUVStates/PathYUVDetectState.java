package org.aquapackrobotics.sw8s.states.PathYUVStates;

import java.io.File;
import java.time.Instant;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledFuture;

import org.aquapackrobotics.sw8s.comms.Camera;
import org.aquapackrobotics.sw8s.comms.CameraFeedSender;
import org.aquapackrobotics.sw8s.comms.CommsThreadManager;
import org.aquapackrobotics.sw8s.states.State;
import org.aquapackrobotics.sw8s.vision.PathYUV;
import org.aquapackrobotics.sw8s.vision.VisualObject;
import org.opencv.core.Mat;

public class PathYUVDetectState extends State {

    private ScheduledFuture<byte[]> depthRead;
    private PathYUV target;
    private final File Dir;

    private double[] PathYUVOpts = { 0.5, 0.4, 0.35, 0.3, 0.2 };
    private int PathYUVidx = 0;
    private String missionName;
    private double initialYaw;
    private double curPitch;
    private double combinedAngle;
    private final double MISSION_DEPTH;

    public PathYUVDetectState(CommsThreadManager manager, String missionName, double initialYaw, double MISSION_DEPTH) {
        super(manager);
        this.PathYUVidx = 0;
        target = new PathYUV(this.PathYUVOpts[this.PathYUVidx]);
        Dir = new File("/mnt/data/" + missionName + "/pathYUV");
        Dir.mkdir();
        this.missionName = missionName;
        this.initialYaw = initialYaw;
        this.curPitch = 40;
        this.combinedAngle = initialYaw;
        this.MISSION_DEPTH = MISSION_DEPTH;
    }

    public void onEnter() throws ExecutionException, InterruptedException {
        try {
            depthRead = manager.MSPeriodicRead((byte) 1);
            var mreturn = manager.setStability2Speeds(0.15, 0.8, curPitch, 0, initialYaw, MISSION_DEPTH);
            while (!mreturn.isDone())
                ;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean onPeriodic() {
        System.out.println("Initial: " + String.valueOf(initialYaw) + ", Current: " + String.valueOf(manager.getYaw()));
        Mat frame = CameraFeedSender.getFrame(Camera.BOTTOM);
        try {
            VisualObject footage = target.relativePosition(frame,
                    Dir.toString() + "/" + Instant.now().toString());

            // Adjust pitch down, but never bring it back up
            double minPitch = (PathYUVOpts.length - PathYUVidx) * 5;
            if (curPitch > minPitch)
                curPitch = minPitch;
            System.out.println("Pitch: " + String.valueOf(curPitch));

            double x = (footage.horizontal_offset / Math.abs(footage.horizontal_offset)) * 0.2;
            System.out.println("X: " + String.valueOf(x));

            double angle = Math.toDegrees(footage.angle);
            double combinedAngle = manager.getYaw();
            if (angle > 10.0)
                combinedAngle -= 5.0;
            else if (angle < -10)
                combinedAngle += 5.0;
            System.out.println("Combined Angle: " + String.valueOf(combinedAngle));
            var mreturn = manager.setStability2Speeds(x, 0.2, curPitch, 0, combinedAngle,
                    MISSION_DEPTH);
            System.out.println("Decimation level: " + String.valueOf(this.PathYUVOpts[this.PathYUVidx]));
            System.out.println("DETECT");

            this.target = new PathYUV(this.PathYUVOpts[this.PathYUVidx++]);
            while (!mreturn.isDone())
                ;
            if (this.PathYUVidx == this.PathYUVOpts.length - 1)
                return true;
        } catch (Exception e) {
            // this.PathYUVidx = 0;
            System.out.println("NO DETECT");
            /*
             * try {
             * var mreturn = manager.setStability2Speeds(0, 0.4, curPitch, 0, combinedAngle,
             * MISSION_DEPTH);
             * while (!mreturn.isDone())
             * ;
             * } catch (Exception e2) {
             * e2.printStackTrace();
             * }
             */
        }
        return false;
    }

    public void onExit() throws ExecutionException, InterruptedException {
        System.out.println("EXIT DETECT STATE");
    }

    public State nextState() {
        return new PathYUVThroughState(manager, missionName, MISSION_DEPTH);
    }
}
