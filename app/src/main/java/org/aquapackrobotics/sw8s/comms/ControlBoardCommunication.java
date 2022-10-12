package org.aquapackrobotics.sw8s.comms;


import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;

import com.fazecast.jSerialComm.*;

/**
 *
 */
class ControlBoardCommunication {
    private SerialPort controlBoardPort;
    
    private static final String MODE_STRING = "MODE";
    private static final String INVERT_STRING = "TINV";
    private static final String GET_STRING = "?";
    private static final String RAW_STRING = "RAW";
    private static final String WATCHDOG_FEED_STRING = "WDGF";
    private static final byte RAW_BYTE = (byte) 'R';
    private static final byte LOCAL_BYTE = (byte) 'L';
    
    //SerialPortDataListener listener = new SerialPortDataListener();

    public ControlBoardCommunication(SerialPort port) {
        controlBoardPort = port;
        controlBoardPort.openPort();
        controlBoardPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 100, 0);
        
        controlBoardPort.addDataListener(new ControlBoardListener());
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
    	modeMessage.writeBytes(MODE_STRING.getBytes());
    	if (mode == ControlBoardMode.RAW) {
    		modeMessage.write(RAW_BYTE);
    	}
    	else if (mode == ControlBoardMode.LOCAL){
    		modeMessage.write(LOCAL_BYTE);
    	}
    	
    	byte[] modeMessageBytes = SerialCommunicationUtility.constructMessage(modeMessage.toByteArray());
    	
    	controlBoardPort.writeBytes(modeMessageBytes, modeMessageBytes.length);
    }

    /**
     * Prompts the control board for the current mode
     */
    public void getMode() {
    	ByteArrayOutputStream message = new ByteArrayOutputStream();
    	
    	message.writeBytes(GET_STRING.getBytes());
    	message.writeBytes(MODE_STRING.getBytes());
    	
    	byte[] messageBytes = SerialCommunicationUtility.constructMessage(message.toByteArray());
        
    	controlBoardPort.writeBytes(messageBytes, messageBytes.length);
    }

    /**
     * Sets the thruster inversions individually.
     * True is inverted, false is not inverted.
     */
    public void setThrusterInversions(boolean invert1, boolean invert2, boolean invert3, boolean invert4, boolean invert5, boolean invert6, boolean invert7, boolean invert8) {
        byte[] is_inv = new byte[8];

        is_inv[0] = (byte)(invert1 ? 1:0);
        is_inv[1] = (byte)(invert2 ? 1:0);
        is_inv[2] = (byte)(invert3 ? 1:0);
        is_inv[3] = (byte)(invert4 ? 1:0);
        is_inv[4] = (byte)(invert5 ? 1:0);
        is_inv[5] = (byte)(invert6 ? 1:0);
        is_inv[6] = (byte)(invert7 ? 1:0);
        is_inv[7] = (byte)(invert8 ? 1:0);

        is_inv = SerialCommunicationUtility.constructMessage(is_inv);

        controlBoardPort.writeBytes(is_inv, 8);
        
    }

    /**
     * Gets the current thruster inversions.
     * @return Array of 8 booleans, each representing an individual thruster.
     */
    public void getThrusterInversions() {
    	ByteArrayOutputStream message = new ByteArrayOutputStream();
    	
    	message.writeBytes(GET_STRING.getBytes());
    	message.writeBytes(INVERT_STRING.getBytes());
    	
    	byte[] messageBytes = SerialCommunicationUtility.constructMessage(message.toByteArray());
        
    	controlBoardPort.writeBytes(messageBytes, messageBytes.length);
    }

    /**
     * Directly sets the speeds of the thrusters.
     * Each double should be from -1 to 1.
     */
    public void setRawSpeeds(double speed1, double speed2, double speed3, double speed4, double speed5, double speed6, double speed7, double speed8) {
    	ByteArrayOutputStream rawSpeed = new ByteArrayOutputStream();
    	rawSpeed.writeBytes(RAW_STRING.getBytes());
    	
    	rawSpeed.writeBytes(ByteBuffer.allocate(4).putFloat((float)speed1).array());
    	rawSpeed.writeBytes(ByteBuffer.allocate(4).putFloat((float)speed2).array());
    	rawSpeed.writeBytes(ByteBuffer.allocate(4).putFloat((float)speed3).array());
    	rawSpeed.writeBytes(ByteBuffer.allocate(4).putFloat((float)speed4).array());
    	rawSpeed.writeBytes(ByteBuffer.allocate(4).putFloat((float)speed5).array());
    	rawSpeed.writeBytes(ByteBuffer.allocate(4).putFloat((float)speed6).array());
    	rawSpeed.writeBytes(ByteBuffer.allocate(4).putFloat((float)speed7).array());
    	rawSpeed.writeBytes(ByteBuffer.allocate(4).putFloat((float)speed8).array());

        byte[] rawSpeedMessage = SerialCommunicationUtility.constructMessage(rawSpeed.toByteArray());

        controlBoardPort.writeBytes(rawSpeedMessage, rawSpeedMessage.length);

    }
    
    /**
     * Feeds motor watchdog
     */
    public void feedWatchdogMotor() {
    	byte[] message = new byte[4];
    	message = WATCHDOG_FEED_STRING.getBytes();
    	
    	byte[] messageBytes = SerialCommunicationUtility.constructMessage(message.toString().getBytes());
        
    	controlBoardPort.writeBytes(messageBytes, messageBytes.length);
    }

    
}
