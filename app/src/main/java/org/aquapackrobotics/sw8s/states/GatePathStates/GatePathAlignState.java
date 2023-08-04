package org.aquapackrobotics.sw8s.states.GatePathStates;

import java.io.File;
import java.io.PrintWriter;
import java.time.Instant;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledFuture;

import org.aquapackrobotics.sw8s.comms.Camera;
import org.aquapackrobotics.sw8s.comms.CameraFeedSender;
import org.aquapackrobotics.sw8s.comms.CommsThreadManager;
import org.aquapackrobotics.sw8s.states.State;
import org.aquapackrobotics.sw8s.vision.Gate;
import org.aquapackrobotics.sw8s.vision.GatePoles;
import org.aquapackrobotics.sw8s.vision.PathYUV;
import org.aquapackrobotics.sw8s.vision.VisualObject;
import org.aquapackrobotics.sw8s.vision.*;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

public class GatePathAlignState extends State {

    private ScheduledFuture<byte[]> depthRead;
    private PathYUV path;
    private final File Dir;

    private double[] PathYUVOpts = { 0.40, 0.35, 0.3, 0.2 };
    private int PathYUVidx = 0;
    private String missionName;
    private double initialYaw;
    private double curPitch;
    private double combinedAngle;
    private final double MISSION_DEPTH;

    public GatePathAlignState(CommsThreadManager manager, String missionName, double initialYaw,
            double MISSION_DEPTH) {
        super(manager);
        this.PathYUVidx = 0;
        path = new PathYUV(this.PathYUVOpts[this.PathYUVidx]);
        Dir = new File("/mnt/data/" + missionName + "/pathYUV");
        Dir.mkdir();
        this.missionName = missionName;
        this.initialYaw = initialYaw;
        this.curPitch = 30;
        this.combinedAngle = initialYaw;
        this.MISSION_DEPTH = MISSION_DEPTH;
    }

    public void onEnter() throws ExecutionException, InterruptedException {
        try {
            depthRead = manager.MSPeriodicRead((byte) 1);
            var mreturn = manager.setStability2Speeds(0.0, 0.8, curPitch, 0, initialYaw, MISSION_DEPTH);
            while (!mreturn.isDone())
                ;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean onPeriodic() {
        Mat frame = CameraFeedSender.getFrame(Camera.BOTTOM);
        try {
            VisualObject footage = path.relativePosition(frame,
                    Dir.toString() + "/" + Instant.now().toString());

            if (Math.abs(footage.angle) < 10 && Math.abs(footage.horizontal_offset) < 20
                    && Math.abs(footage.vertical_offset) < 20) {
                return true;
            }

            System.out.println("Original: " + footage);
            DoubleTriple trans = Translation.movement_triple(
                    new DoublePair(footage.horizontal_offset, footage.vertical_offset),
                    manager.getYaw(), footage.angle);
            combinedAngle = trans.z;
            System.out.println("Computed: " + trans);

            var mreturn = manager.setStability2Speeds(trans.x, trans.y, 0, 0,
                    combinedAngle,
                    MISSION_DEPTH);
            while (!mreturn.isDone())
                ;
        } catch (Exception e) {
        }
        return false;
    }

    public void onExit() throws ExecutionException, InterruptedException {
        System.out.println("EXIT ALIGN STATE");
    }

    public State nextState() {
        return null;
    }
}
