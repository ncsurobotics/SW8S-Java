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
        
        int state = 0;
        double xSpeed = 0;
        double ySpeed = 0;
        double yawSpeed = 0;
        while(true){
            switch (state){
                case 0:
                    if (System.currentTimeMillis() - startTime < 1500){
                        ySpeed = -0.85;
                    } else {
                        ySpeed = 0;
                        state = 1;
                    }
                    break;
                case 1:
                    if (System.currentTimeMillis() - startTime < 2000){
                        xSpeed = -0.85;
                    } else {
                        xSpeed = 0;
                        state = 2;
                    }
                    break;
                case 2:
                    if (System.currentTimeMillis() - startTime < 2500){
                        xSpeed = 0.85;
                    } else {
                        xSpeed = 0;
                        state = 3;
                    }
                    break;
                case 3:
                    if (System.currentTimeMillis() - startTime < 4000){
                        ySpeed = 0.85;
                    } else {
                        ySpeed = 0;
                        xSpeed = -1;
                        state = 4;
                    }
                    break;  
                case 4:
                    if (System.currentTimeMillis() - startTime < 8000){
                        ySpeed = -Math.sqrt(1 - Math.pow(xSpeed,2));
                        xSpeed += 0.005;
                    } else {
                        xSpeed = 0;
                        ySpeed = 0;
                    }

            }
            sim.setRobotSpeed(xSpeed, ySpeed, yawSpeed);
            Thread.sleep(10);
        }
    }
}
