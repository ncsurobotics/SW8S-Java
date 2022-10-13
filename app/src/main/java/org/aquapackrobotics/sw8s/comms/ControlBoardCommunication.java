package org.aquapackrobotics.sw8s.comms;


import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

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
        
        ByteArrayOutputStream message = new ByteArrayOutputStream();

        message.writeBytes(INVERT_STRING.getBytes());
        append(message , invert1);
        append(message , invert2);
        append(message , invert3);
        append(message , invert4);
        append(message , invert5);
        append(message , invert6);
        append(message , invert7);
        append(message , invert8);

        byte[] messageBytes = SerialCommunicationUtility.constructMessage(message.toByteArray());
        
    	controlBoardPort.writeBytes(messageBytes, messageBytes.length);
        
        
    }
    void append(ByteArrayOutputStream stream , boolean b){
        
        byte value = b ? (byte) 1 : (byte) 0; 
        stream.write(value);
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
    	
    	ByteBuffer speedBuffer = ByteBuffer.allocate(4);
    	speedBuffer.order(ByteOrder.LITTLE_ENDIAN);
    	speedBuffer.putFloat((float)speed1);
    	rawSpeed.writeBytes(speedBuffer.array());
    	
    	speedBuffer = ByteBuffer.allocate(4);
    	speedBuffer.order(ByteOrder.LITTLE_ENDIAN);
    	speedBuffer.putFloat((float)speed2);
    	rawSpeed.writeBytes(speedBuffer.array());
    	
    	speedBuffer = ByteBuffer.allocate(4);
    	speedBuffer.order(ByteOrder.LITTLE_ENDIAN);
    	speedBuffer.putFloat((float)speed3);
    	rawSpeed.writeBytes(speedBuffer.array());
    	
    	speedBuffer = ByteBuffer.allocate(4);
    	speedBuffer.order(ByteOrder.LITTLE_ENDIAN);
    	speedBuffer.putFloat((float)speed4);
    	rawSpeed.writeBytes(speedBuffer.array());
    	
    	speedBuffer = ByteBuffer.allocate(4);
    	speedBuffer.order(ByteOrder.LITTLE_ENDIAN);
    	speedBuffer.putFloat((float)speed5);
    	rawSpeed.writeBytes(speedBuffer.array());
    	
    	speedBuffer = ByteBuffer.allocate(6);
    	speedBuffer.order(ByteOrder.LITTLE_ENDIAN);
    	speedBuffer.putFloat((float)speed7);
    	rawSpeed.writeBytes(speedBuffer.array());
    	
    	speedBuffer = ByteBuffer.allocate(4);
    	speedBuffer.order(ByteOrder.LITTLE_ENDIAN);
    	speedBuffer.putFloat((float)speed8);
    	rawSpeed.writeBytes(speedBuffer.array());

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
