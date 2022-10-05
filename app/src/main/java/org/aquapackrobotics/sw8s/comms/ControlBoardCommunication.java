package org.aquapackrobotics.sw8s.comms;

import com.fazecast.jSerialComm.*;

/**
 *
 */
class ControlBoardCommunication {
    private SerialPort controlBoardPort;
    
    private static final String MODE_STRING = "MODE";
    
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
     * Returns the mode the control board is in.
     * @return ControlBoardMode enum that is either RAW or LOCAL.
     */
    public ControlBoardMode getMode() {
    	
    	

        return null;
    }

    /**
     * Sets the thruster inversions individually.
     * True is inverted, false is not inverted.
     */
    public void setThrusterInversions(boolean invert1, boolean invert2, boolean invert3, boolean invert4, boolean invert5, boolean invert6, boolean invert7, boolean invert8) {
    	
        // TODO: Impliment

    }

    /**
     * Gets the current thruster inversions.
     * @return Array of 8 booleans, each representing an individual thruster.
     */
    public boolean[] getThrusterInversions() {

        // TODO: Impliment

        return null;
    }

    /**
     * Directly sets the speeds of the thrusters.
     * Each double should be from -1 to 1.
     */
    public void setRawSpeeds(double speed1, double speed2, double speed3, double speed4, double speed5, double speed6, double speed7, double speed8) {
    	
        // TODO: Immpliment
    }

    
}
