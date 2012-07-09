/*
 *	AmplitudeProcessor.java
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

package org.tritonus.dsp.processor;

import org.tritonus.share.sampled.FloatSampleBuffer;
import org.tritonus.dsp.interfaces.FloatSampleProcessor;




/** Change amplitude of audio data.
 */
public class AmplitudeProcessor
implements FloatSampleProcessor
{
	/** The amplitude to use if none is set explicitly.
	    This amplitude is used before the first call to
	    {@link #setAmplitudeLinear() setAmplitudeLinear} or
	    {@link #setAmplitudeLog() setAmplitudeLog}.
	    It is given as a linear value.
	*/
	private static final float	DEFAULT_AMPLITUDE = 1.0F;


	/** The amplitude to use during the conversion.
	    This value is used to multiply the samples with.
	    It is given as a linear value.

	    @see #setAmplitudeLinear()
	    @see #setAmplitudeLog()
	*/
	private float		m_fAmplitude;



	public AmplitudeProcessor()
	{
		setAmplitudeLinear(DEFAULT_AMPLITUDE);
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
		m_fAmplitude = fAmplitude;
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
		float	fAmplitudeLinear = (float) Math.pow(10.0, fAmplitude/20.0);
		setAmplitudeLinear(fAmplitudeLinear);
	}



	/** Do the amplifying.
	    Here, simply each sample in each channel is multiplied with
	    the amplitude value.
	*/
	public void process(FloatSampleBuffer buffer)
	{
		for (int nChannel = 0; nChannel < buffer.getChannelCount(); nChannel++)
		{
			float[]	afBuffer = buffer.getChannel(nChannel);
			for (int nSample = 0; nSample < buffer.getSampleCount(); nSample++)
			{
				afBuffer[nSample] *= m_fAmplitude;
			}
		}
	}
}



/*** AmplitudeProcessor.java ***/
