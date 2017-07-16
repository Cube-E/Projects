/************************************************
 *
 * Author:      Quoc-Bao Huynh
 * Assignment:  Program 3
 * Class:       CSI 4321
 *
 ************************************************/
package instayak.app;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import instayak.serialization.ComputeHash;
import instayak.serialization.InstaYakACK;
import instayak.serialization.InstaYakChallenge;
import instayak.serialization.InstaYakCredentials;
import instayak.serialization.InstaYakError;
import instayak.serialization.InstaYakException;
import instayak.serialization.InstaYakID;
import instayak.serialization.InstaYakMessage;
import instayak.serialization.InstaYakSLMD;
import instayak.serialization.InstaYakUOn;
import instayak.serialization.InstaYakVersion;
import instayak.serialization.MessageInput;
import instayak.serialization.MessageOutput;
import twitter4j.TwitterException;

/*
 * InstaYakServerProtocol
 *
 * 1.0
 *
 * February 28, 2017
 *
 * Copyright
 */
public class InstaYakServerProtocol implements Runnable {
	// Constant for space
	private static final String SPACE = " ";
	// Constant for colon
	private static final String COLON = ":";
	// Constant for backslash
	private static final String BACK_N = "\n";
	// Constant for hyphen
	private static final String HYPHEN = "-";
	// Constant for password pattern in the file
	private static final String PASS_PATTERN = ":(.*)";
	// Constant for jpeg extension
	private static final String IMG_EXT = ".jpg";
	// Constant for user slmd file extension
	private static final String FILE_EXT = ".SLMD";
	// Constant for ID message type
	private static final String MSG_ID = "id";
	// Constant for CRED message type
	private static final String MSG_CRED = "cred";
	// Constant for UON/SLMD message type
	private static final String MSG_UON_SLMD = "uonSLMD";
	// Constant for received error
	private static final String RCVD_MSG = "Received error: ";
	// Constant for slmd directory
	private static final String DIR_NAME = "userSLMDs";
	// Constant for communication error
	private static final String UN_TO_COM = "Unable to communicate: ";
	// Constant for unexpected message error
	private static final String UN_MSG = "Unexpected message: ";
	// File encoding type
	private static Charset encoding = Charset.forName("ISO-8859-1");
	// Number that all slmd messages start at.
	private static int startNum = 0;
	// The amount of time to wait for the client to send a message
	private static int timeout = 60000;
	// Socket connect to client
	private Socket clntSock;
	// Server logger
	private Logger logger;
	// Server Password file
	private File pFile;

	/**
	 * The contructor of the InstaYakServerProtocol
	 * 
	 * @param clntSock
	 *            the socket of the server
	 * @param logger
	 *            the logger of the server
	 * @param pFile
	 *            the password file for the server
	 */
	public InstaYakServerProtocol(Socket clntSock, Logger logger, File pFile) {
		this.clntSock = clntSock;
		this.logger = logger;
		this.pFile = pFile;
	}

