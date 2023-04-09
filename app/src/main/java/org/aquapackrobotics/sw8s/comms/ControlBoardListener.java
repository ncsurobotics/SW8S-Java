package org.aquapackrobotics.sw8s.comms;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;

import java.nio.charset.StandardCharsets;

/**
 * ControlBoardListener listens for messages from a comm port.
 * See SerialCommunicationUtility for message implementation details.
 */
public class ControlBoardListener implements SerialPortDataListener, ICommPortListener {

    //Acknowledgements
    private static final String ACKNOWLEDGE = "ACK";
    //Status Messages
    private static final String BNO055_STATUS = "BNO055D";
    private static final String WATCHDOG_STATUS = "WDGS";
    private static final String MS5837_STATUS = "MS5837D";

    /** ONLY FOR DEV */
    private static final String DEBUG_STRING = "DEBUG";
    private static final String DEBUG_RAW = "DBGDAT";



    private static ByteArrayOutputStream messageStore = new ByteArrayOutputStream();

    private static MS5837GlobalBuffer depths = new MS5837GlobalBuffer();
    private static BNO055GlobalBuffer imuData = new BNO055GlobalBuffer();

    private static boolean parseStarted = true;
    private static boolean parseEscaped = false;


    
    /**
     * Returns the events for which serialEvent(SerialPortEvent) will be called
     */
    @Override
    public int getListeningEvents() {
        return SerialPort.LISTENING_EVENT_DATA_AVAILABLE;
    }
    
