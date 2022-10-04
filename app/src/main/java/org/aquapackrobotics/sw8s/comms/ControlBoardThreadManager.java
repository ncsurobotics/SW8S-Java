package org.aquapackrobotics.sw8s.comms;

import java.time.LocalDateTime;
import java.util.concurrent.*;

public class ControlBoardThreadManager {
    private ScheduledThreadPoolExecutor pool;

    //will represent serial comms later
    Runnable watchDog = new Runnable() {
        @Override
        public void run() {
            //TODO: Implement watchdog serial comms
        }
    };

    public ControlBoardThreadManager(ScheduledThreadPoolExecutor pool) {
        this.pool = pool;
        startWatchDog(pool);
    }

    private void startWatchDog(ScheduledThreadPoolExecutor pool) {
        pool.scheduleAtFixedRate(watchDog, 0, 500, TimeUnit.MILLISECONDS);
    }

    public <V> void scheduleVoidCallable(Callable<V> b) throws ExecutionException, InterruptedException {
        ScheduledFuture<V> sf = pool.schedule(b, 0, TimeUnit.MILLISECONDS);
    }

    public <V> V scheduleCallable(Callable<V> b) throws ExecutionException, InterruptedException {
        ScheduledFuture<V> sf = pool.schedule(b, 0, TimeUnit.MILLISECONDS);
        return sf.get();
    }
}
