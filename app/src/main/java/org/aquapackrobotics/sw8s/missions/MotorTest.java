//to build gradle do: .\gradlew.bat build
//to run grade do: .\gradlew.bat run
package main.java.org.aquapackrobotics.sw8s.missions;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import org.aquapackrobotics.sw8s.missions.*;
import org.aquapackrobotics.sw8s.comms.*;
import org.aquapackrobotics.sw8s.states.*;
import java.util.concurrent.*;
z
/**
 * State machine for testing motors
 */
public class MotorTest extends Mission {

    public MotorTest(ScheduledThreadPoolExecutor pool) {
        super(pool);

    }

    @Override
    protected State initialState(){
        return new MotorTestInitState(pool);
    }

    @Override
    protected void executeState(State state) throws ExecutionException, InterruptedException  {
        ControlBoardThreadManager manager = new ControlBoardThreadManager(pool);
        manager.setMode(ControlBoardMode.RAW);
        manager.setThrusterInversions(true, true, false, false, true, false, false, true);
        while (state.onPeriodic()) {
            
        }
    }

    @Override
    protected State nextState(State state) {
        return state.nextState();
    }
}
