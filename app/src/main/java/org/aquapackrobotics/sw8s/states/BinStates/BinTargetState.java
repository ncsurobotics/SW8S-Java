package org.aquapackrobotics.sw8s.states.BinStates;

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
import org.aquapackrobotics.sw8s.vision.Bin;
import org.aquapackrobotics.sw8s.vision.*;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

public class BinTargetState extends State {

    private ScheduledFuture<byte[]> MISSION_DEPTHRead;
    private final Bin target;
    private final PathYUV path;
    private final File Dir;
    private final File DirPath;
    private double yaw;
    private final double MISSION_DEPTH;

    private double depth;

    private final double DEPTH_TARGET = -3.0;

    public BinTargetState(CommsThreadManager manager, String testName, double initialYaw, double MISSION_DEPTH) {
        super(manager);
        CameraFeedSender.openCapture(Camera.BOTTOM);
        target = new Bin(false);
        path = new PathYUV();
        Dir = new File("/mnt/data/" + testName + "/bin");
        Dir.mkdir();
        DirPath = new File("/mnt/data/" + testName + "/path");
        DirPath.mkdirs();
        new File(Dir.toString() + "/failure/").mkdirs();
        yaw = initialYaw;
        this.MISSION_DEPTH = MISSION_DEPTH;
    }

    public void onEnter() throws ExecutionException, InterruptedException {
        try {
            System.out.println("ENTER TARGET STATE");
            var mreturn = manager.setStability2Speeds(0, 0, 0, 0, yaw, MISSION_DEPTH);
            while (!mreturn.isDone())
                ;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void pathAlign() {
        Mat frame = CameraFeedSender.getFrame(Camera.BOTTOM);
        try {
            VisualObject footage = path.relativePosition(frame,
                    DirPath.toString() + "/" + Instant.now().toString());
            System.out.println("Original: " + footage);
            DoubleTriple trans = Translation.movement_triple(
                    new DoublePair(footage.horizontal_offset, footage.vertical_offset),
                    manager.getYaw(), footage.angle);
            yaw = trans.z;
            System.out.println("Computed: " + trans);
            var mreturn = manager.setStability2Speeds(trans.x, 0.4, 0, 0,
                    yaw,
                    MISSION_DEPTH);
            while (!mreturn.isDone())
                ;
        } catch (Exception e) {
        }
    }

    public boolean onPeriodic() {
        Mat frame = CameraFeedSender.getFrame(Camera.BOTTOM);
        Mat yoloout = target.detectYoloV5(frame);
        try {
            if (target.detected()) {
                target.transAlign();
                PrintWriter printWriter = new PrintWriter(Dir.toString() + "/" + Instant.now().toString() + ".txt");
                printWriter.println(Arrays.toString(target.translation));
                System.out.println(Arrays.toString(target.translation));
                System.out.println("Translation [x, z, distance]: " + Arrays.toString(target.translation));

                System.out.println("Original: " + Arrays.toString(target.translation));
                DoubleTriple trans = Translation.movement_triple(
                        new DoublePair(target.translation[0], target.translation[1]),
                        manager.getYaw(), target.translation[2]);
                yaw = trans.z;
                System.out.println("Computed: " + trans.toString());
                printWriter.println("Computed: " + trans.toString());
                printWriter.close();

                if (Math.abs(target.translation[0]) < 0.1 && Math.abs(target.translation[1]) < 0.1) {
                    if (depth <= DEPTH_TARGET) {
                        System.out.println("FIRE DROPPERS");
                        File subDir = new File(Dir.toString() + "/" + "fire");
                        subDir.mkdir();
                        Imgcodecs.imwrite(subDir.toString() + "/" + Instant.now().toString() + ".jpeg", yoloout);
                        for (int i = 0; i < 3; i++) {
                            manager.fireDroppers();
                            Thread.sleep(100);
                        }
                        return true;
                    } else {
                        depth -= 0.1;
                    }
                }

                manager.setStability2Speeds(trans.x, trans.y, 0, 0, yaw, depth);

                Imgcodecs.imwrite(Dir.toString() + "/" + Instant.now().toString() + ".jpeg", yoloout);
            } else {
                manager.setStability2Speeds(0, 0, 0, 0, yaw, MISSION_DEPTH);
                System.out.println("Not detected");
                Imgcodecs.imwrite(Dir.toString() + "/failure/" + Instant.now().toString() + ".jpeg", yoloout);
            }
        } catch (

        Exception e) {
            e.printStackTrace();
        }
        return false;

    }

    public void onExit() throws ExecutionException, InterruptedException {
        System.out.println("Exiting bin");
        manager.setGlobalSpeeds(0, 0, 0, 0, 0, 0);
    }

    public State nextState() {
        return null;
    }
}
