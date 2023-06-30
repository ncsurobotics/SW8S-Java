package org.aquapackrobotics.sw8s.comms;

import java.io.IOException;

public interface ICommPort {
    /**
     * Opens the port for reading and writing.
     * 
     * @param listener The listener to hook up to this port.
     */
    void openPort(ICommPortListener listener);

    /**
     * Writes the following bytes to the port.
     * 
     * @param bytes  The bytes to write
     * @param length How many bytes to write from the start of the array provided.
     */
    void writeBytes(byte[] bytes, int length);

    /**
     * Called when the port is no longer in use.
     */
    void closePort();
}
