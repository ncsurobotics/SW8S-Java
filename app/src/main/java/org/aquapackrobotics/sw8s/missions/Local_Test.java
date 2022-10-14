package org.aquapackrobotics.sw8s.missions;

import java.util.concurrent.ScheduledThreadPoolExecutor;

import org.aquapackrobotics.sw8s.states.*;
import org.aquapackrobotics.sw8s.comms.*;

import java.util.Scanner;

import java.util.concurrent.*;

public class Local_Test extends Mission {
    public Local_Test(ScheduledThreadPoolExecutor pool) {
        super(pool);
    }

    public void stopMotors() {
        //set all motors to 0
    }

    // TODO: implement
    @Override
    protected State initialState() {
        return new InitState(pool);
    }

    // TODO: implement
    @Override
    protected void executeState(State state) throws ExecutionException, InterruptedException  {
        Scanner scnr = new Scanner(System.in);

        ControlBoardThreadManager manager = new ControlBoardThreadManager(pool);
        manager.setMode(ControlBoardMode.LOCAL);
        manager.setThrusterInversions(true, true, false, false, true, false, false, true);

        boolean cont = true;
        while (cont) {
            System.out.println("Direction, Power, Seconds (i.e. Right 0.5 3): ");
            String nextLine;
            if (scnr.hasNextLine()) {
                nextLine = scnr.nextLine();
                if (nextLine.equals("")) {
                    //set all motor speeds to 0
                    System.out.println("All motors set to 0.");
                    return;
                }
                String[] lineParts = nextLine.split(" ");
                
                String direction = lineParts[0];
                double power = Double.valueOf(lineParts[1]);
                double seconds = Double.valueOf(lineParts[2]);

                /* 
                switch (direction.toLowerCase()) {
                    case "left": 
                        //setMotorSpeed for seconds seconds
                    case "right":
                        //setMotorSpeed to cause right velocity for seconds seconds
                }*/
            }
        }
    }
    // TODO: implement
    @Override
    protected State nextState(State state) {
        return null;
    }
}
