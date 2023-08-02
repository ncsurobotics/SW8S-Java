package org.aquapackrobotics.sw8s.states.BinStates;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledFuture;

import org.aquapackrobotics.sw8s.comms.CommsThreadManager;
import org.aquapackrobotics.sw8s.states.State;

public class BinInitState extends State {

    private ScheduledFuture<byte[]> depthRead;
    private String missionName;
    private double initialYaw;
    private final double MISSION_DEPTH;

    public BinInitState(CommsThreadManager manager, String missionName, double initialYaw, double MISSION_DEPTH) {
        super(manager);
        this.missionName = missionName;
        this.initialYaw = initialYaw;
        this.MISSION_DEPTH = MISSION_DEPTH;
    }

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

    public boolean onPeriodic() {
        try {
            System.out.println("CUR: " + String.valueOf(manager.getYaw()));
            System.out.println("Target: " + String.valueOf(initialYaw));
            if (depthRead.isDone()) {
                System.out.println("Depth: " + String.valueOf(manager.getDepth()));
                if (manager.getDepth() < -0.5) {
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
        return new BinPathState(manager, missionName, initialYaw, MISSION_DEPTH);
    }
}
