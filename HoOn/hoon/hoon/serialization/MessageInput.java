/************************************************
 *
 * Author:      Quoc-Bao Huynh
 * Assignment:  Program 4
 * Class:       CSI 4321
 *
 ************************************************/
package hoon.serialization;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/*
 * MessageInput
 *
 * 1.0
 *
 * February 24, 2017
 *
 * Copyright
 */
public abstract class MessageInput {

	// The bitmask for a correct version
	private static char versionBitmask = 0xF0;
	// The bitmask for a correct query/response
	private static char QRBitmask = 0x0F;
	// A bitmask with all 1s
	private static int FFBitmask = 0xFF;
	// A correct version
	private static int validVersion = 32;
	// The correct 0s and 1s for a response
	private static int validResponse = 8;
	// Returned if a response is indicated
	private static boolean response = true;
	// The correct 0s and 1s for a query
	private static int validQuery = 0;
	// Returned if a query is indicated
	private static boolean query = false;
	// The amount of bits in a byte
	private static int byteSizeInBits = 8;
	// How many bytes a post length takes up
	private static int lengthOfPostLength = 2;
	// The version/QR index into the byte[]
	private static int versionQRIndex = 0;
	// The ErrorCode index into the byte[]
	private static int errorCodeIndex = 1;
	// The QueryId index into the byte[]
	private static int queryIdIndex = 2;
	// The data field index into the byte[]
	private static int dataIndex = 6;
	// The post index into the byte[]
	private static int postIndex = 8;
	// The character set for the bytes read in
	private static Charset charset = Charset.forName("US-ASCII");

	/**
	 * checks the version of the packet
	 * 
	 * @param buff
	 *            the byte[] to check
	 * @return a boolean if it is a valid version
	 * @throws IOException if IO errors
	 */
	public static boolean checkVersion(byte[] buff) throws IOException {
		char version = (char) buff[versionQRIndex];
		if (buff.length < 1) {
			throw new IOException("Version: insufficient bytes");
		}
		if ((version & versionBitmask) == validVersion) {
			return true;
		}
		return false;
	}

	/**
	 * reads the QR
	 * 
	 * @param buff
	 *            the byte[] to check
	 * @return true if Response. False if Query.
	 * @throws HoOnException
	 *             if neither a query or response
	 * @throws IOException if IO errors
	 */
	public static boolean getQR(byte[] buff) throws HoOnException, IOException {
		char firstByte = (char) buff[versionQRIndex];
		if (buff.length < 1) {
			throw new IOException("QR: insufficient bytes");
		}
		if ((firstByte & QRBitmask) == validResponse) {
			return response;
		} else if ((firstByte & QRBitmask) == validQuery) {
			return query;
		}
		// Reserve is occupied
		throw new HoOnException(ErrorCode.UNEXPECTEDPACKETTYPE);

	}

	/**
	 * Reads the error code from the byte[]
	 * 
	 * @param buff
	 *            the byte[] to read from
	 * @return the ErrorCode value read in
	 * @throws IOException
	 *             if IO errors
	 */
	public static int readErrCode(byte[] buff) throws IOException {
		if (buff.length < 2) {
			throw new IOException("ErrorCode: insufficient bytes");
		}
		return buff[errorCodeIndex];

	}

	/**
	 * Reads the queryID from the byte[]
	 * 
	 * @param buff
	 *            the byte[] to read from
	 * @return a queryID as a unsigned Long
	 * @throws IOException
	 *             if IO errors
	 */
	public static long readQueryId(byte[] buff) throws IOException {
		long result = 0;

		if (buff.length < 6) {
			throw new IOException("QueryId: insufficient bytes");
		}
		for (int i = 0; i < Integer.BYTES; i++) {
			result = result << byteSizeInBits;
			result = result | (buff[queryIdIndex + i] & FFBitmask);
		}
		return result;
	}

	/**
	 * Reads the number of posts
	 * 
	 * @param buff
	 *            the byte[] to read from
	 * @return the number of posts as an unsigned integer
	 * @throws IOException
	 *             if IO errors
	 */
	public static int readNumberPosts(byte[] buff) throws IOException {
		int result = 0;
		if (buff.length < 8) {
			throw new IOException("NumberOfPosts: insufficient bytes");
		}
		for (int i = 0; i < Short.BYTES; i++) {
			result = result << byteSizeInBits;
			result = result | (int) (buff[dataIndex + i] & FFBitmask);
		}
		return result;
	}

	/**
	 * reads all the posts
	 * 
	 * @param buff
	 *            the byte[] to read from
	 * @param numberOfPosts
	 *            the number of posts
	 * @return A list of all the posts
	 * @throws IOException
	 *             if IO errors
	 */
	public static List<String> readPosts(byte[] buff, int numberOfPosts) throws IOException {
		List<String> strList = new ArrayList<String>(numberOfPosts);
		int listNdx = 0;
		int ndx = postIndex;
		while (listNdx < numberOfPosts) {
			int postLength = readPostLength(buff, ndx);
			ndx += lengthOfPostLength;
			if ((buff.length - ndx) < postLength) {
				throw new IOException("Reading Posts: insufficient bytes");
			}
			strList.add(new String(buff, ndx, postLength, charset));
			ndx += postLength;
			listNdx++;

		}
		if (buff.length != ndx) {
			throw new IOException("Too many extra bytes");
		}
		return strList;
	}

	/**
	 * reads the length of each post
	 * 
	 * @param buff
	 *            the byte[] to read from
	 * @param ndx
	 *            the index to start reading from the byte[]
	 * @return an integer representing the post length
	 * @throws IOException
	 *             if IO errors
	 */
	private static int readPostLength(byte[] buff, int ndx) throws IOException {
		int postLength = 0;
		if (buff.length - ndx < lengthOfPostLength) {
			throw new IOException("PostLength:Not enough bytes");
		}
		for (int i = 0; i < Short.BYTES; i++) {
			postLength = postLength << byteSizeInBits;
			postLength = postLength | (buff[ndx + i] & FFBitmask);
		}

		return postLength;
	}

}
