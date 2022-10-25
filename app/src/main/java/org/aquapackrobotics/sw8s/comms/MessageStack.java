package org.aquapackrobotics.sw8s.comms;

import java.util.Stack;

public class MessageStack {
    private static MessageStack ms;
    private Stack<String> messages;


    private MessageStack() {
        messages = new Stack<>();
    }

    public MessageStack getInstance() {
        if (ms == null) {
            ms = new MessageStack();
        }
        return ms;
    }


}
