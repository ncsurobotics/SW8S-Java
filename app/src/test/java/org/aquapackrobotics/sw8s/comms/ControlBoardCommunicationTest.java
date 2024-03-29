package org.aquapackrobotics.sw8s.comms;

import org.aquapackrobotics.sw8s.comms.control.ControlBoardCommunication;
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
}
