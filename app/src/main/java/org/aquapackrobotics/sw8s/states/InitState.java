package org.aquapackrobotics.sw8s.states;


import org.aquapackrobotics.sw8s.states.*;
import org.aquapackrobotics.sw8s.comms.*;

import java.util.Scanner;

import java.util.concurrent.*;

public class InitState extends State {
    public InitState(CommsThreadManager manager) {
        super(manager);
    }

    // TODO: implement
    public void onEnter() {
    }

    // TODO: implement
    public boolean onPeriodic() {
        return false;
    }

    // TODO: implement
    public void onExit() {
    }

    // TODO: implement
    public State nextState() {
        return null;
    }
}
