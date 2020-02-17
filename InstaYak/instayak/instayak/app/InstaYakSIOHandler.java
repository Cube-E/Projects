package instayak.app;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
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

public class InstaYakSIOHandler implements SIOHandler{
	// Constant for password pattern in the file
	private static final String PASS_PATTERN = ":(.*)";
	// File encoding type
	private static Charset encoding = Charset.forName("ISO-8859-1");
	// Constant for ID message type
	private static final String MSG_ID = "id";
	// Constant for CRED message type
	private static final String MSG_CRED = "cred";
	// Constant for UON/SLMD message type
	private static final String MSG_UON_SLMD = "uonSLMD";
	// Constant for unexpected message error
	private static final String UN_MSG = "Unexpected message: ";
	// Constant for received error
	private static final String RCVD_MSG = "Received error: ";
	// Constant for communication error
	private static final String UN_TO_COM = "Unable to communicate: ";
	// Holds the password
	private String password = null;
	// Holds the userID
	private String userId = null;
	// Hold the server hash
	private String serverHash = null;
	// flag to check if the ID has been obtained
	boolean hasId = false;
	// flag to check if the credentials have been obtained
	boolean hasCred = false;
	// Constant for space
	private static final String SPACE = " ";
	// Constant for colon
	private static final String COLON = ":";
	// Constant for backslash
	private static final String BACK_N = "\n";
	ByteArrayInputStream bin;
	// Constant for jpeg extension
	private static final String IMG_EXT = ".jpg";
	// Constant for user slmd file extension
	private static final String FILE_EXT = ".SLMD";
	// Number that all slmd messages start at.
	private static int startNum = 0;
	// Constant for slmd directory
	private static final String DIR_NAME = "userSLMDs";
	// The buffer to hold the bytes as they are read in
	private ByteArrayOutputStream buffer = new ByteArrayOutputStream();
	boolean hasBackR = false;
	/**
	 * Default buffer size
	 */
	private static final int BUFSIZE = 655353;
	/**
	 * Buffer to read into
	 */
	private ByteBuffer readBuffer = ByteBuffer.allocateDirect(BUFSIZE);
	/**
	 * Local byte buffer for processing
	 */
	private final byte[] localBuffer = new byte[BUFSIZE];

	private final byte[] readbuff = new byte[BUFSIZE];
	private final byte[] writebuff = new byte[BUFSIZE];


	public void handleRead(SelectionKey key) throws IOException {
		// Client socket channel has pending data
		SocketChannel clntChan = (SocketChannel) key.channel();
		ByteBuffer buf = (ByteBuffer) key.attachment();
		long bytesRead = clntChan.read(buf);
		if (bytesRead == -1) { // Did the other end close?
			clntChan.close();
		} else if (bytesRead > 0) {
			// Indicate via key that reading/writing are both of interest now.
			key.interestOps(SelectionKey.OP_READ | SelectionKey.OP_WRITE);
		}
	}
	
	/**
	 * Write a version message
	 * 
	 * @return the version message
	 * @throws IOException
	 *             if IO errors
	 */
	private static byte[] writeInstaYakVersion() throws IOException {
		ByteArrayOutputStream b = new ByteArrayOutputStream();
		MessageOutput out = new MessageOutput(b);
		InstaYakVersion ver = new InstaYakVersion();
		ver.encode(out);
		return b.toByteArray();

	}

	@Override
	public void handleRead(SelectionKey key , Logger logger, File pFile) throws IllegalArgumentException, IllegalStateException,IOException {
		SocketChannel clntChan = (SocketChannel) key.channel();
		ByteBuffer buf = (ByteBuffer) key.attachment();
		long bytesRead = clntChan.read(buf);
		if (bytesRead == -1) { // Did the other end close?
			clntChan.close();

		} else if (bytesRead > 0) {
			ByteBuffer bbuf = ByteBuffer.allocate(this.BUFSIZE);
			bbuf.put(pass(buf.array(), logger, clntChan, pFile));
			key.attach(bbuf);
			key.interestOps(SelectionKey.OP_READ | SelectionKey.OP_WRITE);
		}
	}

