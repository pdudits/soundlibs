/*
 *	SAOLGlobals.java
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




public class SAOLGlobals
{
	private static final int		DEFAULT_ARATE = 32000;
	private static final int		DEFAULT_KRATE = 100;
	private static final int		DEFAULT_INCHANNELS = 0; /*?? TODO: */
	private static final int		DEFAULT_OUTCHANNELS = 1;
	private static final int		DEFAULT_INTERP = 0;

	private int		m_nARate;
	private int		m_nKRate;
	private int		m_nInChannels;
	private int		m_nOutChannels;
	private int		m_nInterp;



	public SAOLGlobals()
	{
		this(DEFAULT_ARATE,
		     DEFAULT_KRATE,
		     DEFAULT_INCHANNELS,
		     DEFAULT_OUTCHANNELS,
		     DEFAULT_INTERP);
	}


	private SAOLGlobals(int nDefaultARate,
						int nDefaultKRate,
						int nDefaultInChannels,
						int nDefaultOutChannels,
						int nDefaultInterp)
	{
		m_nARate = nDefaultARate;
		m_nKRate = nDefaultKRate;
		m_nInChannels = nDefaultInChannels;
		m_nOutChannels = nDefaultOutChannels;
		m_nInterp = nDefaultInterp;
	}



	public void setARate(int nARate)
	{
		m_nARate = nARate;
	}



	public int getARate()
	{
		return m_nARate;
	}



	public void setKRate(int nKRate)
	{
		m_nKRate = nKRate;
	}



	public int getKRate()
	{
		return m_nKRate;
	}



	public void setInChannels(int nInChannels)
	{
		m_nInChannels = nInChannels;
	}



	public int getInChannels()
	{
		return m_nInChannels;
	}



	public void setOutChannels(int nOutChannels)
	{
		m_nOutChannels = nOutChannels;
	}



	public int getOutChannels()
	{
		return m_nOutChannels;
	}



	public void setInterp(int nInterp)
	{
		m_nInterp = nInterp;
	}



	public int getInterp()
	{
		return m_nInterp;
	}
}



/*** SAOLGlobals.java ***/
