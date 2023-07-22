package org.aquapackrobotics.sw8s.states.BuoyStates;

import java.io.File;
import java.io.PrintWriter;
import java.time.Instant;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledFuture;

import org.aquapackrobotics.sw8s.comms.CameraFeedSender;
import org.aquapackrobotics.sw8s.comms.ControlBoardThreadManager;
import org.aquapackrobotics.sw8s.states.State;
import org.aquapackrobotics.sw8s.vision.Buoy;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

public class BuoyForwardState extends State {

    private ScheduledFuture<byte[]> depthRead;
    private final Buoy target;
    private final Buoy targetLarge;
    private final File Dir;
    private double depth = -1.5;
    private double yaw;

    public BuoyForwardState(ControlBoardThreadManager manager, String testName) {
        super(manager);
        CameraFeedSender.openCapture(0);
        target = new Buoy(false);
        targetLarge = new Buoy(true);
        Dir = new File("/mnt/data/" + testName + "/buoy");
        Dir.mkdir();
        yaw = manager.getYaw();
    }

    public void onEnter() throws ExecutionException, InterruptedException {
        try {
            System.out.println("ENTER FORWARD STATE");
            depthRead = manager.MSPeriodicRead((byte) 1);
            var mreturn = manager.setStability2Speeds(0, 0, 0, 0, yaw, depth);
            while (!mreturn.isDone())
                ;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean onPeriodic() {
        Mat frame = CameraFeedSender.getFrame(1);
        Mat yoloout = target.detectYoloV5(frame);
        if (target.detected()) {
            target.transAlign();
            try {
                PrintWriter printWriter = new PrintWriter(Dir.toString() + "/" + Instant.now().toString() + ".txt");
                printWriter.print(Arrays.toString(target.translation));
                System.out.println(Arrays.toString(target.translation));
                printWriter.close();
                System.out.println("Translation [x, z, distance]: " + Arrays.toString(target.translation));

                if (Math.abs(target.translation[0]) < 0.1 && Math.abs(target.translation[1]) < 0.1) {
                    return true;
                }

                double x = 0;
                if (Math.abs(target.translation[0]) > 0.1) {
                    x = target.translation[0] > 0 ? 0.1 : -0.1;
                }
                manager.setStability2Speeds(x, 0.1, 0, 0, yaw, depth);
            } catch (Exception e) {
                e.printStackTrace();
            }
            Imgcodecs.imwrite(Dir.toString() + "/" + Instant.now().toString() + ".jpeg", yoloout);
        } else {
            System.out.println("Not detected");
        }
        return false;

    }

    public void onExit() throws ExecutionException, InterruptedException {
        System.out.println("Exiting buoy");
        manager.setStability2Speeds(0, 0, 0, 0, yaw, depth);
        Thread.sleep(500);
        manager.setStability2Speeds(-0.5, -0.5, 0, 0, yaw, depth);
        Thread.sleep(1000);
    }

    public State nextState() {
        return null;
    }
}
