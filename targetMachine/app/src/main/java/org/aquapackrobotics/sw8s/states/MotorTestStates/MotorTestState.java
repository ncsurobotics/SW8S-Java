package org.aquapackrobotics.sw8s.states.MotorTestStates;

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

    public MotorTestState(ControlBoardThreadManager manager, int motorNumber) {
        super(manager);
        this.motorNumber = motorNumber;
    }

    public void onEnter() throws ExecutionException, InterruptedException {
        manager.setMode(ControlBoardMode.RAW);
        manager.setThrusterInversions(true, true, false, false, true, false, false, true);
        manager.setMotorSpeeds(0,0,0,0,0,0,0,0);
        startTime = System.currentTimeMillis();
    }


    public boolean onPeriodic() throws ExecutionException, InterruptedException {
        switch(motorNumber) {
            case 1:
                manager.setMotorSpeeds(0.5,0,0,0,0,0,0,0);
                break;
            case 2:
                manager.setMotorSpeeds(0,0.5,0,0,0,0,0,0);
                break;
            case 3:
                manager.setMotorSpeeds(0,0,0.5,0,0,0,0,0);
                break;
            case 4:
                manager.setMotorSpeeds(0,0,0,0.5,0,0,0,0);
                break;
            case 5:
                manager.setMotorSpeeds(0,0,0,0,0.5,0,0,0);
                break;
            case 6:
                manager.setMotorSpeeds(0,0,0,0,0,0.5,0,0);
                break;
            case 7:
                manager.setMotorSpeeds(0,0,0,0,0,0,0.5,0);
                break;
            case 8:
                manager.setMotorSpeeds(0,0,0,0.5,0,0,0,0.5);
                break;
            default: 
                break;
        }
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
