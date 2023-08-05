package org.aquapackrobotics.sw8s.vision;

public class Translation {
    public static DoublePair convert(DoublePair pair) {
        return new DoublePair(pair.x, -pair.y);
    }

    public static DoublePair movement(DoublePair pair, double cutoff, double speed) {
        double x = 0;
        double y = 0;

        DoublePair conv = convert(pair);
        if (Math.abs(conv.x) > cutoff) {
            x = conv.x > 0 ? speed : -speed;
        }
        if (Math.abs(conv.y) > cutoff) {
            y = conv.y > 0 ? -speed : speed;
        }
        return new DoublePair(x, y);
    }

    public static DoublePair movement(DoublePair pair, double cutoff) {
        return movement(pair, cutoff, 0.2);
    }

    public static DoublePair movement(DoublePair pair) {
        return movement(pair, 0.1);
    }

    public static DoubleTriple movement(DoublePair pair, double curAngle, double measuredAngle, double cutoff,
            double speed) {
        DoublePair result = movement(pair, cutoff, speed);
        double angle = Math.toDegrees(measuredAngle);
        if (angle > 10.0)
            curAngle -= 5.0;
        else if (angle < -10)
            curAngle += 5.0;
        return new DoubleTriple(result.x, result.y, curAngle);
    }

    public static DoubleTriple movement_triple(DoublePair pair, double curAngle, double measuredAngle) {
        return movement(pair, curAngle, measuredAngle, 0.1, 0.3);
    }
}
