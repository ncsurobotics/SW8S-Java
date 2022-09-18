package org.aquapackrobotics.sw8s;

import org.aquapackrobotics.sw8s.constants.Coordinate;

public class Controller 
{

    private Coordinate current;
    private Coordinate goal;
    
    private double xMaxVel;
    private double yMaxVel;

    private double xOffset() { return goal.getXCoord() - current.getXCoord(); }
    private double yOffset() { return goal.getYCoord() - current.getYCoord(); }

    public Controller(Coordinate newCurrent, Coordinate newGoal)
    {

        current = newCurrent;
        goal = newGoal;

        //Find the abs of each offset to compare them
        double xAbsOffset = Math.abs(xOffset());
        double yAbsOffset = Math.abs(yOffset());

        //Calculate the max x and y velocities
        if(xAbsOffset > yAbsOffset)
        {
            xMaxVel = 1;
            yMaxVel = Math.abs(xOffset()/yOffset());
        }
        else if(yAbsOffset > xAbsOffset)
        {
            xMaxVel = Math.abs(xOffset()/yOffset());
            yMaxVel = 1;
        }
        else
        {
            xMaxVel = 1;
            yMaxVel = 1;
        }
    }


    /**
     * Calculate x velocity based on current coordinate.
     * @param newCurrent A Coordinate object with the robots current coords stored in it.
     * @return The x velocity the robot should go.
     */
    public double calculateXVel(Coordinate newCurrent)
    {
        current = newCurrent;
        return Math.min(xMaxVel, Math.max(-xMaxVel, xOffset()/2));
    }

    /**
     * Calculate y velocity based on current coordinate
     * @param newCurrent A Coordinate object with the robots current coords stored in it.
     * @return The y velocity the robot should go.
     */
    public double calculateYVel(Coordinate newCurrent)
    {
        current = newCurrent;
        return Math.min(yMaxVel, Math.max(-yMaxVel, yOffset()/2));
    }

}
