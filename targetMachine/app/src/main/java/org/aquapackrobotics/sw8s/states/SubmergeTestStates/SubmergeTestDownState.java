package org.aquapackrobotics.sw8s.states.SubmergeTestStates;

import org.aquapackrobotics.sw8s.comms.*;
import org.aquapackrobotics.sw8s.states.*;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.*;

public class SubmergeTestDownState extends State {

    ControlBoardThreadManager manager;
    long startTime;
    long endTime;

    public SubmergeTestDownState(ScheduledThreadPoolExecutor pool) {
        super(pool);
        manager = new ControlBoardThreadManager(pool);
    }

    public void onEnter() throws ExecutionException, InterruptedException {
        manager.setMode(ControlBoardMode.LOCAL);
        manager.setThrusterInversions(true, true, false, false, true, false, false, true);
        manager.setLocalSpeeds(0, 0, -0.5, 0, 0, 0);
        startTime = System.currentTimeMillis();
    }


    public boolean onPeriodic() {
        endTime = System.currentTimeMillis();
        if (endTime - startTime >= 4000) {
            return false;
        }
        return true;
    }

    public void onExit() throws ExecutionException, InterruptedException{
        manager.setLocalSpeeds(0,0,0,0,0,0);
    }

    public State nextState() {
        return new SubmergeTestUpState(pool);
    }
}