	/**
	 * Handles the InstaYak client connected to the server
	 * 
	 * @param clntSock
	 *            the socket connected to the client
	 * @param logger
	 *            the logger to log the interactions of the server and client
	 * @param pFile
	 *            the password file
	 * @throws InstaYakException
	 *             if InstaYak serialization errors occur
	 * @throws IOException
	 *             if IO errors occur
	 * @throws TwitterException
	 *             if Twitter errors occur
	 * @throws InterruptedException
	 *             if a signal is sent to the handler
	 */
	public static void handleInstaYakClient(Socket clntSock, Logger logger, File pFile)
			throws InstaYakException, IOException, TwitterException, InterruptedException {
		try {
			clntSock.setSoTimeout(timeout);
			// Get the input and output I/O streams from socket
			MessageInput in = new MessageInput(clntSock.getInputStream());
			MessageOutput out = new MessageOutput(clntSock.getOutputStream());
			// Print the client being handled
			System.out.println("Handling client " + clntSock.getLocalAddress() + HYPHEN + clntSock.getLocalPort()
					+ " with thread id " + Thread.currentThread().getId());
			// Write an InstaYakVersion
			writeInstaYakVersion(out);
			// Read in InstaYakID
			InstaYakMessage msg = InstaYakMessage.decode(in);
			if (!checkMsgInstance(msg, MSG_ID, logger)) {
				clntSock.close();
				return;
			}
			String userID = readInstaYakID(msg, logger, clntSock, in);
			// Search for user ID in password file
			String password = getUserIDandPassword(pFile, userID, logger, clntSock, out);
			if (password == null) {
				clntSock.close();
				return;
			}
			// Generate a nonce to send in an InstaYakChallenge.
			Random rand = new Random(System.currentTimeMillis());
			String nonce = Integer.toString(getRandomNum(rand));
			String serverHash = getInstaYakChalHash(nonce, password);
			writeInstaYakChallenge(nonce, out);
			// Read an InstaYakCredentials
			msg = InstaYakMessage.decode(in);
			if (!checkMsgInstance(msg, MSG_CRED, logger)) {
				clntSock.close();
				return;
			}
			if (!readInstaYakCred(msg, logger, clntSock, serverHash, userID, out)) {
				TimeUnit.SECONDS.sleep(1);
				clntSock.close();
				return;
			}
			// Read UOn or SLMD
			while (!clntSock.isClosed()) {
				try {
					msg = InstaYakMessage.decode(in);
					if (!checkMsgInstance(msg, MSG_UON_SLMD, logger)) {
						clntSock.close();
						return;
					}
					sendUOnSLMD(msg, logger, userID, clntSock);
					writeInstaYakACK(out);
				} catch (SocketException e) {
					logger.log(Level.WARNING, UN_TO_COM + "***client terminated");
					clntSock.close();
				} catch (IOException e) {
					return;
				}
			}
		} catch (IOException e) {
			logger.log(Level.WARNING, UN_TO_COM + e.getMessage());

		} catch (InstaYakException e) {
			logger.log(Level.WARNING, "Invalid message: " + e.getMessage() + e.getClass());
		} finally {
			try {
				clntSock.close();
			} catch (IOException e) {
				e.getMessage();
			}
		}
	}

	/**
	 * implemented run method from runnable. Calls handleInstaYakClient
	 */
	public void run() {
		try {
			handleInstaYakClient(clntSock, logger, pFile);
		} catch (InstaYakException | IOException | TwitterException | InterruptedException e) {
			try {
				clntSock.close();
			} catch (IOException e1) {
				e1.getMessage();
			}
		}

	}

	/**
	 * Reads the InstaYakID from the client
	 * 
	 * @param msg
	 *            the InstaYakMessage
	 * @param in
	 *            the MessageInput
	 * @throws InstaYakException
	 *             if an error occurs
	 * @throws IOException
	 *             if an IO error occurs
	 */
	private static String readInstaYakID(InstaYakMessage msg, Logger logger, Socket clntSock, MessageInput in)
			throws InstaYakException, IOException {

		InstaYakID id = (InstaYakID) msg;
		String logMsg = clntSock.getLocalAddress() + SPACE + clntSock.getLocalPort() + SPACE + id.getID() + COLON
				+ SPACE + id.getOperation() + SPACE + id.getID() + BACK_N;
		logger.log(Level.INFO, logMsg);
		return id.getID();

	}

	/**
	 * Read in an InstaYakCredentials
	 * 
	 * @param msg
	 *            the InstaYakMessage credentials message
	 * @param logger
	 *            the logger to log all information to
	 * @param clntSock
	 *            the socket connected to the client
	 * @param serverHash
	 *            the hash computed by the server
	 * @param userID
	 *            the client identification
	 * @param out
	 *            the object that writes to the client
	 * @return a boolean if the server credentials matches the client
	 *         credentials
	 * @throws InstaYakException
	 *             if invalid InstaYak messages
	 * @throws IOException
	 *             if stream is unable to write
	 */
	private static boolean readInstaYakCred(InstaYakMessage msg, Logger logger, Socket clntSock, String serverHash,
			String userID, MessageOutput out) throws IOException, InstaYakException {

		InstaYakCredentials cred = (InstaYakCredentials) msg;
		if (!serverHash.equals(cred.getHash())) {
			String errMsg = "Unable to authenticate " + userID;
			logger.log(Level.WARNING, errMsg);
			writeInstaYakError(errMsg, clntSock, out);
			return false;
		}
		writeInstaYakACK(out);
		return true;

	}

