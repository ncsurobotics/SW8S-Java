package org.aquapackrobotics.sw8s.states;


import org.aquapackrobotics.sw8s.states.*;
import org.aquapackrobotics.sw8s.comms.*;

import java.util.Arrays;

import java.util.concurrent.*;

public class PeriodicGyro extends State {
    ScheduledFuture<byte[]> gyroRead;
    int loop_runs;

    public PeriodicGyro(ControlBoardThreadManager manager) {
        super(manager);
        loop_runs = 0;
    }

    // TODO: implement
    public void onEnter() {
        try {
            gyroRead = manager.BNO055PeriodicRead((byte)1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // TODO: implement
    public boolean onPeriodic() {
        if ( gyroRead.isDone() ) {
            System.out.println("PeriodicGyro X: " +
                Float.toString(manager.getGyrox()));
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
        return null;
    }
}
