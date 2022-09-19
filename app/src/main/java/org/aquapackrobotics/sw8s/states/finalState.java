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

  public void onEnter() {
    xVel = -1.0;
    yVel = 0.0;
    System.out.println("FinalState onEnter");
  }


  public boolean onPeriodic() {
    return false;
  }

  public void onExit() {
    xVel = 0.0;
    yVel = 0.0;
    yawVel = 0.0;
    System.out.println("FinalState onExit");
  }

  public State nextState() {
      return null;
  }
}
