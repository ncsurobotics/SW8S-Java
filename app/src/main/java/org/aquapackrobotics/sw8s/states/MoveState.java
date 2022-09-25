package org.aquapackrobotics.sw8s.states;

import java.util.concurrent.ScheduledThreadPoolExecutor;

import org.aquapackrobotics.sw8s.constants.Coordinate;
import org.aquapackrobotics.sw8s.constants.GShapeConstants;
import org.aquapackrobotics.sw8s.controllers.Controller;
import org.aquapackrobotics.sw8s.trainingsim.SimWindow;

public class MoveState extends SimState {
    
    private Controller controller;

    //Get the current robot coordinate
    private Coordinate currentCoordinate() { return new Coordinate(this.window.getXPos(), this.window.getYPos()); }

    public MoveState(ScheduledThreadPoolExecutor pool, SimWindow sim, Coordinate newGoal)
    {
        super(pool, sim);
        this.controller = new Controller(currentCoordinate(), newGoal);
    }

    public void onEnter() { }

    public boolean onPeriodic()
    {
        //Set speed based on controller
        this.window.setRobotSpeed(
            controller.calculateXVel(currentCoordinate()), 
            controller.calculateYVel(currentCoordinate()), 
            0
        );

        //Return true when we reach goal
        return !controller.goalMet();
    }

    public void onExit() { this.window.setRobotSpeed(0, 0, 0); }

    public State nextState()
    {
        //Get the current coord index and check if there are more coords
        int currentCoordIndex = GShapeConstants.indexOf(currentCoordinate());

        if(currentCoordIndex + 1 < GShapeConstants.coords.length)
        {
            return new RotateState(
                pool, 
                this.window, 
                currentCoordinate().getAngle(GShapeConstants.coords[currentCoordIndex + 1])
            );
        }
        else
        {
            return null;
        }
    }

}
