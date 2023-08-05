package org.aquapackrobotics.sw8s.comms;

public class Quaternion {
    static public double[] calculate(double quat_w, double quat_x, double quat_y, double quat_z) {
        double pitch, roll, roll_denom, roll_numer, yaw, yaw_denom, yaw_numer;

        pitch = 180.0 * Math.asin(2.0 * (quat_y * quat_z + quat_w * quat_x)) / Math.PI;
        if (Math.abs(90 - Math.abs(pitch)) < 0.1) {
            yaw = 2.0 * 180.0 * Math.atan2(quat_y, quat_w) / Math.PI;
            roll = 0.0;
        } else {
            roll_numer = 2.0 * (quat_w * quat_y - quat_x * quat_z);
            roll_denom = 1.0 - 2.0 * (quat_x * quat_x + quat_y * quat_y);
            roll = 180.0 * Math.atan2(roll_numer, roll_denom) / Math.PI;

            yaw_numer = -2.0 * (quat_x * quat_y - quat_w * quat_z);
            yaw_denom = 1.0 - 2.0 * (quat_x * quat_x + quat_z * quat_z);
            yaw = 180.0 * Math.atan2(yaw_numer, yaw_denom) / Math.PI;
        }

        return new double[] { quat_w, quat_x, quat_y, quat_z, pitch, roll, yaw };
}

}
