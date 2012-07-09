/*
 *	BaseSequencerTestCase.java
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

package org.tritonus.test.api.midi.sequencer;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequencer;

import junit.framework.TestCase;


/**	Base class for testsof javax.sound.midi.Sequencer.
 */
public abstract class BaseSequencerTestCase
extends TestCase
{
	private static final boolean IGNORE_SUN_SEQUENCER = true;


	protected BaseSequencerTestCase(String strName)
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
			if (device instanceof Sequencer &&
				! (IGNORE_SUN_SEQUENCER &&
				   device.getDeviceInfo().getVendor().indexOf("Sun") != -1))
			{
				System.out.println("testing seq: " + device);
				checkSequencer((Sequencer) device);
			}
		}
	}



	protected abstract void checkSequencer(Sequencer seq)
		throws Exception;

	/** Get the prefix for error messages (containing the sequencer's name).
	 */
	protected static String getMessagePrefix(Sequencer seq)
	{
		return seq.getDeviceInfo().getName();
	}
}



/*** BaseSequencerTestCase.java ***/
