package org.aquapackrobotics.sw8s.comms;

import com.fazecast.jSerialComm.SerialPortEvent;

public interface ICommPortListener {
    void serialMessageHandler(byte[] message);
}
