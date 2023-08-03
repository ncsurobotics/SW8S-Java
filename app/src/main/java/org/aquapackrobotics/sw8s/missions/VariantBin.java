package org.aquapackrobotics.sw8s.missions;

import java.util.concurrent.ExecutionException;

import org.aquapackrobotics.sw8s.comms.CommsThreadManager;
import org.aquapackrobotics.sw8s.states.State;
import org.aquapackrobotics.sw8s.states.BinStates.BinInitState;
import org.aquapackrobotics.sw8s.states.BinStates.BinTargetState;

public class VariantBin extends Mission {
    private static final double MISSION_DEPTH = -1.0;

    String missionName;
    private double initialYaw;

    public VariantBin(CommsThreadManager manager, String missionName) {
        super(manager);
        this.manager = manager;
        this.missionName = missionName;
        try {
            var mreturn = manager.BNO055PeriodicRead((byte) 1);
            while (!mreturn.isDone())
                ;
            Thread.sleep(500); // Give sensor time to get itself ready
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.initialYaw = manager.getYaw();

    }

    @Override
    protected State initialState() {
        return new BinInitState(manager, missionName, initialYaw, MISSION_DEPTH);
    }

    @Override
    protected void executeState(State state) throws ExecutionException, InterruptedException {
        while (!state.onPeriodic()) {
        }
    }

    @Override
    protected State nextState(State state) {
        return null;
    }
}
