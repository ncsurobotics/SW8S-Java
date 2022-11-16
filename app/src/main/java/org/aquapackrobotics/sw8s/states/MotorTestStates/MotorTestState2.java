package org.aquapackrobotics.sw8s.states.MotorTestStates;

import org.aquapackrobotics.sw8s.comms.*;
import java.util.concurrent.*;
import org.aquapackrobotics.sw8s.states.*;

public class MotorTestState2 extends State {
    private long startTime;
    private long endTime;

    // Time in milliseconds
    private static long MOTOR_RUN_TIME = 500;
    private static long DELAY = 2000;

    public MotorTestState2(ControlBoardThreadManager manager) {
        super(manager);
    }

    public void onEnter() throws ExecutionException, InterruptedException{
        startTime = System.currentTimeMillis();
        manager.setMotorSpeeds(0,0.5,0,0,0,0,0,0);
    }

    public boolean onPeriodic() {
        endTime = System.currentTimeMillis();
        if (endTime - startTime >= MOTOR_RUN_TIME) {
            return true;
        }
        return false;
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
        return new MotorTestState3(manager);
    }
}
