package org.aquapackrobotics.sw8s.comms;

import org.aquapackrobotics.sw8s.comms.meb.AHT10GlobalBuffer;
import org.aquapackrobotics.sw8s.comms.meb.MEBListener;
import org.aquapackrobotics.sw8s.comms.meb.MEBStatus;
import org.checkerframework.checker.units.qual.A;
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
    AHT10GlobalBuffer aht10GlobalBuffer;

    @Before
    public void setup() {
        mebListener = new MEBListener();
        writeMessage = new ByteArrayOutputStream();
        aht10GlobalBuffer = new AHT10GlobalBuffer();
    }

    @Test
    public void testAHT10() throws IOException, InterruptedException {
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

        Assert.assertEquals(100.0f, aht10GlobalBuffer.temp.getCurrentValue(), 0);
        Assert.assertEquals(200.0f, aht10GlobalBuffer.humid.getCurrentValue(), 0);
    }
}
