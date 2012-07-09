/*
 *	AudioFormatTestCase.java
 */

/*
 *  Copyright (c) 2003 - 2004 by Matthias Pfisterer
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

package org.tritonus.test.api.sampled.misc;

import junit.framework.TestCase;

import java.util.HashMap;
import java.util.Map;

import javax.sound.sampled.AudioFormat;



public class AudioFormatTestCase
extends TestCase
{
	public AudioFormatTestCase(String strName)
	{
		super(strName);
	}



	public void testNoMap()
	{
		AudioFormat fileFormat = new AudioFormat(
			null, 0.0F, 0, 0, 0, 0.0F, false);
		Map<String, Object> propReturn = fileFormat.properties();
		assertNotNull(propReturn);
		assertTrue(propReturn.isEmpty());
		Object result = propReturn.get("bitrate");
		assertNull(result);
	}


	public void testNullMap()
	{
		AudioFormat fileFormat = new AudioFormat(
			null, 0.0F, 0, 0, 0, 0.0F, false,
			null);
		Map<String, Object> propReturn = fileFormat.properties();
		assertTrue(propReturn.isEmpty());
		Object result = propReturn.get("bitrate");
		assertNull(result);
	}


	public void testEmptyMap()
	{
		Map<String, Object> prop = new HashMap<String, Object>();
		AudioFormat format = new AudioFormat(
			null, 0.0F, 0, 0, 0, 0.0F, false, prop);
		Map<String, Object> propReturn = format.properties();
		assertTrue(propReturn.isEmpty());
		Object result = propReturn.get("bitrate");
		assertNull(result);
	}



	public void testCopying()
	{
		Map<String, Object> prop = new HashMap<String, Object>();
		prop.put("bitrate", new Float(22.5F));
		AudioFormat format = new AudioFormat(
			null, 0.0F, 0, 0, 0, 0.0F, false, prop);
		Map<String, Object> propReturn = format.properties();
		assertTrue(prop != propReturn);
		prop.put("bitrate", new Float(42.5F));
		Object result = propReturn.get("bitrate");
		assertEquals(new Float(22.5F), result);
	}


	public void testUnmodifiable()
	{
		Map<String, Object> prop = new HashMap<String, Object>();
		AudioFormat format = new AudioFormat(
			null, 0.0F, 0, 0, 0, 0.0F, false, prop);
		Map<String, Object> propReturn = format.properties();
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
		AudioFormat format = new AudioFormat(
			null, 0.0F, 0, 0, 0, 0.0F, false, prop);
		Map<String, Object> propReturn = format.properties();
		assertEquals(new Float(22.5F), propReturn.get("bitrate"));
		assertEquals("Matthias Pfisterer", propReturn.get("author"));
	}
}



/*** AudioFormatTestCase.java ***/
