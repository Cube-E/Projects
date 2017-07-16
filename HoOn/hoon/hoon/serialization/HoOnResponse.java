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
import java.util.List;

/*
 * HoOnResponse
 *
 * 1.0
 *
 * March 21, 2017
 *
 * Copyright
 */
public class HoOnResponse extends HoOnMessage {
	// The maximum number of posts
	private static int maxNumPosts = 65535;
	// The maximum length of a post
	private static int maxPostLength = 65535;
	// The minimum response packet size
	private static int minResponseSize = 8;
	// The index of the post length
	private static int begPostLenNdx = 8;
	// The post length size in bytes
	private static int byteSizePostLen = 2;
	// The character set to decode from
	private static Charset charset = Charset.forName("US-ASCII");
	// The version and query to be put into the array
	private static int versionPlusResponse = 40;
	// Size of a post length in bytes
	private static int bytePostLength = 2;
	// Holds the number of posts
	private int postNum;
	// Holds the posts
	private List<String> posts;

	/**
	 * Creates a new HoOn response given individual elements
	 * 
	 * @param errorCode
	 *            error code for response
	 * @param queryId
	 *            ID for response
	 * @param posts
	 *            list of posts
	 * @throws IllegalArgumentException
	 *             If the queryId or post list are outside the allowable range
	 * @throws HoOnException if HoOn error occurs
	 */
	public HoOnResponse(ErrorCode errorCode, long queryId, List<String> posts)
			throws IllegalArgumentException, HoOnException {
		setErrorCode(errorCode.getErrorCodeValue());
		setQueryId(queryId);
		setPosts(posts);

	}

	/**
	 * Deserialize HoOn response
	 * 
	 * @param buffer
	 *            bytes from which to deserialize
	 * @throws HoOnException
	 *             if deserialization fails (treat null buffer like empty array)
	 */
	public HoOnResponse(byte[] buffer) throws HoOnException {
		if (buffer == null) {
			throw new HoOnException(ErrorCode.UNEXPECTEDPACKETTYPE);
		}

		if (buffer.length < minResponseSize) {
			throw new HoOnException(ErrorCode.PACKETTOOSHORT);
		}
		// Check version
		try {
			if (!MessageInput.checkVersion(buffer)) {
				throw new HoOnException(ErrorCode.BADVERSION);
			}
			if (!MessageInput.getQR(buffer)) {
				throw new HoOnException(ErrorCode.UNEXPECTEDPACKETTYPE);
			}
		} catch (IOException e) {
			throw new HoOnException(ErrorCode.PACKETTOOSHORT, e.getCause());
		}
		// get errorcode
		try {
			setErrorCode(MessageInput.readErrCode(buffer));
		} catch (IllegalArgumentException | IOException e) {
			throw new HoOnException(ErrorCode.UNEXPECTEDERRORCODE, e.getCause());
		}
		// get queryId
		try {
			setQueryId(MessageInput.readQueryId(buffer));
			// get number of posts
			postNum = MessageInput.readNumberPosts(buffer);
			// Populate the list of posts
			setPosts(MessageInput.readPosts(buffer, postNum));
		} catch (IOException e) {
			throw new HoOnException(ErrorCode.PACKETTOOSHORT, e.getCause());
		}

	}

	/**
	 * Overrides the toString class
	 */
	public String toString() {
		String str = "ErrorCode: " + errorCode + "\nQueryId: " + getQueryId() + "\nPosts:\n";
		for (int i = 0; i < posts.size(); i++) {
			// Subtract one so a new line is not put at the last post in the
			// list
			if (i != posts.size() - 1) {
				str += posts.get(i) + "\n";
			} else {
				str += posts.get(i);
			}
		}
		return str;

	}

	/**
	 * Get the response list of posts
	 * 
	 * @return current list of posts
	 */
	public List<String> getPosts() {
		return posts;
	}

