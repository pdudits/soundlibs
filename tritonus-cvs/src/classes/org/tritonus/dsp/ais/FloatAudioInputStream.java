/*
 *	FloatAudioInputStream.java
 *
 *	This file is part of Tritonus: http://www.tritonus.org/
 */

/*
 *  Copyright (c) 2000 by Florian Bomers <florian@bome.com>
 *  Copyright (c) 2003 by Matthias Pfisterer
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

package org.tritonus.dsp.ais;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;

import org.tritonus.share.sampled.FloatSampleBuffer;
import org.tritonus.share.sampled.convert.TSynchronousFilteredAudioInputStream;



/** Base class for ... .
 */
public abstract class FloatAudioInputStream
extends TSynchronousFilteredAudioInputStream
{
	private AudioFormat intermediateFloatBufferFormat;
	private FloatSampleBuffer	m_floatBuffer = null;



	public FloatAudioInputStream(AudioInputStream sourceStream, AudioFormat targetFormat)
	{
		// transform the targetFormat so that
		// FrameRate, and SampleRate match the sourceFormat
		super (sourceStream, new AudioFormat(
			       targetFormat.getEncoding(),
			       sourceStream.getFormat().getSampleRate(),
			       targetFormat.getSampleSizeInBits(),
			       targetFormat.getChannels(),
			       targetFormat.getChannels()*targetFormat.getSampleSizeInBits()/8,
			       sourceStream.getFormat().getFrameRate(),
			       targetFormat.isBigEndian()));

		int floatChannels = targetFormat.getChannels();
		intermediateFloatBufferFormat = new AudioFormat(
			targetFormat.getEncoding(),
			sourceStream.getFormat().getSampleRate(),
			targetFormat.getSampleSizeInBits(),
			floatChannels,
			floatChannels*targetFormat.getSampleSizeInBits()/8,
			sourceStream.getFormat().getFrameRate(),
			targetFormat.isBigEndian());
	}



	protected int convert(byte[] inBuffer, byte[] outBuffer, int outByteOffset, int inFrameCount)
	{
		int sampleCount = inFrameCount * getOriginalStream().getFormat().getChannels();
		int byteCount = sampleCount * (getOriginalStream().getFormat().getSampleSizeInBits()/8);
		if (m_floatBuffer == null)
		{
			m_floatBuffer = new FloatSampleBuffer();
		}
		m_floatBuffer.initFromByteArray(inBuffer, 0, byteCount, getOriginalStream().getFormat());
		convert(m_floatBuffer);
		m_floatBuffer.convertToByteArray(outBuffer, outByteOffset, intermediateFloatBufferFormat);
		return inFrameCount;
	}



	protected abstract void convert(FloatSampleBuffer buffer);
}



/*** FloatAudioInputStream.java ***/
