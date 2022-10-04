/**
 * 
 */
package org.aquapackrobotics.sw8s.comms;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortEvent;
import com.fazecast.jSerialComm.SerialPortMessageListener;

/**
 * The ControlBoardListener class implements SerialPortMessageListener to listen for messages starting with the START_BYTE.
 * ControlBoardListener listens for messages from the Control Board
 *
 */
public class ControlBoardListener implements SerialPortMessageListener {
	
	private static final byte[] START_BYTE = {(byte) 253};

	public ControlBoardListener() {
		// TODO Auto-generated constructor stub
	}
	
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
		// TODO Auto-generated method stub

	}
	
	/**
	 * Returns a boolean indicating whether the message delimiter indicates the end or the beginning of a message.
	 * @return true if delimiter indicates end of message, false if delimiter indicates beginning
	 */
	@Override
	public boolean delimiterIndicatesEndOfMessage() {
		return false;
	}
	
	/**
	 * The delimiter that the listener will detect messages with
	 */
	@Override
	public byte[] getMessageDelimiter() {
		// TODO Auto-generated method stub
		return START_BYTE;
	}

}
