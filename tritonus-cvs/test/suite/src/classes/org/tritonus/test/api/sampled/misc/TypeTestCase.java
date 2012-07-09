/*
 *	TypeTestCase.java
 */

/*
 *  Copyright (c) 2004 by Matthias Pfisterer
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

//import javax.sound.sampled.AudioFileFormat.Type;



public class TypeTestCase
extends TestCase
{
	public TypeTestCase(String strName)
	{
		super(strName);
	}



// 	public void testEquals()
// 	{
// 		assertTrue("equals(null)", ! Type.ALAW.equals(null));
// 		assertTrue("equals() for same Type",
// 				   Type.ALAW.equals(Type.ALAW));
// 		assertTrue("equals() for different Types",
// 				   ! Type.ALAW.equals(Type.ULAW));
// 		String strTypeName = "my fancy encoding";
// 		String strOtherTypeName = "my other fancy encoding";
// 		Type encoding1 = new Type(strTypeName);
// 		Type encoding2 = new Type(strTypeName);
// 		Type encoding3 = new Type(strOtherTypeName);
// 		assertTrue("equals() for equal custom encodings",
// 				   encoding1.equals(encoding2));
// 		assertTrue("equals() for equal custom encodings",
// 				   encoding2.equals(encoding1));
// 		assertTrue("equals() for different custom encodings",
// 				   ! encoding1.equals(encoding3));
// 		assertTrue("equals() for different custom encodings",
// 				   ! encoding3.equals(encoding1));
// 	}


// 	public void testHashCode()
// 	{
// 		assertEquals("hashCode() for multiple invocations",
// 					 Type.ALAW.hashCode(), Type.ALAW.hashCode());
// 		String strTypeName = "my fancy encoding";
// 		Type encoding1 = new Type(strTypeName);
// 		Type encoding2 = new Type(strTypeName);
// 		assertEquals("hashCode() for same custom Type",
// 					 encoding1.hashCode(), encoding2.hashCode());
// 	}


// 	public void testToString()
// 	{
// 		String strTypeName = "my fancy encoding";
// 		Type encoding = new Type(strTypeName);
// 		assertEquals("toString()", strTypeName, encoding.toString());
// 	}


// 	public void testStaticInstances()
// 	{
// 		assertEquals("PCM_SIGNED.toString()", "PCM_SIGNED",
// 					 Type.PCM_SIGNED.toString());
// 		assertEquals("PCM_UNSIGNED.toString()", "PCM_UNSIGNED",
// 					 Type.PCM_UNSIGNED.toString());
// 		assertEquals("ALAW.toString()", "ALAW",
// 					 Type.ALAW.toString());
// 		assertEquals("ULAW.toString()", "ULAW",
// 					 Type.ULAW.toString());
// 	}
}



/*** TypeTestCase.java ***/
