package org.aquapackrobotics.sw8s.missions;

import org.aquapackrobotics.sw8s.comms.*;
import org.aquapackrobotics.sw8s.comms.*;
import org.aquapackrobotics.sw8s.states.*;

import java.util.concurrent.*;
import java.net.*;
import java.io.*;

import java.net.*;
import java.io.*;

/**
 * State machine for testing motors
 */
public class MotorTest extends Mission {

    public MotorTest(CommsThreadManager manager) {
        super(manager);

    }

    @Override
    protected State initialState(){
        try {
            manager.setThrusterInversions(true, true, false, false, true, false, false, true).get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new MotorTestState(manager, 0);
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
