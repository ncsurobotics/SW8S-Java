package org.aquapackrobotics.sw8s.comms;

import java.io.IOException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.prefs.Preferences;

import org.aquapackrobotics.sw8s.comms.control.ControlBoardCommunication;
import org.aquapackrobotics.sw8s.comms.control.ControlBoardListener;
import org.aquapackrobotics.sw8s.comms.meb.MEBCommunication;
import org.aquapackrobotics.sw8s.comms.meb.*;
import org.aquapackrobotics.sw8s.comms.meb.MEBStatus;

import com.fazecast.jSerialComm.SerialPort;

public class CommsThreadManager {

    // Instance variables
    private ScheduledThreadPoolExecutor pool;
    private ControlBoardCommunication controlBoardCommunication;
    private ControlBoardListener controlListener;
    private MEBCommunication mebCommunication;

    Runnable watchDog = new Runnable() {
        @Override
        public void run() {
            controlBoardCommunication.feedWatchdogMotor();
        }
    };

    // Constructor
    public CommsThreadManager(ScheduledThreadPoolExecutor pool) throws IOException {
        this.pool = pool;
        String cboard_tty = CommsThreadManager.getControlBoardPort();
        String meb_tty = CommsThreadManager.getMEBPort();
        SerialPort robotPort = SerialPort.getCommPort(cboard_tty);
        SerialPort mebPort = SerialPort.getCommPort(meb_tty);
        mebPort.setBaudRate(57600);
        controlBoardCommunication = new ControlBoardCommunication(new SerialComPort(robotPort));
        controlListener = new ControlBoardListener();
        mebCommunication = new MEBCommunication(new SerialComPort(mebPort));
        System.out
                .println("Port " + robotPort.getPortDescription() + " is " + (robotPort.isOpen() ? "open" : "closed"));
        try {
            byte motor_num1 = (byte) 1;
            byte motor_num2 = (byte) 2;
            byte motor_num3 = (byte) 3;
            byte motor_num4 = (byte) 4;
            byte motor_num5 = (byte) 5;
            byte motor_num6 = (byte) 6;
            byte motor_num7 = (byte) 7;
            byte motor_num8 = (byte) 8;
            /* Add gets to confirm they finish sending */
            matrixSet(motor_num3, -1, -1, 0, 0, 0, 1).get();
            matrixSet(motor_num4, 1, -1, 0, 0, 0, -1).get();
            matrixSet(motor_num1, -1, 1, 0, 0, 0, -1).get();
            matrixSet(motor_num2, 1, 1, 0, 0, 0, 1).get();
            matrixSet(motor_num7, 0, 0, -1, -1, -1, 0).get();
            matrixSet(motor_num8, 0, 0, -1, -1, 1, 0).get();
            matrixSet(motor_num5, 0, 0, -1, 1, -1, 0).get();
            matrixSet(motor_num6, 0, 0, -1, 1, 1, 0).get();
            matrixUpdate().get(); // ADDED, MISSING FROM SPEC

            setThrusterInversions(true, true, false, false, true, false, false, true).get();

            setDofSpeeds((float) 0.7071, (float) 0.7071, (float) 1.0,
                    (float) 0.4413, (float) 1.0, (float) 0.8139).get();

            ImuAxisConfig((byte) 6).get();
            System.out.println("WAITING FOR CONFIGURE");
            Thread.sleep(5000);

            stabAssistPID('X', 0.8, 0.0, 0.0, 0.6, false).get();
            stabAssistPID('Y', 0.15, 0.0, 0.0, 0.1, false).get();
            stabAssistPID('Z', 1.6, 1e-6, 0.0, 0.8, false).get();
            stabAssistPID('D', 1.5, 0.0, 0.0, 1.0, false).get();

            startWatchDog();
            // BNO055PeriodicRead((byte) 1).get();
            // MSPeriodicRead((byte) 1).get();
            Thread.sleep(500);

        } catch (Exception e) {
            System.out.println("Could not set motor matrix");
        }
    }

    public static String getControlBoardPort() {
        var prefs = Preferences.userRoot().node(CommsThreadManager.class.getName());
        return prefs.get("CBOARD_PORT", "/dev/ttyACM0");
    }

    public static void setControlBoardPort(String port) {
        var prefs = Preferences.userRoot().node(CommsThreadManager.class.getName());
        prefs.put("CBOARD_PORT", port);
    }

    public static String getMEBPort() {
        var prefs = Preferences.userRoot().node(CommsThreadManager.class.getName());
        return prefs.get("MEB_PORT", "/dev/ttyACM2");
    }

