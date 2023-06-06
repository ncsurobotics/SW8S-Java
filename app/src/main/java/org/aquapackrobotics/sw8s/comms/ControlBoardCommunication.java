package org.aquapackrobotics.sw8s.comms;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.logging.*;
import java.io.IOException;

import java.lang.Byte;
import java.lang.Boolean;

/**
 * Synchronous SW8 control board communication handler.
 * As a general rule:
 *  Setting functions do not block
 *  Getting functions block
 */
class ControlBoardCommunication {
    private final ICommPort controlBoardPort;
    private static final byte[] MODE_STRING = "MODE".getBytes();
    private static final byte[] INVERT_STRING = "TINV".getBytes();
    private static final byte[] GET_STRING = "?".getBytes();
    private static final byte[] RAW_STRING = "RAW".getBytes();
    private static final byte[] LOCAL_STRING = "LOCAL".getBytes();
    private static final byte[] GLOBAL_STRING = "GLOBAL".getBytes();
    private static final byte[] WATCHDOG_FEED_STRING = "WDGF".getBytes();
    private static final byte[] STABILITY_ASSIST_1 = "SASSIST1".getBytes();
    private static final byte[] STABILITY_ASSIST_2 = "SASSIST2".getBytes();
    private static final byte[] MOTOR_MATRIX_UPDATE = "MMATU".getBytes();
    private static final byte[] IMU_AXIS_CONFIG = "BNO055A".getBytes();
    private static final byte[] MOTOR_MATRIX_SET = "MMATS".getBytes();
    private static final byte[] STAB_ASSIST_PID_TUNE = "SASSISTTN".getBytes();
    private static final byte[] BNO055_PERIODIC_READ = "BNO055P".getBytes();
    private static final byte[] BNO055_READ = "BNO055R".getBytes();
    private static final byte[] MS5837_READ = "MS5837R".getBytes();
    private static final byte[] MS5837_PERIODIC_READ = "MS5837P".getBytes();
    private static final byte[] DOF_SPEED_SET = "RELDOF".getBytes();

    private static final byte RAW_BYTE = (byte) 'R';
    private static final byte LOCAL_BYTE = (byte) 'L';
    private static final long READ_TIMEOUT_LENGTH = 1000;
    private static final int THRUSTER_COUNT = 8;

    private Logger logger;

