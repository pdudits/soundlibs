/*
 *	Block.java
 *
 *	This file is part of Tritonus: http://www.tritonus.org/
 */

/*
 *  Copyright (c) 2000 - 2005 by Matthias Pfisterer
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

import org.tritonus.lowlevel.pogg.Ogg;
import org.tritonus.lowlevel.pogg.Packet;
import org.tritonus.share.TDebug;


/** Wrapper for vorbis_block.
 */
public class Block
{
        static
        {
                Ogg.loadNativeLibrary();
                if (TDebug.TraceVorbisNative)
                {
                        setTrace(true);
                }
        }



	/**
	 *	Holds the pointer to vorbis_block
	 *	for the native code.
	 *	This must be long to be 64bit-clean.
	 */
	@SuppressWarnings("unused")
	private long	m_lNativeHandle;



	public Block()
	{
		if (TDebug.TraceVorbisNative) { TDebug.out("Block.<init>(): begin"); }
		int	nReturn = malloc();
		if (nReturn < 0)
		{
			throw new RuntimeException("malloc of vorbis_block failed");
		}
		if (TDebug.TraceVorbisNative) { TDebug.out("Block.<init>(): end"); }
	}



	public void finalize()
	{
		// TODO: call free()
		// call super.finalize() first or last?
		// and introduce a flag if free() has already been called?
	}



	private native int malloc();
	public native void free();


	/** Calls vorbis_block_init().
	 */
	public int init(DspState dspState)
	{
		return init_native(dspState);
	}


	/** Calls vorbis_block_init().
	 */
	public native int init_native(DspState dspState);


	/** Calls vorbis_bitrate_addblock().
	 */
	public int addBlock()
	{
		return addBlock_native();
	}


	/** Calls vorbis_bitrate_addblock().
	 */
	public native int addBlock_native();


	/** Calls vorbis_analysis().
	 */
	public int analysis(Packet packet)
	{
		return analysis_native(packet);
	}


	/** Calls vorbis_analysis().
	 */
	public native int analysis_native(Packet packet);


	/** Calls vorbis_synthesis().
	 */
	public int synthesis(Packet packet)
	{
		return synthesis_native(packet);
	}


	/** Calls vorbis_synthesis().
	 */
	public native int synthesis_native(Packet packet);


	/** Calls vorbis_block_clear().
	 */
	public int clear()
	{
		return clear_native();
	}


	/** Calls vorbis_block_clear().
	 */
	public native int clear_native();


	private static native void setTrace(boolean bTrace);
}





/*** Block.java ***/
