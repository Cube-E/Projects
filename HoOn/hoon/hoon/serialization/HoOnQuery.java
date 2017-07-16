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
 * HoOnQuery
 *
 * 1.0
 *
 * March 21, 2017
 *
 * Copyright
 */
public class HoOnQuery extends HoOnMessage {

	// The size of a query packet
	private int queryPacketSize = 8;
	// Holds the number of posts
	private int requestedPostNum;
	// The maximum size a post can be
	private int maxPostSize = 65535;
	// The minimum size a post can be
	private int minPostSize = 0;
	// The maximum size a post can be
	private int begPostNdx = 6;
	// The version and query to be put into the array
	private static int versionPlusQuery = 32;
	// The error code to be put into the array
	private static int queryErrorCode = 0;

	/**
	 * Creates a new HoOn query given individual elements
	 * 
	 * @param queryId
	 *            ID for query
	 * @param requestedPosts
	 *            Number of requested posts
	 * @throws IllegalArgumentException
	 *             If the queryId or requestedPosts are outside the allowable
	 *             range
	 */
	public HoOnQuery(long queryId, int requestedPosts) throws IllegalArgumentException {
		setQueryId(queryId);
		setRequestedPosts(requestedPosts);
	}

	/**
	 * Deserialize HoOn query
	 * 
	 * @param buffer
	 *            bytes from which to deserialize
	 * @throws HoOnException
	 *             if deserialization fails (treat null buffer like empty array)
	 */
	public HoOnQuery(byte[] buffer) throws HoOnException{
		if (buffer == null) {
			throw new HoOnException(ErrorCode.UNEXPECTEDPACKETTYPE);
		}

		if (buffer.length > queryPacketSize) {
			throw new HoOnException(ErrorCode.PACKETTOOLONG);
		}
		try {
			// Check version
			if (!MessageInput.checkVersion(buffer)) {
				throw new HoOnException(ErrorCode.BADVERSION);
			}
			if (MessageInput.getQR(buffer)) {
				throw new HoOnException(ErrorCode.UNEXPECTEDPACKETTYPE);
			}
			// ErrorCode field should be NOERROR
			if (MessageInput.readErrCode(buffer) != ErrorCode.NOERROR.getErrorCodeValue()) {
				throw new HoOnException(ErrorCode.UNEXPECTEDERRORCODE);
			}
			// get query id and remove the queryId bytes
			setQueryId(MessageInput.readQueryId(buffer));
			setRequestedPosts(MessageInput.readNumberPosts(buffer));
		} catch (IOException e) {
			throw new HoOnException(ErrorCode.PACKETTOOSHORT, e.getCause());
		}catch(IllegalArgumentException e){
			System.out.println("in here");
			throw new HoOnException(ErrorCode.UNEXPECTEDPACKETTYPE, e.getCause());
		}
	}

	/**
	 * Overrides the toString class
	 */
	public String toString() {
		return "QueryId: " + getQueryId() + "\nRequested Posts: " + getRequestedPosts();
	}

	/**
	 * Get the number of requested posts in the message
	 * 
	 * @return current number of requested posts
	 */
	public int getRequestedPosts() {
		return requestedPostNum;
	}

	/**
	 * Set the number of requested posts in the message
	 * 
	 * @param requestedPosts
	 *            new number of requested posts
	 * @throws IllegalArgumentException
	 *             if number of requested posts out of range
	 */
	public void setRequestedPosts(int requestedPosts) throws IllegalArgumentException {
		if (requestedPosts > maxPostSize || requestedPosts < minPostSize) {
			throw new IllegalArgumentException("Invalid requestedPost field");
		}
		this.requestedPostNum = requestedPosts;
	}

	/**
	 * overrides HoOnQuery encode
	 */
	@Override
	public byte[] encode() throws HoOnException {
		byte[] b = new byte[queryPacketSize];
		b[0] = (byte) versionPlusQuery;
		b[1] = (byte) queryErrorCode;
		b = writeQueryId(b, getQueryId());
		b = writeRequestedPosts(b, getRequestedPosts());
		return b;

	}

	/**
	 * Writes the queryId to the buffer
	 * 
	 * @param buff
	 *            the buffer to write to
	 * @param queryId
	 *            the queryId to write
	 * @return the new byte array
	 */
	public byte[] writeQueryId(byte[] buff, long queryId) {
		int offset = queryIdNdx;
		for (int i = 0; i < Integer.BYTES; i++) {
			buff[offset] = (byte) (queryId >> (Integer.BYTES - i - 1) * eightBits);
			offset++;
		}
		return buff;
	}

	/**
	 * Writes the post number to the buffer
	 * 
	 * @param buff
	 *            the buffer to write to
	 * @param requestedPostNum
	 *            the post number to write
	 * @return the new byte array
	 */
	public byte[] writeRequestedPosts(byte[] buff, int requestedPostNum) {
		int offset = begPostNdx;
		for (int i = 0; i < Short.BYTES; i++) {
			buff[offset] = (byte) (requestedPostNum >> (Short.BYTES - i - 1) * eightBits);
			offset++;
		}

		return buff;
	}

	@Override
	/**
	 * Overrides equals
	 * 
	 * @param obj
	 *            an object to compare
	 * @return boolean representing whether it is equal
	 */
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		if (obj instanceof HoOnQuery) {
			HoOnQuery query = (HoOnQuery) obj;
			return this.requestedPostNum == query.getRequestedPosts() && this.getQueryId() == query.getQueryId();
		}
		return false;
	}

	@Override
	/**
	 * Overrides hashcode of object
	 * 
	 * @return int representing the hashcode
	 */
	public int hashCode() {
		int result = 11;
		result = 29 * result + requestedPostNum;
		return result;
	}
}
