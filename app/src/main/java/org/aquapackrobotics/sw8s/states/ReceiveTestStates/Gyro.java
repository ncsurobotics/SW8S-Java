package org.aquapackrobotics.sw8s.states;


import org.aquapackrobotics.sw8s.states.*;
import org.aquapackrobotics.sw8s.comms.*;

import java.util.Arrays;

import java.util.concurrent.*;

public class Gyro extends State {
    ScheduledFuture<float[]> gyroRead;

    public Gyro(ControlBoardThreadManager manager) {
        super(manager);
    }

    // TODO: implement
    public void onEnter() {
        try {
            gyroRead = manager.BNO055Read();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // TODO: implement
    public boolean onPeriodic() {
        if ( gyroRead.isDone() ) {
            try {
                System.out.println("Gyro: " +
                    Arrays.toString(gyroRead.get()));
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }

    // TODO: implement
    public void onExit() {
    }

    // TODO: implement
    public State nextState() {
        return new PeriodicDepth(manager);
    }
}
