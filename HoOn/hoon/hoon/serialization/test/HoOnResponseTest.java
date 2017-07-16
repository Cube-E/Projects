/************************************************
 *
 * Author:      Quoc-Bao Huynh
 * Assignment:  Program 4
 * Class:       CSI 4321
 *
 ************************************************/
package hoon.serialization.test;


import static org.junit.Assert.*;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import hoon.serialization.ErrorCode;
import hoon.serialization.HoOnException;
import hoon.serialization.HoOnMessage;
import hoon.serialization.HoOnQuery;
import hoon.serialization.HoOnResponse;

/*
 * HoOnResponseTest
 *
 * 1.0
 *
 * February 24, 2017
 *
 * Copyright
 */
public class HoOnResponseTest {
	
	private static final byte[] h = "hello".getBytes();
	private static final byte[] a = "testing".getBytes();
	private static final byte[] bas = "This is a really long post\nit even has newlines!".getBytes();
	private static final byte[] na = "".getBytes();
	private static final int size = h.length + a.length + 8 + bas.length + na.length + 8;
	private static ByteBuffer b;
	static{
		try{
			byte[] barr = new byte[size];
			b = ByteBuffer.wrap(barr);
			b.position(0);
			b.put((byte) 40);
			b.put((byte) 2);
			b.putInt((int) 2139062143L);
			b.putShort((short) 4);
			b.putShort((short)h.length);
			b.put(h);
			b.putShort((short)a.length);
			b.put(a);
			b.putShort((short)na.length);
			b.put(na);
			b.putShort((short)bas.length);
			b.put(bas);
		}catch(Exception e){
			throw new RuntimeException("Unable to do it",e);
		}
	}
	
	@Test(expected = HoOnException.class)
	public void testBadVersion() throws HoOnException{
		byte[] barr = new byte[8];
		ByteBuffer be;
		be = ByteBuffer.wrap(barr);
		be.position(0);
		be.put((byte) 50);//bad version
		be.put((byte) 0);
		be.putInt((int) 1234L);
		 HoOnMessage msg = HoOnMessage.decode(be.array());
	}
	
	@Test(expected = HoOnException.class)
	public void testBadQR() throws HoOnException{
		byte[] barr = new byte[8];
		ByteBuffer be;
		be = ByteBuffer.wrap(barr);
		be.position(0);
		be.put((byte) 33); // bad QR
		be.put((byte) 0);
		be.putInt((int) 1234L);
		 HoOnMessage msg = HoOnMessage.decode(be.array());
	}
	
	
	@Test(expected = HoOnException.class)
	public void testReserve() throws HoOnException{
		byte[] barr = new byte[8];
		ByteBuffer be;
		be = ByteBuffer.wrap(barr);
		be.position(0);
		be.put((byte) 31); // bad QR
		be.put((byte) 0);
		be.putInt((int) 1234L);
		 HoOnMessage msg = HoOnMessage.decode(be.array());
	}
	@Test
	public void testDecodeResponse() throws HoOnException{
		 HoOnMessage msg = HoOnMessage.decode(b.array());
	}
	
	@Test(expected = HoOnException.class)
	public void testNullDecodeResponse() throws HoOnException{
		 HoOnMessage msg = HoOnMessage.decode(null);
	}
	
	@Test(expected = HoOnException.class)
	public void testMaxDecodeResponse() throws HoOnException{
		byte[] barr = new byte[65508];
		byte[] h = "hello".getBytes();
		byte[] a = "testing".getBytes();
		byte[] bas = "This is a really long post\nit even has newlines!".getBytes();
		byte[] na = "".getBytes();
		ByteBuffer be;
		be = ByteBuffer.wrap(barr);
		be.position(0);
		be.put((byte) 40);
		be.put((byte) 3);
		be.putInt((int) 2139062143L);
		 HoOnMessage msg = HoOnMessage.decode(be.array());
	}
	
