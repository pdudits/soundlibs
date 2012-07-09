/*
 *	CookedIoctl.java
 *
 *	This file is part of Tritonus: http://www.tritonus.org/
 */

/*
 *  Copyright (c) 2001 by Matthias Pfisterer
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

package org.tritonus.lowlevel.cdda.cooked_ioctl;

import org.tritonus.share.TDebug;



/**	Reading audio CDs using the 'cooked ioctl' interface.
 */
public class CookedIoctl
{
	static
	{
		if (TDebug.TraceCdda) { TDebug.out("CookedIoctl.<clinit>(): loading native library tritonuscooked_ioctl"); }
		System.loadLibrary("tritonuscooked_ioctl");
		if (TDebug.TraceCdda) { TDebug.out("CookedIoctl.<clinit>(): loaded"); }
		// TODO: ????
		setTrace(TDebug.TraceCddaNative);
	}



	/*
	 *	This holds a file descriptor for the native code -
	 *	do not touch!
	 */
	@SuppressWarnings("unused")
	private long		m_lNativeHandle;



	// TODO: parameter strDevicename (or something else sensible)
	public CookedIoctl(String strDevice)
	{
		if (TDebug.TraceCdda) { System.out.println("CookedIoctl.<init>: begin"); }
		int	nResult = open(strDevice);
		if (nResult < 0)
		{
			throw new RuntimeException("cannot open" + strDevice);
		}
		if (TDebug.TraceCdda) { System.out.println("CookedIoctl.<init>: end"); }
	}



	/**	Opens the device.
	 */
	private native int open(String strDevice);

	/**	Closes the device.
	 */
	public native void close();


	/*
	 *	anValues[0]	first track
	 *	anValues[1]	last track
	 *
	 *	anStartTrack[x]	start sector of the track x.
	 *	anType[x]	type of track x.
	 */
	public native int readTOC(int[] anValues,
			   int[] anStartFrame,
			   int[] anLength,
			   int[] anType,
			   boolean[] abCopy,
			   boolean[] abPre,
			   int[] anChannels);



	/**	Reads one or more raw frames from the CD.
		This call reads <CODE>nCount</CODE> frames starting at
		lba position <CODE>nFrame</CODE>.
		<CODE>abData</CODE>  has to be big enough to hold the
		amount of data requested (<CODE>2352 * nCount</CODE> bytes).
	 */
	public native int readFrame(int nFrame, int nCount, byte[] abData);

	private static native void setTrace(boolean bTrace);
}



/*** CookedIoctl.java ***/
