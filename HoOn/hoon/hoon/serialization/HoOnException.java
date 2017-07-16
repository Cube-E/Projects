/************************************************
 *
 * Author:      Quoc-Bao Huynh
 * Assignment:  Program 4
 * Class:       CSI 4321
 *
 ************************************************/
package hoon.serialization;

import java.io.Serializable;

/*
 * HoOnException
 *
 * 1.0
 *
 * March 21, 2017
 *
 * Copyright
 */
public class HoOnException extends Exception implements Serializable {

	private ErrorCode errorCode;

	/**
	 * Constructs a HoOn exception
	 * 
	 * @param errorCode
	 *            The errorCode passed in
	 */
	public HoOnException(ErrorCode errorCode) {
		super(errorCode.getErrorMessage());
		this.errorCode = errorCode;
	}

	/**
	 * Constructs a HoOn exception
	 * 
	 * @param errorCode
	 *            the ErrorCode passed in
	 * @param cause
	 *            the cause
	 */
	public HoOnException(ErrorCode errorCode, Throwable cause) {
		super(errorCode.getErrorMessage(), cause);
		this.errorCode = errorCode;

	}

	/**
	 * Returns the errorCode stored
	 * 
	 * @return the ErrorCode stored
	 */
	public ErrorCode getErrorCode() {
		return this.errorCode;
	}

}
