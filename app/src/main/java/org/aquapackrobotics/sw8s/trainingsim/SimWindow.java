package org.aquapackrobotics.sw8s.trainingsim;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Polygon;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.awt.geom.AffineTransform;
import java.awt.RenderingHints;

public class SimWindow extends JFrame {

    // Current robot velocity goals
    private double targetXVel, targetYVel, targetYawVel;

    // Current robot position (center of robot)
    private double robotXPos, robotYPos, robotAngle;

    // Current robot velocities
    private double robotXVel, robotYVel, robotYawVel;

    // "field" properties
    private final int fieldWidth = 600;
    private final int fieldHeight = 600;

    // Physical robot properties
    private final int robotHeight = 75;
    private final int robotWidth = 75;
    private final double robotMaxSpeed = 100;
    private final double accelRate = 0.3;


    // Waypoints manually added to "field"
    HashMap<String, Dimension> waypoints;

    public SimWindow(){
        targetXVel = 0;
        targetYVel = 0;
        targetYawVel = 0;
        robotXVel = 0;
        robotYVel = 0;
        robotYawVel = 0;
        robotXPos = fieldWidth / 2;
        robotYPos = fieldHeight - robotHeight / 2 - 5;
        robotAngle = 0;
        waypoints = new HashMap<>();

        this.setTitle("AquaPack Robotics Software Training Simulator");
        this.setLayout(new BorderLayout());
        this.add(new SimPanel(), BorderLayout.CENTER);
        this.setResizable(false);
        this.pack();
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
    }

    public void setRobotSpeed(double xSpeed, double ySpeed, double yawSpeed){
        synchronized(this){
            this.targetXVel = Math.min(1, Math.max(-1, xSpeed));
            this.targetYVel = Math.min(1, Math.max(-1, ySpeed));
            this.targetYawVel = Math.min(1, Math.max(-1, yawSpeed));
        }
    }

    public double getXVel() { return robotXVel; }

    public double getYVel() { return robotYVel; }

    public double getYawVel() { return robotYawVel; }

    public double getXPos() { return robotXPos; }

    public double getYPos() { return robotYPos; }

    public double getRobotAngle(){
        return robotAngle;
    }

    public void addWaypoint(String name, int x, int y) throws Exception{
        if(waypoints.containsKey(name)){
            throw new Exception("Duplicate waypoint name.");
        }
        synchronized(this){
            waypoints.put(name, new Dimension(x, y));
        }
    }

    public void removeWaypoint(String name) throws Exception{
        if(!waypoints.containsKey(name)){
            throw new Exception("Attempted to remove nonexistent waypoint.");
        }
        synchronized(this){
            waypoints.remove(name);
        }
    }

    class SimPanel extends JPanel{
        private Timer timer;

        private final int UPDATE_RATE = 10; // ms

        public SimPanel(){
            this.setPreferredSize(new Dimension(fieldWidth, fieldHeight));
            timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    update();
                }
            }, 0, UPDATE_RATE);
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    repaint();
                }
            }, 0, 16);
        }

        private void drawRobot(Graphics2D g2){
            Rectangle rect = new Rectangle((int)(robotXPos - robotWidth / 2.0), (int)(robotYPos - robotHeight / 2.0), robotWidth, robotHeight);
            Polygon arrow = new Polygon(new int[]{(int)robotXPos, (int)robotXPos + 5, (int)robotXPos - 5}, new int[]{(int)(robotYPos - robotHeight / 2.0), (int)(robotYPos - robotHeight / 2.0) + 5, (int)(robotYPos - robotHeight / 2.0) + 5}, 3);
            AffineTransform tf = new AffineTransform();
            tf.rotate(Math.toRadians(robotAngle), rect.getX() + rect.width/2, rect.getY() + rect.height/2);
            Shape rotatedRect = tf.createTransformedShape(rect);
            Shape rotatedArrow = tf.createTransformedShape(arrow);
            g2.setColor(Color.RED);
            g2.fill(rotatedRect);
            g2.setColor(Color.BLACK);
            g2.fill(rotatedArrow);
        }

        private void drawWaypoints(Graphics2D g2){
            synchronized(this){
                g2.setColor(Color.GREEN);
                for(Dimension d : waypoints.values()){
                    g2.fillRect((int)d.getWidth() - 2, (int)d.getHeight() - 2, 4, 4);
                }
            }
        }

        private void drawField(Graphics2D g2){
            g2.setColor(Color.GRAY);
            g2.fillRect(0, 0, fieldWidth, fieldHeight);
        }

        private void update(){
            // Update velocities
            if(robotXVel < targetXVel){
                robotXVel = Math.min(robotXVel + accelRate, targetXVel);
            }else if(robotXVel > targetXVel){
                robotXVel = Math.max(robotXVel - accelRate, targetXVel);
            }
            if(robotYVel < targetYVel){
                robotYVel = Math.min(robotYVel + accelRate, targetYVel);
            }else if(robotYVel > targetYVel){
                robotYVel = Math.max(robotYVel - accelRate, targetYVel);
            }
            if(robotYawVel < targetYawVel){
                robotYawVel = Math.min(robotYawVel + accelRate, targetYawVel);
            }else if(robotYVel > targetYawVel){
                robotYawVel = Math.max(robotYawVel - accelRate, targetYawVel);
            }

            // Calculate positions
            robotXPos += robotXVel * robotMaxSpeed * UPDATE_RATE / 1000.0;
            robotYPos += robotYVel * robotMaxSpeed * UPDATE_RATE / 1000.0;
            robotAngle += robotYawVel * robotMaxSpeed * UPDATE_RATE / 1000.0;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D)g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            drawField(g2);
            drawRobot(g2);
            drawWaypoints(g2);
        }
    }
}
