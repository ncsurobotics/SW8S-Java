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
        xVel = 0.0;
        yVel = -1;
        System.out.println("State init OnEnter");
    }

    // TODO: implement
    public boolean onPeriodic() {
        xVel = 1.0;
        yVel = 0.0;
        return false;
    }

    // TODO: implement
    public void onExit() {
        xVel = 0.0;
        yVel = 0.0;
        System.out.println("State init onExit");
    }

    // TODO: implement
    public State nextState() {
        stateTwo s2 = new stateTwo(pool); 
        return s2;
    }
}
