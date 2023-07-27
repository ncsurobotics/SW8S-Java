package org.aquapackrobotics.sw8s.missions;

import org.aquapackrobotics.sw8s.comms.*;

import org.aquapackrobotics.sw8s.states.*;
import org.aquapackrobotics.sw8s.comms.*;

import java.util.Scanner;

import java.util.concurrent.*;

public class Raw_Test extends Mission {
    Scanner scnr = new Scanner(System.in);

    public Raw_Test(CommsThreadManager manager) {
        super(manager);
    }

    // TODO: implement
    @Override
    protected State initialState() {
        return new InitState(manager);
    }

    // TODO: implement
    @Override
    protected void executeState(State state) throws ExecutionException, InterruptedException {
        manager.setThrusterInversions(true, true, false, false, true, false, false, true);

        boolean cont = true;
        while (cont) {
            System.out.print("Motor Number, Speed (i.e. 1 0.5): ");
            String nextLine;
            if (scnr.hasNextLine()) {
                nextLine = scnr.nextLine();
                if (nextLine.equals("")) {
                    //set all motor speeds to 0
                    manager.setMotorSpeeds(0,0,0,0,0,0,0,0);
                    System.out.println("All motors set to 0.");
                    return;
                }
                String[] lineParts = nextLine.split(" ");
                
                int motorNumber = Integer.valueOf(lineParts[0]);
                float speed = Float.valueOf(lineParts[1]);
                float motor_vals[] = new float[8];
                for (int i = 0; i < motor_vals.length; ++i) {
                    motor_vals[i] = 0;
                }
                motor_vals[motorNumber-1] = speed;
                ScheduledFuture result = manager.setMotorSpeeds(motor_vals[0],motor_vals[1],motor_vals[2],motor_vals[3],motor_vals[4],motor_vals[5],motor_vals[6],motor_vals[7]);
                result.get();

                //set motor speed to inputted values
                //setMotorSpeed(motorNumber, speed);
                System.out.println("Motor: " + motorNumber + " // Speed: " + speed);
            }
        }
    }

    // TODO: implement
    @Override
    protected State nextState(State state) {
        return null;
    }
}
