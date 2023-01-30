package org.aquapackrobotics.sw8s.comms;

public interface ICommPortListener {
    void eventBytesHandler(byte[] message);
}
