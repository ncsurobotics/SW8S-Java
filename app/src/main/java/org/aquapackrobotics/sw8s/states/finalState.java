package org.aquapackrobotics.sw8s.states;
import java.util.concurrent.ScheduledThreadPoolExecutor;

public class finalState extends State{
  public finalState(ScheduledThreadPoolExecutor pool) {
    super(pool);
  }

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
}
