package org.aquapackrobotics.sw8s.comms;

import com.fazecast.jSerialComm.*;

public class ControlBoardCommunication {

    private SerialPort controlBoardPort;

    public enum ControlBoardMode { RAW, LOCAL };

    //Constructor
	private static final byte START_BYTE = (byte) 253;
	private static final byte END_BYTE = (byte) 254;
	private static final byte ESCAPE_BYTE = (byte) 255;
	
    // Look in "Communication Protocol" under SW8E Control Board to find the spec

    public ControlBoardCommunication() {

        controlBoardPort = SerialPort.getCommPorts()[0];

    }

    /**
     * Sets the mode of the control board.
     * @param mode ControlBoardMode enum that is either RAW or LOCAL.
     */
    public void setMode(ControlBoardMode mode) {

        // TODO: Impliment

    }

    /**
     * Returns the mode the control board is in.
     * @return ControlBoardMode enum that is either RAW or LOCAL.
     */
    public ControlBoardMode getMode() {

        // TODO: Impliment

        return null;
    }

    /**
     * Sets the thruster inversions individually.
     * @param invertThrusters Array of 8 booleans, each representing an individual thruster.
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
     * @param speeds Array of 8 doubles that should range from -1 to 1, each representing an individual thruster.
     */
    public void setRawSpeeds(double speed1, double speed2, double speed3, double speed4, double speed5, double speed6, double speed7, double speed8) {

        // TODO: Immpliment

    }
    
    /**
     * Sends given payload to board. Handles addition of START_BYTE, END_BYTE, ESCAPE_BYTE
     * @param byteArray the payload to be sent to the Control Board
     * @param payLoadLength the length of the payload
     */
    private void writeBytesToBoard(byte[] byteArray, long payLoadLength) {
    	
    	
    	controlBoardPort.writeBytes(byteArray, payLoadLength);
    }

}
