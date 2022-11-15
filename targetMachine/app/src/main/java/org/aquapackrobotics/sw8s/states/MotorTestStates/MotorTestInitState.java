package org.aquapackrobotics.sw8s.states.MotorTestStates;

import org.aquapackrobotics.sw8s.comms.*;
import org.aquapackrobotics.sw8s.states.*;
import java.util.concurrent.*;

public class MotorTestInitState extends State {

    public MotorTestInitState(ControlBoardThreadManager manager) {
        super(manager);
    }

    public void onEnter() throws ExecutionException, InterruptedException {
        manager.setMode(ControlBoardMode.RAW);
        manager.setThrusterInversions(true, true, false, false, true, false, false, true);
        manager.setMotorSpeeds(0,0,0,0,0,0,0,0);
    }


    public boolean onPeriodic() {
        return true;
    }

    public void onExit() throws ExecutionException, InterruptedException{
    }

    public State nextState() {
        return new MotorTestState(manager);
    }
}
