/*
 *	DspState.java
 *
 *	This file is part of Tritonus: http://www.tritonus.org/
 */

/*
 *  Copyright (c) 2000 - 2001 by Matthias Pfisterer
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

package org.tritonus.lowlevel.vorbis;

import org.tritonus.lowlevel.ogg.Ogg;
import org.tritonus.lowlevel.ogg.Packet;
import org.tritonus.share.TDebug;


/** Wrapper for vorbis_dsp_state.
 */
public class DspState
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
	 *	Holds the pointer to vorbis_dsp_state
	 *	for the native code.
	 *	This must be long to be 64bit-clean.
	 */
	@SuppressWarnings("unused")
	private long	m_lNativeHandle;



	public DspState()
	{
		if (TDebug.TraceVorbisNative) { TDebug.out("DspState.<init>(): begin"); }
		int	nReturn = malloc();
		if (nReturn < 0)
		{
			throw new RuntimeException("malloc of vorbis_dsp_state failed");
		}
		if (TDebug.TraceVorbisNative) { TDebug.out("DspState.<init>(): end"); }
	}



	public void finalize()
	{
		// TODO: call free()
		// call super.finalize() first or last?
		// and introduce a flag if free() has already been called?
	}



	private native int malloc();
	public native void free();


	/** Initialize for encoding.
	    Calls vorbis_analysis_init().
	 */
	public native int initAnalysis(Info info);


	/** Calls vorbis_analysis_headerout().
	 */
	public native int headerOut(
		Comment comment,
		Packet packet,
		Packet commentPacket,
		Packet codePacket);


	/** Calls vorbis_analysis_buffer() and
	    vorbis_analysis_wrote().
	 */
	public native int write(float[][] values, int nValues);


	/** Calls vorbis_analysis_blockout().
	 */
	public native int blockOut(Block block);


	/** Calls vorbis_bitrate_flushpacket().
	 */
	public native int flushPacket(Packet packet);


	/** Initialize for decoding.
	    Calls vorbis_synthesis_init().
	 */
	public native int initSynthesis(Info info);


	/** Calls vorbis_synthesis_blockin().
	 */
	public native int blockIn(Block block);


	/** Calls vorbis_synthesis_pcmout().
	 */
	public native int pcmOut(float[][] afPcm);


	/** Calls vorbis_synthesis_read().
	 */
	public native int read(int nSamples);


	/** Accesses sequence.
	 */
	public native long getSequence();


	/** Calls vorbis_dsp_clear().
	 */
	public native void clear();


	private static native void setTrace(boolean bTrace);
}





/*** DspState.java ***/
