package org.aquapackrobotics.sw8s.missions;

import java.util.concurrent.ScheduledThreadPoolExecutor;

import org.aquapackrobotics.sw8s.states.*;
import org.aquapackrobotics.sw8s.trainingsim.SimWindow;

import java.util.Scanner;

public class Raw_Test extends Mission {
    Scanner scnr = new Scanner(System.in);

    public Raw_Test(ScheduledThreadPoolExecutor pool) {
        super(pool);
    }

    // TODO: implement
    @Override
    protected State initialState() {
        return null;
    }

    // TODO: implement
    @Override
    protected void executeState(State state) {
        boolean continue = true;
        while (continue) {
            System.out.print("Motor Number, Speed (i.e. 1 0.5): ");

            String[] lineParts = nextLine.split(" ");
            
            int motorNumber = Integer.valueOf(lineParts[0]);
            double speed = Double.valueOf(lineParts[1]);
        }
    }

    // TODO: implement
    @Override
    protected State nextState(State state) {
        return null;
    }
}