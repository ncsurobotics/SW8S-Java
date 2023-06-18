package org.aquapackrobotics.sw8s.comms;

import java.io.IOException;

import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

import java.util.Enumeration;
import java.util.logging.*;

/**
 * A singleton {@link ConcurrentHashMap} of message IDs and their corresponding acknowledgements.
 */
public class MessageStack {
    private static MessageStack ms;
    private ConcurrentHashMap<Short, byte[]> messages;
    private Logger logger;

    private MessageStack() {
        messages = new ConcurrentHashMap<Short, byte[]>();

        logger = Logger.getLogger("Comms_In");
        logger.setUseParentHandlers(false);
        for (var h : logger.getHandlers()) logger.removeHandler(h);
        try {
            FileHandler fHandle = new FileHandler("%t/Comms_In.log", true);
            fHandle.setFormatter(new SimpleFormatter());
            logger.addHandler(fHandle);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Singleton pattern.
     * @return the global instance of this class.
     */
    public static MessageStack getInstance() {
        if (ms == null) {
            ms = new MessageStack();
        }
        return ms;
    }

    private void logResponse(short id, byte[] message) {
        logger.info(id + " | " + Arrays.toString(message));
    }
    
    /**
     * Puts the given message at the front of the message deque
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
     * Removes the first element of the deque and returns it. Also features a timeout if no element is available.
     * @return the first element of the deque, or null if no element during specified timeout
     * @throws InterruptedException if interrupted while waiting for element to become available
     */
    public byte[] getMsgById(short id) throws InterruptedException {
        //Returns the first message stored in the map
        byte[] msg;
        Enumeration enu = messages.keys();
        //while (enu.hasMoreElements()) {
            //System.out.println("STACK ENTRY: " + enu.nextElement());
        //}
        while ((msg = messages.remove(id)) == null)  {
            Thread.sleep(1);
        }
        return msg;
    }
    
    /**
     * Clears the MessageStack by setting the singleton to null.
     */
    public static void clear() {
        ms = null;
    }
}