package org.aquapackrobotics.sw8s.comms.control;

import java.util.concurrent.atomic.AtomicBoolean;

public class WatchDogStatus {
    private AtomicBoolean watchDogKill;
    private static WatchDogStatus wds;

    private WatchDogStatus() {
        watchDogKill = new AtomicBoolean();
        watchDogKill.set(false);
    }

    public static WatchDogStatus getInstance() {
        if (wds == null) {
            wds = new WatchDogStatus();
        }
        return wds;
    }

    public void setWatchDogKill(boolean wdgk) {
        watchDogKill.set(wdgk);
    }

    public boolean getWatchDogKill() {
        return watchDogKill.get();
    }

}