	@Override
	public void handleAccept(SelectionKey key) throws IOException {
		SocketChannel clntChan = ((ServerSocketChannel) key.channel()).accept();
		clntChan.configureBlocking(false);
		clntChan.register(key.selector(), SelectionKey.OP_READ, ByteBuffer.allocate(BUFSIZE));
		clntChan.write(ByteBuffer.wrap(writeInstaYakVersion()));
		
	}
	
	
	public byte[] pass(byte[] readBuff, Logger logger,  SocketChannel clntChan,  File pFile)
			throws IllegalArgumentException, IllegalStateException {
		// Simply repeat read bytes
		if (!getAllBytes(readBuff)) {
			return null;
		}

		bin = new ByteArrayInputStream(buffer.toByteArray());
		buffer = new ByteArrayOutputStream();
		MessageInput in = new MessageInput(bin);
		try {
			InstaYakMessage msg;

			msg = InstaYakMessage.decode(in);

			// Check for ID && get password && send a challenge
			if (!hasId && !checkMsgInstance(msg, MSG_ID, logger)) {
				throw new IllegalStateException();
			} else if (!hasId && checkMsgInstance(msg, MSG_ID, logger)) {
				// The first message is an id message
				userId = readInstaYakID(msg, logger, in, clntChan);
				// Check if the user exists within the password file
				if (!checkUser(pFile, userId, logger)) {
					String errMsg = "No such user " + userId;
					logger.log(Level.WARNING, errMsg);
					throw new IllegalArgumentException(errMsg);
				} else {
					// else the user exists
					password = parsePassword(pFile, userId);
					// generate a challenge
					Random rand = new Random(System.currentTimeMillis());
					String nonce = Integer.toString(getRandomNum(rand));
					serverHash = getInstaYakChalHash(nonce, password);
					hasId = true;
					return writeInstaYakChallenge(nonce);
				}
			}

			if (hasId && !hasCred && !checkMsgInstance(msg, MSG_CRED, logger)) {
				throw new IllegalStateException();
			}
			if (!hasCred && checkMsgInstance(msg, MSG_CRED, logger)) {
				if (!readInstaYakCred(msg, logger, serverHash, userId)) {
					String errMsg = "Unable to authenticate " + userId;
					logger.log(Level.WARNING, errMsg);
					throw new IllegalArgumentException(errMsg);
				} else {
					hasCred = true;
					return writeInstaYakACK();
				}
			}
			if (hasId && hasCred && checkMsgInstance(msg, MSG_UON_SLMD, logger)) {
				if (!sendUOnSLMD(msg, logger, userId, clntChan)) {
					return null;
				}
				return writeInstaYakACK();
			}
			logger.log(Level.WARNING, UN_MSG + msg.getOperation());
		} catch (InstaYakException e) {
			logger.log(Level.WARNING, "Invalid message: " + e.getMessage() + e.getClass());
		} catch (IOException e) {
			logger.log(Level.WARNING, UN_TO_COM + e.getMessage());

		}
		return null;
	}
	
