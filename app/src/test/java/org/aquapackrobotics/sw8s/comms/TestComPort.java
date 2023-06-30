package org.aquapackrobotics.sw8s.comms;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Mock serial port.
 * Mimics the SW8E Control Board.
 */
public class TestComPort implements ICommPort {

    public static final String ReceivedWatchdogMsg = "Received Watchdog";
    public static final String ReceivedModeMsg = "Received Mode";

    public static final String RequestedModeMsg = "Requested Mode";

    private ICommPortListener portListener;
    private List<String> messageQueue = new ArrayList<>();
    private ByteArrayOutputStream received = new ByteArrayOutputStream();
    private boolean isStartMessage;

    // Robot state
    private boolean isPortOpened;
    private ControlBoardMode robotMode;

    // SPEC
    public static final byte[] WATCHDOG = "WDGF".getBytes();
    public static final String MODE_STRING = "MODE";
    public static final byte[] MODE = MODE_STRING.getBytes();
    public static final byte[] GET_MODE = "?MODE".getBytes();

    public void openPort(ICommPortListener listener) {
        isPortOpened = true;
        portListener = listener;
    }

    public void writeBytes(byte[] bytes, int length) {
        verifyPortOpened();
        
        // Load message in
        boolean isEscape = false;
        for (int i = 0; i < bytes.length && i < length; i++) {
            byte b = bytes[i];

            if (SerialCommunicationUtility.isEscape(b)) {
                isEscape = true;
            } else if (isStartMessage) {
                if (SerialCommunicationUtility.isEndOfMessage(b) && !isEscape) {
                    readBuffer();
                    received.reset();
                    isStartMessage = false;
                } else if (SerialCommunicationUtility.isStartOfMessage(b) && !isEscape) {
                    throw new RuntimeException("Cannot process a start byte after the previous start byte hasn't been closed.");
                } else {
                    received.write(b);
                }

            } else if (SerialCommunicationUtility.isStartOfMessage(b)) {
                isStartMessage = true;
            }
        }

    }

    public void closePort() {
        isPortOpened = false;
    }

    /**
     * Messages are added to this queue in response to properly processed inputs from writeByte.
     * Possible messages are available from this class.
     * @return
     */
    public List<String> getMessages() {
        return messageQueue;
    }

    private void readBuffer() {
        byte[] receivedAsBytes = received.toByteArray();
        
        receivedAsBytes = SerialCommunicationUtility.destructMessage(receivedAsBytes);

        for (byte b : receivedAsBytes) {
            System.out.print(b);
            System.out.print(" ");
        }
        System.out.println(" ");
        for (byte b : receivedAsBytes) {
            System.out.print((char) b);
            System.out.print(" ");
        }

        //Remove message ID
        byte lowByte = receivedAsBytes[1];
        byte highByte = receivedAsBytes[0];
        short id = (short) (((highByte & 0xFF) << 8) | (lowByte & 0xFF));
        receivedAsBytes = Arrays.copyOfRange(receivedAsBytes, 2, receivedAsBytes.length);
        
        processMessage(receivedAsBytes);
    }

    private void processMessage(byte[] message) {
        String messageAsString = new String(message, StandardCharsets.US_ASCII);

        // Watchdog
        if (Arrays.equals(message, WATCHDOG)) {
            // TODO 1000 ms WATCHDOG
            outputResult(ReceivedWatchdogMsg);
        }
        // Set Mode
        else if (messageAsString.startsWith(MODE_STRING)) {
            switch (message[MODE.length]) {
                case 'L':
                    robotMode = ControlBoardMode.LOCAL;
                    break;
                case 'R':
                    robotMode = ControlBoardMode.RAW;
                    break;
                default:
                    robotMode = ControlBoardMode.UNKNOWN;
                    throw new RuntimeException("The mode was not supported");
            }

            outputResult(ReceivedModeMsg);
        }
        // Get Mode
        else if (Arrays.equals(message, GET_MODE)) {
            byte[] sendingMessage = Arrays.copyOf(MODE, MODE.length + 1);
            byte appendedMode = '\0';
            switch (robotMode) {
                case LOCAL:
                    appendedMode = 'L';
                    break;
                case RAW:
                    appendedMode = 'R';
                    break;
                case UNKNOWN:
                    appendedMode = 'U';
                    break;
            }
            sendingMessage[sendingMessage.length - 1] = appendedMode;

            portListener.eventBytesHandler(SerialCommunicationUtility.constructMessage(sendingMessage).message);

            outputResult(RequestedModeMsg);
        }
    }

    private void outputResult(String result) {
        messageQueue.add(result);
        System.out.println(result);
    }

    private void verifyPortOpened() {
        if (!isPortOpened) {
            throw new RuntimeException("Port on TestComPort was not opened before access.");
        }
    }
}
