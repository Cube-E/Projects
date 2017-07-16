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
 * InstaYakCredentials
 *
 * 1.0
 *
 * February 24, 2017
 *
 * Copyright
 */
public class InstaYakCredentials extends InstaYakMessage {

	// The InstaYakCredentials operation.
	public static final String OP = "CRED";
	// Regular expression pattern that the version will check against.
	private static String versionPattern = "[0-9A-F]+";
	// Regular expression pattern that the version will check against.
	private static int hashLength = 32;
	// The InstaYakCredentials hash holder.
	private String hash;

	/**
	 * Constructs credentials message using given hash
	 * 
	 * @param hash
	 *            The hash for credentials
	 * @throws InstaYakException
	 *             if validation of hash fails
	 */
	public InstaYakCredentials(String hash) throws InstaYakException {
		setHash(hash);
	}

	/**
	 * Constructs credentials message using deserialization. Only parses
	 * material specific to this message (that is not operation)
	 * 
	 * @param in
	 *            Deserialization input source
	 * @throws InstaYakException
	 *             if parse or validation failure
	 * @throws IOException
	 *             if I/O problem
	 */
	public InstaYakCredentials(MessageInput in) throws InstaYakException, IOException {
		setHash(in.getTokenTillNewLine());
	}

	/**
	 * Returns an InstaYakCredentials String representation. Ex: ("Credentials:
	 * Hash=12345")
	 * 
	 * @return string InstaYakCredentials String representation
	 */
	public String toString() {
		return ("Credentials: Hash=" + this.hash);
	}

	/**
	 * Returns InstaYakCredential hash
	 * 
	 * @return hash The InstaYakCredential hash
	 */
	public final String getHash() {
		return this.hash;
	}

	/**
	 * Sets supposed InstaYakCredential hash
	 * 
	 * @param hash
	 *            Supposed InstaYakCredential hash
	 * @throws InstaYakException
	 *             if null or invalid hash
	 */
	public final void setHash(String hash) throws InstaYakException {
		// validate only letters and numbers
		if (hash == null || hash.length() != hashLength || !hash.matches(versionPattern)) {
			throw new InstaYakException("Error: Invalid Hash");
		}
		this.hash = hash;
	}

	/**
	 * Returns message operation ERROR
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
		out.writeMessage(OP + SPACE + this.hash + LINE_DELIM);
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
			return hash.equals(obj);
		}
		if (obj instanceof InstaYakCredentials) {
			InstaYakCredentials cred = (InstaYakCredentials) obj;
			return hash.equals(cred.hash) && this.getOperation().equals(cred.getOperation());
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
		result = 29 * result + hash.hashCode();
		return result;
	}
}
