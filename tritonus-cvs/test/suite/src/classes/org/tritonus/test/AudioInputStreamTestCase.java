/*
 *	AudioInputStreamTestCase.java
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

package org.tritonus.test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import junit.framework.TestCase;



public class AudioInputStreamTestCase
extends TestCase
{
	public AudioInputStreamTestCase(String strName)
	{
		super(strName);
	}



	public void testConstructorNullPointers()
	{
		@SuppressWarnings("unused") AudioInputStream ais = null;
		InputStream is = new ByteArrayInputStream(new byte[0]);
		AudioFormat format = new AudioFormat(44100.0F, 16, 2, true, false);
		try
		{
			ais = new AudioInputStream(null, format, AudioSystem.NOT_SPECIFIED);
			fail("no NullpointerException thrown for null InputStream");
		}
		catch (NullPointerException e)
		{
		}

		try
		{
			ais = new AudioInputStream(is, null, AudioSystem.NOT_SPECIFIED);
			fail("no NullpointerException thrown for null AudioFormat");
		}
		catch (NullPointerException e)
		{
		}
	}



// 	public void testLength()
// 	{
// 		Map prop = new HashMap();
// 		prop.put("bitrate", new Float(22.5F));
// 		AudioInputStream format = new AudioInputStream(AudioFormat.Encoding.PCM_SIGNED,
// 											   44100.0F, 16, 2, 4, 44100.0F,
// 											   true, prop);
// 		Map propReturn = format.properties();
// 		assertTrue(prop != propReturn);
// 		prop.put("bitrate", new Float(42.5F));
// 		Object result = propReturn.get("bitrate");
// 		assertEquals(new Float(22.5F), result);
// 	}


// 	public void testUnmodifiable()
// 	{
// 		Map prop = new HashMap();
// 		AudioInputStream format = new AudioInputStream(AudioFormat.Encoding.PCM_SIGNED,
// 											   44100.0F, 16, 2, 4, 44100.0F,
// 											   true, prop);
// 		Map propReturn = format.properties();
// 		try
// 		{
// 			propReturn.put("author", "Matthias Pfisterer");
// 			fail("returned Map allows modifications");
// 		}
// 		catch (UnsupportedOperationException e)
// 		{
// 		}
// 	}


// 	public void testGet()
// 	{
// 		Map prop = new HashMap();
// 		prop.put("bitrate", new Float(22.5F));
// 		prop.put("author", "Matthias Pfisterer");
// 		AudioInputStream format = new AudioInputStream(AudioFormat.Encoding.PCM_SIGNED,
// 											   44100.0F, 16, 2, 4, 44100.0F,
// 											   true, prop);
// 		Map propReturn = format.properties();
// 		assertEquals(new Float(22.5F), propReturn.get("bitrate"));
// 		assertEquals("Matthias Pfisterer", propReturn.get("author"));
// 	}
}



/*** AudioInputStreamTestCase.java ***/
