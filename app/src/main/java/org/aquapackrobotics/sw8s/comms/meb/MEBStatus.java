package org.aquapackrobotics.sw8s.comms.meb;

public class MEBStatus {
    public static boolean isLeak;
    public static boolean isArmed;
    public static float systemVoltage;
    public static int shutdownCause;

    private static MEBStatus mebStatus;

    private MEBStatus() {
        boolean isLeak = false;
        boolean isArmed = false;
        float systemVoltage = 0.0f;
        int shutdownCause = 0;
    }

    public static MEBStatus getInstance() {
        if (mebStatus == null) {
            mebStatus = new MEBStatus();
        }

        return mebStatus;
    }
}
