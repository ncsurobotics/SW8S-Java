//to build gradle do: .\gradlew.bat build
//to run grade do: .\gradlew.bat run
package org.aquapackrobotics.sw8s.missions;

import org.aquapackrobotics.sw8s.comms.*;
import org.aquapackrobotics.sw8s.comms.*;
import org.aquapackrobotics.sw8s.states.*;
import org.aquapackrobotics.sw8s.states.MotorTestStates.*;

import java.util.concurrent.*;
import java.net.*;
import java.io.*;

/**
 * State machine for testing motors
 */
public class MotorTest extends Mission {

    public MotorTest(ControlBoardThreadManager manager) {
        super(manager);

    }

    @Override
    protected State initialState(){
        return new MotorTestInitState(manager);
    }

    @Override
    protected void executeState(State state) throws ExecutionException, InterruptedException  {
        while (!state.onPeriodic()) {}
    }

    @Override
    protected State nextState(State state) {
        return state.nextState();
    }
}
