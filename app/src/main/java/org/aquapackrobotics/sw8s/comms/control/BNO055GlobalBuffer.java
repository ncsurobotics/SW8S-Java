package org.aquapackrobotics.sw8s.comms.control;

import org.aquapackrobotics.sw8s.comms.*;

public class BNO055GlobalBuffer {
    DataBuffer quat_w = new DataBuffer();
    DataBuffer quat_x = new DataBuffer();
    DataBuffer quat_y = new DataBuffer();
    DataBuffer quat_z = new DataBuffer();
    DataBuffer accum_pitch = new DataBuffer();
    DataBuffer accum_roll = new DataBuffer();
    DataBuffer accum_yaw = new DataBuffer();
}
