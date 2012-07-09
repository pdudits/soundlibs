/*
 *	Bus.java
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

package org.tritonus.saol.engine;




/**	Bus in the SA engine.
	This interface abstracts the way calculated samples are
	output from the engine. The engine only calls this interface,
	while implementations of this interface write the samples to a
	file, a line, a network socket or whatever else.

	@author Matthias Pfisterer
 */
public class Bus
implements Output
{
	private float[]		m_afValues;



	public Bus(int nWidth)
	{
		m_afValues = new float[nWidth];
	}


	/**	Gives the width of this bus.
		@returns	width of the bus (number of channels)
	 */
	public int getWidth()
	{
		return m_afValues.length;
	}


	/**	Initiate the cumulation of a sample value.
		Sets the values of all samples to 0.0.
		This method must be called in an a-cycle before
		any instrument's a-cycle code is executed.
	*/
	public void clear()
	{
		for (int i = 0; i < getWidth(); i++)
		{
			m_afValues[i] = 0.0F;
		}
	}


	/**	Add the sample value of one instrument.
		This method can be called by instrument's a-cycle
		code to output the sample value the instrument has
		calculated for this a-cycle.
		The current hacky version allows only for mono samples.
	*/
	public void output(float fSample)
	{
		for (int i = 0; i < getWidth(); i++)
		{
			m_afValues[i] += fSample;
		}
	}


	/**	Add sample values of one instrument.
		This method can be called by instrument's a-cycle
		code to output the sample value the instrument has
		calculated for this a-cycle.
		The current hacky version allows only for mono samples.
	*/
	public void output(float[] afSamples)
	{
		for (int i = 0; i < getWidth(); i++)
		{
			m_afValues[i] += afSamples[i];
		}
	}


	public float[] getValues()
	{
		return m_afValues;
	}
}



/*** Bus.java ***/
