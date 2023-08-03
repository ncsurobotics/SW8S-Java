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
import org.aquapackrobotics.sw8s.vision.GatePoles;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

public class GateFindState extends State {

    private final GatePoles target;
    private final File Dir;
    private final double MISSION_DEPTH;
    private String testName;

    public GateFindState(CommsThreadManager manager, String testName, double MISSION_DEPTH) {
        super(manager);
        CameraFeedSender.openCapture(Camera.FRONT);
        target = new GatePoles(true);
        Dir = new File("/mnt/data/" + testName + "/gate");
        Dir.mkdir();
        new File(Dir.toString() + "/failure/").mkdirs();
        this.MISSION_DEPTH = MISSION_DEPTH;
        this.testName = testName;
    }

    public void onEnter() throws ExecutionException, InterruptedException {
        try {
            System.out.println("ENTER FORWARD STATE");
            var mreturn = manager.setStability1Speeds(0, 0, 0, 0, 0.2, MISSION_DEPTH);
            while (!mreturn.isDone())
                ;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean onPeriodic() {
        Mat frame = CameraFeedSender.getFrame(Camera.FRONT);
        Mat yoloout = target.detectYoloV5(frame);
        try {
            if (target.detected()) {
                target.transAlign();
                PrintWriter printWriter = new PrintWriter(Dir.toString() + "/" + Instant.now().toString() + ".txt");
                printWriter.print(Arrays.toString(target.translation));
                System.out.println(Arrays.toString(target.translation));
                printWriter.close();
                System.out.println("Translation [x, y, distance]: " + Arrays.toString(target.translation));
                Imgcodecs.imwrite(Dir.toString() + "/" + Instant.now().toString() + ".jpeg", yoloout);
                Imgcodecs.imwrite(Dir.toString() + "/" + Instant.now().toString() + ".jpeg", yoloout);
                var mreturn = manager.setStability1Speeds(0, 0, 0, 0, 0, MISSION_DEPTH);
                while (!mreturn.isDone())
                    ;
                return true;
            } else {
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;

    }

    public void onExit() throws ExecutionException, InterruptedException {
        System.out.println("Exiting gate");
    }

    public State nextState() {
        return new GateForwardState(manager, testName, MISSION_DEPTH);
    }
}
