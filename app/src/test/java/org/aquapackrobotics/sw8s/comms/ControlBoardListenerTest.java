package org.aquapackrobotics.sw8s.comms;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ControlBoardListenerTest {
    private ControlBoardListener listener;
    
    private static byte[] watchdogEnableMessage;
    private static byte[] watchdogKillMessage;

    @Before
    public void setup() {
        listener = new ControlBoardListener();
        
        //Sets watchdog enable and watchdog kill messages for testing
        ByteArrayOutputStream watchdogMessage = new ByteArrayOutputStream();
        
        watchdogMessage.writeBytes("WDGS".getBytes());
        watchdogMessage.write((byte)1);
        watchdogEnableMessage = watchdogMessage.toByteArray();
        
        watchdogMessage.reset();
        System.out.println(watchdogMessage.toString());
        watchdogMessage.writeBytes("WDGS".getBytes());
        watchdogMessage.write((byte)0);
        watchdogKillMessage = watchdogMessage.toByteArray();

    }

    @After
    public void cleanup() {
        MessageStack.clear();
    }
    
    private byte[] generateAcknowledgeMessage(short id, byte errorCode, byte[] data) {
    	ByteArrayOutputStream acknowledgeMessage = new ByteArrayOutputStream();

    	//Appends 'ACK' to acknowledge message
    	acknowledgeMessage.writeBytes("ACK".getBytes());
    	//Appends ack_id to acknowledge message
    	byte idLowByte = (byte) (id & 0x00FF);
        byte idHighByte = (byte) ((id & 0xFF00) >> 8);
        acknowledgeMessage.write(idHighByte);
        acknowledgeMessage.write(idLowByte);
        //Appends error code to acknowledge message
        acknowledgeMessage.write(errorCode);
        acknowledgeMessage.writeBytes(data);
        return SerialCommunicationUtility.constructMessage(acknowledgeMessage.toByteArray()).message;
    }

    @Test
    public void testWatchDogStatus() {
    	//Makes sure WatchDogKill is initially set to false
    	Assert.assertFalse(WatchDogStatus.getInstance().getWatchDogKill());
    	
    	//Sends watchdog enable message
    	listener.eventBytesHandler(SerialCommunicationUtility.constructMessage(watchdogEnableMessage).message);
    	Assert.assertFalse(WatchDogStatus.getInstance().getWatchDogKill());

    	//Sends watchdog kill message
    	listener.eventBytesHandler(SerialCommunicationUtility.constructMessage(watchdogKillMessage).message);
    	Assert.assertTrue(WatchDogStatus.getInstance().getWatchDogKill());
    	
    	//Sends watchdog enable message
    	listener.eventBytesHandler(SerialCommunicationUtility.constructMessage(watchdogEnableMessage).message);
    	Assert.assertFalse(WatchDogStatus.getInstance().getWatchDogKill());
    }
    
    @Test
    public void testAcknowledgeMessage() {
    	//Tests sending acknowledge messages with 20 different ack_ids
    	for (short id = 1; id <= 20; id++) {
    		byte idLowByte = (byte) (id & 0x00FF);
            byte idHighByte = (byte) ((id & 0xFF00) >> 8);
	        //Expected message retrieved from MessageStack should be data containing the idLowByte and idHighByte
	        byte[] expectedMessageData = {(byte)idLowByte, (byte)idHighByte};
	        //Generates acknowledge message
    		byte[] acknowledgeMessage = generateAcknowledgeMessage(id, (byte)0, expectedMessageData);

	        //System.out.println(i);
	        //Sends message bytes to listener
	        listener.eventBytesHandler(acknowledgeMessage);
	        try {
				Assert.assertTrue(Arrays.equals(expectedMessageData, MessageStack.getInstance().getMsgById(id)));
			} catch (InterruptedException e) { }
    	}
    }
    
    @Test
    public void testPartialMessage() {
    	ByteArrayOutputStream acknowledgeMessage = new ByteArrayOutputStream();
    	byte[] firstHalfAcknowledge;
    	byte[] secondHalfAcknowledge;
    	byte[] expectedMessageData = {(byte)255};
    	
    	short id = 0;
    	byte[] fullMessage = generateAcknowledgeMessage(id, (byte)0, expectedMessageData);

    	firstHalfAcknowledge = Arrays.copyOfRange(fullMessage, 0, fullMessage.length / 2);
        secondHalfAcknowledge = Arrays.copyOfRange(fullMessage, fullMessage.length / 2, fullMessage.length);
        
        listener.eventBytesHandler(firstHalfAcknowledge);
        listener.eventBytesHandler(secondHalfAcknowledge);
        
        try {
			Assert.assertTrue(Arrays.equals(expectedMessageData, MessageStack.getInstance().getMsgById(id)));
		} catch (InterruptedException e) { }
        acknowledgeMessage.reset();
        
    }
    
    @Test
    public void testMultipleMessages() {
    	ByteArrayOutputStream acknowledgeMessageDouble = new ByteArrayOutputStream();
    	byte[] expectedMessage = new byte[0];

    	short id = 0;
        acknowledgeMessageDouble.writeBytes(generateAcknowledgeMessage(id, (byte)0, expectedMessage));
        
        short id2 = 10;
        acknowledgeMessageDouble.writeBytes(generateAcknowledgeMessage(id2, (byte)0, expectedMessage));

        listener.eventBytesHandler(acknowledgeMessageDouble.toByteArray());
        try {
			Assert.assertTrue(Arrays.equals(expectedMessage, MessageStack.getInstance().getMsgById(id)));
			Assert.assertTrue(Arrays.equals(expectedMessage, MessageStack.getInstance().getMsgById(id2)));

		} catch (InterruptedException e) { }
    }
}
