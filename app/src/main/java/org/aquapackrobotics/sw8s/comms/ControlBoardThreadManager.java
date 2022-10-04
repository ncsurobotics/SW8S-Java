package org.aquapackrobotics.sw8s.comms;

import org.checkerframework.checker.units.qual.C;

import java.util.concurrent.*;

public class ControlBoardThreadManager {

    //TODO: JAVADOC
    private ScheduledThreadPoolExecutor pool;
    private ControlBoardCommunication controlBoardCommunication;
    private ControlBoardMode mode;

    //will represent serial comms later
    Runnable watchDog = new Runnable() {
        @Override
        public void run() {
            //TODO: Implement watchdog serial comms
        }
    };

    public ControlBoardThreadManager(ScheduledThreadPoolExecutor pool) {
        this.pool = pool;
        startWatchDog();
        controlBoardCommunication = new ControlBoardCommunication();
    }

    private void startWatchDog() {
        pool.scheduleAtFixedRate(watchDog, 0, 500, TimeUnit.MILLISECONDS);
    }

    private <V> ScheduledFuture<V> scheduleTask(Callable<V> b) throws ExecutionException, InterruptedException {
        ScheduledFuture<V> sf = pool.schedule(b, 0, TimeUnit.MILLISECONDS);
        return sf;
    }

    public <V> V waitForResult(ScheduledFuture<V> scheduledFuture) throws ExecutionException, InterruptedException {
        return scheduledFuture.get();
    }

    public ScheduledFuture<Void> setMode(ControlBoardMode controlBoardMode) throws ExecutionException, InterruptedException {
        Callable<Void> modeRunnable = new Callable<Void>() {
            @Override
            public Void call() {
                controlBoardCommunication.setMode(controlBoardMode);
                return null;
            }
        };

        return scheduleTask(modeRunnable);
    }

    public ScheduledFuture<ControlBoardMode> getMode() throws ExecutionException, InterruptedException {
        Callable<ControlBoardMode> modeCallable = new Callable<ControlBoardMode>() {
            @Override
            public ControlBoardMode call() {
                return controlBoardCommunication.getMode();
            }
        };

        return scheduleTask(modeCallable);
    }

    public ScheduledFuture<Void> setThrusterInversions(boolean invert1, boolean invert2, boolean invert3, boolean invert4, boolean invert5, boolean invert6, boolean invert7, boolean invert8) throws ExecutionException, InterruptedException {
        Callable<Void> inversionCallable = new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                controlBoardCommunication.setThrusterInversions(invert1, invert2, invert3, invert4, invert5, invert6, invert7, invert8);
                return null;
            }
        };
       return scheduleTask(inversionCallable);
    }

    public ScheduledFuture<boolean[]> getThrusterInversions() throws ExecutionException, InterruptedException {
        Callable<boolean[]> inverisonsGetter = new Callable<boolean[]>() {
            @Override
            public boolean[] call() throws Exception {
                return controlBoardCommunication.getThrusterInversions();
            }
        };

        return scheduleTask(inverisonsGetter);
    }
    public ScheduledFuture<Void> setMotorSpeeds(double speed1, double speed2, double speed3, double speed4, double speed5, double speed6, double speed7, double speed8) throws ExecutionException, InterruptedException {
        Callable<Void> speedsCallable = new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                controlBoardCommunication.setRawSpeeds(speed1, speed2, speed3, speed4, speed5, speed6, speed7, speed8);
                return null;
            }
        };
       return scheduleTask(speedsCallable);
    }

    public void dispose() {
        controlBoardCommunication.dispose();
    }
}
