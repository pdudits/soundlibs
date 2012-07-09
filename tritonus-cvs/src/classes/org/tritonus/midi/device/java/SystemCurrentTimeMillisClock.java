/*
 *	SystemCurrentTimeMillisClock.java
 *
 *	This file is part of Tritonus: http://www.tritonus.org/
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

/*
|<---            this code is formatted to fit into 80 columns             --->|
*/

package org.tritonus.midi.device.java;



/** Sequencer clock based on System.currentTimeMillis().
 */
public class SystemCurrentTimeMillisClock
implements JavaSequencer.Clock
{
	/**	Retrieve system time in microseconds.
		This method retrieves the time by calling
		{@link java.lang.System#currentTimeMillis}.

		@return the system time in microseconds
	*/
	public long getMicroseconds()
	{
		return System.currentTimeMillis() * 1000;
	}
}



/*** SystemCurrentTimeMillisClock.java ***/
