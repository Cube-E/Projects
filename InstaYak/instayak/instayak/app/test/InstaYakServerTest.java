package instayak.app.test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

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

public class InstaYakServerTest {
	// Constant string
	private static final String SPACE = " ";
	// Constant for communication error
	private static final String UNABLE_TO_COM = "Unable to communicate: ";
	// Constant for invalid message error
	private static final String INV_MSG = "Invalid message: ";
	// Constant for unexpected message error
	private static final String UNEX_MSG = "Unexpected message: ";
	// Constant for Error message error
	private static final String ERROR = "ERROR: ";
	// Constant for Validation message error
	private static final String VAL_FAIL = "Validation failed: ";
	// Constant for UOn or SLMD output choice
	private static final String UON_OR_SLMD = "[UOn, SLMD]> ";
	// Constant for UOn input
	private static final String UON = "uon";
	// Constant for slmd input
	private static final String SLMD = "slmd";
	// Constant to prompt user for category
	private static final String CATEGORY = "Category> ";
	// Constant to prompt user for image
	private static final String IMG_FNAME = "Image Filename> ";
	// Constant to prompt user to continue image
	private static final String CONTINUE = "Continue (Y/N)> ";
	// Constant for parameters error message
	private static final String PARAM = "Parameter(s): <Server><Port><UserID><Password>";
	// Constant for yes validation
	private static final String YES = "yes";
	// Constant #2 for yes validation
	private static final String Y = "y";
	// Constant for no validation
	private static final String NO = "no";
	// Constant #2 for no validation
	private static final String N = "n";

	private static int num = 0;
	/**
	 * The client of InstaYak
	 * 
	 * @param args
	 *            4 arguments passed to the client
	 */
	public static void main(String[] args) {
		// Test for the correct number of arguments.
		if ((args.length != 4)) {
			System.err.println(UNABLE_TO_COM + PARAM);
			System.exit(-1);
		}
		// Server name or IP Address.
		String server = args[0];
		// Get the port number. Default port is 7.
		int servPort = Integer.parseInt(args[1]);
		// Create a socket that is connected to the server on the specified
		// port.
		try {
			Socket socket = new Socket(server, servPort);
			InputStream i = socket.getInputStream();
			OutputStream o = socket.getOutputStream();
			MessageInput in = new MessageInput(i);
			MessageOutput out = new MessageOutput(o);
			// Read in InstaYakVersion
			InstaYakMessage msg = InstaYakMessage.decode(in);
			readInstaYakVersion(msg, in);
			// Write out InstaYakID
			InstaYakID id = new InstaYakID("notme");
			id.encode(out);
			writeInstaYakID(args[2], out);
			// Read in the challenge
			msg = InstaYakMessage.decode(in);
			String nonce = readInstaYakChallenge(msg, in);
			// Write out InstaYakCredentials

			writeInstaYakCredentials(args[3], nonce, out);
			// Read in ACK
			System.out.println(ackFeedBack(msg, in));
			// loop for UOn and SLMD
			uonORslmdLoop(in, out, msg);
			// Close connection
			socket.close();
		} catch (SecurityException | NullPointerException | IllegalArgumentException | IOException e) {
			System.err.println(UNABLE_TO_COM + e.getMessage());
		} catch (InstaYakException e) {
			// Invalid Message failure
			System.err.println(INV_MSG + e.getMessage());
		}

	}

	/**
	 * Read an 
	 * @param msg
	 *            the InstaYakMessage
	 * @param in
	 *            the MessageInput
	 * @throws InstaYakException
	 *             if an error occurs
	 * @throws IOException
	 *             if an IO error occurs
	 */
	private static void readInstaYakVersion(InstaYakMessage msg, MessageInput in)
			throws InstaYakException, IOException {
		while (!(msg instanceof InstaYakVersion)) {
			if (msg instanceof InstaYakError) {
				// Error failure
				System.err.println(ERROR + getInstaYakErrorMessage(msg));
				System.exit(-1);
			}
			// Unexpected message failure
			System.err.println(UNEX_MSG + msg.toString());
			msg = InstaYakMessage.decode(in);
		}
		InstaYakVersion version = (InstaYakVersion) msg;
		System.out.println(version.getOperation() + SPACE + version.getVersion());
	}

	/**
	 * 
	 * @param msg the InstaYakMessage
	 * @param in the MessageInput to read from
	 * @return String representing the nonce
	 * @throws InstaYakException if error occurs
	 * @throws IOException if io error
	 */
	private static String readInstaYakChallenge(InstaYakMessage msg, MessageInput in)
			throws InstaYakException, IOException {
		while (!(msg instanceof InstaYakChallenge)) {
			if (msg instanceof InstaYakError) {
				// Error failure
				System.err.println(ERROR + getInstaYakErrorMessage(msg));
				System.exit(-1);
			}
			// Unexpected message failure
			System.err.println(UNEX_MSG + msg.getOperation());
			msg = InstaYakMessage.decode(in);
		}
		InstaYakChallenge clng = (InstaYakChallenge) msg;
		String nonce = clng.getNonce();
		System.out.println(clng.getOperation() + SPACE + nonce);
		return nonce;
	}

