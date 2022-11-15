package org.aquapackrobotics.sw8s.comms;

import java.util.Arrays;

/**
 * Helpers functions to deal with byte arrays as messages
 */
public class ByteArrayUtility {

	/**
	 * Checks if the start of a byte array matches another
	 * @param array The byte array to inspect
	 * @param start The start of the byte array expected
	 * @return true if array starts with the bytes in start
	 */
	public static boolean startsWith(byte[] array, byte[] start) {
		return array != null && Arrays.equals(Arrays.copyOf(array, start.length), start);
	}
}
