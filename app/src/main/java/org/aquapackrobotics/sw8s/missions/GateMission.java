package org.aquapackrobotics.sw8s.missions;

import java.io.File;
import java.time.Instant;
import java.util.concurrent.ExecutionException;

import org.aquapackrobotics.sw8s.comms.CameraFeedSender;
import org.aquapackrobotics.sw8s.comms.CommsThreadManager;
import org.aquapackrobotics.sw8s.states.State;
import org.aquapackrobotics.sw8s.vision.Gate;
import org.opencv.imgcodecs.Imgcodecs;

/**
 * Mission for navigating gates
 */
public class GateMission extends PathYUV {
    private Gate gateModel;
    private File Dir;

    public GateMission(CommsThreadManager manager, String missionName) {
        super(manager, missionName);
        CameraFeedSender.openCapture(1, missionName);
        Dir = new File("/mnt/data/" + missionName + "/gate");
        Dir.mkdirs();
    }

    @Override
    protected void executeState(State state) throws ExecutionException, InterruptedException {
        while (!state.onPeriodic()) {
            Gate gateModel = new Gate();
            Imgcodecs.imwrite(Dir.toString() + "/" + Instant.now().toString() + ".jpeg",
                    gateModel.detectYoloV5(CameraFeedSender.getFrame(0)));
        }
    }
}
