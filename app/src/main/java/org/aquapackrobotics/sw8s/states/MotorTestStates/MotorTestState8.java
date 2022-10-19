package org.aquapackrobotics.sw8s.states;

import org.aquapackrobotics.sw8s.comms.*;
import java.util.concurrent.*;
import org.aquapackrobotics.sw8s.states.*;
import java.util.concurrent.ScheduledThreadPoolExecutor;

public class MotorTestState8 extends State {
    private long startTime;
    private long endTime;
    ControlBoardThreadManager manager;

    public MotorTestState8(ScheduledThreadPoolExecutor pool) {
        super(pool);
        manager = new ControlBoardThreadManager(pool);
    }

    // TODO: implement
    public void onEnter() throws ExecutionException, InterruptedException{
        manager.setMode(ControlBoardMode.RAW);
        manager.setThrusterInversions(true, true, false, false, true, false, false, true);
        manager.setMotorSpeeds(0, 0, 0, 0, 0, 0, 0, 0.8);
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
    public void onExit() throws ExecutionException, InterruptedException{
        manager.setMotorSpeeds(0,0,0,0,0,0,0,0);
    }

    // TODO: implement
    public State nextState() {
        return null;
    }
}
