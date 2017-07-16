/************************************************
 *
 * Author:      Quoc-Bao Huynh
 * Assignment:  Program 4
 * Class:       CSI 4321
 *
 ************************************************/
package hoon.serialization;

import java.io.IOException;

/*
 * HoOnMessage
 *
 * 1.0
 *
 * March 21, 2017
 *
 * Copyright
 */
public abstract class HoOnMessage {

	// Holds the queryId of the packet
	private long queryId;
	// Holds the ErrorCode of the packet
	protected ErrorCode errorCode = ErrorCode.NOERROR;
	// A Response message is equivalent to 1 or true
	private static boolean response = true;
	// A Query message is equivalent to 0 or true
	private static boolean query = false;
	// how many eight bits is
	protected static int eightBits = 8;
	// the index of the query into the buffer
	protected static int queryIdNdx = 2;
	// The max query size
	private static long maxQuerySize = 4294967295L;

	/**
	 * Deserialize HoOn Message header
	 * 
	 * @param buffer
	 *            bytes from which to deserialize
	 * @return Deserialized HoOn message
	 * @throws HoOnException
	 *             if deserialization or validation fails (treat null buffer
	 *             like empty array). Validation problems include
	 *             insufficient/excess bytes (PACKETTOOSHORT/LONG), incorrect
	 *             version (BADVERSION), bad reserve (NETWORKERROR), unexpected
	 *             error code (UNEXPECTEDERRORCODE), and unexpected type
	 *             (UNEXPECTEDPACKETTYPE)
	 */
	public static HoOnMessage decode(byte[] buffer) throws HoOnException {
		// Check if byte array is null
		if (buffer == null || buffer.length < 1) {
			throw new HoOnException(ErrorCode.PACKETTOOSHORT);
		}

		try {
			boolean QR = MessageInput.getQR(buffer);
			if (QR == response) {
				return new HoOnResponse(buffer);
			} else if (QR == query) {
				return new HoOnQuery(buffer);
			} else {
				throw new HoOnException(ErrorCode.UNEXPECTEDPACKETTYPE);
			}
		} catch (IOException e) {
			throw new HoOnException(ErrorCode.PACKETTOOSHORT);
		}

	}

	/**
	 * Serialize the HoOn message
	 * 
	 * @return serialized HoOn message
	 * @throws HoOnException
	 *             if error during serialization
	 */
	public abstract byte[] encode() throws HoOnException;

	/**
	 * Set the message query ID
	 * 
	 * @param queryId
	 *            the new query ID
	 * @throws IllegalArgumentException
	 *             if the given ID is out of range
	 */
	public void setQueryId(long queryId) throws IllegalArgumentException {
		// Test whether queryId is out of range

		if (queryId > maxQuerySize || queryId < 0) {
			throw new IllegalArgumentException("Invalid queryId field");
		}
		this.queryId = queryId;
	}

	/**
	 * Get the message query ID
	 * 
	 * @return current query ID
	 */
	public long getQueryId() {
		return this.queryId;
	}

	/**
	 * Get the message error code
	 * 
	 * @return message error code
	 */
	public ErrorCode getErrorCode() {
		return errorCode;
	}

}
