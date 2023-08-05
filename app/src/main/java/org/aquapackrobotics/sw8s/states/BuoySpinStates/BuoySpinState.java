package org.aquapackrobotics.sw8s.states.BuoySpinStates;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledFuture;

import org.aquapackrobotics.sw8s.comms.CommsThreadManager;
import org.aquapackrobotics.sw8s.states.State;

public class BuoySpinState extends State {

    private ScheduledFuture<byte[]> rotRead;
    private String missionName;
    private double initialYaw;
    private final double MISSION_DEPTH;
    private int rollCount = 0;

    public BuoySpinState(CommsThreadManager manager, String missionName, double initialYaw,
            double MISSION_DEPTH) {
        super(manager);
        this.missionName = missionName;
        this.initialYaw = initialYaw;
        this.MISSION_DEPTH = MISSION_DEPTH;
    }

    public void onEnter() throws ExecutionException, InterruptedException {
        try {
            rotRead = manager.BNO055PeriodicRead((byte) 1);
            var mreturn = manager.setDepthHold(0, 0.8, 0, 0.8, 0,
                    MISSION_DEPTH);
            while (!mreturn.isDone())
                ;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean onPeriodic() {
        try {
            if (rotRead.isDone()) {
                if (manager.getGyro()[5] > 350) {
                    ++rollCount;
                    Thread.sleep(200);
                }
                if (rollCount >= 2) {
                    return true;
                }
            }

            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void onExit() throws ExecutionException, InterruptedException {
    }

    public State nextState() {
        return new BuoySpinForwardState(manager, missionName, initialYaw, MISSION_DEPTH);
    }
}
