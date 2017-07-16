/************************************************
 *
 * Author:      Quoc-Bao Huynh
 * Assignment:  Program 0
 * Class:       CSI 4321
 *
 ************************************************/
package instayak.serialization;

import java.io.*;

/*
 * MessageInput
 *
 * 1.0
 *
 * February 24, 2017
 *
 * Copyright
 */
public class MessageInput {

	//The inputstream wrapper
	private InputStreamReader sin;
	// The encoding
	private static String encoding = "ISO-8859-1";
	// Constant '\n'
	private static Character backN = '\n';
	// Constant '\r'
	private static Character backR = '\r';
	// Constant ' '
	private static Character space = ' ';

	/**
	 * Constructs an input source from an InputStream
	 * 
	 * @param in
	 *            byte input source
	 */
	public MessageInput(InputStream in) {
		try {
			sin = new InputStreamReader(in, encoding);
		} catch (UnsupportedEncodingException | NullPointerException e) {
			e.getMessage();
		}

	}

	/**
	 * Parses till a space is reached
	 * 
	 * @return String the token read in till the space
	 * @throws IOException
	 *             if error in opening stream
	 */
	public String getTokenTillSpace() throws IOException {
		if (sin == null) {
			throw new IOException("null inputStream");
		}
		// int to hold the currently read in character
		int ch= -1;
		// set if a space is encountered. to exit loop
		boolean flag = false;
		String op = "";
		// Read one byte at a time.
		while (!flag && ((ch = sin.read()) > -1)) {
			// Check for ' ' or '\r'.
			if ((char) ch == space || (char) ch == backR) {
				flag = true;
			} else {
				op = op + (char) ch;
			}
			
		}
		if(ch == -1){
			throw new IOException("End of stream");
		}
		return op;
	}

	/**
	 * Parses till a \r\n is reached
	 * 
	 * @return String the token read in till the new line
	 * @throws IOException if null or invalid frame
	 */
	public String getTokenTillNewLine() throws IOException {
		if (sin == null) {
			throw new IOException("Null input stream");
		}
		String msg = "";
		int ch;
		// Read one byte at a time.
		while (((ch = sin.read()) > -1)) {
			// Compare if the byte is a '\r' or \'n'
			if ((char) ch != backN && ((char) ch != backR)) {
				msg = msg + (char) ch;
			}
			// Check for the "\r\n" frame.
			if ((char) ch == backR) {
				if (validateNewLine()) {
					return msg;
				}
				// "\r\n" frame was not found.
				throw new IOException("\r\n was not found");
			}
			if ((char) ch == backN) {
				throw new IOException("no \n");
			}

		}
		if (ch == -1) {
			throw new IOException("End of stream");
		}
		return msg;
	}

	/**
	 * Reads a character and test if it is a '\n'
	 * 
	 * @throws IOException
	 *             if error in opening stream
	 * @return boolean represents whether a newline was reached
	 */
	public boolean validateNewLine() throws IOException {
		int ch;
		if ((ch = sin.read()) > -1) {
			if ((char) ch == backN) {
				return true;
			}
		}
		// Invalid frame so read till the next new line
		boolean flag = false;
		while (!flag && ((ch = sin.read()) > -1)) {
			if ((char) ch == backN) {
				flag = true;
			}
		}
		return false;
	}

}
