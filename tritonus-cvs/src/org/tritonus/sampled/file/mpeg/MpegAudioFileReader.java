/*
 *	MpegAudioFileReader.java
 *
 *	This file is part of Tritonus: http://www.tritonus.org/
 */

/*
 *  Copyright (c) 1999 -2001 by Matthias Pfisterer
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

package org.tritonus.sampled.file.mpeg;

import java.io.InputStream;
import java.io.IOException;
import java.io.EOFException;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.tritonus.share.TDebug;
import org.tritonus.share.sampled.file.TAudioFileFormat;
import org.tritonus.share.sampled.file.TAudioFileReader;



/*
 * @author Matthias Pfisterer
 */
public class MpegAudioFileReader
extends	TAudioFileReader
{
	private static final int	SYNC = 0xFFE00000;
	private static final AudioFormat.Encoding[][]	sm_aEncodings =
	{ 
		{new AudioFormat.Encoding("MPEG2DOT5L3"), 
		 new AudioFormat.Encoding("MPEG2DOT5L2"), 
		 new AudioFormat.Encoding("MPEG2DOT5L1")},
		{null,
		 null,
		 null},	/* reserved */
		{new AudioFormat.Encoding("MPEG2L3"),
		 new AudioFormat.Encoding("MPEG2L2"),
		 new AudioFormat.Encoding("MPEG2L1")},
		{new AudioFormat.Encoding("MPEG1L3"),
		 new AudioFormat.Encoding("MPEG1L2"),
		 new AudioFormat.Encoding("MPEG1L1")},
	};
	private static final float[][]	sm_afSamplingRates =
	{
		{11025.0F, 12000.0F, 8000.0F},
		{0.0F, 0.0F, 0.0F},	/* reserved */
		{22050.0F, 24000.0F, 16000.0F},
		{44100.0F, 48000.0F, 32000.0F},
	};

	private static final int	MARK_LIMIT = 4;



	public MpegAudioFileReader()
	{
		super(MARK_LIMIT, true);
	}



	@Override
	protected AudioFileFormat getAudioFileFormat(InputStream inputStream, long lFileSizeInBytes)
		throws UnsupportedAudioFileException, IOException
	{
		if (TDebug.TraceAudioFileReader) { TDebug.out("MpegAudioFileReader.getAudioFileFormat(): begin"); }
		int	b0 = inputStream.read();
		int	b1 = inputStream.read();
		int	b2 = inputStream.read();
		int	b3 = inputStream.read();
		if ((b0 | b1 | b2 | b3) < 0)
		{
			throw new EOFException();
		}
		int	nHeader = (b0 << 24) + (b1 << 16) + (b2 << 8) + (b3 << 0);
		/*
		 *	We check for the sync bits. If they are present, we
		 *	assume that we have an MPEG bitstream.
		 */
		if ((nHeader & SYNC) != SYNC)
		{
			throw new UnsupportedAudioFileException("not a MPEG stream: no sync bits");
		}
		int	nVersion = (nHeader >> 19) & 0x3;
		if (nVersion == 1)
		{
			throw new UnsupportedAudioFileException("not a MPEG stream: wrong version");
		}
		int nLayer = (nHeader >> 17) & 0x3;
		if (nLayer == 0)
		{
			throw new UnsupportedAudioFileException("not a MPEG stream: wrong layer");
		}
		AudioFormat.Encoding	encoding = sm_aEncodings[nVersion][nLayer - 1];
		// TODO: bit rate, protection
		int	nSFIndex = (nHeader >> 10) & 0x3;
		if (nSFIndex == 3)
		{
			throw new UnsupportedAudioFileException("not a MPEG stream: wrong sampling rate");
		}
		float	fSamplingRate = sm_afSamplingRates[nVersion][nSFIndex];
		int	nMode = (nHeader >> 6) & 0x3;
		int	nChannels = nMode == 3 ? 1 : 2;

		AudioFormat	format = new AudioFormat(
			encoding,
			fSamplingRate,
			AudioSystem.NOT_SPECIFIED /*???*/,
			nChannels,
			AudioSystem.NOT_SPECIFIED /*????*/,
			AudioSystem.NOT_SPECIFIED /*????*/,
			true);
		//$$fb 2000-08-15: workaround for the fixed extension problem in AudioFileFormat.Type
		// see org.tritonus.share.sampled.AudioFileTypes.java
		AudioFileFormat.Type type = new AudioFileFormat.Type("MPEG", "mpeg");
		if (encoding.equals(new AudioFormat.Encoding("MPEG1L3")))
		{
			type = new AudioFileFormat.Type("MP3", "mp3");
		}

		/*
		  If the file size is known, we derive the number of frames
		  ('frame size') from it.
		  If the values don't fit into integers, we leave them at
		  NOT_SPECIFIED. 'Unknown' is considered less incorrect than
		  a wrong value.
		*/
		// [fb] not specifying it causes Sun's Wave file writer to write rubbish
		int	nByteSize = AudioSystem.NOT_SPECIFIED;
		int	nFrameSize = AudioSystem.NOT_SPECIFIED;
		if (lFileSizeInBytes != AudioSystem.NOT_SPECIFIED
		    && lFileSizeInBytes <= Integer.MAX_VALUE)
		{
			nByteSize = (int) lFileSizeInBytes;
			// TODO: check if we can calculate a useful value here
			// nFrameSize = (int) (lFileSizeInBytes / 33);
		}

		AudioFileFormat	audioFileFormat =
			new TAudioFileFormat(
				type,
				format,
				nFrameSize,
				nByteSize);
		if (TDebug.TraceAudioFileReader) { TDebug.out("MpegAudioFileReader.getAudioFileFormat(): end"); }
		return audioFileFormat;
	}
}



/*** MpegAudioFileReader.java ***/

