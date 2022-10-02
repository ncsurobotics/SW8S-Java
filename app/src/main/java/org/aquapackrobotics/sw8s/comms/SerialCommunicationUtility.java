package org.aquapackrobotics.sw8s.comms;

/**
 * Provides helpers for creating and parsing messages
 * that the SW8 control board could use.
 */
public class SerialCommunicationUtility {
    private static final byte START_BYTE = (byte) 253;
    private static final byte END_BYTE = (byte) 254;
    private static final byte ESCAPE_BYTE = (byte) 255;

    /**
     * Takes in a raw message and converts it into the format a SW8 control board can use.
     * @param message The raw message
     * @return The message encoded in the format the SW8 control board uses
     */
    public static byte[] constructMessage(byte[] message) {
        StringBuilder formattedMessage = new StringBuilder();

        formattedMessage.append(START_BYTE);

        // Add escaped message to formatted message
        for (byte msgByte : message) {
            addEscapedByteToBuilder(formattedMessage, msgByte);
        }

        // Add CRC to formatted message
        long crc = CRC.calculateCRC(CRC.Parameters.CRC16, message);
        short crc16 = (short) crc;
        byte lowByte = (byte) (crc16 & 0x00FF);
        byte highByte = (byte) ((crc16 & 0xFF00) >> 8);
        addEscapedByteToBuilder(formattedMessage, highByte);
        addEscapedByteToBuilder(formattedMessage, lowByte);

        formattedMessage.append(END_BYTE);

        return formattedMessage.toString().getBytes();
    }
    /**
     * Takes in an encoded message received from a SW8 control board and converts it into a usable format.
     * @param message An encoded message from a control board
     * @return The raw message extracted from the encoded message
     */
    public static byte[] destructMessage(byte[] message) {
        StringBuilder deformattedMessage = new StringBuilder();

        return deformattedMessage.toString().getBytes();
    }

    private static void addEscapedByteToBuilder(StringBuilder builder, byte msgByte) {
        if (msgByte == END_BYTE || msgByte == START_BYTE || msgByte == ESCAPE_BYTE) {
            builder.append(msgByte);
        }

        builder.append(msgByte);
    }
}
