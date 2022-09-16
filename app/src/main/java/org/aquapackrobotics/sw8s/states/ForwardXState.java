package org.aquapackrobotics.sw8s.states;

import java.util.concurrent.ScheduledThreadPoolExecutor;

import org.aquapackrobotics.sw8s.trainingsim.SimWindow;

/**
 * Moves robot a certain distance in the X-axis
 */
public class ForwardXState extends SimState {
	
	//Initial X position of the robot
	private double initialX;
	//Holds position of X position of last waypoint to prevent duplicates
	private int lastX = -1;
	//Holds position of Y position of last waypoint to prevent duplicates
	private int lastY = -1;
	//Target distance to be traveled by the robot
	private double targetDistance;
	//Minimum distance from target position to be considered done
	private final double kError = 0.5;
	
    public ForwardXState(ScheduledThreadPoolExecutor pool, SimWindow sim, double distance) {
        super(pool, sim);
        targetDistance = distance;        
    }

    public void onEnter() {
    	window.setRobotSpeed(0.0, 0.0, 0.0);
    	initialX = window.getXPos();
    }
    
    /**
     * Moves robot at a speed of 1.0 to the target position.
     * Once robot is within kError of the target position, returns false to signal completion
     */
    public boolean onPeriodic() {
    	//Prevents duplicate waypoints
    	if ((int)window.getXPos() != lastX || (int)window.getYPos() != lastY) {
    		try {
    			window.addWaypoint("Gtrace" + System.currentTimeMillis(), (int)window.getXPos(), (int)window.getYPos());
    		}
    		catch (Exception e) {
    			e.getMessage();
    		}
    	}
    	
    	lastX = (int)window.getXPos();
    	lastY = (int)window.getYPos();
    	window.setRobotSpeed(Math.signum(targetDistance) * 1.0, 0.0, 0.0);
    	if (Math.abs(window.getXPos() - (initialX + targetDistance)) < kError)
    		return false;
    	else
    		return true;
    }

    public void onExit() {
    	window.setRobotSpeed(0.0, 0.0, 0.0);
    }

    /**
     * Returns null to signal completion of mission
     */
    public State nextState() {
    	return null;
    }
}
