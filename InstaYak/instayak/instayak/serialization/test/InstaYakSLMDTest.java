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

import instayak.serialization.InstaYakACK;
import instayak.serialization.InstaYakChallenge;
import instayak.serialization.InstaYakException;
import instayak.serialization.InstaYakMessage;
import instayak.serialization.InstaYakSLMD;
import instayak.serialization.InstaYakVersion;
import instayak.serialization.MessageInput;
import instayak.serialization.MessageOutput;

public class InstaYakSLMDTest extends InstaYakMessageTest{
	
	//constants
	private static final byte[] m1Enc;
	private static final InstaYakSLMD m1;
	private static final InstaYakSLMD m2;
	//input stream
    private InputStream in;


	
	static{
		try{
			m1Enc = "SLMD\r\n".getBytes("ISO8859-1");
			m1 = new InstaYakSLMD();
			m2 = new InstaYakSLMD();
		}catch(UnsupportedEncodingException var1){
			throw new RuntimeException(var1);
		}
	}

	//Test messageInput constructor
	@Test
	public void testNormalSLMD() throws IOException, InstaYakException {
        InputStream f =  new ByteArrayInputStream("SLMD\r\n".getBytes(StandardCharsets.ISO_8859_1));
        MessageInput msg = new MessageInput(f);
        InstaYakSLMD slmd = new InstaYakSLMD();
        slmd.decode(msg);
        assertEquals(slmd.getOperation(), "SLMD");

	}
	
	@Test(expected = InstaYakException.class)
	public void testDoubleBackR() throws IOException, InstaYakException {
        InputStream f =  new ByteArrayInputStream("SLMD\r\r\n".getBytes(StandardCharsets.ISO_8859_1));
        MessageInput msg = new MessageInput(f);
        InstaYakSLMD slmd = new InstaYakSLMD();
        slmd.decode(msg);

	}
	
	@Test (expected = InstaYakException.class)
	public void testFrontSpace() throws IOException, InstaYakException {
        InputStream f =  new ByteArrayInputStream(" SLMD\r\n".getBytes(StandardCharsets.ISO_8859_1));
        MessageInput msg = new MessageInput(f);
        InstaYakSLMD slmd = new InstaYakSLMD();
        slmd.decode(msg);

	}
	
	//test ToString
	@Test 
	public void testToString() throws IOException, InstaYakException {
        InputStream f =  new ByteArrayInputStream("SLMD\r\n".getBytes(StandardCharsets.ISO_8859_1));
        MessageInput msg = new MessageInput(f);
        InstaYakSLMD slmd = new InstaYakSLMD();
        slmd.decode(msg);
        assertEquals(slmd.toString(), "SLMD");
	}
	
	//test getOperation
	@Test
	public void testGetOperation() throws IOException, InstaYakException {
        InputStream f =  new ByteArrayInputStream("SLMD\r\n".getBytes(StandardCharsets.ISO_8859_1));
        MessageInput msg = new MessageInput(f);
        InstaYakSLMD slmd = new InstaYakSLMD();
        slmd.decode(msg);
        assertEquals(slmd.getOperation(), "SLMD");

	}
	
	//test encode
    @Test
    public void testParallelEncode() throws IOException {
        ByteArrayOutputStream bout1 = new ByteArrayOutputStream();
        MessageOutput out1 = new MessageOutput(bout1);
        ByteArrayOutputStream bout2 = new ByteArrayOutputStream();
        MessageOutput out2 = new MessageOutput(bout2);
        m2.encode(out2);
        m1.encode(out1);
        Assert.assertArrayEquals(m1Enc, bout1.toByteArray());
        Assert.assertArrayEquals(m1Enc, bout2.toByteArray());
    }
    
	//Test equals
	@Test
	public void testEquals() throws InstaYakException, IOException{
		in = getInputStream("SLMD\r\nSLMD\r\n");
		MessageInput min = new MessageInput(in);
		InstaYakMessage v1 = InstaYakMessage.decode(min);	
		InstaYakSLMD v2 = new InstaYakSLMD();
		assertTrue(v1.equals(v2));
	}
	
	@Test
	public void testNotEquals() throws InstaYakException, IOException{
		in = getInputStream("SLMD\r\nSLMD\r\n");
		MessageInput min = new MessageInput(in);
		InstaYakMessage v1 = InstaYakMessage.decode(min);	
		InstaYakACK v2 = new InstaYakACK();
		assertFalse(v1.equals(v2));

	}
	
    //Test hashCode
    @Test
    public void testHashCode() throws IOException, InstaYakException {
		in = getInputStream("SLMD\r\nSLMD\r\n");
		MessageInput min = new MessageInput(in);
		InstaYakMessage v1 = InstaYakMessage.decode(min);	
		InstaYakSLMD v2 = new InstaYakSLMD();
		assertTrue(v1.hashCode() == v2.hashCode());
    }
	
	

}