	/**
	 * Set the response list of posts
	 * 
	 * @param posts
	 *            new list of posts
	 * @throws IllegalArgumentException
	 *             if (list is null or outside length range) OR (an individual
	 *             post is null or outside length range)
	 */
	public void setPosts(List<String> posts) throws IllegalArgumentException {
		if (posts == null || posts.size() > maxNumPosts) {
			throw new IllegalArgumentException();
		}
		for (String str : posts) {
			if (str == null || str.getBytes(charset).length > maxPostLength) {
				throw new IllegalArgumentException();
			}
		}
		this.posts = posts;

	}

	/**
	 * Set the response error value (0-7)
	 * 
	 * @param errorCodeValue
	 *            new error value
	 * @throws IllegalArgumentException
	 *             if the error code value is out of range
	 * 
	 */
	public void setErrorCode(int errorCodeValue) throws IllegalArgumentException {
		this.errorCode = ErrorCode.getErrorCode(errorCodeValue);
	}

	/**
	 * Set the response error code
	 * 
	 * @param errorCode the ErrorCode to set
	 */
	public void setErrorCode(ErrorCode errorCode) {
		this.errorCode = errorCode;
	}

	/**
	 * overrides encode from HoOnMessage
	 */
	@Override
	public byte[] encode() throws HoOnException {
		try {
			int responsePacketSize = minResponseSize;
			for (String str : getPosts()) {
				responsePacketSize += bytePostLength;
				responsePacketSize += str.length();
			}
			byte[] b = new byte[responsePacketSize];
			b[0] = (byte) versionPlusResponse;
			b[1] = (byte) getErrorCode().getErrorCodeValue();
			b = writeQueryId(b, getQueryId());
			b = writePostLength(b, getPosts().size());
			int ndx = begPostLenNdx;
			for (String str : getPosts()) {
				b = writePostSize(b, str.length(), ndx);
				ndx += byteSizePostLen;
				b = writePost(b, str, ndx);
				ndx += str.length();
			}
			return b;
		} catch (Exception e) {
			throw new HoOnException(ErrorCode.NETWORKERROR, e.getCause());
		}

	}

	/**
	 * writePostSize
	 * 
	 * @param buff
	 *            the buffer to write to
	 * @param size
	 *            the size to be written
	 * @param ndx
	 *            the index to write the size to
	 * @return the array with the post written to it
	 */
	public byte[] writePostSize(byte[] buff, int size, int ndx) {
		for (int i = 0; i < Short.BYTES; i++) {
			buff[ndx] = (byte) (size >> (Short.BYTES - i - 1) * 8);
			ndx++;
		}
		return buff;
	}

	/**
	 * writePost
	 * 
	 * @param buff
	 *            the buffer to write to
	 * @param str
	 *            the String to write
	 * @param ndx
	 *            the index to write the string to
	 * @return the array with the string written to it
	 */
	public byte[] writePost(byte[] buff, String str, int ndx) {
		for (int i = 0; i < str.length(); i++) {
			buff[ndx] = (byte) str.charAt(i);
			ndx++;
		}
		return buff;
	}

	/**
	 * writeQueryId
	 * 
	 * @param buff
	 *            the buffer to write to
	 * @param queryId
	 *            the queryId to write
	 * @return the array with the queryId written in
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
	 * writePostLength
	 * 
	 * @param buff
	 *            the buffer to write to
	 * @param requestedPostNum
	 *            the number of posts to write
	 * @return the array with the number of posts written to it
	 */
	public byte[] writePostLength(byte[] buff, int requestedPostNum) {
		int offset = 6;
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
		if (obj instanceof HoOnResponse) {
			HoOnResponse response = (HoOnResponse) obj;
			boolean flag = true;
			List<String> t = this.getPosts();
			List<String> r = response.getPosts();
			for (int i = 0; i < t.size(); i++) {
				if (!(t.get(i).equals(r.get(i)))) {
					flag = false;
				}
			}
			for (int i = 0; i < r.size(); i++) {
				if (!(r.get(i).equals(t.get(i)))) {
					flag = false;
				}
			}

			return this.getErrorCode().getErrorCodeValue() == response.getErrorCode().getErrorCodeValue()
					&& this.getQueryId() == response.getQueryId() && flag;
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
		result = 29 * result + postNum;
		return result;
	}

}
