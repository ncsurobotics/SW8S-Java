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
     * Sets the mode of the control board.
     * @param mode ControlBoardMode enum that is either RAW or LOCAL.
     */
    public void setMode(ControlBoardMode mode) {
    	ByteArrayOutputStream modeMessage = new ByteArrayOutputStream();
    	modeMessage.writeBytes(MODE_STRING);
    	if (mode == ControlBoardMode.RAW) {
    		modeMessage.write(RAW_BYTE);
    	} else if (mode == ControlBoardMode.LOCAL){
    		modeMessage.write(LOCAL_BYTE);
    	}
    	
    	byte[] modeMessageBytes = SerialCommunicationUtility.constructMessage(modeMessage.toByteArray());
    	
    	controlBoardPort.writeBytes(modeMessageBytes, modeMessageBytes.length);
    }

    /**
     * Prompts the control board for the current mode. Blocks until completion.
     * @throws InterruptedException If message retrieval takes too long.
     * @return The mode set in the control board.
     */
    

    /**
     * Sets the thruster inversions individually.
     * True is inverted, false is not inverted.
     */
    public void setThrusterInversions(boolean invert1, boolean invert2, boolean invert3, boolean invert4, boolean invert5, boolean invert6, boolean invert7, boolean invert8) {
        ByteArrayOutputStream message = new ByteArrayOutputStream();

        message.writeBytes(INVERT_STRING);
        appendInversion(message , invert1);
        appendInversion(message , invert2);
        appendInversion(message , invert3);
        appendInversion(message , invert4);
        appendInversion(message , invert5);
        appendInversion(message , invert6);
        appendInversion(message , invert7);
        appendInversion(message , invert8);

        byte[] messageBytes = SerialCommunicationUtility.constructMessage(message.toByteArray());
        
    	controlBoardPort.writeBytes(messageBytes, messageBytes.length);
    }

    private void appendInversion(ByteArrayOutputStream stream , boolean b){
        byte value = b ? (byte) 1 : (byte) 0; 
        stream.write(value);
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
    public void setRawSpeeds(double speed1, double speed2, double speed3, double speed4, double speed5, double speed6, double speed7, double speed8) {
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

        byte[] rawSpeedMessage = SerialCommunicationUtility.constructMessage(rawSpeed.toByteArray());
        controlBoardPort.writeBytes(rawSpeedMessage, rawSpeedMessage.length);
    }

    /**
     * Sets x, y, z, pitch, roll, and yaw in local mode (in that order).
     * Each double should be from -1 to 1.
     */
    public void setLocalSpeeds(double x, double y, double z, double pitch, double roll, double yaw) {
    	ByteArrayOutputStream localSpeed = new ByteArrayOutputStream();
    	localSpeed.writeBytes(LOCAL_STRING);

		SerialCommunicationUtility.writeEncodedFloat(localSpeed, (float) x);
        SerialCommunicationUtility.writeEncodedFloat(localSpeed, (float) y);
        SerialCommunicationUtility.writeEncodedFloat(localSpeed, (float) z);
        SerialCommunicationUtility.writeEncodedFloat(localSpeed, (float) pitch);
        SerialCommunicationUtility.writeEncodedFloat(localSpeed, (float) roll);
        SerialCommunicationUtility.writeEncodedFloat(localSpeed, (float) yaw);

        byte[] localSpeedMessage = SerialCommunicationUtility.constructMessage(localSpeed.toByteArray());

        
        controlBoardPort.writeBytes(localSpeedMessage, localSpeedMessage.length);
    }
    
    /**
     * Sets x, y, z, pitch, roll, and yaw in global mode (in that order).
     * Each double should be from -1 to 1.
     */
    public void setGlobalSpeeds(double x, double y, double z, double pitch, double roll, double yaw) {
    	ByteArrayOutputStream localSpeed = new ByteArrayOutputStream();
    	localSpeed.writeBytes(GLOBAL_STRING);

		SerialCommunicationUtility.writeEncodedFloat(localSpeed, (float) x);
        SerialCommunicationUtility.writeEncodedFloat(localSpeed, (float) y);
        SerialCommunicationUtility.writeEncodedFloat(localSpeed, (float) z);
        SerialCommunicationUtility.writeEncodedFloat(localSpeed, (float) pitch);
        SerialCommunicationUtility.writeEncodedFloat(localSpeed, (float) roll);
        SerialCommunicationUtility.writeEncodedFloat(localSpeed, (float) yaw);

        byte[] localSpeedMessage = SerialCommunicationUtility.constructMessage(localSpeed.toByteArray());

        
        controlBoardPort.writeBytes(localSpeedMessage, localSpeedMessage.length);
    }

    public void SetStabilityAssist1(double x, double y, double yaw, double targePitch, double targetRoll, double targetDepth){
        ByteArrayOutputStream StabilityAssist1 = new ByteArrayOutputStream();
    	StabilityAssist1.writeBytes(STABILITY_ASSIST_1);

		SerialCommunicationUtility.writeEncodedFloat(StabilityAssist1, (float) x);
        SerialCommunicationUtility.writeEncodedFloat(StabilityAssist1, (float) y);
        SerialCommunicationUtility.writeEncodedFloat(StabilityAssist1, (float) yaw);
        SerialCommunicationUtility.writeEncodedFloat(StabilityAssist1, (float) targePitch);
        SerialCommunicationUtility.writeEncodedFloat(StabilityAssist1, (float) targetRoll);
        SerialCommunicationUtility.writeEncodedFloat(StabilityAssist1, (float) targetDepth);

        byte[] StabilityAssistMessage1 = SerialCommunicationUtility.constructMessage(StabilityAssist1.toByteArray());

        
        controlBoardPort.writeBytes(StabilityAssistMessage1, StabilityAssistMessage1.length);
    }

    public void SetStabilityAssist2(double x, double y, double yaw, double targePitch, double targetRoll, double targetDepth){
        ByteArrayOutputStream StabilityAssist2 = new ByteArrayOutputStream();
    	StabilityAssist2.writeBytes(STABILITY_ASSIST_1);

		SerialCommunicationUtility.writeEncodedFloat(StabilityAssist2, (float) x);
        SerialCommunicationUtility.writeEncodedFloat(StabilityAssist2, (float) y);
        SerialCommunicationUtility.writeEncodedFloat(StabilityAssist2, (float) yaw);
        SerialCommunicationUtility.writeEncodedFloat(StabilityAssist2, (float) targePitch);
        SerialCommunicationUtility.writeEncodedFloat(StabilityAssist2, (float) targetRoll);
        SerialCommunicationUtility.writeEncodedFloat(StabilityAssist2, (float) targetDepth);

        byte[] StabilityAssistMessage2 = SerialCommunicationUtility.constructMessage(StabilityAssist2.toByteArray());

        
        controlBoardPort.writeBytes(StabilityAssistMessage2, StabilityAssistMessage2.length);
    }

    // TO DO : ADD INTO THREAD MANAGER
    public void MatrixUpdate(){
        ByteArrayOutputStream MotorMatrixUpdate = new ByteArrayOutputStream();
        MotorMatrixUpdate.writeBytes(MOTOR_MATRIX_UPDATE);

        byte[] UpdateMessage = SerialCommunicationUtility.constructMessage(MotorMatrixUpdate.toByteArray());

        controlBoardPort.writeBytes(UpdateMessage, UpdateMessage.length);
    }
    
    // TO DO : ADD INTO THREAD MANAGER
    public void ImuAxisConfig(int config){
        ByteArrayOutputStream AxisConfig = new ByteArrayOutputStream();
        AxisConfig.writeBytes(IMU_AXIS_CONFIG);
        byte config_byte = (byte) config;
        AxisConfig.write(config_byte);

        byte [] AxisConfigMessage = SerialCommunicationUtility.constructMessage(AxisConfig.toByteArray());

        controlBoardPort.writeBytes(AxisConfigMessage, AxisConfigMessage.length);
    }

    // TO DO : ADD INTO THREAD MANAGER
    public StabAssistPID(char which, double kp, double ki, double kd, double kf, double limit){

    }

    //Messages to implement:
    // Motor Matrix Set
    // Stablility Assist PID tune
    // BNO055 Periodic Read
    // BNO055 Read
    // MS5837 Read
    // BNO055 Data Status
    // MS5837 Data Status


    /**
     * Feeds motor watchdog
     */
    public void feedWatchdogMotor() {
    	byte[] messageBytes = SerialCommunicationUtility.constructMessage(WATCHDOG_FEED_STRING);
    	controlBoardPort.writeBytes(messageBytes, messageBytes.length);
    }
}
