package org.aquapackrobotics.sw8s.states.BuoySpinStates;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledFuture;

import org.aquapackrobotics.sw8s.comms.CommsThreadManager;
import org.aquapackrobotics.sw8s.states.State;

public class BuoySpinInitState extends State {

    private ScheduledFuture<byte[]> depthRead;
    private String missionName;
    private double initialYaw;
    private final double MISSION_DEPTH;

    public BuoySpinInitState(CommsThreadManager manager, String missionName, double initialYaw, double MISSION_DEPTH) {
        super(manager);
        this.missionName = missionName;
        this.initialYaw = initialYaw;
        this.MISSION_DEPTH = MISSION_DEPTH;
    }

    public void onEnter() throws ExecutionException, InterruptedException {
        try {
            var mreturn = manager.setStability2Speeds(0, 0.8, 0, 0, initialYaw,
                    MISSION_DEPTH);
            System.out.println("MISSION DEPTH: " + MISSION_DEPTH);
            while (!mreturn.isDone())
                ;
            depthRead = manager.BNO055PeriodicRead((byte) 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean onPeriodic() {
        System.out.println("Initial yaw: " + initialYaw);
        System.out.println("Current yaw: " + manager.getYaw());
        try {
            if (depthRead.isDone()) {
                if (manager.getDepth() > MISSION_DEPTH + 0.5) {
                    Thread.sleep(15000);
                    // return true;
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
        return new BuoySpinState(manager, missionName, initialYaw, MISSION_DEPTH);
    }
}