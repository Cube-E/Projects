/************************************************
 *
 * Author:      Quoc-Bao Huynh
 * Assignment:  Program 0
 * Class:       CSI 4321
 *
 ************************************************/
package instayak.serialization;

import java.io.IOException;

/*
 * InstaYakError
 *
 * 1.0
 *
 * February 24, 2017
 *
 * Copyright
 */
public class InstaYakError extends InstaYakMessage {

	// The InstaYakError operation.
	public static final String OP = "ERROR";
	// The InstaYakError error message.
	private String message;

	/**
	 * Constructs error message using set values
	 * 
	 * @param message
	 *            InstaYak error message
	 * @throws InstaYakException
	 *             if validation fails
	 */
	public InstaYakError(String message) throws InstaYakException {
		setMessage(message);
	}

	/**
	 * Constructs error message using deserialization. Only parses material
	 * specific to this message (that is not operation)
	 * 
	 * @param in
	 *            deserialization input source
	 * @throws InstaYakException
	 *             if parse or validation failure
	 * @throws IOException
	 *             if I/O problem
	 */
	public InstaYakError(MessageInput in) throws InstaYakException, IOException {
			setMessage(in.getTokenTillNewLine());
	}

	/**
	 * Returns message
	 * 
	 * @return String representation of InstaYakError. ex: ("Error: Message=Bad
	 *         stuff")
	 */
	public String toString() {
		return ("Error: Message=" + getMessage());
	}

	/**
	 * Gets message
	 * 
	 * @return String the InstaYakError message
	 */
	public final String getMessage() {
		return message;
	}

	/**
	 * Sets message
	 * 
	 * @param message
	 *            new message
	 * @throws InstaYakException
	 *             if null or invalid message
	 */
	public final void setMessage(String message) throws InstaYakException {
		if (message == null || !InstaYakError.hasAlphNumeric(message)) {
			this.message = null;
			throw new InstaYakException("Error: Invalid message");
		}
		this.message = message;
	}

	/**
	 * Returns message operation "ERROR"
	 * 
	 * @return message operation
	 */
	public String getOperation() {
		return (OP);
	}

	/**
	 * Serializes message to given output sink
	 * 
	 * @param out
	 *            serialization output sink
	 * @throws IOException
	 *             if I/O problem
	 */
	public void encode(MessageOutput out) throws IOException {
		out.writeMessage(OP + SPACE + this.message + LINE_DELIM);
	}

	/**
	 * Tests for alphanumeric
	 * 
	 * @param str
	 *            string will be tested for alphanumeric characters and spaces
	 * @return boolean Whether the string is valid
	 */
	public static boolean hasAlphNumeric(String str) {
		if (str.isEmpty()) {
			return false;
		}
		for (char c : str.toCharArray()) {
			if (!(Character.isLetter(c) || Character.isDigit(c) || Character.isWhitespace(c))) {
				return false;
			}
		}
		return true;
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
		if (obj instanceof String) {
			return message.equals(obj);
		}
		if (obj instanceof InstaYakError) {
			InstaYakError err = (InstaYakError) obj;
			return message.equals(err.message);
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
		result = 29 * result + message.hashCode();
		return result;
	}
}
