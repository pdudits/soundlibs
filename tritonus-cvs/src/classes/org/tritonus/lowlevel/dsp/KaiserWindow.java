/*
 *	KaiserWindow.java
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


/**	An implementation of the Kaiser window.
 */
public class KaiserWindow
implements Window
{
	/**	The alpha parameter of the Kaiser window.
	 */
	private double		m_dAlpha;


	/**	Constructor taking alpha.
		@param dAlpha The alpha parameter of the Kaiser window.
	*/
	public KaiserWindow(double dAlpha)
	{
		m_dAlpha = dAlpha;
	}


	/**	Returns alpha.
		@return alpha.
	*/
	public double getAlpha()
	{
		return m_dAlpha;
	}


	/**	Get an array containing the window coefficients.
		@param nOrder The number of elements that the returned
		array should have.
	 */
	public double[] getWindow(int nOrder)
	{
		double[]	adWindow = new double[nOrder];
		for (int n = 0; n < nOrder; n++)
		{
			adWindow[n] = Util.I0(getAlpha() * Math.sqrt(n * (2.0 * nOrder - n)) / nOrder) / Util.I0(getAlpha());
		}
		return adWindow;
	}
} 



/*** KaiserWindow.java ***/
