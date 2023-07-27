package org.aquapackrobotics.sw8s.states.ReceiveTestStates;

import org.aquapackrobotics.sw8s.states.*;
import org.aquapackrobotics.sw8s.comms.*;

import java.util.Arrays;

import java.util.concurrent.*;

public class Depth extends State {
    ScheduledFuture<Float> depthRead;

    public Depth(CommsThreadManager manager) {
        super(manager);
    }

    // TODO: implement
    public void onEnter() {
        try {
            depthRead = manager.MS5837Read();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // TODO: implement
    public boolean onPeriodic() {
        if (depthRead.isDone()) {
            try {
                System.out.println("Depth: " + depthRead.get().toString());
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
        return new Gyro(manager);
    }
}
