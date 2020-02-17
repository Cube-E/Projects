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
import instayak.serialization.InstaYakError;
import instayak.serialization.InstaYakException;
import instayak.serialization.InstaYakMessage;
import instayak.serialization.MessageInput;
import instayak.serialization.MessageOutput;

public class InstaYakErrorTest {

    private static final InstaYakError m1;
    private static final byte[] m1Enc;
    private static final InstaYakError m2;
    private static final byte[] m2Enc;
    private InputStream in;
    
    static {
        try {
            m1 = new InstaYakError("no nothings");
            m1Enc = "ERROR no nothings\r\n".getBytes("ISO8859-1");
            m2 = new InstaYakError("no somethings");
            m2Enc = "ERROR no somethings\r\n".getBytes("ISO8859-1");
        } catch (UnsupportedEncodingException | InstaYakException var1) {
            throw new RuntimeException("Evil", var1);
        }
    }
    
    //Test InstaYakError()
    @Test
	public void testConstructor() throws IOException, InstaYakException {
        InstaYakError err = new InstaYakError("   a valid   error message ");
	}
    
    @Test (expected= InstaYakException.class)
	public void testInvalidConstructor() throws IOException, InstaYakException {
        InstaYakError err = new InstaYakError("   a valid  ! error message ");
	}
    
    
    //Test InstaYakError(MessageInput)
    @Test
	public void testNormalError() throws IOException, InstaYakException {
        InputStream f =  new ByteArrayInputStream("ERROR me ssa ge \r\n".getBytes(StandardCharsets.ISO_8859_1));
        MessageInput msg = new MessageInput(f);
        InstaYakError err = (InstaYakError)InstaYakMessage.decode(msg);
        assertEquals(err.toString(), "Error: Message=me ssa ge ");
        assertEquals(err.getOperation(), "ERROR");
     
	}
     
    @Test (expected = InstaYakException.class)
 	public void testErrorWithSymbols() throws IOException, InstaYakException {
        InputStream f =  new ByteArrayInputStream("ERROR mes!sage\r\n".getBytes(StandardCharsets.ISO_8859_1));
        MessageInput msg = new MessageInput(f);
        InstaYakError err = (InstaYakError)InstaYakMessage.decode(msg);
	}
    
    @Test (expected = InstaYakException.class)
    public void testConstructorIOExcep() throws IOException, InstaYakException{
    	MessageInput msg = new MessageInput(null);
    	InstaYakError blah = new InstaYakError(msg);
    	
    	
    }
    
    @Test (expected = InstaYakException.class)
 	public void testBadBackRError() throws IOException, InstaYakException {
        InputStream f =  new ByteArrayInputStream("ERROR message\r\r\n".getBytes(StandardCharsets.ISO_8859_1));
        MessageInput msg = new MessageInput(f);
        InstaYakError err = (InstaYakError)InstaYakMessage.decode(msg);
	}
    
    //Test tostring
    @Test
	public void testToString() throws IOException, InstaYakException {
        InputStream f =  new ByteArrayInputStream("ERROR me ssa ge \r\n".getBytes(StandardCharsets.ISO_8859_1));
        MessageInput msg = new MessageInput(f);
        InstaYakError err = (InstaYakError)InstaYakMessage.decode(msg);
        assertEquals(err.toString(), "Error: Message=me ssa ge ");
     
	}
    
    //test get message
    @Test
	public void testGetMessage() throws IOException, InstaYakException {
        InputStream f =  new ByteArrayInputStream("ERROR me ssa ge \r\n".getBytes(StandardCharsets.ISO_8859_1));
        MessageInput msg = new MessageInput(f);
        InstaYakError err = (InstaYakError)InstaYakMessage.decode(msg);
        assertEquals(err.getMessage(), "me ssa ge ");     
	}
    
    //test set message
    @Test
	public void testSetMessage() throws IOException, InstaYakException {
        InputStream f =  new ByteArrayInputStream("ERROR me ssa ge \r\n".getBytes(StandardCharsets.ISO_8859_1));
        MessageInput msg = new MessageInput(f);
        InstaYakError err = (InstaYakError)InstaYakMessage.decode(msg);
        err.setMessage("a new message");
        assertEquals(err.getMessage(), "a new message");    
	}
    
    @Test (expected = InstaYakException.class)
	public void testSetBadMessage() throws IOException, InstaYakException {
        InputStream f =  new ByteArrayInputStream("ERROR me ssa ge \r\n".getBytes(StandardCharsets.ISO_8859_1));
        MessageInput msg = new MessageInput(f);
        InstaYakError err = (InstaYakError)InstaYakMessage.decode(msg);
        err.setMessage("a new message!");
	}
    
    @Test (expected = InstaYakException.class)
	public void testSetBadMessag2e() throws IOException, InstaYakException {
        InputStream f =  new ByteArrayInputStream("ERROR me ssa ge \r\n".getBytes(StandardCharsets.ISO_8859_1));
        MessageInput msg = new MessageInput(f);
        InstaYakError err = (InstaYakError)InstaYakMessage.decode(msg);
        err.setMessage(null);
	}
    
    //test get operation
    @Test
	public void testGetOperation() throws IOException, InstaYakException {
        InputStream f =  new ByteArrayInputStream("ERROR me ssa ge \r\n".getBytes(StandardCharsets.ISO_8859_1));
        MessageInput msg = new MessageInput(f);
        InstaYakError err = (InstaYakError)InstaYakMessage.decode(msg);
        assertEquals(err.getOperation(), "ERROR");    
	}
    
    
    //Test InstaYakError encode
    @Test
    public void testParallelEncode() throws IOException {
        ByteArrayOutputStream bout1 = new ByteArrayOutputStream();
        MessageOutput out1 = new MessageOutput(bout1);
        ByteArrayOutputStream bout2 = new ByteArrayOutputStream();
        MessageOutput out2 = new MessageOutput(bout2);
        m2.encode(out2);
        m1.encode(out1);
        Assert.assertArrayEquals(m1Enc, bout1.toByteArray());
        Assert.assertArrayEquals(m2Enc, bout2.toByteArray());
    }
	

    
    //Test Equals
    @Test
    public void testGoodEquals() throws IOException, InstaYakException {
        InputStream f =  new ByteArrayInputStream("ERROR no somethings\r\n".getBytes(StandardCharsets.ISO_8859_1));
        MessageInput msg = new MessageInput(f);
        InstaYakError err = (InstaYakError)InstaYakMessage.decode(msg);       
        assertTrue(m2.equals(err));
    }
    
    @Test
    public void testBadEquals() throws IOException, InstaYakException {      
        assertFalse(m2.equals(m1));
    }
    
    //Test hash
    @Test
    public void testHash() throws IOException, InstaYakException {
        in =  new ByteArrayInputStream("ERROR no somethings\r\n".getBytes(StandardCharsets.ISO_8859_1));
  		MessageInput min = new MessageInput(in);
  		InstaYakMessage v1 = InstaYakMessage.decode(min);	
        InstaYakError v2 = new InstaYakError("no somethings");    
  		assertTrue(v1.hashCode() == v2.hashCode());
    }
	
   

}
