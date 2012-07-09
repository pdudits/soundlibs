/*
 *	CddaDriveListConnection.java
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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.PrintStream;

import java.net.URL;
import java.net.URLConnection;

import java.util.Iterator;

import org.tritonus.lowlevel.cdda.CddaMidLevel;
import org.tritonus.lowlevel.cdda.CddaUtils;

import org.tritonus.share.TDebug;



public class CddaDriveListConnection
extends	URLConnection
{
	private CddaMidLevel	m_cddaMidLevel;



	// TODO: m_cdda.close();
	public CddaDriveListConnection(URL url)
	{
		super(url);
		if (TDebug.TraceCdda) { TDebug.out("CddaDriveListConnection.<init>(): begin"); }
		if (TDebug.TraceCdda) { TDebug.out("CddaDriveListConnection.<init>(): end"); }
	}



	public void connect()
	{
		if (TDebug.TraceCdda) { TDebug.out("CddaDriveListConnection.connect(): begin"); }
		if (! connected)
		{
			m_cddaMidLevel = CddaUtils.getCddaMidLevel();
			connected = true;
		}
		if (TDebug.TraceCdda) { TDebug.out("CddaDriveListConnection.connect(): end"); }
	}



	public InputStream getInputStream()
		throws IOException
	{
		if (TDebug.TraceCdda) { TDebug.out("CddaDriveListConnection.getInputStream(): begin"); }
		connect();
		Iterator	drivesIterator = m_cddaMidLevel.getDevices();
		ByteArrayOutputStream	baos = new ByteArrayOutputStream();
		PrintStream		out = new PrintStream(baos);
		while (drivesIterator.hasNext())
		{
			String	strDrive = (String) drivesIterator.next();
			out.print(strDrive + "\n");
		}
		byte[]	abData = baos.toByteArray();
		baos.close();
		ByteArrayInputStream	bais = new ByteArrayInputStream(abData);
		if (TDebug.TraceCdda) { TDebug.out("CddaDriveListConnection.getInputStream(): end"); }
		return bais;
	}
}



/*** CddaDriveListConnection.java ****/
