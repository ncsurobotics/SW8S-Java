package org.aquapackrobotics.sw8s.states.StabilityGateStates;

import java.util.concurrent.ExecutionException;

import org.aquapackrobotics.sw8s.comms.Camera;
import org.aquapackrobotics.sw8s.comms.CameraFeedSender;
import org.aquapackrobotics.sw8s.comms.CommsThreadManager;
import org.aquapackrobotics.sw8s.states.State;
import org.aquapackrobotics.sw8s.vision.Path;
import org.aquapackrobotics.sw8s.vision.VisualObject;
import org.opencv.core.Mat;

public class StabilityGateVisionState extends State {

    private double yaw;
    private final double PITCH = 90;

    private final Path target;

    public StabilityGateVisionState(CommsThreadManager manager, double yaw) {
        super(manager);
        this.yaw = yaw;
        CameraFeedSender.openCapture(Camera.BOTTOM);
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
        Mat frame = CameraFeedSender.getFrame(Camera.BOTTOM);
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
