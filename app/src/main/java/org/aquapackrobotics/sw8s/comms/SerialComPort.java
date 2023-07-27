package org.aquapackrobotics.sw8s.comms;

import org.aquapackrobotics.sw8s.comms.control.ControlBoardListener;
import org.aquapackrobotics.sw8s.comms.meb.MEBListener;

import com.fazecast.jSerialComm.SerialPort;

public class SerialComPort implements ICommPort {
    public SerialPort serialPort;

    public SerialComPort(SerialPort port) {
        serialPort = port;
    }

    public void openPort(ICommPortListener listener) {
        serialPort.openPort();
        serialPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 100, 0);
        serialPort.addDataListener((ControlBoardListener) listener);
    }

    public void openPortMEB(ICommPortListener listener) {
        serialPort.openPort();
        serialPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 100, 0);
        serialPort.addDataListener((MEBListener) listener);
    }

    public synchronized void writeBytes(byte[] data, int length) {
        serialPort.writeBytes(data, length);
    }

    public void closePort() {
        serialPort.closePort();
    }

}
