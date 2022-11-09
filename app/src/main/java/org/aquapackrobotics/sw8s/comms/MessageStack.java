package org.aquapackrobotics.sw8s.comms;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

public class MessageStack {
    private static MessageStack ms;
    private LinkedBlockingDeque<String> messages;


    private MessageStack() {
        messages = new LinkedBlockingDeque<String>();
    }

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
    public void push(String message) {
    	//This adds the message onto the message deque
    	messages.addFirst(message);
    }
    
    /**
     * Removes the first element of the deque and returns it. Also features a timeout if no element is available.
     * @param timeout the length of the timeout
     * @param unit the time unit the timeout is specified in
     * @return the first element of the deque, or null if no element during specified timeout
     * @throws InterruptedException if interrupted while waiting for element to become available
     */
    public String pop(long timeout, TimeUnit unit) throws InterruptedException {
    	//Returns the first message stored in the deque
    	return messages.pollFirst(timeout, unit);
    }
    
    /**
     * Clears the MessageStack by setting the singleton to null.
     */
    public static void clear() {
    	ms = null;
    }
}
