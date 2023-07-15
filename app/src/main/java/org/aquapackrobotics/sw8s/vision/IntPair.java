package org.aquapackrobotics.sw8s.vision;

import java.util.Objects;

public class IntPair {
    final public int x;
    final public int y;

    public IntPair(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(Object otherObj) {
        if (otherObj instanceof IntPair) {
            IntPair other = (IntPair) otherObj;
            return (x == other.x) && (y == other.y);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}
