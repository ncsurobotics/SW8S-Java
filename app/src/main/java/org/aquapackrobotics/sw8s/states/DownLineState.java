package org.aquapackrobotics.sw8s.states;

import org.aquapackrobotics.sw8s.trainingsim.SimWindow;

import java.util.concurrent.ScheduledThreadPoolExecutor;

public class DownLineState extends SimState {

    public DownLineState(ScheduledThreadPoolExecutor pool, SimWindow window) {
        super(pool, window);
        onEnter();
    }

    @Override
    public void onEnter() {

    }

    @Override
    public boolean onPeriodic() {
        return false;
    }

    @Override
    public void onExit() {

    }

    @Override
    public State nextState() {
        return null;
    }
}
