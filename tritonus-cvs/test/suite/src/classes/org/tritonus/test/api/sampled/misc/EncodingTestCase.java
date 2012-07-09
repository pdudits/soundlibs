/*
 *	EncodingTestCase.java
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

package org.tritonus.test.api.sampled.misc;

import junit.framework.TestCase;

import javax.sound.sampled.AudioFormat.Encoding;



public class EncodingTestCase
extends TestCase
{
	public EncodingTestCase(String strName)
	{
		super(strName);
	}



	public void testEquals()
	{
		assertTrue("equals(null)", ! Encoding.ALAW.equals(null));
		assertTrue("equals() for same Encoding",
				   Encoding.ALAW.equals(Encoding.ALAW));
		assertTrue("equals() for different Encodings",
				   ! Encoding.ALAW.equals(Encoding.ULAW));
		String strEncodingName = "my fancy encoding";
		String strOtherEncodingName = "my other fancy encoding";
		Encoding encoding1 = new Encoding(strEncodingName);
		Encoding encoding2 = new Encoding(strEncodingName);
		Encoding encoding3 = new Encoding(strOtherEncodingName);
		assertTrue("equals() for equal custom encodings",
				   encoding1.equals(encoding2));
		assertTrue("equals() for equal custom encodings",
				   encoding2.equals(encoding1));
		assertTrue("equals() for different custom encodings",
				   ! encoding1.equals(encoding3));
		assertTrue("equals() for different custom encodings",
				   ! encoding3.equals(encoding1));
	}


	public void testHashCode()
	{
		assertEquals("hashCode() for multiple invocations",
					 Encoding.ALAW.hashCode(), Encoding.ALAW.hashCode());
		String strEncodingName1 = "my fancy encoding";
		String strEncodingName2 = "my fancy encoding";
		Encoding encoding1 = new Encoding(strEncodingName1);
		Encoding encoding2 = new Encoding(strEncodingName2);
		assertEquals("hashCode() for same custom Encoding",
					 encoding1.hashCode(), encoding2.hashCode());
	}


	public void testToString()
	{
		String strEncodingName = "my fancy encoding";
		Encoding encoding = new Encoding(strEncodingName);
		assertEquals("toString()", strEncodingName, encoding.toString());
	}


	public void testStaticInstances()
	{
		assertEquals("PCM_SIGNED.toString()", "PCM_SIGNED",
					 Encoding.PCM_SIGNED.toString());
		assertEquals("PCM_UNSIGNED.toString()", "PCM_UNSIGNED",
					 Encoding.PCM_UNSIGNED.toString());
		assertEquals("ALAW.toString()", "ALAW",
					 Encoding.ALAW.toString());
		assertEquals("ULAW.toString()", "ULAW",
					 Encoding.ULAW.toString());
	}
}



/*** EncodingTestCase.java ***/
