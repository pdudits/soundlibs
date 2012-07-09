/*
 *	BaseDataOutputStreamTestCase.java
 */

/*
 *  Copyright (c) 2001 - 2004 by Matthias Pfisterer
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

package org.tritonus.test.tritonus.share.sampled.file;

import junit.framework.TestCase;

import org.tritonus.share.sampled.file.TDataOutputStream;

import org.tritonus.test.Util;



public abstract class BaseDataOutputStreamTestCase
extends TestCase
{
	private boolean		m_bSeekable;


	protected BaseDataOutputStreamTestCase(String strName, boolean bSeekable)
	{
		super(strName);
		m_bSeekable = bSeekable;
	}


	protected abstract TDataOutputStream createDataOutputStream()
		throws Exception;

	protected abstract byte[] getWrittenData()
		throws Exception;


	public void testWriting()
		throws Exception
	{
		TDataOutputStream	dataOutputStream = createDataOutputStream();
		checkTDataOutputStream(dataOutputStream);
 		byte[]	abResultingData = getWrittenData();
		// Util.dumpByteArray(abResultingData);
		checkTDataOutputStream2(abResultingData);
	}



	public void testSupportsSeek()
		throws Exception
	{
		TDataOutputStream dataOutputStream = createDataOutputStream();
		assertEquals("seekability", m_bSeekable, dataOutputStream.supportsSeek());
	}



	private void checkTDataOutputStream(TDataOutputStream dataOutputStream)
		throws Exception
	{
		dataOutputStream.writeLittleEndian32(0x12345678);
		dataOutputStream.writeLittleEndian16((short) 0x2345);
		dataOutputStream.close();
	}



	private void checkTDataOutputStream2(byte[] abResultingData)
		throws Exception
	{
 		byte[]	abExpectedData = new byte[]{0x78, 0x56, 0x34, 0x12, 0x45, 0x23};
 		assertTrue(Util.compareByteArrays(abExpectedData, 0, abResultingData, 0, abExpectedData.length));
	}
}



/*** BaseDataOutputStreamTestCase.java ***/
