package org.aquapackrobotics.sw8s.vision;

public class VisualObject {
    public final double color;
    public final double width;
    public final double angle; // positive means sloped right
    public final double horizontal_offset; // positive means right of center
    public final double vertical_offset; // positive means down of center

    /*
     * linear needs to be five values long
     */
    VisualObject(double[] linear) {
        color = linear[0];
        width = linear[1];
        angle = linear[2];
        horizontal_offset = linear[3];
        vertical_offset = linear[4];
    }
}