	/**
	 * 
	 * @param arg
	 *            the ID to be passed
	 * @param out
	 *            the outputstream to be written to
	 * @throws IOException
	 *             if io errors occur
	 */
	private static void writeInstaYakID(String arg, MessageOutput out) throws IOException {
		try {
			InstaYakID id = new InstaYakID(arg);
			id.encode(out);
		} catch (InstaYakException e) {
			// validation failure
			System.err.println(e.getMessage());
			System.exit(-1);
		}
	}

	/**
	 * 
	 * @param arg
	 *            the password
	 * @param nonce
	 *            the nonce to be appended with the password
	 * @param out
	 *            the outputsteam to be written to
	 * @throws IOException
	 *             if io errors occur
	 */
	private static void writeInstaYakCredentials(String arg, String nonce, MessageOutput out) throws IOException {
		try {
			nonce = nonce + arg;
			nonce = ComputeHash.computeHash(nonce);
			InstaYakCredentials cred = new InstaYakCredentials(nonce);
			cred.encode(out);
		} catch (InstaYakException e) {
			System.err.println(VAL_FAIL + e.getMessage());
		}
	}

	/**
	 * 
	 * @param in
	 *            the inputstream to be read from
	 * @param out
	 *            the outputstream to be read from
	 * @param msg
	 *            the InstaYakMessage to decode from
	 * @throws IOException
	 *             if io errors occur
	 * @throws InstaYakException
	 *             if an error occurs
	 */
	private static void uonORslmdLoop(MessageInput in, MessageOutput out, InstaYakMessage msg)
			throws IOException, InstaYakException {
		Scanner sin = new Scanner(System.in);
		String choice = null;
		String yesNo = null;
		boolean Continue = true;
		while (Continue) {
			boolean validAnswer = false;
			while (!validAnswer) {
				// Ask for UOn or SLMD
				System.out.println(UON_OR_SLMD);
				choice = sin.nextLine();
				choice = choice.toLowerCase();
				if (choice.equals(UON)) {
					boolean doU = false;
					while (!doU) {
						try {
							sendUOn(out, sin);
							doU = true;
						} catch (InstaYakException e) {
							System.err.println(e.getMessage());
						}
					}
					System.out.println(ackFeedBack(msg, in));
					validAnswer = true;
				} else if (choice.equals(SLMD)) {
					sendSLMD(out);
					System.out.println(ackFeedBack(msg, in));
					validAnswer = true;
				} else {
					System.err.println("Invalid choice");
				}
			}
			// Ask to continue
			validAnswer = false;
			while (!validAnswer) {
				System.out.println(CONTINUE);
				yesNo = sin.nextLine();
				yesNo = yesNo.toLowerCase();
				if (YES.equals(yesNo) || Y.equals(yesNo)) {
					Continue = true;
					validAnswer = true;
				} else if (NO.equals(yesNo) || N.equals(yesNo)) {
					Continue = false;
					validAnswer = true;
				}
			}

		}
		sin.close();
	}

	/**
	 * Sends a SLMD message
	 * 
	 * @param out
	 *            the MessageOutput to write to the server
	 * @throws IOException
	 *             if IO problems
	 */
	private static void sendSLMD(MessageOutput out) throws IOException {
		InstaYakSLMD slmd = new InstaYakSLMD();
		slmd.encode(out);
	}

	/**
	 * Sends a UOn message
	 * 
	 * @param out
	 *            the MessageOutput to write to the server
	 * @param sin
	 *            the scanner to read user input
	 * @throws IOException
	 *             if IO problems
	 * @throws InstaYakException
	 *             if validation problems
	 */
	private static void sendUOn(MessageOutput out, Scanner sin) throws IOException, InstaYakException {
		String category;
		String path;
		// Ask for the category and image
		System.out.println(CATEGORY);
		category = sin.nextLine();
		System.out.println(IMG_FNAME);
		path = sin.nextLine();
		if (path == null || path.isEmpty()) {
			path = "null";
		}
		// Get the image
		File file = new File(path);
		while (!file.exists()) {
			System.err.println("Invalid image");
			System.out.println(IMG_FNAME);
			path = sin.nextLine();
			file = new File(path);
		}
		byte[] img = Files.readAllBytes(file.toPath());
		InstaYakUOn uon = new InstaYakUOn(category, img);
		uon.encode(out);

	}

	/**
	 * Waits for an InstaYakACK message
	 * 
	 * @param msg
	 *            used for decoding the messages from the server
	 * @param in
	 *            the MessagInput stream the server writes to
	 * @return String of the ack message
	 * @throws InstaYakException
	 *             if validation errors
	 * @throws IOException
	 *             if IOProblems
	 */
	private static String ackFeedBack(InstaYakMessage msg, MessageInput in) throws InstaYakException, IOException {
		msg = InstaYakMessage.decode(in);
		while (!(msg instanceof InstaYakACK)) {
			if (msg instanceof InstaYakError) {
				// Error failure
				System.err.println(ERROR + getInstaYakErrorMessage(msg));
				System.exit(-1);
			}
			// Unexpected message failure
			System.err.println(UNEX_MSG + msg.getOperation());
			msg = InstaYakMessage.decode(in);
		}
		InstaYakACK ack = (InstaYakACK) msg;
		return ack.getOperation();
	}

	/**
	 * Gets the error message from the InstaYakError
	 * 
	 * @param msg
	 *            the InstaYakMessage
	 * @return String of the InstaYakMessage message
	 */
	private static String getInstaYakErrorMessage(InstaYakMessage msg) {
		InstaYakError error = (InstaYakError) msg;
		return error.getMessage();
	}
}
