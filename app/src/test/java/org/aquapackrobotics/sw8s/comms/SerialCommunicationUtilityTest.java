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
    private static final byte[] encodedMessage_MODEL = new byte[] {77,79,68,69,76,33,112};
    private static final byte[] rawMessage_MODEL = new byte[] {77,79,68,69,76};

    // Message "?TINV"
    private static final byte[] encodedMessage_TINV = new byte[] {63,84,73,78,86,80,29};
    private static final byte[] rawMessage_TINV = new byte[] {63,84,73,78,86};

    // Message zeros
    public static final byte[] encodedMessage_Zeros = new byte[] {82,65,87,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,96,57};
    public static final byte[] rawMessage_Zeros = new byte[] {82,65,87,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};

    @Test
    public void testDestructModel() {
        Assert.assertArrayEquals(rawMessage_MODEL,
                SerialCommunicationUtility.destructMessage(encodedMessage_MODEL));
    }

    @Test
    public void testDestructTINV() {
        Assert.assertArrayEquals(rawMessage_TINV,
                SerialCommunicationUtility.destructMessage(encodedMessage_TINV));
    }

    @Test
    public void testDestructZeros() {
        Assert.assertArrayEquals(rawMessage_Zeros,
                SerialCommunicationUtility.destructMessage(encodedMessage_Zeros));
    }

    @Test
    public void testConstructModel() {
        TEMPprintArray(appendStartEndMarkers(encodedMessage_Zeros),
                SerialCommunicationUtility.constructMessage(rawMessage_Zeros));
        
        Assert.assertArrayEquals(appendStartEndMarkers(encodedMessage_MODEL),
                SerialCommunicationUtility.constructMessage(rawMessage_MODEL));
    }

    @Test
    public void testConstructTINV() {
        TEMPprintArray(appendStartEndMarkers(encodedMessage_TINV),
                SerialCommunicationUtility.constructMessage(rawMessage_TINV));

        Assert.assertArrayEquals(appendStartEndMarkers(encodedMessage_TINV),
                SerialCommunicationUtility.constructMessage(rawMessage_TINV));
    }

    @Test
    public void testConstructZeros() {
        TEMPprintArray(appendStartEndMarkers(encodedMessage_Zeros),
                SerialCommunicationUtility.constructMessage(rawMessage_Zeros));

        Assert.assertArrayEquals(appendStartEndMarkers(encodedMessage_Zeros),
                SerialCommunicationUtility.constructMessage(rawMessage_Zeros));
    }

    @Test
    public void testCRC16() {
        short crc16 = CRC.CITT16_False(rawMessage_MODEL, rawMessage_MODEL.length);
        byte lowByte = (byte) (crc16 & 0x00FF);
        byte highByte = (byte) ((crc16 & 0xFF00) >> 8);

        Assert.assertEquals(highByte, encodedMessage_MODEL[encodedMessage_MODEL.length - 2]);
        Assert.assertEquals(lowByte, encodedMessage_MODEL[encodedMessage_MODEL.length - 1]);
    }

    @Test
    public void testMessageMarkers() {
        Assert.assertTrue(SerialCommunicationUtility.isStartOfMessage(START_BYTE));
        Assert.assertTrue(SerialCommunicationUtility.isEndOfMessage(END_BYTE));
    }

    private byte[] appendStartEndMarkers(byte[] original) {
        byte[] wrappedOriginal = new byte[original.length + 2];

        System.arraycopy(original, 0, wrappedOriginal, 1, original.length);

        wrappedOriginal[0] = START_BYTE;
        wrappedOriginal[wrappedOriginal.length - 1] = END_BYTE;

        return wrappedOriginal;
    }

    private void TEMPprintArray(byte[] one, byte[] two) {
        for (byte b : one) {
            System.out.print((int) b);
            System.out.print(" ");
        }

        System.out.print("\n");

        for (byte b : one) {
            System.out.print((int) b);
            System.out.print(" ");
        }
    }
}
