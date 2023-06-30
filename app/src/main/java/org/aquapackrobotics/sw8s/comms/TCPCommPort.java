package org.aquapackrobotics.sw8s.comms;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.ThreadFactory;

public class TCPCommPort implements ICommPort, ThreadFactory {

    private static String endPoint = "localhost";
    private static final int port = 5012;
    private InputStream socketIn;
    private OutputStream socketOut;

    ControlBoardListener listener;

    private byte[] buffer;

    public Socket simSocket;

    public TCPCommPort(Socket socket) {
        simSocket = socket;
        buffer = new byte[64];
    }

    @Override
    public void openPort(ICommPortListener listener) {
        try {
            // simSocket = new Socket(endPoint, port);
            socketIn = simSocket.getInputStream();
            socketOut = simSocket.getOutputStream();
            this.listener = (ControlBoardListener) listener;
            newThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        listenForEvents();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        } catch (IOException e) {

        }

    }

    private void listenForEvents() throws IOException {
        while (true) {
            if (socketIn.available() > 0) {
                listener.tcpEvent(this);
            }
        }
    }

    public byte[] getBytesAvailable() throws IOException {
        int size = socketIn.read(buffer);
        byte[] input = new byte[size];
        for (int i = 0; i < size; i++) {
            input[i] = buffer[i];
            buffer[i] = 0;
        }
        return input;
    }

    @Override
    public synchronized void writeBytes(byte[] bytes, int length) {
        try {
            socketOut.write(bytes, 0, (int) length);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void closePort() {
        try {
            socketIn.close();
            socketOut.close();
            simSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Thread newThread(Runnable r) {
        return new Thread(r);
    }
}
