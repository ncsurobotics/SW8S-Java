package org.aquapackrobotics.sw8s.missions;

import org.aquapackrobotics.sw8s.comms.*;

import org.aquapackrobotics.sw8s.states.*;
import org.aquapackrobotics.sw8s.comms.*;

import java.util.Scanner;

import java.util.concurrent.*;

public class Local_Test extends Mission {
    public Local_Test(CommsThreadManager manager) {
        super(manager);
    }

    public void stopMotors() {
        //set all motors to 0
    }

    // TODO: implement
    @Override
    protected State initialState() {
        return new InitState(manager);
    }

    // TODO: implement
    @Override
    protected void executeState(State state) throws ExecutionException, InterruptedException  {
        Scanner scnr = new Scanner(System.in);

        manager.setThrusterInversions(true, true, false, false, true, false, false, true);

        boolean cont = true;
        while (cont) {
            System.out.println("Direction, Power (i.e. Right 0.5 3): ");
            String nextLine;
            if (scnr.hasNextLine()) {
                nextLine = scnr.nextLine();
                if (nextLine.equals("")) {
                    manager.setLocalSpeeds(0,0,0,0,0,0);
                    System.out.println("All motors set to 0.");
                    return;
                }
                String[] lineParts = nextLine.split(" ");
                
                String direction = lineParts[0];
                double power = Double.valueOf(lineParts[1]);

                switch (direction.toLowerCase()) {
                    case "left": 
                        manager.setLocalSpeeds(-power, 0, 0, 0, 0, 0);
                        break;
                    case "right":
                        manager.setLocalSpeeds(power, 0, 0, 0, 0, 0);
                        break;
                    case "forward":
                        manager.setLocalSpeeds(0, power, 0, 0, 0, 0);
                        break;
                    case "backward":
                        manager.setLocalSpeeds(0, -power, 0, 0, 0, 0);
                        break;
                    case "up":
                        manager.setLocalSpeeds(0, 0, power, 0, 0, 0);
                        break;
                    case "down":
                        manager.setLocalSpeeds(0, 0, -power, 0, 0, 0);
                        break;
                    case "pitch":
                        manager.setLocalSpeeds(0, 0, 0, power, 0, 0);
                        break;
                    case "roll":
                        manager.setLocalSpeeds(0, 0, 0, 0, power, 0);
                        break;
                    case "yaw":
                        manager.setLocalSpeeds(0, 0, 0, 0, 0, power);
                        break;
                }
            }
        }
    }
    // TODO: implement
    @Override
    protected State nextState(State state) {
        return null;
    }
}
