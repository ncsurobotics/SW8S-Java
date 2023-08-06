package org.aquapackrobotics.sw8s.vision;

import java.util.Objects;

public class DoublePair {
    final public double x;
    final public double y;

    public DoublePair(double x, double y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(Object otherObj) {
        if (otherObj instanceof DoublePair) {
            DoublePair other = (DoublePair) otherObj;
            return (x == other.x) && (y == other.y);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public String toString() {
        return "[" + x + ", " + y + "]";
    }
}
