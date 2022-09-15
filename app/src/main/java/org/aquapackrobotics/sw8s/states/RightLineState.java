package org.aquapackrobotics.sw8s.states;

import org.aquapackrobotics.sw8s.trainingsim.SimWindow;

import java.util.concurrent.ScheduledThreadPoolExecutor;

public class RightLineState extends SimState {
    public RightLineState(ScheduledThreadPoolExecutor pool, SimWindow window) {
        super(pool, window);
    }

    @Override
    public void onEnter()
    {

    }

    @Override
    public boolean onPeriodic(){
        return true;
    }

    @Override
    public void onExit() {
        ResetMomentum();
    }

    @Override
    public State nextState() { return null; }
}
