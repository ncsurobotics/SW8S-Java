package org.aquapackrobotics.sw8s.states;

import org.aquapackrobotics.sw8s.trainingsim.SimWindow;

import java.util.concurrent.ScheduledThreadPoolExecutor;

public class RightLineState extends SimState {
    public RightLineState(ScheduledThreadPoolExecutor pool, SimWindow window) {
        super(pool, window);

        onEnter();
    }

    @Override
    public void onEnter()
    {
        // Move to starting position
        window.setRobotSpeed(0, -1, 0);
        try {
            Thread.sleep(2000);
        } catch (Exception e) {
            System.out.print(e.getMessage());
        }
    }

    @Override
    public boolean onPeriodic(){

        // Draw G
        window.setRobotSpeed(1, 0, 0);
        this.drawTrace(500, 4);

        return true;
    }

    @Override
    public void onExit() {
        ResetMomentum();
    }

    @Override
    public State nextState() { return new DownLineState(pool, window); }
}
