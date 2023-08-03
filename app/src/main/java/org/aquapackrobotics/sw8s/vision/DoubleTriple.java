package org.aquapackrobotics.sw8s.vision;

import java.util.Objects;

public class DoubleTriple {
    final public double x;
    final public double y;
    final public double z;

    public DoubleTriple(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public boolean equals(Object otherObj) {
        if (otherObj instanceof DoubleTriple) {
            DoubleTriple other = (DoubleTriple) otherObj;
            return (x == other.x) && (y == other.y) && (z == other.z);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, z);
    }

    @Override
    public String toString() {
        return "[" + x + ", " + y + ", " + z + "]";
    }
}
