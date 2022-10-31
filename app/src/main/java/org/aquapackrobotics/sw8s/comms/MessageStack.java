package org.aquapackrobotics.sw8s.comms;

import java.util.Stack;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BinaryOperator;
import java.util.function.UnaryOperator;

public class MessageStack {
    private static MessageStack ms;
    private AtomicReference<Stack<String>> messages;


    private MessageStack() {
        messages = new AtomicReference<Stack<String>>();
    }

    public static MessageStack getInstance() {
        if (ms == null) {
            ms = new MessageStack();
        }
        return ms;
    }

    public void push(String message) {
    	Stack<String> stack = new Stack<String>();
    	stack.push(message);
    	
    	//This operator pushes the top element of the second stack onto the first and returns it
    	BinaryOperator<Stack<String>> combine = (stack1, stack2) -> {
    		stack1.push(stack2.pop());
    		return stack1;
    	};
    	
    	//This pushes the message onto the message stack
    	messages.accumulateAndGet(stack, combine);
    }
    
    public String pop() {
    	//Should probably rewrite to use an atomic function to update the message stack
    	return messages.get().pop();
    }
    
}
