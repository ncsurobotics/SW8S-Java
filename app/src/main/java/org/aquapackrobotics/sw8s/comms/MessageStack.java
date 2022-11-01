package org.aquapackrobotics.sw8s.comms;

import java.util.concurrent.LinkedBlockingDeque;

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

    public void push(String message) {
    	//This adds the message onto the message deque
    	messages.add(message);
    }
    
    public String pop() {
    	//Returns the first message stored in the deque
    	return messages.remove();
    }
    
}
