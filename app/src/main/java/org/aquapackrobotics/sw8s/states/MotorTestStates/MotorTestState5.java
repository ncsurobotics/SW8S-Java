package org.aquapackrobotics.sw8s.states;

import org.aquapackrobotics.sw8s.comms.ControlBoardThreadManager;
import java.util.concurrent.ScheduledThreadPoolExecutor;

public class MotorTestState5 extends State {
    public MotorTestState5(ScheduledThreadPoolExecutor pool, ControlBoardThreadManager manager) {
        super(pool, manager);
        long startTime;
        long endTime;
    }

    // TODO: implement
    public void onEnter() {
        manager.setMotorSpeeds(0, 0, 0, 0, 0.5, 0, 0, 0);
        startTime = System.currentTimeMillis();
    }

    // TODO: implement
    public boolean onPeriodic() {
        endTime = System.currentTimeMillis();
        if (endTime - startTime >= 1000) {
            return false;
        }
        return true;
    }

    // TODO: implement
    public void onExit() {
        manager.setMotorSpeeds(0,0,0,0,0,0,0,0);
    }

    // TODO: implement
    public State nextState() {
        return new MotorTestState6(pool, manager);
    }
}
