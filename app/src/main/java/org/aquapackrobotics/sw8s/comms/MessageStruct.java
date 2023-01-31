package org.aquapackrobotics.sw8s.comms;

/**
 * "Struct" for pairing a Message ID to a fully constructed commboard message.
 * Used in {@link ControlBoardCommunication} to isolate msg ID from a message and return for use in {@link ControlBoardThreadManager}.
 * */
public class MessageStruct {
    public short id;
    public byte[] message;

}

public class BN0055data implements Data{
    public float data =;

    float getData(){
        return data;
    }


}

public class MS5837data implements Data {
    float data;
    float getData(){
        return data;
    }
}

public class MS5837GlobalBuffer{
    DataBuffer depth;
}


public class BNO055GlobalBuffer{
    DataBuffer gyrox;
    DataBuffer gyrox;
    DataBuffer gyroz;
    DataBuffer quat_w;
    DataBuffer quat_x;
    DataBuffer quat_y;
    DataBuffer quat_z;
}


// Black board for all sensor data from status and acknowledgement messages(Implement command to recieve depth every .1 seconds)
// Make sure everything works
// Cameras
// Mission