	@Test(expected = HoOnException.class)
	public void testMinDecodeResponse() throws HoOnException{
		byte[] barr = new byte[7];
		byte[] h = "hello".getBytes();
		byte[] a = "testing".getBytes();
		byte[] bas = "This is a really long post\nit even has newlines!".getBytes();
		byte[] na = "".getBytes();
		ByteBuffer be;
		be = ByteBuffer.wrap(barr);
		be.position(0);
		be.put((byte) 40);
		be.put((byte) 3);
		be.putInt((int) 2139062143L);
		 HoOnMessage msg = HoOnMessage.decode(be.array());
	}
	@Test
	public void testToString() throws HoOnException{
		
		HoOnResponse q = new HoOnResponse(b.array());

		String str = "ErrorCode: " + ErrorCode.getErrorCode(2)+ "\nQueryId:"
								   + " 2139062143\nPosts:\nhello\ntesting\n" 
								   + ""+ "\nThis is a really long post\nit even"
								   + " has newlines!";
		assertTrue(q.toString().equals(str));
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testNegativeQueryIdConstructor() throws IllegalArgumentException, HoOnException{
		List<String> strList = new ArrayList<String>(3);
		strList.add("hello");
		strList.add("testing is fun!!!");
		strList.add("I'm so tired");
		HoOnResponse r = new HoOnResponse(ErrorCode.UNEXPECTEDERRORCODE, -1L, strList);
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testLargeQueryIdConstructor() throws IllegalArgumentException, HoOnException{
		List<String> strList = new ArrayList<String>(3);
		strList.add("hello");
		strList.add("testing is fun!!!");
		strList.add("I'm so tired!");
		HoOnResponse r = new HoOnResponse(ErrorCode.UNEXPECTEDERRORCODE, 4294967296L, strList);
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testTooBigPostList() throws IllegalArgumentException, HoOnException{
		List<String> strList = new ArrayList<String>(65536);
		for(int i = 0; i < 65536; i++){
			strList.add("bloop");
		}
		HoOnResponse r = new HoOnResponse(ErrorCode.UNEXPECTEDERRORCODE, 1L, strList);
	}
	@Test (expected=IllegalArgumentException.class)
	public void testNullPostList() throws IllegalArgumentException, HoOnException{
		List<String> strList =null;
		HoOnResponse r = new HoOnResponse(ErrorCode.UNEXPECTEDERRORCODE, 1L, strList);
	}
	
	@Test
	public void testValidByteConstructor() throws HoOnException{
		HoOnResponse q = new HoOnResponse(b.array());
		List<String> a = new ArrayList<String>();
		a.add("hello");
		a.add("testing");
		a.add("");
		a.add("This is a really long post\nit even has newlines!");
		assertEquals(q.getErrorCode().getErrorCodeValue(), 2);
		assertEquals(q.getQueryId(), 2139062143L);
		List<String> temp = q.getPosts();
		for(int i = 0; i< q.getPosts().size(); i++){
			assertTrue(temp.get(i).equals(a.get(i)));
		}
		
		for(int i = 0; i< a.size(); i++){
			assertTrue(a.get(i).equals(temp.get(i)));
		}
		
		Assert.assertArrayEquals(b.array(), q.encode());		
	}
	
	@Test(expected = HoOnException.class)
	public void testNullBuffConstructor() throws HoOnException{
		byte[] a = null;
		HoOnResponse r = new HoOnResponse(a);
	}
	
	@Test(expected = HoOnException.class)
	public void testZeroBuffConstructor() throws HoOnException{
		byte[] a = {};
		HoOnResponse r = new HoOnResponse(a);
	}
	
	@Test(expected=HoOnException.class)
	public void testBadErrorCodeConstructor() throws HoOnException{
		byte[] barr = new byte[8];
		byte[] h = "hello".getBytes();
		byte[] a = "testing".getBytes();
		byte[] bas = "This is a really long post\nit even has newlines!".getBytes();
		byte[] na = "".getBytes();
		ByteBuffer be;
		be = ByteBuffer.wrap(barr);
		be.position(0);
		be.put((byte) 40);
		be.put((byte) 6);
		be.putInt((int) 2139062143L);
		be.putShort((short) 0);
		HoOnResponse r = new HoOnResponse(be.array());
	}
	
	@Test(expected=HoOnException.class)
	public void testBadErrorCodeConstructor2() throws HoOnException{
		byte[] barr = new byte[8];
		byte[] h = "hello".getBytes();
		byte[] a = "testing".getBytes();
		byte[] bas = "This is a really long post\nit even has newlines!".getBytes();
		byte[] na = "".getBytes();
		ByteBuffer be;
		be = ByteBuffer.wrap(barr);
		be.position(0);
		be.put((byte) 40);
		be.put((byte) 8);
		be.putInt((int) 2139062143L);
		be.putShort((short) 0);
		HoOnResponse r = new HoOnResponse(be.array());
	}
	
	@Test(expected=HoOnException.class)
	public void testBadErrorCodeConstructor3() throws HoOnException{
		byte[] barr = new byte[8];
		byte[] h = "hello".getBytes();
		byte[] a = "testing".getBytes();
		byte[] bas = "This is a really long post\nit even has newlines!".getBytes();
		byte[] na = "".getBytes();
		ByteBuffer be;
		be = ByteBuffer.wrap(barr);
		be.position(0);
		be.put((byte) 40);
		be.put((byte) -1);
		be.putInt((int) 2139062143L);
		be.putShort((short) 0);
		HoOnResponse r = new HoOnResponse(be.array());
	}
	

	@Test(expected= IllegalArgumentException.class)
	public void setBadPosts() throws IllegalArgumentException, HoOnException{
		List<String> a = new ArrayList<String>(65536);
		for(int i = 0; i< 65536; i++){
			a.add("bloop");
		}
		HoOnResponse r = new HoOnResponse(ErrorCode.BADVERSION, 1234L, a);
	}
	
	@Test(expected= IllegalArgumentException.class)
	public void setNullPosts() throws IllegalArgumentException, HoOnException{
		List<String> a = null;
		HoOnResponse r = new HoOnResponse(ErrorCode.BADVERSION, 1234L, a);
	}
	
	@Test(expected= IllegalArgumentException.class)
	public void setNullStringPosts() throws IllegalArgumentException, HoOnException{
		List<String> a = new ArrayList<String>(12);
		for(int i = 0; i< 12; i++){
			a.add("bloop");
		}
		a.set(6, null);
		HoOnResponse r = new HoOnResponse(ErrorCode.BADVERSION, 1234L, a);
	}
	
	@Test
	public void getPosts() throws IllegalArgumentException, HoOnException{
		List<String> a = new ArrayList<String>(12);
		for(int i = 0; i< 12; i++){
			a.add("bloop");
		}
		HoOnResponse r = new HoOnResponse(ErrorCode.BADVERSION, 1234L, a);
		List<String> temp = r.getPosts();
		for(int i = 0; i < a.size(); i++){
			assertTrue(a.get(i).equals(temp.get(i)));
		}
		for(int i = 0; i < temp.size(); i++){
			assertTrue(a.get(i).equals(temp.get(i)));
		}
	}
	
	@Test(expected= IllegalArgumentException.class)
	public void setBadErrorCode() throws HoOnException{
		byte[] barr = new byte[8];
		byte[] h = "hello".getBytes();
		byte[] a = "testing".getBytes();
		byte[] bas = "This is a really long post\nit even has newlines!".getBytes();
		byte[] na = "".getBytes();
		ByteBuffer be;
		be = ByteBuffer.wrap(barr);
		be.position(0);
		be.put((byte) 40);
		be.put((byte) 3);//bad error code
		be.putInt((int) 2139062143L);
		be.putShort((short) 0);
		HoOnResponse r = new HoOnResponse(be.array());
		r.setErrorCode(-1);
	}
	
	@Test(expected= IllegalArgumentException.class)
	public void setBadErrorCode2() throws HoOnException{
		byte[] barr = new byte[8];
		byte[] h = "hello".getBytes();
		byte[] a = "testing".getBytes();
		byte[] bas = "This is a really long post\nit even has newlines!".getBytes();
		byte[] na = "".getBytes();
		ByteBuffer be;
		be = ByteBuffer.wrap(barr);
		be.position(0);
		be.put((byte) 40);
		be.put((byte) 7);//bad error code
		be.putInt((int) 2139062143L);
		be.putShort((short) 0);
		HoOnResponse r = new HoOnResponse(be.array());
		r.setErrorCode(98);
	}
	
	@Test(expected= IllegalArgumentException.class)
	public void setBadErrorCode3() throws HoOnException{
		byte[] barr = new byte[8];
		byte[] h = "hello".getBytes();
		byte[] a = "testing".getBytes();
		byte[] bas = "This is a really long post\nit even has newlines!".getBytes();
		byte[] na = "".getBytes();
		ByteBuffer be;
		be = ByteBuffer.wrap(barr);
		be.position(0);
		be.put((byte) 40);
		be.put((byte) 2);//bad error code
		be.putInt((int) 2139062143L);
		be.putShort((short) 0);
		HoOnResponse r = new HoOnResponse(be.array());
		r.setErrorCode(6);
	}
	
	@Test
	public void testEncode() throws HoOnException{
		HoOnResponse q = new HoOnResponse(b.array());	
		for(byte a: q.encode()){
		}
		for(byte a: b.array()){
		}
		Assert.assertArrayEquals(b.array(), q.encode());		
	}

	
	@Test
	public void testEquals() throws HoOnException{
		
		byte[] barrs = new byte[size];
		byte[] hs = "hello".getBytes();
		byte[] as = "testing".getBytes();
		byte[] bass = "This is a really long post\nit even has newlines!".getBytes();
		byte[] nas = "".getBytes();
		ByteBuffer be;
		be = ByteBuffer.wrap(barrs);
		be.position(0);
		be.put((byte) 40);
		be.put((byte) 2);
		be.putInt((int) 2139062143L);
		be.putShort((short) 4);
		be.putShort((short)hs.length);
		be.put(hs);
		be.putShort((short)as.length);
		be.put(as);
		be.putShort((short)nas.length);
		be.put(nas);
		be.putShort((short)bass.length);
		be.put(bass);
		HoOnResponse r = new HoOnResponse(be.array());
		HoOnResponse aj = new HoOnResponse(b.array());

		assertTrue(r.equals(aj));
	}
	
	@Test
	public void testNotEquals() throws HoOnException{
		
		byte[] barrs = new byte[size-1];
		byte[] hs = "hello".getBytes();
		byte[] as = "testing".getBytes();
		byte[] bass = "his is a really long post\nit even has newlines!".getBytes();
		byte[] nas = "".getBytes();
		ByteBuffer be;
		be = ByteBuffer.wrap(barrs);
		be.position(0);
		be.put((byte) 40);
		be.put((byte) 2);
		be.putInt((int) 2139062143L);
		be.putShort((short) 4);
		be.putShort((short)hs.length);
		be.put(hs);
		be.putShort((short)as.length);
		be.put(as);
		be.putShort((short)nas.length);
		be.put(nas);
		be.putShort((short)bass.length);
		be.put(bass);
		HoOnResponse r = new HoOnResponse(be.array());
		HoOnResponse aj = new HoOnResponse(b.array());

		assertFalse(r.equals(aj));
	}
	
    //Test hashCode
    @Test
    public void testHashCode() throws HoOnException {
		
		byte[] barrs = new byte[size];
		byte[] hs = "hello".getBytes();
		byte[] as = "testing".getBytes();
		byte[] bass = "This is a really long post\nit even has newlines!".getBytes();
		byte[] nas = "".getBytes();
		ByteBuffer be;
		be = ByteBuffer.wrap(barrs);
		be.position(0);
		be.put((byte) 40);
		be.put((byte) 2);
		be.putInt((int) 2139062143L);
		be.putShort((short) 4);
		be.putShort((short)hs.length);
		be.put(hs);
		be.putShort((short)as.length);
		be.put(as);
		be.putShort((short)nas.length);
		be.put(nas);
		be.putShort((short)bass.length);
		be.put(bass);
		HoOnResponse r = new HoOnResponse(be.array());
		HoOnResponse aj = new HoOnResponse(b.array());
		assertTrue(r.hashCode() == aj.hashCode());
    }
	
    @Test
    public void testErrorcodeget0() throws HoOnException {
    	HoOnResponse r = new HoOnResponse(b.array());
    	r.setErrorCode(0);
    	assertEquals(r.getErrorCode().getErrorCodeValue(), ErrorCode.NOERROR.getErrorCodeValue());
    	assertTrue(r.getErrorCode().toString().equals(ErrorCode.NOERROR.toString()));
    	
    }
    
    @Test (expected =IllegalArgumentException.class)
    public void testErrorcodegetBad() throws HoOnException {
    	HoOnResponse r = new HoOnResponse(b.array());
    	r.setErrorCode(0);
    	ErrorCode.getErrorCode(6);
  	
    }
    
    @Test (expected =IllegalArgumentException.class)
    public void testErrorcodegetBad2() throws HoOnException {
    	HoOnResponse r = new HoOnResponse(b.array());
    	r.setErrorCode(0);
    	ErrorCode.getErrorCode(-1);	
    }
    
    @Test (expected =IllegalArgumentException.class)
    public void testErrorcodegetBad3() throws HoOnException {
    	HoOnResponse r = new HoOnResponse(b.array());
    	r.setErrorCode(0);
    	ErrorCode.getErrorCode(8);	
    }
	
	

	
	
	
	
	
	
}
