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
        window.setRobotSpeed(0, 1, 0);
        this.drawTrace(750, 4);

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
