package org.aquapackrobotics.sw8s.states;

import java.util.concurrent.ScheduledThreadPoolExecutor;

import org.aquapackrobotics.sw8s.trainingsim.SimWindow;
import org.aquapackrobotics.sw8s.constants.Coordinate;
import org.aquapackrobotics.sw8s.constants.GShapeConstants;

public class InitState extends SimState {
    public InitState(ScheduledThreadPoolExecutor pool, SimWindow sim) {
        super(pool, sim);
    }

    private Coordinate currentCoordinate() { return new Coordinate(this.window.getXPos(), this.window.getYPos()); }

    public void onEnter() { }

    public boolean onPeriodic() { return false; }

    public void onExit() { }

    public State nextState()
     {
        return new RotateState(
            pool, 
            this.window, 
            currentCoordinate().getAngle(GShapeConstants.coords[0])
        );
    }
}
