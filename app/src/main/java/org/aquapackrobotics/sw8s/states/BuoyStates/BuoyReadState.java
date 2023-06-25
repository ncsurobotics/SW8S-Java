package org.aquapackrobotics.sw8s.states.BuoyStates;

import java.io.File;
import java.time.Instant;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledFuture;

import org.aquapackrobotics.sw8s.comms.CameraFeedSender;
import org.aquapackrobotics.sw8s.comms.ControlBoardThreadManager;
import org.aquapackrobotics.sw8s.states.State;
import org.aquapackrobotics.sw8s.vision.Buoy;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.videoio.VideoCapture;

public class BuoyReadState extends State {

    private ScheduledFuture<byte[]> depthRead;
    private final VideoCapture cap;
    private final Buoy target;
    private final File Dir;

    public BuoyReadState(ControlBoardThreadManager manager) {
        super(manager);
        this.cap = CameraFeedSender.openCapture(1);
        target = new Buoy();
        Dir = new File(new File(System.getProperty("java.io.tmpdir")), "buoy");
        Dir.mkdir();
    }

    public void onEnter() throws ExecutionException, InterruptedException {
        try {
            depthRead = manager.MSPeriodicRead((byte) 1);
            var mreturn = manager.setStability2Speeds(0, 0, 0, 0, manager.getYaw(), -1.0);
            while (!mreturn.isDone())
                ;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean onPeriodic() {
        Mat frame = new Mat();
        if (cap.read(frame)) {
            Mat yoloout = target.detectYoloV5(frame);
            Imgcodecs.imwrite(Dir.toString() + Instant.now().toString() + ".jpeg", yoloout);
        }
        return true;
    }

    public void onExit() throws ExecutionException, InterruptedException {
    }

    public State nextState() {
        return null;
    }
}