	/**
	 * Sends the InstaYakVersion over to the client
	 * 
	 * @param arg
	 *            the ID to be passed
	 * @param out
	 *            the outputstream to be written to
	 * @throws IOException
	 *             if io errors occur
	 */
	private static void writeInstaYakVersion(MessageOutput out) throws IOException {
		InstaYakVersion ver = new InstaYakVersion();
		ver.encode(out);
	}

	/**
	 * Send over an InstaYakChallenge
	 * 
	 * @param nonce
	 *            the of the InstaYakChallenge object
	 * @param out
	 *            the output
	 * @throws IOException
	 * @throws InstaYakException
	 */
	private static void writeInstaYakChallenge(String nonce, MessageOutput out) throws IOException, InstaYakException {
		InstaYakChallenge chal = new InstaYakChallenge(nonce);
		chal.encode(out);
	}

	/**
	 * Send an InstaYakError with the error message
	 * 
	 * @param errMsg
	 *            the error message
	 * @param out
	 *            the MessageOutput to write to
	 * @throws InstaYakException
	 *             if an error occurs
	 * @throws IOException
	 *             if an IO errors occure
	 */
	private static void writeInstaYakError(String errMsg, Socket clntSock, MessageOutput out)
			throws InstaYakException, IOException {
		InstaYakError err = new InstaYakError(errMsg);
		err.encode(out);
		clntSock.shutdownOutput();
	}

	/**
	 * Send an InstaYakACK
	 * 
	 * @param out
	 *            the MessageOutput
	 * @throws IOException
	 *             if IO errors
	 */
	private static void writeInstaYakACK(MessageOutput out) throws IOException {
		InstaYakACK ack = new InstaYakACK();
		ack.encode(out);
	}

	/**
	 * Checks what type of InstaYak message the msg is
	 * 
	 * @param msg
	 *            the InstaYakMessage in question
	 * @param messageType
	 *            the message type wanted to be found
	 * @param logger
	 *            the logger to log all information to
	 * @return true if it is the message type and false otherwise
	 * @throws IOException
	 *             if IO errors
	 */
	private static boolean checkMsgInstance(InstaYakMessage msg, String messageType, Logger logger) throws IOException {
		if (MSG_ID.equals(messageType)) {
			if (!(msg instanceof InstaYakID)) {
				if (msg instanceof InstaYakError) {
					InstaYakError err = (InstaYakError) msg;
					logger.log(Level.WARNING, RCVD_MSG + err.getMessage());
					return false;
				}
				logger.log(Level.WARNING, UN_MSG + msg.getOperation());
				return false;
			}
		} else if (MSG_CRED.equals(messageType)) {
			if (!(msg instanceof InstaYakCredentials)) {
				if (msg instanceof InstaYakError) {
					InstaYakError err = (InstaYakError) msg;
					logger.log(Level.WARNING, RCVD_MSG + err.getMessage());
					return false;
				}
				logger.log(Level.WARNING, UN_MSG + msg.getOperation());
				return false;
			}
		} else if (MSG_UON_SLMD.equals(messageType)) {
			if (!(msg instanceof InstaYakUOn) && !(msg instanceof InstaYakSLMD)) {
				if (msg instanceof InstaYakError) {
					InstaYakError err = (InstaYakError) msg;
					logger.log(Level.WARNING, RCVD_MSG + err.getMessage());
					return false;
				}

				logger.log(Level.WARNING, UN_MSG + msg.getOperation());
				return false;
			}
		}
		return true;
	}

