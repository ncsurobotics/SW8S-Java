package org.aquapackrobotics.sw8s.missions;

import java.util.concurrent.ScheduledThreadPoolExecutor;

import org.aquapackrobotics.sw8s.states.*;

import java.util.Scanner;

public class Local_Test extends Mission {
    Scanner scnr = new Scanner(System.in);

    public Local_Test(ScheduledThreadPoolExecutor pool) {
        super(pool);
    }

    // TODO: implement
    @Override
    protected State initialState() {
        System.out.println("initState reached");
        return new InitState(pool);
    }

    // TODO: implement
    @Override
    protected void executeState(State state) {
        System.out.println("executeState reached");
        boolean cont = true;
        while (cont) {
            System.out.println("Direction, Power, Seconds (i.e. Right 0.5 3): ");
            String nextLine = scnr.nextLine();
            if (nextLine.equals("")) {
                return;
            }

            String[] lineParts = nextLine.split(" ");
            
            String direction = lineParts[0];
            double power = Double.valueOf(lineParts[1]);
            double seconds = Double.valueOf(lineParts[2]);

            /* 
            switch (direction.toLowerCase()) {
                case "left": 
                    //setMotorSpeed
                case "right":
                    //setMotorSpeed to cause right velocity

            }*/
        }
    }

    // TODO: implement
    @Override
    protected State nextState(State state) {
        return null;
    }
}