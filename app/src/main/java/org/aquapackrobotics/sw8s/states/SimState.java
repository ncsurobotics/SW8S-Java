package org.aquapackrobotics.sw8s.states;

import java.util.concurrent.ScheduledThreadPoolExecutor;

import org.aquapackrobotics.sw8s.trainingsim.SimWindow;

/**
 * Simulator specific state.
 */
public abstract class SimState extends State {
    SimWindow window;

    public SimState(ScheduledThreadPoolExecutor pool, SimWindow window) {
        super(pool);
        this.window = window;
    }
}
