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
import org.junit.Assert;
import org.junit.Test;
import hoon.serialization.HoOnException;
import hoon.serialization.HoOnMessage;
import hoon.serialization.HoOnQuery;
import hoon.serialization.HoOnResponse;

/*
 * HoOnQueryTest
 *
 * 1.0
 *
 * February 24, 2017
 *
 * Copyright
 */
public class HoOnQueryTest {


	private static ByteBuffer b;
	static{
		try{
			byte[] barr = new byte[8];
			b = ByteBuffer.wrap(barr);
			b.position(0);
			b.put((byte) 32);
			b.put((byte) 0);
			b.putInt((int) 2139062143L);
			b.putShort((short) 41234);

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
	public void testNullDecodeResponse() throws HoOnException{
		 HoOnMessage msg = HoOnMessage.decode(null);
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
	public void testMaxDecodeResponse() throws HoOnException{
		byte[] barr = new byte[9];//above max packet size
		ByteBuffer be;
		be = ByteBuffer.wrap(barr);
		be.position(0);
		be.put((byte) 32);
		be.put((byte) 0);
		be.putInt((int) 1234L);
		 HoOnMessage msg = HoOnMessage.decode(be.array());
	}
	
	@Test(expected = HoOnException.class)
	public void testMinDecodeResponse() throws HoOnException{
		byte[] barr = new byte[7];//below min packet size
		ByteBuffer be;
		be = ByteBuffer.wrap(barr);
		be.position(0);
		be.put((byte) 32);
		be.put((byte) 3);
		be.putInt((int) 1243L);
		 HoOnMessage msg = HoOnMessage.decode(be.array());
	}
	@Test
	public void testDecodeQuery() throws HoOnException{
		 HoOnMessage msg = HoOnMessage.decode(b.array());
	}

	@Test (expected=IllegalArgumentException.class)
	public void testNegativeQueryIdConstructor() throws  HoOnException{
		HoOnQuery q = new HoOnQuery(-1L, 1);
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testBigQueryIdConstructor() throws HoOnException{
		HoOnQuery q = new HoOnQuery(4294967296L, 1);
	}
	
	@Test 
	public void testMaxLongConstructor() throws HoOnException{
		HoOnQuery q = new HoOnQuery(4294967295L, 1);
		assertEquals(q.getQueryId(),4294967295L );
	}
	
	@Test (expected=HoOnException.class)
	public void badErrorCodeConstructor() throws HoOnException{
		byte[] b = new byte[8];
		ByteBuffer qBuffer = ByteBuffer.wrap(b);
		qBuffer.position(0);
		qBuffer.put((byte) 32);
		qBuffer.put((byte) 1);
		qBuffer.putInt((int) 2139062143L);
		qBuffer.putShort((short) 1);
		HoOnQuery q = new HoOnQuery(qBuffer.array());

	}
	@Test(expected=HoOnException.class)
	public void nullBuffConstructor() throws HoOnException{
		byte[] b = null;
		HoOnQuery q = new HoOnQuery(b);
	}
	
	@Test(expected=HoOnException.class)
	public void emptyBufferConstructor() throws HoOnException{
		byte[] b = {};
		HoOnQuery q = new HoOnQuery(b);
	}
	@Test
	public void testValidByteConstructor() throws HoOnException{
		byte[] b = new byte[8];
		ByteBuffer qBuffer = ByteBuffer.wrap(b);
		qBuffer.position(0);
		qBuffer.put((byte) 32);
		qBuffer.put((byte) 0);
		qBuffer.putInt((int) 2139062143L);
		qBuffer.putShort((short) 1);
		
		HoOnQuery q = new HoOnQuery(qBuffer.array());
		assertEquals(q.getQueryId(), 2139062143L);
		assertEquals(q.getRequestedPosts(), 1);
	}
	
	@Test
	public void testEncode() throws HoOnException{
		HoOnQuery q = new HoOnQuery(2139062143L, 1);
		byte[] b = new byte[8];
		ByteBuffer qBuffer = ByteBuffer.wrap(b);
		qBuffer.position(0);
		qBuffer.put((byte) 32);
		qBuffer.put((byte) 0);
		qBuffer.putInt((int) 2139062143L);
		qBuffer.putShort((short) 1);

		q.encode();
		Assert.assertArrayEquals(qBuffer.array(), q.encode());
	}
	
	@Test
	public void testToString() throws HoOnException{
		byte[] b = new byte[8];
		ByteBuffer qBuffer = ByteBuffer.wrap(b);
		qBuffer.position(0);
		qBuffer.put((byte) 32);
		qBuffer.put((byte) 0);
		qBuffer.putInt((int) 2139062143L);
		qBuffer.putShort((short) 1);
		
		HoOnQuery q = new HoOnQuery(qBuffer.array());
		assertTrue(q.toString().equals("QueryId: 2139062143\nRequested Posts: 1"));

	}
	@Test
	public void testdoublequerydecode() throws HoOnException{
		byte[] b = new byte[8];
		ByteBuffer qBuffer = ByteBuffer.wrap(b);
		qBuffer.position(0);
		qBuffer.put((byte) 32);
		qBuffer.put((byte) 0);
		qBuffer.putInt((int) 2139062143L);
		qBuffer.putShort((short) 1);
		
		HoOnQuery q = new HoOnQuery(qBuffer.array());
		HoOnQuery aa = new HoOnQuery(2139062143L, 1);
		
		Assert.assertArrayEquals(aa.encode(), q.encode());

		assertTrue(q.toString().equals("QueryId: 2139062143\nRequested Posts: 1"));

	}
	@Test
	public void testGetRequestedPosts() throws HoOnException{
		byte[] b = new byte[8];
		ByteBuffer qBuffer = ByteBuffer.wrap(b);
		qBuffer.position(0);
		qBuffer.put((byte) 32);
		qBuffer.put((byte) 0);
		qBuffer.putInt((int) 2139062143L);
		qBuffer.putShort((short) 1);
		HoOnQuery q = new HoOnQuery(qBuffer.array());
		assertEquals(q.getRequestedPosts(), 1);

	}
	
	@Test (expected = IllegalArgumentException.class)
	public void testBadSetRequestedPosts() throws HoOnException{
		byte[] b = new byte[8];
		ByteBuffer qBuffer = ByteBuffer.wrap(b);
		qBuffer.position(0);
		qBuffer.put((byte) 32);
		qBuffer.put((byte) 0);
		qBuffer.putInt((int) 2435);
		qBuffer.putShort((short) -1);
		HoOnQuery q = new HoOnQuery(qBuffer.array());
		q.setRequestedPosts(2139062143);
	}
	
	@Test (expected = IllegalArgumentException.class)
	public void testBadSetRequestedPosts2() throws HoOnException{
		HoOnQuery q = new HoOnQuery(2139062143L,2139062143 );
	}
	
	@Test
	public void testEquals() throws HoOnException{
		HoOnQuery q = new HoOnQuery(2139062143L,1 );
		
		byte[] b = new byte[8];
		ByteBuffer qBuffer = ByteBuffer.wrap(b);
		qBuffer.position(0);
		qBuffer.put((byte) 32);
		qBuffer.put((byte) 0);
		qBuffer.putInt((int) 2139062143L);
		qBuffer.putShort((short) 1);
		HoOnQuery  qq = new HoOnQuery(qBuffer.array());
		
		
		assertTrue(qq.equals(q));
	}
	
	@Test
	public void testNotEquals() throws HoOnException{
		HoOnQuery q = new HoOnQuery(2139062143L,11 );
		
		byte[] b = new byte[8];
		ByteBuffer qBuffer = ByteBuffer.wrap(b);
		qBuffer.position(0);
		qBuffer.put((byte) 32);
		qBuffer.put((byte) 0);
		qBuffer.putInt((int) 2139062143L);
		qBuffer.putShort((short) 1);
		HoOnQuery  qq = new HoOnQuery(qBuffer.array());
		
		assertFalse(qq.equals(q));
	}
	
	
    //Test hashCode
    @Test
    public void testHashCode() throws HoOnException {
		
		HoOnQuery q = new HoOnQuery(2139062143L,1 );
		
		byte[] b = new byte[8];
		ByteBuffer qBuffer = ByteBuffer.wrap(b);
		qBuffer.position(0);
		qBuffer.put((byte) 32);
		qBuffer.put((byte) 0);
		qBuffer.putInt((int) 2139062143L);
		qBuffer.putShort((short) 1);
		HoOnQuery  qq = new HoOnQuery(qBuffer.array());
		
		
		assertTrue(qq.hashCode() == q.hashCode());
    }
	
	
	
	
}
