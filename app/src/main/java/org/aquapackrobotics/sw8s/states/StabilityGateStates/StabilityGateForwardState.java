package org.aquapackrobotics.sw8s.states.StabilityGateStates;

import org.aquapackrobotics.sw8s.comms.*;
import org.aquapackrobotics.sw8s.states.*;
import java.util.concurrent.*;
import java.util.Arrays;

public class StabilityGateForwardState extends State {

    long startTime;
    long endTime;

    public StabilityGateForwardState(ControlBoardThreadManager manager) {
        super(manager);
    }

    public void onEnter() throws ExecutionException, InterruptedException {
        startTime = System.currentTimeMillis();
        try {
            //var mreturn = manager.setStability1Speeds(0, 0.5, -0.05, 0, 0, -1.5);
            var mreturn = manager.setStability2Speeds(0, 0.5, 0, 0, 0, -1.5);
            while (! mreturn.isDone());
            System.out.println("DONE");
            System.out.println(Arrays.toString(mreturn.get()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public boolean onPeriodic() {
        double ySpeed = 0;
        try {
            endTime = System.currentTimeMillis();
            if (endTime - startTime >= 15000) {
                return true;
            }

            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void onExit() throws ExecutionException, InterruptedException{
    }

    public State nextState() {
        return new StabilityGateSpinState(manager);
    }
}
