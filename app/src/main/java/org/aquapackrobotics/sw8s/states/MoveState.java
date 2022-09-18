package org.aquapackrobotics.sw8s.states;

import java.util.concurrent.ScheduledThreadPoolExecutor;

import org.aquapackrobotics.sw8s.constants.Coordinate;
import org.aquapackrobotics.sw8s.Controller;
import org.aquapackrobotics.sw8s.trainingsim.SimWindow;

public class MoveState extends SimState {
    
    private Controller controller;
    private Coordinate goal;

    //Get the current robot coordinate
    private Coordinate currentCoordinate() { return new Coordinate(this.window.getXPos(), this.window.getYPos()); }

    public MoveState(ScheduledThreadPoolExecutor pool, SimWindow sim, Coordinate newGoal)
    {
        super(pool, sim);
        this.goal = newGoal;
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
        return !currentCoordinate().equals(this.goal);
    }

    public void onExit() { this.window.setRobotSpeed(0, 0, 0); }

    public State nextState()
    {
        return null;
    }

}
