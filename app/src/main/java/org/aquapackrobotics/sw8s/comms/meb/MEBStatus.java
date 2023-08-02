package org.aquapackrobotics.sw8s.comms.meb;

public class MEBStatus {
    public boolean isLeak;
    public boolean isArmed;
    public float systemVoltage;
    public int shutdownCause;

    public float humid;
    public float temp;

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
