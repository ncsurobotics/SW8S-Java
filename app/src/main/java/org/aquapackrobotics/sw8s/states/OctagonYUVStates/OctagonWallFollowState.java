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

public class OctagonWallFollowState extends State {

    private final Wall target;
    private final File Dir;
    private double depth;
    private double combinedAngle;

    private double y;

    public OctagonWallFollowState(CommsThreadManager manager, String missionName, double MISSION_DEPTH) {
        super(manager);
        target = new Wall();
        Dir = new File("/mnt/data/" + missionName + "/path");
        Dir.mkdir();
        this.depth = MISSION_DEPTH;
        this.combinedAngle = manager.getYaw();
        y = 0;
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

            VisualObject footage = target.relativePosition(frame,
                    Dir.toString() + "/" + Instant.now().toString());

            y = 0.4;
            var mreturn = manager.setStability2Speeds(0.4, y, 0, 0,
                    combinedAngle,
                    depth);
            while (!mreturn.isDone())
                ;
            ;
        } catch (Exception e) {
            if (e instanceof VisualObjectException) {
                y = -0.4;
            } else {
                e.getStackTrace();
            }
        }
        return false;
    }

    public void onExit() throws ExecutionException, InterruptedException {
    }

    public State nextState() {
        return null;
    }
}
