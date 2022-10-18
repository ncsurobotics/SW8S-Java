package org.aquapackrobotics.sw8s.comms;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TestComPort implements ICommPort {

    public static final String ReceivedWatchdogMsg = "Received Watchdog";

    private boolean isPortOpened;
    private ICommPortListener portListener;
    private List<String> messageQueue = new ArrayList<>();

    private ByteArrayOutputStream received = new ByteArrayOutputStream();

    private boolean isStartMessage;

    // SPEC
    public static final byte[] WATCHDOG_STRING = "WDGF".getBytes();

    public void openPort(ICommPortListener listener) {
        isPortOpened = true;
        portListener = listener;
    }

    public void writeBytes(byte[] bytes, long length) {
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

    private void readBuffer() {
        byte[] receivedAsBytes = received.toByteArray();

        for (var b : receivedAsBytes) {
            System.out.print(b);
            System.out.print(" ");
        }
        System.out.println(" " + received);


        receivedAsBytes = SerialCommunicationUtility.destructMessage(receivedAsBytes);

        if (Arrays.equals(receivedAsBytes, WATCHDOG_STRING)) {
            // WATCHDOG
            outputResult(ReceivedWatchdogMsg);
        }
    }

    private void outputResult(String result) {
        messageQueue.add(result);
        System.out.println(result);
    }

    public void closePort() {
        isPortOpened = false;
    }

    public List<String> getMessages() {
        return messageQueue;
    }

    private void verifyPortOpened() {
        if (!isPortOpened) {
            throw new RuntimeException("Port on TestComPort was not opened before access.");
        }
    }
}
