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

import instayak.serialization.InstaYakChallenge;
import instayak.serialization.InstaYakCredentials;
import instayak.serialization.InstaYakException;
import instayak.serialization.InstaYakMessage;
import instayak.serialization.MessageInput;
import instayak.serialization.MessageOutput;

public class InstaYakCredentialsTest extends InstaYakMessageTest {
	
    private static final InstaYakCredentials c1;
    private static final byte[] test;
    private static final InstaYakCredentials c2;
    private static final byte[] test2;
    private static final byte[] test3;
    private InputStream in;

    static {
        try {
        	c1 = new InstaYakCredentials("0A0B0C0D0E0F01020304050607080900");
            test = "CRED 0A0B0C0D0E0F01020304050607080900\r\n".getBytes("ISO8859-1");
            c2 = new InstaYakCredentials("0A0B0C0D0E0F00010203040506070809");
            test2 = "CRED 0A0B0C0D0E0F00010203040506070809\r\n".getBytes("ISO8859-1");
            test3 = "CRED 0A0B0C0D0E0F0001020304050607080A\r\nCRED 0A0B0C0D0E0F0001020304050607080A\r\n".getBytes("ISO8859-1");
        } catch (UnsupportedEncodingException | InstaYakException var1) {
            throw new RuntimeException("Error", var1);
        }
    }
    
    //Test string constructor
	@Test (expected=InstaYakException.class)
	public void testInvalidStringConstructor() throws InstaYakException{
		InstaYakCredentials m = new InstaYakCredentials(" ");
	}
	
	@Test (expected=InstaYakException.class)
	public void testInvalidStringConstructor2() throws InstaYakException{
		InstaYakCredentials m = new InstaYakCredentials("");
	}

	@Test (expected=InstaYakException.class)
	public void testInvalidStringConstructor3() throws InstaYakException{
		InstaYakCredentials m = new InstaYakCredentials("0A0B0C0D0E0F000102030405060708");
	}
	
	@Test (expected=InstaYakException.class)
	public void testInvalidStringConstructor4() throws InstaYakException{
		InstaYakCredentials m = new InstaYakCredentials("0A0B0C0D0E0F0a01020304050607080A");
	}
	
	@Test (expected=InstaYakException.class)
	public void testBadCharHash() throws InstaYakException{
		InstaYakCredentials m = new InstaYakCredentials("GA0B0C0D0E0F0G01020304050607080A");
	}
	
	@Test 
	public void testValidStringConstructor() throws InstaYakException{
		InstaYakCredentials m = new InstaYakCredentials("0A0B0C0D0E0F0001020304050607080A");
		assertTrue("0A0B0C0D0E0F0001020304050607080A".equals(m.getHash()));
	}

	//Test MessageInput constructor
	@Test (expected=InstaYakException.class)
	public void testInvalidInputConstructor() throws IOException, InstaYakException{
		InstaYakCredentials m = new InstaYakCredentials(new MessageInput(null));
	}
	
	@Test (expected=InstaYakException.class)
	public void testInvalidInputConstructor2() throws InstaYakException, IOException{
		in = getInputStream("CRED 0A0B0C0D0E0F0a01020304050607080A/r/n");
		InstaYakMessage m = InstaYakMessage.decode(new MessageInput(in));
	}
	
	@Test (expected=InstaYakException.class)
	public void testInvalidInputConstructor3() throws InstaYakException, IOException{
		in = getInputStream("CRED 0A0B0C0D0E0F0001020304050607080/r/n");
		InstaYakMessage m = InstaYakMessage.decode(new MessageInput(in));
	}
	
	@Test (expected=InstaYakException.class)
	public void testInvalidInputConstructor4() throws InstaYakException, IOException{
		in = getInputStream("CRED 0A0B0C0D0E0F0001020304050607080/r /n");
		InstaYakMessage m = InstaYakMessage.decode(new MessageInput(in));
	}
	
	@Test (expected=InstaYakException.class)
	public void testInvalidInputConstructor5() throws InstaYakException, IOException{
		in = getInputStream("CRED /r  /n");
		InstaYakMessage m = InstaYakMessage.decode(new MessageInput(in));
	}
	
	@Test 
	public void testValidInputConstructor4() throws InstaYakException, IOException{
		in = getInputStream("CRED 0A0B0C0D0E0F00010203040506070809\r\n");
		InstaYakMessage m = InstaYakMessage.decode(new MessageInput(in));
		InstaYakCredentials j = new InstaYakCredentials("0A0B0C0D0E0F00010203040506070809");
		assertTrue(j.equals(m));
	}
	
	//Test toString
	@Test
	public void testToSting() throws InstaYakException, IOException{
		InstaYakCredentials m = new InstaYakCredentials("0A0B0C0D0E0F0001020304050607080A");
		assertTrue("Credentials: Hash=0A0B0C0D0E0F0001020304050607080A".equals(m.toString()));
	}
	
