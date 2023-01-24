package org.aquapackrobotics.sw8s.comms;

/**
 * "Struct" for pairing a Message ID to a fully constructed commboard message.
 * Used in {@link ControlBoardCommunication} to isolate msg ID from a message and return for use in {@link ControlBoardThreadManager}.
 * */
public class MessageStruct {
    public short id;
    public byte[] message;
}

// Needs series of structs to handle message data
// Need a message struct for recieving
// Error code in message structs
// Implement all the rest of the commands