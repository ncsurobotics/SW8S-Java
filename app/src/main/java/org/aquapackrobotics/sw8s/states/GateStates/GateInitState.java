package org.aquapackrobotics.sw8s.states.GateStates;

import org.aquapackrobotics.sw8s.comms.*;
import org.aquapackrobotics.sw8s.states.*;
import java.util.concurrent.*;

public class GateInitState extends State {

    ScheduledFuture<byte[]> depthRead;
    ScheduledFuture<byte[]> gyroRead;

    public GateInitState(ControlBoardThreadManager manager) {
        super(manager);
    }

    public void onEnter() throws ExecutionException, InterruptedException {
        manager.setThrusterInversions(true, true, false, false, true, false, false, true);
        manager.setMotorSpeeds(0,0,0,0,0,0,0,0);
        try {
            depthRead = manager.MSPeriodicRead((byte)1);
            gyroRead = manager.BNO055PeriodicRead((byte)1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public boolean onPeriodic() {
        if ( depthRead.isDone() && gyroRead.isDone() ) {
            return true;
        }
        return false;
    }

    public void onExit() throws ExecutionException, InterruptedException{
    }

    public State nextState() {
        return new GateSubmergeState(manager);
    }
}
