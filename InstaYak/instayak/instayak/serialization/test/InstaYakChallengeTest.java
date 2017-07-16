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
import instayak.serialization.InstaYakID;
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
import java.util.ArrayList;
import java.util.Collection;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

public class InstaYakChallengeTest extends InstaYakMessageTest {

	private static final byte[] test;
	private static final byte[] test2;
	private static final InstaYakChallenge tID;
    private InputStream in;
	
	static {
		try {
			test = "CLNG 1234\r\n".getBytes("ISO8859-1");
			test2= "CLNG 1234\r\nCLNG 1234\r\n".getBytes("ISO8859-1");
			tID = new InstaYakChallenge("1234");
		} catch (UnsupportedEncodingException | InstaYakException e) {
			throw new RuntimeException("Unable to encode", e);
		} 
	}
	
	//Test String constructor
	@Test (expected=InstaYakException.class)
	public void testInvalidStringConstructor() throws InstaYakException{
		InstaYakChallenge m = new InstaYakChallenge(" ");
	}
	
	@Test (expected=InstaYakException.class)
	public void testInvalidStringConstructor2() throws InstaYakException{
		InstaYakChallenge m = new InstaYakChallenge("1234567890.0987654321");
	}
	
	@Test (expected=InstaYakException.class)
	public void testInvalidStringConstructor3() throws InstaYakException{
		InstaYakChallenge m = new InstaYakChallenge("");
	}
	
	@Test (expected=InstaYakException.class)
	public void testInvalidStringConstructor4() throws InstaYakException{
		InstaYakChallenge m = new InstaYakChallenge("12341234a1234123412341234");
	}
	
	@Test 
	public void testValidStringConstructor() throws InstaYakException{
		InstaYakChallenge m = new InstaYakChallenge("1234");
		assertTrue("1234".equals(m.getNonce()));
	}
	
	//Test MessageInput constructor
	
	@Test (expected=InstaYakException.class)
	public void testInvalidInputConstructor() throws IOException, InstaYakException{
		InstaYakChallenge m = new InstaYakChallenge(new MessageInput(null));
	}
	
	@Test (expected=InstaYakException.class)
	public void testInvalidInputConstructor2() throws InstaYakException, IOException{
		in = getInputStream("CLNG 1234,1234/r/n");
		InstaYakMessage m = InstaYakMessage.decode(new MessageInput(in));
	}
	
	@Test (expected=InstaYakException.class)
	public void testInvalidInputConstructor3() throws InstaYakException, IOException{
		in = getInputStream("CLNG /r/n");
		InstaYakMessage m = InstaYakMessage.decode(new MessageInput(in));
	}
	
	@Test (expected=InstaYakException.class)
	public void testInvalidInputConstructor4() throws InstaYakException, IOException{
		in = getInputStream("CLNG a143/r/n");
		InstaYakMessage m = InstaYakMessage.decode(new MessageInput(in));
	}
	
	//Test toString
	@Test
	public void testToSting() throws InstaYakException, IOException{
		InstaYakChallenge m = new InstaYakChallenge("143");
		assertTrue("Challenge: Nonce=143".equals(m.toString()));
	}
	
	//Test getNonce
	@Test 
	public void testGetNonce() throws InstaYakException, IOException{
		InstaYakChallenge m = new InstaYakChallenge("143");
		assertTrue("143".equals(m.getNonce()));
	}
	
	//Test SetNonce
	@Test (expected=InstaYakException.class)
	public void testInvalidSetNonce() throws InstaYakException, IOException{
		InstaYakChallenge m = new InstaYakChallenge("143");
		m.setNonce("");
	}
	
	@Test (expected=InstaYakException.class)
	public void testInvalidSetNonce2() throws InstaYakException, IOException{
		InstaYakChallenge m = new InstaYakChallenge("143");
		m.setNonce(" ");
	}
	
	@Test (expected=InstaYakException.class)
	public void testInvalidSetNonce3() throws InstaYakException, IOException{
		InstaYakChallenge m = new InstaYakChallenge("143");
		m.setNonce("19987123094870987074502-1293784124");
	}
	
	@Test 
	public void testValidSetNonce() throws InstaYakException, IOException{
		InstaYakChallenge m = new InstaYakChallenge("143");
		m.setNonce("190987");
		assertTrue("190987".equals(m.getNonce()));
	}
	
	//Test getOperation
	@Test 
	public void testGetOperation() throws InstaYakException, IOException{
		InstaYakChallenge m = new InstaYakChallenge("143");
		assertTrue("CLNG".equals(m.getOperation()));
	}
	
	//Test encode. Also test decode when reading from the stream
	@Test
	public void testFaultyInputStreamEncode() throws InstaYakException, IOException{
		in = getInputStream("CLNG 1234\rXX\nCLNG 1234\r\n");
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
		in = getInputStream("CLNG 1234\r\nCLNG 1234\r\n");
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
		InstaYakChallenge v3 = new InstaYakChallenge("1234");
		v3.encode(out3);		
		Assert.assertArrayEquals(a.toByteArray(), b.toByteArray());
		Assert.assertArrayEquals(a.toByteArray(), c.toByteArray());
		Assert.assertArrayEquals(b.toByteArray(), c.toByteArray());
	}
	
	@Test
	public void testDoubleEncodes() throws InstaYakException, IOException{
		in = getInputStream("CLNG 1234\r\nCLNG 1234\r\n");
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
		in = getInputStream("CLNG 1234\r\nCLNG 1234\r\n");
		MessageInput min = new MessageInput(in);
		InstaYakMessage v1 = InstaYakMessage.decode(min);	
		InstaYakChallenge v2 = new InstaYakChallenge("1234");
		assertTrue(v1.equals(v2));
	}
	
	@Test
	public void testNotEquals() throws InstaYakException, IOException{
		in = getInputStream("CLNG 1234\r\nCLNG 123\r\n");
		MessageInput min = new MessageInput(in);
		InstaYakMessage v1 = InstaYakMessage.decode(min);	
		InstaYakVersion v2 = new InstaYakVersion();
		assertFalse(v1.equals(v2));

	}
	
    //Test hashCode
    @Test
    public void testHashCode() throws IOException, InstaYakException {
		in = getInputStream("CLNG 1234\r\nCLNG 1234\r\n");
		MessageInput min = new MessageInput(in);
		InstaYakMessage v1 = InstaYakMessage.decode(min);	
		InstaYakChallenge v2 = new InstaYakChallenge("1234");
		assertTrue(v1.hashCode() == v2.hashCode());
    }
	


}
