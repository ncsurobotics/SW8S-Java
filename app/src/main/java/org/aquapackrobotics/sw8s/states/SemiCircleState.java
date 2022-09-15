package org.aquapackrobotics.sw8s.states;

import org.aquapackrobotics.sw8s.trainingsim.SimWindow;

import java.util.concurrent.ScheduledThreadPoolExecutor;

public class SemiCircleState extends SimState {

    private final double centerX;
    private final double centerY;

    private final int circleRes = 16;
    private final double epsilon = 1.5;
    private final double circleRadius;

    private int circleStep;
    private double xTarget;
    private double yTarget;

    public SemiCircleState(ScheduledThreadPoolExecutor pool, SimWindow window, double centerX, double centerY) {
        super(pool, window);

        this.centerX = centerX;
        this.centerY = centerY;

        // Forces a circle step increment reevaluation when onPeriodic is first called
        xTarget = window.getXPos();
        yTarget = window.getYPos();

        // Use distance to the center as the radius
        circleRadius = calculateDistance(centerX, centerY, window.getXPos(), window.getYPos());

        onEnter();
    }

    @Override
    public void onEnter() { }

    // Not dead reckoning
    @Override
    public boolean onPeriodic() {

        if (isCloseEnough(xTarget, yTarget)) {
            try {
                window.addWaypoint("Circle step " + circleStep, (int) xTarget, (int) yTarget);
            } catch (Exception e) {
                System.out.print(e.getMessage());
            }

            // Find position in circle we should be targeting
            double stepNormalized = (double) ++circleStep / circleRes;
            double angle = Math.PI * 1/2 + Math.PI * stepNormalized;
            xTarget = centerX + Math.cos(angle) * circleRadius;
            yTarget = centerY + Math.sin(angle) * circleRadius;

            // Compute movement vector and normalize to length 1
            double sqrMag = calculateDistance(xTarget, yTarget, window.getXPos(), window.getYPos());
            double xDisp = (xTarget - window.getXPos()) / sqrMag;
            double yDisp = (yTarget - window.getYPos()) / sqrMag;

            window.setRobotSpeed(xDisp, yDisp, 0);
        }

        return circleStep >= circleRes;
    }

    private double calculateDistance(double xDest, double yDest, double xSource, double ySource) {
        double xDisp = xDest - xSource;
        double yDisp = yDest - ySource;
        return Math.sqrt(xDisp * xDisp + yDisp * yDisp);
    }

    private boolean isCloseEnough(double x, double y) {
        return Math.abs(x - window.getXPos()) < epsilon && Math.abs(y - window.getYPos()) < epsilon;
    }

    @Override
    public void onExit() {
        ResetMomentum();
    }

    @Override
    public State nextState() {
        return null;
    }
}
