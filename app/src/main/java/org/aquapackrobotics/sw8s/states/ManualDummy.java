package org.aquapackrobotics.sw8s.states;

import java.util.concurrent.ExecutionException;

import org.aquapackrobotics.sw8s.comms.CommsThreadManager;

public class ManualDummy extends State {

    public ManualDummy(CommsThreadManager manager) {
        super(manager);
    }

    public void onEnter() throws ExecutionException, InterruptedException {
    }

    public boolean onPeriodic() {
        return false;
    }

    public void onExit() throws ExecutionException, InterruptedException {
    }

    public State nextState() {
        return null;
    }
}
