package org.aquapackrobotics.sw8s.states.GateStates;

import java.io.File;
import java.io.PrintWriter;
import java.time.Instant;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;

import org.aquapackrobotics.sw8s.comms.Camera;
import org.aquapackrobotics.sw8s.comms.CameraFeedSender;
import org.aquapackrobotics.sw8s.comms.CommsThreadManager;
import org.aquapackrobotics.sw8s.states.State;
import org.aquapackrobotics.sw8s.vision.Gate;
import org.aquapackrobotics.sw8s.vision.GatePoles;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

public class GateForwardState extends State {

    private final GatePoles target;
    private final File Dir;
    private double yaw;
    private double noDetectCount;
    private final double MISSION_DEPTH;

    public GateForwardState(CommsThreadManager manager, String testName, double MISSION_DEPTH) {
        super(manager);
        CameraFeedSender.openCapture(Camera.FRONT);
        target = new GatePoles(true);
        Dir = new File("/mnt/data/" + testName + "/gate");
        Dir.mkdir();
        yaw = manager.getYaw();
        this.noDetectCount = -1;
        this.MISSION_DEPTH = MISSION_DEPTH;
    }

    public void onEnter() throws ExecutionException, InterruptedException {
        try {
            System.out.println("ENTER FORWARD STATE");
            var mreturn = manager.setStability2Speeds(0, 0.4, 0, 0, yaw, MISSION_DEPTH);
            while (!mreturn.isDone())
                ;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean onPeriodic() {
        Mat frame = CameraFeedSender.getFrame(Camera.FRONT);
        Mat yoloout = target.detectYoloV5(frame);
        if (target.detected()) {
            noDetectCount = 0;
            target.transAlign();
            try {
                PrintWriter printWriter = new PrintWriter(Dir.toString() + "/" + Instant.now().toString() + ".txt");
                printWriter.print(Arrays.toString(target.translation));
                System.out.println(Arrays.toString(target.translation));
                printWriter.close();
                System.out.println("Translation [x, y, distance]: " + Arrays.toString(target.translation));

                if (Math.abs(target.translation[2]) < 0.1) {
                    return true;
                }

                double x = 0;
                if (Math.abs(target.translation[0]) > 0.1) {
                    x = target.translation[0] > 0 ? 0.2 : -0.2;
                }

                manager.setStability2Speeds(x, 0.4, 0, 0, yaw, MISSION_DEPTH);
            } catch (Exception e) {
                e.printStackTrace();
            }
            Imgcodecs.imwrite(Dir.toString() + "/" + Instant.now().toString() + ".jpeg", yoloout);
        } else {
            if (noDetectCount >= 0)
                ++noDetectCount;
            System.out.println("Not detected");
        }

        if (noDetectCount >= 5) {
            return true;
        }
        return false;

    }

    public void onExit() throws ExecutionException, InterruptedException {
        System.out.println("Exiting gate");
    }

    public State nextState() {
        return null;
    }
}
