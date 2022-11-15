package org.aquapackrobotics.sw8s.states.;

import org.aquapackrobotics.sw8s.comms.*;
import org.aquapackrobotics.sw8s.states.*;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.*;

public class StateName extends State {

    ControlBoardThreadManager manager;

    public StateName(ScheduledThreadPoolExecutor pool) {
        super(pool);
        manager = new ControlBoardThreadManager(pool);
    }

    public void onEnter() throws ExecutionException, InterruptedException {
    }


    public boolean onPeriodic() {
        return false;
    }

    public void onExit() throws ExecutionException, InterruptedException{
    }

    public State nextState() {
        return new nextState(pool);
    }
}
