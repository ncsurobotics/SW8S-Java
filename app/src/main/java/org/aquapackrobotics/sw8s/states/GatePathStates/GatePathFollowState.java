package org.aquapackrobotics.sw8s.states.GatePathStates;

import java.util.concurrent.*;
import java.io.File;
import java.lang.Math;
import java.time.Instant;

import org.opencv.videoio.VideoCapture;
import org.opencv.core.Mat;

import org.aquapackrobotics.sw8s.comms.*;
import org.aquapackrobotics.sw8s.states.State;
import org.aquapackrobotics.sw8s.vision.*;

public class GatePathFollowState extends State {

    private ScheduledFuture<byte[]> depthRead;
    private final PathY target;
    private final File Dir;
    private int countUnseen;
    private final double startYaw;

    public GatePathFollowState(ControlBoardThreadManager manager, String missionName) {
        super(manager);
        CameraFeedSender.openCapture(1);
        target = new PathY();
        Dir = new File("/mnt/data/" + missionName + "/gate");
        Dir.mkdir();
        countUnseen = 0;
        startYaw = manager.getYaw() - 6.0;
        System.out.println("startYaw: " + String.valueOf(startYaw));
    }

    public void onEnter() throws ExecutionException, InterruptedException {
        try {
            depthRead = manager.MSPeriodicRead((byte) 1);
            var mreturn = manager.setStability2Speeds(0, 0, 0, 0, this.startYaw, -2.0);
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
                    Dir.toString() + "/" + Instant.now().toString() + ".jpeg");
            countUnseen = 0;
            double x = (footage.horizontal_offset / Math.abs(footage.horizontal_offset)) * 0.2;
            System.out.println("X: " + String.valueOf(x));
            var mreturn = manager.setStability2Speeds(x, 0.4, 0, 0, this.startYaw, -2.0);
            while (!mreturn.isDone())
                ;
        } catch (Exception e) {
            if (++countUnseen >= 10)
                return true;
            System.out.println("Count Unseen: " + String.valueOf(countUnseen));
        }
        return false;
    }

    public void onExit() throws ExecutionException, InterruptedException {
    }

    public State nextState() {
        return null;
    }
}