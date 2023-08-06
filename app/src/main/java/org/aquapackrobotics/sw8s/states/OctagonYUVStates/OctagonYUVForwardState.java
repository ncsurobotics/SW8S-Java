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

public class OctagonYUVForwardState extends State {

    private final Octagon target;
    private final File Dir;
    private String missionName;
    private double targetYaw;
    private final double MISSION_DEPTH;

    public OctagonYUVForwardState(CommsThreadManager manager, String missionName, double targetYaw,
            double MISSION_DEPTH) {
        super(manager);
        target = new Octagon();
        Dir = new File("/mnt/data/" + missionName + "/path");
        Dir.mkdir();
        this.targetYaw = targetYaw;
        this.MISSION_DEPTH = MISSION_DEPTH;
    }

    public void onEnter() throws ExecutionException, InterruptedException {
        try {
            var mreturn = manager.setStability2Speeds(0.6, 0.8, 0, 0, targetYaw, MISSION_DEPTH);
            while (!mreturn.isDone())
                ;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean onPeriodic() {
        Mat frame = CameraFeedSender.getFrame(Camera.BOTTOM);
        try {
            VisualObject footage = target.relativePosition(frame,
                    Dir.toString() + "/" + Instant.now().toString() + ".jpeg");
            return true;
        } catch (Exception e) {
        }
        return false;
    }

    public void onExit() throws ExecutionException, InterruptedException {
        System.out.println("EXIT FORWARD STATE");
    }

    public State nextState() {
        return new OctagonYUVFollowState(manager, missionName, MISSION_DEPTH);
    }
}
