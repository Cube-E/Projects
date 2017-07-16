/************************************************
 *
 * Author:      Dr. Donahoo
 * Assignment:  Program 2
 * Class:       CSI 4321
 *
 ************************************************/
package instayak.serialization;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class ComputeHash {

	/**
	 * Computes the md5 hash of a string
	 * @param msg the string to compute
	 * @return String of the computed hash
	 * @throws UnsupportedEncodingException the encoding is invalid
	 */
	public static String computeHash(String msg) throws UnsupportedEncodingException {
		try {
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			byte[] buf = md5.digest(msg.getBytes("ISO8859_1"));
			return hashToString(buf);
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("Unable to get MD5", e);
		}
	}

	/**
	 * converts the has to a string
	 * @param bytes the byte array to convert to a string
	 * @return String the hashed string
	 */
	public static String hashToString(byte[] bytes) {
		String hexHash = "";
		for (byte b : bytes) {
			String v = Integer.toHexString(Integer.valueOf(b & 0xff));
			if (v.length() == 1)
				v = "0" + v;
			hexHash += v.toUpperCase();
		}

		return hexHash;
	}
}
