package org.aquapackrobotics.sw8s.missions;

import java.util.concurrent.ExecutionException;

import org.aquapackrobotics.sw8s.comms.CommsThreadManager;
import org.aquapackrobotics.sw8s.states.State;
import org.aquapackrobotics.sw8s.states.BuoyStates.BuoyInitState;

public class Buoys extends Mission {
    private static final double MISSION_DEPTH = -2.7;

    String missionName;

    public Buoys(CommsThreadManager manager, String missionName) {
        super(manager);
        this.missionName = missionName;
    }

    @Override
    protected State initialState() {
        return new BuoyInitState(manager, missionName, MISSION_DEPTH);
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
