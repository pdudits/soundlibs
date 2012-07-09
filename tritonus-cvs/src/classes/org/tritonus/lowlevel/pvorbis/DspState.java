/*
 *	DspState.java
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
import org.tritonus.lowlevel.pogg.Buffer;
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


	private Info	m_info;


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
	 */
	public int initAnalysis(Info info)
	{
		m_info = info;
		return initAnalysis_native(info);
	}


	/** Initialize for encoding.
	    Calls vorbis_analysis_init().
	 */
	public native int initAnalysis_native(Info info);


	private Info getInfo()
	{
		return m_info;
	}


	/** Calls vorbis_analysis_headerout().
	 */
	public int headerOut(
		Comment comment,
		Packet infoPacket,
		Packet commentPacket,
		Packet codePacket)
	{
		// for testing, write the info header here, too
		//Packet testInfoPacket = new Packet();
		Buffer buffer = new Buffer();
		buffer.writeInit();

		getInfo().pack(buffer);
		infoPacket.setData(buffer.getBuffer(), 0, buffer.bytes());
		infoPacket.setFlags(true, false, 0);

		buffer.reset();
		comment.pack(buffer);
		commentPacket.setData(buffer.getBuffer(), 0, buffer.bytes());
		commentPacket.setFlags(false, false, 0);

		int nReturn = headerOut_native(codePacket);
/*
		byte[] buffer1 = infoPacket.getData();
		byte[] buffer2 = testInfoPacket.getData();
		TDebug.out("comment buffers: " + buffer1.length + ", " + buffer2.length);
		String s = "";
		for (int i = 0; i < buffer1.length; i++)
		{
			s += "" + buffer1[i] + ", ";
		}
		TDebug.out("buffer1: " + s);
		s = "";
		for (int i = 0; i < buffer2.length; i++)
		{
			s += "" + buffer2[i] + ", ";
		}
		TDebug.out("buffer2: " + s);
		testInfoPacket.free();
*/
		buffer.writeClear();
		buffer.free();
		return nReturn;
	}


	/** Calls vorbis_analysis_headerout().
	 */
	public native int headerOut_native(Packet codePacket);


	/** Calls vorbis_analysis_buffer() and
	    vorbis_analysis_wrote().
	 */
	public int write(float[][] values, int nValues)
	{
		return write_native(values, nValues);
	}


	/** Calls vorbis_analysis_buffer() and
	    vorbis_analysis_wrote().
	 */
	public native int write_native(float[][] values, int nValues);


	/** Calls vorbis_analysis_blockout().
	 */
	public int blockOut(Block block)
	{
		return blockOut_native(block);
	}


	/** Calls vorbis_analysis_blockout().
	 */
	public native int blockOut_native(Block block);


	/** Calls vorbis_bitrate_flushpacket().
	 */
	public int flushPacket(Packet packet)
	{
		return flushPacket_native(packet);
	}


	/** Calls vorbis_bitrate_flushpacket().
	 */
	public native int flushPacket_native(Packet packet);


	/** Initialize for decoding.
	    Calls vorbis_synthesis_init().
	 */
	public int initSynthesis(Info info)
	{
		return initSynthesis_native(info);
	}


	/** Initialize for decoding.
	    Calls vorbis_synthesis_init().
	 */
	public native int initSynthesis_native(Info info);


	/** Calls vorbis_synthesis_blockin().
	 */
	public int blockIn(Block block)
	{
		return blockIn_native(block);
	}


	/** Calls vorbis_synthesis_blockin().
	 */
	public native int blockIn_native(Block block);


	/** Calls vorbis_synthesis_pcmout().
	 */
	public int pcmOut(float[][] afPcm)
	{
		return pcmOut_native(afPcm);
	}


	/** Calls vorbis_synthesis_pcmout().
	 */
	public native int pcmOut_native(float[][] afPcm);


	/** Calls vorbis_synthesis_read().
	 */
	public int read(int nSamples)
	{
		return read_native(nSamples);
	}


	/** Calls vorbis_synthesis_read().
	 */
	public native int read_native(int nSamples);


	/** Accesses sequence.
	 */
	public long getSequence()
	{
		return getSequence_native();
	}


	/** Accesses sequence.
	 */
	public native long getSequence_native();


	/** Calls vorbis_dsp_clear().
	 */
	public void clear()
	{
		clear_native();
	}


	/** Calls vorbis_dsp_clear().
	 */
	public native void clear_native();


	private static native void setTrace(boolean bTrace);
}





/*** DspState.java ***/
