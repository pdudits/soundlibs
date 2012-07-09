/*
 *	BaseAudioFileReaderTestCase.java
 */

/*
 *  Copyright (c) 2001 - 2003 by Matthias Pfisterer <Matthias.Pfisterer@web.de>
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

package org.tritonus.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.spi.FormatConversionProvider;

import org.tritonus.share.sampled.Encodings;


public class BaseFormatConversionProviderTestCase
extends BaseProviderTestCase
{
	private static final AudioFormat.Encoding[]	EMPTY_ENCODING_ARRAY = new AudioFormat.Encoding[0];
	private static final AudioFormat.Encoding[]	ALL_ENCODINGS = new AudioFormat.Encoding[]
	{
		AudioFormat.Encoding.PCM_SIGNED,
		AudioFormat.Encoding.PCM_UNSIGNED,
		AudioFormat.Encoding.ULAW,
		AudioFormat.Encoding.ALAW,
		Encodings.getEncoding("GSM0610"),
		Encodings.getEncoding("MPEG1L1"),
		Encodings.getEncoding("MPEG1L2"),
		Encodings.getEncoding("MPEG1L3"),
		Encodings.getEncoding("MPEG2L1"),
		Encodings.getEncoding("MPEG2L2"),
		Encodings.getEncoding("MPEG2L3"),
		Encodings.getEncoding("MPEG2DOT5L1"),
		Encodings.getEncoding("MPEG2DOT5L2"),
		Encodings.getEncoding("MPEG2DOT5L3"),
		Encodings.getEncoding("VORBIS"),
		Encodings.getEncoding("IMA_ADPCM"),
	};




	private static final boolean	DEBUG = true;
	private static final String	RESOURCE_BASENAME = "formatconversionprovider";



	public BaseFormatConversionProviderTestCase(String strName)
	{
		super(strName,
		      RESOURCE_BASENAME);
	}



	protected FormatConversionProvider getFormatConversionProvider()
	{
		return (FormatConversionProvider) getProvider();
	}



	public void testGetSourceEncodings()
	{
		AudioFormat.Encoding[]	aEncodings = null;
		if (getTestProvider())
		{
			aEncodings = getFormatConversionProvider().getSourceEncodings();
			checkEncodings(aEncodings, true);
		}
	}



	public void testGetTargetEncodings()
	{
		AudioFormat.Encoding[]	aEncodings = null;
		if (getTestProvider())
		{
			aEncodings = getFormatConversionProvider().getTargetEncodings();
			checkEncodings(aEncodings, false);
		}
	}



	private void checkEncodings(AudioFormat.Encoding[] aEncodings,
				    boolean bSource)
	{
		AudioFormat.Encoding[] aExpectedEncodings = getEncodings(bSource);
		Iterator	iter;
		List	encodings = Arrays.asList(aEncodings);
		List	expectedEncodings = Arrays.asList(aExpectedEncodings);
		iter = encodings.iterator();
		while (iter.hasNext())
		{
			Object encoding = iter.next();
			assertTrue("returned encoding in expected encodings",
				   expectedEncodings.contains(encoding));
		}
		iter = expectedEncodings.iterator();
		while (iter.hasNext())
		{
			Object encoding = iter.next();
			assertTrue("expected encoding in returned encodings",
				   encodings.contains(encoding));
		}
	}



	public void testIsSourceEncodingsSupported()
	{
		implTestIsEncodingSupported(true);
	}



	public void testIsTargetEncodingsSupported()
	{
		implTestIsEncodingSupported(false);
	}



	private void implTestIsEncodingSupported(boolean bSource)
	{
		if (getTestProvider())
		{
			AudioFormat.Encoding[] aExpectedEncodings = getEncodings(bSource);
			for (int i = 0; i < aExpectedEncodings.length; i++)
			{
				boolean	bSupported;
				if (bSource)
				{
					bSupported = getFormatConversionProvider().isSourceEncodingSupported(aExpectedEncodings[i]);
				}
				else
				{
					bSupported = getFormatConversionProvider().isTargetEncodingSupported(aExpectedEncodings[i]);
				}
				assertTrue("expected encoding supported",
					   bSupported);
			}
			AudioFormat.Encoding[] aUnexpectedEncodings = getUnexpectedEncodings(bSource);
			for (int i = 0; i < aUnexpectedEncodings.length; i++)
			{
				boolean	bSupported;
				if (bSource)
				{
					bSupported = getFormatConversionProvider().isSourceEncodingSupported(aUnexpectedEncodings[i]);
				}
				else
				{
					bSupported = getFormatConversionProvider().isTargetEncodingSupported(aUnexpectedEncodings[i]);
				}
				assertTrue("unexpected encoding supported",
					   ! bSupported);
			}
		}
	}



	private void checkAudioInputStream(AudioInputStream audioInputStream, boolean bRealLengthExpected)
		throws Exception
	{
		checkAudioFormat(audioInputStream.getFormat());
		long	lExpectedFrameLength = AudioSystem.NOT_SPECIFIED;
		if (/*getCheckRealLengths() ||*/ bRealLengthExpected)
		{
			lExpectedFrameLength = getFrameLength();
		}
		assertEquals("frame length",
			     lExpectedFrameLength,
			     audioInputStream.getFrameLength());
		if (/*getCheckRealLengths() ||*/ bRealLengthExpected)
		{
			int	nExpectedDataLength = (int) (lExpectedFrameLength * getFrameSize());
			byte[]	abRetrievedData = new byte[nExpectedDataLength];
			int	nRead = audioInputStream.read(abRetrievedData);
			assertEquals("reading data",
				     nExpectedDataLength,
				     nRead);
// 			for (int i = 0; i < nExpectedDataLength; i++)
// 			{
// 				assertEquals("data content", 0, abRetrievedData[i]);
// 			}
		}
		else
		{
			// TODO: try to at least read some bytes?
		}
	}



	private void checkAudioFormat(AudioFormat audioFormat)
		throws Exception
	{
		assertEquals("encoding",
			     getEncoding(),
			     audioFormat.getEncoding());
		assertEquals("sample rate",
			     getSampleRate(),
			     audioFormat.getSampleRate(),
			     DELTA);
		assertEquals("sample size (bits)",
			     getSampleSizeInBits(),
			     audioFormat.getSampleSizeInBits());
		assertEquals("channels",
			     getChannels(),
			     audioFormat.getChannels());
		assertEquals("frame size",
			     getFrameSize(),
			     audioFormat.getFrameSize());
		assertEquals("frame rate",
			     getFrameRate(),
			     audioFormat.getFrameRate(),
			     DELTA);
		assertEquals("big endian",
			     getBigEndian(),
			     audioFormat.isBigEndian());
	}



	private String getFilename()
	{
		String	strFileName = getResourceString(getResourcePrefix() + ".filename");
		return strFileName;
	}



	private long getByteLength()
	{
		String	strByteLength = getResourceString(getResourcePrefix() + ".byteLength");
		long	lByteLength = Long.parseLong(strByteLength);
		return lByteLength;
	}



	private AudioFormat.Encoding getEncoding()
	{
		String	strEncodingName = getResourceString(getResourcePrefix() + ".format.encoding");
		AudioFormat.Encoding	encoding = Encodings.getEncoding(strEncodingName);
		return encoding;
	}



	private float getSampleRate()
	{
		String	strSampleRate = getResourceString(getResourcePrefix() + ".format.sampleRate");
		float	fSampleRate = Float.parseFloat(strSampleRate);
		return fSampleRate;
	}



	private int getSampleSizeInBits()
	{
		String	strSampleSizeInBits = getResourceString(getResourcePrefix() + ".format.sampleSizeInBits");
		int	nSampleSizeInBits = Integer.parseInt(strSampleSizeInBits);
		return nSampleSizeInBits;
	}



	private int getChannels()
	{
		String	strChannels = getResourceString(getResourcePrefix() + ".format.channels");
		int	nChannels = Integer.parseInt(strChannels);
		return nChannels;
	}



	private int getFrameSize()
	{
		String	strFrameSize = getResourceString(getResourcePrefix() + ".format.frameSize");
		int	nFrameSize = Integer.parseInt(strFrameSize);
		return nFrameSize;
	}



	private float getFrameRate()
	{
		String	strFrameRate = getResourceString(getResourcePrefix() + ".format.frameRate");
		float	fFrameRate = Float.parseFloat(strFrameRate);
		return fFrameRate;
	}



	private boolean getBigEndian()
	{
		String	strBigEndian = getResourceString(getResourcePrefix() + ".format.bigEndian");
		boolean	bBigEndian = strBigEndian.equals("true");
		return bBigEndian;
	}



	private long getFrameLength()
	{
		String	strFrameLength = getResourceString(getResourcePrefix() + ".frameLength");
		long	lFrameLength = Long.parseLong(strFrameLength);
		return lFrameLength;
	}



	private AudioFormat.Encoding[] getEncodings(boolean bSource)
	{
		if (bSource)
		{
			return getEncodings("sourceEncodings");
		}
		else
		{
			return getEncodings("targetEncodings");
		}
	}



	private AudioFormat.Encoding[] getUnexpectedEncodings(boolean bSource)
	{
		AudioFormat.Encoding[]	aExpectedEncodings;
		if (bSource)
		{
			aExpectedEncodings = getEncodings("sourceEncodings");
		}
		else
		{
			aExpectedEncodings = getEncodings("targetEncodings");
		}
		List	expectedEncodings = Arrays.asList(aExpectedEncodings);
		AudioFormat.Encoding[]	aAllEncodings = ALL_ENCODINGS;
		AudioFormat.Encoding[]	aUnexpectedEncodings = new AudioFormat.Encoding[aAllEncodings.length - aExpectedEncodings.length];
		int	nIndex = 0;
		for (int i = 0; i < aAllEncodings.length; i++)
		{
			if (! expectedEncodings.contains(aAllEncodings[i]))
			{
				aUnexpectedEncodings[nIndex] = aAllEncodings[i];
				nIndex++;
			} 
		}
		return aUnexpectedEncodings;
	}



	private AudioFormat.Encoding[] getEncodings(String strKey)
	{
		String	strEncodings = getResourceString(getResourcePrefix() + "." + strKey);
		List<AudioFormat.Encoding> encodingsList =
			new ArrayList<AudioFormat.Encoding>();
		StringTokenizer	tokenizer = new StringTokenizer(strEncodings);
		while (tokenizer.hasMoreTokens())
		{
			String	strEncodingName = tokenizer.nextToken();
			AudioFormat.Encoding	encoding = Encodings.getEncoding(strEncodingName);
			encodingsList.add(encoding);
		}
		return (AudioFormat.Encoding[]) encodingsList.toArray(EMPTY_ENCODING_ARRAY);
	}
}



/*** BaseFormatConversionProviderTestCase.java ***/
