/************************************************
 *
 * Author:      Quoc-Bao Huynh
 * Assignment:  Program 1
 * Class:       CSI 4321
 *
 ************************************************/
package instayak.serialization;

import java.io.IOException;

/*
 * InstaYakSLMD
 *
 * 1.0
 *
 * February 24, 2017
 *
 * Copyright
 */
public class InstaYakSLMD extends InstaYakMessage {

	// The InstaYakSLMD operation
	public static final String OP = "SLMD";
	// Constant "invalid" string
	private static final String INVALID = "Invalid";

	/**
	 * Default SLMD constructor
	 */
	public InstaYakSLMD() {

	}

	/**
	 * Constructs new ACK message using deserialization. Only parses material
	 * specific to this message (that is not operation)
	 * 
	 * @param in
	 *            deserialization input source
	 * @throws IOException
	 *             for IO problems
	 * @throws InstaYakException
	 *             for invalid strings
	 */
	public InstaYakSLMD(MessageInput in) throws IOException, InstaYakException {
		if (!in.validateNewLine()) {
			throw new InstaYakException(INVALID + SPACE + OP);
		}
	}

	/**
	 * Returns an InstaYakSLMD String representation
	 * 
	 * @return string an InstaYakSLMD String representation
	 */
	public String toString() {
		return (OP);
	}

	/**
	 * Returns an InstaYakSLMD operation
	 * 
	 * @return String an InstaYakSLMD operation "SLMD"
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
		// Throws an IOException if the message could not be written.
		out.writeMessage(OP + LINE_DELIM);
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

		// Check if an InstaYakVersion was passed in.
		if (obj instanceof InstaYakSLMD) {
			InstaYakSLMD v = (InstaYakSLMD) obj;
			return OP.equals(v.getOperation());
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
		result = 29 * result + OP.hashCode();
		return result;
	}
}
