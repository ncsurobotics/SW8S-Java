package org.aquapackrobotics.sw8s.comms;

import org.junit.Assert;
import org.junit.Test;

/**
 * Tests the SerialCommunicationUtility static class
 */
public class SerialCommunicationUtilityTest {
    private static final byte START_BYTE = (byte) 253;
    private static final byte END_BYTE = (byte) 254;
    private static final byte ESCAPE_BYTE = (byte) 255;

    // Message "MODEL"
    private static byte[] encodedMessage_MODEL = new byte[] {START_BYTE,77,79,68,69,76,33,112,END_BYTE};
    private static byte[] rawMessage_MODEL = new byte[] {77,79,68,69,76};

    // Message "?TINV"
    private static byte[] encodedMessage_TINV = new byte[] {START_BYTE,63,84,73,78,86,80,29,END_BYTE};
    private static byte[] rawMessage_TINV = new byte[] {START_BYTE,63,84,73,78,86,END_BYTE};

    // Message
    public static byte[] encodedMessage_Zeros = new byte[] {START_BYTE,82,65,87,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,96,57,END_BYTE};
    public static byte[] rawMessage_Zeros = new byte[] {82,65,87,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};

    @Test
    public void testDestructModel() {
        Assert.assertEquals(rawMessage_MODEL,
                SerialCommunicationUtility.destructMessage(encodedMessage_MODEL));
    }

    @Test
    public void testDestructTINV() {
        Assert.assertEquals(rawMessage_TINV,
                SerialCommunicationUtility.destructMessage(encodedMessage_TINV));
    }

    @Test
    public void testDestructZeros() {
        Assert.assertEquals(rawMessage_Zeros,
                SerialCommunicationUtility.destructMessage(encodedMessage_Zeros));
    }

    @Test
    public void testConstructModel() {
        Assert.assertEquals(encodedMessage_MODEL,
                SerialCommunicationUtility.constructMessage(rawMessage_MODEL));
    }

    @Test
    public void testConstructTINV() {
        Assert.assertEquals(encodedMessage_TINV,
                SerialCommunicationUtility.constructMessage(rawMessage_TINV));
    }

    @Test
    public void testConstructZeros() {
        Assert.assertEquals(encodedMessage_Zeros,
                SerialCommunicationUtility.constructMessage(rawMessage_Zeros));
    }

    @Test
    public void testMessageMarkers() {
        Assert.assertTrue(SerialCommunicationUtility.isStartOfMessage(START_BYTE));
        Assert.assertTrue(SerialCommunicationUtility.isEndOfMessage(END_BYTE));
    }
}
