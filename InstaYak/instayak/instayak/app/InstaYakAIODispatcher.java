/************************************************
 *
 * Author:      Quoc-Bao Huynh
 * Assignment:  Program 7
 * Class:       CSI 4321
 *
 ************************************************/
package instayak.app;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import instayak.serialization.InstaYakError;
import instayak.serialization.InstaYakException;
import instayak.serialization.MessageOutput;

/*
 * InstaYakAIODispatcher
 *
 * 1.0
 *
 * April 18, 2017
 *
 * Some code taken from Dr. Donahoo
 */
public class InstaYakAIODispatcher {
	/**
	 * Default buffer size
	 */
	private static final int BUFSIZE = 655353;
	/**
	 * Protocol-specific handler
	 */
	private final AIOHandler hdlr;
	/**
	 * Server logger
	 */
	private final Logger logger;
	/**
	 * Buffer to read into
	 */
	private ByteBuffer readBuffer = ByteBuffer.allocateDirect(BUFSIZE);
	/**
	 * Buffers to write from
	 */
	private List<ByteBuffer> writeBufferList = new ArrayList<>();
	/**
	 * Local byte buffer for processing
	 */
	private final byte[] localBuffer = new byte[BUFSIZE];
	/**
	 * Server password file
	 */
	private File pfile;

	/**
	 * Instantiate dispatcher for new client
	 * 
	 * @param hdlr
	 *            protocol-specific handler
	 * @param logger
	 *            server logger
	 * @param pFile
	 *            the server password file
	 */
	public InstaYakAIODispatcher(final AIOHandler hdlr, final Logger logger, File pFile) {
		this.hdlr = hdlr;
		this.logger = logger;
		this.pfile = pFile;
	}

	/**
	 * Handle client connection accept
	 * 
	 * @param clntChan
	 *            accepted channel
	 */
	public void handleAccept(final AsynchronousSocketChannel clntChan) {
		// Get bytes (if any) to send and write them
		processWriteBuffer(clntChan, hdlr.handleAccept());
	}

	/**
	 * Prepare the write buffer containing given bytes to be sent on given
	 * channel
	 * 
	 * @param clntChan
	 *            channel on which to send bytes
	 * @param buf
	 *            bytes to send
	 */
	private void processWriteBuffer(final AsynchronousSocketChannel clntChan, final byte[] buf) {
		// If buffer contains data to write, prepare for sending
		if (buf != null && buf.length > 0) {
			writeBufferList.add(ByteBuffer.wrap(buf));
			clntChan.write(writeBufferList.toArray(new ByteBuffer[] {}), 0, writeBufferList.size(), -1, null, this,
					makeWriteCompletionHandler(clntChan, logger));
			// If buffer does not contain data, prepare for reading
		} else {
			clntChan.read(readBuffer, this, makeReadCompletionHandler(clntChan, logger));
		}
	}

	/**
	 * Handle client write
	 * 
	 * @param clntChan
	 *            channel for writing
	 */
	public void handleWrite(final AsynchronousSocketChannel clntChan) {
		// Remove first buffer in list until list is empty or the first buffer
		// has bytes left to write
		while (!writeBufferList.isEmpty() && !writeBufferList.get(0).hasRemaining()) {

			writeBufferList.remove(0);
		}
		// Nothing to write, so read
		if (writeBufferList.isEmpty()) {
			clntChan.read(readBuffer, this, makeReadCompletionHandler(clntChan, logger));
		} else {
			// More to write
			clntChan.write(writeBufferList.toArray(new ByteBuffer[] {}), 0, writeBufferList.size(), -1, null, this,
					makeWriteCompletionHandler(clntChan, logger));
		}
	}

