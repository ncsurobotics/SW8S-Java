// A Java program for a Server
package org.aquapackrobotics.sw8s;

import java.net.*;
import java.io.*;

public class Server
{
    //initialize socket and input stream
    private Socket       socket = null;
    private ServerSocket server = null;
    private DataInputStream in   = null;

    // constructor with port
    public Server(int port)
    {
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

                }
                catch(IOException i)
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
    }

    public static void main(String args[])
    {
        String currentMission = "N/A";
        String currentState = "N/A";
        String helpFlag[] = {
            "\nBasic Utility:", 
            "\n'--help' or '-h' -- displays list of command flags",
            "\n'--currentMission' -- prints the current mission",
            "\nTesting:",
            "\n'--test' -- The Command Flag used in Testing", 
            "\n'--testmission' -- sets mission to be the string 'test' for testing purposes",
            "\nMissions:", 
            "\n'--raw_test' runs the Raw Test mission",
            "\n '--local_test' runs the Local Test mission"};
        System.out.println("Basic Format: gradle run --args='_'");
        for (String str: args) {
            switch (str) {
                case "--test":
                    currentMission = "None, running test";
                    System.out.println("Yay! it worked!");
                    break;
                case "--testmission":
                    currentMission = "Test Mission";
                    break;
                case "--currentMission":
                    System.out.println(currentMission);
                    break;
                case "--manual":
                    Server server = new Server(5000);
                    break;
                case "-h":
                case "--help":
                    for(int i = 0; i < helpFlag.length; i++){
                        System.out.println(helpFlag[i]);
                    }
                    break;
                // case "--raw_test":
                //     currentMission = "Raw Test";
                //     Mission missionRaw_Test = (Mission) new Raw_TestMission(pool);
                //     missionRaw_Test.run();
                //     break;
                // case "--local_test":
                //     currentMission = "Local Test";
                //     Mission missionLocal_Test = (Mission) new Local_TestMission(pool);
                //     missionLocal_Test.run();
                //     break;
                // case "-s1":
                //     executeState(State1);
                //     break;
                // case "-s2":
                //     executeState(State2);
                //     break;
                // case "-s3":
                //     executeState(State3);
                //     break;
            }
        }
    }
}
