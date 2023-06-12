package org.aquapackrobotics.sw8s.states.StabilityGateStates;

import org.aquapackrobotics.sw8s.comms.*;
import org.aquapackrobotics.sw8s.states.*;
import java.util.concurrent.*;
import java.util.Arrays;

public class StabilityGateHoldState extends State {

    ScheduledFuture<byte[]> depthRead;

    // in milliseconds
    private static long DELAY = 2000;
    private long startTime;
    double yaw;

    public StabilityGateHoldState(ControlBoardThreadManager manager, double yaw) {
        super(manager);
        this.yaw = yaw;
    }

    public void onEnter() throws ExecutionException, InterruptedException {
        try {
            //var mreturn = manager.setStability1Speeds(0, 0, 0, 0, 0, -1.5);
            var mreturn = manager.setStability2Speeds(0, 0, 0, 0, yaw, -2.1);
            while (! mreturn.isDone());
            startTime = System.currentTimeMillis();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }


    public boolean onPeriodic() {
        try {
            if (System.currentTimeMillis() - startTime >= DELAY) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }

    public void onExit() throws ExecutionException, InterruptedException{
        //manager.setGlobalSpeeds(0, 0, 0, 0, 0, 0);
    }

    public State nextState() {
        return new StabilityGateForwardState(manager, yaw);
    }
}
