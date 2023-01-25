package org.aquapackrobotics.sw8s.comms;

import com.fazecast.jSerialComm.SerialPort;

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
        controlBoardCommunication = new ControlBoardCommunication(new SerialComPort(robotPort));
        System.out.println("Port " + robotPort.getPortDescription() + " is " + (robotPort.isOpen() ? "open" : "closed"));
        startWatchDog();
        try{
            byte motor_num1 = (byte) 1;
            byte motor_num2 = (byte) 2;
            byte motor_num3 = (byte) 3;
            byte motor_num4 = (byte) 4;
            byte motor_num5 = (byte) 5;
            byte motor_num6 = (byte) 6;
            byte motor_num7 = (byte) 7;
            byte motor_num8 = (byte) 8;
            matrixSet(motor_num1,-1,-1,0,0,0,1);
            matrixSet(motor_num2,1,-1,0,0,0,-1);
            matrixSet(motor_num3,-1,1,0,0,0,-1);
            matrixSet(motor_num4,1,1,0,0,0,1);
            matrixSet(motor_num5,0,0,-1,-1,-1,0);
            matrixSet(motor_num6,0,0,-1,-1,1,0);
            matrixSet(motor_num7,0,0,-1,1,-1,0);
            matrixSet(motor_num8,0,0,-1,1,1,0);
        }
        catch(Exception e){
            System.out.println("Could not set motor matrix");
        }

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
    public ScheduledFuture<byte[]> setThrusterInversions(boolean invert1, boolean invert2, boolean invert3, boolean invert4, boolean invert5, boolean invert6, boolean invert7, boolean invert8) throws ExecutionException, InterruptedException {
        Callable<byte[]> inversionCallable = new Callable<byte[]>() {
            @Override
            public byte[] call() throws Exception {
                short id = controlBoardCommunication.setThrusterInversions(invert1, invert2, invert3, invert4, invert5, invert6, invert7, invert8);
                return MessageStack.getInstance().getMsgById(id);
            }
        };

       return scheduleTask(inversionCallable);
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
    public ScheduledFuture<byte[]> setMotorSpeeds(double speed1, double speed2, double speed3, double speed4, double speed5, double speed6, double speed7, double speed8) throws ExecutionException, InterruptedException {
        Callable<byte[]> speedsCallable = new Callable<>() {
            @Override
            public byte[] call() throws Exception {
                short id = controlBoardCommunication.setRawSpeeds(speed1, speed2, speed3, speed4, speed5, speed6, speed7, speed8);
                return MessageStack.getInstance().getMsgById(id);
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
    public ScheduledFuture<byte[]> setLocalSpeeds(double x, double y, double z, double pitch, double roll, double yaw) throws ExecutionException, InterruptedException {
        Callable<byte[]> speedsCallable = new Callable<>() {
            @Override
            public byte[] call() throws Exception {
                short id = controlBoardCommunication.setLocalSpeeds(x, y, z, pitch, roll, yaw);
                return MessageStack.getInstance().getMsgById(id);
            }
        };

        return scheduleTask(speedsCallable);
    }

    public ScheduledFuture<byte[]> matrixUpdate() throws ExecutionException, InterruptedException {
        Callable<byte[]> speedsCallable = new Callable<>() {
            @Override
            public byte[] call() throws Exception {
                short id = controlBoardCommunication.MatrixUpdate();
                return MessageStack.getInstance().getMsgById(id);
            }
        };

        return scheduleTask(speedsCallable);
    }

    public ScheduledFuture<byte[]> matrixSet(byte thruster_num, double x, double y, double z, double pitch, double roll, double yaw) throws ExecutionException, InterruptedException {
        Callable<byte[]> speedsCallable = new Callable<>() {
            @Override
            public byte[] call() throws Exception {
                short id = controlBoardCommunication.setMotorMatrix(thruster_num, x,y,z,pitch,roll,yaw);
                return MessageStack.getInstance().getMsgById(id);
            }
        };

        return scheduleTask(speedsCallable);
    }

    public ScheduledFuture<byte[]> ImuAxisConfig(int config) throws ExecutionException, InterruptedException {
        Callable<byte[]> speedsCallable = new Callable<>() {
            @Override
            public byte[] call() throws Exception {
                short id = controlBoardCommunication.ImuAxisConfig(config);
                return MessageStack.getInstance().getMsgById(id);
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
