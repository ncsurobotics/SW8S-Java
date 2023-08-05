package org.aquapackrobotics.sw8s.comms.control;

import java.util.concurrent.atomic.AtomicBoolean;

public class WatchDogStatus {
    public static AtomicBoolean watchDogKill = new AtomicBoolean(false);
}
