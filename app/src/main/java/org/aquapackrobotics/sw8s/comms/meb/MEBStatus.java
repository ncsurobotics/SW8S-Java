package org.aquapackrobotics.sw8s.comms.meb;

public class MEBStatus {
    public static boolean isLeak;
    public static boolean isArmed;
    public static float systemVoltage;
    public static int shutdownCause;

    public static float humid;
    public static float temp;

    private static MEBStatus mebStatus;



    private MEBStatus() {
        isLeak = false;
        isArmed = false;
        systemVoltage = 0.0f;
        shutdownCause = 0;
        humid = 0.0f;
        temp = 0.0f;
    }

    public static MEBStatus getInstance() {
        if (mebStatus == null) {
            mebStatus = new MEBStatus();
        }

        return mebStatus;
    }
}
