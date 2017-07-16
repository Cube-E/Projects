/************************************************
 *
 * Author:      Quoc-Bao Huynh
 * Assignment:  Program 5
 * Class:       CSI 4321
 *
 ************************************************/
package hoon.app;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Random;

import hoon.serialization.HoOnException;
import hoon.serialization.HoOnQuery;
import hoon.serialization.HoOnResponse;

/*
 * HoOnClient
 *
 * 1.0
 *
 * March 29, 2017
 *
 * Copyright
 */
public class HoOnClient {

	// The wait time for a resonse in milliseconds
	private static final int TIMEOUT = 5000;
	// The max size receiveable udp length packet.
	private static final int MAXUDPLENGTH = 65536;
	// The max amount of tries to send a query
	private static final int MAXTRIES = 10;

	public static void main(String args[]) {
		if (args.length != 3) { // Test for correct argument list
			System.err.println("Parameter(s): <Server IP/Name><Server port><Number of responses desired");
			System.exit(-1);
		}
		try {
			// get the address of the IP passed in
			InetAddress servAddr = InetAddress.getByName(args[0]);
			// get the port number
			int servPort = Integer.parseInt(args[1]);
			// create a new Datagram socket
			DatagramSocket socket = new DatagramSocket();
			// set timeout
			socket.setSoTimeout(TIMEOUT);
			try {
				Random rand = new Random(System.currentTimeMillis());
				// Generate queryID
				int queryId = genQueryId(rand);
				// Make query package
				HoOnQuery query = new HoOnQuery((long) queryId, Integer.parseInt(args[2]));
				byte[] message = query.encode();
				DatagramPacket sendPacket = new DatagramPacket(message, message.length, servAddr, servPort);
				// Make response package
				byte[] answer = new byte[MAXUDPLENGTH];
				DatagramPacket rcvdPacket = new DatagramPacket(answer, MAXUDPLENGTH);
				// wait to receive packet
				waitForResponse(socket, sendPacket, rcvdPacket, servAddr, queryId);

			} catch (NumberFormatException e) {
				System.err.println("The post number given is not a parsable integer");
				System.exit(-1);
			} catch (HoOnException e) {
				System.err.println(e.getErrorCode().getErrorMessage());
				System.exit(-1);
			} catch (IOException e) {
				System.err.println(e.getMessage());
				System.exit(-1);
			} catch(IllegalArgumentException e){
				System.err.println(e.getMessage());
			}
		} catch (UnknownHostException | SecurityException e) {
			System.err.println("Cannot resolve server name");
			System.exit(-1);
		} catch (NumberFormatException e) {
			System.err.println("Port value received is not a parsable int ");
			System.exit(-1);
		} catch (SocketException e) {
			System.err.println("Socket could not be opened");
			System.exit(-1);
		}

	}

	/**
	 * waits and starts whole new sending process if queryIds do not match
	 * 
	 * @param socket
	 *            the connected socket
	 * @param sendPacket
	 *            the packet to send
	 * @param rcvdPacket
	 *            the packet that will hold what is received
	 * @param servAddr
	 *            the server address
	 * @param queryId
	 *            the client generated queryId
	 * @throws IOException
	 *             if IO errors
	 */
	private static void waitForResponse(DatagramSocket socket, DatagramPacket sendPacket, DatagramPacket rcvdPacket,
			InetAddress servAddr, int queryId) throws IOException {
		boolean flag = false;
		while (!flag) {
			if (getResponse(socket, sendPacket, rcvdPacket, servAddr)) {
				try {
					// check queryId
					byte[] temp = Arrays.copyOfRange(rcvdPacket.getData(), 0, rcvdPacket.getLength());
					HoOnResponse response = new HoOnResponse(temp);
					if (response.getQueryId() == queryId && response.getErrorCode().getErrorCodeValue() == 0) {
						System.out.println(printResponse(response));
						flag = true;
					}
					else if(response.getErrorCode().getErrorCodeValue() != 0){
						System.out.println(printResponse(response));
						flag = true;
					}
				} catch (HoOnException e) {
					System.out.println(e.getErrorCode().getErrorMessage());
				}
			} else {
				System.err.println("Max amount of resend tries reached");
				System.exit(-1);
			}

		}
	}

	/**
	 * print the response received
	 * 
	 * @param rcvdPacket
	 *            the received packet to print
	 * @return the string to be printed
	 * @throws HoOnException
	 *             If an error has occurred
	 */
	private static String printResponse(HoOnResponse response) throws HoOnException {
		if (response.getErrorCode().getErrorCodeValue() == 0) {
			return response.toString();
		} else {
			return response.getErrorCode().getErrorMessage();
		}
	}

	/**
	 * send the packet and get the response back
	 * 
	 * @param socket
	 *            the connected socket
	 * @param sendPacket
	 *            the packet to send
	 * @param rcvdPacket
	 *            the packet that will hold what is received
	 * @param servAddr
	 *            the server address
	 * @return a boolean whether a successful response was received
	 * @throws IOException
	 *             if IO problems
	 */
	private static boolean getResponse(DatagramSocket socket, DatagramPacket sendPacket, DatagramPacket rcvdPacket,
			InetAddress servAddr) throws IOException {
		boolean received = false;
		int tries = 0;
		do {
			socket.send(sendPacket);
			try {
				socket.receive(rcvdPacket);
				if (rcvdPacket.getAddress().equals(servAddr)) {
					received = true;
				}
				tries++;
			} catch (SocketTimeoutException e) {
				received = false;
				tries++;
			} catch (Exception e) {
				System.err.println("An error occurred while sending/receiving the message" + e.getMessage());
				System.exit(-1);
			}
		} while (!received && tries < MAXTRIES);
		return received;
	}

	/**
	 * Generate a queryId number
	 * 
	 * @param rand
	 *            the Random objects
	 * @return the random number generated
	 */
	private static int genQueryId(Random rand) {
		int r = rand.nextInt(1000000);
		int range = 10000000 - 1000000 + 1;
		r = (r % range) * 21;
		return r;
	}
}
