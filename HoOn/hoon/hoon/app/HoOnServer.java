/************************************************
 *
 * Author:      Quoc-Bao Huynh
 * Assignment:  Program 6
 * Class:       CSI 4321
 *
 ************************************************/
package hoon.app;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import hoon.serialization.ErrorCode;
import hoon.serialization.HoOnException;
import hoon.serialization.HoOnQuery;
import hoon.serialization.HoOnResponse;
import twitter4j.Paging;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;

/*
 * HoOnResponse
 *
 * 1.0
 *
 * April 4, 2017
 *
 * Copyright
 */
public class HoOnServer {
	// The max size receivable query length packet
	public static final int MAXQLENGTH = 8;
	// The max size receiveable udp length packet.
	private static final int MAXUDPLENGTH = 65507;
	// The queryId for HoOnQuery
	private static long queryId;
	// The default page length
	private static int defaultPageLength = 20;
	// The logger
	public static Logger logger;
	// The connections logger file name
	public static String CONNECTION_FILE = "connections";
	// The encoding of the handler
	private static String encoding = ("US-ASCII");
	// The error queryid
	private static Long errorQId = 0L;

	public static void main(String args[]) {
		// The only parameter is the server port
		if (args.length != 1) {
			System.err.println("Parameter(s): <Server Port>");
			System.exit(-1);
		}
		try {
			// Get the server port number
			int servPort = Integer.parseInt(args[0]);
			// Make the connection
			DatagramSocket socket = new DatagramSocket(servPort);
			// Make the receiving packet
			byte[] qArr = new byte[MAXQLENGTH];
			DatagramPacket qPacket = new DatagramPacket(qArr, MAXQLENGTH);
			boolean running = true;
			logger = Logger.getLogger(CONNECTION_FILE);
			FileHandler handler = new FileHandler("connections.log");
			handler.setEncoding(encoding);
			logger.addHandler(handler);
			handler.setFormatter(new SimpleFormatter());
			while (running) {
				try {
					socket.receive(qPacket);
					String adrPor = "Address: " + qPacket.getAddress().toString() + " Port: " + qPacket.getPort();
					logger.log(Level.INFO, adrPor);
					// get the byte array from the packet. Put it into a
					// HoOnQuery
					byte[] temp = Arrays.copyOfRange(qPacket.getData(), 0, qPacket.getLength());
					// if making a query throws a HoOnException make a packet
					// and send an response with the errorcode filled
					HoOnQuery query = new HoOnQuery(temp);
					queryId = query.getQueryId();
					// Valid query, so get the twitter posts
					Twitter twitter = TwitterFactory.getSingleton();
					List<Status> twitterList = getTwitterList(query, twitter);
					List<String> postList = new ArrayList<String>(0);
					// Got twitter list correctly. Get the posts
					if (query.getRequestedPosts() < twitterList.size()) {
						getTwitterList(query, twitterList, postList);
					} else {
						getTwitterList(query, twitterList, postList);
					}
					// make a response
					HoOnResponse response = new HoOnResponse(ErrorCode.NOERROR, query.getQueryId(), postList);
					// valid response so send the response to client
					byte[] rArr = response.encode();
					DatagramPacket rPacket = new DatagramPacket(rArr, rArr.length, qPacket.getAddress(),
							qPacket.getPort());
					socket.send(rPacket);

				} catch (IOException e) {
					logger.log(Level.WARNING, "Socket I/O problems: " + e.getMessage());
				} catch (HoOnException e) {
					logger.log(Level.WARNING, e.getErrorCode().getErrorMessage());
					sendErrorPacket(e.getErrorCode(), errorQId, qPacket, socket);
				} catch (TwitterException e) {
					logger.log(Level.WARNING, ErrorCode.NETWORKERROR.getErrorMessage());
					sendErrorPacket(ErrorCode.NETWORKERROR, queryId, qPacket, socket);
				}
			}
		} catch (NumberFormatException e) {
			logger.log(Level.WARNING,
					"During Startup, The port number given is not a parsable integer: " + e.getMessage());
			System.exit(-1);
		} catch (SocketException e) {
			logger.log(Level.WARNING, "During Startup, Socket could not be opened: " + e.getMessage());
			System.exit(-1);
		} catch (SecurityException e1) {
			logger.log(Level.WARNING, "During Startup, Error handling logger: " + e1.getMessage());
		} catch (IOException e1) {
			logger.log(Level.WARNING, "During Startup, Error opening logger file: " + e1.getMessage());
		}
	}

	/**
	 * getTwitterList
	 * 
	 * @param query
	 *            The HoOnQuery to get the queryId
	 * @param twitter
	 *            the Twitter instance
	 * @return the list of Statuses
	 * @throws TwitterException
	 *             if Twitter problems
	 */
	public static List<Status> getTwitterList(HoOnQuery query, Twitter twitter) throws TwitterException {
		List<Status> twitterList;
		if (query.getRequestedPosts() > defaultPageLength) {
			Paging p = new Paging();
			p.setCount(query.getRequestedPosts());
			twitterList = twitter.getUserTimeline(p);
		} else {
			twitterList = twitter.getUserTimeline();
		}
		return twitterList;
	}

	/**
	 * getTwitterList
	 * 
	 * @param query
	 *            the HoOnQuery
	 * @param twitterList
	 *            the list of twitter Statuses
	 * @param postList
	 *            the list of posts to append to
	 * @return a list of strings containing the posts
	 */
	public static List<String> getTwitterList(HoOnQuery query, List<Status> twitterList, List<String> postList) {
		int size = getSize(query.getRequestedPosts(), twitterList);
		for (int i = 0; i < size; i++) {
			postList.add(twitterList.get(i).getUser().getName() + ": " + twitterList.get(i).getText());
		}
		return postList;
	}

	/**
	 * sendErrorPacket
	 * 
	 * @param errorCode
	 *            the errorcode to send
	 * @param qId
	 *            the queryId to send
	 * @param qPacket
	 *            the packet containing the address and port to send to
	 * @param socket
	 *            the socket to send from
	 */
	public static void sendErrorPacket(ErrorCode errorCode, Long qId, DatagramPacket qPacket, DatagramSocket socket) {
		// Start a list of size zero
		List<String> posts = new ArrayList<String>(0);
		try {
			HoOnResponse response = new HoOnResponse(errorCode, qId, posts);
			byte[] err = response.encode();
			DatagramPacket rPacket = new DatagramPacket(err, err.length, qPacket.getAddress(), qPacket.getPort());
			socket.send(rPacket);
		} catch (IllegalArgumentException | HoOnException e1) {
			logger.log(Level.WARNING, "Error while sending the Error Packet " + e1.getMessage());
		} catch (IOException e) {
			logger.log(Level.WARNING, "IO error while sending the Error Packet " + e.getMessage());
		}
	}

	/**
	 * getSize
	 * 
	 * @param listSize
	 *            the final size of the list to send
	 * @param twitterList
	 *            the twitter list of statuses
	 * @return the size of a list that can fit into a UDP packet
	 */
	public static int getSize(int listSize, List<Status> twitterList) {
		boolean goodSize = false;
		int size = listSize;
		// check the size it will be
		while (!goodSize) {
			int totalSize = 0;
			for (int i = 0; i < size; i++) {
				totalSize += twitterList.get(i).getUser().getName().length();
				totalSize += twitterList.get(i).getText().length();
				// Two extra for characters ':' and ' '
				totalSize += 2;
			}
			if (totalSize <= MAXUDPLENGTH) {
				goodSize = true;
			} else {
				size--;
			}
		}
		return size;
	}
}
