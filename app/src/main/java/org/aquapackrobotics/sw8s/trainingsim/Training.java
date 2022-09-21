package org.aquapackrobotics.sw8s.trainingsim;

/**
 * Vestigial, for reference only
 */
public class Training {
    public static void main(String args[]) throws Exception{
        SimWindow sim = new SimWindow();

        double kp = -100;
        double targetAngle = -45;

        Thread.sleep(100);

        long startTime = System.currentTimeMillis();

        while(true){
            double yawSpeed = kp * (sim.getRobotAngle() - targetAngle);
            double ySpeed = (System.currentTimeMillis() - startTime < 1500) ? -1 : -.01;
            sim.setRobotSpeed(10, ySpeed, yawSpeed);
            Thread.sleep(100);
        }
    }
}