	/**
	 * buffers all bytes until the frame is reached
	 * 
	 * @param byteBuffer
	 *            the byte array to check
	 * @return true if the end, false otherwise.
	 */
	public boolean getAllBytes(byte[] byteBuffer) {

		buffer.write(byteBuffer, 0, byteBuffer.length);
		if(byteBuffer.length <2){
			return false;
		}
		if ((char) byteBuffer[byteBuffer.length - 2] == '\r' && (char) byteBuffer[byteBuffer.length - 1] == '\n') {
			return true;
		} else {
			return false;
		}

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
	private static boolean sendUOnSLMD(InstaYakMessage msg, Logger logger, String userID,
			SocketChannel clntSock) throws IOException, SocketException {
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
				return false;
			}
			String logMsg = clntSock.getLocalAddress() + SPACE + userID + COLON + SPACE + uon.getOperation() + BACK_N;
			logger.log(Level.INFO, logMsg);
			return true;
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
					String logMsg = clntSock.getLocalAddress() + SPACE + userID + COLON + SPACE + slmdMsg + BACK_N;
					logger.log(Level.INFO, logMsg);

				}
			}
			return true;
		}
		return true;

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
	 * Send an InstaYakACK
	 * 
	 * @return a byte array with the message
	 * @throws IOException
	 *             if IO errors
	 */
	private static byte[] writeInstaYakACK() throws IOException {
		ByteArrayOutputStream b = new ByteArrayOutputStream();
		MessageOutput out = new MessageOutput(b);
		InstaYakACK ack = new InstaYakACK();
		ack.encode(out);
		return b.toByteArray();
	}

	/**
	 * Read in an InstaYakCredentials
	 * 
	 * @param msg
	 *            the InstaYakMessage credentials message
	 * @param logger
	 *            the logger to log all information to
	 * @param serverHash
	 *            the hash computed by the server
	 * @param userID
	 *            the client identification
	 * @return a boolean if the server credentials matches the client
	 *         credentials
	 * @throws InstaYakException
	 *             if invalid InstaYak messages
	 * @throws IOException
	 *             if stream is unable to write
	 */
	private static boolean readInstaYakCred(InstaYakMessage msg, Logger logger, String serverHash, String userID)
			throws IOException, InstaYakException {

		InstaYakCredentials cred = (InstaYakCredentials) msg;
		if (!serverHash.equals(cred.getHash())) {
			return false;
		}
		// writeInstaYakACK(out);
		return true;

	}

	/**
	 * Send over an InstaYakChallenge
	 * 
	 * @param nonce
	 *            the of the InstaYakChallenge object
	 * @return a byte array containing the message
	 * @throws IOException
	 *             if IO errors
	 * @throws InstaYakException
	 *             if serialization errors
	 */
	private static byte[] writeInstaYakChallenge(String nonce) throws IOException, InstaYakException {
		ByteArrayOutputStream b = new ByteArrayOutputStream();
		MessageOutput out = new MessageOutput(b);
		InstaYakChallenge chal = new InstaYakChallenge(nonce);
		chal.encode(out);
		return b.toByteArray();
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
	 * Checks if the user is valid and gets the password
	 * 
	 * @param pFile
	 *            the password file
	 * @param userID
	 *            the ID to be found
	 * @param logger
	 *            the logger to log all information to
	 * @return the password or null if the user does not exist
	 * @throws IOException
	 *             if file io problems
	 * @throws InstaYakException
	 *             if errors InstaYak class errors occur
	 */
	private static boolean checkUser(File pFile, String userID, Logger logger) throws IOException, InstaYakException {
		List<String> IDlist = Files.readAllLines(pFile.toPath(), encoding);
		String idPass = null;
		String pattern = userID + PASS_PATTERN;
		for (String temp : IDlist) {
			if (temp.matches(pattern)) {
				idPass = temp;
			}
		}
		if (idPass == null) {
			return false;
		}
		return true;
	}

	/**
	 * Parses the password from the given string
	 * 
	 * @param pFile
	 *            the password file
	 * @param userID
	 *            the user id
	 * @return the password found
	 * @throws IOException
	 *             if IO error occurs
	 */
	private static String parsePassword(File pFile, String userID) throws IOException {
		List<String> IDlist = Files.readAllLines(pFile.toPath(), encoding);
		String idPass = null;
		String pattern = userID + PASS_PATTERN;
		for (String temp : IDlist) {
			if (temp.matches(pattern)) {
				idPass = temp;
			}
		}
		int ndx = idPass.indexOf(COLON);
		return idPass.substring(ndx + 1, idPass.length());
	}

	/**
	 * Reads the InstaYakID from the client
	 * 
	 * @param msg
	 *            the InstaYakMessage
	 * @param logger
	 *            the logger
	 * @param in
	 *            the MessageInput
	 * @param clntChan
	 *            the client channel
	 * @return the user ID
	 * @throws InstaYakException
	 *             if serialization errors
	 * @throws IOException
	 *             if IO errors
	 */
	private static String readInstaYakID(InstaYakMessage msg, Logger logger, MessageInput in,
			final SocketChannel clntChan) throws InstaYakException, IOException {

		InstaYakID id = (InstaYakID) msg;
		String logMsg = clntChan.getLocalAddress()
				+ /* SPACE + clntChan.getport+ */SPACE + id.getID() + COLON + SPACE + id.getOperation() + SPACE
				+ id.getID() + BACK_N;
		logger.log(Level.INFO, logMsg);
		return id.getID();

	}

	/**
	 * Handle accept of new connection and return message to write. By default
	 * this is a no-op that writes nothing.
	 * 
	 * @return message to write (null if none)
	 */
	public byte[] handleAccept() {
		try {
			return writeInstaYakVersion();
		} catch (IOException e) {
			System.err.println("Error in handleAccept");
		}
		return null;

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

	@Override
	public void handleWrite(SelectionKey key) throws IOException {
		// TODO Auto-generated method stub
		
	}

}
