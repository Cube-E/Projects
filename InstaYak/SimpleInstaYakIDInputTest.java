package instayak.serialization.test;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.UnsupportedEncodingException;

import org.junit.Test;

import instayak.serialization.InstaYakACK;
import instayak.serialization.InstaYakException;
import instayak.serialization.InstaYakID;
import instayak.serialization.InstaYakMessage;
import instayak.serialization.MessageInput;

public class SimpleInstaYakIDInputTest {
	private static final String CHARENC = "ISO8859-1";

	@Test
	public void testSyncDecode() throws UnsupportedEncodingException, IOException, InstaYakException {
		// Decode serialized message
		try (PipedOutputStream out = new PipedOutputStream();InputStream in = new PipedInputStream(out, 10000)) {
			MessageInput min = new MessageInput(in);
			out.write("ID bob\r\n".getBytes(CHARENC));
			InstaYakID id = (InstaYakID) InstaYakMessage.decode(min);
			assertEquals("bob", id.getID());
			assertEquals("ID", id.getOperation());
			out.write("ACK\r\n".getBytes(CHARENC));
			InstaYakACK ack = (InstaYakACK) InstaYakMessage.decode(min);
			assertEquals("ACK", ack.getOperation());
		}
	}
}