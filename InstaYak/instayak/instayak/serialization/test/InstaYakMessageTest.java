/************************************************
 *
 * Author:      Quoc-Bao Huynh
 * Assignment:  Program 0
 * Class:       CSI 4321
 *
 ************************************************/
package instayak.serialization.test;

import instayak.serialization.InstaYakException;
import instayak.serialization.InstaYakMessage;
import instayak.serialization.MessageInput;
import instayak.serialization.MessageOutput;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import org.junit.Assert;
import org.junit.Test;

public abstract class InstaYakMessageTest {
	
	//pass an inputstream
	public InputStream getInputStream(String str) throws UnsupportedEncodingException{
		InputStream in = new ByteArrayInputStream(str.getBytes("ISO8859-1"));
		return in;
	}
	
	//pass a bytearrayoutputstream
	public ByteArrayOutputStream getByteOutputStream(){
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		return out;
	}
	
	//pass a messageoutput
	public MessageOutput getMessageOut(ByteArrayOutputStream b){
		MessageOutput out = new MessageOutput(b);
		return out;
	}
	
	
}
