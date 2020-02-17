package instayak.app;

import java.io.File;
import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.logging.Logger;

public interface SIOHandler {
	/**
	 * Handle accept of new connection and return message to write. By default
	 * this is a no-op that writes nothing.
	 * 
	 * @return message to write (null if none)
	 */
	void handleAccept(SelectionKey key) throws IOException;

	/**
	 * Handle write from given buffer. By default, this is a no-op.
	 * 
	 * @param writeBuff
	 *            written bytes
	 */
	void handleWrite(SelectionKey key) throws IOException;
	
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
	 * @throws IOException 
	 */
	 void handleRead(SelectionKey key , Logger logger, File pFile)throws IllegalArgumentException, IllegalStateException, IOException;

	
}
