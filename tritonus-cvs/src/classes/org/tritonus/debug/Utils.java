/*
 *	Utils.java
 *
 *	This file is part of Tritonus: http://www.tritonus.org/
 */

/*
 *  Copyright (c) 1999 - 2002 by Matthias Pfisterer
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

package org.tritonus.debug;

import org.aspectj.lang.JoinPoint;

import org.tritonus.share.TDebug;



/** Utility methods for the debugging aspects.
 */
public class Utils
{
	/** Indentation step.
	    This value determines how many spaces are added/removed
	    for each step of indantation.
	*/
	private static final int	INDENTATION_STEP = 2;

	/** Indentation string.
	    This string is used to generate the appropriate number of spaces.
	*/
	private static final String	INDENTATION_STRING = "                                                                                ";

	/** Current indentation.
	    This holds the current number of blanks to add before each line.
	    The value starts with -INDENTATION_STEP because the first call to
	    outSteppingIn will increase this value prior to printing.
	*/
	private static int	sm_nIndentation = -INDENTATION_STEP;



	public static void outEnteringJoinPoint(JoinPoint joinPoint)
	{
		outSteppingIn("-> " + getSignature(joinPoint));
	}



	public static void outLeavingJoinPoint(JoinPoint joinPoint)
	{
		outSteppingOut("<- " + getSignature(joinPoint));
	}


	private static String getSignature(JoinPoint joinPoint)
	{
		return joinPoint.getStaticPart().getSignature().toShortString();
	}



	/** Print message, increasing the indentation.
	 */
	public static void outSteppingIn(String strMessage)
	{
		sm_nIndentation += INDENTATION_STEP;
		out(strMessage);
	}



	/** Print message, decreasing the indentation.
	 */
	public static void outSteppingOut(String strMessage)
	{
		out(strMessage);
		sm_nIndentation -= INDENTATION_STEP;
	}


	/** Print message with the current indentation.
	 */
	public static void out(String strMessage)
	{
		TDebug.out(INDENTATION_STRING.substring(0, sm_nIndentation) + strMessage);
	}
}



/*** Utils.java ***/
