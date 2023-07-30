package org.aquapackrobotics.sw8s.states.SubmergeTestStates;

import org.aquapackrobotics.sw8s.comms.*;
import org.aquapackrobotics.sw8s.states.*;
import java.util.concurrent.*;

public class SubmergeTestUpState extends State {

    CommsThreadManager manager;
    long startTime;
    long endTime;

    public SubmergeTestUpState(CommsThreadManager manager) {
        super(manager);
    }

    public void onEnter() throws ExecutionException, InterruptedException {
        manager.setThrusterInversions(true, true, false, false, true, false, false, true);
        manager.setLocalSpeeds(0, 0, 0.5, 0, 0, 0);
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
        return null;
    }
}
