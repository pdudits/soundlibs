/*
 *	AudioSystemShadowTestCase.java
 */

/*
 *  Copyright (c) 2001 - 2002 by Matthias Pfisterer
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

package org.tritonus.test.tritonus.share.sampled;

import java.io.ByteArrayOutputStream;
import java.io.File;

import junit.framework.TestCase;

import org.tritonus.share.sampled.AudioSystemShadow;
import org.tritonus.share.sampled.file.TDataOutputStream;

import org.tritonus.test.Util;



public class AudioSystemShadowTestCase
extends TestCase
{
	public AudioSystemShadowTestCase(String strName)
	{
		super(strName);
	}


	public void testGetDataOutputStreamFile()
		throws Exception
	{
		File file = new File("/tmp/dataoutputstream.tmp");
		TDataOutputStream	dataOutputStream = AudioSystemShadow.getDataOutputStream(file);
		checkTDataOutputStream(dataOutputStream, true);
 		byte[]	abResultingData = Util.getByteArrayFromFile(file);
		// Util.dumpByteArray(abResultingData);
		checkTDataOutputStream2(abResultingData);
	}



	public void testGetDataOutputStreamOutputStream()
		throws Exception
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		TDataOutputStream	dataOutputStream = AudioSystemShadow.getDataOutputStream(baos);
		checkTDataOutputStream(dataOutputStream, false);
 		byte[]	abResultingData = baos.toByteArray();
		// Util.dumpByteArray(abResultingData);
		checkTDataOutputStream2(abResultingData);
	}



	private void checkTDataOutputStream(TDataOutputStream dataOutputStream,
										boolean bSeekable)
		throws Exception
	{
		assertNotNull(dataOutputStream);
		assertEquals("seekable", bSeekable, dataOutputStream.supportsSeek());
		dataOutputStream.writeLittleEndian32(0x12345678);
		dataOutputStream.writeLittleEndian16((short) 0x2345);
		dataOutputStream.close();
	}



	private void checkTDataOutputStream2(byte[] abResultingData)
		throws Exception
	{
 		byte[]	abExpectedData = new byte[]{0x78, 0x56, 0x34, 0x12, 0x45, 0x23};
 		assertTrue("data ok", Util.compareByteArrays(abExpectedData, 0, abResultingData, 0, abExpectedData.length));
	}
}



/*** AudioSystemShadowTestCase.java ***/
