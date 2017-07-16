/************************************************
 *
 * Author:      Quoc-Bao Huynh
 * Assignment:  Program 0
 * Class:       CSI 4321
 *
 ************************************************/
package instayak.serialization;

import java.io.Serializable;

/*
 * InstaYakException
 *
 * 1.0
 *
 * February 24, 2017
 *
 * Copyright
 */
public class InstaYakException extends Exception implements Serializable {

	/**
	 * Constructs an InstaYak exception
	 * 
	 * @param message
	 *            exception message
	 * @param cause
	 *            exception cause
	 */
	public InstaYakException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Constructs an InstaYak exception
	 * 
	 * @param message
	 *            exception message
	 */
	public InstaYakException(String message) {
		super(message);
	}
}