    public static void setMEBPort(String port) {
        var prefs = Preferences.userRoot().node(CommsThreadManager.class.getName());
        prefs.put("MEB_PORT", port);
    }

    /**
     * Schedules the watch dog thread runnable.
     */
    private void startWatchDog() {
        pool.scheduleAtFixedRate(watchDog, 0, 200, TimeUnit.MILLISECONDS);
    }

    /**
     * Utility function that waits for the result from a ScheduledFuture and returns
     * it when it is available.
     * 
     * @param <V>             Variable type that the scheduledFuture will return.
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
     * 
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
    public ScheduledFuture<byte[]> setThrusterInversions(boolean invert1, boolean invert2, boolean invert3,
            boolean invert4, boolean invert5, boolean invert6, boolean invert7, boolean invert8)
            throws ExecutionException, InterruptedException {
        Callable<byte[]> inversionCallable = new Callable<byte[]>() {
            @Override
            public byte[] call() throws Exception {
                short id = controlBoardCommunication.setThrusterInversions(invert1, invert2, invert3, invert4, invert5,
                        invert6, invert7, invert8);
                return controlListener.getMsgById(id);
            }
        };

        return scheduleTask(inversionCallable);
    }

    /**
     * Sets the motor speeds individually and directly.
     * 
     * @param speed1 Double for the first motor speed
     * @param speed2 Double for the second motor speed
     * @param speed3 Double for the third motor speed
     * @param speed4 Double for the fourth motor speed
     * @param speed5 Double for the fifth motor speed
     * @param speed6 Double for the sixth motor speed
     * @param speed7 Double for the seventh motor speed @param speed8 Double for the
     *               eighth motor speed
     * @return ScheduledFuture that will return void.
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public ScheduledFuture<byte[]> setMotorSpeeds(float speed1, float speed2, float speed3, float speed4, float speed5,
            float speed6, float speed7, float speed8) throws ExecutionException, InterruptedException {
        Callable<byte[]> speedsCallable = new Callable<>() {
            @Override
            public byte[] call() throws Exception {
                short id = controlBoardCommunication.setRawSpeeds(speed1, speed2, speed3, speed4, speed5, speed6,
                        speed7, speed8);
                return controlListener.getMsgById(id);
            }
        };

        return scheduleTask(speedsCallable);
    }

    /**
     * Sets the motor speeds individually and directly.
     * 
     * @return ScheduledFuture that will return void.
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public ScheduledFuture<byte[]> setLocalSpeeds(double x, double y, double z, double xrot, double yrot, double zrot)
            throws ExecutionException, InterruptedException {
        Callable<byte[]> speedsCallable = new Callable<>() {
            @Override
            public byte[] call() throws Exception {
                short id = controlBoardCommunication.setLocalSpeeds(x, y, z, xrot, yrot, zrot);
                return controlListener.getMsgById(id);
            }
        };

        return scheduleTask(speedsCallable);
    }

    public ScheduledFuture<byte[]> setGlobalSpeeds(double x, double y, double z, double pitch_spd, double roll_spd,
            double yaw_spd) throws ExecutionException, InterruptedException {
        Callable<byte[]> speedsCallable = new Callable<>() {
            @Override
            public byte[] call() throws Exception {
                short id = controlBoardCommunication.setGlobalSpeeds(x, y, z, pitch_spd, roll_spd, yaw_spd);
                return controlListener.getMsgById(id);
            }
        };

        return scheduleTask(speedsCallable);
    }

    public ScheduledFuture<byte[]> setStability1Speeds(double x, double y, double yawSpd, double targetPitch,
            double targetRoll, double targetDepth) throws ExecutionException, InterruptedException {
        Callable<byte[]> speedsCallable = new Callable<>() {
            @Override
            public byte[] call() throws Exception {
                short id = controlBoardCommunication.setStabilityAssist1(x, y, yawSpd, targetPitch, targetRoll,
                        targetDepth);
                return controlListener.getMsgById(id);
            }
        };

        return scheduleTask(speedsCallable);
    }

    public ScheduledFuture<byte[]> setStability2Speeds(double x, double y, double targetPitch, double targetRoll,
            double targetYaw, double targetDepth) throws ExecutionException, InterruptedException {
        Callable<byte[]> speedsCallable = new Callable<>() {
            @Override
            public byte[] call() throws Exception {
                short id = controlBoardCommunication.SetStabilityAssist2(x, y, targetPitch, targetRoll, targetYaw,
                        targetDepth);
                return controlListener.getMsgById(id);
            }
        };

        return scheduleTask(speedsCallable);
    }

    public ScheduledFuture<byte[]> setDepthHold(double x, double y, double pitchSpeed, double rollSpeed,
            double yawSpeed, double targetDepth) throws ExecutionException, InterruptedException {
        Callable<byte[]> speedsCallable = new Callable<>() {
            @Override
            public byte[] call() throws Exception {
                short id = controlBoardCommunication.SetDepthHold(x, y, pitchSpeed, rollSpeed, yawSpeed,
                        targetDepth);
                return controlListener.getMsgById(id);
            }
        };

        return scheduleTask(speedsCallable);
    }

    public ScheduledFuture<byte[]> matrixUpdate() throws ExecutionException, InterruptedException {
        Callable<byte[]> speedsCallable = new Callable<>() {
            @Override
            public byte[] call() throws Exception {
                short id = controlBoardCommunication.MatrixUpdate();
                return controlListener.getMsgById(id);
            }
        };

        return scheduleTask(speedsCallable);
    }

    public ScheduledFuture<byte[]> matrixSet(byte thruster_num, double x, double y, double z, double pitch, double roll,
            double yaw) throws ExecutionException, InterruptedException {
        Callable<byte[]> speedsCallable = new Callable<>() {
            @Override
            public byte[] call() throws Exception {
                short id = controlBoardCommunication.setMotorMatrix(thruster_num, x, y, z, pitch, roll, yaw);
                return controlListener.getMsgById(id);
            }
        };

        return scheduleTask(speedsCallable);
    }

    public ScheduledFuture<byte[]> ImuAxisConfig(byte config) throws ExecutionException, InterruptedException {
        Callable<byte[]> speedsCallable = new Callable<>() {
            @Override
            public byte[] call() throws Exception {
                short id = controlBoardCommunication.ImuAxisConfig(config);
                return controlListener.getMsgById(id);
            }
        };

        return scheduleTask(speedsCallable);
    }

    public ScheduledFuture<byte[]> stabAssistPID(char which, double kp, double ki, double kd, double limit,
            boolean invert) throws ExecutionException, InterruptedException {
        Callable<byte[]> assistCallable = new Callable<>() {
            @Override
            public byte[] call() throws Exception {
                short id = controlBoardCommunication.StabAssistPID(which, kp, ki, kd, limit, invert);
                return controlListener.getMsgById(id);
            }
        };
        return scheduleTask(assistCallable);
    }

    public ScheduledFuture<byte[]> BNO055PeriodicRead(byte enable) throws ExecutionException, InterruptedException {
        Callable<byte[]> readCallable = new Callable<>() {
            @Override
            public byte[] call() throws Exception {
                short id = controlBoardCommunication.BNO055PeriodicRead(enable);
                return controlListener.getMsgById(id);
            }
        };
        return scheduleTask(readCallable);
    }

    public ScheduledFuture<float[]> BNO055Read() throws ExecutionException, InterruptedException {
        Callable<float[]> readCallable = new Callable<>() {
            @Override
            public float[] call() throws Exception {
                float[] data = new float[7];

                short id = controlBoardCommunication.BNO055Read();
                ByteBuffer buffer_data = ByteBuffer.wrap(
                        controlListener.getMsgById(id));

                try {
                    for (int i = 0; i < 7; i++) {
                        data[i] = buffer_data.getFloat();
                    }
                } catch (BufferUnderflowException e) {
                    e.printStackTrace();
                    return null;
                }
                return data;
            }
        };
        return scheduleTask(readCallable);
    }

    public ScheduledFuture<double[]> quatCalculatedBN055Read() throws ExecutionException, InterruptedException {
        Callable<double[]> readCallable = new Callable<>() {
            @Override
            public double[] call() throws Exception {
                float[] data = new float[7];

                short id = controlBoardCommunication.BNO055Read();
                ByteBuffer buffer_data = ByteBuffer.wrap(
                        controlListener.getMsgById(id));

                try {
                    for (int i = 0; i < 7; i++) {
                        data[i] = buffer_data.getFloat();
                    }
                    double quat_w = data[0];
                    double quat_x = data[1];
                    double quat_y = data[2];
                    double quat_z = data[3];

                    double pitch, roll, roll_denom, roll_numer, yaw, yaw_denom, yaw_numer;

                    pitch = 180.0 * Math.asin(2.0 * (quat_y * quat_z + quat_w * quat_x)) / Math.PI;
                    if (Math.abs(90 - Math.abs(pitch)) < 0.1) {
                        yaw = 2.0 * 180.0 * Math.atan2(quat_y, quat_w) / Math.PI;
                        roll = 0.0;
                    } else {
                        roll_numer = 2.0 * (quat_w * quat_y - quat_x * quat_z);
                        roll_denom = 1.0 - 2.0 * (quat_x * quat_x + quat_y * quat_y);
                        roll = 180.0 * Math.atan2(roll_numer, roll_denom) / Math.PI;

                        yaw_numer = -2.0 * (quat_x * quat_y - quat_w * quat_z);
                        yaw_denom = 1.0 - 2.0 * (quat_x * quat_x + quat_z * quat_z);
                        yaw = 180.0 * Math.atan2(yaw_numer, yaw_denom) / Math.PI;
                    }

                    return new double[] { quat_w, quat_x, quat_y, quat_z, pitch, roll, yaw };
                } catch (BufferUnderflowException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        };
        return scheduleTask(readCallable);
    }

    public ScheduledFuture<Float> MS5837Read() throws ExecutionException, InterruptedException {
        Callable<Float> readCallable = new Callable<>() {
            @Override
            public Float call() throws Exception {
                short id = controlBoardCommunication.MS5837Read();
                ByteBuffer buffer_data = ByteBuffer.wrap(
                        controlListener.getMsgById(id));
                buffer_data.order(ByteOrder.LITTLE_ENDIAN);
                return buffer_data.getFloat();
            }
        };
        return scheduleTask(readCallable);
    }

    public ScheduledFuture<byte[]> MSPeriodicRead(byte enable) throws ExecutionException, InterruptedException {
        Callable<byte[]> readCallable = new Callable<>() {
            @Override
            public byte[] call() throws Exception {
                short id = controlBoardCommunication.MSPeriodicRead(enable);
                return controlListener.getMsgById(id);
            }
        };
        return scheduleTask(readCallable);
    }

    public float getDepth() {
        return controlListener.getDepth();
    }

    public double[] getGyro() {
        return controlListener.getGyroData();
    }

    public double getYaw() {
        return getGyro()[6];
    }

    public double getAccumulatedYaw() {
        return controlListener.getAccumulatedYaw();
    }

    public boolean getArm() {
        return MEBStatus.isArmed;
    }

    public ScheduledFuture<byte[]> setDofSpeeds(float x, float y, float z,
            float xrot, float yrot, float zrot) throws ExecutionException, InterruptedException {
        Callable<byte[]> setCallable = new Callable<>() {
            @Override
            public byte[] call() throws Exception {
                short id = controlBoardCommunication.setDofSpeeds(x, y, z, xrot, yrot, zrot);
                return controlListener.getMsgById(id);
            }
        };
        return scheduleTask(setCallable);
    }

    /**
     * Trigger both droppers (1 and 2)
     * 
     * @return ScheduledFuture that will return void.
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public ScheduledFuture<byte[][]> fireDroppers()
            throws ExecutionException, InterruptedException {
        Callable<byte[][]> dropper_callable = new Callable<byte[][]>() {
            @Override
            public byte[][] call() throws Exception {
                short id = mebCommunication.drop_1();
                short id2 = mebCommunication.drop_2();
                return new byte[][] { mebCommunication.getMsgById(id), mebCommunication.getMsgById(id2) };
            }
        };

        return scheduleTask(dropper_callable);
    }

    /**
     * Reset MSB (enable droppers and torpedos)
     * 
     * @return ScheduledFuture that will return void.
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public ScheduledFuture<byte[]> resetMSB()
            throws ExecutionException, InterruptedException {
        Callable<byte[]> reset_callable = new Callable<byte[]>() {
            @Override
            public byte[] call() throws Exception {
                short id = mebCommunication.resetMSB();
                return mebCommunication.getMsgById(id);
            }
        };

        return scheduleTask(reset_callable);
    }

    // Closes controlBoardCommunication.
    public void dispose() {
        controlBoardCommunication.dispose();
    }

    /**
     * Takes a callable as a parameter, schedules it to the pool with a 0 time
     * delay, and returns its ScheduledFuture.
     * 
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

    public ScheduledFuture<?> scheduleRunnable(Runnable runnable) throws ExecutionException, InterruptedException {
        return pool.schedule(runnable, 0, TimeUnit.MILLISECONDS);
    }

    public ScheduledFuture<Boolean> scheduleCallable(Callable<Boolean> callable)
            throws ExecutionException, InterruptedException {
        return pool.schedule(callable, 0, TimeUnit.MILLISECONDS);
    }
}