	//Test getHash
	public void testGetHash() throws InstaYakException, IOException{
		InstaYakCredentials m = new InstaYakCredentials("0A0B0C0D0E0F0001020304050607080A");
		assertTrue("0A0B0C0D0E0F0001020304050607080A".equals(m.getHash()));
	}
	
	//Test SetHash
	@Test (expected=InstaYakException.class)
	public void testBadSetHash() throws InstaYakException, IOException{
		InstaYakCredentials m = new InstaYakCredentials("0A0B0C0D0E0F0001020304050607080A");
		m.setHash(null);
	}
	
	@Test (expected=InstaYakException.class)
	public void testBadSetHash2() throws InstaYakException, IOException{
		InstaYakCredentials m = new InstaYakCredentials("0A0B0C0D0E0F0001020304050607080A");
		m.setHash("0A0B0C0D0E0F0001020304050607080");
	}
	
	@Test (expected=InstaYakException.class)
	public void testBadSetHash3() throws InstaYakException, IOException{
		InstaYakCredentials m = new InstaYakCredentials("0A0B0C0D0E0F0001020304050607080A");
		m.setHash("0A0B0C0D0E0Fa001020304050607080");
	}
	
	@Test 
	public void testSetHash() throws InstaYakException, IOException{
		InstaYakCredentials m = new InstaYakCredentials("0A0B0C0D0E0F0001020304050607080A");
		m.setHash("0A0B0C0D0E0F00010203040506070809");
		assertTrue("0A0B0C0D0E0F00010203040506070809".equals(m.getHash()));
	}
	
	//Test getOperation
	@Test
	public void testGetOP() throws InstaYakException, IOException{
		InstaYakCredentials m = new InstaYakCredentials("0A0B0C0D0E0F0001020304050607080A");
		assertTrue("CRED".equals(m.getOperation()));
	}
	
	
	
    //Test InstaYakError encode
    @Test
    public void testParallelEncode() throws IOException {
        ByteArrayOutputStream bout1 = new ByteArrayOutputStream();
        MessageOutput out1 = new MessageOutput(bout1);
        ByteArrayOutputStream bout2 = new ByteArrayOutputStream();
        MessageOutput out2 = new MessageOutput(bout2);
        c2.encode(out2);
        c1.encode(out1);
        Assert.assertArrayEquals(test, bout1.toByteArray());
        Assert.assertArrayEquals(test2, bout2.toByteArray());
    }
    
	@Test
	public void testThreeEncodes() throws InstaYakException, IOException{
		in = getInputStream("CRED 0A0B0C0D0E0F0001020304050607080A\r\nCRED 0A0B0C0D0E0F0001020304050607080A\r\n");
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
		InstaYakCredentials v3 = new InstaYakCredentials("0A0B0C0D0E0F0001020304050607080A");
		v3.encode(out3);		
		Assert.assertArrayEquals(a.toByteArray(), b.toByteArray());
		Assert.assertArrayEquals(a.toByteArray(), c.toByteArray());
		Assert.assertArrayEquals(b.toByteArray(), c.toByteArray());
	}
    
	@Test
	public void testDoubleEncodes() throws InstaYakException, IOException{
		in = getInputStream("CRED 0A0B0C0D0E0F0001020304050607080A\r\nCRED 0A0B0C0D0E0F0001020304050607080A\r\n");
		MessageInput min = new MessageInput(in);		
		ByteArrayOutputStream a = getByteOutputStream();
		MessageOutput out = getMessageOut(a);		
		InstaYakMessage v1 = InstaYakMessage.decode(min);
		v1.encode(out);		
		InstaYakMessage v2 = InstaYakMessage.decode(min);
		v2.encode(out);
		Assert.assertArrayEquals(a.toByteArray(), test3);

	}
	
    //Test Equals
    @Test
    public void testGoodEquals() throws IOException, InstaYakException {
        InputStream f =  new ByteArrayInputStream("CRED 0A0B0C0D0E0F00010203040506070809\r\n".getBytes(StandardCharsets.ISO_8859_1));
        MessageInput msg = new MessageInput(f);
        InstaYakCredentials err = (InstaYakCredentials)InstaYakMessage.decode(msg);       
        assertTrue(c2.equals(err) && err.equals(c2));
    }
    
    @Test
    public void testBadEquals() throws IOException, InstaYakException {      
        assertFalse(c2.equals(c1));
    }
	
    //Test hashCode
    @Test
    public void testHashCode() throws IOException, InstaYakException {
        InputStream f =  new ByteArrayInputStream("CRED 0A0B0C0D0E0F00010203040506070809\r\n".getBytes(StandardCharsets.ISO_8859_1));
        MessageInput msg = new MessageInput(f);
        InstaYakCredentials err = (InstaYakCredentials)InstaYakMessage.decode(msg);       
        assertTrue(c2.hashCode() == err.hashCode());
    }
   

}
