/************************************************
 *
 * Author:      Quoc-Bao Huynh
 * Assignment:  Program 0
 * Class:       CSI 4321
 *
 ************************************************/
package instayak.serialization.test;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

import org.junit.Assert;
import org.junit.Test;

import instayak.serialization.InstaYakCredentials;
import instayak.serialization.InstaYakException;
import instayak.serialization.InstaYakID;
import instayak.serialization.InstaYakMessage;
import instayak.serialization.MessageInput;
import instayak.serialization.MessageOutput;

public class InstaYakIDTest extends InstaYakMessageTest{
	private static final byte[] test;
	private static final byte[] test2;
	private static final InstaYakID tID;
	private static final InstaYakID tID2;
	private InputStream in;
	
	//Test String constructor
	static {
		try {
			test = "ID bob\r\n".getBytes("ISO8859-1");
			tID = new InstaYakID("bob");
			test2 = "ID john\r\n".getBytes("ISO8859-1");
			tID2 = new InstaYakID("john");
			
		} catch (UnsupportedEncodingException | InstaYakException e) {
			throw new RuntimeException("Error", e);
		} 
	}
	
	@Test (expected=InstaYakException.class)
	public void testInvalidStringConstructor() throws InstaYakException{
		InstaYakID m = new InstaYakID(" ");
	}
	
	@Test (expected=InstaYakException.class)
	public void testInvalidStringConstructor2() throws InstaYakException{
		InstaYakID m = new InstaYakID("jo:e");
	}
	
	//Test MessageInput constructor
	@Test (expected=InstaYakException.class)
	public void testInvalidInputConstructor() throws IOException, InstaYakException{
		InstaYakID m = new InstaYakID(new MessageInput(null));
	}
	
	@Test (expected=InstaYakException.class)
	public void testInvalidInputConstructor2() throws InstaYakException, IOException{
		in = getInputStream("ID jo:hn\r\n");
		InstaYakMessage m = InstaYakMessage.decode(new MessageInput(in));
	}
	
	@Test (expected=InstaYakException.class)
	public void testInvalidInputConstructor3() throws InstaYakException, IOException{
		in = getInputStream("ID \r\n");
		InstaYakMessage m = InstaYakMessage.decode(new MessageInput(in));
	}
	
	@Test (expected=InstaYakException.class)
	public void testInvalidInputConstructor4() throws InstaYakException, IOException{
		in = getInputStream("ID  \r\n");
		InstaYakMessage m = InstaYakMessage.decode(new MessageInput(in));
	}
		
		//These tests also test MessageInput
		@Test (expected=InstaYakException.class)
		public void testInvalidInputConstructorBadFrame() throws InstaYakException, IOException{
			in = getInputStream("ID jo:hn\n");
			InstaYakMessage m = InstaYakMessage.decode(new MessageInput(in));
		}
		
		@Test (expected=InstaYakException.class)
		public void testInvalidInputConstructorBadFrame2() throws InstaYakException, IOException{
			in = getInputStream("ID jo:hn\r\r\n");
			InstaYakMessage m = InstaYakMessage.decode(new MessageInput(in));
		}
		
		@Test (expected=InstaYakException.class)
		public void testInvalidInputConstructorBadFrame3() throws InstaYakException, IOException{
			in = getInputStream("ID john\r \n");
			InstaYakMessage m = InstaYakMessage.decode(new MessageInput(in));
		}
		
		@Test (expected=InstaYakException.class)
		public void testInvalidInputConstructorBadFrame4() throws InstaYakException, IOException{
			in = getInputStream("ID john\r \n");
			InstaYakMessage m = InstaYakMessage.decode(new MessageInput(in));
		}
		
		//Tests alphanumeric validation
		@Test (expected=InstaYakException.class)
		public void testInvalidInputConstructorBadMessage() throws InstaYakException, IOException{
			in = getInputStream("ID  john\r\n");
			InstaYakMessage m = InstaYakMessage.decode(new MessageInput(in));
		}
		
		@Test (expected=InstaYakException.class)
		public void testInvalidInputConstructorBadMessage2() throws InstaYakException, IOException{
			in = getInputStream("ID john \r\n");
			InstaYakMessage m = InstaYakMessage.decode(new MessageInput(in));
		}
		
		
		
	//Test toString
	@Test
	public void testToString(){
		assertEquals(tID.toString(), "ID: ID=bob");
	}
	
	//Test getID
	@Test
	public void testGetID(){
		assertEquals(tID.getID(), "bob");
	}
	
	//Test setID
	@Test (expected=InstaYakException.class)
	public void testSetBadID() throws InstaYakException{
		tID.setID("joh:n");
	}
	
	@Test (expected=InstaYakException.class)
	public void testSetBadID2() throws InstaYakException{
		tID.setID(null);
	}
	
	@Test 
	public void testSetGoodID() throws InstaYakException{
		tID.setID("1234john");
		assertEquals(tID.getID(), "1234john");
	}

	//Test getOperation
	@Test
	public void testGetOperation(){
		assertEquals(tID.getOperation(), "ID");
	}
	
	//Test encode
	@Test 
	public void testEncode() throws IOException, InstaYakException{
		ByteArrayOutputStream b = getByteOutputStream();
		MessageOutput out = getMessageOut(b);
		InstaYakID id = new InstaYakID("bob");
		id.encode(out);
		Assert.assertArrayEquals(test, b.toByteArray());
		
	}
	
	@Test 
	public void testEncode2() throws IOException, InstaYakException{
		ByteArrayOutputStream b = getByteOutputStream();
		MessageOutput out = getMessageOut(b);
		tID2.encode(out);
		Assert.assertArrayEquals(test2, b.toByteArray());
		
	}
	
	//Test equals
	@Test
	public void testEquals() throws InstaYakException, IOException{
		in = getInputStream("ID john\r\n");
		InstaYakMessage m = InstaYakMessage.decode(new MessageInput(in));
		assertTrue(m.toString().equals(tID2.toString()));
	}
	
	@Test
	public void testNotEquals() throws InstaYakException, IOException{
		in = getInputStream("ID bob\r\n");
		InstaYakMessage m = InstaYakMessage.decode(new MessageInput(in));

		assertFalse(m.toString().equals(tID2.toString()));
	}
	
    //Test hashCode
	@Test
	public void testHashCode() throws InstaYakException, IOException{
		in = getInputStream("ID john\r\n");
		InstaYakMessage m = InstaYakMessage.decode(new MessageInput(in));
		assertTrue(m.hashCode() == tID2.hashCode());
	}

}
