//to build gradle do: .\gradlew.bat build
//to run grade do: .\gradlew.bat run
package main.java.org.aquapackrobotics.sw8s.missions;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import org.aquapackrobotics.sw8s.missions.*;
import org.aquapackrobotics.sw8s.comms.ControlBoardThreadManager;
import org.aquapackrobotics.sw8s.states.State;
import org.aquapackrobotics.sw8s.states.*;
import java.util.concurrent.*;

/**
 * State machine for testing motors
 */
public class MotorTest extends Mission {

    public MotorTest(ScheduledThreadPoolExecutor pool) {
        super(pool);

    }

    // TODO: implement
    @Override
    protected State initialState() {
        ControlBoardThreadManager manager = new ControlBoardThreadManager(pool);
        manager.setMode(ControlBoardMode.RAW);
        manager.setThrusterInversions(true, true, false, false, true, false, false, true);
        return MotorTestInitState;
    }

    // TODO: implement
    @Override
    protected void executeState(State state) throws ExecutionException, InterruptedException  {
        while (state.onPeriodic()) {

        }
    }

    // TODO: implement
    @Override
    protected State nextState(State state) {
        return state.nextState();
    }
}
