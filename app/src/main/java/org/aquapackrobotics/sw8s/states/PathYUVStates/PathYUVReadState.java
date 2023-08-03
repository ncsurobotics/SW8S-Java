package org.aquapackrobotics.sw8s.states.PathYUVStates;

import java.io.File;
import java.time.Instant;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledFuture;

import org.aquapackrobotics.sw8s.comms.Camera;
import org.aquapackrobotics.sw8s.comms.CameraFeedSender;
import org.aquapackrobotics.sw8s.comms.CommsThreadManager;
import org.aquapackrobotics.sw8s.states.State;
import org.aquapackrobotics.sw8s.vision.PathYUV;
import org.opencv.core.Mat;

public class PathYUVReadState extends State {

    private ScheduledFuture<byte[]> depthRead;
    private final File Dir;
    private final double MISSION_DEPTH;
    // private final double[] candidates = { 0.1, 0.15, 0.2, 0.25, 0.3, 0.35, 0.4,
    // 0.45, 0.5 };
    // private File[] cand_files = new File[9];
    // private final double MISSION_DEPTH;

    public PathYUVReadState(CommsThreadManager manager, double MISSION_DEPTH) {
        super(manager);
        CameraFeedSender.openCapture(Camera.BOTTOM);
        Dir = new File(new File(System.getProperty("java.io.tmpdir")), "path");
        Dir.mkdir();
        this.MISSION_DEPTH = MISSION_DEPTH;
        // for (int i = 0; i < candidates.length; i++) {
        // cand_files[i] = new File(Dir, "/" + String.valueOf(i));
        // cand_files[i].mkdir();
        // }
        // this.MISSION_DEPTH = MISSION_DEPTH;
    }

    public void onEnter() throws ExecutionException, InterruptedException {
        try {
            depthRead = manager.MSPeriodicRead((byte) 1);
            var mreturn = manager.setStability2Speeds(0, 0, 0, 0, manager.getYaw(),
                    MISSION_DEPTH);
            while (!mreturn.isDone())
                ;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean onPeriodic() {
        Mat frame = CameraFeedSender.getFrame(Camera.BOTTOM);
        // for (int i = 0; i < candidates.length; i++) {
        // PathYUV target = new PathYUV(candidates[i]);
        PathYUV target = new PathYUV(0.25);
        // target.processFrame(frame, cand_files[i].toString() + "/" +
        target.processFrame(frame, Dir.toString() + "/" + Instant.now().toString());
        try {
            System.out.println(target.relativePosition(frame));
        } catch (Exception e) {
        }
        // }
        return false;

    }

    public void onExit() throws ExecutionException, InterruptedException {
    }

    public State nextState() {
        return null;
    }
}
