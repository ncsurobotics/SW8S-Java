package org.aquapackrobotics.sw8s.controllers;

public class AngleController {
    
    private double current;
    private double goal;

    private final double yawMaxVel = 1;

    private double angleOffset() { return (goal - current + 180 ) % 360 - 180; }

    public AngleController(double newCurrent, double newGoal)
    {
        current = newCurrent;
        goal = newGoal;
    }

    /**
     * Calculate yaw velocity based on current angle.
     * @param newCurrent The current angle.
     * @return The yaw velocity the robot should go.
     */
    public double calculateYawVel(double newCurrent)
    {
        current = newCurrent;
        return Math.min(yawMaxVel, Math.max(-yawMaxVel, angleOffset()/2));
    }

    /**
     * Checks to see if the goal has been met
     * @return Boolean checking if the goal is met
     */
    public boolean goalMet()
    {
        return angleOffset() == 0;
    }
}
