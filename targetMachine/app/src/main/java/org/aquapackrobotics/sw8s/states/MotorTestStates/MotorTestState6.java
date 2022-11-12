package org.aquapackrobotics.sw8s.states.MotorTestStates;

import org.aquapackrobotics.sw8s.comms.*;
import java.util.concurrent.*;
import org.aquapackrobotics.sw8s.states.*;
import java.util.concurrent.ScheduledThreadPoolExecutor;

public class MotorTestState6 extends State {
    private long startTime;
    private long endTime;
    ControlBoardThreadManager manager;

    // Time in milliseconds
    private static long MOTOR_RUN_TIME = 1000;
    private static long DELAY = 2000;
    
    public MotorTestState6(ScheduledThreadPoolExecutor pool) {
        super(pool);
        manager = new ControlBoardThreadManager(pool);
    }

    public void onEnter() throws ExecutionException, InterruptedException{
        manager.setMode(ControlBoardMode.RAW);
        manager.setThrusterInversions(true, true, false, false, true, false, false, true);
        manager.setMotorSpeeds(0, 0, 0, 0, 0, 0.5, 0, 0);
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
        manager.setMotorSpeeds(0,0,0,0,0,0,0,0);
        startTime = System.currentTimeMillis();
        do {
            endTime = System.currentTimeMillis();
            if (endTime - startTime >= DELAY) {
                break;
            }
        } while(true);
    }

    public State nextState() {
        return new MotorTestState7(pool);
    }
}
