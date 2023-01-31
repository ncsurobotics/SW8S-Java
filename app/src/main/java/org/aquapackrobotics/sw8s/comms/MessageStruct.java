package org.aquapackrobotics.sw8s.comms;

/**
 * "Struct" for pairing a Message ID to a fully constructed commboard message.
 * Used in {@link ControlBoardCommunication} to isolate msg ID from a message and return for use in {@link ControlBoardThreadManager}.
 * */
public class MessageStruct {
    public short id;
    public byte[] message;
}

public class DataStruct{
    public double MS5837depth; // depth
    public double[] BNO055data = new double[7]; // gyros and quaternions
}



// Black board for all sensor data from status and acknowledgement messages(Implement command to recieve depth every .1 seconds)
// Make sure everything works
// Cameras
// Mission
