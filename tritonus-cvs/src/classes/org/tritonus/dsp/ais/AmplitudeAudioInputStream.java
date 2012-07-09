/*
 *	AmplitudeAudioInputStream.java
 *
 *	This file is part of Tritonus: http://www.tritonus.org/
 */

/*
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

import org.tritonus.dsp.processor.AmplitudeProcessor;
import org.tritonus.share.sampled.FloatSampleBuffer;




/** Change amplitude of audio data.
 */
public class AmplitudeAudioInputStream
extends FloatAudioInputStream
{
	/** The processor that does the work.
	 */
	private AmplitudeProcessor	m_processor;



	public AmplitudeAudioInputStream(AudioInputStream sourceStream)
	{
		this(sourceStream, sourceStream.getFormat());
	}



	public AmplitudeAudioInputStream(AudioInputStream sourceStream,
					 AudioFormat targetFormat)
	{
		super (sourceStream, targetFormat);
		m_processor = new AmplitudeProcessor();
	}



	/** Set the amplitude.
	    The value passed here is the value the samples are
	    multiplied with. So 1.0F means no change in amplitude. 2.0F
	    doubles the amplitude. 0.5F cuts it to half, and so on.
	    This is in contrast to {@link #setAmplitudeLog() setAmplitudeLog},
	    where you can pass the amplitude change as dB values.
	*/
	public void setAmplitudeLinear(float fAmplitude)
	{
		m_processor.setAmplitudeLinear(fAmplitude);
	}




	/** Set the amplitude.
	    The value passed here is in dB.
	    So 0.0F means no change in amplitude. +6.0F
	    doubles the amplitude. -6.0F cuts it to half, and so on.
	    This is in contrast to
	    {@link #setAmplitudeLinear setAmplitudeLinear()},
	    where you can pass the amplitude change linear values.
	*/
	public void setAmplitudeLog(float fAmplitude)
	{
		m_processor.setAmplitudeLog(fAmplitude);
	}



	/** Do the amplifying.
	    Here, simply each sample in each channel is multiplied with
	    the amplitude value.
	*/
	protected void convert(FloatSampleBuffer buffer)
	{
		m_processor.process(buffer);
	}
}



/*** AmplitudeAudioInputStream.java ***/
