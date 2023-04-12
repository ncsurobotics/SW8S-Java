package org.aquapackrobotics.sw8s.comms;

import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class TCPCommPort implements ICommPort {

    private static String endPoint = "localhost";
    private static final int port = 5012;
    private DataInputStream socketIn;
    private DataOutputStream socketOut;

    private byte[] buffer;

    public Socket simSocket;

    public TCPCommPort(Socket socket){
        simSocket = socket;
        buffer = new byte[64];
    }
    public void openPort(ICommPortListener listener) {
        try {
            simSocket = new Socket(endPoint, port);
            socketIn = (DataInputStream) simSocket.getInputStream();
            socketOut = (DataOutputStream) simSocket.getOutputStream();
        } catch (IOException e) {

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
    public synchronized void writeBytes(byte[] bytes, long length) {
        try {
            socketOut.write(bytes, 0, (int) length);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void closePort()  {
        try {
            socketIn.close();
            socketOut.close();
            simSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
