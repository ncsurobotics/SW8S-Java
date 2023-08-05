package org.aquapackrobotics.sw8s.states.BuoySpinStates;

import java.io.File;
import java.io.PrintWriter;
import java.time.Instant;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;

import org.aquapackrobotics.sw8s.comms.Camera;
import org.aquapackrobotics.sw8s.comms.CameraFeedSender;
import org.aquapackrobotics.sw8s.comms.CommsThreadManager;
import org.aquapackrobotics.sw8s.states.State;
import org.aquapackrobotics.sw8s.vision.Buoy;
import org.aquapackrobotics.sw8s.vision.DoublePair;
import org.aquapackrobotics.sw8s.vision.Translation;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

public class BuoySpinForwardState extends State {

    private final Buoy target;
    private final File Dir;
    private double initialYaw;
    private double noDetectCount;
    private final double MISSION_DEPTH;
    private double combinedAngle;
    private String missionName;

    public BuoySpinForwardState(CommsThreadManager manager, String missionName, double initialYaw,
            double MISSION_DEPTH) {
        super(manager);
        CameraFeedSender.openCapture(Camera.BOTTOM);
        CameraFeedSender.openCapture(Camera.FRONT);
        target = new Buoy(false);
        Dir = new File("/mnt/data/" + missionName + "/buoy");
        Dir.mkdirs();
        new File(Dir.toString() + "/failure/").mkdirs();
        this.missionName = missionName;
        this.initialYaw = initialYaw;
        this.noDetectCount = -1;
        this.MISSION_DEPTH = MISSION_DEPTH;
        combinedAngle = manager.getYaw();
    }

    public void onEnter() throws ExecutionException, InterruptedException {
        try {
            System.out.println("ENTER FORWARD STATE");
            var mreturn = manager.setStability2Speeds(0, 0.8, 0, 0, initialYaw, MISSION_DEPTH);
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

                manager.setStability2Speeds(trans.x, 0.8, 0, 0, combinedAngle, MISSION_DEPTH);
            } catch (Exception e) {
                e.printStackTrace();
            }
            Imgcodecs.imwrite(Dir.toString() + "/" + Instant.now().toString() + ".jpeg", yoloout);
        } else {
            if (noDetectCount >= 0) {
                ++noDetectCount;
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
        manager.setStability2Speeds(0, 0.8, 0, 0, initialYaw, MISSION_DEPTH);
        Thread.sleep(4000);
        manager.setStability2Speeds(0, -0.3, 0, 0, initialYaw, MISSION_DEPTH);
        while ((manager.getYaw() - initialYaw) > 10) {
            System.out.println("DIFFERENCE: " + manager.getYaw() + ", " + initialYaw);
            Thread.sleep(100);
        }
    }

    public State nextState() {
        return new BuoySpinForwardState(manager, missionName, initialYaw, MISSION_DEPTH);
    }
}
