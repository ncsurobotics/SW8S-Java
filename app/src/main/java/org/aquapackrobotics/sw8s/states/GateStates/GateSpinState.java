package org.aquapackrobotics.sw8s.states.GateStates;

import org.aquapackrobotics.sw8s.comms.Camera;
import org.aquapackrobotics.sw8s.comms.CameraFeedSender;
import org.aquapackrobotics.sw8s.comms.CommsThreadManager;
import org.aquapackrobotics.sw8s.states.State;
import org.aquapackrobotics.sw8s.vision.Gate;
import org.aquapackrobotics.sw8s.vision.*;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

import java.io.File;
import java.io.PrintWriter;
import java.time.Instant;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;

public class GateSpinState extends State {
    private final GatePoles target;
    private final File Dir;
    private double yaw;
    private double noDetectCount;
    private final double MISSION_DEPTH;

    public GateSpinState(CommsThreadManager manager, String testName, double missionDepth) {
        super(manager);
        CameraFeedSender.openCapture(Camera.FRONT);
        target = new GatePoles();
        Dir = new File("/mnt/data/" + testName + "/gate");
        Dir.mkdir();
        yaw = manager.getYaw();
        this.noDetectCount = -1;
        this.MISSION_DEPTH = missionDepth;
    }

    @Override
    public void onEnter() throws ExecutionException, InterruptedException {
        try {
            System.out.println("ENTER GATE SPIN STATE");
            var mreturn = manager.setStability2Speeds(0, 0, 0, 0, yaw, MISSION_DEPTH);
            while (!mreturn.isDone())
                ;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onPeriodic() throws ExecutionException, InterruptedException {
        Mat frame = CameraFeedSender.getFrame(Camera.FRONT);
        Mat yoloout = target.detectYoloV5(frame);
        if (target.detected()) {
            noDetectCount = 0;
            target.transAlign();
            try {
                PrintWriter printWriter = new PrintWriter(Dir.toString() + "/" + Instant.now().toString() + ".txt");
                printWriter.println(Arrays.toString(target.translation));
                System.out.println(Arrays.toString(target.translation));
                System.out.println("Translation [x, y, distance]: " + Arrays.toString(target.translation));
                Imgcodecs.imwrite(Dir.toString() + "/" + Instant.now().toString() + ".jpeg", yoloout);

                DoublePair trans = Translation.movement(
                        new DoublePair(target.translation[0], target.translation[1]));
                System.out.println("Computed: " + target);
                printWriter.println("Computed: " + target);
                printWriter.close();

                if (Math.abs(target.translation[2]) < 0.1) {
                    return true;
                }

                manager.setStability2Speeds(trans.x, 0.4, 0, 0, yaw++, MISSION_DEPTH);
            } catch (Exception e) {
                e.printStackTrace();
            }
            Imgcodecs.imwrite(Dir.toString() + "/" + Instant.now().toString() + ".jpeg", yoloout);
        } else {
            if (noDetectCount >= 0)
                ++noDetectCount;
            System.out.println("Not detected");
        }

        if (noDetectCount >= 4) {
            return true;
        }
        return false;
    }

    @Override
    public void onExit() throws ExecutionException, InterruptedException {
        System.out.println("exiting spin");

    }

    @Override
    public State nextState() {
        return null;
    }
}
