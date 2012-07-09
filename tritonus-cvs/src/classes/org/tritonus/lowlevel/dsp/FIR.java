/*
 *	FIR.java
 *
 *	This file is part of Tritonus: http://www.tritonus.org/
 */

/*
 *  Copyright (c) 2002 by Matthias Pfisterer
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

package org.tritonus.lowlevel.dsp;



/**	A Finite Impulse Response (FIR) filter.
 */
public class FIR
implements Filter
{
	/**	The length of the filter (number of coefficients).
	 */
	private int		m_nLength;

	/**	The filter coefficients.
	 */
	private float[]		m_afCoefficients;

	/**	The buffer for past input values.
		This stores the input values needed for convolution.
		The buffer is used as a circular buffer.
	 */
	private float[]		m_afBuffer;

	/**	The index into m_afBuffer.
		Since m_afBuffer is used as a circular buffer,
		a buffer pointer is needed.
	 */
	private int		m_nBufferIndex;


	/**	Init a FIR filter with coefficients.

		@param afCoefficients The array of filter coefficients.
	 */
	public FIR(float[] afCoefficients)
	{
		m_nLength = afCoefficients.length;
		m_afCoefficients = new float[m_nLength];
		System.arraycopy(afCoefficients, 0, m_afCoefficients, 0, m_nLength);
		m_afBuffer = new float[m_nLength];
		m_nBufferIndex = 0;
	}


	/**	Process an input sample and calculate an output sample.
		Call this method to use the filter.
	 */
	public float process(float fInput)
	{
		m_nBufferIndex = (m_nBufferIndex + 1) % m_nLength;
		m_afBuffer[m_nBufferIndex] = fInput;
		int	nBufferIndex = m_nBufferIndex;
		float	fOutput = 0.0F;
		for (int i = 0; i < m_nLength; i++)
		{
			fOutput += m_afCoefficients[i] * m_afBuffer[nBufferIndex];
			nBufferIndex--;
			if (nBufferIndex < 0)
			{
				nBufferIndex += m_nLength;
			}
		}
		return fOutput;
	}


	/**	Returns the length of the filter.
		This returns the length of the filter
		(the number of coefficients). Note that this is not
		the same as the order of the filter. Commonly,
		the 'order' of a FIR filter is said to be the number
		of coefficients minus 1: Since a single coefficient
		is only an amplifier/attenuator, this is considered
		order zero.

		@return The length of the filter (the number of coefficients).
	*/
	private int getLength()
	{
		return m_nLength;
	}


	/**	Get the frequency response of the filter at a specified frequency.
		This method calculates the frequency response of the filter
		for a specified frequency. Calling this method is allowed
		at any time, even while the filter is operating. It does not
		affect the operation of the filter.

		@param dOmega The frequency for which the frequency response
		should be calculated. Has to be given as omega values
		([-PI .. +PI]).

		@return The calculated frequency response.
	 */
	public double getFrequencyResponse(double dOmega)
	{
		double	dReal = 0.0;
		double	dImag = 0.0;
		for (int i = 0; i < getLength(); i++)
		{
			dReal += m_afCoefficients[i] * Math.cos(i * dOmega);
			dImag += m_afCoefficients[i] * Math.sin(i * dOmega);
		}
		double	dResult = Math.sqrt(dReal * dReal + dImag * dImag);
		return dResult;
	}


	/**	Get the phase response of the filter at a specified frequency.
		This method calculates the phase response of the filter
		for a specified frequency. Calling this method is allowed
		at any time, even while the filter is operating. It does not
		affect the operation of the filter.

		@param dOmega The frequency for which the phase response
		should be calculated. Has to be given as omega values
		([-PI .. +PI]).

		@return The calculated phase response.
	 */
	public double getPhaseResponse(double dOmega)
	{
		double	dReal = 0.0;
		double	dImag = 0.0;
		for (int i = 0; i < getLength(); i++)
		{
			dReal += m_afCoefficients[i] * Math.cos(i * dOmega);
			dImag += m_afCoefficients[i] * Math.sin(i * dOmega);
		}
		double	dResult = Math.atan2(dImag, dReal);
		return dResult;
	}
} 



/*** FIR.java ***/
