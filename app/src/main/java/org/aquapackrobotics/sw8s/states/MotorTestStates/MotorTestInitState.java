package org.aquapackrobotics.sw8s.states;

import org.aquapackrobotics.sw8s.comms.*;
import java.util.concurrent.ScheduledThreadPoolExecutor;

public class MotorTestInitState extends State {
    public MotorTestInitState(ScheduledThreadPoolExecutor pool) {
        super(pool);
        ControlBoardThreadManager manager = new ControlBoardThreadManager(pool);
        manager.setMode(ControlBoardMode.RAW);
        manager.setThrusterInversions(true, true, false, false, true, false, false, true);
    }

    // TODO: implement
    public void onEnter() {
    }

    // TODO: implement
    public boolean onPeriodic() {
        return false;
    }

    // TODO: implement
    public void onExit() {
        manager.setMotorSpeeds(0,0,0,0,0,0,0,0);
    }

    // TODO: implement
    public State nextState() {
        return new MotorTestState1(pool, manager);
    }
}
