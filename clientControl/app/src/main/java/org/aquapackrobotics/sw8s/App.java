// A Java program for a Client
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

import org.aquapackrobotics.sw8s.processEvent;

public class App
{
    public App(String address, int port)
    {
        
        processEvent listener = new processEvent(address, port);

        String currentMission = "N/A";
        String currentState = "N/A";
        
        // Variables
        Color red = new Color(255, 0, 0);
        Color darkGrey = new Color(80, 80, 80);
        Color white = new Color(255, 255, 255);
       
        //Creating the Frame
        JFrame frame = new JFrame("Chat Frame");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 800);
       
        //Low Panel and ESTOP
        JPanel lowPanel = new JPanel(); // the panel is not visible in output
        lowPanel.setBackground(darkGrey);
        JButton eStop = new JButton("STOP");
        eStop.setPreferredSize(new Dimension(300, 100));
        eStop.setBackground(red);
        eStop.setForeground(white);
        eStop.setFont(new Font(Font.DIALOG, Font.BOLD, 50));
        //eStop.addKeyListener(KeyEvent.VK_ESCAPE);
        //eStop.setMnemonic(KeyEvent.VK_ESCAPE);
        lowPanel.add(eStop);
        
        //WASD
            //W -
        

        //High Panel and Content
        JPanel highPanel = new JPanel();
        highPanel.setBackground(darkGrey);
        JLabel label = new JLabel("Enter Text");
        label.setFont(new Font(Font.DIALOG, Font.BOLD, 20));
        label.setForeground(white);
        JTextField tf = new JTextField(50); // accepts upto 50 characters
        JButton sendButton = new JButton("Send");
        highPanel.add(label);
        highPanel.add(tf);
        highPanel.add(sendButton);

        // Text Area at the Center
        JTextArea ta = new JTextArea();
        ta.setText("Mission: " + currentMission + 
                 "\nState:   " + currentState +
                   "");
        Font  f  = new Font(Font.DIALOG,  Font.BOLD, 50);
        ta.setFont(f);
        ta.addKeyListener(listener);
        ta.setEditable(false);

        //Adding Panel/Text Area to the frame.
        frame.getContentPane().add(BorderLayout.NORTH, highPanel);
        frame.getContentPane().add(BorderLayout.SOUTH, lowPanel);
        frame.getContentPane().add(BorderLayout.CENTER, ta);
        frame.setVisible(true);

    }

    public static void main(String args[])
    {
        App client;

        for (String str: args) {
            switch (str) {
                case "--local":
                    client = new App("127.0.0.1", 5000);
                    break;
                default:
                    client = new App("192.168.2.5", 5000);
                    break;
            }
        }
    }
}
