package org.aquapackrobotics.sw8s.states.StabilityGateStates;

import org.aquapackrobotics.sw8s.comms.*;
import org.aquapackrobotics.sw8s.states.*;
import java.util.concurrent.*;
import java.util.Arrays;

public class StabilityGateForwardState extends State {

    private long startTime;
    private long endTime;
    private double yaw;

    private long midTime;

    public StabilityGateForwardState(ControlBoardThreadManager manager, double yaw) {
        super(manager);
        this.yaw = yaw;
    }

    public void onEnter() throws ExecutionException, InterruptedException {
        startTime = System.currentTimeMillis();
        midTime = startTime;
        try {
            var mreturn = manager.setStability2Speeds(0, 0.5, 0, 0, yaw, -2.1);
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
            /* 10 seconds */
            if (endTime - startTime >= 10 * 1000) {
                return true;
            }
            if (endTime - midTime >= 1000) {
                //yaw += -(yaw / Math.abs(yaw)) * 0.01;
                midTime = endTime;
                var mreturn = manager.setStability2Speeds(0, 0.5, 0, 0, yaw, -2.1);
                while (! mreturn.isDone());
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
        //return new StabilityGateSpinState(manager);
        return null;
    }
}
