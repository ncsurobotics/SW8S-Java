/**
 * 
 */
package org.aquapackrobotics.sw8s.comms;

import java.util.Arrays;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;

/**
 * The ControlBoardListener class implements SerialPortMessageListener to listen for messages starting with the START_BYTE.
 * ControlBoardListener listens for messages from the Control Board
 *
 */
public class ControlBoardListener implements SerialPortDataListener {

	private static final byte START_BYTE = (byte) 253;
	private static final byte END_BYTE = (byte) 254;
	private static final int THRUSTER_COUNT = 8;
	
	/**
	 * Returns the events for which serialEvent(SerialPortEvent) will be called
	 */
	@Override
	public int getListeningEvents() {
		return SerialPort.LISTENING_EVENT_DATA_AVAILABLE;
	}
	
	/**
	 * Run when the specified ListeningEvent is triggered
	 */
	@Override
	public void serialEvent(SerialPortEvent event) {
		int size = event.getSerialPort().bytesAvailable();
		byte[] message = new byte[size];
		event.getSerialPort().readBytes(message, size);
		
		try {
			//If message does not start with start byte or end with end byte, it is ignored
			if (message[0] != START_BYTE || message[size - 1] != END_BYTE)
				throw new IllegalArgumentException();
			
			//Remove start and end bytes
			byte[] strippedMessage = Arrays.copyOfRange(message, 1, size - 1);
			//Will throw IllegalArgumentException if garbage/corrupted
			byte[] decodedMessage = SerialCommunicationUtility.destructMessage(strippedMessage);
			if (decodedMessage.toString().startsWith("MODE")) {
				setMode(decodedMessage);
			}
			else if(decodedMessage.toString().startsWith("TINV")) {
				//TODO: Implement
			} else if (decodedMessage.toString().startsWith("WDGK")) {
				WatchDogStatus.getInstance().setWatchDogKill(true);
			}
		}
		catch (IllegalArgumentException e) {
			//Do nothing
		}
	}
	
	/**
	 * Sets the current mode in ControlBoardCommunication based on the passed message
	 * @param message the destructed message
	 */
	private void setMode(byte [] message){ // takes in destructed message
			String m = message.toString(); // message payload converted into string
			if(m == "MODER"){
				ControlBoardCommunication.setCurrentMode(ControlBoardMode.RAW);
			}
			if(m == "MODEL"){	
				ControlBoardCommunication.setCurrentMode(ControlBoardMode.LOCAL);
			}
				
	}

	private void getThrusterInversions(byte [] message){ // byte is a basically int8_t
		String m = message.toString();
		String command = m.substring(0,3);
		boolean [] inversions = new boolean[8];
		if(command == "TINV"){
			for(int i = 4; i<THRUSTER_COUNT; i++){
				inversions[i] = message[i] == 1 ? true : false;
			}
		}
	}
	
}
