package org.aquapackrobotics.sw8s.states;
import java.util.concurrent.ScheduledThreadPoolExecutor;

public class stateTwo extends State{
  public stateTwo(ScheduledThreadPoolExecutor pool) {
    super(pool);
  }

  //x and y velocities for Initial State
  private double xVel = 0;
  private double yVel = 0;
  private double yawVel = 0;
  int count = 0;
  

  public double getX(){
    return xVel;
  }

  public double getY(){
    return yVel;
  }

  public double getYaw(){
    return yawVel;
  }
}
