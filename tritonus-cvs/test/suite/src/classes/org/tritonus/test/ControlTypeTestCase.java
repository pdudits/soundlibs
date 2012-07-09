/*
 *	ControlTypeTestCase.java
 */

/*
 *  Copyright (c) 2002 by Matthias Pfisterer <Matthias.Pfisterer@web.de>
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

import javax.sound.sampled.Control;
// import javax.sound.sampled.BooleanControl;



/**	Tests for class javax.sound.sampled.Control.
 */
public class ControlTypeTestCase
extends TestCase
{
	public ControlTypeTestCase(String strName)
	{
		super(strName);
	}



	/**	Checks the constructor().
		The test checks if the constructor does not throw an
		exception.
	*/
	public void testConstructor()
		throws Exception
	{
		String	strTypeName = "TeSt";
		@SuppressWarnings("unused") Control.Type	type =
			new TestControlType(strTypeName);
	}



	/**	Checks equals().
		The test checks if an object is considered equal to
		itself.
	*/
	public void testEqualsSelfIdentity()
		throws Exception
	{
		String	strTypeName = "TeSt";
		Control.Type	type = new TestControlType(strTypeName);
		assertTrue("self-identity", type.equals(type));
	}



	/**	Checks equals().
		The test checks if two objects are considered unequal,
		even if they have the same type string.
	*/
	public void testEqualsSelfUnequality()
		throws Exception
	{
		String	strTypeName = "TeSt";
		Control.Type	type0 = new TestControlType(strTypeName);
		Control.Type	type1 = new TestControlType(strTypeName);
		assertTrue("unequality", ! type0.equals(type1));
	}



	/**	Checks hashCode().
		The test checks if two calls to hashCode() on the
		same object return the same value.
	*/
	public void testHashCode()
		throws Exception
	{
		String	strTypeName = "TeSt";
		Control.Type	type = new TestControlType(strTypeName);
		assertTrue("hash code", type.hashCode() == type.hashCode());
	}



	/**	Checks toString().
		The test checks if the string returned by toString()
		equals the one passed in the constructor
		(and doesn't throw an exception).
	*/
	public void testToString()
		throws Exception
	{
		String	strTypeName = "TeSt";
		Control.Type	type = new TestControlType(strTypeName);
		String		strReturnedTypeName = type.toString();
		assertEquals("toString() result", strTypeName, strReturnedTypeName);
	}



	/**	Inner class used to get around protected constructor.
	 */
	private class TestControlType
	extends Control.Type
	{
		public TestControlType(String strName)
		{
			super(strName);
		}
	}
}



/*** ControlTypeTestCase.java ***/
