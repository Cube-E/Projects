/************************************************
 *
 * Author:      Quoc-Bao Huynh
 * Assignment:  Program 0
 * Class:       CSI 4321
 *
 ************************************************/
package instayak.serialization;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

/*
 * MessageOutput
 *
 * 1.0
 *
 * February 24, 2017
 *
 * Copyright
 */
public class MessageOutput {

	// the offset into the stream
	private final int OFFSET = 0;
	// The OutputStreamWriter member variable
	private OutputStreamWriter oStream;

	/**
	 * Constructs a new output sink from an OutputStream
	 * 
	 * @param out
	 *            byte output sink
	 */
	public MessageOutput(OutputStream out) {
		try {
			oStream = new OutputStreamWriter(out, "ISO-8859-1");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Writes the message given to the outputstream
	 * 
	 * @param message
	 *            the message to write
	 * @throws IOException
	 *             if IO problems
	 */
	public void writeMessage(String message) throws IOException {
		oStream.write(message, OFFSET, message.length());
		oStream.flush();
	}
}
