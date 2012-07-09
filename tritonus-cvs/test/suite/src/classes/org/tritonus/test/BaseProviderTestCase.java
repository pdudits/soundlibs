/*
 *	BaseProviderTestCase.java
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

package org.tritonus.test;

import	java.util.MissingResourceException;
import	java.util.ResourceBundle;

import	javax.sound.sampled.AudioFileFormat;
import	javax.sound.sampled.AudioFormat;

import	junit.framework.TestCase;

import	org.tritonus.share.sampled.AudioFileTypes;
import	org.tritonus.share.sampled.Encodings;



public class BaseProviderTestCase
extends TestCase
{
	private static final boolean	DEBUG = true;

	/**	Precision for float comparisons.
	 */
	protected static final float	DELTA = 0.1F;


	private ResourceBundle		m_resourceBundle;
	private String			m_strResourcePrefix;
	private Object			m_provider;
	private boolean			m_bCheckRealLengths;



	public BaseProviderTestCase(String strName,
				    String strResourceBasename)
	{
		super(strName);
		m_resourceBundle = loadResourceBundle(strResourceBasename);
	}



	protected void setResourcePrefix(String strResourcePrefix)
	{
		m_strResourcePrefix = strResourcePrefix;
	}



	protected String getResourcePrefix()
	{
		return m_strResourcePrefix;
	}



	protected void setUp()
		throws Exception
	{
		if (getTestProvider())
		{
			String	strClassName = getClassName();
			Class	cls = Class.forName(strClassName);
			m_provider = cls.newInstance();
		}
	}



	protected Object getProvider()
	{
		return m_provider;
	}



	protected boolean getTestProvider()
	{
		return true;
	}



	protected boolean getTestAudioSystem()
	{
		return true;
	}



// 	private void checkAudioFileFormat(AudioFileFormat audioFileFormat, boolean bRealLengthExpected)
// 		throws Exception
// 	{
// 		assertEquals("type",
// 			     getType(),
// 			     audioFileFormat.getType());
// 		checkAudioFormat(audioFileFormat.getFormat());
// 		long	lExpectedByteLength = AudioSystem.NOT_SPECIFIED;
// 		long	lExpectedFrameLength = AudioSystem.NOT_SPECIFIED;
// 		if (getCheckRealLengths() || bRealLengthExpected)
// 		{
// 			lExpectedByteLength = getByteLength();
// 			lExpectedFrameLength = getFrameLength();
// 		}
// 		assertEquals("byte length",
// 			     lExpectedByteLength,
// 			     audioFileFormat.getByteLength());
// 		assertEquals("frame length",
// 			     lExpectedFrameLength,
// 			     audioFileFormat.getFrameLength());
// 	}



// 	private void checkAudioInputStream(AudioInputStream audioInputStream, boolean bRealLengthExpected)
// 		throws Exception
// 	{
// 		checkAudioFormat(audioInputStream.getFormat());
// 		long	lExpectedFrameLength = AudioSystem.NOT_SPECIFIED;
// 		if (getCheckRealLengths() || bRealLengthExpected)
// 		{
// 			lExpectedFrameLength = getFrameLength();
// 		}
// 		assertEquals("frame length",
// 			     lExpectedFrameLength,
// 			     audioInputStream.getFrameLength());
// 		if (getCheckRealLengths() || bRealLengthExpected)
// 		{
// 			int	nExpectedDataLength = (int) (lExpectedFrameLength * getFrameSize());
// 			byte[]	abRetrievedData = new byte[nExpectedDataLength];
// 			int	nRead = audioInputStream.read(abRetrievedData);
// 			assertEquals("reading data",
// 				     nExpectedDataLength,
// 				     nRead);
// // 			for (int i = 0; i < nExpectedDataLength; i++)
// // 			{
// // 				assertEquals("data content", 0, abRetrievedData[i]);
// // 			}
// 		}
// 		else
// 		{
// 			// TODO: try to at least read some bytes?
// 		}
// 	}



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



	private ResourceBundle loadResourceBundle(String sResourceBasename)
	{
		ResourceBundle	resourceBundle = null;
		try
		{
			resourceBundle = ResourceBundle.getBundle(sResourceBasename);
		}
		catch (MissingResourceException e)
		{
			e.printStackTrace();
/*			System.err.println("ActionManager.loadResourceBundle(): cannot find property file!");
			System.exit(1);
*/		}
		return resourceBundle;
	}



	protected String getResourceString(String strKey)
	{
		return m_resourceBundle.getString(strKey);
	}



	private String getClassName()
	{
		String	strClassName = getResourceString(getResourcePrefix() + ".class");
		return strClassName;
	}



	private String getFilename()
	{
		String	strFileName = getResourceString(getResourcePrefix() + ".filename");
		return strFileName;
	}



	private AudioFileFormat.Type getType()
	{
		String	strTypeName = getResourceString(getResourcePrefix() + ".type");
		AudioFileFormat.Type	type = AudioFileTypes.getType(strTypeName);
		return type;
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
}



/*** BaseProviderTestCase.java ***/
