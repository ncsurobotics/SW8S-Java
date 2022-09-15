package org.aquapackrobotics.sw8s.states;

import org.aquapackrobotics.sw8s.trainingsim.SimWindow;

import java.util.concurrent.ScheduledThreadPoolExecutor;

public class SemiCircleState extends SimState {

    public SemiCircleState(ScheduledThreadPoolExecutor pool, SimWindow window) {
        super(pool, window);
        onEnter();
    }

    @Override
    public void onEnter() { }

    @Override
    public boolean onPeriodic() {
        return true;
    }

    @Override
    public void onExit() {
        ResetMomentum();
    }

    @Override
    public State nextState() {
        return null;
    }
}
