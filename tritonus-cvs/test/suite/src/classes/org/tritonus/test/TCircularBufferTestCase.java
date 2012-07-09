/*
 *	TCircularBufferTestCase.java
 */

/*
 *  Copyright (c) 2001 - 2002 by Matthias Pfisterer <Matthias.Pfisterer@web.de>
 *
 *   This program is free software; you can redistribute it and/or modify
 *   it under the terms of the GNU Library General Public License as published
 *   by the Free Software Foundation; either version 2 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Library General Public License for more details.
 *
 *   You should have received a copy of the GNU Library General Public
 *   License along with this program; if not, write to the Free Software
 *   Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */

package org.tritonus.test;

import junit.framework.TestCase;

import org.tritonus.share.TCircularBuffer;



public class TCircularBufferTestCase
extends TestCase
{
	public TCircularBufferTestCase(String strName)
	{
		super(strName);
	}



	public void testBufferSize()
	{
		int	nSize = 45678;
		TCircularBuffer	buffer = new TCircularBuffer(
			nSize, false, false, null);
		assertEquals("buffer size", nSize, buffer.availableWrite());
		nSize = 0;
		buffer = new TCircularBuffer(
			nSize, false, false, null);
		assertEquals("buffer size", nSize, buffer.availableWrite());
	}



	public void testAvailable()
	{
		int	nBufferSize = 45678;
		int	nWriteSize1 = nBufferSize / 2;
		int	nWriteSize2 = nBufferSize / 5;
		int	nReadSize1 = nBufferSize / 10;
		int	nReadSize2 = nBufferSize / 3;
		TCircularBuffer	buffer = new TCircularBuffer(
			nBufferSize, true, true, null);
		assertEquals("availableWrite()", nBufferSize, buffer.availableWrite());
		assertEquals("availableRead()", 0, buffer.availableRead());
		buffer.write(new byte[nBufferSize]);
		assertEquals("availableWrite()", 0, buffer.availableWrite());
		assertEquals("availableRead()", nBufferSize, buffer.availableRead());
		buffer.read(new byte[nBufferSize]);
		assertEquals("availableWrite()", nBufferSize, buffer.availableWrite());
		assertEquals("availableRead()", 0, buffer.availableRead());


		buffer.write(new byte[nWriteSize1]);
		assertEquals("availableWrite()", nBufferSize - nWriteSize1, buffer.availableWrite());
		assertEquals("availableRead()", nWriteSize1, buffer.availableRead());
		buffer.write(new byte[nWriteSize2]);
		assertEquals("availableWrite()", nBufferSize - nWriteSize1 - nWriteSize2, buffer.availableWrite());
		assertEquals("availableRead()", nWriteSize1 + nWriteSize2, buffer.availableRead());
		buffer.read(new byte[nReadSize1]);
		assertEquals("availableWrite()", nBufferSize - nWriteSize1 - nWriteSize2 + nReadSize1, buffer.availableWrite());
		assertEquals("availableRead()", nWriteSize1 + nWriteSize2 - nReadSize1, buffer.availableRead());
		buffer.read(new byte[nReadSize2]);
		assertEquals("availableWrite()", nBufferSize - nWriteSize1 - nWriteSize2 + nReadSize1 + nReadSize2, buffer.availableWrite());
		assertEquals("availableRead()", nWriteSize1 + nWriteSize2 - nReadSize1 - nReadSize2, buffer.availableRead());
	}


	public void testReadWrite()
	{
		int	nBufferSize = 8901 * 4;
		int	nResult;
		byte[]	abWriteArray = new byte[nBufferSize];
		byte[]	abReadArray = new byte[nBufferSize];
		TCircularBuffer	buffer = new TCircularBuffer(
			nBufferSize, true, true, null);
		for (int i = 0 ; i < abWriteArray.length; i++)
		{
			abWriteArray[i] = (byte)(i % 256);
		}
		nResult = buffer.write(abWriteArray);
		assertEquals("written length", abWriteArray.length, nResult);
		nResult = buffer.read(abReadArray);
		assertEquals("read length", abReadArray.length, nResult);
		assertTrue("data content", Util.compareByteArrays(abReadArray, 0, abWriteArray, 0, abReadArray.length));

		buffer.write(new byte[nBufferSize / 3]);
		nResult = buffer.write(abWriteArray, nBufferSize / 4, nBufferSize / 2);
		assertEquals("written length", nBufferSize / 2, nResult);
		buffer.read(new byte[nBufferSize / 3]);
		nResult = buffer.read(abReadArray, 0, nBufferSize / 2);
		assertEquals("read length", nBufferSize / 2, nResult);
		assertTrue("data content", Util.compareByteArrays(abReadArray, 0, abWriteArray, nBufferSize / 4, nBufferSize / 2));
	}


	public void testTrigger()
	{
		TestTrigger	trigger = new TestTrigger();
		
		int	nBufferSize = 45678;
		TCircularBuffer	buffer = new TCircularBuffer(
			nBufferSize, false, true, trigger);
		buffer.read(new byte[10]);
		assertTrue("trigger called", trigger.isCalled());

		trigger.reset();
		buffer.write(new byte[nBufferSize / 3]);
		buffer.read(new byte[nBufferSize / 2]);
		assertTrue("trigger called", trigger.isCalled());
	}


	public void testClose()
	{
		int	nResult;
		int	nBufferSize = 45678;
		TestTrigger	trigger = new TestTrigger();
		TCircularBuffer	buffer = new TCircularBuffer(
			nBufferSize, true, true, trigger);
		buffer.write(new byte[nBufferSize / 2]);
		assertEquals("availableWrite()", nBufferSize / 2, buffer.availableWrite());
		assertEquals("availableRead()", nBufferSize / 2, buffer.availableRead());
		buffer.close();
		assertEquals("availableWrite()", nBufferSize / 2, buffer.availableWrite());
		assertEquals("availableRead()", nBufferSize / 2, buffer.availableRead());
		nResult = buffer.read(new byte[nBufferSize / 2]);
		assertEquals("read length", nBufferSize / 2, nResult);
		assertEquals("availableWrite()", nBufferSize, buffer.availableWrite());
		assertEquals("availableRead()", 0, buffer.availableRead());
		nResult = buffer.read(new byte[nBufferSize / 2]);
		assertEquals("read length", -1, nResult);
		assertTrue("trigger invocation", !trigger.isCalled());
	}




	private static class TestTrigger
	implements TCircularBuffer.Trigger
	{
		private boolean	m_bCalled = false;


		public void execute()
		{
			m_bCalled = true;
		}


		public boolean isCalled()
		{
			return m_bCalled;
		}



		public void reset()
		{
			m_bCalled = false;
		}
	}
}



/*** TCircularBufferTestCase.java ***/
