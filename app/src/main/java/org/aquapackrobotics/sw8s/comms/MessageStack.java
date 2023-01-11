package org.aquapackrobotics.sw8s.comms;

import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

/**
 * A singleton of a stack of incoming messages.
 * Pop to consume.
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
        short id = (short) message[3];
        byte[] msg = Arrays.copyOfRange(message, 4, message.length);
    	messages.put(id, msg);
    }
    
    /**
     * Removes the first element of the deque and returns it. Also features a timeout if no element is available.
     * @return the first element of the deque, or null if no element during specified timeout
     * @throws InterruptedException if interrupted while waiting for element to become available
     */
    public byte[] getMsgById(short id) {
    	//Returns the first message stored in the deque
    	return messages.remove(id);
    }
    
    /**
     * Clears the MessageStack by setting the singleton to null.
     */
    public static void clear() {
    	ms = null;
    }
}
