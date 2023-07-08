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
    private double depth = -1;

    public BuoyForwardState(ControlBoardThreadManager manager, String testName) {
        super(manager);
        CameraFeedSender.openCapture(0);
        target = new Buoy(false);
        targetLarge = new Buoy(true);
        Dir = new File("/mnt/data/" + testName + "/buoy");
        Dir.mkdir();
    }

    public void onEnter() throws ExecutionException, InterruptedException {
        try {
            System.out.println("ENTER FORWARD STATE");
            depthRead = manager.MSPeriodicRead((byte) 1);
            var mreturn = manager.setStability2Speeds(0, 0, 0, 0, manager.getYaw(), depth);
            while (!mreturn.isDone())
                ;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean onPeriodic() {
        Mat frame = CameraFeedSender.getFrame(1);
        Mat yoloout = target.detectYoloV5(frame);
        target.transAlign();
        try {
            PrintWriter printWriter = new PrintWriter(Dir.toString() + "/" + Instant.now().toString() + ".txt");
            printWriter.print(Arrays.toString(target.translation));
            System.out.println(Arrays.toString(target.translation));
            // target.translation[1];
            printWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Imgcodecs.imwrite(Dir.toString() + "/" + Instant.now().toString() + ".jpeg", yoloout);
        return false;
    }

    public void onExit() throws ExecutionException, InterruptedException {
    }

    public State nextState() {
        return null;
    }
}
