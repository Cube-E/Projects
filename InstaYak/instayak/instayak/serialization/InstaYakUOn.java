/************************************************
 *
 * Author:      Quoc-Bao Huynh
 * Assignment:  Program 1
 * Class:       CSI 4321
 *
 ************************************************/
package instayak.serialization;

import java.io.IOException;
import java.util.Arrays;
import java.util.Base64;

/*
 * InstaYakUOn
 *
 * 1.0
 *
 * February 24, 2017
 *
 * Copyright
 */
public class InstaYakUOn extends InstaYakMessage {

	// The InstaYakUOn operation.
	public static final String OP = "UOn";
	// The InstaYakUOn category.
	private String categ;
	// The InstaYakUon image.
	private byte[] img;
	// The encoding
	private static String encoding = "ISO-8859-1";

	/**
	 * Constructs UOn message using set values
	 * 
	 * @param category
	 *            UOn category
	 * @param image
	 *            UOn image
	 * @throws InstaYakException
	 *             if validation fails
	 */
	public InstaYakUOn(String category, byte[] image) throws InstaYakException {
		setCategory(category);
		setImage(image);
	}

	/**
	 * Constructs new UOn message using deserialization. Only parses material
	 * specific to this message (that is not operation)
	 * 
	 * @param in
	 *            deserialization input source
	 * @throws InstaYakException
	 *             if parse or validation failure
	 * @throws IOException
	 *             if I/O problem
	 */
	public InstaYakUOn(MessageInput in) throws InstaYakException, IOException {
		setCategory(in.getTokenTillSpace());
		// Read Base64 String and decode into raw bytes
		String str = in.getTokenTillNewLine();
		setImage(Base64.getDecoder().decode(str.getBytes(encoding)));
	}

	/**
	 * Returns an InstaYakUOn String representation. ("UOn: Category=Movie
	 * Image=500 bytes")
	 * 
	 * @return string representation
	 */
	public String toString() {
		return ("UOn: Category=" + categ + SPACE + "Image=" + img.length + SPACE + "bytes");
	}

	/**
	 * Gets category
	 * 
	 * @return String The InstaYakUOn category
	 */
	public String getCategory() {
		return categ;
	}

	/**
	 * Sets category
	 * 
	 * @param category
	 *            new category
	 * @throws InstaYakException
	 *             if null or invalid category
	 */
	public void setCategory(String category) throws InstaYakException {
		if (category == null || category.isEmpty() || !hasAlphNumeric(category)) {
			throw new InstaYakException("Invalid Category");
		}
		this.categ = category;
	}

	/**
	 * Returns an InstaYakUOn image
	 * 
	 * @return image an InstaYakUOn image
	 */
	public byte[] getImage() {
		return img;
	}

	/**
	 * Sets InstaYakUOn image
	 * 
	 * @param image
	 *            new image
	 * @throws InstaYakException
	 *             if null image
	 */
	public final void setImage(byte[] image) throws InstaYakException {
		if (image == null) {
			throw new InstaYakException("Null image");
		}
		img = image;
	}

	/**
	 * Returns InstaYakUOn operation "UOn:
	 * 
	 * @return String InstaYakUOn operation
	 */
	public String getOperation() {
		return OP;
	}

	/**
	 * Serializes message to given output sink
	 * 
	 * @param out
	 *            serialization output sink
	 * @throws IOException
	 *             if I/O problem
	 */
	public void encode(MessageOutput out) throws IOException {
		// Encode raw byte image into a Base64 String.
		String strImg = Base64.getEncoder().withoutPadding().encodeToString(this.img);
		// Will throw IOException if the message cannot be written.
		out.writeMessage(OP + SPACE + categ + SPACE + strImg + LINE_DELIM);
	}

	@Override
	/**
	 * Overrides equals
	 * 
	 * @param obj
	 *            an object to compare
	 * @return boolean representing whether it is equal
	 */
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		// Check if a string was passed in to compare.
		if (obj instanceof String) {
			return categ.equals(obj);
		}
		// Check if an InstaYakVersion was passed in.
		if (obj instanceof InstaYakUOn) {
			InstaYakUOn uon = (InstaYakUOn) obj;
			return categ.equals(uon.categ) && Arrays.equals(img, uon.getImage());
		}
		return false;
	}

	/**
	 * Overrides hashcode of object
	 * 
	 * @return int representing the hashcode
	 */
	@Override
	public int hashCode() {
		int result = 11;
		result = 29 * result + categ.hashCode();
		return result;
	}
}
