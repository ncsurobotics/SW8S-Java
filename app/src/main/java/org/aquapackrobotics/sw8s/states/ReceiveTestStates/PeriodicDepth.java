package org.aquapackrobotics.sw8s.states;


import org.aquapackrobotics.sw8s.states.*;
import org.aquapackrobotics.sw8s.comms.*;

import java.lang.Float;

import java.util.concurrent.*;

public class PeriodicDepth extends State {
    ScheduledFuture<byte[]> depthRead;
    int loop_runs;

    public PeriodicDepth(ControlBoardThreadManager manager) {
        super(manager);
        loop_runs = 0;
    }

    // TODO: implement
    public void onEnter() {
        try {
            depthRead = manager.MSPeriodicRead((byte)1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // TODO: implement
    public boolean onPeriodic() {
        if ( depthRead.isDone() ) {
            System.out.println("PeriodicDepth: " +
                Float.toString(manager.getDepth()));
            if (++loop_runs < 10) {
                return false;
            }
            return true;
        }
        return false;
    }

    // TODO: implement
    public void onExit() {
    }

    // TODO: implement
    public State nextState() {
        return new PeriodicGyro(manager);
    }
}
