package org.aquapackrobotics.sw8s.comms;

import com.fazecast.jSerialComm.SerialPort;

public class SerialComPort implements ICommPort {
    public SerialPort serialPort;

    public SerialComPort(SerialPort port) {
        serialPort = port;
    }

    public void openPort(ICommPortListener listener) {
        serialPort.openPort();
        serialPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 100, 0);
    }

    public void writeBytes(byte[] data, long length) {
        serialPort.writeBytes(data, length);
    }

    public void closePort() {
        serialPort.closePort();
    }

}