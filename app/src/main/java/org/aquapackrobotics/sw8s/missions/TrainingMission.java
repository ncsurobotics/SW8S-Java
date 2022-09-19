package org.aquapackrobotics.sw8s.missions;

import java.util.concurrent.ScheduledThreadPoolExecutor;

import org.aquapackrobotics.sw8s.states.State;
import org.aquapackrobotics.sw8s.states.InitState;
import org.aquapackrobotics.sw8s.trainingsim.SimWindow;

/**
 * Competition mission, fully autonomous.
 */
public class TrainingMission extends Mission {
    SimWindow sim;
    InitState is = new InitState(pool);
    public TrainingMission(ScheduledThreadPoolExecutor pool) {
        super(pool);
        sim = new SimWindow();
    }

    // TODO: implement
    @Override
    protected State initialState() {
        System.out.println("In Initial State");
        try{
            is.onEnter();
            sim.setRobotSpeed(is.getX(), is.getY(), 0);
            Thread.sleep(2500);
            while(is.onPeriodic()){
            }
            sim.setRobotSpeed(is.getX(), is.getY(), 0);
            Thread.sleep(2500);
            is.onExit();
            sim.setRobotSpeed(is.getX(), is.getY(), 0);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return is.nextState();
    }

    // TODO: implement
    @Override
    protected void executeState(State state) {
        try{
            if(state.onPeriodic()) {
                state.onEnter();
                sim.setRobotSpeed(state.getX(), state.getY(), state.getYaw());
                Thread.sleep(1500);
                double c = 0.1;
                double r = 1;
                double t  = 0.0;
                double xVel = -0.1;
                double yVel = -0.1;
                while(state.onPeriodic()){
                    xVel = -r*Math.sin(c*t);
                    yVel = -r*Math.cos(c*t);
                    t++;
                    sim.setRobotSpeed(xVel, yVel, state.getYaw());
                    Thread.sleep(200);
                    }
                state.onExit();
                sim.setRobotSpeed(state.getX(), state.getY(), state.getYaw());
                }
            else{
                    try{
                        state.onEnter();
                        sim.setRobotSpeed(state.getX(), state.getY(), 0);
                        Thread.sleep(100);
                        sim.setRobotSpeed(state.getX(), state.getY(), 0);
                        Thread.sleep(1000);
                        state.onExit();
                        sim.setRobotSpeed(0, 0, 0);
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // TODO: implement
    @Override
    protected State nextState(State state) {
        return null;
    }
}
