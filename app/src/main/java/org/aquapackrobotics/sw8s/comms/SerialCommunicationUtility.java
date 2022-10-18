package org.aquapackrobotics.sw8s.comms;

import java.io.ByteArrayOutputStream;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import java.io.IOException;

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
        ByteArrayOutputStream formattedMessage = new ByteArrayOutputStream();

        formattedMessage.write(START_BYTE);

        // Add escaped message to formatted message
        for (byte msgByte : message) {
            addEscapedByteToStream(formattedMessage, msgByte);
        }

        // Add CRC to formatted message
        short crc16 = CRC.CITT16_False(message, message.length);
        byte lowByte = (byte) (crc16 & 0x00FF);
        byte highByte = (byte) ((crc16 & 0xFF00) >> 8);
        addEscapedByteToStream(formattedMessage, highByte);
        addEscapedByteToStream(formattedMessage, lowByte);

        formattedMessage.write(END_BYTE);

        return formattedMessage.toByteArray();
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

        // Verify CRC

        byte lowByte = message[message.length - 1];
        byte highByte = message[message.length - 2];
        short retrievedCRC16 = (short) ((short) (highByte << 8) + lowByte);

        short calculatedCRC16 = CRC.CITT16_False(message, message.length - 2);

        if (retrievedCRC16 != calculatedCRC16) {
            throw new IllegalArgumentException("Calculated CRC16 of message " +
                    ((calculatedCRC16 & 0xFF00) >> 8) + " " + (calculatedCRC16 & 0x00FF) +
                    " is not equal to the attached CRC16 " +
                    ((retrievedCRC16 & 0xFF00) >> 8) + " " + (retrievedCRC16 & 0x00FF));
        }

        // Verification completed, extract the message
        ByteArrayOutputStream deFormattedMessage = new ByteArrayOutputStream();

        // Strip escape bytes
        for (int msgIndex = 0; msgIndex < message.length - 2; msgIndex++) {
            byte msgByte = message[msgIndex];

            if (msgByte != ESCAPE_BYTE)
                deFormattedMessage.write(msgByte);
        }

        return deFormattedMessage.toByteArray();
    }

    /**
     * Write a float in little-endian form to a byte stream
     * @param stream The target stream to write to
     * @param value The float value that will be converted to the SW8 control board format
     */
    public static void writeEncodedFloat(ByteArrayOutputStream stream, float value) {
        //int speedAsInt = Float.floatToRawIntBits(value);

        //stream.write((speedAsInt & 0xFF000000) >> 24);
        //stream.write((speedAsInt & 0x00FF0000) >> 16);
        //stream.write((speedAsInt & 0x0000FF00) >> 8);
        //stream.write(speedAsInt & 0x000000FF);

        try {
            ByteBuffer bb = ByteBuffer.allocate(4);
            bb.order(ByteOrder.LITTLE_ENDIAN);
            bb.putFloat(value);
            stream.write(bb.array());
        } catch(IOException e) {}
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

    /**
     * Checks if a byte is the escape character
     * @param byteMessage The byte to check
     * @return Returns true if the byte is an escape character
     */
    public static boolean isEscape(byte byteMessage) { return byteMessage == ESCAPE_BYTE; }

    private static void addEscapedByteToStream(ByteArrayOutputStream stream, byte msgByte) {
        if (msgByte == END_BYTE || msgByte == START_BYTE || msgByte == ESCAPE_BYTE) {
            stream.write(msgByte);
        }

        stream.write(msgByte);
    }
}
