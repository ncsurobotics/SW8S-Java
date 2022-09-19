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
    }

    // TODO: implement
    @Override
    protected State nextState(State state) {
        return null;
    }
}
