package org.aquapackrobotics.sw8s.states.GatePathStates;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledFuture;

import org.aquapackrobotics.sw8s.comms.CommsThreadManager;
import org.aquapackrobotics.sw8s.states.State;

public class GatePathSubmergeState extends State {

    private ScheduledFuture<byte[]> depthRead;
    private String missionName;
    private double initialYaw;
    private double prevTime;
    private final double MISSION_DEPTH;

    public GatePathSubmergeState(CommsThreadManager manager, String missionName, double initialYaw,
            double MISSION_DEPTH) {
        super(manager);
        this.missionName = missionName;
        this.initialYaw = initialYaw;
        this.prevTime = System.currentTimeMillis();
        this.MISSION_DEPTH = MISSION_DEPTH;
    }

    public void onEnter() throws ExecutionException, InterruptedException {
        try {
            depthRead = manager.MSPeriodicRead((byte) 1);
            var mreturn = manager.setStability2Speeds(0, 0, 0, 0, initialYaw,
                    MISSION_DEPTH);
            while (!mreturn.isDone())
                ;
            Thread.sleep(2000); // Make rotation obvious to judges
            var mreturn2 = manager.setStability2Speeds(0, 0, 30, 0, initialYaw,
                    MISSION_DEPTH);
            while (!mreturn2.isDone())
                ;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean onPeriodic() {
        try {
            if (System.currentTimeMillis() - this.prevTime > 100) {
                this.prevTime = System.currentTimeMillis();
                System.out.println("Depth: " + String.valueOf(manager.getDepth()));
                System.out.println("Current Angle: " + String.valueOf(manager.getYaw()));
                System.out.println("Target Angle: " + String.valueOf(initialYaw));
            }
            if (depthRead.isDone()) {
                if (manager.getDepth() < (MISSION_DEPTH + 0.5) && (Math.abs(manager.getYaw() - initialYaw) < 5)) {
                    Thread.sleep(2000); // sleep two seconds
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
        return new GatePathDetectState(manager, missionName, initialYaw, MISSION_DEPTH);
    }
}
