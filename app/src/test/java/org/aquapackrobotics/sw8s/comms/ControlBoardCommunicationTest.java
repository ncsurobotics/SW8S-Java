package org.aquapackrobotics.sw8s.comms;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ControlBoardCommunicationTest {
    private TestComPort port;
    private ControlBoardCommunication controlBoard;

    @Before
    public void setup() {
        port = new TestComPort();
        controlBoard = new ControlBoardCommunication(port);
    }

    @After
    public void cleanup() {
        port.closePort();
        port = null;
    }

    @Test
    public void testWatchdog() {
        controlBoard.feedWatchdogMotor();

        Assert.assertTrue(port.getMessages().contains(TestComPort.ReceivedWatchdogMsg));
    }

    @Test
    public void testSetMode() {
        controlBoard.setMode(ControlBoardMode.LOCAL);

        Assert.assertTrue(port.getMessages().contains(TestComPort.ReceivedModeMsg));
    }

    @Test public void testGetMode() throws InterruptedException {

        controlBoard.setMode(ControlBoardMode.LOCAL);

        ControlBoardMode retrievedMode = controlBoard.getMode();

        Assert.assertEquals(retrievedMode, ControlBoardMode.LOCAL);
        Assert.assertTrue(port.getMessages().contains(TestComPort.RequestedModeMsg));
    }
}
