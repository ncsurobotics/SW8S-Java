package org.aquapackrobotics.sw8s.comms;

/**
 * "Struct" for holding the different contents of an acknowledge message.
 * Used in {@link SerialCommunicationUitlity.destructAcknowledgeMessage()} to isolate acknowledge ID, error code, and data from the acknowledge message.
 * */
public class AcknowledgeMessageStruct {
    public short acknowledgeId;
    public byte errorCode;
    public byte[] data;
}
