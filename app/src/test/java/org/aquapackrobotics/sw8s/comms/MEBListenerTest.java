package org.aquapackrobotics.sw8s.comms;

import org.aquapackrobotics.sw8s.comms.meb.MEBListener;
import org.aquapackrobotics.sw8s.comms.meb.MEBStatus;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class MEBListenerTest {
    private byte[] message;
    private MEBListener mebListener;

    ByteArrayOutputStream writeMessage;

    @Before
    public void setup() {
        mebListener = new MEBListener();
        writeMessage = new ByteArrayOutputStream();
    }

    @Test
    public void testAHT10() throws IOException {
        short id = 0;
        byte idLowByte = (byte) (id & 0x00FF);
        byte idHighByte = (byte) ((id & 0xFF00) >> 8);
        writeMessage.writeBytes("AHT10".getBytes());
        writeMessage.write(ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putFloat(100).array());
        writeMessage.write(ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putFloat(200).array());
        writeMessage.write(idHighByte);
        writeMessage.write(idLowByte);
        MessageStruct constructedMessage = SerialCommunicationUtility.constructMessage(writeMessage.toByteArray());
        mebListener.eventBytesHandler(constructedMessage.message);
        Assert.assertEquals(100.0f, MEBStatus.getInstance().temp, 0);
        Assert.assertEquals(200.0f, MEBStatus.getInstance().humid, 0);
    }

    @Test
    public void testLeakTrue() {
        short id = 0;
        byte idLowByte = (byte) (id & 0x00FF);
        byte idHighByte = (byte) ((id & 0xFF00) >> 8);
        writeMessage.writeBytes("LEAK".getBytes());
        writeMessage.write((byte) 1);
        writeMessage.write(idHighByte);
        writeMessage.write(idLowByte);
        MessageStruct constructedMessage = SerialCommunicationUtility.constructMessage(writeMessage.toByteArray());
        mebListener.eventBytesHandler(constructedMessage.message);
        Assert.assertEquals(true, MEBStatus.getInstance().isLeak);
    }

    @Test
    public void testLeakFalse() {
        short id = 0;
        byte idLowByte = (byte) (id & 0x00FF);
        byte idHighByte = (byte) ((id & 0xFF00) >> 8);
        writeMessage.writeBytes("LEAK".getBytes());
        writeMessage.write((byte) 0);
        writeMessage.write(idHighByte);
        writeMessage.write(idLowByte);
        MessageStruct constructedMessage = SerialCommunicationUtility.constructMessage(writeMessage.toByteArray());
        mebListener.eventBytesHandler(constructedMessage.message);
        Assert.assertEquals(false, MEBStatus.getInstance().isLeak);
    }

    @Test
    public void testTarmArmed() {
        short id = 0;
        byte idLowByte = (byte) (id & 0x00FF);
        byte idHighByte = (byte) ((id & 0xFF00) >> 8);
        writeMessage.writeBytes("TARM".getBytes());
        writeMessage.write((byte) 1);
        writeMessage.write(idHighByte);
        writeMessage.write(idLowByte);
        MessageStruct constructedMessage = SerialCommunicationUtility.constructMessage(writeMessage.toByteArray());
        mebListener.eventBytesHandler(constructedMessage.message);
        Assert.assertEquals(true, MEBStatus.getInstance().isArmed);
    }

    @Test
    public void testTarmNotArmed() {
        short id = 0;
        byte idLowByte = (byte) (id & 0x00FF);
        byte idHighByte = (byte) ((id & 0xFF00) >> 8);
        writeMessage.writeBytes("TARM".getBytes());
        writeMessage.write((byte) 0);
        writeMessage.write(idHighByte);
        writeMessage.write(idLowByte);
        MessageStruct constructedMessage = SerialCommunicationUtility.constructMessage(writeMessage.toByteArray());
        mebListener.eventBytesHandler(constructedMessage.message);
        Assert.assertEquals(false, MEBStatus.getInstance().isArmed);
    }

    @Test
    public void testSystemVoltage() throws IOException {
        short id = 0;
        byte idLowByte = (byte) (id & 0x00FF);
        byte idHighByte = (byte) ((id & 0xFF00) >> 8);
        writeMessage.writeBytes("VSYS".getBytes());
        writeMessage.write(ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putFloat(12.0f).array());
        writeMessage.write(idHighByte);
        writeMessage.write(idLowByte);
        MessageStruct constructedMessage = SerialCommunicationUtility.constructMessage(writeMessage.toByteArray());
        mebListener.eventBytesHandler(constructedMessage.message);
        Assert.assertEquals(12.0f, MEBStatus.getInstance().systemVoltage, 0);
    }

    @Test
    public void testShutdown() {
        short id = 0;
        byte idLowByte = (byte) (id & 0x00FF);
        byte idHighByte = (byte) ((id & 0xFF00) >> 8);
        writeMessage.writeBytes("SDOWN".getBytes());
        writeMessage.write((byte) 60);
        writeMessage.write(idHighByte);
        writeMessage.write(idLowByte);
        MessageStruct constructedMessage = SerialCommunicationUtility.constructMessage(writeMessage.toByteArray());
        mebListener.eventBytesHandler(constructedMessage.message);
        Assert.assertEquals(60, MEBStatus.getInstance().shutdownCause, 0);
    }
}
