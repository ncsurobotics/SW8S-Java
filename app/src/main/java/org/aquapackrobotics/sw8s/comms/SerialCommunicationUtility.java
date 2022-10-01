package org.aquapackrobotics.sw8s.comms;

/**
 * Provides helpers for creating and parsing messages
 * that the SW8 control board could use.
 */
class SerialCommunicationUtility {
    /**
     * self.__ser.write(ControlBoard.START_BYTE)
     *         for i in range(len(msg)):
     *             c = msg[i:i+1]
     *             if c == ControlBoard.START_BYTE or c == ControlBoard.END_BYTE or c == ControlBoard.ESCAPE_BYTE:
     *                 self.__ser.write(ControlBoard.ESCAPE_BYTE)
     *             self.__ser.write(c)
     *         crc = Crc16.calcbytes(msg, byteorder='big')
     *         self.__ser.write(crc)
     *         self.__ser.write(ControlBoard.END_BYTE)
     */

    private static final byte START_BYTE = (byte) 253;
    private static final byte END_BYTE = (byte) 254;
    private static final byte ESCAPE_BYTE = (byte) 255;

    public static byte[] constructMessage(byte[] message) {
        StringBuilder formattedMessage = new StringBuilder();

        long crc = CRC.calculateCRC(CRC.Parameters.CRC16, message);

        for (byte msgByte : message) {
            if (msgByte == START_BYTE || msgByte == END_BYTE || msgByte == ESCAPE_BYTE) {
                formattedMessage.append(ESCAPE_BYTE);
            }

            formattedMessage.append(msgByte);
        }

        // TODO THIS DOESN'T CONFORM TO SPEC, NEED TO ESCAPE & HIGH & LOW ORDER
        formattedMessage.append(crc);
        
        formattedMessage.append(END_BYTE);

        return formattedMessage.toString().getBytes();
    }

    public static byte[] destructMessage(byte[] message) {
        StringBuilder deformattedMessage = new StringBuilder();


        return deformattedMessage.toString().getBytes();
    }
}
