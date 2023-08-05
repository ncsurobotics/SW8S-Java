package org.aquapackrobotics.sw8s.comms.meb;

public class MEBStatus {
    public static volatile boolean isLeak = false;
    public static volatile boolean isArmed = false;
    public static volatile float systemVoltage = 0.0f;
    public static volatile int shutdownCause = 0;

    public static volatile float humid = 0.0f;
    public static volatile float temp = 0.0f;
}
