package org.aquapackrobotics.sw8s.states.BuoyStates;

import java.io.File;
import java.io.PrintWriter;
import java.time.Instant;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledFuture;

import org.aquapackrobotics.sw8s.comms.Camera;
import org.aquapackrobotics.sw8s.comms.CameraFeedSender;
import org.aquapackrobotics.sw8s.comms.CommsThreadManager;
import org.aquapackrobotics.sw8s.states.State;
import org.aquapackrobotics.sw8s.vision.*;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

public class BuoyForwardState extends State {

    private ScheduledFuture<byte[]> MISSION_DEPTHRead;
    private final Buoy target;
    private final Buoy targetLarge;
    private final File Dir;
    private double initialYaw;
    private double noDetectCount;
    private final double MISSION_DEPTH;

    public BuoyForwardState(CommsThreadManager manager, String testName, double initialYaw, double MISSION_DEPTH) {
        super(manager);
        CameraFeedSender.openCapture(Camera.FRONT);
        target = new Buoy(false);
        targetLarge = new Buoy(true);
        Dir = new File("/mnt/data/" + testName + "/buoy");
        Dir.mkdirs();
        new File(Dir.toString() + "/failure/").mkdirs();
        this.initialYaw = initialYaw;
        this.noDetectCount = -1;
        this.MISSION_DEPTH = MISSION_DEPTH;
    }

    public void onEnter() throws ExecutionException, InterruptedException {
        try {
            System.out.println("ENTER FORWARD STATE");
            var mreturn = manager.setStability2Speeds(0, 0, 0, 0, initialYaw, MISSION_DEPTH);
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

                double x = 0;
                if (Math.abs(target.translation[0]) > 0.1) {
                    x = target.translation[0] > 0 ? -0.2 : 0.2;
                }

                manager.setStability2Speeds(x, 0.4, 0, 0, initialYaw, MISSION_DEPTH);
            } catch (Exception e) {
                e.printStackTrace();
            }
            Imgcodecs.imwrite(Dir.toString() + "/" + Instant.now().toString() + ".jpeg", yoloout);
        } else {
            if (noDetectCount >= 0)
                ++noDetectCount;
            try {
                manager.setStability2Speeds(0, 0, 0, 0, initialYaw, MISSION_DEPTH);
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println("Not detected");
            Imgcodecs.imwrite(Dir.toString() + "/failure/" + Instant.now().toString() + ".jpeg", yoloout);
        }

        if (noDetectCount >= 15) {
            return true;
        }
        return false;

    }

    public void onExit() throws ExecutionException, InterruptedException {
        System.out.println("Exiting buoy");
        manager.setStability2Speeds(0, 1, 0, 0, initialYaw, MISSION_DEPTH);
        Thread.sleep(4000);
        System.out.println("Stopping motors");
        manager.setStability2Speeds(0, 0, 0, 0, initialYaw, MISSION_DEPTH);
        Thread.sleep(1000);
        System.out.println("Driving back");
        manager.setStability2Speeds(-0.5, -0.5, 0, 0, initialYaw, MISSION_DEPTH);
        Thread.sleep(6000);
    }

    public State nextState() {
        return null;
    }
}
