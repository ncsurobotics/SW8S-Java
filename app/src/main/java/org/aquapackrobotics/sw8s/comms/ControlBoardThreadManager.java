package org.aquapackrobotics.sw8s.comms;

import com.fazecast.jSerialComm.SerialPort;

import javax.naming.ldap.Control;
import java.util.concurrent.*;

public class ControlBoardThreadManager {

    //Instance variables
    private ScheduledThreadPoolExecutor pool;
    private ControlBoardCommunication controlBoardCommunication;
    Runnable watchDog = new Runnable() {
        @Override
        public void run() {
            controlBoardCommunication.feedWatchdogMotor();
        }
    };

    //Constructor
    public ControlBoardThreadManager(ScheduledThreadPoolExecutor pool) {
        this.pool = pool;
        SerialPort robotPort = SerialPort.getCommPort("/dev/ttyACM0");
        controlBoardCommunication = new ControlBoardCommunication(robotPort);
        System.out.println("Port " + robotPort.getPortDescription() + " is " + (robotPort.isOpen() ? "open" : "closed"));
        startWatchDog();
    }

    /**
     * Schedules the watch dog thread runnable.
     */
    private void startWatchDog() {
        pool.scheduleAtFixedRate(watchDog, 0, 200, TimeUnit.MILLISECONDS);
    }

    /**
     * Utility function that waits for the result from a ScheduledFuture and returns it when it is available.
     * @param <V> Variable type that the scheduledFuture will return.
     * @param scheduledFuture ScheduledFuture that will be waited upon.
     * @return The result from the scheduledFuture.
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public <V> V waitForResult(ScheduledFuture<V> scheduledFuture) throws ExecutionException, InterruptedException {
        return scheduledFuture.get();
    }

    /**
     * Sets the mode of the control board.
     * @param controlBoardMode The new disired controlBoardMode.
     * @return ScheduledFuture that will return void.
     * @throws ExecutionException
     * @throws InterruptedException
     */
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

    /**
     * Gets the current mode from the control board
     * @return ScheduledFuture that will return the ControlBoardMode.
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public ScheduledFuture<ControlBoardMode> getMode() throws ExecutionException, InterruptedException {

        Callable<ControlBoardMode> modeCallable = new Callable<>() {
            @Override
            public ControlBoardMode call() throws InterruptedException {
                return controlBoardCommunication.getMode();
                
            }
        };

        return scheduleTask(modeCallable);
    }

    /**
     * Set the inversions for each thruster individually.
     * @param invert1 Boolean that the first thruster inversion will be set to.
     * @param invert2 Boolean that the second thruster inversion will be set to.
     * @param invert3 Boolean that the third thruster inversion will be set to.
     * @param invert4 Boolean that the fourth thruster inversion will be set to.
     * @param invert5 Boolean that the fifth thruster inversion will be set to.
     * @param invert6 Boolean that the sixth thruster inversion will be set to.
     * @param invert7 Boolean that the seventh thruster inversion will be set to.
     * @param invert8 Boolean that the eighth thruster inversion will be set to.
     * @return ScheduledFuture that will return void.
     * @throws ExecutionException
     * @throws InterruptedException
     */
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

    /**
     * Gets the current inversions from the control board.
     * @return ScheduledFuture with a list of eight booleans which represents the inversion state for each motor respectively. 
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public ScheduledFuture<boolean[]> getThrusterInversions() throws ExecutionException, InterruptedException {
        //REVERT TO boolean[] RETURN AFTER TEST
        Callable<boolean[]> inversionsGetter = new Callable<>() {
            @Override
            public boolean[] call() throws Exception {
                controlBoardCommunication.getThrusterInversions();
                String msg = MessageStack.getInstance().pop(1000, TimeUnit.MILLISECONDS);
                String inversionsString = msg.startsWith("TINV") ? msg.substring(3) : null;
                boolean[] inversionsArray = new boolean[8];

                for(int i = 0; i < inversionsString.length(); i++) {

                    String booleanAsString = inversionsString.substring(i,i+1);
                    if(booleanAsString.equals("0")) {
                        inversionsArray[i] = false;
                    } else if(booleanAsString.equals("1")) {
                        inversionsArray[i] = true;
                    } else {
                        throw new IllegalArgumentException("Received invalid inversion message. Expected a string of 1s or 0s.");
                    }
                    
                }

                return inversionsArray;

            }
        };

        return scheduleTask(inversionsGetter);
    }

    /**
     * Sets the motor speeds individually and directly.
     * @param speed1 Double for the first motor speed
     * @param speed2 Double for the second motor speed
     * @param speed3 Double for the third motor speed
     * @param speed4 Double for the fourth motor speed
     * @param speed5 Double for the fifth motor speed
     * @param speed6 Double for the sixth motor speed
     * @param speed7 Double for the seventh motor speed
     * @param speed8 Double for the eighth motor speed
     * @return ScheduledFuture that will return void.
     * @throws ExecutionException
     * @throws InterruptedException
     */
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

    /**
     * Sets the motor speeds individually and directly.
     * @return ScheduledFuture that will return void.
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public ScheduledFuture<Void> setLocalSpeeds(double x, double y, double z, double pitch, double roll, double yaw) throws ExecutionException, InterruptedException {
        Callable<Void> speedsCallable = new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                controlBoardCommunication.setLocalSpeeds(x, y, z, pitch, roll, yaw);
                return null;
            }
        };

        return scheduleTask(speedsCallable);
    }

    //Closes controlBoardCommunication.
    public void dispose() {
        controlBoardCommunication.dispose();
    }

    /**
     * Takes a callable as a parameter, schedules it to the pool with a 0 time delay, and returns its ScheduledFuture.
     * @param <V>
     * @param b
     * @return
     * @throws ExecutionException
     * @throws InterruptedException
     */
    private <V> ScheduledFuture<V> scheduleTask(Callable<V> b) throws ExecutionException, InterruptedException {
        ScheduledFuture<V> sf = pool.schedule(b, 0, TimeUnit.MILLISECONDS);
        return sf;
    }
}
