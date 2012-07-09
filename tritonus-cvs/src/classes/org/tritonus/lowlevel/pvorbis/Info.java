/*
 *	Info.java
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


/** Wrapper for vorbis_info.
 */
@SuppressWarnings("unused")
public class Info
implements VorbisConstants
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
	 *	Holds the pointer to vorbis_info
	 *	for the native code.
	 *	This must be long to be 64bit-clean.
	 */
	private long	m_lNativeHandle;


	private int m_nVersion;
	private int m_nChannels;
	private int m_nRate;
	private int m_nBitrateUpper;
	private int m_nBitrateNominal;
	private int m_nBitrateLower;



	public Info()
	{
		if (TDebug.TraceVorbisNative) { TDebug.out("Info.<init>(): begin"); }
		int	nReturn = malloc();
		if (nReturn < 0)
		{
			throw new RuntimeException("malloc of vorbis_info failed");
		}
		if (TDebug.TraceVorbisNative) { TDebug.out("Info.<init>(): end"); }
	}



	@Override
	public void finalize()
	{
		// TODO: call free()
		// call super.finalize() first or last?
		// and introduce a flag if free() has already been called?
	}



	private native int malloc();
	public native void free();


	/** Calls vorbis_info_init().
	 */
	public void init()
	{
		m_nVersion = 0;
		m_nChannels = 0;
		m_nRate = 0;
		m_nBitrateUpper = 0;
		m_nBitrateNominal = 0;
		m_nBitrateLower = 0;

		// TODO: allocate codec_setup_info
		init_native();
	}


	/** Calls vorbis_info_init().
	 */
	public native void init_native();


	/** Calls vorbis_info_clear().
	 */
	public void clear()
	{
		// TODO: lots of stuff in codec_setup_info

		m_nVersion = 0;
		m_nChannels = 0;
		m_nRate = 0;
		m_nBitrateUpper = 0;
		m_nBitrateNominal = 0;
		m_nBitrateLower = 0;

		clear_native();
	}


	/** Calls vorbis_info_clear().
	 */
	public native void clear_native();


	public int getVersion()
	{
		return getVersion_native();
	}


	private native int getVersion_native();


	public void setValues(int nVersion,
						  int nChannels,
						  int nRate,
						  int nBitrateUpper,
						  int nBitrateNominal,
						  int nBitrateLower,
						  int nBlocksize0,
						  int nBlocksize1)
	{
		m_nVersion = nVersion;
		m_nChannels = nChannels;
		m_nRate = nRate;
		m_nBitrateUpper = nBitrateUpper;
		m_nBitrateNominal = nBitrateNominal;
		m_nBitrateLower = nBitrateLower;

		setValues_native(nVersion,
						 nChannels,
						 nRate,
						 nBitrateUpper,
						 nBitrateNominal,
						 nBitrateLower,
						 nBlocksize0,
						 nBlocksize1);
	}


	private native void setValues_native(int nVersion,
										 int nChannels,
										 int nRate,
										 int nBitrateUpper,
										 int nBitrateNominal,
										 int nBitrateLower,
										 int nBlocksize0,
										 int nBlocksize1);


	public int getBlocksize0()
	{
		return getBlocksize_native(0);
	}


	public int getBlocksize1()
	{
		return getBlocksize_native(1);
	}


	/**
	   @param nIndex which blocksize is desired. Has to be either 0 or
	   1.
	 */
	private native int getBlocksize_native(int nIndex);


	/** Accesses channels.
	 */
	public int getChannels()
	{
		// return m_nChannels;
		return getChannels_native();
	}


	/** Accesses channels.
	 */
	private native int getChannels_native();



	/** Accesses rate.
	 */
	public int getRate()
	{
		//return m_nRate;
		return getRate_native();
	}


	/** Accesses rate.
	 */
	private native int getRate_native();


	public int getBitrateUpper()
	{
		//return m_nBitrateUpper;
		return getBitrateUpper_native();
	}


	private native int getBitrateUpper_native();


	public int getBitrateNominal()
	{
		//return m_nBitrateNominal;
		return getBitrateNominal_native();
	}


	private native int getBitrateNominal_native();


	public int getBitrateLower()
	{
		//return m_nBitrateLower;
		return getBitrateLower_native();
	}



	private native int getBitrateLower_native();


	/** Calls vorbis_encode_init().
	 */
	public int encodeInit(
		int nChannels,
		int nRate,
		int nMaxBitrate,
		int nNominalBitrate,
		int nMinBitrate)
	{
		return encodeInit_native(
			nChannels,
			nRate,
			nMaxBitrate,
			nNominalBitrate,
			nMinBitrate);
	}


	/** Calls vorbis_encode_init().
	 */
	private native int encodeInit_native(
		int nChannels,
		int nRate,
		int nMaxBitrate,
		int nNominalBitrate,
		int nMinBitrate);



	/** Calls vorbis_encode_init_vbr().
	 */
	public int encodeInitVBR(
		int nChannels,
		int nRate,
		float fQuality)
	{
		return encodeInitVBR_native(
			nChannels,
			nRate,
			fQuality);
	}


	/** Calls vorbis_encode_init_vbr().
	 */
	private native int encodeInitVBR_native(
		int nChannels,
		int nRate,
		float fQuality);


	/** Calls vorbis_synthesis_headerin().
	 */
	public int headerIn(Comment comment, Packet packet)
	{
		if(packet == null)
		{
			return OV_EBADHEADER;
		}
		Buffer buffer = new Buffer();
		byte[] abData = packet.getData();
		//Buffer.outBuffer(abData);
		buffer.readInit(abData, abData.length);

		/* Which of the three types of header is this? */
		/* Also verify header-ness, vorbis */
		int packtype = buffer.read(8);
		//TDebug.out("packtype: " + packtype);
		String s = buffer.readString(6);
		if(! "vorbis".equals(s))
		{
			/* not a vorbis header */
			buffer.free();
			return OV_ENOTVORBIS;
		}
		int r;
		switch (packtype)
		{
		case 0x01: /* least significant *bit* is read first */
			if(! packet.isBos())
			{
				/* Not the initial packet */
				buffer.free();
				return OV_EBADHEADER;
			}
			if (getRate() != 0)
			{
				/* previously initialized info header */
				buffer.free();
				return OV_EBADHEADER;
			}
			r = unpack(buffer);
			buffer.free();
			return r;

		case 0x03: /* least significant *bit* is read first */
			if (getRate() == 0)
			{
				/* um... we didn't get the initial header */
				buffer.free();
				return OV_EBADHEADER;
			}
			r = comment.unpack(buffer);
			buffer.free();
			return r;

		case 0x05: /* least significant *bit* is read first */
			if (getRate() == 0 || comment.getVendor() == null)
			{
				/* um... we didn;t get the initial header or comments yet */
				buffer.free();
				return OV_EBADHEADER;
			}
			r = headerIn_native(buffer, packtype, packet);
			buffer.free();
			return r;
			//return(_vorbis_unpack_books(vi,&buffer));

		default:
			/* Not a valid vorbis header type */
			buffer.free();
			return OV_EBADHEADER;
		}
	}


	/** Calls vorbis_synthesis_headerin().
	 */
	private native int headerIn_native(Buffer buffer, int nPacketType,
									  Packet packet);


	public int pack(Buffer buffer)
	{
		/* preamble */  
		buffer.write(0x01, 8);
		buffer.write("vorbis");

		buffer.write(0x00, 32); // version
		buffer.write(getChannels(), 8);
		buffer.write(getRate(), 32);

		buffer.write(getBitrateUpper(), 32);
		buffer.write(getBitrateNominal(), 32);
		buffer.write(getBitrateLower(), 32);

		buffer.write(ilog2(getBlocksize0()), 4);
		buffer.write(ilog2(getBlocksize1()), 4);

		/* end of packet */
		buffer.write(1, 1);

		return 0;
	}


	public int unpack(Buffer buffer)
	{
		int nVersion = buffer.read(32);
		if (nVersion != 0)
		{
			return OV_EVERSION;
		}
		int nChannels = buffer.read(8);
		int nRate = buffer.read(32);
		int nBitrateUpper = buffer.read(32);
		int nBitrateNominal = buffer.read(32);
		int nBitrateLower = buffer.read(32);
		int nBlocksize0 = 1 << buffer.read(4);
		int nBlocksize1 = 1 << buffer.read(4);

		if (nChannels < 1 || nRate < 1 ||
			nBlocksize0 < 8 || nBlocksize1 < nBlocksize0)
		{
			clear();
			return OV_EBADHEADER;
		}

		/* EOP check */
		if (buffer.read(1) != 1)
		{
			clear();
			return OV_EBADHEADER;
		}

		setValues(nVersion,
				  nChannels,
				  nRate,
				  nBitrateUpper,
				  nBitrateNominal,
				  nBitrateLower,
				  nBlocksize0,
				  nBlocksize1);
		// everything ok.
		return 0;
	}


	private static native void setTrace(boolean bTrace);


	private static int ilog2(int v)
	{
		int ret = 0;
		if (v != 0)
			--v;
		while (v != 0)
		{
			ret++;
			v >>= 1;
		}
		return ret;
	}
}





/*** Info.java ***/
