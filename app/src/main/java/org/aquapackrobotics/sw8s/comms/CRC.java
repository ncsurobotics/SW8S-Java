package org.aquapackrobotics.sw8s.comms;

/**
 * Comms-only helper class to encode CRC.
 */
class CRC
{
    /**
     * Calculate 16-bit CRC (CCITT-FALSE) of the given data
     * @param data Data to calculate crc of
     * @param len The length to calculate the CRC of
     * @return Calculated crc
     */
    static short CITT16_False(byte[] data, int len) {
        short crc = (short) 0xFFFF;
        int pos = 0;
        while (pos < len) {
            byte b = data[pos];
            for(int i = 0; i < 8; ++i){
                byte bit = ((b >> (7 - i) & 1) == 1) ? (byte) 1 : (byte) 0;
                byte c15 = ((crc >> 15 & 1) == 1) ? (byte) 1 : (byte) 0;
                crc <<= 1;
                if ((c15 ^ bit) != 0) {
                    crc ^= 0x1021;
                }
            }

            pos++;
        }
        return crc;
    }
}