package org.aquapackrobotics.sw8s.missions;

import org.aquapackrobotics.sw8s.comms.*;

import org.aquapackrobotics.sw8s.states.State;

import java.util.concurrent.*;

/**
 * Robot behavior interface.
 * <p>
 * Missions are state machines, using State objects to progress.
 */
public abstract class Mission {
    /**
     * Processing thread manager.
     * <p>
     * Repeated tasks are Runnables, submitted with FixedRate.
     * <p>
     * Single tasks with a return value are Callables, submitted with schedule
     */
    protected ControlBoardThreadManager manager;

    /**
     * Generic Mission constructor.
     * <p>
     * Extension isn't expected.
     * Guarantees all Mission objects use a thread manager.
     *
     * @param manager A non-filled thread manager
     */
    public Mission(ControlBoardThreadManager manager) {
        this.manager = manager;
    }

    /**
     * Execute the state machine.
     * <p>
     * Proceeds through all states in graph.
     */
    public void run() throws ExecutionException, InterruptedException {
        State currentState = initialState();
        while (currentState != null) {
            currentState.onEnter();
            executeState(currentState);
            currentState.onExit();
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
    protected abstract void executeState(State state) throws ExecutionException, InterruptedException  ;

    /**
     * Computes the next machine state.
     * <p>
     * Uses fields from the current state and Mission parameters.
     *
     * @param state current machine state
     */
    protected abstract State nextState(State state);
}
