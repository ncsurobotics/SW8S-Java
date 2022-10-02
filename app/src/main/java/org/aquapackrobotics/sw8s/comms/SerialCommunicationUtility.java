package org.aquapackrobotics.sw8s.comms;

import java.util.Arrays;

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
     * @param message An encoded message from a control board. Should not include the start and end bytes.
     * @return The raw message extracted from the encoded message
     */
    public static byte[] destructMessage(byte[] message) throws IllegalArgumentException {
        // Verify there is enough length to hold at least 1 byte of message and 2 bytes of CRC16
        if (message == null) {
            throw new IllegalArgumentException("Message argument was null");
        } else if (message.length < 3) {
            throw new IllegalArgumentException("Message argument was too short to hold a message and a CRC16");
        }

        byte[] strippedMessage = Arrays.copyOfRange(message, 0, message.length - 2);

        // Verify CRC

        byte lowByte = message[message.length - 1];
        byte highByte = message[message.length - 2];
        short retrievedCRC16 = (short) ((highByte << 8) + lowByte);

        long calculatedCRC = CRC.calculateCRC(CRC.Parameters.CRC16, strippedMessage);
        short calculatedCRC16 = (short) calculatedCRC;

        if (retrievedCRC16 != calculatedCRC16) {
            throw new IllegalArgumentException("Calculated CRC16 of message is not equal to the attached CRC16");
        }

        // Verification completed, extract the message
        StringBuilder deFormattedMessage = new StringBuilder();

        // Strip escape bytes
        for (byte msgByte : strippedMessage) {
            if (msgByte != ESCAPE_BYTE)
                deFormattedMessage.append(msgByte);
        }

        return deFormattedMessage.toString().getBytes();
    }

    /**
     * Checks if a byte is the start of a message.
     * @param byteMessage The byte to check
     * @return Returns true if the byte is the start of a message
     */
    public static boolean isStartOfMessage(byte byteMessage) {
        return byteMessage == START_BYTE;
    }

    /**
     *Checks if a byte is the end of a message.
     * @param byteMessage The byte to check
     * @return Returns true if the byte is the end of a message
     */
    public static boolean isEndOfMessage(byte byteMessage) {
        return byteMessage == END_BYTE;
    }

    private static void addEscapedByteToBuilder(StringBuilder builder, byte msgByte) {
        if (msgByte == END_BYTE || msgByte == START_BYTE || msgByte == ESCAPE_BYTE) {
            builder.append(msgByte);
        }

        builder.append(msgByte);
    }
}
