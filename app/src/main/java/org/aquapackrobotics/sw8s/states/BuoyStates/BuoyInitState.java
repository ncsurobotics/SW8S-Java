package org.aquapackrobotics.sw8s.states.BuoyStates;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledFuture;

import org.aquapackrobotics.sw8s.comms.CommsThreadManager;
import org.aquapackrobotics.sw8s.states.State;

public class BuoyInitState extends State {

    private ScheduledFuture<byte[]> depthRead;
    private String missionName;
    private final double MISSION_DEPTH;

    public BuoyInitState(CommsThreadManager manager, String missionName, double MISSION_DEPTH) {
        super(manager);
        this.missionName = missionName;
        this.MISSION_DEPTH = MISSION_DEPTH;
    }

    public void onEnter() throws ExecutionException, InterruptedException {
        try {
            var mreturn = manager.setStability2Speeds(0, 0, 0, 0, manager.getYaw(),
                    MISSION_DEPTH);
            while (!mreturn.isDone())
                ;
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

    public void onExit() throws ExecutionException, InterruptedException {
    }

    public State nextState() {
        // return new BuoyReadState(manager);
        return new BuoyForwardState(manager, missionName, MISSION_DEPTH);
    }
}
