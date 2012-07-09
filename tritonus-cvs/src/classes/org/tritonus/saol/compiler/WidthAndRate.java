/*
 *	WidthAndRate.java
 *
 *	This file is part of Tritonus: http://www.tritonus.org/
 */

/*
 *  Copyright (c) 2002 by Matthias Pfisterer
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

package org.tritonus.saol.compiler;



public class WidthAndRate
{
	public static final int		WIDTH_UNKNOWN = -1;
	public static final int		WIDTH_INCHANNELS = -2;
	public static final int		WIDTH_OUTCHANNELS = -3;

	// do not change arbitrarily; InstrumentCompilation depends on it!
	public static final int		RATE_UNKNOWN = 0;
	public static final int		RATE_I = 1;
	public static final int		RATE_K = 2;
	public static final int		RATE_A = 3;
	public static final int		RATE_X = 4;
	// not really rates...
	public static final int		RATE_TABLE = 5;
	public static final int		RATE_OPARRAY = 6;



	private int		m_nWidth;
	private int		m_nRate;



	public WidthAndRate(int nWidth, int nRate)
	{
		m_nWidth = nWidth;
		m_nRate = nRate;
	}



	public int getWidth()
	{
		return m_nWidth;
	}



	public int getRate()
	{
		return m_nRate;
	}
}



/*** WidthAndRate.java ***/
