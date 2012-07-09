/*
 *	AuAudioOutputStreamTestCase.java
 */

/*
 *  Copyright (c) 2001 - 2002 by Matthias Pfisterer <Matthias.Pfisterer@web.de>
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

package org.tritonus.test.tritonus.sampled.file;

import	javax.sound.sampled.AudioFormat;

import	org.tritonus.share.sampled.file.AudioOutputStream;
import	org.tritonus.share.sampled.file.TDataOutputStream;
import	org.tritonus.sampled.file.AuAudioOutputStream;


public class AuAudioOutputStreamTestCase
extends BaseAudioOutputStreamTestCase
{
	private static final int	EXPECTED_ADDITIONAL_HEADER_LENGTH = 20;


	public AuAudioOutputStreamTestCase(String strName)
	{
		super(strName);
	}



	protected AudioOutputStream createAudioOutputStreamImpl(
		AudioFormat audioFormat,
		long nLength,
		TDataOutputStream dataOutputStream)
		throws Exception
	{
		return new AuAudioOutputStream(audioFormat,
									   nLength,
									   dataOutputStream);
	}


	/*
	  nLength has to be < 255, or the implementation of this method
	  has to be changed
	 */
	protected byte[] getExpectedHeaderData(AudioFormat audioFormat,
										   int nLength,
										   boolean bSeekable,
										   boolean bLengthGiven)
	{
		int nSampleRate = (int) audioFormat.getSampleRate();
		byte[]	abExpectedHeaderData = new byte[]{
				0x2e, 0x73, 0x6e, 0x64,
				0, 0, 0, (byte) (24 + getExpectedAdditionalHeaderLength()),
				0, 0, 0, 0, // <-- not yet populated
				0, 0, 0, getEncoding(audioFormat),
				0, (byte) (nSampleRate / 65536), (byte) (nSampleRate / 256), (byte) nSampleRate,
				0, 0, 0, (byte) audioFormat.getChannels()
			};
		if (bLengthGiven || bSeekable)
		{
			abExpectedHeaderData[11] = (byte) nLength;
		}
		else
		{
			abExpectedHeaderData[8] = (byte) 0xff;
			abExpectedHeaderData[9] = (byte) 0xff;
			abExpectedHeaderData[10] = (byte) 0xff;
			abExpectedHeaderData[11] = (byte) 0xff;
		}
		return abExpectedHeaderData;
	}


	private byte getEncoding(AudioFormat format)
	{
		// works only for simple cases
		return (byte)(format.getSampleSizeInBits() / 8 + 1);
	}


	protected int getExpectedAdditionalHeaderLength()
	{
		return EXPECTED_ADDITIONAL_HEADER_LENGTH;
	}


	protected boolean getBigEndian()
	{
		return true;
	}


	protected boolean is8bitUnsigned()
	{
		return false;
	}
}



/*** AuAudioOutputStreamTestCase.java ***/
