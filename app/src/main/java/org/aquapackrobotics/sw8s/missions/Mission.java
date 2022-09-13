package org.aquapackrobotics.sw8s.missions;

import java.util.concurrent.ScheduledThreadPoolExecutor;

import org.aquapackrobotics.sw8s.states.State;

/**
 * Robot behavior interface.
 * <p>
 * Missions are state machines, using State objects to progress.
 */
public abstract class Mission {
    /**
     * Processing thread pool.
     * <p>
     * Repeated tasks are Runnables, submitted with FixedRate.
     * <p>
     * Single tasks with a return value are Callables, submitted with schedule
     */
    protected ScheduledThreadPoolExecutor pool;

    /**
     * Generic Mission constructor.
     * <p>
     * Extension isn't expected.
     * Guarantees all Mission objects use a thread pool.
     *
     * @param pool A non-filled thread pool
     */
    public Mission(ScheduledThreadPoolExecutor pool) {
        this.pool = pool;
    }

    /**
     * Execute the state machine.
     * <p>
     * Proceeds through all states in graph.
     */
    public void run() {
        State currentState = initialState();
        while (currentState != null) {
            executeState(currentState);
            currentState = nextState(currentState);
        }
    }

    /**
     * Returns the machine's starting state.
     */
    protected abstract State initialState();

    /**
     * Wraps running the code in a state.
     * <p>
     * Wrapper is useful for non-state actions, i.e. checking operator input
     *
     * @param state current machine state
     */
    protected abstract void executeState(State state);

    /**
     * Computes the next machine state.
     * <p>
     * Uses fields from the current state and Mission parameters.
     *
     * @param state current machine state
     */
    protected abstract State nextState(State state);
}
