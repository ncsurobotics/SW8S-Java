package org.aquapackrobotics.sw8s.states;

import java.util.concurrent.*;

import org.aquapackrobotics.sw8s.comms.*;

/**
 * One step of machine execution.
 * <p>
 * Every separate task that is completed should be a different state.
 * Each task has start conditions, execution logic, and end conditions
 */
public abstract class State {
    protected ControlBoardThreadManager manager;
    /**
     * Creates a state instance.
     * <p>
     * Do not preserve State instances, create new ones when a task repeats.
     *
     * @param manager the Missions' manager for task submission
     */
    public State(ControlBoardThreadManager manager) {
        this.manager = manager;
    }

    /**
     * Enforce starting conditions.
     */
    abstract public void onEnter() throws ExecutionException, InterruptedException;

    /**
     * Repeatedly called by state machine.
     * <p>
     * Should not loop.
     * Looping here can trap the state machine.
     * Condition checking should be if, not while.
     *
     * @return if exit conditions are met
     */
    abstract public boolean onPeriodic();

    /**
     * Cleans up state effects and threads.
     * <p>
     * After onExit, the robot and thread manager state should be identical to
     * before onEnter.
     */
    abstract public void onExit() throws ExecutionException, InterruptedException;

    /**
     * Recommends the next state.
     * <p>
     * May be ignored by a state machine override.
     * Otherwise should be the main decision on the next state.
     *
     * @return an instance for the next state
     */
    abstract public State nextState();
}
