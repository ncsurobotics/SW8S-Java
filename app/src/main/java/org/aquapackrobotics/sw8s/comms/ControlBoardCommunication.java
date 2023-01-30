package org.aquapackrobotics.sw8s.comms;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

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
    private static final byte[] STABILITY_ASSIST_1 = "SASSISTST1".getBytes();
    private static final byte[] STABILITY_ASSIST_2 = "SASSISTST2".getBytes();
    private static final byte[] MOTOR_MATRIX_UPDATE = "MMATU".getBytes();
    private static final byte[] IMU_AXIS_CONFIG = "BNO055A".getBytes();
    private static final byte[] MOTOR_MATRIX_SET = "MMATS".getBytes();
    private static final byte[] STAB_ASSIST_PID_TUNE = "SASSISTTN".getBytes();
    private static final byte[] BNO055_PERIODIC_READ = "BNO055P".getBytes();
    private static final byte[] BNO055_READ = "BNO055R".getBytes();
    private static final byte[] MS5837_READ = "MS5837R".getBytes();
    private static final byte[] MS5837_PERIODIC_READ = "MS5837P".getBytes();


    private static final byte RAW_BYTE = (byte) 'R';
    private static final byte LOCAL_BYTE = (byte) 'L';
    private static final long READ_TIMEOUT_LENGTH = 1000;
    private static final int THRUSTER_COUNT = 8;

    /**
     * Construct a new ControlBoardCommunication listening and writing on the given port
     * @param port The port to listen on
     */
    public ControlBoardCommunication(ICommPort port) {
        controlBoardPort = port;
        controlBoardPort.openPort(new ControlBoardListener());
    }

    /**
     * Call this at the end of the lifetime to free the serial ports.
     */
    public void dispose() {
        controlBoardPort.closePort();
    }

    /**
     * Prompts the control board for the current mode. Blocks until completion.
     * @throws InterruptedException If message retrieval takes too long.
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
        
        appendInversion(inv, invert8);
        appendInversion(inv, invert7);
        appendInversion(inv, invert6);
        appendInversion(inv, invert5);
        appendInversion(inv, invert4);
        appendInversion(inv, invert3);
        appendInversion(inv, invert2);
        appendInversion(inv, invert1);

        message.write(inv);

        MessageStruct messageStruct = SerialCommunicationUtility.constructMessage(message.toByteArray());
        byte[] messageBytes = messageStruct.message;
        short msgID = messageStruct.id;
        controlBoardPort.writeBytes(messageBytes, messageBytes.length);
        return msgID;
    }

    private void appendInversion(byte inversion, boolean b){
        byte value = b ? (byte) 1 : (byte) 0; 
        inversion <<= (byte) 1 ;
        inversion |= value;
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
    public short setRawSpeeds(double speed1, double speed2, double speed3, double speed4, double speed5, double speed6, double speed7, double speed8) {
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
        return messageStruct.id;
    }

    /**
     * Sets x, y, z, pitch, roll, and yaw in local mode (in that order).
     * Each double should be from -1 to 1.
     */
    public short setLocalSpeeds(double x, double y, double z, double pitch, double roll, double yaw) {
        ByteArrayOutputStream localSpeed = new ByteArrayOutputStream();
        localSpeed.writeBytes(LOCAL_STRING);

        SerialCommunicationUtility.writeEncodedFloat(localSpeed, (float) x);
        SerialCommunicationUtility.writeEncodedFloat(localSpeed, (float) y);
        SerialCommunicationUtility.writeEncodedFloat(localSpeed, (float) z);
        SerialCommunicationUtility.writeEncodedFloat(localSpeed, (float) pitch);
        SerialCommunicationUtility.writeEncodedFloat(localSpeed, (float) roll);
        SerialCommunicationUtility.writeEncodedFloat(localSpeed, (float) yaw);

        MessageStruct messageStruct = SerialCommunicationUtility.constructMessage(localSpeed.toByteArray());
        byte[] localSpeedMessage = messageStruct.message;
        controlBoardPort.writeBytes(localSpeedMessage, localSpeedMessage.length);
        return messageStruct.id;
    }
    
    /**
     * Sets x, y, z, pitch, roll, and yaw in global mode (in that order).
     * Each double should be from -1 to 1.
     */
    public short setGlobalSpeeds(double x, double y, double z, double pitch, double roll, double yaw) {
        ByteArrayOutputStream globalSpeed = new ByteArrayOutputStream();
        globalSpeed.writeBytes(GLOBAL_STRING);

        SerialCommunicationUtility.writeEncodedFloat(globalSpeed, (float) x);
        SerialCommunicationUtility.writeEncodedFloat(globalSpeed, (float) y);
        SerialCommunicationUtility.writeEncodedFloat(globalSpeed, (float) z);
        SerialCommunicationUtility.writeEncodedFloat(globalSpeed, (float) pitch);
        SerialCommunicationUtility.writeEncodedFloat(globalSpeed, (float) roll);
        SerialCommunicationUtility.writeEncodedFloat(globalSpeed, (float) yaw);

        MessageStruct messageStruct = SerialCommunicationUtility.constructMessage(globalSpeed.toByteArray());
        byte[] globalSpeedMessage = messageStruct.message;
        controlBoardPort.writeBytes(globalSpeedMessage, globalSpeedMessage.length);
        return messageStruct.id;
    }

    public short SetStabilityAssist1(double x, double y, double yaw, double targePitch, double targetRoll, double targetDepth) {
        ByteArrayOutputStream StabilityAssist1 = new ByteArrayOutputStream();
        StabilityAssist1.writeBytes(STABILITY_ASSIST_1);

        SerialCommunicationUtility.writeEncodedFloat(StabilityAssist1, (float) x);
        SerialCommunicationUtility.writeEncodedFloat(StabilityAssist1, (float) y);
        SerialCommunicationUtility.writeEncodedFloat(StabilityAssist1, (float) yaw);
        SerialCommunicationUtility.writeEncodedFloat(StabilityAssist1, (float) targePitch);
        SerialCommunicationUtility.writeEncodedFloat(StabilityAssist1, (float) targetRoll);
        SerialCommunicationUtility.writeEncodedFloat(StabilityAssist1, (float) targetDepth);

        MessageStruct messageStruct = SerialCommunicationUtility.constructMessage(StabilityAssist1.toByteArray());
        byte[] StabilityAssistMessage1 = messageStruct.message;
        controlBoardPort.writeBytes(StabilityAssistMessage1, StabilityAssistMessage1.length);
        return messageStruct.id;
    }

    public short SetStabilityAssist2(double x, double y, double yaw, double targePitch, double targetRoll, double targetDepth){
        ByteArrayOutputStream StabilityAssist2 = new ByteArrayOutputStream();
        StabilityAssist2.writeBytes(STABILITY_ASSIST_2);

        SerialCommunicationUtility.writeEncodedFloat(StabilityAssist2, (float) x);
        SerialCommunicationUtility.writeEncodedFloat(StabilityAssist2, (float) y);
        SerialCommunicationUtility.writeEncodedFloat(StabilityAssist2, (float) yaw);
        SerialCommunicationUtility.writeEncodedFloat(StabilityAssist2, (float) targePitch);
        SerialCommunicationUtility.writeEncodedFloat(StabilityAssist2, (float) targetRoll);
        SerialCommunicationUtility.writeEncodedFloat(StabilityAssist2, (float) targetDepth);


        MessageStruct messageStruct = SerialCommunicationUtility.constructMessage(StabilityAssist2.toByteArray());
        byte[] StabilityAssistMessage2  = messageStruct.message;
        controlBoardPort.writeBytes(StabilityAssistMessage2, StabilityAssistMessage2.length);
        return messageStruct.id;
    }

    // TO DO : ADD INTO THREAD MANAGER
    public short MatrixUpdate() {
        ByteArrayOutputStream MotorMatrixUpdate = new ByteArrayOutputStream();
        MotorMatrixUpdate.writeBytes(MOTOR_MATRIX_UPDATE);

        MessageStruct messageStruct = SerialCommunicationUtility.constructMessage(MotorMatrixUpdate.toByteArray());
        byte[] UpdateMessage = messageStruct.message;
        controlBoardPort.writeBytes(UpdateMessage, UpdateMessage.length);
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
        return messageStruct.id;
    }
    
    
    public short ImuAxisConfig(int config) {
        ByteArrayOutputStream AxisConfig = new ByteArrayOutputStream();
        AxisConfig.writeBytes(IMU_AXIS_CONFIG);
        byte config_byte = (byte) config;
        AxisConfig.write(config_byte);

        MessageStruct messageStruct = SerialCommunicationUtility.constructMessage(AxisConfig.toByteArray());
        byte [] AxisConfigMessage = messageStruct.message;
        controlBoardPort.writeBytes(AxisConfigMessage, AxisConfigMessage.length);
        return messageStruct.id;
    }

    public short StabAssistPID(char which, double kp, double ki, double kd, double kf, double limit){
        //TODO
        ByteArrayOutputStream StabAssistTuner = new ByteArrayOutputStream();
        StabAssistTuner.writeBytes(STAB_ASSIST_PID_TUNE);
        byte w = (byte) which;
        StabAssistTuner.write(w);

        SerialCommunicationUtility.writeEncodedFloat(StabAssistTuner, (float) kp);
        SerialCommunicationUtility.writeEncodedFloat(StabAssistTuner, (float) ki);
        SerialCommunicationUtility.writeEncodedFloat(StabAssistTuner, (float) kd);
        SerialCommunicationUtility.writeEncodedFloat(StabAssistTuner, (float) kf);
        SerialCommunicationUtility.writeEncodedFloat(StabAssistTuner, (float) limit);

        MessageStruct messageStruct = SerialCommunicationUtility.constructMessage(StabAssistTuner.toByteArray());
        byte [] StabAssistTunerMessage = messageStruct.message;
        controlBoardPort.writeBytes(StabAssistTunerMessage, StabAssistTunerMessage.length);
        return messageStruct.id;
    }

    public short BNO055PeriodicRead(byte enable){
        //TODO
        ByteArrayOutputStream PeriodicRead = new ByteArrayOutputStream();
        PeriodicRead.writeBytes(BNO055_PERIODIC_READ);
        PeriodicRead.write(enable);
        MessageStruct messageStruct = SerialCommunicationUtility.constructMessage(PeriodicRead.toByteArray());
        byte [] PeriodicReadMessage = messageStruct.message;
        controlBoardPort.writeBytes(PeriodicReadMessage, PeriodicReadMessage.length);
        return messageStruct.id;
    }

    public short BNO055Read(){
        ByteArrayOutputStream Read = new ByteArrayOutputStream();
        Read.writeBytes(BNO055_READ);
        MessageStruct messageStruct = SerialCommunicationUtility.constructMessage(Read.toByteArray());
        byte [] ReadMessage = messageStruct.message;
        controlBoardPort.writeBytes(ReadMessage, ReadMessage.length);
        return messageStruct.id;
    }

    public short MS5837Read(){
        ByteArrayOutputStream MSRead = new ByteArrayOutputStream();
        MSRead.writeBytes(MS5837_READ);
        MessageStruct messageStruct = SerialCommunicationUtility.constructMessage(MSRead.toByteArray());
        byte [] MSReadMessage = messageStruct.message;
        controlBoardPort.writeBytes(MSReadMessage, MSReadMessage.length);
        return messageStruct.id;
    }
    
    public short MSPeriodicRead(byte enable){
        ByteArrayOutputStream MSPeriodicRead = new ByteArrayOutputStream();
        MSPeriodicRead.writeBytes(MS5837_PERIODIC_READ);
        MSPeriodicRead.write(enable);
        MessageStruct messageStruct = SerialCommunicationUtility.constructMessage(MSPeriodicRead.toByteArray());
        byte [] MSPeriodicReadMessage = messageStruct.message;
        controlBoardPort.writeBytes(MSPeriodicReadMessage, MSPeriodicReadMessage.length);
        return messageStruct.id;

    }
   

    /**
     * Feeds motor watchdog
     */
    public void feedWatchdogMotor() {
        byte[] messageBytes = SerialCommunicationUtility.constructMessage(WATCHDOG_FEED_STRING).message;
        controlBoardPort.writeBytes(messageBytes, messageBytes.length);
    }
}
