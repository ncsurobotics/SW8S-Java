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

    public MessageStack getInstance() {
        if (ms == null) {
            ms = new MessageStack();
        }
        return ms;
    }

    public void push(String message) {
    	Stack<String> stack = new Stack<String>();
    	stack.push(message);
    	
    	BinaryOperator<Stack<String>> combine = (stack1, stack2) -> {
    		stack1.push(stack2.pop());
    		return stack1;
    	};
    	
    	messages.accumulateAndGet(stack, combine);
    }
    
    public String pop() {
    	UnaryOperator<Stack<String>> pop = (stack1) -> {
    		stack1.pop();
    		return stack1;
    	};
    	
    	Stack<String> stack = messages.getAndUpdate(pop);
    	return stack.pop();
    }
    
}
