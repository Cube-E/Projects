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
import java.util.Base64;
import org.junit.Assert;
import org.junit.Test;
import instayak.serialization.InstaYakException;
import instayak.serialization.InstaYakMessage;
import instayak.serialization.InstaYakUOn;
import instayak.serialization.MessageInput;
import instayak.serialization.MessageOutput;



/**
 * Created by QB on 1/29/2017.
 */
public class InstaYakUOnTest extends InstaYakMessageTest{
	
    private static final InstaYakUOn m1;
    private static final byte[] m1Enc;
    private static final InstaYakUOn m2;
    private static final byte[] m2Enc;
    private static final byte[] m5Enc;
    private static final byte[] m6Enc;
    
    private InputStream in;


    static {
        try {
        	m1Enc = Base64.getEncoder().encode("apictureofcats".getBytes("ISO8859-1"));
            m1 = new InstaYakUOn("cats",m1Enc);
            
            m2Enc = Base64.getEncoder().encode("apictureofdogs".getBytes("ISO8859-1"));
            m2 = new InstaYakUOn("dogs", m2Enc);
            
            m5Enc = "UOn cats ".getBytes("ISO8859-1");
            m6Enc = "UOn dogs ".getBytes("ISO8859-1");
           
        } catch (UnsupportedEncodingException | InstaYakException var1) {
            throw new RuntimeException("Evil", var1);
        }
    }
    
    //Test String/Byte[] constructor
    @Test (expected=InstaYakException.class)
    public void testNoCategConstructor() throws InstaYakException{
    	InstaYakUOn err = new InstaYakUOn("", m2Enc);

    }
    
    @Test (expected=InstaYakException.class)
    public void testNullCategConstructor() throws InstaYakException{
    	InstaYakUOn err = new InstaYakUOn(null, m2Enc);

    }
    
    @Test (expected=InstaYakException.class)
    public void testConstructor() throws InstaYakException{
    	InstaYakUOn err = new InstaYakUOn("dog!", m2Enc);
    }
    
    //Test MessageInput constructor
    @Test
    public void testMInputConstructor() throws InstaYakException, IOException{
    	String str = Base64.getEncoder().encodeToString("apictureofdogs".getBytes(StandardCharsets.ISO_8859_1));
    	str = "UOn dogs " + str.toString()+ "\r\n";
    	in =  getInputStream(str);
    	MessageInput msg = new MessageInput(in);
        InstaYakUOn err = (InstaYakUOn)InstaYakMessage.decode(msg);   
        assertTrue("dogs".equals(err.getCategory()));
      //  Assert.assertArrayEquals(barr, err.getImage());
    }
    
    //test toString
    @Test
    public void testToString() throws InstaYakException, IOException{
    	String str = Base64.getEncoder().encodeToString("apictureofdogs".getBytes(StandardCharsets.ISO_8859_1));
    	str = "UOn dogs " + str.toString()+ "\r\n";
    	in =  getInputStream(str);
    	MessageInput msg = new MessageInput(in);
        InstaYakUOn err = (InstaYakUOn)InstaYakMessage.decode(msg);   
        int size = err.getImage().length;
        str ="UOn: Category=dogs Image="+size+" bytes";
        assertTrue(str.equals(err.toString()));
    }
    
    //Test getCategory
    @Test
    public void testGetCategory() throws InstaYakException, IOException{
    	assertTrue("dogs".equals(m2.getCategory()));
    }
    
    //Test getImage
    @Test
    public void testGetImage() throws InstaYakException, IOException{
    	String str = Base64.getEncoder().encodeToString("apictureofdogs".getBytes(StandardCharsets.ISO_8859_1));
    	str = "UOn dogs " + str.toString()+ "\r\n";
    	in =  getInputStream(str);
    	MessageInput msg = new MessageInput(in);
        InstaYakUOn err = (InstaYakUOn)InstaYakMessage.decode(msg);  
        
        byte[] barr = Base64.getEncoder().encode("apictureofdogs".getBytes(StandardCharsets.ISO_8859_1));
        barr = Base64.getDecoder().decode(barr);
        Assert.assertArrayEquals(barr, err.getImage());;
    }
    
    //test setCategory
    @Test
    public void testSetCategory() throws InstaYakException, IOException{
    	String str = Base64.getEncoder().encodeToString("apictureofdogs".getBytes(StandardCharsets.ISO_8859_1));
    	str = "UOn dogs " + str.toString()+ "\r\n";
    	in =  getInputStream(str);
    	MessageInput msg = new MessageInput(in);
        InstaYakUOn err = (InstaYakUOn)InstaYakMessage.decode(msg);  
        
        err.setCategory("cats");
        assertTrue("cats".equals(err.getCategory()));
    }
    
    @Test (expected=InstaYakException.class)
    public void testBadSetCategory() throws InstaYakException, IOException{
    	String str = Base64.getEncoder().encodeToString("apictureofdogs".getBytes(StandardCharsets.ISO_8859_1));
    	str = "UOn dogs " + str.toString()+ "\r\n";
    	in =  getInputStream(str);
    	MessageInput msg = new MessageInput(in);
        InstaYakUOn err = (InstaYakUOn)InstaYakMessage.decode(msg);  
        
        err.setCategory("cat!");
    }

