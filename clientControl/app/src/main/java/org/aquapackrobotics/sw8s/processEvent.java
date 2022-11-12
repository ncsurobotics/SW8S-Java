package org.aquapackrobotics.sw8s;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.net.*;
import java.io.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class processEvent implements KeyListener
{
    // initialize socket and input output streams
        private Socket socket        = null;
        private DataInputStream input = null;
        private DataOutputStream out     = null;
        
        public void keyReleased(KeyEvent event) {};
        public void keyTyped(KeyEvent event) {};
        @Override
        public void keyPressed(KeyEvent e) {
            // TO IMPLEMENT: ALL ZEROES, MAKE ROBOT NOT GO **BEFORE** SYSTEM.EXIT
            //currentMission = "stopped";
            try
            {
                if(e.getKeyChar() == ' ') {
                    out.writeUTF("Over");
                    input.close();
                    out.close();
                    socket.close();
                } else {
                    out.writeUTF(KeyEvent.getKeyText(e.getKeyCode()));
                }

                System.out.println("SEND COMMAND: " + KeyEvent.getKeyText(e.getKeyCode()));
            }
            catch(IOException i)
            {
                System.out.println(i);
            }
        }
    public processEvent(String address, int port){
    // establish a connection
        try
        {
            socket = new Socket(address, port);
            System.out.println("Connected");

            // takes input from terminal
            input = new DataInputStream(System.in);

            // sends output to the socket
            out = new DataOutputStream(socket.getOutputStream());
        }
        catch(UnknownHostException u)
        {
            System.out.println(u);
        }
        catch(IOException i)
        {
            System.out.println(i);
        }

        // string to read message from input
        String line = "";
    }
}
