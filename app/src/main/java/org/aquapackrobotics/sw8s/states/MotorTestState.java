package org.aquapackrobotics.sw8s.states;

import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledFuture;

import org.aquapackrobotics.sw8s.comms.CommsThreadManager;

public class MotorTestState extends State {

    private int motorNumber;

    private long startTime;
    private long endTime;

    ScheduledFuture<byte[]> motorSpeedsReturn;

    // Time in milliseconds
    private static long MOTOR_RUN_TIME = 1000;
    private static long DELAY = 1000;
    private static float TEST_SPEED = (float) 0.3;

    public MotorTestState(CommsThreadManager manager, int motorNumber) {
        super(manager);
        this.motorNumber = motorNumber;
    }

    public void onEnter() throws ExecutionException, InterruptedException {
        startTime = System.currentTimeMillis();
        manager.setThrusterInversions(true, true, false, false, true, false, false, true);

        float[] speeds = new float[8];
        if (motorNumber >= 1 && motorNumber <= 8) {
            System.out.println("Running motor: " + motorNumber);
            speeds[motorNumber - 1] = TEST_SPEED;
        }
        motorSpeedsReturn = manager.setMotorSpeeds(speeds[0], speeds[1], speeds[2], speeds[3],
                speeds[4], speeds[5], speeds[6], speeds[7]);
    }

    public boolean onPeriodic() throws ExecutionException, InterruptedException {
        endTime = System.currentTimeMillis();
        if (endTime - startTime >= MOTOR_RUN_TIME) {
            System.out.println("Got ACK: " + motorSpeedsReturn.isDone());
            System.out.println("Got ACK: " + Arrays.toString(motorSpeedsReturn.get()));
            return true;
        }
        return false;
    }

    public void onExit() throws ExecutionException, InterruptedException {
        manager.setMotorSpeeds(0, 0, 0, 0, 0, 0, 0, 0);
        startTime = System.currentTimeMillis();
        do {
            endTime = System.currentTimeMillis();
            if (endTime - startTime >= DELAY) {
                break;
            }
        } while (true);
    }

    public State nextState() {
        if (motorNumber > 8) {
            try {
                manager.setMotorSpeeds(0, 0, 0, 0, 0, 0, 0, 0);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
        return new MotorTestState(manager, motorNumber + 1);
    }
}
