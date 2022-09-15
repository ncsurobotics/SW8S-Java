package org.aquapackrobotics.sw8s.states;

import java.util.concurrent.ScheduledThreadPoolExecutor;

import org.aquapackrobotics.sw8s.trainingsim.SimWindow;

/*
 * Moves robot a certain distance in the Y-axis
 */
public class ForwardYState extends SimState {
	
	//Initial Y position of the robot
	double initialY;
	//Target distance to be traveled by the robot
	double targetDistance;
	//Minimum distance from target position to be considered done
	final double kError = 0.5;
	
    public ForwardYState(ScheduledThreadPoolExecutor pool, SimWindow sim, double distance) {
        super(pool, sim);
        targetDistance = distance;
    }
  
    public void onEnter() {
    	window.setRobotSpeed(0.0, 0.0, 0.0);
    	initialY = window.getYPos();
    }
    
    /*
     * Moves robot at a speed of 1.0 to the target position.
     * Once robot is within kError of the target position, returns false to signal completion
     */
    public boolean onPeriodic() {
    	window.setRobotSpeed(0.0, Math.signum(targetDistance) * 1.0, 0.0);
    	if (Math.abs(window.getYPos() - (initialY + targetDistance)) < kError)
    		return false;
    	else
    		return true;
    }

    public void onExit() {
    	window.setRobotSpeed(0.0, 0.0, 0.0);
    }
    
    //Returns an instance of RotateState with a radius of 100
    public State nextState() {
        return new RotateState(pool, window, 100);
    }
}
