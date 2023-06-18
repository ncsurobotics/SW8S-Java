package org.aquapackrobotics.sw8s.comms;

/**
 * "Struct" for pairing a Message ID to a fully constructed commboard message.
 * Used in {@link ControlBoardCommunication} to isolate msg ID from a message and return for use in {@link ControlBoardThreadManager}.
 * */
public class MessageStruct {
    public short id;
    public byte[] message;

}


// Make sure everything works
// Cameras
// Mission(Includes acoustics)
// Need to make a place to put data from acknowledge messages