package org.aquapackrobotics.sw8s.comms;

import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

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
    	//This adds the message onto the message deque
       
        byte lowByte = message[0];
        byte highByte = message[1];

        short id = (short) (((highByte & 0xFF) << 8) | (lowByte & 0xFF));

        byte[] msg = Arrays.copyOfRange(message, 2, message.length);
    	messages.put(id, msg); // puts inside 

        AcknowledgeMessageStruct ack = new AcknowledgeMessageStruct();
        ack.acknowledgeId = id;
        ack.errorCode = msg[3];
        ack.data = Arrays.copyOfRange(message, 4, message.length);
        System.out.println(ack.errorCode);
    }
    
    /**
     * Removes the first element of the deque and returns it. Also features a timeout if no element is available.
     * @return the first element of the deque, or null if no element during specified timeout
     * @throws InterruptedException if interrupted while waiting for element to become available
     */
    public byte[] getMsgById(short id) throws InterruptedException {
    	//Returns the first message stored in the map
        Thread.sleep(250);
        AcknowledgeMessageStruct msg = null;
        while ((msg.data = messages.remove(id)) == null)  {
            Thread.sleep(1);
        }
    	return msg.data;
    }
    
    /**
     * Clears the MessageStack by setting the singleton to null.
     */
    public static void clear() {
    	ms = null;
    }
}
