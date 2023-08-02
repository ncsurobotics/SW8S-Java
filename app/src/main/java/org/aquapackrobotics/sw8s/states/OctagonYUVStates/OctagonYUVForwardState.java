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

    private ScheduledFuture<byte[]> depthRead;
    private final PathYUV target;
    private final File Dir;
    private double depth = -1.5;
    private String missionName;
    private double targetYaw;

    public OctagonYUVForwardState(CommsThreadManager manager, String missionName, double targetYaw) {
        super(manager);
        target = new PathYUV(new IntPair(Integer.MIN_VALUE, 124), new IntPair(124, Integer.MAX_VALUE), 10,
                800, 0.25);
        Dir = new File("/mnt/data/" + missionName + "/path");
        Dir.mkdir();
        this.targetYaw = targetYaw;
    }

    public void onEnter() throws ExecutionException, InterruptedException {
        try {
            depthRead = manager.MSPeriodicRead((byte) 1);
            var mreturn = manager.setStability2Speeds(0, 0.4, 0, 0, targetYaw, depth);
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
        return new OctagonYUVFollowState(manager, missionName);
    }
}
