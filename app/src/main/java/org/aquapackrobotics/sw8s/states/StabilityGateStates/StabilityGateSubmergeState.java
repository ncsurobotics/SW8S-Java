package org.aquapackrobotics.sw8s.states.StabilityGateStates;

import org.aquapackrobotics.sw8s.comms.*;
import org.aquapackrobotics.sw8s.states.*;
import java.util.concurrent.*;
import java.util.Arrays;

import java.lang.Double;

public class StabilityGateSubmergeState extends State {

    ScheduledFuture<byte[]> depthRead;

    public StabilityGateSubmergeState(ControlBoardThreadManager manager) {
        super(manager);
    }

    public void onEnter() throws ExecutionException, InterruptedException {
        try {
            depthRead = manager.MSPeriodicRead((byte)1);
            //var mreturn = manager.setStability1Speeds(0, 0, 0, 0, 0, -1.5);
            var mreturn = manager.setStability2Speeds(0, 0, 0, 0, manager.getYaw(), -1.5);
            while (! mreturn.isDone());
            System.out.println("DONE");
            System.out.println(Arrays.toString(mreturn.get()));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }


    public boolean onPeriodic() {
        try {
            if ( depthRead.isDone() ) {
                if ( manager.getDepth() < -1.4 ) {
                    return true;
                }
            }

            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void onExit() throws ExecutionException, InterruptedException{
        //manager.setGlobalSpeeds(0, 0, 0, 0, 0, 0);
    }

    public State nextState() {
        return new StabilityGateHoldState(manager);
    }
}