    /**
     * Construct a new ControlBoardCommunication listening and writing on the given port
     * @param port The port to listen on
     */
    public ControlBoardCommunication(ICommPort port) {
        controlBoardPort = port;
        controlBoardPort.openPort(new ControlBoardListener());

        logger = Logger.getLogger("Comms_Out");
        logger.setUseParentHandlers(false);
        for (var h : logger.getHandlers()) logger.removeHandler(h);
        try {
            FileHandler fHandle = new FileHandler("%t/Comms_Out.log", true);
            fHandle.setFormatter(new SimpleFormatter());
            logger.addHandler(fHandle);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Call this at the end of the lifetime to free the serial ports.
     */
    public void dispose() {
        controlBoardPort.closePort();
    }

    private void logCommand(MessageStruct msg, String code, String data) {
        logger.info(code + " | " + Short.toString(msg.id) + " | " + data + 
                " | " + Arrays.toString(msg.message));
    }

    /**
     * Prompts the control board for the current mode. Blocks until completion.
     * @throws InterruptedException If message retrievag takes too long.
     * @return The mode set in the control board.
     */
    

    /**
     * Sets the thruster inversions individually.
     * True is inverted, false is not inverted.
     * @return the message ID of the sent message.
     */
    public short setThrusterInversions(boolean invert1, boolean invert2, boolean invert3, boolean invert4, boolean invert5, boolean invert6, boolean invert7, boolean invert8) throws InterruptedException {
        ByteArrayOutputStream message = new ByteArrayOutputStream();

        message.writeBytes(INVERT_STRING);
        byte inv = 0;
        
        inv = appendInversion(inv, invert8);
        inv = appendInversion(inv, invert7);
        inv = appendInversion(inv, invert6);
        inv = appendInversion(inv, invert5);
        inv = appendInversion(inv, invert4);
        inv = appendInversion(inv, invert3);
        inv = appendInversion(inv, invert2);
        inv = appendInversion(inv, invert1);

        message.write(inv);

        MessageStruct messageStruct = SerialCommunicationUtility.constructMessage(message.toByteArray());
        byte[] messageBytes = messageStruct.message;
        short msgID = messageStruct.id;
        controlBoardPort.writeBytes(messageBytes, messageBytes.length);

        logCommand(messageStruct, "setThrusterInversions", 
                    Arrays.toString(new boolean[]{invert1, invert2, invert3,
                        invert4, invert5, invert6, invert7, invert8}));
        return msgID;
    }

    private byte appendInversion(byte inversion, boolean b){
        byte value = b ? (byte) 1 : (byte) 0; 
        inversion <<= (byte) 1 ;
        inversion |= value;
        return inversion;
    }

    /**
     * Gets the current thruster inversions. Blocks until completion.
     * @throws InterruptedException If message retrieval takes too long.
     * @return Array of 8 booleans, each representing an individual thruster.
     */
     
    /**
     * Directly sets the speeds of the thrusters.
     * Each double should be from -1 to 1.
     */
    public short setRawSpeeds(float speed1, float speed2, float speed3, float speed4, float speed5, float speed6, float speed7, float speed8) {
        ByteArrayOutputStream rawSpeed = new ByteArrayOutputStream();
        rawSpeed.writeBytes(RAW_STRING);

        SerialCommunicationUtility.writeEncodedFloat(rawSpeed, (float) speed1);
        SerialCommunicationUtility.writeEncodedFloat(rawSpeed, (float) speed2);
        SerialCommunicationUtility.writeEncodedFloat(rawSpeed, (float) speed3);
        SerialCommunicationUtility.writeEncodedFloat(rawSpeed, (float) speed4);
        SerialCommunicationUtility.writeEncodedFloat(rawSpeed, (float) speed5);
        SerialCommunicationUtility.writeEncodedFloat(rawSpeed, (float) speed6);
        SerialCommunicationUtility.writeEncodedFloat(rawSpeed, (float) speed7);
        SerialCommunicationUtility.writeEncodedFloat(rawSpeed, (float) speed8);

        MessageStruct messageStruct = SerialCommunicationUtility.constructMessage(rawSpeed.toByteArray());
        byte[] rawSpeedMessage = messageStruct.message;
        controlBoardPort.writeBytes(rawSpeedMessage, rawSpeedMessage.length);

        logCommand(messageStruct, "setRawSpeeds", 
                    Arrays.toString(new float[]{speed1, speed2, speed3,
                        speed4, speed5, speed6, speed7, speed8}));

        return messageStruct.id;
    }

    /**
     * Sets x, y, z, pitch, roll, and yaw in local mode (in that order).
     * Each double should be from -1 to 1.
     */
    public short setLocalSpeeds(double x, double y, double z, double xrot, double yrot, double zrot) {
        ByteArrayOutputStream localSpeed = new ByteArrayOutputStream();
        localSpeed.writeBytes(LOCAL_STRING);

        SerialCommunicationUtility.writeEncodedFloat(localSpeed, (float) x);
        SerialCommunicationUtility.writeEncodedFloat(localSpeed, (float) y);
        SerialCommunicationUtility.writeEncodedFloat(localSpeed, (float) z);
        SerialCommunicationUtility.writeEncodedFloat(localSpeed, (float) xrot);
        SerialCommunicationUtility.writeEncodedFloat(localSpeed, (float) yrot);
        SerialCommunicationUtility.writeEncodedFloat(localSpeed, (float) zrot);

        MessageStruct messageStruct = SerialCommunicationUtility.constructMessage(localSpeed.toByteArray());
        byte[] localSpeedMessage = messageStruct.message;
        controlBoardPort.writeBytes(localSpeedMessage, localSpeedMessage.length);

        logCommand(messageStruct, "setLocalSpeeds", 
                    Arrays.toString(new double[]{x, y, z, xrot, yrot, zrot}));

        return messageStruct.id;
    }
    
    /**
     * Sets x, y, z, pitch, roll, and yaw in global mode (in that order).
     * Each double should be from -1 to 1.
     */
    public short setGlobalSpeeds(double x, double y, double z, double pitchSpd, double rollSpd, double yawSpd) {
        ByteArrayOutputStream globalSpeed = new ByteArrayOutputStream();
        globalSpeed.writeBytes(GLOBAL_STRING);

        SerialCommunicationUtility.writeEncodedFloat(globalSpeed, (float) x);
        SerialCommunicationUtility.writeEncodedFloat(globalSpeed, (float) y);
        SerialCommunicationUtility.writeEncodedFloat(globalSpeed, (float) z);
        SerialCommunicationUtility.writeEncodedFloat(globalSpeed, (float) pitchSpd);
        SerialCommunicationUtility.writeEncodedFloat(globalSpeed, (float) rollSpd);
        SerialCommunicationUtility.writeEncodedFloat(globalSpeed, (float) yawSpd);

        MessageStruct messageStruct = SerialCommunicationUtility.constructMessage(globalSpeed.toByteArray());
        byte[] globalSpeedMessage = messageStruct.message;
        controlBoardPort.writeBytes(globalSpeedMessage, globalSpeedMessage.length);

        logCommand(messageStruct, "setGlobalSpeeds", 
                    Arrays.toString(new double[]{x, y, z, pitchSpd, rollSpd, yawSpd}));

        return messageStruct.id;
    }

    public short setStabilityAssist1(double x, double y, double yawSpd, double targetPitch, double targetRoll, double targetDepth) {
        ByteArrayOutputStream StabilityAssist1 = new ByteArrayOutputStream();
        StabilityAssist1.writeBytes(STABILITY_ASSIST_1);

        SerialCommunicationUtility.writeEncodedFloat(StabilityAssist1, (float) x);
        SerialCommunicationUtility.writeEncodedFloat(StabilityAssist1, (float) y);
        SerialCommunicationUtility.writeEncodedFloat(StabilityAssist1, (float) yawSpd);
        SerialCommunicationUtility.writeEncodedFloat(StabilityAssist1, (float) targetPitch);
        SerialCommunicationUtility.writeEncodedFloat(StabilityAssist1, (float) targetRoll);
        SerialCommunicationUtility.writeEncodedFloat(StabilityAssist1, (float) targetDepth);

        MessageStruct messageStruct = SerialCommunicationUtility.constructMessage(StabilityAssist1.toByteArray());
        byte[] StabilityAssistMessage1 = messageStruct.message;
        controlBoardPort.writeBytes(StabilityAssistMessage1, StabilityAssistMessage1.length);

        logCommand(messageStruct, "setStabilityAssist_1", 
                    Arrays.toString(new double[]{x, y, yawSpd, targetPitch,
                        targetRoll, targetDepth}));

        return messageStruct.id;
    }

    public short SetStabilityAssist2(double x, double y, double targetPitch, double targetRoll, double targetYaw, double targetDepth){
        ByteArrayOutputStream StabilityAssist2 = new ByteArrayOutputStream();
        StabilityAssist2.writeBytes(STABILITY_ASSIST_2);

        SerialCommunicationUtility.writeEncodedFloat(StabilityAssist2, (float) x);
        SerialCommunicationUtility.writeEncodedFloat(StabilityAssist2, (float) y);
        SerialCommunicationUtility.writeEncodedFloat(StabilityAssist2, (float) targetPitch);
        SerialCommunicationUtility.writeEncodedFloat(StabilityAssist2, (float) targetRoll);
        SerialCommunicationUtility.writeEncodedFloat(StabilityAssist2, (float) targetYaw);
        SerialCommunicationUtility.writeEncodedFloat(StabilityAssist2, (float) targetDepth);


        MessageStruct messageStruct = SerialCommunicationUtility.constructMessage(StabilityAssist2.toByteArray());
        byte[] StabilityAssistMessage2  = messageStruct.message;
        controlBoardPort.writeBytes(StabilityAssistMessage2, StabilityAssistMessage2.length);

        logCommand(messageStruct, "setStabilityAssist_2", 
                    Arrays.toString(new double[]{x, y, targetPitch,
                        targetRoll, targetYaw, targetDepth}));

        return messageStruct.id;
    }

    // TO DO : ADD INTO THREAD MANAGER
    public short MatrixUpdate() {
        ByteArrayOutputStream MotorMatrixUpdate = new ByteArrayOutputStream();
        MotorMatrixUpdate.writeBytes(MOTOR_MATRIX_UPDATE);

        MessageStruct messageStruct = SerialCommunicationUtility.constructMessage(MotorMatrixUpdate.toByteArray());
        byte[] UpdateMessage = messageStruct.message;
        controlBoardPort.writeBytes(UpdateMessage, UpdateMessage.length);

        logCommand(messageStruct, "matrixUpdate", "");

        return messageStruct.id;
    }

    public short setMotorMatrix(byte thruster_num, double x, double y, double z, double pitch, double roll, double yaw) {
        ByteArrayOutputStream MotorMatrixSet = new ByteArrayOutputStream();
        MotorMatrixSet.writeBytes(MOTOR_MATRIX_SET);

        MotorMatrixSet.write(thruster_num);
        
        SerialCommunicationUtility.writeEncodedFloat(MotorMatrixSet, (float) x);
        SerialCommunicationUtility.writeEncodedFloat(MotorMatrixSet, (float) y);
        SerialCommunicationUtility.writeEncodedFloat(MotorMatrixSet, (float) z);
        SerialCommunicationUtility.writeEncodedFloat(MotorMatrixSet, (float) pitch);
        SerialCommunicationUtility.writeEncodedFloat(MotorMatrixSet, (float) roll);
        SerialCommunicationUtility.writeEncodedFloat(MotorMatrixSet, (float) yaw);

        MessageStruct messageStruct = SerialCommunicationUtility.constructMessage(MotorMatrixSet.toByteArray());

        byte[] UpdateMessage = messageStruct.message;
        controlBoardPort.writeBytes(UpdateMessage, UpdateMessage.length);

        logCommand(messageStruct, "setMotorMatrix", 
                    Arrays.toString(new double[]{thruster_num, x, y, z,
                        pitch, roll, yaw}));

        return messageStruct.id;
    }
    
    
    public short ImuAxisConfig(byte config) {
        ByteArrayOutputStream AxisConfig = new ByteArrayOutputStream();
        AxisConfig.writeBytes(IMU_AXIS_CONFIG);
        AxisConfig.write(config);

        MessageStruct messageStruct = SerialCommunicationUtility.constructMessage(AxisConfig.toByteArray());
        byte [] AxisConfigMessage = messageStruct.message;
        controlBoardPort.writeBytes(AxisConfigMessage, AxisConfigMessage.length);

        logCommand(messageStruct, "ImuAxisConfig", Byte.toString(config));

        return messageStruct.id;
    }

    public short StabAssistPID(char which, double kp, double ki, double kd, double limit, boolean invert){
        ByteArrayOutputStream StabAssistTuner = new ByteArrayOutputStream();
        StabAssistTuner.writeBytes(STAB_ASSIST_PID_TUNE);
        byte w = (byte) which;
        StabAssistTuner.write(w);

        SerialCommunicationUtility.writeEncodedFloat(StabAssistTuner, (float) kp);
        SerialCommunicationUtility.writeEncodedFloat(StabAssistTuner, (float) ki);
        SerialCommunicationUtility.writeEncodedFloat(StabAssistTuner, (float) kd);
        SerialCommunicationUtility.writeEncodedFloat(StabAssistTuner, (float) limit);
        StabAssistTuner.write(invert ? (byte)1 : (byte)0);

        MessageStruct messageStruct = SerialCommunicationUtility.constructMessage(StabAssistTuner.toByteArray());
        byte [] StabAssistTunerMessage = messageStruct.message;
        controlBoardPort.writeBytes(StabAssistTunerMessage, StabAssistTunerMessage.length);

        logCommand(messageStruct, "stabAssistPID", which + ", " +
                Arrays.toString(new double[]{kp, ki, kd, limit}) + ", " + Boolean.toString(invert));

        return messageStruct.id;
    }

    public short BNO055PeriodicRead(byte enable){
        ByteArrayOutputStream PeriodicRead = new ByteArrayOutputStream();
        PeriodicRead.writeBytes(BNO055_PERIODIC_READ);
        PeriodicRead.write(enable);
        MessageStruct messageStruct = SerialCommunicationUtility.constructMessage(PeriodicRead.toByteArray());
        byte [] PeriodicReadMessage = messageStruct.message;
        controlBoardPort.writeBytes(PeriodicReadMessage, PeriodicReadMessage.length);

        logCommand(messageStruct, "BNO055PeriodicRead", Byte.toString(enable));

        return messageStruct.id;
    }

    public short BNO055Read(){
        ByteArrayOutputStream Read = new ByteArrayOutputStream();
        Read.writeBytes(BNO055_READ);
        MessageStruct messageStruct = SerialCommunicationUtility.constructMessage(Read.toByteArray());
        byte [] ReadMessage = messageStruct.message;
        controlBoardPort.writeBytes(ReadMessage, ReadMessage.length);

        logCommand(messageStruct, "BNO055Read", "");

        return messageStruct.id;
    }

    public short MS5837Read(){
        ByteArrayOutputStream MSRead = new ByteArrayOutputStream();
        MSRead.writeBytes(MS5837_READ);
        MessageStruct messageStruct = SerialCommunicationUtility.constructMessage(MSRead.toByteArray());
        byte [] MSReadMessage = messageStruct.message;
        controlBoardPort.writeBytes(MSReadMessage, MSReadMessage.length);

        logCommand(messageStruct, "MS5837Read", "");

        return messageStruct.id;
    }
    
    public short MSPeriodicRead(byte enable){
        ByteArrayOutputStream MSPeriodicRead = new ByteArrayOutputStream();
        MSPeriodicRead.writeBytes(MS5837_PERIODIC_READ);
        MSPeriodicRead.write(enable);
        MessageStruct messageStruct = SerialCommunicationUtility.constructMessage(MSPeriodicRead.toByteArray());
        byte [] MSPeriodicReadMessage = messageStruct.message;
        controlBoardPort.writeBytes(MSPeriodicReadMessage, MSPeriodicReadMessage.length);

        logCommand(messageStruct, "MSPeriodicRead", Byte.toString(enable));

        return messageStruct.id;
    }
   
    /**
     * Sets relative speeds of motion in each degree of freedom.
     * Each double should be from -1 to 1.
     */
    public short setDofSpeeds(float x, float y, float z, float xrot, float yrot, float zrot) {
        ByteArrayOutputStream rawDoF = new ByteArrayOutputStream();
        rawDoF.writeBytes(DOF_SPEED_SET);

        SerialCommunicationUtility.writeEncodedFloat(rawDoF, (float) x);
        SerialCommunicationUtility.writeEncodedFloat(rawDoF, (float) y);
        SerialCommunicationUtility.writeEncodedFloat(rawDoF, (float) z);
        SerialCommunicationUtility.writeEncodedFloat(rawDoF, (float) xrot);
        SerialCommunicationUtility.writeEncodedFloat(rawDoF, (float) yrot);
        SerialCommunicationUtility.writeEncodedFloat(rawDoF, (float) zrot);

        MessageStruct messageStruct = SerialCommunicationUtility.constructMessage(rawDoF.toByteArray());
        byte[] rawDofMessage = messageStruct.message;
        controlBoardPort.writeBytes(rawDofMessage, rawDofMessage.length);

        logCommand(messageStruct, "setDofSpeeds", 
                    Arrays.toString(new float[]{x, y, z, xrot, yrot, zrot}));

        return messageStruct.id;
    }

    /**
     * Feeds motor watchdog
     */
    public void feedWatchdogMotor() {
        MessageStruct messageStruct = SerialCommunicationUtility.constructMessage(WATCHDOG_FEED_STRING);
        controlBoardPort.writeBytes(messageStruct.message, messageStruct.message.length);
        logCommand(messageStruct, "feedWatchdogMotor", "");
    }
}
