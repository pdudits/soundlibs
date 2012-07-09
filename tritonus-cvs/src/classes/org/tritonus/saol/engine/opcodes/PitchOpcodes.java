/*
 *	PitchOpcodes.java
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



/**	The tune-related opcodes (Section 5.9.5).
	These opcodes depend on a tune value. This tune value
	is global, but only inside the current orchestra. To allow
	the concurrent rendering of multiple orchestras, the tune value
	cannot be global (in programming language terms), but has to
	be encapsulated in class instances. So this class has to be
	instantiated once per orchestra rendering.
 */
public final class PitchOpcodes
{
	private static final float		DEFAULT_TUNE = 440.0F;

	private float		m_fTune;



	public PitchOpcodes()
	{
		m_fTune = DEFAULT_TUNE;
	}



	public static void buildOpcodeTable(OpcodeTable opcodeTable)
	{
		OpcodeClass	staticClass = new OpcodeClass("org.tritonus.saol.engine.opcodes.PitchOpcodes", OpcodeClass.TYPE_STATIC);
		OpcodeClass	instanceClass = new OpcodeClass("org.tritonus.saol.engine.opcodes.PitchOpcodes", OpcodeClass.TYPE_RUNTIME_INSTANCE);

		opcodeTable.addEntry(new OpcodeEntry("gettune", instanceClass, WidthAndRate.RATE_X));
		opcodeTable.addEntry(new OpcodeEntry("settune", instanceClass, WidthAndRate.RATE_K));

		opcodeTable.addEntry(new OpcodeEntry("octpch", staticClass, WidthAndRate.RATE_X));
		opcodeTable.addEntry(new OpcodeEntry("pchoct", staticClass, WidthAndRate.RATE_X));
		opcodeTable.addEntry(new OpcodeEntry("cpspch", instanceClass, WidthAndRate.RATE_X));
		opcodeTable.addEntry(new OpcodeEntry("pchcps", instanceClass, WidthAndRate.RATE_X));
		opcodeTable.addEntry(new OpcodeEntry("cpsoct", instanceClass, WidthAndRate.RATE_X));
		opcodeTable.addEntry(new OpcodeEntry("octcps", instanceClass, WidthAndRate.RATE_X));
		opcodeTable.addEntry(new OpcodeEntry("midipch", staticClass, WidthAndRate.RATE_X));
		opcodeTable.addEntry(new OpcodeEntry("pchmidi", staticClass, WidthAndRate.RATE_X));
		opcodeTable.addEntry(new OpcodeEntry("midioct", staticClass, WidthAndRate.RATE_X));
		opcodeTable.addEntry(new OpcodeEntry("octmidi", staticClass, WidthAndRate.RATE_X));
		opcodeTable.addEntry(new OpcodeEntry("midicps", instanceClass, WidthAndRate.RATE_X));
		opcodeTable.addEntry(new OpcodeEntry("cpsmidi", instanceClass, WidthAndRate.RATE_X));
	}


	public float gettune()
	{
		return m_fTune;
	}


	public float settune(float x)
	{
		m_fTune = x;
		return x;
	}


	public static float octpch(float x)
	{
		return getOctValue(getPchOctave(x), getPchNote(x));
	}


	public static float pchoct(float x)
	{
		return getPchValue(getOctOctave(x), getOctNote(x));
	}


	public float cpspch(float x)
	{
		return getCpsValue(getPchOctave(x), getPchNote(x));
	}


	public float pchcps(float x)
	{
		return getPchValue(getCpsOctave(x), getCpsNote(x));
	}


	public float cpsoct(float x)
	{
		return getCpsValue(getOctOctave(x), getOctNote(x));
	}


	public float octcps(float x)
	{
		return getOctValue(getCpsOctave(x), getCpsNote(x));
	}


	public static float midipch(float x)
	{
		return getMidiValue(getPchOctave(x), getPchNote(x));
	}


	public static float pchmidi(float x)
	{
		return getPchValue(getMidiOctave(x), getMidiNote(x));
	}


	public static float midioct(float x)
	{
		return getMidiValue(getOctOctave(x), getOctNote(x));
	}


	public static float octmidi(float x)
	{
		return getOctValue(getMidiOctave(x), getMidiNote(x));
	}


	public float midicps(float x, float y)
	{
		return getMidiValue(getCpsOctave(x), getCpsNote(x));
	}




	public float cpsmidi(float x)
	{
		return getCpsValue(getMidiOctave(x), getMidiNote(x));
	}


	/*
	 *	helper methds
	 */


	/*
	  These methods use the following conventions:

	  octave:
	  ...
	   6
	   7
	   8	octave of middle c
	   9
	  10
	  ...

	  note:
	   0	C
	   1	C#
	   2	D
	   3	D#
	   4	E
	   5	F
	   6	F#
	   7	G
	   8	G#
	   9	A
	  10	A#
	  11	B
	*/


	private float getCpsValue(int nOctave, int nNote)
	{
		return gettune() * (float) Math.pow(2.0, (nOctave - 8) + (nNote - 9) / 12.0);
	}


	private int getCpsOctave(float fCps)
	{
		double	dRelativePitch = fCps / gettune();
		double	dTone = Math.log(dRelativePitch) * (1.0 / Math.log(2.0));
		return (int) dTone + 8;
	}


	private int getCpsNote(float fCps)
	{
		double	dRelativePitch = fCps / gettune();
		double	dTone = Math.log(dRelativePitch) * (1.0 / Math.log(2.0));
		return (int) ((dTone - (int) dTone) * 12.0);
	}



	private static float getMidiValue(int nOctave, int nNote)
	{
		return (nOctave - 3) * 12 + nNote;
	}


	private static int getMidiOctave(float fMidi)
	{
		return (int) fMidi / 12 + 3;
	}


	private static int getMidiNote(float fMidi)
	{
		return (int) fMidi % 12;
	}



	private static float getOctValue(int nOctave, int nNote)
	{
		return nOctave + nNote * (1.0F / 12.0F);
	}


	private static int getOctOctave(float fOct)
	{
		return (int) fOct;
	}


	private static int getOctNote(float fOct)
	{
		return (int) ((fOct - getOctOctave(fOct)) * 12.0F);
	}



	private static float getPchValue(int nOctave, int nNote)
	{
		return nOctave + nNote * 0.01F;
	}


	private static int getPchOctave(float fPch)
	{
		return (int) fPch;
	}


	private static int getPchNote(float fPch)
	{
		return (int) ((fPch - getPchOctave(fPch)) * 100.0F);
	}



	private static float notImplemented()
	{
		throw new RuntimeException("opcode not implemented");
	}
}



/*** PitchOpcodes.java ***/
