package org.aquapackrobotics.sw8s.missions;

import java.util.concurrent.*;
import java.net.*;
import java.io.*;

import org.aquapackrobotics.sw8s.states.State;
import org.aquapackrobotics.sw8s.states.ManualDummy;
import org.aquapackrobotics.sw8s.comms.*;

/**
 * Competition mission, fully autonomous.
 */
public class ManualMission extends Mission {

    //initialize socket and input stream
    private Socket       socket = null;
    private ServerSocket server = null;
    private DataInputStream in   = null;
    int port;

    ScheduledFuture<byte[]> depthRead;
    ScheduledFuture<byte[]> gyroRead;

    public ManualMission(ControlBoardThreadManager manager, int port) {
        super(manager);
        this.port = port;
    }

    // TODO: replace this awful hack
    @Override
    protected State initialState() {
        try {
            manager.setThrusterInversions(true, true, false, false, true, false, false, true);
            depthRead = manager.MSPeriodicRead((byte)1);
            gyroRead = manager.BNO055PeriodicRead((byte)1);
        }
        catch(Exception e) {
            System.out.println(e);
        }
        // starts server and waits for a connection
        try
        {
            server = new ServerSocket(port);
            System.out.println("Server started");

            System.out.println("Waiting for a client ...");

            socket = server.accept();
            System.out.println("Client accepted");

            // takes input from the client socket
            in = new DataInputStream(
                new BufferedInputStream(socket.getInputStream()));

            String line = "";

            // reads message from client until "Over" is sent
            try
            {
                while (!line.equals("Over"))
                {
                        line = in.readUTF();
                        System.out.println(line);
                        processController(line);
                        System.out.println("DEPTH READ STATUS: " + depthRead.isDone());
                        System.out.println("GYRO READ STATUS: " + gyroRead.isDone());
                        if ( ! ( depthRead.isDone() && gyroRead.isDone() ) ) {
                            System.out.println("WAITING");
                        }
                        if ( depthRead.isDone() ) {
                            System.out.println("Depth: " + manager.getDepth());
                        }
                        if ( gyroRead.isDone() ) {
                            System.out.println("Gyro X: " + manager.getGyrox());
                        }
                    }
            }
            catch(Exception i)
            {
                try {
                    manager.setLocalSpeeds(0, 0, 0, 0, 0, 0);
                    System.out.println(i);
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("Closing connection");

            // close connection
            socket.close();
            in.close();
        }
        catch(IOException i)
        {
            System.out.println(i);
        }
        return new ManualDummy(manager);
    }

    // TODO: fix this spaghetti
    private static double power = 0.5;
    private void processController(String command) throws ExecutionException, InterruptedException {
        switch (command.toLowerCase()) {
            case "a":  // left
                manager.setLocalSpeeds(-power, 0, 0, 0, 0, 0);
                break;
            case "d": // right
                manager.setLocalSpeeds(power, 0, 0, 0, 0, 0);
                break;
            case "w": // forward
                manager.setLocalSpeeds(0, power, 0, 0, 0, 0);
                break;
            case "s": // backward
                manager.setLocalSpeeds(0, -power, 0, 0, 0, 0);
                break;
            case "up": // pitch up
                manager.setLocalSpeeds(0, 0, power, 0, 0, 0);
                break;
            case "down": // pitch down
                manager.setLocalSpeeds(0, 0, -power, 0, 0, 0);
                break;
            case "q": // roll left
                manager.setLocalSpeeds(0, 0, 0, 0, power, 0);
                break;
            case "e": // roll right
                manager.setLocalSpeeds(0, 0, 0, 0, -power, 0);
                break;
            case "right": // yaw forward
                manager.setLocalSpeeds(0, 0, 0, 0, 0, power);
                break;
            case "left": // yaw backward
                manager.setLocalSpeeds(0, 0, 0, 0, 0, -power);
                break;
            case "space": // space
                manager.setLocalSpeeds(0, 0, 0, 0, 0, 0);
                break;
            default:
                manager.setLocalSpeeds(0, 0, 0, 0, 0, 0);
                break;
        }
    }

    // TODO: implement
    @Override
    protected void executeState(State state)  throws ExecutionException, InterruptedException  {
        while (true) {
            if ( ( depthRead.isDone() && gyroRead.isDone() ) ) {
                System.out.println("WAITING");
            }
            if ( depthRead.isDone() ) {
                System.out.println("Depth: " + manager.getDepth());
            }
            if ( gyroRead.isDone() ) {
                System.out.println("Gyro X: " + manager.getGyrox());
            }
            Thread.sleep(100);
        }
    }

    // TODO: implement
    @Override
    protected State nextState(State state) {
        return null;
    }
}
