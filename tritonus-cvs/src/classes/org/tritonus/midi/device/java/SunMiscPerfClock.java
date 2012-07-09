/*
 *	SunMiscPerfClock.java
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

import sun.misc.Perf;


/** Sequencer clock based on sun.misc.Perf.
	Sun JDK 1.4.2 or later is required to compile this class.
 */
public class SunMiscPerfClock
implements JavaSequencer.Clock
{
	private Perf m_perf;
	private long m_lTicksPerSecond;


	public SunMiscPerfClock()
	{
		m_perf = Perf.getPerf(); // may throw SecurityException
		m_lTicksPerSecond = m_perf.highResFrequency();
	}


	/**	Retrieve system time in microseconds.
		This method retrieves the time by calling
		{@link sun.misc.Perf}.

		@return the system time in microseconds
	*/
	public long getMicroseconds()
	{
		return (m_perf.highResCounter() * 1000000) / m_lTicksPerSecond;
	}
}



/*** SunMiscPerfClock.java ***/
