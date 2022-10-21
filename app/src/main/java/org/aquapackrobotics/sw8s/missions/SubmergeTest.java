//to build gradle do: .\gradlew.bat build
//to run grade do: .\gradlew.bat run
package org.aquapackrobotics.sw8s.missions;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import org.aquapackrobotics.sw8s.states.*;
import org.aquapackrobotics.sw8s.states.SubmergeTestStates.*;

import java.util.concurrent.*;
/**
 * State machine for testing motors
 */
public class SubmergeTest extends Mission {

    public SubmergeTest(ScheduledThreadPoolExecutor pool) {
        super(pool);

    }

    @Override
    protected State initialState(){
        return new SubmergeTestInitState(pool);
    }

    @Override
    protected void executeState(State state) throws ExecutionException, InterruptedException  {
        while (state.onPeriodic()) {
            
        }
    }

    @Override
    protected State nextState(State state) {
        return state.nextState();
    }
}
