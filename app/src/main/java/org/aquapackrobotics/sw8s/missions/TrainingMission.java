package org.aquapackrobotics.sw8s.missions;

import java.util.concurrent.ScheduledThreadPoolExecutor;

import org.aquapackrobotics.sw8s.states.InitState;
import org.aquapackrobotics.sw8s.states.State;
import org.aquapackrobotics.sw8s.trainingsim.SimWindow;

/**
 * Competition mission, fully autonomous.
 */
public class TrainingMission extends Mission {
     SimWindow window;

    public TrainingMission(ScheduledThreadPoolExecutor pool) {
        super(pool);
        window = new SimWindow();
    }

    // TODO: implement
    @Override
    protected State initialState() {
        return new InitState(pool, window);
    }

    // TODO: implement
    @Override
    protected void executeState(State state) {
        state.onEnter();
        while (!state.onPeriodic()){}
        state.onExit();
    }

    // TODO: implement
    @Override
    protected State nextState(State state) {
        return state.nextState();
    }
}