    /**
     * Run when the specified ListeningEvent is triggered
     */
    @Override
    public void serialEvent(SerialPortEvent event) {
        try {
        int size = event.getSerialPort().bytesAvailable();
        byte[] message = new byte[size];
        event.getSerialPort().readBytes(message, size);

        eventBytesHandler(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Processes bytes from a serial port listening event that may contain an incomplete or multiple messages
     * @param message the bytes to process
     */
    public void eventBytesHandler(byte[] message) {
        for (byte b : message) {
            if (parseEscaped) {
                //Currently escaped
                //Handle valid escape sequences
                if (SerialCommunicationUtility.isStartOfMessage(b) || SerialCommunicationUtility.isEndOfMessage(b) || SerialCommunicationUtility.isEscape(b))
                    messageStore.write(b);

                //No longer escaped
                parseEscaped = false;
            }
            else if (parseStarted) {
                if (SerialCommunicationUtility.isStartOfMessage(b)) {
                    //Handle start byte (not escaped)
                    //Discard old data
                    messageStore.reset();
                }
                else if (SerialCommunicationUtility.isEndOfMessage(b)) {
                    //Handle end byte (not escaped)
                    //messageStore now contains entire message
                    
                    try {
                        //Checks CRC using destructMessage()
                        byte[] destructedMessage = SerialCommunicationUtility.destructMessage(messageStore.toByteArray());
                        //This is a valid message, sends to serialMessageHandler
                        messageHandler(destructedMessage);
                    }
                    catch (IllegalArgumentException e) {
                        //Catches any exceptions thrown from destructMessage such as invalid CRC
                        //Invalid message so restarts parse
                        parseStarted = false;
                    }
                }
                else if (SerialCommunicationUtility.isEscape(b)) {
                    //Handle escape byte (not escaped)
                    parseEscaped = true;
                }
                else {
                    messageStore.write(b);
                }
            }
            else if (SerialCommunicationUtility.isStartOfMessage(b)){
                parseStarted = true;
                messageStore.reset();
            }
        }
    }

    private void watchdogDisable(byte [] message){
        String m = message.toString();
        if(m == "WDGK"){
            // TO DO 
        }
    }
    
    /**
     * Processes a complete message's payload.
     * The message is assumed to have had the correct CRC and start and end with the proper bytes
     * @param message The message to process
     */
    public void messageHandler(byte[] message) {
        try {
            /*
            //If message does not start with start byte or end with end byte, it is ignored
            if (!SerialCommunicationUtility.isStartOfMessage(message[0]) ||
                    !SerialCommunicationUtility.isEndOfMessage(message[message.length - 1]))
                throw new IllegalArgumentException("Message does not start with start byte or end byte: " + Arrays.toString(message));
                
            //Remove start and end bytes
            byte[] strippedMessage = Arrays.copyOfRange(message, 1, message.length - 1);
            //Will throw IllegalArgumentException if garbage/corrupted
            byte[] decodedMessage = SerialCommunicationUtility.destructMessage(strippedMessage);
            */
            
            //Remove message ID of received message
            byte[] strippedMessage = Arrays.copyOfRange(message, 2, message.length);

            if (ByteArrayUtility.startsWith(strippedMessage, WATCHDOG_STATUS.getBytes())) {
                if (strippedMessage[4] == (byte)0)
                    WatchDogStatus.getInstance().setWatchDogKill(true);
                else if (strippedMessage[4] == (byte)1)
                    WatchDogStatus.getInstance().setWatchDogKill(false);
            }
            else if(ByteArrayUtility.startsWith(strippedMessage, MS5837_STATUS.getBytes())){
                byte [] data = Arrays.copyOfRange(strippedMessage,7,strippedMessage.length);
                ByteBuffer buffer = ByteBuffer.wrap(data);
                buffer.order(ByteOrder.LITTLE_ENDIAN);
                float num = buffer.getFloat();
                depths.depth.enqueue(num);
            }
            else if(ByteArrayUtility.startsWith(strippedMessage, BNO055_STATUS.getBytes())){
                byte [] data = Arrays.copyOfRange(strippedMessage,7,strippedMessage.length);
                ByteBuffer buffer = ByteBuffer.wrap(data);
                buffer.order(ByteOrder.LITTLE_ENDIAN);

                float num1 = buffer.getFloat();
                float num2 = buffer.getFloat();
                float num3 = buffer.getFloat();
                float num4 = buffer.getFloat();
                float num5 = buffer.getFloat();
                float num6 = buffer.getFloat();
                float num7 = buffer.getFloat();

                imuData.gyrox.enqueue(num1);
                //imuData.gyrox.enqueue(num2);
                //imuData.gyrox.enqueue(num3);
                //imuData.gyrox.enqueue(num4);
                //imuData.gyrox.enqueue(num5);
                //imuData.gyrox.enqueue(num6);
                //imuData.gyrox.enqueue(num7);


            }
            else if (ByteArrayUtility.startsWith(strippedMessage, ACKNOWLEDGE.getBytes())) {
                //Pushes message onto message stack if acknowledge message
                MessageStack.getInstance().push(Arrays.copyOfRange(strippedMessage, 3, strippedMessage.length));
            }
            else if (ByteArrayUtility.startsWith(strippedMessage, DEBUG_STRING.getBytes())) {
                // THIS IS AN EXTREMELY CRUDE WAY TO READ DEBUG MESSAGES
                // FIX LATER 
                // ISO-8859-1 is ASCII
                System.out.println(System.currentTimeMillis() + "> DEBUG: " + new String(strippedMessage, StandardCharsets.US_ASCII));
            }
            else if (ByteArrayUtility.startsWith(strippedMessage, DEBUG_RAW.getBytes())) {
                // THIS IS AN EXTREMELY CRUDE WAY TO READ DEBUG MESSAGES
                // FIX LATER 
                System.out.println("DEBUG_RAW: " + Arrays.toString(strippedMessage));
            }
            else {
                //Received message is not a watchdog or acknowledgement message, it is ignored
                throw new IllegalArgumentException("Received message is not a watchdog status or an acknowledge message: " + Arrays.toString(strippedMessage));
            }
            
        }
        catch (IllegalArgumentException e) {
            System.out.println("Something went wrong in receiving a message");
            System.out.println(e.getMessage());
        }
    }

    /**
     * Returns the current depth and gyrox 
     */
    public float getDepth(){
        return depths.depth.getCurrentValue();
    }
    public float[] getGyroData(){
        float [] values = new float[6];
        values[0] = imuData.gyrox.getCurrentValue();
        values[1] = imuData.gyroz.getCurrentValue();
        values[2] = imuData.quat_w.getCurrentValue();
        values[3] = imuData.quat_x.getCurrentValue();
        values[4] = imuData.quat_y.getCurrentValue();
        values[5] = imuData.quat_z.getCurrentValue();

        return values;
    }
}
