package org.aquapackrobotics.sw8s.states;

import java.util.concurrent.ScheduledThreadPoolExecutor;

import org.aquapackrobotics.sw8s.trainingsim.SimWindow;
import org.aquapackrobotics.sw8s.Controller;
import org.aquapackrobotics.sw8s.constants.GShapeConstants;

public class InitState extends SimState {
    public InitState(ScheduledThreadPoolExecutor pool, SimWindow sim) {
        super(pool, sim);
    }

    public void onEnter() {
        this.window.setRobotSpeed(0, -1, 0);
    }

    public boolean onPeriodic() {
        this.window.setRobotSpeed(
            0, 
            Controller.calculateVel(GShapeConstants.gCoord1.getYCoord(), this.window.getYPos()), 
            0
        );

        return this.window.getYPos() != GShapeConstants.gCoord1.getYCoord();
    }

    public void onExit() {
        this.window.setRobotSpeed(0, 0, 0);
    }

    public State nextState() {
        return null;
    }
}
