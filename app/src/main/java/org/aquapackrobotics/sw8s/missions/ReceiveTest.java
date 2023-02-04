package org.aquapackrobotics.sw8s.missions;

import org.aquapackrobotics.sw8s.comms.*;

import org.aquapackrobotics.sw8s.states.*;
import org.aquapackrobotics.sw8s.comms.*;

import java.util.Scanner;

import java.util.concurrent.*;

public class ReceiveTest extends Mission {
    public ReceiveTest(ControlBoardThreadManager manager) {
        super(manager);
    }

    // TODO: implement
    @Override
    protected State initialState() {
        return new Depth(manager);
    }

    // TODO: implement
    @Override
    protected void executeState(State state) throws ExecutionException, InterruptedException  {
        while ( !state.onPeriodic() );
    }

    // TODO: implement
    @Override
    protected State nextState(State state) {
        return state.nextState();
    }
}
