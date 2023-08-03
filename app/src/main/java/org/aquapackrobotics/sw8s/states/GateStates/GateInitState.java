package org.aquapackrobotics.sw8s.states.GateStates;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledFuture;

import org.aquapackrobotics.sw8s.comms.CommsThreadManager;
import org.aquapackrobotics.sw8s.states.State;

public class GateInitState extends State {

    private ScheduledFuture<byte[]> depthRead;
    private String missionName;
    private double initialYaw;
    private final double MISSION_DEPTH;

    private boolean spin;

    public GateInitState(CommsThreadManager manager, String missionName, double initialYaw, double MISSION_DEPTH) {
        super(manager);
        this.missionName = missionName;
        this.initialYaw = initialYaw;
        this.MISSION_DEPTH = MISSION_DEPTH;
        spin = false;
    }

    public void onEnter() throws ExecutionException, InterruptedException {
        try {
            var mreturn = manager.setStability2Speeds(0, 0, 0, 0, initialYaw,
                    MISSION_DEPTH);
            while (!mreturn.isDone())
                ;
            depthRead = manager.BNO055PeriodicRead((byte) 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean onPeriodic() {
        try {
            if (depthRead.isDone()) {
                if (manager.getDepth() > MISSION_DEPTH + 0.5) {
                    return true;
                }
            }

            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public GateInitState setSpin() {
        this.spin = true;
        return this;
    }

    public void onExit() throws ExecutionException, InterruptedException {
    }

    public State nextState() {
        if (spin)
            return new GateSpinState(manager, missionName, MISSION_DEPTH);
        return new GateFindState(manager, missionName, MISSION_DEPTH);
    }
}
