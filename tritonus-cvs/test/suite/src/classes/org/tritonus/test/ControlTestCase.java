/*
 *	ControlTestCase.java
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
import javax.sound.sampled.BooleanControl;



/**	Tests for class javax.sound.sampled.Control.
 */
public class ControlTestCase
extends TestCase
{
	public ControlTestCase(String strName)
	{
		super(strName);
	}



	/**	Checks getType().
		The test checks if the object returned by
		getType() is the one passed to the constructor.
	*/
	public void testGetTypeObject()
		throws Exception
	{
		Control.Type	type = BooleanControl.Type.MUTE;
		Control		control = new TestControl(type);
		Control.Type	returnedType = control.getType();
		assertEquals("type object", type, returnedType);
	}



	/**	Checks getType().
		The test checks if the object returned by
		getType() is null, as is passed to the constructor.
	*/
	public void testGetTypeNull()
		throws Exception
	{
		Control.Type	type = null;
		Control		control = new TestControl(type);
		Control.Type	returnedType = control.getType();
		assertEquals("type object (null)", type, returnedType);
	}



	/**	Checks toString().
		The test checks if the string returned by toString()
		contains characters (and doesn't throw an exception).
	*/
	public void testToString()
		throws Exception
	{
		Control.Type	type = BooleanControl.Type.MUTE;
		Control		control = new TestControl(type);
		String		strReturnedString = control.toString();
		assertTrue("toString() result", strReturnedString.length() > 0);
	}



	/**	Inner class used to get around protected constructor.
	 */
	private class TestControl
	extends Control
	{
		public TestControl(Control.Type type)
		{
			super(type);
		}
	}
}



/*** ControlTestCase.java ***/
