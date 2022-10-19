package org.aquapackrobotics.sw8s.states;

import org.aquapackrobotics.sw8s.comms.*;
import org.aquapackrobotics.sw8s.states.*;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.*;

public class MotorTestInitState extends State {

    ControlBoardThreadManager manager;

    public MotorTestInitState(ScheduledThreadPoolExecutor pool) {
        super(pool);
        manager = new ControlBoardThreadManager(pool);
    }

    // TODO: implement
    public void onEnter() throws ExecutionException, InterruptedException {
        manager.setMode(ControlBoardMode.RAW);
        manager.setThrusterInversions(true, true, false, false, true, false, false, true);
    }

    // TODO: implement
    public boolean onPeriodic() {
        return false;
    }

    // TODO: implement
    public void onExit() throws ExecutionException, InterruptedException{
        manager.setMotorSpeeds(0,0,0,0,0,0,0,0);
    }

    // TODO: implement
    public State nextState() {
        return new MotorTestState1(pool);
    }
}
