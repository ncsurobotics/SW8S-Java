//Work in here for overall code
package org.aquapackrobotics.sw8s.missions;

import java.util.concurrent.ScheduledThreadPoolExecutor;

import org.aquapackrobotics.sw8s.states.*;
import org.aquapackrobotics.sw8s.trainingsim.SimWindow;

/**
 * Competition mission, fully autonomous.
 */
public class TrainingMission extends Mission {
     SimWindow sim;

    public TrainingMission(ScheduledThreadPoolExecutor pool) {
        super(pool);
        sim = new SimWindow();

        initialState();
    }

    // TODO: implement
    @Override
    protected State initialState() {
        System.out.print("TrainingMission.initialState: Hi-> ");
        //return new State(pool, sim);
        return new State1(pool, sim);
    }

    // TODO: implement
    @Override
    protected void executeState(State state) {
        state.onEnter();
        System.out.print("Periodic Reached -> ");
        while (state.onPeriodic() == false) {
        }
        state.onExit();
    }

    // TODO: implement
    @Override
    protected State nextState(State state) {
        System.out.print("TrainingMission nextState reached-> ");
        //InitState obj = new InitState();
        return state.nextState();
        //return null;
    }
}
