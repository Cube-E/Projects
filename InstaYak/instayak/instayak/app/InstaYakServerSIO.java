package instayak.app;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.Iterator;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class InstaYakServerSIO {

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

	private static final int BUFSIZE = 256; // Buffer size (bytes)
	private static final int TIMEOUT = 3000; // Wait timeout (milliseconds)

	public static void main(String[] args) throws IOException {
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


		// Create a selector to multiplex listening sockets and connections
		Selector selector = Selector.open();

		// Create listening socket channel for each port and register selector
		ServerSocketChannel listnChannel = ServerSocketChannel.open();
		listnChannel.socket().bind(new InetSocketAddress(Integer.parseInt(args[0])));
		listnChannel.configureBlocking(false); // must be nonblocking to
												// register
		// Register selector with channel. The returned key is ignored
		listnChannel.register(selector, SelectionKey.OP_ACCEPT);

		// Create a handler that will implement the protocol
		SIOHandler protocol = new InstaYakSIOHandler();

		while (true) { // Run forever, processing available I/O operations
			// Wait for some channel to be ready (or timeout)
			if (selector.select(TIMEOUT) == 0) { // returns # of ready chans
				continue;
			}

			// Get iterator on set of keys with I/O to process
			Iterator<SelectionKey> keyIter = selector.selectedKeys().iterator();
			while (keyIter.hasNext()) {
				SelectionKey key = keyIter.next(); // Key is bit mask
				// Server socket channel has pending connection requests?
				if (key.isAcceptable()) {
					protocol.handleAccept(key);
				}
				// Client socket channel has pending data?
				if (key.isReadable()) {
					protocol.handleRead(key, logger, pFile);
				}
				if (key.isValid() && key.isWritable()) {
					protocol.handleWrite(key);
				}
				keyIter.remove(); // remove from set of selected keys
			}
		}
		} catch (SecurityException | IOException e) {
			System.exit(-1);
		}
	}

}
