package org.aquapackrobotics.sw8s.states.GatePathStates;

import java.util.concurrent.*;
import java.io.File;
import java.time.Instant;

import org.opencv.videoio.VideoCapture;
import org.opencv.core.Mat;

import org.aquapackrobotics.sw8s.comms.*;
import org.aquapackrobotics.sw8s.states.State;
import org.aquapackrobotics.sw8s.vision.*;

import org.opencv.imgcodecs.Imgcodecs;

public class GatePathReadState extends State {

    private ScheduledFuture<byte[]> depthRead;
    private final File Dir;
    private final PathY target;

    public GatePathReadState(ControlBoardThreadManager manager, String missionName) {
        super(manager);
        CameraFeedSender.openCapture(1);
        target = new PathY();
        Dir = new File(new File(System.getProperty("java.io.tmpdir")), "path");
        Dir.mkdir();
    }

    public void onEnter() throws ExecutionException, InterruptedException {
        try {
            depthRead = manager.MSPeriodicRead((byte) 1);
            var mreturn = manager.setStability2Speeds(0, 0, 0, 0, manager.getYaw(),
                    -1.5);
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
            double x = (footage.horizontal_offset / Math.abs(footage.horizontal_offset)) * 0.2;
            System.out.println("X: " + String.valueOf(x));
        } catch (Exception e) {
            System.out.println("No gate seen.");
        }
        // }
        return false;

    }

    public void onExit() throws ExecutionException, InterruptedException {
    }

    public State nextState() {
        return null;
    }
}