	/**
	 * Send either a uon or a slmd to Twitter
	 * 
	 * @param msg
	 *            the UOn or SLMD message
	 * @param logger
	 *            the logger to log all information to
	 * @param userID
	 *            the client identification
	 * @param clntSock
	 *            the socket the connected to the client
	 * @throws IOException
	 *             if io problems reading and writing to files
	 * @throws SocketException
	 *             if user terminates unexpectedly
	 */
	private static void sendUOnSLMD(InstaYakMessage msg, Logger logger, String userID, Socket clntSock)
			throws IOException, SocketException {

		if (msg instanceof InstaYakUOn) {
			InstaYakUOn uon = (InstaYakUOn) msg;
			// Make a file
			String fileName = userID + IMG_EXT;
			File userPic = new File(fileName);
			FileOutputStream fs = new FileOutputStream(userPic);
			fs.write(uon.getImage());
			fs.close();
			try {
				Twitter4J.sendUOn(userID, uon.getCategory(), userPic);
			} catch (TwitterException e) {
				clntSock.close();
			}
			String logMsg = clntSock.getLocalAddress() + SPACE + clntSock.getLocalPort() + SPACE + userID + COLON
					+ SPACE + uon.getOperation() + BACK_N;
			logger.log(Level.INFO, logMsg);
		} else if (msg instanceof InstaYakSLMD) {
			String userFileName = userID + FILE_EXT;
			File slmdFile = new File(makeSLMDdirectory(), userFileName);
			if (!slmdFile.exists()) {
				slmdFile.createNewFile();
			}
			Scanner is = new Scanner(slmdFile);
			int num = startNum;
			if (is.hasNextInt()) {
				num = is.nextInt();
			}
			boolean flag = false;
			String slmdMsg;
			while (!flag) {
				num = num + 1;
				PrintWriter pw = new PrintWriter(slmdFile);
				pw.print(num);
				pw.flush();
				slmdMsg = ("SLMD " + num);
				try {
					Twitter4J.sendSLMD(userID, slmdMsg);
					flag = true;
				} catch (TwitterException e) {
					flag = false;
				}
				if (flag) {
					String logMsg = clntSock.getLocalAddress() + SPACE + clntSock.getLocalPort() + SPACE + userID
							+ COLON + SPACE + slmdMsg + BACK_N;
					logger.log(Level.INFO, logMsg);
				}
			}
		}

	}

	/**
	 * Generate hash
	 * 
	 * @param nonce
	 *            the nonce to be hashed with the password
	 * @param pass
	 *            the password to be hashed with the nonce
	 * @return the hash of the password + nonce
	 * @throws UnsupportedEncodingException
	 *             if invalid nonce or password
	 */
	private static String getInstaYakChalHash(String nonce, String pass) throws UnsupportedEncodingException {
		nonce = nonce + pass;
		nonce = ComputeHash.computeHash(nonce);
		return nonce;

	}

	/**
	 * makes the slmd directory for the .slmd user files
	 * 
	 * @return the directory
	 */
	private static File makeSLMDdirectory() {
		File newDir = new File(DIR_NAME);
		if (!newDir.exists()) {
			newDir.mkdirs();
		}
		return newDir;
	}

	/**
	 * Checks if the user is valid and gets the password
	 * 
	 * @param pFile
	 *            the password file
	 * @param userID
	 *            the ID to be found
	 * @param logger
	 *            the logger to log all information to
	 * @param clntSock
	 *            the socket connected to the client
	 * @param out
	 *            writes serialized messages to the client
	 * @return the password or null if the user does not exist
	 * @throws IOException
	 *             if file io problems
	 * @throws InstaYakException
	 *             if errors InstaYak class errors occur
	 */
	private static String getUserIDandPassword(File pFile, String userID, Logger logger, Socket clntSock,
			MessageOutput out) throws IOException, InstaYakException {
		List<String> IDlist = Files.readAllLines(pFile.toPath(), encoding);
		String idPass = null;
		String pattern = userID + PASS_PATTERN;
		for (String temp : IDlist) {
			if (temp.matches(pattern)) {
				idPass = temp;
			}
		}
		if (idPass == null) {
			String errMsg = "No such user " + userID;
			writeInstaYakError(errMsg, clntSock, out);
			logger.log(Level.WARNING, errMsg);
			return null;
		}
		return parsePassword(idPass);
	}

	/**
	 * Parses the password from the given string
	 * 
	 * @param idPass
	 * @return
	 */
	private static String parsePassword(String idPass) {
		int ndx = idPass.indexOf(COLON);
		return idPass.substring(ndx + 1, idPass.length());
	}

	/**
	 * Generate a random number
	 * 
	 * @param rand
	 *            the Random objects
	 * @return the random number generated
	 */
	private static int getRandomNum(Random rand) {
		int r = rand.nextInt(1000000);
		int range = 10000000 - 1000000 + 1;
		r = (r % range) * 21;
		return r;
	}

}
