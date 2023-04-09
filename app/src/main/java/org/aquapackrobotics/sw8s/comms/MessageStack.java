package org.aquapackrobotics.sw8s.comms;

import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

import java.util.Enumeration;

/**
 * A singleton {@link ConcurrentHashMap} of message IDs and their corresponding acknowledgements.
 */
public class MessageStack {
    private static MessageStack ms;
    private ConcurrentHashMap<Short, byte[]> messages;

    private MessageStack() {
        messages = new ConcurrentHashMap<Short, byte[]>();
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

        // If there's an error, exit
        if (errorCode != (byte) 0) {
            String errorMsg = "Error code " + errorCode + " with ID " + id + " and message " ;
            for (var c : data) {
                errorMsg += (byte) c + " ";
            }
            System.out.print(errorMsg);
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
