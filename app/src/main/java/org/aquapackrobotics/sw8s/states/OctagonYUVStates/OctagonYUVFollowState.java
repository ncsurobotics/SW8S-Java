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
            VisualObject footage = target.relativePosition(frame,
                    Dir.toString() + "/" + Instant.now().toString());
            DoubleTriple trans = Translation.movement_triple(
                    new DoublePair(footage.horizontal_offset, footage.vertical_offset),
                    manager.getYaw(), footage.angle);
            combinedAngle = trans.z;
            if (Math.abs(footage.horizontal_offset) < 20
                    && Math.abs(footage.vertical_offset) < 20) {
                depth += 0.05;
            }

            var mreturn = manager.setStability2Speeds(trans.x, trans.y, 0, 0,
                    combinedAngle,
                    depth);
            while (!mreturn.isDone())
                ;
            ;
        } catch (Exception e) {
        }
        if (depth >= 0) {
            return true;
        }
        return false;
    }

    public void onExit() throws ExecutionException, InterruptedException {
    }

    public State nextState() {
        return null;
    }
}
