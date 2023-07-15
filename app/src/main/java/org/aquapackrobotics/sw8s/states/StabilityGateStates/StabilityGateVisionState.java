package org.aquapackrobotics.sw8s.states.StabilityGateStates;

import org.aquapackrobotics.sw8s.comms.*;
import org.aquapackrobotics.sw8s.states.*;
import org.aquapackrobotics.sw8s.vision.*;

import org.opencv.videoio.VideoCapture;
import org.opencv.core.Mat;

import java.util.concurrent.*;
import java.util.Arrays;

public class StabilityGateVisionState extends State {

    private double yaw;
    private final double PITCH = 90;

    private final Path target;

    public StabilityGateVisionState(ControlBoardThreadManager manager, double yaw) {
        super(manager);
        this.yaw = yaw;
        CameraFeedSender.openCapture(0);
        /* TODO: Adjust these to see side poles */
        target = new Path(100, 170, 30, 400);
    }

    /* Wait until gate is seen */
    public void onEnter() throws ExecutionException, InterruptedException {
        while (true) {
            try {
                yaw += 0.1;
                System.out.println("Waiting to see gate...");
                var mreturn = manager.setStability2Speeds(0, 0, PITCH, 0, yaw, -2.1);
                while (!mreturn.isDone())
                    ;
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                break;
            } catch (Exception e) {
                Thread.sleep(500);
            }
        }
    }

    public boolean onPeriodic() {
        Mat frame = CameraFeedSender.getFrame(0);
        try {
            VisualObject footage = target.relativePosition(frame);
            double x = (footage.horizontal_offset / Math.abs(footage.horizontal_offset)) * 0.1;
            /* Try to flip camera to face gate with pitch target */
            // x may or may not be the right variable to use here
            var mreturn = manager.setStability2Speeds(x, 1.0, 90, 0, yaw, -2.1);
            while (!mreturn.isDone())
                ;
        } catch (Exception e) {
            e.printStackTrace();
            return true;
        }
        return false;
    }

    public void onExit() throws ExecutionException, InterruptedException {
    }

    public State nextState() {
        return new StabilityGateForwardState(manager, yaw);
    }
}
