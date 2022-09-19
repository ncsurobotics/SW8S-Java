package org.aquapackrobotics.sw8s.states;

import java.util.concurrent.ScheduledThreadPoolExecutor;

public class InitState extends State {
    public InitState(ScheduledThreadPoolExecutor pool) {
        super(pool);
    }


    //x and y velocities for Initial State
    private double xVel = 0;
    private double yVel = 0;
    private double yawVel = 0;


    public double getX(){
        return xVel;
    } 

    public double getY(){
        return yVel;
    }

    public double getYaw(){
        return yawVel;
    }

    // TODO: implement
    public void onEnter() {
    }

    // TODO: implement
    public boolean onPeriodic() {
        return false;
    }

    // TODO: implement
    public void onExit() {
    }

    // TODO: implement
    public State nextState() {
        return null;
    }
}
