package org.aquapackrobotics.sw8s.states;

import java.util.concurrent.ScheduledThreadPoolExecutor;

import org.aquapackrobotics.sw8s.constants.Coordinate;
import org.aquapackrobotics.sw8s.constants.GShapeConstants;
import org.aquapackrobotics.sw8s.controllers.AngleController;
import org.aquapackrobotics.sw8s.trainingsim.SimWindow;

public class RotateState extends SimState
{

    private AngleController controller;
    private double goal;

    private double currentAngle() { return this.window.getRobotAngle(); }

    public RotateState(ScheduledThreadPoolExecutor pool, SimWindow sim, double newGoal)
    {
        super(pool, sim);
        goal = newGoal;
        controller = new AngleController(currentAngle(), goal);
    }

    public void onEnter() { }

    public boolean onPeriodic()
    {
        //Set speed based on controller
        this.window.setRobotSpeed(
            0,
            0,
            controller.calculateYawVel(currentAngle())
        );

        return !controller.goalMet();
    }

    public void onExit() { this.window.setRobotSpeed(0, 0, 0); }

    public State nextState()
    {
        //Get the current coord index and check if there are more coords
        int currentCoordIndex = GShapeConstants.indexOf( new Coordinate(this.window.getXPos(), this.window.getYPos()) );

        if(currentCoordIndex + 1 < GShapeConstants.coords.length)
        {
            return new MoveState(
                pool, 
                this.window, 
                GShapeConstants.coords[currentCoordIndex + 1]
            );
        }
        else
        {
            return null;
        }
    }
    
}