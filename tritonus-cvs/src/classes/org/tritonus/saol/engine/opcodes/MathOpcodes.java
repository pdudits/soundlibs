/*
 *	MathOpcodes.java
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

package org.tritonus.saol.engine.opcodes;

import org.tritonus.saol.compiler.WidthAndRate;


/**	The Math Opcodes (Section 5.9.4).
 */
public class MathOpcodes
{
	private static final float	LOG_10 = (float) Math.log(10.0);


	public static void buildOpcodeTable(OpcodeTable opcodeTable)
	{
		OpcodeClass	opcodeClass = new OpcodeClass("org.tritonus.saol.engine.opcodes.MathOpcodes", OpcodeClass.TYPE_STATIC);

		opcodeTable.addEntry(new OpcodeEntry("int", opcodeClass, "_int", WidthAndRate.RATE_X));
		opcodeTable.addEntry(new OpcodeEntry("frac", opcodeClass, WidthAndRate.RATE_X));
		opcodeTable.addEntry(new OpcodeEntry("dbamp", opcodeClass, WidthAndRate.RATE_X));
		opcodeTable.addEntry(new OpcodeEntry("ampdb", opcodeClass, WidthAndRate.RATE_X));
		opcodeTable.addEntry(new OpcodeEntry("abs", opcodeClass, WidthAndRate.RATE_X));
		opcodeTable.addEntry(new OpcodeEntry("sgn", opcodeClass, WidthAndRate.RATE_X));
		opcodeTable.addEntry(new OpcodeEntry("exp", opcodeClass, WidthAndRate.RATE_X));
		opcodeTable.addEntry(new OpcodeEntry("log", opcodeClass, WidthAndRate.RATE_X));
		opcodeTable.addEntry(new OpcodeEntry("sqrt", opcodeClass, WidthAndRate.RATE_X));
		opcodeTable.addEntry(new OpcodeEntry("sin", opcodeClass, WidthAndRate.RATE_X));
		opcodeTable.addEntry(new OpcodeEntry("cos", opcodeClass, WidthAndRate.RATE_X));
		opcodeTable.addEntry(new OpcodeEntry("atan", opcodeClass, WidthAndRate.RATE_X));
		opcodeTable.addEntry(new OpcodeEntry("pow", opcodeClass, WidthAndRate.RATE_X));
		opcodeTable.addEntry(new OpcodeEntry("log10", opcodeClass, WidthAndRate.RATE_X));
		opcodeTable.addEntry(new OpcodeEntry("asin", opcodeClass, WidthAndRate.RATE_X));
		opcodeTable.addEntry(new OpcodeEntry("acos", opcodeClass, WidthAndRate.RATE_X));
		opcodeTable.addEntry(new OpcodeEntry("ceil", opcodeClass, WidthAndRate.RATE_X));
		opcodeTable.addEntry(new OpcodeEntry("floor", opcodeClass, WidthAndRate.RATE_X));
		opcodeTable.addEntry(new OpcodeEntry("min", opcodeClass, WidthAndRate.RATE_X));
		opcodeTable.addEntry(new OpcodeEntry("max", opcodeClass, WidthAndRate.RATE_X));

	}


	public static float _int(float x)
	{
		return (float) ((long) x);
	}


	public static float frac(float x)
	{
		return x - _int(x);
	}


	public static float dbamp(float x)
	{
		return 90.0F + 20.0F * log10(x);
	}


	public static float ampdb(float x)
	{
		return pow(10.0F, (x - 90.0F) / 20.0F);
	}


	public static float abs(float x)
	{
		return Math.abs(x);
	}


	public static float sgn(float x)
	{
		return (x < 0.0F) ? -1.0F : ((x > 0.0F) ? +1.0F : 0.0F);
	}


	public static float exp(float x)
	{
		return (float) Math.exp(x);
	}


	public static float log(float x)
	{
		return (float) Math.log(x);
	}


	public static float sqrt(float x)
	{
		return (float) Math.sqrt(x);
	}


	public static float sin(float x)
	{
		return (float) Math.sin(x);
	}


	public static float cos(float x)
	{
		return (float) Math.cos(x);
	}


	public static float atan(float x)
	{
		return (float) Math.atan(x);
	}


	public static float pow(float x, float y)
	{
		return (float) Math.pow(x, y);
	}




	public static float log10(float x)
	{
		/*	Uses the formula:
			log-b (a) = ln (a) / ln (b)
			(ln: logarithmus naturalis, logarith with base e)
		*/
		return (float) Math.log(x) * (1 / LOG_10);
	}


	public static float asin(float x)
	{
		return (float) Math.asin(x);
	}


	public static float acos(float x)
	{
		return (float) Math.acos(x);
	}


	public static float ceil(float x)
	{
		return (float) Math.ceil(x);
	}


	public static float floor(float x)
	{
		return (float) Math.floor(x);
	}


	public static float min(float[] x)
	{
		switch (x.length)
		{
		case 1:
			return x[0];

		case 2:
			return Math.min(x[0], x[1]);

		default:
			float	fMin = x[0];
			for (int i = 1; i < x.length; i++)
			{
				fMin = Math.min(fMin, x[i]);
			}
			return fMin;
		}
	}


	public static float max(float[] x)
	{
		switch (x.length)
		{
		case 1:
			return x[0];

		case 2:
			return Math.max(x[0], x[1]);

		default:
			float	fMax = x[0];
			for (int i = 1; i < x.length; i++)
			{
				fMax = Math.max(fMax, x[i]);
			}
			return fMax;
		}
	}
}



/*** MathOpcodes.java ***/
