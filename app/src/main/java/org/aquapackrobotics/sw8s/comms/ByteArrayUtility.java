/**
 * 
 */
package org.aquapackrobotics.sw8s.comms;

import java.util.Arrays;

/**
 * @author vaibh
 *
 */
public class ByteArrayUtility {

	public static boolean startsWith(byte[] array, byte[] start) {
		return Arrays.equals(Arrays.copyOf(array, start.length), start);
	}
}
