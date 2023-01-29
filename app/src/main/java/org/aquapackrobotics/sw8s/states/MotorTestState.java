package org.aquapackrobotics.sw8s.states;

import org.aquapackrobotics.sw8s.comms.*;
import org.aquapackrobotics.sw8s.states.*;
import java.util.concurrent.*;

public class MotorTestState extends State {

    private int motorNumber;

    private long startTime;
    private long endTime;

    // Time in milliseconds
    private static long MOTOR_RUN_TIME = 500;
    private static long DELAY = 2000;
    private static double TEST_SPEED = 0.5;

    public MotorTestState(ControlBoardThreadManager manager, int motorNumber) {
        super(manager);
        this.motorNumber = motorNumber;
    }

    public void onEnter() throws ExecutionException, InterruptedException {
        manager.setThrusterInversions(true, true, false, false, true, false, false, true);
        startTime = System.currentTimeMillis();

		double[] speeds = new double[8];
		if (motorNumber >= 1 && motorNumber <= 8) {
			speeds[motorNumber - 1] = TEST_SPEED;
		}
        manager.setMotorSpeeds(speeds[0], speeds[1], speeds[2], speeds[3],
				speeds[4], speeds[5], speeds[6], speeds[7]);
    }


    public boolean onPeriodic() throws ExecutionException, InterruptedException {
        endTime = System.currentTimeMillis();
        if (endTime - startTime >= MOTOR_RUN_TIME) {
            return true;
        }
        return false;
    }

    public void onExit() throws ExecutionException, InterruptedException{
        manager.setMotorSpeeds(0,0,0,0,0,0,0,0);
        startTime = System.currentTimeMillis();
        do {
            endTime = System.currentTimeMillis();
            if (endTime - startTime >= DELAY) {
                break;
            }
        } while(true);
    }

    public State nextState() {
        if (motorNumber == 8) {
            return null;
        }
        return new MotorTestState(manager, motorNumber + 1);
    }
}
