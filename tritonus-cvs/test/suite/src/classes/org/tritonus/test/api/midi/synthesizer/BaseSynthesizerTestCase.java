/*
 *	BaseSynthesizerTestCase.java
 */

/*
 *  Copyright (c) 2006 by Matthias Pfisterer
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

package org.tritonus.test.api.midi.synthesizer;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Synthesizer;

import junit.framework.TestCase;


/**	Base class for testsof javax.sound.midi.Synthesizer.
 */
public abstract class BaseSynthesizerTestCase
extends TestCase
{
	private static final boolean IGNORE_SUN_SYNTHESIZER = false;


	protected BaseSynthesizerTestCase(String strName)
	{
		super(strName);
	}



	/**	Iterate over all available Sequencers.
	*/
	public void testSeqencer()
		throws Exception
	{
		MidiDevice.Info[] infos = MidiSystem.getMidiDeviceInfo();
		for (int i = 0; i < infos.length; i++)
		{
			MidiDevice device = MidiSystem.getMidiDevice(infos[i]);
			if (device instanceof Synthesizer &&
				! (IGNORE_SUN_SYNTHESIZER &&
				   device.getDeviceInfo().getVendor().indexOf("Sun") != -1))
			{
				System.out.println("testing synth: " + device);
				checkSynthesizer((Synthesizer) device);
			}
		}
	}



	protected abstract void checkSynthesizer(Synthesizer seq)
		throws Exception;

	protected static String constructErrorMessage(Synthesizer synth,
						String strMessage,
						boolean bOpen)
	{
		String strAll = getMessagePrefix(synth) + strMessage;
		strAll += " in " + (bOpen ? "open" : "closed") + " state";
		return strAll;
	}


	/** Get the prefix for error messages (containing the Synthesizer's name).
	 */
	protected static String getMessagePrefix(Synthesizer seq)
	{
		return seq.getDeviceInfo().getName() + ": ";
	}
}



/*** BaseSynthesizerTestCase.java ***/
