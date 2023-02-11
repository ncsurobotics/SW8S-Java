package org.aquapackrobotics.sw8s.states.GateStates;

import org.aquapackrobotics.sw8s.comms.*;
import org.aquapackrobotics.sw8s.states.*;
import java.util.concurrent.*;

public class GateSpinState extends State {

    long startTime;
    long endTime;
    boolean recoverDepth;
    ScheduledFuture<byte[]> depthRead;

    public GateSpinState(ControlBoardThreadManager manager) {
        super(manager);
    }

    public void onEnter() throws ExecutionException, InterruptedException {
        try {
            depthRead = manager.MSPeriodicRead((byte)1);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        startTime = System.currentTimeMillis();
        recoverDepth = false;
    }


    public boolean onPeriodic() {
        double ySpeed = 0;
        try {
            if ( depthRead.isDone() ) {
                if ( manager.getDepth() > -0.5 ) {
                    recoverDepth = true;
                }
                if ( recoverDepth && manager.getDepth() > -1.0 ) {
                    ySpeed = -0.4;
                } else {
                    recoverDepth = false;
                }
            }

            endTime = System.currentTimeMillis();
            if (endTime - startTime >= 10000) {
                return true;
            }

            manager.setGlobalSpeeds(0, 0, ySpeed, 0, 0, 0.3);
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
        return null;
    }
}
