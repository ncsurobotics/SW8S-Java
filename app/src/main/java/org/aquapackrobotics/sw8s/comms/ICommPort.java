package org.aquapackrobotics.sw8s.comms;

public interface ICommPort {
    public void openPort(ICommPortListener listener);

    public void writeBytes(byte[] bytes, long length);

    public void closePort();
}
