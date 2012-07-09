/*
 *	BaseMidiDeviceTestCase.java
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

package org.tritonus.test.api.midi.device;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiSystem;

import junit.framework.TestCase;


/**	Base class for tests of javax.sound.midi.MidiDevice.
 */
public abstract class BaseMidiDeviceTestCase
extends TestCase
{
	protected BaseMidiDeviceTestCase(String strName)
	{
		super(strName);
	}



	/**	Iterate over all available MidiDevices.
	*/
	protected void checkMidiDevice(Check check)
		throws Exception
	{
		MidiDevice.Info[] infos = MidiSystem.getMidiDeviceInfo();
		for (int i = 0; i < infos.length; i++)
		{
			MidiDevice device = MidiSystem.getMidiDevice(infos[i]);
			System.out.println("testing device: " + device);
			check.check(device);
		}
	}



	/** Get the prefix for error messages (containing the sequencer's name).
	 */
	protected static String getMessagePrefix(MidiDevice device)
	{
		return device.getDeviceInfo().getName();
	}


	protected interface Check
	{
		public void check(MidiDevice device)
			throws Exception;
	}
}



/*** BaseMidiDeviceTestCase.java ***/
