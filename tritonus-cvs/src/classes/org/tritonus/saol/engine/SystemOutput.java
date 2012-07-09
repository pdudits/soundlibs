/*
 *	SystemOutput.java
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

import java.io.IOException;



/**	Output method for the SA engine.
	This interface abstracts the way calculated samples are
	output from the engine. The engine only calls this interface,
	while implementations of this interface write the samples to a
	file, a line, a network socket or whatever else.

	@author Matthias Pfisterer
 */
public interface SystemOutput
extends Output
{
	/**	Writes the accumulated sample values to the output media.
		This method must be called by the engine after all
		instrument's a-cycle code for this cycle is executed.
		It is intended to actually write the resulting sample data
		to the desired location. The desired location may be a file,
		a line, or somthing else, depending on this interface'
		implementation.
	*/
	public void emit()
		throws IOException;


	/**	Closes the output destination.
		This method must be called by the engine after execution,
		i.e. when there are no further a-cycles.
		It is intended to close files, lines, or other output
		destinations.
	*/
	public void close()
		throws IOException;
}



/*** SystemOutput.java ***/
