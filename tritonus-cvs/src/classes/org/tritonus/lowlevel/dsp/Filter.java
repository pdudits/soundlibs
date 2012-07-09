/*
 *	Filter.java
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


/**	Common interface for all types of filters.
	This is intended for filters that consume samples
	the same rate they output them.
	Examples of such filters are common FIR and IIR filters.
*/
public interface Filter
{
	/**	Process one sample through the filter.
		Input and output samples are normally in the
		range [-1.0 .. +1.0].
	*/
	public float process(float fSample);


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
	public double getFrequencyResponse(double dOmega);


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
	public double getPhaseResponse(double dOmega);
} 



/*** Filter.java ***/
