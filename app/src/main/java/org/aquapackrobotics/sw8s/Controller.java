package org.aquapackrobotics.sw8s;

public class Controller 
{
    public static double calculateVel(double disiredPos, double currentPos)
    {
        double difference = disiredPos - currentPos;
        return Math.min(1, Math.max(-1, difference/2));
    }

}
