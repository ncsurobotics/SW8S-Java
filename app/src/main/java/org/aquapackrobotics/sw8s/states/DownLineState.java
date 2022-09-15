package org.aquapackrobotics.sw8s.states;

import org.aquapackrobotics.sw8s.trainingsim.SimWindow;

import java.util.concurrent.ScheduledThreadPoolExecutor;

public class DownLineState extends SimState {

    private double startX;
    private double startY;

    public DownLineState(ScheduledThreadPoolExecutor pool, SimWindow window) {
        super(pool, window);

        startX = window.getXPos();
        startY = window.getYPos();

        onEnter();
    }

    @Override
    public void onEnter() { }

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
        return new SemiCircleState(pool, window, startX, startY);
    }
}
