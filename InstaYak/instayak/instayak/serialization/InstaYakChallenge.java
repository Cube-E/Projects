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
 * InstaYakChallenge
 *
 * 1.0
 *
 * February 24, 2017
 *
 * Copyright
 */
public class InstaYakChallenge extends InstaYakMessage {

	// The InstaYakChallenge operation.
	public static final String OP = "CLNG";
	// The InstaYakChallenge nonce holder.
	private String non;

	/*******************************************************************/
	/**
	 * Constructs challenge message using given values
	 * 
	 * @param nonce
	 *            a challenge nonce
	 * @throws InstaYakException
	 *             if validation fails
	 */
	public InstaYakChallenge(String nonce) throws InstaYakException {
		// Set nonce value.
		this.setNonce(nonce);
	}

	/*******************************************************************/
	/**
	 * Constructs challenge message using deserialization. Only parses material
	 * specific to this message (that is not operation)
	 * 
	 * @param in
	 *            deserialization input source
	 * @throws InstaYakException
	 *             if parse or validation failure
	 * @throws IOException
	 *             if I/O problem
	 */
	public InstaYakChallenge(MessageInput in) throws IOException, InstaYakException {
		// Get and set the nonce value.
		setNonce(in.getTokenTillNewLine());
	}

	/*******************************************************************/
	/**
	 * returns an InstaYakChallenge string representation ("Challenge:
	 * Nonce=12345")
	 * 
	 * @return string representation of InstaYakChallenge
	 */
	public String toString() {
		return "Challenge: Nonce=" + non;
	}

	/*******************************************************************/
	/**
	 * gets the nonce
	 * 
	 * @return non nonce
	 */
	public final String getNonce() {
		return non;
	}

	/*******************************************************************/
	/**
	 * sets the supposed InstaYakChallenge nonce
	 * 
	 * @param nonce
	 *            The supposed InstaYakChallenge
	 * @throws InstaYakException
	 *             if null or invalid nonce
	 */
	public void setNonce(String nonce) throws InstaYakException {
		// Check if nonce only has numbers
		if (nonce == null || !(hasNumeric(nonce))) {
			throw new InstaYakException("Error: Invalid Nonce");
		}
		// store the nonce value
		non = nonce;
	}

	/*******************************************************************/
	/**
	 * Returns InstaYakChallenge operation
	 * 
	 * @return InstaYakChallenge operation
	 */
	public String getOperation() {
		return OP;
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
		out.writeMessage(OP + SPACE + non + LINE_DELIM);

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
			return non.equals(obj);
		}
		if (obj instanceof InstaYakChallenge) {
			InstaYakChallenge chal = (InstaYakChallenge) obj;
			return non.equals(chal.non) && this.getOperation().equals(chal.getOperation());
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
		result = 29 * result + non.hashCode();
		return result;
	}
}
