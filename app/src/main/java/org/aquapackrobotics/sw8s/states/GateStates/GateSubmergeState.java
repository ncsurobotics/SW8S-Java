package org.aquapackrobotics.sw8s.states.GateStates;

import org.aquapackrobotics.sw8s.comms.*;
import org.aquapackrobotics.sw8s.states.*;
import java.util.concurrent.*;

public class GateSubmergeState extends State {

    ScheduledFuture<byte[]> depthRead;

    public GateSubmergeState(ControlBoardThreadManager manager) {
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
            if ( depthRead.isDone() ) {
                if ( manager.getDepth() > -1.0 ) {
                    manager.setGlobalSpeeds(0, 0, -0.4, 0, 0, 0);
                    return false;
                } else {
                    manager.setGlobalSpeeds(0, 0, 0, 0, 0, 0);
                    return true;
                }
            }

            manager.setGlobalSpeeds(0, 0, 0, 0, 0, 0);
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
        return new GateForwardState(manager);
    }
}
