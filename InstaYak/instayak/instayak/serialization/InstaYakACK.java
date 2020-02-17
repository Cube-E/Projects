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
 * InstaYakACK
 *
 * 1.0
 *
 * February 24, 2017
 *
 * Copyright
 */
public class InstaYakACK extends InstaYakMessage {

	// The InstaYakACK operation.
	public static final String OP = "ACK";

	/**
	 * Default constructor for InstaYakACK
	 */
	public InstaYakACK() {
	}

	/**
	 * Constructs new ACK message using deserialization. Only parses material
	 * specific to this message (that is not operation)
	 * 
	 * @param in
	 *            deserialization input source
	 * @throws IOException
	 *             if IO problems
	 * @throws InstaYakException
	 *             if validation problems
	 */
	public InstaYakACK(MessageInput in) throws IOException, InstaYakException {
		if (!in.validateNewLine()) {
			throw new InstaYakException("Invalid ACK");
		}
	}

	/**
	 * Returns an InstaYakACK String representation ("ACK")
	 * 
	 * @return string an InstaYakACK String representation
	 */
	public String toString() {
		return (OP);
	}

	/**
	 * Returns an InstaYakACK operation "ACK"
	 * 
	 * @return an InstaYakACK operation
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
		if (obj instanceof InstaYakACK) {
			InstaYakACK v = (InstaYakACK) obj;
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
