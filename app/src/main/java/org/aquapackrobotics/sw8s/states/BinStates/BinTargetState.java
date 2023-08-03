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
import org.aquapackrobotics.sw8s.vision.VisualObject;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

public class BinTargetState extends State {

    private ScheduledFuture<byte[]> MISSION_DEPTHRead;
    private final Bin target;
    private final Bin targetLarge;
    private final File Dir;
    private double yaw;
    private final double MISSION_DEPTH;

    private final double descend;

    public BinTargetState(CommsThreadManager manager, String testName, double MISSION_DEPTH) {
        super(manager);
        CameraFeedSender.openCapture(Camera.BOTTOM);
        target = new Bin(false);
        targetLarge = new Bin(true);
        Dir = new File("/mnt/data/" + testName + "/bin");
        Dir.mkdir();
        new File(Dir.toString() + "/failure/").mkdirs();
        yaw = manager.getYaw();
        this.MISSION_DEPTH = MISSION_DEPTH;
        descend = 0;
    }

    public void onEnter() throws ExecutionException, InterruptedException {
        try {
            System.out.println("ENTER FORWARD STATE");
            var mreturn = manager.setStability2Speeds(0, 0, 0, 0, yaw, MISSION_DEPTH);
            while (!mreturn.isDone())
                ;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean onPeriodic() {
        double total_depth = MISSION_DEPTH + descend;
        Mat frame = CameraFeedSender.getFrame(Camera.BOTTOM);
        Mat yoloout = target.detectYoloV5(frame);
        try {
            if (target.detected()) {
                target.transAlign();
                PrintWriter printWriter = new PrintWriter(Dir.toString() + "/" + Instant.now().toString() + ".txt");
                printWriter.print(Arrays.toString(target.translation));
                System.out.println(Arrays.toString(target.translation));
                printWriter.close();
                System.out.println("Translation [x, z, distance]: " + Arrays.toString(target.translation));

                if (Math.abs(target.translation[0]) < 0.1 && Math.abs(target.translation[1]) < 0.1) {
                    System.out.println("FIRE DROPPERS");
                    File subDir = new File(Dir.toString() + "/" + "fire");
                    subDir.mkdir();
                    Imgcodecs.imwrite(subDir.toString() + "/" + Instant.now().toString() + ".jpeg", yoloout);
                    // return true;
                }

                double x = 0;
                if (Math.abs(target.translation[0]) > 0.1) {
                    x = target.translation[0] > 0 ? 0.4 : -0.4;
                }

                double y = 0;
                if (Math.abs(target.translation[1]) > 0.1) {
                    y = target.translation[1] > 0 ? 0.4 : -0.4;
                }

                manager.setStability2Speeds(x, y, 0, 0, yaw, total_depth);
                if (target.translation[0] > manager.getGyro()[0] - 0.1
                        && target.translation[0] < manager.getGyro()[0] + 0.1
                        && target.translation[1] > manager.getGyro()[1] - 0.1
                        && target.translation[1] < manager.getGyro()[1] + 0.1) {
                    if (manager.getDepth() > total_depth - 0.25 && manager.getDepth() < total_depth + 0.25) {
                        for (int i = 0; i < 3; i++) {
                            manager.fireDroppers();
                            Thread.sleep(100);
                        }
                        return false;
                    }
                }

                Imgcodecs.imwrite(Dir.toString() + "/" + Instant.now().toString() + ".jpeg", yoloout);
            } else {
                manager.setStability2Speeds(0, 0, 0, 0, yaw, MISSION_DEPTH);
                System.out.println("Not detected");
                Imgcodecs.imwrite(Dir.toString() + "/failure/" + Instant.now().toString() + ".jpeg", yoloout);
            }
        } catch (Exception e) {
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