	/**
	 * Create completion handler for write
	 * 
	 * @param clntChan
	 *            channel for writing
	 * @param logger
	 *            server logger
	 * 
	 * @return write completion handler
	 */
	public static CompletionHandler<Long, InstaYakAIODispatcher> makeWriteCompletionHandler(
			final AsynchronousSocketChannel clntChan, final Logger logger) {
		return new CompletionHandler<Long, InstaYakAIODispatcher>() {
			/*
			 * Called when write completes
			 * 
			 * @param clntChan channel for write
			 * 
			 * @param aioDispatcher AIO dispatcher for handling write
			 */
			public void completed(final Long bytesWritten, final InstaYakAIODispatcher aioDispatcher) {
				aioDispatcher.handleWrite(clntChan);
			}

			/*
			 * Called if read fails
			 * 
			 * @param ex exception triggered by read failure
			 * 
			 * @param aioDispatcher AIO dispatcher for handling read
			 */
			public void failed(final Throwable ex, final InstaYakAIODispatcher aioDispatcher) {
				logger.log(Level.WARNING, "write failed", ex);
				try {
					clntChan.close();
				} catch (IOException e) {
					logger.warning("Attempted to close " + clntChan + " and failed");
				}
			}
		};
	}

	/**
	 * Handle client read
	 * 
	 * @param clntChan
	 *            channel for reading
	 * @throws IOException
	 *             if IO errors
	 * @throws InstaYakException
	 *             if serialization problems
	 */
	public void handleRead(final AsynchronousSocketChannel clntChan) throws IOException, InstaYakException {
		// Read next set of bytes
		readBuffer.flip();
		readBuffer.get(localBuffer, 0, readBuffer.limit());
		// Allow protocol to handle the read
		byte buf[];
		try {
			buf = hdlr.handleRead(Arrays.copyOf(localBuffer, readBuffer.limit()), logger, clntChan, pfile);
			readBuffer.clear();
			// Write result
			processWriteBuffer(clntChan, buf);
		} catch (IllegalArgumentException e) {
			processWriteBuffer(clntChan, writeInstaYakError(e.getMessage()));
			clntChan.close();
		} catch (IllegalStateException e) {
			clntChan.close();
		}

	}

	/**
	 * Create completion handler for read
	 * 
	 * @param clntChan
	 *            channel for reading
	 * @param logger
	 *            server logger
	 * 
	 * @return read completion handler
	 */
	public static CompletionHandler<Integer, InstaYakAIODispatcher> makeReadCompletionHandler(
			final AsynchronousSocketChannel clntChan, final Logger logger) {
		return new CompletionHandler<Integer, InstaYakAIODispatcher>() {
			/*
			 * Called when read completes
			 * 
			 * @param clntChan channel for read
			 * 
			 * @param aioDispatcher AIO dispatcher for handling read
			 */
			public void completed(final Integer bytesRead, final InstaYakAIODispatcher aioDispatcher) {
				try {
					// If other end closed, we will
					if (bytesRead == -1) {
						clntChan.close();
						return;
					}
					// Call protocol-specific handler
					aioDispatcher.handleRead(clntChan);
				} catch (IOException | InstaYakException ex) {
					failed(ex, aioDispatcher);
				}
			}

			/*
			 * Called if read fails
			 * 
			 * @param ex exception triggered by read failure
			 * 
			 * @param aioDispatcher AIO dispatcher for handling read
			 */
			public void failed(final Throwable ex, final InstaYakAIODispatcher aioDispatcher) {
				logger.log(Level.WARNING, "read failed", ex);
				try {
					clntChan.close();
				} catch (IOException e) {
					logger.warning("Attempted to close " + clntChan + " and failed");
				}
			}
		};
	}

	/**
	 * Send an InstaYakError with the error message
	 * 
	 * @param errMsg
	 *            the error message
	 * @throws InstaYakException
	 *             if an error occurs
	 * @throws IOException
	 *             if an IO errors occurs
	 */
	private static byte[] writeInstaYakError(String errMsg) throws InstaYakException, IOException {
		ByteArrayOutputStream b = new ByteArrayOutputStream();
		MessageOutput out = new MessageOutput(b);
		InstaYakError err = new InstaYakError(errMsg);
		err.encode(out);
		return b.toByteArray();
	}

}
