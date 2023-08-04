package org.aquapackrobotics.sw8s.states.BuoyPathStates;

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

public class BuoyPathForwardState extends State {

    private ScheduledFuture<byte[]> MISSION_DEPTHRead;
    private final Buoy target;
    private final PathYUV path;
    private final File Dir;
    private final File DirPath;
    private double initialYaw;
    private double noDetectCount;
    private final double MISSION_DEPTH;
    private double combinedAngle;
    private String testName;

    private double strafe;
    private int count;
    private int count_max;

    public BuoyPathForwardState(CommsThreadManager manager, String testName, double initialYaw, double MISSION_DEPTH) {
        super(manager);
        CameraFeedSender.openCapture(Camera.BOTTOM);
        CameraFeedSender.openCapture(Camera.FRONT);
        target = new Buoy(false);
        path = new PathYUV();
        Dir = new File("/mnt/data/" + testName + "/buoy");
        Dir.mkdirs();
        DirPath = new File("/mnt/data/" + testName + "/path");
        DirPath.mkdirs();
        new File(Dir.toString() + "/failure/").mkdirs();
        this.initialYaw = initialYaw;
        this.noDetectCount = -1;
        this.MISSION_DEPTH = MISSION_DEPTH;
        combinedAngle = manager.getYaw();
        this.strafe = -0.15;
        this.count = 0;
        this.count_max = 10;
    }

    public boolean pathAlign() {
        Mat frame = CameraFeedSender.getFrame(Camera.BOTTOM);
        try {
            VisualObject footage = path.relativePosition(frame,
                    DirPath.toString() + "/" + Instant.now().toString());
            System.out.println("Original: " + footage);
            DoubleTriple trans = Translation.movement_triple(
                    new DoublePair(footage.horizontal_offset, footage.vertical_offset),
                    manager.getYaw(), footage.angle);
            combinedAngle = trans.z;
            System.out.println("Computed: " + trans);
            var mreturn = manager.setStability2Speeds(trans.x, 0.4, 0, 0,
                    combinedAngle,
                    MISSION_DEPTH);
            while (!mreturn.isDone())
                ;
            return true;
        } catch (Exception e) {
        }
        return false;
    }

    public void onEnter() throws ExecutionException, InterruptedException {
        try {
            System.out.println("ENTER FORWARD STATE");
            var mreturn = manager.setStability2Speeds(-0.15, 0.4, 0, 0, initialYaw, MISSION_DEPTH);
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
                System.out.println("Computed: " + trans);
                printWriter.println("Computed: " + trans);
                printWriter.close();

                manager.setStability2Speeds(trans.x, 0.4, 0, 0, combinedAngle, MISSION_DEPTH);
            } catch (Exception e) {
                e.printStackTrace();
            }
            Imgcodecs.imwrite(Dir.toString() + "/" + Instant.now().toString() + ".jpeg", yoloout);
        } else {
            if (noDetectCount >= 0) {
                ++noDetectCount;
            }
            if (!pathAlign()) {
                try {
                    // Gradually increasing strafe
                    if (++count > count_max) {
                        count_max += 5;
                        count = 0;
                        strafe = -strafe;
                    }
                    manager.setStability2Speeds(strafe, 0.25, 0, 0, combinedAngle, MISSION_DEPTH);
                } catch (Exception e) {
                    e.printStackTrace();
                }
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
        manager.setStability2Speeds(0, 0.4, 0, 0, initialYaw, MISSION_DEPTH);
        while ((manager.getYaw() - initialYaw) > 10) {
            System.out.println("DIFFERENCE: " + manager.getYaw() + ", " + initialYaw);
            Thread.sleep(100);
        }
        System.out.println("Stopping motors");
        manager.setStability2Speeds(0, 0, 0, 0, initialYaw, MISSION_DEPTH);
    }

    public State nextState() {
        return null;
        /*
         * return new BuoyPathPastState(manager, testName,
         * combinedAngle >= 0.0 ? combinedAngle - 180.0 : combinedAngle + 180,
         * MISSION_DEPTH);
         */
    }
}
