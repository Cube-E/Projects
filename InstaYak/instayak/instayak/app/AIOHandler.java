/************************************************
 *
 * Author:      Quoc-Bao Huynh
 * Assignment:  Program 7
 * Class:       CSI 4321
 *
 ************************************************/
package instayak.app;

import java.io.File;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.logging.Logger;

/*
 * AIOHandler
 *
 * 1.0
 *
 * April 18, 2017
 *
 * Some code taken from Dr. Donahoo
 */
public interface AIOHandler {
	/**
	 * Handle accept of new connection and return message to write. By default
	 * this is a no-op that writes nothing.
	 * 
	 * @return message to write (null if none)
	 */
	default byte[] handleAccept() {
		return null;
	}

	/**
	 * Handle write from given buffer. By default, this is a no-op.
	 * 
	 * @param writeBuff
	 *            written bytes
	 */
	default void handleWrite(byte[] writeBuff) {
	}

	/**
	 * Handle read to given buffer and return any message in response to read
	 * bytes.
	 * 
	 * @param readBuff
	 *            read bytes
	 * @param logger
	 *            the logger to log messages
	 * @param clntChan
	 *            the client channel
	 * @param pFile
	 *            the password file
	 * @return message in response to read bytes (null if none)
	 * @throws IllegalArgumentException
	 *             if illegal arguement is given
	 * @throws IllegalStateException
	 *             if the an illegal state is taken
	 */
	byte[] handleRead(byte[] readBuff, Logger logger, final AsynchronousSocketChannel clntChan, File pFile)
			throws IllegalArgumentException, IllegalStateException;
}
