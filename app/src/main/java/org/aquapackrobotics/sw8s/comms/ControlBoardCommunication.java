package org.aquapackrobotics.sw8s.comms;

import com.fazecast.jSerialComm.*;

public class ControlBoardCommunication {
	private SerialPort controlBoardPort;
	
	private static int START_BYTE;
	private static int END_BYTE;
    // Look in "Communication Protocol" under SW8E Control Board to find the spec
	
    public ControlBoardCommunication() {
    	controlBoardPort = SerialPort.getCommPorts()[0];
    }
    
    private void writeBytesToBoard(byte[] byteArray) {
    	 
    }

}
