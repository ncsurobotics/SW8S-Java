package org.aquapackrobotics.sw8s.comms.meb;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.time.Instant;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import org.aquapackrobotics.sw8s.comms.*;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;

/**
 * MEBListener listens for messages from a comm port.
 * See SerialCommunicationUtility for message implementation details.
 */
public class MEBListener implements SerialPortDataListener, ICommPortListener {

    // Acknowledgements
    private static final String ACKNOWLEDGE = "ACK";
    private static final String AHT10 = "AHT10";
    private static final String LEAK = "LEAK";
    private static final String TARM = "TARM";
    private static final String VSYS = "VSYS";
    private static final String SHUTDOWN = "SDOWN";

    private static ByteArrayOutputStream messageStore = new ByteArrayOutputStream();
    private static boolean parseStarted = true;
    private static boolean parseEscaped = false;
    private static ConcurrentHashMap<Short, byte[]> messages = new ConcurrentHashMap<Short, byte[]>();
    private static Logger logger;

    private static boolean wasArmed = false;

    private static MEBStatus mebStatus = MEBStatus.getInstance();

    static {
        logger = Logger.getLogger("MEB_Comms_In");
        logger.setUseParentHandlers(false);
        for (var h : logger.getHandlers())
            logger.removeHandler(h);
        try {
            new File("/mnt/data/comms/meb/in").mkdirs();
            FileHandler fHandle = new FileHandler("/mnt/data/comms/meb/in" + Instant.now().toString() + ".log", true);
            fHandle.setFormatter(new SimpleFormatter());
            logger.addHandler(fHandle);
        } catch (IOException e) {
            e.printStackTrace();
        }
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
        try {
            int size = event.getSerialPort().bytesAvailable();
            byte[] message = new byte[size];
            event.getSerialPort().readBytes(message, size);

            eventBytesHandler(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void tcpEvent(TCPCommPort tcp) throws IOException {
        byte[] message = tcp.getBytesAvailable();
        eventBytesHandler(message);
    }

    private void logCommand(byte[] msg, String code, String data) {
        logger.info(code + " | " + data +
                " | " + Arrays.toString(msg));
    }

    /**
     * Processes bytes from a serial port listening event that may contain an
     * incomplete or multiple messages
     * 
     * @param message the bytes to process
     */
    public void eventBytesHandler(byte[] message) {
        for (byte b : message) {
            if (parseEscaped) {
                // Currently escaped
                // Handle valid escape sequences
                if (SerialCommunicationUtility.isStartOfMessage(b) || SerialCommunicationUtility.isEndOfMessage(b)
                        || SerialCommunicationUtility.isEscape(b))
                    messageStore.write(b);

                // No longer escaped
                parseEscaped = false;
            } else if (parseStarted) {
                if (SerialCommunicationUtility.isStartOfMessage(b)) {
                    // Handle start byte (not escaped)
                    // Discard old data
                    messageStore.reset();
                } else if (SerialCommunicationUtility.isEndOfMessage(b)) {
                    // Handle end byte (not escaped)
                    // messageStore now contains entire message

                    try {
                        // Checks CRC using destructMessage()
                        byte[] destructedMessage = SerialCommunicationUtility
                                .destructMessage(messageStore.toByteArray());
                        // This is a valid message, sends to serialMessageHandler
                        messageHandler(destructedMessage);
                    } catch (IllegalArgumentException e) {
                        // Catches any exceptions thrown from destructMessage such as invalid CRC
                        // Invalid message so restarts parse
                        parseStarted = false;
                    }
                } else if (SerialCommunicationUtility.isEscape(b)) {
                    // Handle escape byte (not escaped)
                    parseEscaped = true;
                } else {
                    messageStore.write(b);
                }
            } else if (SerialCommunicationUtility.isStartOfMessage(b)) {
                parseStarted = true;
                messageStore.reset();
            }
        }
    }

    /**
     * Processes a complete message's payload.
     * The message is assumed to have had the correct CRC and start and end with the
     * proper bytes
     * 
     * @param message The message to process
     */
    public void messageHandler(byte[] message) {
        try {
            // Remove message ID of received message
            byte[] strippedMessage = Arrays.copyOfRange(message, 2, message.length);

            if (ByteArrayUtility.startsWith(strippedMessage, ACKNOWLEDGE.getBytes())) {
                // Pushes message onto message stack if acknowledge message
                push(Arrays.copyOfRange(strippedMessage, 3, strippedMessage.length));
            } else if (ByteArrayUtility.startsWith(strippedMessage, AHT10.getBytes())) {
                byte[] data = Arrays.copyOfRange(strippedMessage, 5, strippedMessage.length);
                for (byte s : strippedMessage) {
                    System.out.println(s);
                }
                ByteBuffer buffer = ByteBuffer.wrap(data);
                buffer.order(ByteOrder.LITTLE_ENDIAN);
                float temp = buffer.getFloat();
                float humid = buffer.getFloat();
                mebStatus.temp = temp;
                mebStatus.humid = humid;
                logCommand(data, "AHT10", String.format("%f, %f", temp, humid));
            } else if (ByteArrayUtility.startsWith(strippedMessage, LEAK.getBytes())) {
                byte[] data = Arrays.copyOfRange(strippedMessage, 4, strippedMessage.length);
                byte leakStatus = strippedMessage[4];
                if (leakStatus == (byte) 1) {
                    mebStatus.isLeak = true;
                } else {
                    mebStatus.isLeak = false;
                }
                logCommand(data, "LEAK", mebStatus.isLeak ? "1" : "0");
            } else if (ByteArrayUtility.startsWith(strippedMessage, TARM.getBytes())) {
                byte armStatus = strippedMessage[4];
                byte[] data = Arrays.copyOfRange(strippedMessage, 4, strippedMessage.length);
                if (armStatus == (byte) 1) {
                    mebStatus.isArmed = true;
                    wasArmed = true;
                    System.out.println("ARMED");
                } else {
                    mebStatus.isArmed = false;
                    System.out.println("KILLED");
                    if (wasArmed) {
                        System.out.println("Kill after arm, exiting");
                        System.exit(5);
                    }
                }
                logCommand(data, "TARM", mebStatus.isArmed ? "1" : "0");
            } else if (ByteArrayUtility.startsWith(strippedMessage, VSYS.getBytes())) {
                byte[] data = Arrays.copyOfRange(strippedMessage, 4, strippedMessage.length);
                ByteBuffer buffer = ByteBuffer.wrap(data);
                buffer.order(ByteOrder.LITTLE_ENDIAN);
                float voltage = buffer.getFloat();
                mebStatus.systemVoltage = voltage;
                logCommand(data, "VSYS", String.format("%f", voltage));
            } else if (ByteArrayUtility.startsWith(strippedMessage, SHUTDOWN.getBytes())) {
                byte[] data = Arrays.copyOfRange(strippedMessage, 5, strippedMessage.length);
                ByteBuffer buffer = ByteBuffer.wrap(data);
                int cause = buffer.get();
                mebStatus.shutdownCause = cause;
            } else {
                // push(Arrays.copyOfRange(strippedMessage, 3, strippedMessage.length));
                // Received message is not an acknowledgement message, it is ignored
                /*
                 * throw new IllegalArgumentException(
                 * "Received message is not a watchdog status or an acknowledge message: "
                 * + Arrays.toString(strippedMessage));
                 */
            }

        } catch (IllegalArgumentException e) {
            System.out.println("Something went wrong in receiving a message");
            System.out.println(e.getMessage());
        }
    }

    private void logResponse(short id, byte[] message) {
        logger.info(id + " | " + Arrays.toString(message));
    }

    /**
     * Puts the given message in the map
     * 
     * @param message message to add
     */
    public void push(byte[] message) {

        // ID
        byte lowByte = message[1];
        byte highByte = message[0];
        short id = (short) (((highByte & 0xFF) << 8) | (lowByte & 0xFF));

        // Error code
        int errorCode = message[2];

        // Data
        byte[] data = Arrays.copyOfRange(message, 3, message.length);

        logResponse(id, message);

        // If there's an error, exit
        if (errorCode != (byte) 0) {
            String errorMsg = "Error code " + errorCode + " with ID " + id + " and message " + Arrays.toString(message);
            for (var c : data) {
                errorMsg += (byte) c + " ";
            }
            System.out.println(errorMsg);
            return;
        }

        // Payload
        messages.put(id, data);
    }

    /**
     * Removes the matching message and returns it. Also features a timeout if no
     * element is available.
     * 
     * @return the matching element, or null if no element during
     *         specified timeout
     * @throws InterruptedException if interrupted while waiting for element to
     *                              become available
     */
    public byte[] getMsgById(short id) throws InterruptedException {
        // Returns the first message stored in the map
        byte[] msg;
        while ((msg = messages.remove(id)) == null) {
            Thread.sleep(1);
        }
        return msg;
    }

}
