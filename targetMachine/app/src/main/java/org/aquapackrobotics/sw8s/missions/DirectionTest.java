package org.aquapackrobotics.sw8s.missions;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import org.aquapackrobotics.sw8s.states.State;
import org.aquapackrobotics.sw8s.states.DirectionalStates.*;

/**
 * State machine to test directional movement. "n" to advance to next state.
 */
public class DirectionTest extends Mission {

	public DirectionTest(ScheduledThreadPoolExecutor pool) {
		super(pool);
	}

	@Override
	protected State initialState() {
		return new StationaryInitState(pool);
	}

	@Override
	protected void executeState(State state) throws ExecutionException, InterruptedException {
        while (state.onPeriodic()) {
            
        }
	}

	@Override
	protected State nextState(State state) {
		return state.nextState();
	}

}
