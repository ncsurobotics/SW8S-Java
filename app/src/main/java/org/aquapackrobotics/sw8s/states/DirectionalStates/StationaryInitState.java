package org.aquapackrobotics.sw8s.states.DirectionalStates;

import java.util.concurrent.ExecutionException;

import org.aquapackrobotics.sw8s.states.State;
import org.aquapackrobotics.sw8s.comms.*;

public class StationaryInitState extends State {

	public StationaryInitState(ControlBoardThreadManager manager) {
		super(manager);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onEnter() throws ExecutionException, InterruptedException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onPeriodic() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onExit() throws ExecutionException, InterruptedException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public State nextState() {
		// TODO Auto-generated method stub
		return null;
	}

}