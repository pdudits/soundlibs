/*
 *	DataLineInfoTestCase.java
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

package org.tritonus.test.api.sampled.line;

import junit.framework.TestCase;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.Line;
import javax.sound.sampled.SourceDataLine;



public class DataLineInfoTestCase
extends TestCase
{
	public DataLineInfoTestCase(String strName)
	{
		super(strName);
	}



	public void testConstructors()
	{
		DataLine.Info info;
		info = new DataLine.Info(String.class,
								 new AudioFormat[0],
								 123, 456);
		checkInfo(info, String.class, 0, 123, 456);
	}



	private void checkInfo(DataLine.Info info,
						   Class expectedLineClass,
						   int nExpectedFormatsArrayLength,
						   int nExpectedMinBufferSize,
						   int nExpectedMaxBufferSize)
	{
		assertEquals("lineClass", expectedLineClass, info.getLineClass());
		assertEquals("AudioFormat array length", nExpectedFormatsArrayLength,
					 info.getFormats().length);
		assertEquals("min buffer size", nExpectedMinBufferSize,
					 info.getMinBufferSize());
		assertEquals("max buffer size", nExpectedMaxBufferSize,
					 info.getMaxBufferSize());
	}

	public void testMatches()
	{
		DataLine.Info info1 = new DataLine.Info(SourceDataLine.class,
												null);
		Line.Info info2 = new Line.Info(SourceDataLine.class);
		assertTrue("DataLine.Info against Line.Info", ! info1.matches(info2));
		assertTrue("Line.Info against DataLine.Info", info2.matches(info1));
	}


	public void testToString()
	{
	}
}



/*** DataLineInfoTestCase.java ***/
