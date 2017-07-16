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
 * InstaYakMessage
 *
 * 1.0
 *
 * February 24, 2017
 *
 * Copyright
 */
public abstract class InstaYakMessage {
	// Frame for the message read from the inputStream.
	protected final String LINE_DELIM = "\r\n";
	// Space character used when writing to outputStream.
	protected final String SPACE = " ";

	/**
	 * Default constructor. Makes an instayak.serialization.InstaYakMessage
	 */
	public InstaYakMessage() {

	}

	/**
	 * Deserializes message from input source
	 * 
	 * @param in
	 *            deserialization input source
	 * @return a specific InstaYak message resulting from deserialization
	 * @throws InstaYakException
	 *             if parse or validation problem
	 * @throws IOException
	 *             if I/O problem
	 */
	public static InstaYakMessage decode(MessageInput in) throws InstaYakException, IOException {
		String op = in.getTokenTillSpace();
		switch (op) {
		case InstaYakVersion.OP:
			return new InstaYakVersion(in);
		case InstaYakID.OP:
			return new InstaYakID(in);
		case InstaYakChallenge.OP:
			return new InstaYakChallenge(in);
		case InstaYakCredentials.OP:
			return new InstaYakCredentials(in);
		case InstaYakUOn.OP:
			return new InstaYakUOn(in);
		case InstaYakSLMD.OP:
			return new InstaYakSLMD(in);
		case InstaYakACK.OP:
			return new InstaYakACK(in);
		case InstaYakError.OP:
			return new InstaYakError(in);
		default:
			// if invalid token, read the rest of the line.
			try{
			in.getTokenTillNewLine();
			}catch(IOException e){
				throw new InstaYakException("Invalid Bounds");
			}
			throw new InstaYakException("Error: Invalid Operation");
		}

	}

	/**
	 * Gets the message operation.
	 * 
	 * @return the message operation
	 */
	public abstract String getOperation();

	/**
	 * Serializes message to given output sink
	 * 
	 * @param out
	 *            serialization output sink
	 * @throws IOException
	 *             if I/O problem
	 */
	public abstract void encode(MessageOutput out) throws IOException;

	/**
	 * tests alphanumeric
	 * 
	 * @param str
	 *            string that has what is tested
	 * @return boolean of whether the string is all numeric
	 */
	public static boolean hasAlphNumeric(String str) {
		if (str.isEmpty()) {
			return false;
		}
		for (char c : str.toCharArray()) {
			if (!(Character.isLetter(c) || Character.isDigit(c))) {
				return false;
			}
		}
		return true;
	}

	/**
	 * tests numeric characters
	 * 
	 * @param str
	 *            string that has what is tested
	 * @return boolean of whether the string is all numeric
	 */
	public static boolean hasNumeric(String str) {
		if (str.isEmpty()) {
			return false;
		}
		for (char c : str.toCharArray()) {
			if (!(Character.isDigit(c))) {
				return false;
			}
		}
		return true;
	}

}
