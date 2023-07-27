//to build gradle do: .\gradlew.bat build
//to run grade do: .\gradlew.bat run
package org.aquapackrobotics.sw8s.missions;

import org.aquapackrobotics.sw8s.comms.*;
import org.aquapackrobotics.sw8s.states.*;
import org.aquapackrobotics.sw8s.states.SubmergeTestStates.*;

import java.util.concurrent.*;
/**
 * State machine for testing submerging
 */
public class SubmergeTest extends Mission {

    public SubmergeTest(CommsThreadManager manager) {
        super(manager);

    }

    @Override
    protected State initialState(){
        return new SubmergeTestInitState(manager);
    }

    @Override
    protected void executeState(State state) throws ExecutionException, InterruptedException  {
        state.onEnter();
        while (state.onPeriodic()) {
            
        }
        state.onExit();
    }

    @Override
    protected State nextState(State state) {
        return state.nextState();
    }
}
