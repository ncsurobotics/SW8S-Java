package org.aquapackrobotics.sw8s.vision;

/**
 * Path specialization for octagon table
 * 
 * @author Xingjian Li
 *
 */
public class Octagon extends PathYUV {
    public Octagon(double scale) {
        super(new IntPair(Integer.MIN_VALUE, 152), new IntPair(152, Integer.MAX_VALUE), 10,
                800, scale, new IntPair(25, 25), new IntPair(8, 16));
    }

    public Octagon() {
        this(0.25);
    }
}
