package org.aquapackrobotics.sw8s.vision;

public class VisualObjectException extends Exception {
    public VisualObjectException(String s) {
        super(s);
    }
    public VisualObjectException() {
        this(null);
    }
}