    @Test (expected=InstaYakException.class)
    public void testBadSetCategory2() throws InstaYakException, IOException{
    	String str = Base64.getEncoder().encodeToString("apictureofdogs".getBytes(StandardCharsets.ISO_8859_1));
    	str = "UOn dogs " + str.toString()+ "\r\n";
    	in =  getInputStream(str);
    	MessageInput msg = new MessageInput(in);
        InstaYakUOn err = (InstaYakUOn)InstaYakMessage.decode(msg);  
        
        err.setCategory(null);
    }
    
    //test setimage
    @Test 
    public void testSetImage() throws InstaYakException, IOException{
    	String str = Base64.getEncoder().encodeToString("apictureofdogs".getBytes(StandardCharsets.ISO_8859_1));
    	str = "UOn dogs " + str.toString()+ "\r\n";
    	in =  getInputStream(str);
    	MessageInput msg = new MessageInput(in);
        InstaYakUOn err = (InstaYakUOn)InstaYakMessage.decode(msg);  
        
        byte[] barr = Base64.getEncoder().encode("apictureofcats".getBytes(StandardCharsets.ISO_8859_1));
        barr = Base64.getDecoder().decode(barr);
        err.setImage(barr);
        Assert.assertArrayEquals(barr, err.getImage());;
              
    }
    
    @Test (expected=InstaYakException.class)
    public void testBadSetImage() throws InstaYakException, IOException{
    	String str = Base64.getEncoder().encodeToString("apictureofdogs".getBytes(StandardCharsets.ISO_8859_1));
    	str = "UOn dogs " + str.toString()+ "\r\n";
    	in =  getInputStream(str);
    	MessageInput msg = new MessageInput(in);
        InstaYakUOn err = (InstaYakUOn)InstaYakMessage.decode(msg);  
        
        err.setImage(null);
    }
    
    //test getOperation
    @Test
    public void testGeOperation() throws InstaYakException, IOException{
    	assertTrue("UOn".equals(m2.getOperation()));
    }
      
    //test encode
    @Test 
    public void testEncode() throws IOException, InstaYakException{
    	ByteArrayOutputStream bout1 = new ByteArrayOutputStream();
    	MessageOutput out1 = new MessageOutput(bout1);
    	
    	ByteArrayOutputStream bout2 = new ByteArrayOutputStream();
    	MessageOutput out2 = new MessageOutput(bout2);
    	
    	String str = Base64.getEncoder().encodeToString("apictureofdogs".getBytes(StandardCharsets.ISO_8859_1));
    	str = "UOn dogs " + str.toString()+ "\r\n";
    	in =  getInputStream(str);
    	MessageInput msg = new MessageInput(in);
        InstaYakUOn err = (InstaYakUOn)InstaYakMessage.decode(msg);    
        
    	str = Base64.getEncoder().encodeToString("apictureofdogs".getBytes(StandardCharsets.ISO_8859_1));
    	str = "UOn dogs " + str.toString()+ "\r\n";
    	InputStream jin =  getInputStream(str);
    	MessageInput mam = new MessageInput(jin);
        InstaYakUOn uhh = (InstaYakUOn)InstaYakMessage.decode(mam);

    	err.encode(out1);
    	uhh.encode(out2);
    	
    	Assert.assertArrayEquals(bout2.toByteArray(), bout1.toByteArray() );
    }
    
    //test equals
    @Test 
    public void testEquals() throws InstaYakException, IOException{
    	String str = Base64.getEncoder().encodeToString("apictureofdogs".getBytes(StandardCharsets.ISO_8859_1));
    	str = "UOn dogs " + str.toString()+ "\r\n";
    	in =  getInputStream(str);
    	MessageInput msg = new MessageInput(in);
        InstaYakUOn err = (InstaYakUOn)InstaYakMessage.decode(msg);    
        
    	str = Base64.getEncoder().encodeToString("apictureofdogs".getBytes(StandardCharsets.ISO_8859_1));
    	str = "UOn dogs " + str.toString()+ "\r\n";
    	InputStream jin =  getInputStream(str);
    	MessageInput mam = new MessageInput(jin);
        InstaYakUOn uhh = (InstaYakUOn)InstaYakMessage.decode(mam);
        
        Assert.assertArrayEquals(err.getImage(), uhh.getImage());

    }
    
    @Test
    public void testBadEquals() throws IOException, InstaYakException {      
        assertFalse(m2.equals(m1));
    }
    
    //test hash
    @Test 
    public void testHash() throws InstaYakException, IOException{
    	String str = Base64.getEncoder().encodeToString("apictureofdogs".getBytes(StandardCharsets.ISO_8859_1));
    	str = "UOn dogs " + str.toString()+ "\r\n";
    	in =  getInputStream(str);
    	MessageInput msg = new MessageInput(in);
        InstaYakUOn err = (InstaYakUOn)InstaYakMessage.decode(msg);    
        
    	str = Base64.getEncoder().encodeToString("apictureofdogs".getBytes(StandardCharsets.ISO_8859_1));
    	str = "UOn dogs " + str.toString()+ "\r\n";
    	InputStream jin =  getInputStream(str);
    	MessageInput mam = new MessageInput(jin);
        InstaYakUOn uhh = (InstaYakUOn)InstaYakMessage.decode(mam);
        
        assertTrue(err.hashCode() == uhh.hashCode());

    }
    


}