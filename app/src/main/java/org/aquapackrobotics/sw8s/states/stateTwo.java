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

  public void onEnter() {
    //xVel = 1.0;
    //yVel = -1.0;
    //yawVel = 0.05;
  }



  public boolean onPeriodic() {
    count++;
    if(count == 62) return false;
    return true;
  }


  public void onExit() {
    xVel = 0.0;
    yVel = 0.0;
    yawVel = 0.0;
  }

  public State nextState() {
    finalState fs = new finalState(pool); 
    return fs;
}
}
