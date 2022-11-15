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
    private static final byte[] WATCHDOG_FEED_STRING = "WDGF".getBytes();
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
    public ControlBoardMode getMode() throws InterruptedException {
    	ByteArrayOutputStream message = new ByteArrayOutputStream();
    	
    	message.writeBytes(GET_STRING);
    	message.writeBytes(MODE_STRING);
    	
    	byte[] messageBytes = SerialCommunicationUtility.constructMessage(message.toByteArray());
        
    	controlBoardPort.writeBytes(messageBytes, messageBytes.length);
    	
    	ControlBoardMode controlBoardMode;
        byte[] msg = MessageStack.getInstance().pop(READ_TIMEOUT_LENGTH, TimeUnit.MILLISECONDS);
        byte mode = ByteArrayUtility.startsWith(msg, MODE_STRING) ? msg[MODE_STRING.length] : null;

        switch (mode) {
            case RAW_BYTE:
                controlBoardMode = ControlBoardMode.RAW;
                break;
            case LOCAL_BYTE:
                controlBoardMode = ControlBoardMode.LOCAL;
                break;
            default:
                controlBoardMode = ControlBoardMode.UNKNOWN;

        }

        return controlBoardMode;
    }
 

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
    public boolean[] getThrusterInversions() throws InterruptedException {
    	ByteArrayOutputStream message = new ByteArrayOutputStream();
    	
    	message.writeBytes(GET_STRING);
    	message.writeBytes(INVERT_STRING);

        // Request the thruster inversions from the board
    	byte[] messageBytes = SerialCommunicationUtility.constructMessage(message.toByteArray());
    	controlBoardPort.writeBytes(messageBytes, messageBytes.length);

        // Retrieve messages from the board until we get the right one
        byte[] msg = null;
        do {
            if (msg != null) {
                MessageStack.getInstance().push(msg);
            }
            msg = MessageStack.getInstance().pop(READ_TIMEOUT_LENGTH, TimeUnit.MILLISECONDS);
        } while (ByteArrayUtility.startsWith(msg, INVERT_STRING));

        byte[] inversions = Arrays.copyOfRange(msg, INVERT_STRING.length, msg.length);

        // Process inversions
        boolean[] inversionsArray = new boolean[THRUSTER_COUNT];

        for(int i = 0; i < inversions.length; i++) {

            if(inversions[i] == (byte) 0) {
                inversionsArray[i] = false;
            } else if(inversions[i] == (byte) 1) {
                inversionsArray[i] = true;
            } else {
                throw new IllegalArgumentException("Received invalid inversion message. Expected a string of 1s or 0s.");
            }
            
        }

        return inversionsArray;
    }

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
     * Feeds motor watchdog
     */
    public void feedWatchdogMotor() {
    	byte[] messageBytes = SerialCommunicationUtility.constructMessage(WATCHDOG_FEED_STRING);
    	controlBoardPort.writeBytes(messageBytes, messageBytes.length);
    }
}
