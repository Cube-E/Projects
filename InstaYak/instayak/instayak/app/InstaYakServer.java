/************************************************
 *
 * Author:      Quoc-Bao Huynh
 * Assignment:  Program 3
 * Class:       CSI 4321
 *
 ************************************************/
package instayak.app;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import instayak.serialization.InstaYakException;
import twitter4j.TwitterException;

/*
 * InstaYakServer
 *
 * 1.0
 *
 * February 28, 2017
 *
 * Copyright
 */
public class InstaYakServer {
	// The connections logger file name
	public static String CONNECTION_FILE = "connections";
	// Constant if password file is not found.
	private static String PASS_NOT_FOUND = "Password file not found";
	// The encoding of the handler
	private static String encoding = ("ISO-8859-1");
	// Number of arguments
	private static int numArg = 3;
	// The logger
	public static Logger logger;

	/**
	 * Runs the InstaYakServer
	 * 
	 * @param args
	 *            Includes port#, number of threads, and password file
	 */
	public static void main(String[] args) {
		// Test for correct # of args
		if (args.length != numArg) {
			System.err.println("Parameter(s): <Port><numberOfThreads><passwordFile>");
			System.exit(-1);
		}
		// Server port
		int instaYakServPort = Integer.parseInt(args[0]);
		try {
			// Create a server socket to accept client connection requests
			final ServerSocket servSock = new ServerSocket(instaYakServPort);
			final int threadPoolSize = Integer.parseInt(args[1]);
			final String passFile = args[2];
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
			// create a logger
			logger = Logger.getLogger(CONNECTION_FILE);
			FileHandler handler = new FileHandler("connections.log");
			handler.setEncoding(encoding);
			logger.addHandler(handler);
			handler.setFormatter(new SimpleFormatter());
			// Spawn a fixed number of threads to service clients
			spawnThreads(threadPoolSize, servSock, pFile);
		} catch (IOException | SecurityException | IllegalArgumentException | NullPointerException e) {
			System.err.println(e.getMessage());
			System.exit(-1);
		}
	}

	/**
	 * Spawns the number of threads given and runs them.
	 * 
	 * @param threadPoolsize
	 *            the number of threads desired
	 * @param servSock
	 *            the server socket
	 * @param pFile
	 *            the password file
	 */
	private static void spawnThreads(int threadPoolsize, ServerSocket servSock, File pFile) {
		for (int i = 0; i < threadPoolsize; i++) {
			Thread thread = new Thread() {
				public void run() {
					while (true) {
						try {
							Socket clntSock = servSock.accept(); // Wait for a
																	// connection
							InstaYakServerProtocol.handleInstaYakClient(clntSock, logger, pFile); // Handle
																									// it
						} catch (SecurityException | NullPointerException | IllegalArgumentException | IOException
								| InterruptedException e) {
							logger.log(Level.WARNING, "Unable to communicate " + e.getMessage());
						} catch (InstaYakException e) {
							logger.log(Level.WARNING, "Invalid message: " + e.getMessage());
						} catch (TwitterException e) {
							System.err.println("Problems with Twitter");
							e.getMessage();
						}
					}
				}
			};
			thread.start();
		}
	}
}
