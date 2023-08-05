#!/usr/bin/env python3
import sys
import math

class BNO055Data:
    def __init__(self):
        self.quat_w: float = 0.0
        self.quat_x: float = 0.0
        self.quat_y: float = 0.0
        self.quat_z: float = 0.0
        self.pitch: float = 0.0
        self.roll: float = 0.0
        self.yaw: float = 0.0
        self.accum_pitch: float = 0.0
        self.accum_roll: float = 0.0
        self.accum_yaw: float = 0.0


## Parse byte data from BNO055 readings into the data class object
def calculate_quaternion(data: list[float]):
    new_data = BNO055Data()
    
    # Parse data
    new_data.quat_w = data[0]
    new_data.quat_x = data[1]
    new_data.quat_y = data[2]
    new_data.quat_z = data[3]

    # Calculate euler angles from quaternion
    # z-x'-y'' convention

    new_data.pitch = 180.0 * math.asin(2.0 * (new_data.quat_y*new_data.quat_z + new_data.quat_w*new_data.quat_x)) / math.pi
    if abs(90 - abs(new_data.pitch)) < 0.1:
        # Pitch is +/- 90 degrees
        # This is gimbal lock scenario
        # Roll and yaw mean the same thing
        # roll + yaw = 2 * atan2(q.y, q.w)
        # Can split among roll and yaw any way (not unique)
        new_data.yaw = 2.0 * 180.0 * math.atan2(new_data.quat_y, new_data.quat_w) / math.pi
        new_data.roll = 0.0
    else:
        roll_numer = 2.0 * (new_data.quat_w*new_data.quat_y - new_data.quat_x*new_data.quat_z)
        roll_denom = 1.0 - 2.0 * (new_data.quat_x*new_data.quat_x + new_data.quat_y*new_data.quat_y)
        new_data.roll = 180.0 * math.atan2(roll_numer, roll_denom) / math.pi
        
        yaw_numer = -2.0 * (new_data.quat_x*new_data.quat_y - new_data.quat_w*new_data.quat_z)
        yaw_denom = 1.0 - 2.0 * (new_data.quat_x*new_data.quat_x + new_data.quat_z*new_data.quat_z)
        new_data.yaw = 180.0 * math.atan2(yaw_numer, yaw_denom) / math.pi

    print([new_data.quat_w, new_data.quat_x, new_data.quat_y, new_data.quat_z, new_data.pitch, new_data.yaw, new_data.roll])

data = []
for arg in sys.argv[1:]:
    data.append(float(arg))
calculate_quaternion(data)
