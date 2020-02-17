package instayak.serialization.test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import instayak.serialization.InstaYakException;
import instayak.serialization.InstaYakMessage;
import instayak.serialization.MessageInput;

@RunWith(Parameterized.class)
public class SimpleInstaYakMessageDecodeTest {
	
	private static final String CHARENC = "ISO8859-1";
	
	@Parameter(0)
	public String encoding;
	
	@Parameters(name="Testing {0}")
	public static Collection<Object[]> data() throws UnsupportedEncodingException {
	    return Arrays.asList(
	    		new Object[][] {
	    			{ "INSTAYAK 1.0\r\n" },
	    			{ "ID bob\r\n" },
	    			{ "CLNG 1\r\n" },
	    			{ "CRED 000102030405060708090A0B0C0D0E0F\r\n" },
	    			{ "UOn Movie ABCDEFGHIJKLMNOPQRSTUVWYYZabcdefghijklmnopqrstuvwxyz0123456789+/\r\n" },
	    			{ "SLMD\r\n" },
	    			{ "ACK\r\n" },
	    			{ "ERROR Boo\r\n" },
	    			});
	}

	@Test
	public void testDecode() throws InstaYakException, IOException {
		MessageInput min = new MessageInput(new ByteArrayInputStream(encoding.getBytes(CHARENC)));
		InstaYakMessage.decode(min);
	}
}