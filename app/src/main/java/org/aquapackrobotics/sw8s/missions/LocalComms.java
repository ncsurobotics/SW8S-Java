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
                    processController(line);
                }
                catch(Exception i)
                {
                    System.out.println(i);
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
