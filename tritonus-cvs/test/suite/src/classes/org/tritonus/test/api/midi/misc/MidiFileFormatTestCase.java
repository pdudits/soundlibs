/*
 *	MidiFileFormatTestCase.java
 */

/*
 *  Copyright (c) 2003 by Matthias Pfisterer
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

package org.tritonus.test.api.midi.misc;

import junit.framework.TestCase;

import java.util.HashMap;
import java.util.Map;
import javax.sound.midi.MidiFileFormat;



public class MidiFileFormatTestCase
extends TestCase
{
	private static final float DELTA = 1E-9F;


	public MidiFileFormatTestCase(String strName)
	{
		super(strName);
	}


	public void testGetValues()
	{
		checkGetValues(0, 0.0F, 0, 0, 0L, false);
		checkGetValues(0, 0.0F, 0, 0, 0L, true);
		checkGetValues(2, -1.0F, 25, 725, 600000L, false);
		checkGetValues(2, -1.0F, 25, 725, 600000L, true);
	}


	private void checkGetValues(int nType, float fDivisionType,
								int nResolution, int nByteLength,
								long lMicrosecondLength, boolean bWithMap)
	{
		MidiFileFormat fileFormat;
		if (bWithMap)
		{
			Map<String, Object> prop = new HashMap<String, Object>();
			fileFormat = new MidiFileFormat(nType, fDivisionType,
											nResolution, nByteLength,
											lMicrosecondLength, prop);
		}
		else
		{
			fileFormat = new MidiFileFormat(nType, fDivisionType,
											nResolution, nByteLength,
											lMicrosecondLength);
		}
		assertEquals("type", nType, fileFormat.getType());
		assertEquals("division type", fDivisionType, fileFormat.getDivisionType(), DELTA);
		assertEquals("resolution", nResolution, fileFormat.getResolution());
		assertEquals("byte length", nByteLength, fileFormat.getByteLength());
		assertEquals("microsecond length", lMicrosecondLength, fileFormat.getMicrosecondLength());
	}


	public void testNoMap()
	{
		MidiFileFormat fileFormat = new MidiFileFormat(0, 0.0F, 0, 0, 0L);
		Map<String, Object> propReturn = fileFormat.properties();
		assertNotNull(propReturn);
		assertTrue(propReturn.isEmpty());
		Object result = propReturn.get("bitrate");
		assertNull(result);
	}


	public void testNullMap()
	{
		MidiFileFormat fileFormat = new MidiFileFormat(0, 0.0F, 0, 0, 0L,
													   null);
		Map<String, Object> propReturn = fileFormat.properties();
		assertTrue(propReturn.isEmpty());
		Object result = propReturn.get("bitrate");
		assertNull(result);
	}


	public void testEmptyMap()
	{
		Map<String, Object> prop = new HashMap<String, Object>();
		MidiFileFormat fileFormat = new MidiFileFormat(0, 0.0F, 0, 0, 0L,
													   prop);
		Map<String, Object> propReturn = fileFormat.properties();
		assertTrue(propReturn.isEmpty());
		Object result = propReturn.get("bitrate");
		assertNull(result);
	}


	public void testCopying()
	{
		Map<String, Object> prop = new HashMap<String, Object>();
		prop.put("bitrate", new Float(22.5F));
		MidiFileFormat fileFormat = new MidiFileFormat(0, 0, 0, 0, 0,
													   prop);
		Map<String, Object> propReturn = fileFormat.properties();
		assertTrue(prop != propReturn);
		prop.put("bitrate", new Float(42.5F));
		Object result = propReturn.get("bitrate");
		assertEquals(new Float(22.5F), result);
	}


	public void testUnmodifiable()
	{
		Map<String, Object> prop = new HashMap<String, Object>();
		MidiFileFormat fileFormat = new MidiFileFormat(0, 0.0F, 0, 0, 0L,
													   prop);
		Map<String, Object> propReturn = fileFormat.properties();
		try
		{
			propReturn.put("author", "Matthias Pfisterer");
			fail("returned Map allows modifications");
		}
		catch (UnsupportedOperationException e)
		{
		}
	}


	public void testGet()
	{
		Map<String, Object> prop = new HashMap<String, Object>();
		prop.put("bitrate", new Float(22.5F));
		prop.put("author", "Matthias Pfisterer");
		MidiFileFormat fileFormat = new MidiFileFormat(0, 0.0F, 0, 0, 0L,
													   prop);
		Map<String, Object> propReturn = fileFormat.properties();
		assertEquals(new Float(22.5F), propReturn.get("bitrate"));
		assertEquals("Matthias Pfisterer", propReturn.get("author"));
	}
}



/*** MidiFileFormatTestCase.java ***/
