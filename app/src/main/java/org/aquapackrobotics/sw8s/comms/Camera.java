package org.aquapackrobotics.sw8s.comms;

import java.util.prefs.Preferences;

public enum Camera {
    BOTTOM, FRONT;

    public int getID() {
        var prefs = Preferences.userRoot().node(this.getClass().getName());
        switch (this) {
            case BOTTOM:
                return prefs.getInt("BOTTOM", 0);
            case FRONT:
                return prefs.getInt("TOP", 1);
            default:
                throw new IllegalStateException();
        }
    }

    public void swap() {
        var prefs = Preferences.userRoot().node(this.getClass().getName());
        prefs.putInt("BOTTOM", prefs.getInt("BOTTOM", 0) == 0 ? 1 : 0);
        prefs.putInt("TOP", prefs.getInt("TOP", 1) == 1 ? 0 : 1);
    }

    public void set(String field, int val) {
        Preferences.userRoot().node(this.getClass().getName()).putInt(field, val);
    }
}
