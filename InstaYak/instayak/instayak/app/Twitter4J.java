/************************************************
 *
 * Author:      Quoc-Bao Huynh
 * Assignment:  Program 3
 * Class:       CSI 4321
 *
 ************************************************/
package instayak.app;

import java.io.File;

import twitter4j.Status;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;

/*
 * Twitter4J
 *
 * 1.0
 *
 * February 28, 2017
 *
 * Copyright
 */
public class Twitter4J {
	// What is in the UOn each time
	private static String uonStats = ": UOn #";
	// What is in the SLMD message each time;
	private static String colonSpace = ": ";

	/**
	 * Send a uon throught Twitter
	 * 
	 * @param userID
	 *            the client identification
	 * @param message
	 *            the message desired to be sent
	 * @param fileName
	 *            the filename for the picture
	 * @throws TwitterException
	 *             if there are Twitter sending errors
	 */
	public static void sendUOn(String userID, String message, File fileName) throws TwitterException {
		// Get twitter instance using credentials stored in twitter4j.properties
		Twitter twitter = new TwitterFactory().getInstance();
		message = userID + uonStats + message;
		// Update status
		updateUOnStatus(twitter, message, fileName);
	}

	/**
	 * Send a slmd over twitter
	 * 
	 * @param userID
	 *            the client identification
	 * @param message
	 *            the message desired to be sent
	 * @throws TwitterException
	 *             if there are Twitter sending errors
	 */
	public static void sendSLMD(String userID, String message) throws TwitterException {

		// Get twitter instance using credentials stored in twitter4j.properties
		Twitter twitter = new TwitterFactory().getInstance();
		message = userID + colonSpace + message;
		// Update status
		updateSLMDStatus(twitter, message);
	}

	/**
	 * Update the Twitter status with UOn message
	 * 
	 * @param twitter
	 *            the tweet to be sent
	 * @param message
	 *            the message to be in the tweet
	 * @param fileName
	 *            the image to attach to the tweet
	 * @return the status of the update
	 * @throws TwitterException
	 *             if there are Twitter sending errors
	 */
	private static Status updateUOnStatus(final Twitter twitter, final String message, final File fileName)
			throws TwitterException {
		StatusUpdate update = new StatusUpdate(message);
		// File f = new File(filename);
		update.media(fileName);
		return twitter.updateStatus(update);
	}

	/**
	 * Update the Twitter status with SLMD message
	 * 
	 * @param twitter
	 *            the tweet to be sent
	 * @param message
	 *            the message to be in the tweet
	 * @return the status of the update
	 * @throws TwitterException
	 *             if there are Twitter sending errors
	 */
	private static Status updateSLMDStatus(final Twitter twitter, final String message) throws TwitterException {
		StatusUpdate update = new StatusUpdate(message);
		return twitter.updateStatus(update);
	}
}
