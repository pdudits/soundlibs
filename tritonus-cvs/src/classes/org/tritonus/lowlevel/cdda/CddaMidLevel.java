/*
 *	CddaMidLevel.java
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

package org.tritonus.lowlevel.cdda;

import java.io.InputStream;
import java.io.IOException;
import java.util.Iterator;
import javax.sound.sampled.AudioInputStream;



/**	Mid-level interface definition for reading CDs
 */
public interface CddaMidLevel
{
	/**	Size of a cdda frame in bytes.
	 */
	public static final int		FRAME_SIZE = 2352;


	// TODO: document!!
	/**	Gives the available CDROM devices.
		The returned iteration should contain a list of Strings.
		Each String represents an internal name of a CDROM drive.
		This String should be considered implementation-specific.
		It may contain no useful information (however, most
		time it does). Currently, it is required that the
		String starts with exactely one '/'.
		Should only those drives returned that have an audio CD in?
	 */
	public Iterator getDevices();


	/**	Gives the default drive.
		A String should be returned that represents the default drive.
		The String has to follow the conventions described in
		getDevices(). The String returned by this method should also
		appear as one of the elements in the iteration returned
		by getDevices().
	 */
	public String getDefaultDevice();


	public InputStream getTocAsXml(String strDevice)
		throws IOException;

	public AudioInputStream getTrack(String strDevice, int nTrack)
		throws IOException;
}



/*** CddaMidLevel.java ***/
