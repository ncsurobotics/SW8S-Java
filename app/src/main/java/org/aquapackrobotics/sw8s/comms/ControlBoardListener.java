package org.aquapackrobotics.sw8s.comms;

import java.util.Arrays;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;

/**
 * ControlBoardListener listens for messages from a comm port.
 * See SerialCommunicationUtility for message implementation details.
 */
public class ControlBoardListener implements SerialPortDataListener, ICommPortListener {

	private static final String WATCHDOG_STATUS = "WDGS";
	private static final String ACKNOWLEDGE = "ACK";
	
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

		serialMessageHandler(message);
	}

	/**
	 * Processes an incoming message
	 * @param message The message to process
	 */
	public void serialMessageHandler(byte[] message) {
		try {
			//If message does not start with start byte or end with end byte, it is ignored
			if (!SerialCommunicationUtility.isStartOfMessage(message[0]) ||
					!SerialCommunicationUtility.isEndOfMessage(message[message.length - 1]))
				throw new IllegalArgumentException("Message does not start with start byte or end byte: " + Arrays.toString(message));
			//Remove start and end bytes

			byte[] strippedMessage = Arrays.copyOfRange(message, 1, message.length - 1);
			//Will throw IllegalArgumentException if garbage/corrupted
			byte[] decodedMessage = SerialCommunicationUtility.destructMessage(strippedMessage);

			//Remove message ID of received message
			decodedMessage = Arrays.copyOfRange(decodedMessage, 2, decodedMessage.length);

			if (ByteArrayUtility.startsWith(decodedMessage, WATCHDOG_STATUS.getBytes())) {
				if (decodedMessage[4] == (byte)0)
					WatchDogStatus.getInstance().setWatchDogKill(true);
			}
			else if (ByteArrayUtility.startsWith(decodedMessage, ACKNOWLEDGE.getBytes())) {
				//Pushes message onto message stack if acknowledge message
				MessageStack.getInstance().push(Arrays.copyOfRange(decodedMessage, 3, message.length));
			}
			else {
				//Received message is not a watchdog or acknowledgement message, it is ignored
				throw new IllegalArgumentException("Received message is not a watchdog status or an acknowledge message: " + Arrays.toString(decodedMessage));
			}
			
		}
		catch (IllegalArgumentException e) {
			System.out.println("Something went wrong in receiving a message");
			System.out.println(e.getMessage());
		}
	}
}
