package org.aquapackrobotics.sw8s.trainingsim;

/**
 * Vestigial, for reference only
 */
public class Training {
    public static void main(String args[]) throws Exception{
        SimWindow sim = new SimWindow();

        double kp = 10;
        double targetAngle = 45;

        Thread.sleep(1000);

        long startTime = System.currentTimeMillis();

        while(true){
        	//double yawSpeed = kp * (sim.getRobotAngle() - targetAngle);
            double xSpeed = (System.currentTimeMillis() - startTime < 1750) ? 0.85 : 0.0;
            sim.setRobotSpeed(xSpeed, 0, 0);
            Thread.sleep(100);
            
            double ySpeed = (System.currentTimeMillis() - startTime > 1750 && System.currentTimeMillis() - startTime < 3750) ? 2 : 0.0;
            sim.setRobotSpeed(0, ySpeed, 0);
            Thread.sleep(100);
            
        }
    }
}