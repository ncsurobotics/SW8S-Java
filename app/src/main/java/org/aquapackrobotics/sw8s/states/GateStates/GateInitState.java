package org.aquapackrobotics.sw8s.states.GateStates;

import org.aquapackrobotics.sw8s.comms.*;
import org.aquapackrobotics.sw8s.states.*;
import java.util.concurrent.*;

public class GateInitState extends State {

    ControlBoardThreadManager manager;

    public GateInitState(ControlBoardThreadManager manager) {
        super(manager);
    }

    public void onEnter() throws ExecutionException, InterruptedException {
        manager.setMode(ControlBoardMode.RAW);
        manager.setThrusterInversions(true, true, false, false, true, false, false, true);
        manager.setMotorSpeeds(0,0,0,0,0,0,0,0);
    }


    public boolean onPeriodic() {
        return false;
    }

    public void onExit() throws ExecutionException, InterruptedException{
    }

    public State nextState() {
        return new GateSubmergeState(manager);
    }
}
