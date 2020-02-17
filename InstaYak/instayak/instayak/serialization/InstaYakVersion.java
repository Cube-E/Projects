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
 * InstaYakVersion
 *
 * 1.0
 *
 * February 24, 2017
 *
 * Copyright
 */
public class InstaYakVersion extends InstaYakMessage {
	// The InstaYakVersion operation.
	public static final String OP = "INSTAYAK";

	// The version representation that will be passed from toString.
	private static String versionRep = "InstaYak";
	// The version will be stored here.
	private static String version = "1.0";

	/**
	 * Default constructor of InstaYakVersion
	 */
	public InstaYakVersion() {
	}

	/*******************************************************************/
	/**
	 * Constructs version message using deserialization. Only parses material
	 * specific to this message (that is not operation)
	 * 
	 * @param in
	 *            deserialization input source
	 * @throws InstaYakException
	 *             if parse or validation failure
	 * @throws IOException
	 *             if I/O problem
	 */
	public InstaYakVersion(MessageInput in) throws InstaYakException, IOException {
		// Check that the version has a number on each side of the decimal.
		if (!(version.equals(in.getTokenTillNewLine()))) {
			throw new InstaYakException("Not the right version");
		}

	}

	/*******************************************************************/
	/**
	 * Returns a String representation ("InstaYak")
	 * 
	 * @return InstaYakVersion string representation
	 */
	public String toString() {
		return versionRep;
	}

	/*******************************************************************/
	/**
	 * Returns message operation
	 * 
	 * @return InstaYakVersion message operation
	 */
	public String getOperation() {
		return OP;
	}

	/*******************************************************************/
	/**
	 * Returns message version
	 * 
	 * @return InstaYakVersion message version
	 */
	public String getVersion() {
		return version;
	}

	/*******************************************************************/
	/**
	 * Serializes message to given output sink
	 * 
	 * @param out
	 *            serialization output sink
	 * @throws IOException
	 *             if I/O problem
	 */
	public void encode(MessageOutput out) throws IOException {
		out.writeMessage(OP + SPACE + version + LINE_DELIM);

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
		// Check if a string was passed in to compare.
		if (obj instanceof String) {
			return version.equals(obj);
		}
		// Check if an InstaYakVersion was passed in.
		if (obj instanceof InstaYakVersion) {
			InstaYakVersion v = (InstaYakVersion) obj;
			return version.equals(InstaYakVersion.version) && this.getOperation().equals(v.getOperation());
		}
		return false;
	}

	/**
	 * Overrides hashcode of object
	 * 
	 * @return int representing the hashcode
	 */
	@Override
	public int hashCode() {
		int result = 11;
		result = 29 * result + version.hashCode();
		return result;
	}

}
