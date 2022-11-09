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
public class ControlBoardListener implements SerialPortDataListener, ICommPortListener {

	private static final byte START_BYTE = (byte) 253;
	private static final byte END_BYTE = (byte) 254;
	private static final int THRUSTER_COUNT = 8;
	private static final String WATCHDOG_KILL = "WDGK";
	
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

		serialEvent(message);
	}


	@Override
	public void serialEvent(byte[] message) {
		try {
			//If message does not start with start byte or end with end byte, it is ignored
			if (message[0] != START_BYTE || message[message.length - 1] != END_BYTE)
				throw new IllegalArgumentException();

			//Remove start and end bytes
			byte[] strippedMessage = Arrays.copyOfRange(message, 1, message.length - 1);
			//Will throw IllegalArgumentException if garbage/corrupted
			byte[] decodedMessage = SerialCommunicationUtility.destructMessage(strippedMessage);
			String decodedMessageString = new String(decodedMessage);

			if (decodedMessageString.startsWith(WATCHDOG_KILL)) {
				WatchDogStatus.getInstance().setWatchDogKill(true);
			}
			else {
				MessageStack.getInstance().push(decodedMessageString);
			}
		}
		catch (IllegalArgumentException e) {
			//Do nothing
		}
	}
}
