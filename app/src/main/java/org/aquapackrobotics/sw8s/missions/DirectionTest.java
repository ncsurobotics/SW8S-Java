package org.aquapackrobotics.sw8s.missions;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import org.aquapackrobotics.sw8s.comms.ControlBoardMode;
import org.aquapackrobotics.sw8s.comms.ControlBoardThreadManager;
import org.aquapackrobotics.sw8s.states.State;
import org.aquapackrobotics.sw8s.states.DirectionalStates.StationaryInitState;

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
		ControlBoardThreadManager manager = new ControlBoardThreadManager(pool);
        manager.setMode(ControlBoardMode.RAW);
        manager.setThrusterInversions(true, true, false, false, true, false, false, true);
        while (state.onPeriodic()) {
            
        }
	}

	@Override
	protected State nextState(State state) {
		return state.nextState();
	}

}
