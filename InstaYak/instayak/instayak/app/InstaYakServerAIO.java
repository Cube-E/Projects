/************************************************
 *
 * Author:      Quoc-Bao Huynh
 * Assignment:  Program 7
 * Class:       CSI 4321
 *
 ************************************************/
package instayak.app;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/*
 * InstaYakServerAIO
 *
 * 1.0
 *
 * April 18, 2017
 *
 * Some code taken from Dr. Donahoo
 */
public class InstaYakServerAIO {

	// Number of arguments
	private static int numArg = 2;
	// The connections logger file name
	public static String CONNECTION_FILE = "connections";
	// Constant if password file is not found.
	private static String PASS_NOT_FOUND = "Password file not found";
	// The logger
	public static Logger logger;
	// The encoding of the handler
	private static String encoding = ("ISO-8859-1");

	public static void main(String[] args) throws SecurityException, IOException {
		// Test for correct # of
		if (args.length != numArg) {
			System.err.println("Parameter(s): <Port><passwordFile>");
			System.exit(-1);
		}

		// Server port
		int instaYakServPort = Integer.parseInt(args[0]);

		final String passFile = args[1];
		// Check if password file exist.
		if (passFile.equals(null)) {
			System.err.println(PASS_NOT_FOUND);
			System.exit(-1);
		}
		File pFile = new File(passFile);
		if (!pFile.exists()) {
			System.err.println(PASS_NOT_FOUND);
			System.exit(-1);
		}

		try {
			// create a logger
			logger = Logger.getLogger(CONNECTION_FILE);
			FileHandler handler = new FileHandler("connections.log");
			handler.setEncoding(encoding);
			logger.addHandler(handler);
			handler.setFormatter(new SimpleFormatter());

			// Create listening socket channel
			AsynchronousServerSocketChannel listenChannel = null;
			try {
				// Bind local port
				listenChannel = AsynchronousServerSocketChannel.open().bind(new InetSocketAddress(instaYakServPort));
				// Create accept handler
				listenChannel.accept(null, makeAcceptCompletionHandler(listenChannel, logger, pFile));
			} catch (IOException ex) {
				System.err.println("Unable to create server socket channel: " + ex.getMessage());
				System.exit(1);
			}
			// Block until current thread dies
			try {
				Thread.currentThread().join();
			} catch (InterruptedException e) {
			}
		} catch (SecurityException | IOException e) {
			System.err.println(e.getMessage());
			System.exit(-1);
		}

	}

	/**
	 * Create completion handler for accept
	 * 
	 * @param listenChannel
	 *            channel listening for new clients
	 * @param logger
	 *            server logger
	 * @param pFile
	 *            server password file
	 * @return completion handler
	 */
	public static CompletionHandler<AsynchronousSocketChannel, Void> makeAcceptCompletionHandler(
			final AsynchronousServerSocketChannel listenChannel, final Logger logger, File pFile) {
		return new CompletionHandler<AsynchronousSocketChannel, Void>() {
			/*
			 * Called when accept completes
			 * 
			 * @param clntChan channel for new client
			 * 
			 * @param v void means no attachment
			 */
			@Override
			public void completed(AsynchronousSocketChannel clntChan, Void v) {
				listenChannel.accept(null, this);
				InstaYakAIODispatcher aioDispatcher = new InstaYakAIODispatcher(new InstaYakAIOHandler(), logger,
						pFile);
				aioDispatcher.handleAccept(clntChan);
			}

			/*
			 * Called if accept fails
			 * 
			 * @param ex exception triggered by accept failure
			 * 
			 * @param v void means no attachment
			 */
			@Override
			public void failed(Throwable ex, Void v) {
				logger.log(Level.WARNING, "accept failed", ex);
			}
		};
	}
}
