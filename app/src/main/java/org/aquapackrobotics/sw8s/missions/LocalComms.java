package org.aquapackrobotics.sw8s.missions;

import java.util.concurrent.*;
import java.net.*;
import java.io.*;
import org.aquapackrobotics.sw8s.states.State;
import org.aquapackrobotics.sw8s.comms.*;

/**
 * Competition mission, fully autonomous.
 */
public class LocalComms extends Mission {

    //initialize socket and input stream
    private Socket       socket = null;
    private ServerSocket server = null;
    private DataInputStream in   = null;
    private DataOutputStream out = null;
    int port;

    public LocalComms(ControlBoardThreadManager manager, int port) {
        super(manager);
        this.port = port;
    }

    // TODO: replace this awful hack
    @Override
    protected State initialState() {
        // starts server and waits for a connection
        try
        {
            server = new ServerSocket(port);
            System.out.println("Server started");

            System.out.println("Waiting for a client ...");

            socket = server.accept();
            System.out.println("Client accepted");
            out = new DataOutputStream(socket.getOutputStream());
            out.writeUTF("Hello");
            System.out.println("Message sent");
            // takes input from the client socket
            in = new DataInputStream(
                new BufferedInputStream(socket.getInputStream()));

            String line = "";

            // reads message from client until "Over" is sent
            while (!line.equals("Over"))
            {
                try
                {
                    line = in.readUTF();
                    System.out.println(line);
                    //processController(line);
                    switch (line.toLowerCase()) {
                        case "a":  // left
                            out.writeUTF("a");
                            System.out.println("reading");
                            break;
                        case "d": // right
                            out.writeUTF("d");
                            break;
                        case "w": // forward
                            out.writeUTF("w");
                            break;
                        case "s": // backward
                            out.writeUTF("s");
                            break;
                        case "up": // up
                            out.writeUTF("up");
                            break;
                        case "down": // down
                            out.writeUTF("down");
                            break;
                        case "o": // pitch up
                            out.writeUTF("o");
                            break;
                        case "u": // pitch down
                            out.writeUTF("u");
                            break;  
                        case "q": // roll left
                            out.writeUTF("q");
                            break;
                        case "e": // roll right
                            out.writeUTF("e");
                            break;
                        case "l": // yaw clockwise
                            out.writeUTF("l");
                            break;
                        case "j": // yaw counterclockwise
                            out.writeUTF("j");
                            break;
                        case "space": // space
                            out.writeUTF("zero");
                            break;
                        default:
                            break;
                    }


                }
                catch(SocketException e)
                {
                    out.close();
                    in.close();
                    socket.close();
                    System.exit(0);
                }
                catch(Exception i)
                {
                    System.out.println(i);
                }
            }

            //Setting all motors to zero before shut off
            //manager.setLocalSpeeds(0, 0, 0, 0, 0, 0);
            System.out.println("Closing connection");
            // close connection
            socket.close();
            in.close();
        }
        catch(IOException i)
        {
            System.out.println(i);
        }
        return null;
    }

    // TODO: fix this spaghetti
    private void processController(String command) throws ExecutionException, InterruptedException {
    }

    // TODO: implement
    @Override
    protected void executeState(State state)  throws ExecutionException, InterruptedException  {
    }

    // TODO: implement
    @Override
    protected State nextState(State state) {
        return null;
    }
}
