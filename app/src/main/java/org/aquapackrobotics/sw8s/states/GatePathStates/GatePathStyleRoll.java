package org.aquapackrobotics.sw8s.states.GatePathStates;

import org.aquapackrobotics.sw8s.comms.Camera;
import org.aquapackrobotics.sw8s.comms.CameraFeedSender;
import org.aquapackrobotics.sw8s.comms.CommsThreadManager;
import org.aquapackrobotics.sw8s.states.State;
import org.aquapackrobotics.sw8s.vision.GatePoles;
import org.opencv.core.Mat;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledFuture;

public class GatePathStyleRoll extends State {

    private ScheduledFuture<byte[]> depthRead;
    private String missionName;
    private double initialYaw;
    private double prevTime;
    private final double MISSION_DEPTH;

    private GatePoles gatePoles;

    /**
     * Creates a state instance.
     * <p>
     * Do not preserve State instances, create new ones when a task repeats.
     *
     * @param manager the Missions' manager for task submission
     */
    public GatePathStyleRoll(CommsThreadManager manager, String missionName, double initialYaw,
                             double MISSION_DEPTH) {
        super(manager);
        this.missionName = missionName;
        this.initialYaw = initialYaw;
        this.prevTime = System.currentTimeMillis();
        this.MISSION_DEPTH = MISSION_DEPTH;
        gatePoles = new GatePoles(true);
    }

    @Override
    public void onEnter() throws ExecutionException, InterruptedException {
        try {
            depthRead = manager.MSPeriodicRead((byte) 1);
            var mreturn = manager.setStability2Speeds(0, 0, 0, 0, initialYaw,
                    MISSION_DEPTH);
            while (!mreturn.isDone())
                ;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onPeriodic() throws ExecutionException, InterruptedException {
        int gateDetects = 0;
        Mat frame = CameraFeedSender.getFrame(Camera.FRONT);
        Mat yoloout = gatePoles.detectYoloV5(frame);
        if (gatePoles.detected()) {

        }
        if (gateDetects == 2) {
            return true;
        }
        return false;
    }

    @Override
    public void onExit() throws ExecutionException, InterruptedException {

    }

    @Override
    public State nextState() {
        return null;
    }
}
