package org.aquapackrobotics.sw8s.states;


import org.aquapackrobotics.sw8s.states.*;
import org.aquapackrobotics.sw8s.comms.*;

import java.util.Arrays;

import java.util.concurrent.*;

public class PeriodicDepth extends State {
    ScheduledFuture<byte[]> depthRead;

    public PeriodicDepth(ControlBoardThreadManager manager) {
        super(manager);
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
            try {
                System.out.println("PeriodicDepth: " +
                    Arrays.toString(depthRead.get()));
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return true;
            }
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
