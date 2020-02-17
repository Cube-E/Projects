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
 * InstaYakID
 *
 * 1.0
 *
 * February 24, 2017
 *
 * Copyright
 */
public class InstaYakID extends InstaYakMessage {

	// The InstaYakID operation.
	public static final String OP = "ID";
	// Variable to hold ID read in.
	private String identification;

	/**
	 * Constructs an ID from the ID passed in
	 * 
	 * @param ID
	 *            id passed in
	 * @throws InstaYakException
	 *             if validation errors
	 */
	public InstaYakID(String ID) throws InstaYakException {
		setID(ID);
	}

	/*******************************************************************/
	/**
	 * Constructs ID message using deserialization. Only parses material
	 * specific to this message (that is not operation)
	 * 
	 * @param in
	 *            deserialization input source
	 * @throws InstaYakException
	 *             if parse or validation failure
	 * @throws IOException
	 *             if I/O problem
	 */
	public InstaYakID(MessageInput in) throws InstaYakException, IOException {
		// get and set the ID
			setID(in.getTokenTillNewLine());
	}

	/*******************************************************************/
	/**
	 * Return a string representation of InstaYakID
	 * 
	 * @return InstaYakID string representation
	 */
	public String toString() {
		return ("ID: ID=" + this.identification);
	}

	/*******************************************************************/
	/**
	 * Gets ID String member variable
	 * 
	 * @return InstaYakID ID
	 */
	public final String getID() {
		return this.identification;
	}

	/*******************************************************************/
	/**
	 * Sets ID member variable from ID passed in.
	 * 
	 * @param ID
	 *            Supposed InstaYakID ID
	 * @throws InstaYakException
	 *             if validation error
	 */
	public final void setID(String ID) throws InstaYakException {
		// Check if ID only letters and numbers;
		if (ID == null || !hasAlphNumeric(ID)) {
			identification = null;
			throw new InstaYakException("Error: Invalid ID");
		}
		// store the identification value
		identification = ID;
	}
	/*******************************************************************/
	// * @overrides getOperation in class InstaYakMessage

	/**
	 * Returns the specific message operation
	 * 
	 * @return String describing the operation
	 */
	public String getOperation() {
		return OP;
	}
	/*******************************************************************/
	// * @overrides encode in class InstaYakMessage

	/**
	 * Serializes message to given output sink
	 * 
	 * @param out
	 *            serialization output sink
	 * @throws IOException
	 *             if I/O problem
	 */
	public void encode(MessageOutput out) throws IOException {
		out.writeMessage(OP + SPACE + identification + LINE_DELIM);
	}

	@Override
	/**
	 * Overrides equals of Object
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
			return identification.equals(obj);
		}
		// Check if an InstaYakID was passed to compare.
		if (obj instanceof InstaYakID) {
			InstaYakID id = (InstaYakID) obj;
			return identification.equals(id.identification) && this.getOperation().equals(id.getOperation());
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
		result = 29 * result + identification.hashCode();
		return result;
	}
}
