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
    	ByteArrayOutputStream acknowledgeMessage = new ByteArrayOutputStream();
    	//Tests sending acknowledge messages with 20 different ack_ids
    	for (short i = 1; i <= 20; i++) {
    		//Appends 'ACK' to acknowledge message
	    	acknowledgeMessage.writeBytes("ACK".getBytes());
	    	//Appends ack_id to acknowledge message
	    	byte idLowByte = (byte) (i & 0x00FF);
	        byte idHighByte = (byte) ((i & 0xFF00) >> 8);
	        acknowledgeMessage.write(idHighByte);
	        acknowledgeMessage.write(idLowByte);
	        //Appends error code to acknowledge message
	        acknowledgeMessage.write((byte)0);
	        //Testing that acknowledge message data is stored, using the id low and high bytes to represent random data
	        acknowledgeMessage.write((byte)idLowByte);
	        acknowledgeMessage.write((byte)idHighByte);
	        //Expected message retrieved from MessageStack should be data containing the idLowByte and idHighByte
	        byte[] expectedMessage = {(byte)idLowByte, (byte)idHighByte};
	        MessageStruct message = SerialCommunicationUtility.constructMessage(acknowledgeMessage.toByteArray());
	        //System.out.println(i);
	        //Sends message bytes to listener
	        listener.eventBytesHandler(message.message);
	        try {
				Assert.assertTrue(Arrays.equals(expectedMessage, MessageStack.getInstance().getMsgById(i)));
			} catch (InterruptedException e) { }
	        acknowledgeMessage.reset();
    	}
    }
    
    @Test
    public void testPartialMessage() {
    	//TODO: Finish test case
    }
    
    @Test
    public void testMultipleMessages() {
    	ByteArrayOutputStream acknowledgeMessageDouble = new ByteArrayOutputStream();
    	ByteArrayOutputStream acknowledgeMessage = new ByteArrayOutputStream();
    	byte[] expectedMessage = new byte[0];

    	short id = 0;
    	//Appends 'ACK' to acknowledge message
    	acknowledgeMessage.writeBytes("ACK".getBytes());
    	//Appends ack_id to acknowledge message
    	byte idLowByte = (byte) (id & 0x00FF);
        byte idHighByte = (byte) ((id & 0xFF00) >> 8);
        acknowledgeMessage.write(idHighByte);
        acknowledgeMessage.write(idLowByte);
        //Appends error code to acknowledge message
        acknowledgeMessage.write((byte)0);
        acknowledgeMessageDouble.writeBytes(SerialCommunicationUtility.constructMessage(acknowledgeMessage.toByteArray()).message);
        
        acknowledgeMessage.reset();
        short id2 = 10;
        //Appends 'ACK' to acknowledge message
    	acknowledgeMessage.writeBytes("ACK".getBytes());
    	//Appends ack_id to acknowledge message
    	idLowByte = (byte) (id2 & 0x00FF);
        idHighByte = (byte) ((id2 & 0xFF00) >> 8);
        acknowledgeMessage.write(idHighByte);
        acknowledgeMessage.write(idLowByte);
        //Appends error code to acknowledge message
        acknowledgeMessage.write((byte)0);
        acknowledgeMessageDouble.writeBytes(SerialCommunicationUtility.constructMessage(acknowledgeMessage.toByteArray()).message);
        
        listener.eventBytesHandler(acknowledgeMessageDouble.toByteArray());
        try {
			Assert.assertTrue(Arrays.equals(expectedMessage, MessageStack.getInstance().getMsgById(id)));
			Assert.assertTrue(Arrays.equals(expectedMessage, MessageStack.getInstance().getMsgById(id2)));

		} catch (InterruptedException e) { }
    }
}
