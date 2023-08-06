package org.aquapackrobotics.sw8s.missions;

import java.util.concurrent.ExecutionException;

import org.aquapackrobotics.sw8s.comms.Camera;
import org.aquapackrobotics.sw8s.comms.CommsThreadManager;
import org.aquapackrobotics.sw8s.comms.Linux;
import org.aquapackrobotics.sw8s.states.State;
import org.aquapackrobotics.sw8s.states.BuoySpinStates.BuoySpinInitState;

public class BuoySpin extends Mission {
    private static final double MISSION_DEPTH = -0.1;

    private String missionName;
    private double initialYaw;

    public BuoySpin(CommsThreadManager manager, String missionName) {
        super(manager);
        this.missionName = missionName;
        try {
            Linux.changeExposure(Camera.FRONT, 13);
            var mreturn = manager.BNO055PeriodicRead((byte) 1);
            while (!mreturn.isDone())
                ;
            Thread.sleep(2000); // Give sensor time to get itself ready
            // this.initialYaw = manager.quatCalculatedBN055Read().get()[6];
            this.initialYaw = manager.getYaw();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(10);
        }
    }

    @Override
    protected State initialState() {
        return new BuoySpinInitState(manager, missionName, initialYaw, MISSION_DEPTH);
    }

    @Override
    protected void executeState(State state) throws ExecutionException, InterruptedException {
        while (!state.onPeriodic()) {
        }
    }

    @Override
    protected State nextState(State state) {
        return state.nextState();
    }
}