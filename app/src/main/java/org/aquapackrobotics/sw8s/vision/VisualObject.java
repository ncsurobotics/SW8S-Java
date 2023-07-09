package org.aquapackrobotics.sw8s.vision;

public class VisualObject {
    public double color;
    public double width;
    public double angle; // positive means sloped right
    public double horizontal_offset; // positive means right of center
    public double vertical_offset; // positive means down of center

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

    public void add(VisualObject other) {
        this.color += other.color;
        this.width += other.width;
        this.angle += other.angle;
        this.horizontal_offset += other.horizontal_offset;
        this.vertical_offset += other.vertical_offset;
    }

    public void divide(int factor) {
        this.color /= factor;
        this.width /= factor;
        this.angle /= factor;
        this.horizontal_offset /= factor;
        this.vertical_offset /= factor;
    }
}
