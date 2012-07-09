/*
 *	VorbisConstants.java
 *
 *	This file is part of Tritonus: http://www.tritonus.org/
 */

/*
 *  Copyright (c) 2005 by Matthias Pfisterer
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

package org.tritonus.lowlevel.pvorbis;


/** Constants, especially error codes from vorbis/codec.h.
 */
public interface VorbisConstants
{
	public static final int OV_FALSE      = -1;
	public static final int OV_EOF        = -2;
	public static final int OV_HOLE       = -3;

	public static final int OV_EREAD      = -128;
	public static final int OV_EFAULT     = -129;
	public static final int OV_EIMPL      = -130;
	public static final int OV_EINVAL     = -131;
	public static final int OV_ENOTVORBIS = -132;
	public static final int OV_EBADHEADER = -133;
	public static final int OV_EVERSION   = -134;
	public static final int OV_ENOTAUDIO  = -135;
	public static final int OV_EBADPACKET = -136;
	public static final int OV_EBADLINK   = -137;
	public static final int OV_ENOSEEK    = -138;
}



/*** VorbisConstants.java ***/
