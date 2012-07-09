/*
 *	CddaTocConnection.java
 *
 *	This file is part of Tritonus: http://www.tritonus.org/
 */

/*
 *  Copyright (c) 2001 - 2002 by Matthias Pfisterer
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

package org.tritonus.sampled.cdda;

import java.io.InputStream;
import java.io.IOException;

import java.net.URL;
import java.net.URLConnection;

import org.tritonus.lowlevel.cdda.CddaMidLevel;
import org.tritonus.lowlevel.cdda.CddaUtils;

import org.tritonus.share.TDebug;



public class CddaTocConnection
extends	URLConnection
{
	/**	The cdda device name to read from.
	 */
	private String		m_strDevice;

	private CddaMidLevel	m_cddaMidLevel;



	// TODO: m_cdda.close();
	public CddaTocConnection(URL url)
	{
		super(url);
		if (TDebug.TraceCdda) { TDebug.out("CddaTocConnection.<init>(): begin"); }
		m_strDevice = url.getPath();
		if (TDebug.TraceCdda) { TDebug.out("CddaTocConnection.<init>(): end"); }
	}



	public void connect()
	{
		if (TDebug.TraceCdda) { TDebug.out("CddaTocConnection.connect(): begin"); }
		if (! connected)
		{
			m_cddaMidLevel = CddaUtils.getCddaMidLevel();
			if (m_strDevice.equals(""))
			{
				m_strDevice = m_cddaMidLevel.getDefaultDevice();
			}
			connected = true;
		}
		if (TDebug.TraceCdda) { TDebug.out("CddaTocConnection.connect(): end"); }
	}



	public InputStream getInputStream()
		throws IOException
	{
		if (TDebug.TraceCdda) { TDebug.out("CddaTocConnection.getInputStream(): begin"); }
		connect();
		String	strDevice = getDevice();
		InputStream	inputStream = m_cddaMidLevel.getTocAsXml(strDevice);
		if (TDebug.TraceCdda) { TDebug.out("CddaTocConnection.getInputStream(): end"); }
		return inputStream;
	}



	private String getDevice()
	{
		return m_strDevice;
	}
}



/*** CddaTocConnection.java ****/
