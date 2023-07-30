package org.aquapackrobotics.sw8s.comms.meb;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.Arrays;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import org.aquapackrobotics.sw8s.comms.*;

/**
 * Synchronous SW8 control board communication handler.
 * As a general rule:
 * Setting functions do not block
 * Getting functions block
 */
public class MEBCommunication {
    private final ICommPort MEBPort;

    private static final byte START_BYTE = (byte) 253;
    private static final byte END_BYTE = (byte) 254;
    private static final byte ESCAPE_BYTE = (byte) 255;
    private static Short uniqueID = 0;

    private static final byte[] RESET_MSB = new byte[] { 'M', 'S', 'B', 0x00 };
    private static final byte[] DROP_1 = new byte[] { 'M', 'S', 'B', 0x01 };
    private static final byte[] DROP_2 = new byte[] { 'M', 'S', 'B', 0x02 };
    private static final byte[] TORPEDO_1 = new byte[] { 'M', 'S', 'B', 0x03 };
    private static final byte[] TORPEDO_2 = new byte[] { 'M', 'S', 'B', 0x04 };

    private Logger logger;
    private MEBListener listener;

    /**
     * Construct a new MEBCommunication listening and writing on the given port
     * 
     * @param port The port to listen on
     */
    public MEBCommunication(ICommPort port) {
        MEBPort = port;
        listener = new MEBListener();
        MEBPort.openPortMEB(listener);

        logger = Logger.getLogger("MEB_Comms_Out");
        logger.setUseParentHandlers(false);
        for (var h : logger.getHandlers())
            logger.removeHandler(h);
        try {
            new File("/mnt/data/comms/meb").mkdir();
            FileHandler fHandle = new FileHandler("/mnt/data/comms/meb/out" + Instant.now().toString() + ".log", true);
            fHandle.setFormatter(new SimpleFormatter());
            logger.addHandler(fHandle);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Call this at the end of the lifetime to free the serial ports.
     */
    public void dispose() {
        MEBPort.closePort();
    }

    private void logCommand(MessageStruct msg, String code, String data) {
        logger.info(code + " | " + Short.toString(msg.id) + " | " + data +
                " | " + Arrays.toString(msg.message));
    }

    /**
     * Resets MSB
     * 
     * @return the message ID of the sent message.
     */
    public short resetMSB() throws InterruptedException {
        ByteArrayOutputStream message = new ByteArrayOutputStream();

        message.writeBytes(RESET_MSB);

        MessageStruct messageStruct = constructMessage(message.toByteArray());
        byte[] messageBytes = messageStruct.message;
        short msgID = messageStruct.id;
        MEBPort.writeBytes(messageBytes, messageBytes.length);

        logCommand(messageStruct, "Reset MSB", String.valueOf(msgID));
        return msgID;
    }

    /**
     * Triggers dropper 1
     * 
     * @return the message ID of the sent message.
     */
    public short drop_1() throws InterruptedException {
        ByteArrayOutputStream message = new ByteArrayOutputStream();

        message.writeBytes(DROP_1);

        MessageStruct messageStruct = constructMessage(message.toByteArray());
        byte[] messageBytes = messageStruct.message;
        short msgID = messageStruct.id;
        MEBPort.writeBytes(messageBytes, messageBytes.length);

        logCommand(messageStruct, "Trigger dropper 1", String.valueOf(msgID));
        return msgID;
    }

    /**
     * Triggers dropper 2
     * 
     * @return the message ID of the sent message.
     */
    public short drop_2() throws InterruptedException {
        ByteArrayOutputStream message = new ByteArrayOutputStream();

        message.writeBytes(DROP_2);

        MessageStruct messageStruct = constructMessage(message.toByteArray());
        byte[] messageBytes = messageStruct.message;
        short msgID = messageStruct.id;
        MEBPort.writeBytes(messageBytes, messageBytes.length);

        logCommand(messageStruct, "Trigger dropper 2", String.valueOf(msgID));
        return msgID;
    }

    /**
     * Triggers torpedo 1
     * 
     * @return the message ID of the sent message.
     */
    public short torpedo_1() throws InterruptedException {
        ByteArrayOutputStream message = new ByteArrayOutputStream();

        message.writeBytes(TORPEDO_1);

        MessageStruct messageStruct = constructMessage(message.toByteArray());
        byte[] messageBytes = messageStruct.message;
        short msgID = messageStruct.id;
        MEBPort.writeBytes(messageBytes, messageBytes.length);

        logCommand(messageStruct, "Trigger torpedo 1", String.valueOf(msgID));
        return msgID;
    }

    /**
     * Triggers torpedo 2
     * 
     * @return the message ID of the sent message.
     */
    public short torpedo_2() throws InterruptedException {
        ByteArrayOutputStream message = new ByteArrayOutputStream();

        message.writeBytes(TORPEDO_2);

        MessageStruct messageStruct = constructMessage(message.toByteArray());
        byte[] messageBytes = messageStruct.message;
        short msgID = messageStruct.id;
        MEBPort.writeBytes(messageBytes, messageBytes.length);

        logCommand(messageStruct, "Trigger torpedo 2", String.valueOf(msgID));
        return msgID;
    }

    /**
     * Wraps the corresponding method in the internal {@link MEBListener}
     */
    public byte[] getMsgById(short id) throws InterruptedException {
        return listener.getMsgById(id);
    }

    /**
     * Takes in a raw message and converts it into the format a SW8 control board
     * can use.
     * 
     * @param message The raw message
     * @return The message encoded in the format the SW8 control board uses
     */
    public static MessageStruct constructMessage(byte[] message) {
        ByteArrayOutputStream formattedMessage = new ByteArrayOutputStream();

        formattedMessage.write(START_BYTE);
        short messageId = incrementId();
        byte idLowByte = (byte) (messageId & 0x00FF);
        byte idHighByte = (byte) ((messageId & 0xFF00) >> 8);

        addEscapedByteToStream(formattedMessage, idHighByte);
        addEscapedByteToStream(formattedMessage, idLowByte);

        // Add escaped message to formatted message
        for (byte msgByte : message) {
            addEscapedByteToStream(formattedMessage, msgByte);
        }

        // calculate CRC
        ByteArrayOutputStream crcMessage = new ByteArrayOutputStream();
        crcMessage.write(idHighByte);
        crcMessage.write(idLowByte);
        crcMessage.writeBytes(message);

        short crc16 = CRC.CITT16_False(crcMessage.toByteArray(), crcMessage.size());

        // Append CRC
        byte lowByte = (byte) (crc16 & 0x00FF);
        byte highByte = (byte) ((crc16 & 0xFF00) >> 8);
        addEscapedByteToStream(formattedMessage, highByte);
        addEscapedByteToStream(formattedMessage, lowByte);

        formattedMessage.write(END_BYTE);
        MessageStruct ms = new MessageStruct();
        ms.message = formattedMessage.toByteArray();
        ms.id = messageId;

        return ms;
    }

    public static Short incrementId() {
        short temp;
        synchronized (uniqueID) {
            temp = uniqueID;
            uniqueID++;
            if (uniqueID == 32767) {
                uniqueID = 0;
            }
        }

        return temp;
    }

    private static void addEscapedByteToStream(ByteArrayOutputStream stream, byte msgByte) {
        if (msgByte == END_BYTE || msgByte == START_BYTE || msgByte == ESCAPE_BYTE) {
            stream.write(ESCAPE_BYTE);
        }

        stream.write(msgByte);
    }
}
