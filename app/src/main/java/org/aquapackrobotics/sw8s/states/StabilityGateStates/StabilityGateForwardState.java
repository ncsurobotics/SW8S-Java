package org.aquapackrobotics.sw8s.states.StabilityGateStates;

import org.aquapackrobotics.sw8s.comms.*;
import org.aquapackrobotics.sw8s.states.*;
import java.util.concurrent.*;

public class StabilityGateForwardState extends State {

    long startTime;
    long endTime;

    public StabilityGateForwardState(ControlBoardThreadManager manager) {
        super(manager);
    }

    public void onEnter() throws ExecutionException, InterruptedException {
        startTime = System.currentTimeMillis();
    }


    public boolean onPeriodic() {
        double ySpeed = 0;
        try {
            endTime = System.currentTimeMillis();
            if (endTime - startTime >= 10000) {
                return true;
            }

            manager.setStability1Speeds(0.3, 0, 0, 0, 0, 1);
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void onExit() throws ExecutionException, InterruptedException{
        manager.setGlobalSpeeds(0, 0, 0, 0, 0, 0);
    }

    public State nextState() {
        return new StabilityGateSpinState(manager);
    }
}
