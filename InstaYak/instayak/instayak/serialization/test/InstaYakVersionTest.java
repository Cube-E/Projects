/************************************************
 *
 * Author:      Quoc-Bao Huynh
 * Assignment:  Program 0
 * Class:       CSI 4321
 *
 ************************************************/
package instayak.serialization.test;

import instayak.serialization.InstaYakChallenge;
import instayak.serialization.InstaYakCredentials;
import instayak.serialization.InstaYakException;
import instayak.serialization.InstaYakMessage;
import instayak.serialization.InstaYakVersion;
import instayak.serialization.MessageInput;
import instayak.serialization.MessageOutput;
import instayak.serialization.test.InstaYakMessageTest;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

import org.junit.Assert;
import org.junit.Test;


public class InstaYakVersionTest extends InstaYakMessageTest {

	private static final byte[] test;
	private static final byte[] test2;
	//private static final InstaYakID v1;
	private InputStream in;
	

	//Test constructor
	static {
		try {
			test = "INSTAYAK 1.0\r\n".getBytes("ISO8859-1");
			test2 = "INSTAYAK 1.0\r\nINSTAYAK 1.0\r\n".getBytes("ISO8859-1");
			

			
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("Unable to encode", e);
		} 
	}
	//Test MessageInput constructor
	@Test (expected=InstaYakException.class)
	public void testNullConstructor() throws IOException, InstaYakException{
		InstaYakVersion m = new InstaYakVersion(new MessageInput(null));
	}
	@Test(expected=InstaYakException.class)
	public void testBadVersionConstructor() throws InstaYakException, IOException{
		in = getInputStream("INSTAYAK 1.\r\n");
		InstaYakMessage m = InstaYakMessage.decode(new MessageInput(in));
	}
	
	@Test(expected=InstaYakException.class)
	public void testBadVersionConstructor2() throws InstaYakException, IOException{
		in = getInputStream("INSTAYAK .0\r\n");
		InstaYakMessage m = InstaYakMessage.decode(new MessageInput(in));
	}
	
	@Test(expected=InstaYakException.class)
	public void testOnlyOP() throws InstaYakException, IOException{
		in = getInputStream("INSTAYAK");
		InstaYakMessage m = InstaYakMessage.decode(new MessageInput(in));
	}
	
	@Test(expected=InstaYakException.class)
	public void testBadFrameConstructor() throws InstaYakException, IOException{
		in = getInputStream("INSTAYAK 1.0\rXXXXX\n");
		InstaYakMessage m = InstaYakMessage.decode(new MessageInput(in));
	}
	
	//Test toString
	@Test
	public void testToSting() throws InstaYakException, IOException{
		InstaYakVersion m = new InstaYakVersion();
		assertTrue("InstaYak".equals(m.toString()));
	}
	
	//Test getOperation
	@Test
	public void testGetOP() throws InstaYakException, IOException{
		InstaYakVersion m = new InstaYakVersion();
		assertTrue("INSTAYAK".equals(m.getOperation()));
	}
	
	//Test encode. Also test decode when reading from the stream
	@Test
	public void testFaultyInputStreamEncode() throws InstaYakException, IOException{
		in = getInputStream("INSTAYAK 1.0\rXXXXX\nINSTAYAK 1.0\r\n");
		MessageInput min = new MessageInput(in);
		try{
		InstaYakMessage bad = InstaYakMessage.decode(min);
		}catch(InstaYakException e){
			ByteArrayOutputStream b = getByteOutputStream();
			MessageOutput out = getMessageOut(b);
			InstaYakMessage good = InstaYakMessage.decode(min);
			good.encode(out);
			Assert.assertArrayEquals(test, b.toByteArray());
		}
	}
	
	
	@Test
	public void testThreeEncodes() throws InstaYakException, IOException{
		in = getInputStream("INSTAYAK 1.0\r\nINSTAYAK 1.0\r\n");
		MessageInput min = new MessageInput(in);		
		ByteArrayOutputStream a = getByteOutputStream();
		MessageOutput out = getMessageOut(a);		
		ByteArrayOutputStream b = getByteOutputStream();
		MessageOutput out2 = getMessageOut(b);		
		ByteArrayOutputStream c = getByteOutputStream();
		MessageOutput out3 = getMessageOut(c);		
		InstaYakMessage v1 = InstaYakMessage.decode(min);
		v1.encode(out);		
		InstaYakMessage v2 = InstaYakMessage.decode(min);
		v2.encode(out2);	
		InstaYakVersion v3 = new InstaYakVersion();
		v3.encode(out3);		
		Assert.assertArrayEquals(a.toByteArray(), b.toByteArray());
		Assert.assertArrayEquals(a.toByteArray(), c.toByteArray());
		Assert.assertArrayEquals(b.toByteArray(), c.toByteArray());
	}
	
	@Test
	public void testDoubleEncodes() throws InstaYakException, IOException{
		in = getInputStream("INSTAYAK 1.0\r\nINSTAYAK 1.0\r\n");
		MessageInput min = new MessageInput(in);		
		ByteArrayOutputStream a = getByteOutputStream();
		MessageOutput out = getMessageOut(a);		
		InstaYakMessage v1 = InstaYakMessage.decode(min);
		v1.encode(out);		
		InstaYakMessage v2 = InstaYakMessage.decode(min);
		v2.encode(out);
		Assert.assertArrayEquals(a.toByteArray(), test2);

	}
	
	//Test equals
	@Test
	public void testEquals() throws InstaYakException, IOException{
		in = getInputStream("INSTAYAK 1.0\r\nINSTAYAK 1.0\r\n");
		MessageInput min = new MessageInput(in);
		InstaYakMessage v1 = InstaYakMessage.decode(min);	
		InstaYakVersion v2 = new InstaYakVersion();
		assertTrue(v1.equals(v2));

	}
	
	
    //Test hashCode
    @Test
    public void testHashCode() throws IOException, InstaYakException {
        in = getInputStream("INSTAYAK 1.0\r\nINSTAYAK 1.0\r\n");
		MessageInput min = new MessageInput(in);
		InstaYakMessage v1 = InstaYakMessage.decode(min);	
		InstaYakVersion v2 = new InstaYakVersion();
		assertTrue(v1.hashCode() == v2.hashCode());
    }
    
    //if I ever want to check if version is 2.0/3.0/etc
	//Regular expression pattern that the version will check against.
	//private static String versionPattern = "([0-9]+).([0-9]+)";
}
