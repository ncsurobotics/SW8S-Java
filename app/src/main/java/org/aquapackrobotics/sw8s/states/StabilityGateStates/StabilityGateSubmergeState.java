package org.aquapackrobotics.sw8s.states.StabilityGateStates;

import org.aquapackrobotics.sw8s.comms.*;
import org.aquapackrobotics.sw8s.states.*;
import java.util.concurrent.*;

public class StabilityGateSubmergeState extends State {

    ScheduledFuture<byte[]> depthRead;

    public StabilityGateSubmergeState(ControlBoardThreadManager manager) {
        super(manager);
    }

    public void onEnter() throws ExecutionException, InterruptedException {
        try {
            depthRead = manager.MSPeriodicRead((byte)1);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }


    public boolean onPeriodic() {
        try {
            manager.setStability1Speeds(0, 0, 0, 0, 0, 1);
            if ( depthRead.isDone() ) {
                if ( manager.getDepth() > -1.0 ) {
                    return false;
                } else {
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
        manager.setGlobalSpeeds(0, 0, 0, 0, 0, 0);
    }

    public State nextState() {
        return new StabilityGateForwardState(manager);
    }
}
