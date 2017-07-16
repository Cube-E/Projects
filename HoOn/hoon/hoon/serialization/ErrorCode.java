/************************************************
 *
 * Author:      Quoc-Bao Huynh
 * Assignment:  Program 4
 * Class:       CSI 4321
 *
 ************************************************/
package hoon.serialization;

/*
 * ErrorCode
 *
 * 1.0
 *
 * March 21, 2017
 *
 * Copyright
 */
public enum ErrorCode {

	// Indicates no error.
	NOERROR(0, "No error"),
	// Indicates a message with a bad version was received.
	BADVERSION(1, "Bad version"),
	// Indicates a message with an unexpected error code was received.
	UNEXPECTEDERRORCODE(2, "Unexpected error code"),
	// Indicates a message with an unexpected packet type was received
	UNEXPECTEDPACKETTYPE(3, "Unexpected packet type"),
	// Indicates a message with extraneous trailing bytes was received.
	PACKETTOOLONG(4, "Packet too long"),
	// Indicates a message with insufficient bytes was received.
	PACKETTOOSHORT(5, "Packet too short"),
	// Indicates some network error occurred.
	NETWORKERROR(7, "Network error");
	// The value of the error. Ranges from 0-6.
	private int errVal;
	// The message associated with the error.
	private String message;

	/**
	 * Constructor of the errorCode enum
	 * 
	 * @param errVal
	 * @param message
	 */
	private ErrorCode(int errVal, String message) {
		this.errVal = errVal;
		this.message = message;
	}

	/**
	 * Get the error value (0-6)
	 * 
	 * @return the value associate with the error code
	 */
	public int getErrorCodeValue() {
		return errVal;
	}

	/**
	 * Get the error message
	 * 
	 * @return the message associate with the error code
	 */
	public String getErrorMessage() {
		return message;
	}

	/**
	 * Get the error code associated with the given error value
	 * 
	 * @param errorCodeValue
	 *            the value of the error code
	 * @return error code associated with given value
	 * @throws IllegalArgumentException
	 *             if error value is out of range
	 */
	public static ErrorCode getErrorCode(int errorCodeValue) throws IllegalArgumentException {
		switch (errorCodeValue) {
		case 0:
			return NOERROR;
		case 1:
			return BADVERSION;
		case 2:
			return UNEXPECTEDERRORCODE;
		case 3:
			return UNEXPECTEDPACKETTYPE;
		case 4:
			return PACKETTOOLONG;
		case 5:
			return PACKETTOOSHORT;
		case 7:
			return NETWORKERROR;
		default:
			throw new IllegalArgumentException("Invalid ErrorCode " + errorCodeValue);

		}
	}

}
