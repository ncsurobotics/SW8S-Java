package org.aquapackrobotics.sw8s.states.MotorTestStates;

import org.aquapackrobotics.sw8s.comms.*;
import java.util.concurrent.*;
import org.aquapackrobotics.sw8s.states.*;
import java.util.concurrent.ScheduledThreadPoolExecutor;

public class MotorTestState3 extends State {
    private long startTime;
    private long endTime;
    ControlBoardThreadManager manager;

    public MotorTestState3(ScheduledThreadPoolExecutor pool) {
        super(pool);
        manager = new ControlBoardThreadManager(pool);
    }

    public void onEnter() throws ExecutionException, InterruptedException{
        manager.setMode(ControlBoardMode.RAW);
        manager.setThrusterInversions(true, true, false, false, true, false, false, true);
        manager.setMotorSpeeds(0, 0, 0.5, 0, 0, 0, 0, 0);
        startTime = System.currentTimeMillis();
    }

    public boolean onPeriodic() {
        endTime = System.currentTimeMillis();
        if (endTime - startTime >= 1000) {
            return false;
        }
        return true;
    }

    public void onExit() throws ExecutionException, InterruptedException{
        manager.setMotorSpeeds(0,0,0,0,0,0,0,0);
    }

    public State nextState() {
        return new MotorTestState4(pool);
    }
}
