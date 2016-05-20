/*
 *	CompoundControl.java
 *
 *	This file is part of Tritonus: http://www.tritonus.org/
 */

/*
 *  Copyright (c) 1999 - 2004 by Matthias Pfisterer
 *
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
 *
 */

/*
|<---            this code is formatted to fit into 80 columns             --->|
*/

package javax.sound.sampled;

import java.util.Arrays;
import java.util.Collection;

import org.tritonus.share.TDebug;



public abstract class CompoundControl
extends Control
{
	private static final Control[]	EMPTY_CONTROL_ARRAY = new Control[0];

	private Collection<Control>	m_memberControls;



	protected CompoundControl(Type type,
				 Control[] aMemberControls)
	{
		super(type);
		if (TDebug.TraceControl) TDebug.out("CompoundControl.<init>: begin");
		m_memberControls = Arrays.asList(aMemberControls);
		if (TDebug.TraceControl) TDebug.out("CompoundControl.<init>: end");
	}



	public Control[] getMemberControls()
	{
		return m_memberControls.toArray(EMPTY_CONTROL_ARRAY);
	}



	public String toString()
	{
		// TODO:
		return super.toString() + " components: " + "";
	}





	public static class Type
	extends Control.Type
	{
		protected Type(String strName)
		{
			super(strName);
		}
	}
}



/*** CompoundControl.java ***/
