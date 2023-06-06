package org.aquapackrobotics.sw8s.states.BuoyStates;

import org.aquapackrobotics.sw8s.comms.*;
import org.aquapackrobotics.sw8s.states.*;
import java.util.concurrent.*;

public class BuoyForwardState extends State {

    ControlBoardThreadManager manager;
    long startTime;
    long endTime;

    public BuoyForwardState(ControlBoardThreadManager manager) {
        super(manager);
    }

    public void onEnter() throws ExecutionException, InterruptedException {
        manager.setThrusterInversions(true, true, false, false, true, false, false, true);
        manager.setLocalSpeeds(0, .5, 0, 0, 0, 0);
        startTime = System.currentTimeMillis();
    }


    public boolean onPeriodic() {
        endTime = System.currentTimeMillis();
        if (endTime - startTime >= 2500) {
            return false;
        }
        return true;
    }

    public void onExit() throws ExecutionException, InterruptedException{
        manager.setLocalSpeeds(0, 0, 0, 0, 0, 0);
    }

    public State nextState() {
        return new BuoyChooseState(manager);
    }
}
