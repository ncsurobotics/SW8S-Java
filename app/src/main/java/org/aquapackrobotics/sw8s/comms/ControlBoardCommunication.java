package org.aquapackrobotics.sw8s.comms;


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
    	StringBuilder modeMessage = new StringBuilder();
    	modeMessage.append(MODE_STRING);
    	if (mode == ControlBoardMode.RAW) {
    		modeMessage.append(RAW_BYTE);
    	}
    	else if (mode == ControlBoardMode.LOCAL){
    		modeMessage.append(LOCAL_BYTE);
    	}
    	
    	byte[] modeMessageBytes = SerialCommunicationUtility.constructMessage(modeMessage.toString().getBytes());
    	
    	controlBoardPort.writeBytes(modeMessageBytes, modeMessageBytes.length);
    }

    /**
     * Prompts the control board for the current mode
     */
    public void getMode() {
    	StringBuilder message = new StringBuilder();
    	
    	message.append(GET_STRING);
    	message.append(MODE_STRING);
    	
    	byte[] messageBytes = SerialCommunicationUtility.constructMessage(message.toString().getBytes());
        
    	controlBoardPort.writeBytes(messageBytes, messageBytes.length);
    }

    /**
     * Sets the thruster inversions individually.
     * True is inverted, false is not inverted.
     */
    public void setThrusterInversions(boolean invert1, boolean invert2, boolean invert3, boolean invert4, boolean invert5, boolean invert6, boolean invert7, boolean invert8) {
    	
        // TODO: Impliment
        byte[] is_inv = new byte[8];

        is_inv[0] = (byte)(invert1 ? 1:0);
        is_inv[1] = (byte)(invert2 ? 1:0);
        is_inv[2] = (byte)(invert3 ? 1:0);
        is_inv[3] = (byte)(invert4 ? 1:0);
        is_inv[4] = (byte)(invert5 ? 1:0);
        is_inv[5] = (byte)(invert6 ? 1:0);
        is_inv[6] = (byte)(invert7 ? 1:0);
        is_inv[7] = (byte)(invert8 ? 1:0);

        is_inv = SerialCommunicationUtility.constructMessage(is_inv.toString().getBytes());

        controlBoardPort.writeBytes(is_inv, 8);
        
    }

    /**
     * Gets the current thruster inversions.
     * @return Array of 8 booleans, each representing an individual thruster.
     */
    public void getThrusterInversions() {

        // TODO: Impliment
        byte [] get_inversion = new byte[4];
        get_inversion = SerialCommunicationUtility.constructMessage(INVERT_STRING.getBytes());
        
        controlBoardPort.writeBytes(get_inversion, 4);
       // return null;
    }

    /**
     * Directly sets the speeds of the thrusters.
     * Each double should be from -1 to 1.
     */
    public void setRawSpeeds(double speed1, double speed2, double speed3, double speed4, double speed5, double speed6, double speed7, double speed8) {
    	
        // TODO: Immpliment
        byte [] speeds = new byte[8];
        speeds[0] = (byte)speed1;
        speeds[1] = (byte)speed2;
        speeds[2] = (byte)speed3;
        speeds[3] = (byte)speed4;
        speeds[4] = (byte)speed5;
        speeds[5] = (byte)speed6;
        speeds[6] = (byte)speed7;
        speeds[7] = (byte)speed8;

        speeds = SerialCommunicationUtility.constructMessage(speeds.toString().getBytes());

        controlBoardPort.writeBytes(speeds, 8);

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
