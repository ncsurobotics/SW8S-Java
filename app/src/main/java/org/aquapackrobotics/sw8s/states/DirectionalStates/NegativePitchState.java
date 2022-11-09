package org.aquapackrobotics.sw8s.states.DirectionalStates;

import org.aquapackrobotics.sw8s.comms.*;
import org.aquapackrobotics.sw8s.states.*;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.*;

public class NegativePitchState extends State {

    ControlBoardThreadManager manager;
    private long startTime;
    private long endTime;

    private static final long MOTOR_RUN_TIME = 2000;
    private static final long DELAY = 2000;

    public NegativePitchState(ScheduledThreadPoolExecutor pool) {
        super(pool);
        manager = new ControlBoardThreadManager(pool);
    }

    public void onEnter() throws ExecutionException, InterruptedException {
        manager.setMode(ControlBoardMode.LOCAL);
        manager.setThrusterInversions(true, true, false, false, true, false, false, true);
        manager.setLocalSpeeds(0, 0, 0, -0.5, 0, 0);
        startTime = System.currentTimeMillis();
    }



    public boolean onPeriodic() {
        endTime = System.currentTimeMillis();
        if (endTime - startTime >= MOTOR_RUN_TIME) {
            return false;
        }
        return true;
    }

    public void onExit() throws ExecutionException, InterruptedException{
        manager.setLocalSpeeds(0,0,0,0,0,0);
        startTime = System.currentTimeMillis();
        do {
            endTime = System.currentTimeMillis();
            if (endTime - startTime >= DELAY) {
                break;
            }
        } while(true);
    }

    public State nextState() {
        return new PositiveRollState(pool);
    }
}
