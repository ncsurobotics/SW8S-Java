package org.aquapackrobotics.sw8s.missions;

import java.util.concurrent.ScheduledThreadPoolExecutor;

import org.aquapackrobotics.sw8s.missions.Mission;

/**
 * Competition mission, fully autonomous.
 */
public class AutoMission extends Mission {

    public AutoMission(ScheduledThreadPoolExecutor pool) {
        super(pool);
    }

    // TODO: implement
    @Override
    protected Object initialState() {
        return null;
    }

    // TODO: implement
    @Override
    protected void executeState(Object state) {
    }

    // TODO: implement
    @Override
    protected Object nextState(Object state) {
        return null;
    }
}
