/*
 *	MidiSystemTestCase.java
 */

/*
 *  Copyright (c) 2004 by Matthias Pfisterer
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

package org.tritonus.test.api.midi.midisystem;

import junit.framework.TestCase;

import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiDevice;
//import javax.sound.midi.Sequencer;
//import javax.sound.midi.Synthesizer;



public class MidiSystemTestCase
extends TestCase
{
	private static final float DELTA = 1E-9F;


	public MidiSystemTestCase(String strName)
	{
		super(strName);
	}


	public void testGetDevices()
		throws Exception
	{
		assertNotNull("getSynthesizer()", MidiSystem.getSynthesizer());
		assertNotNull("getSequencer()", MidiSystem.getSequencer());
 		assertNotNull("getSequencer(true)", MidiSystem.getSequencer(true));
 		assertNotNull("getSequencer(false)", MidiSystem.getSequencer(false));
		assertNotNull("getReceiver()", MidiSystem.getReceiver());
		assertNotNull("getTransmitter()", MidiSystem.getTransmitter());
	}


	public void testGetEachMidiDevice()
		throws Exception
	{
		MidiDevice.Info[] infos = MidiSystem.getMidiDeviceInfo();
		assertTrue("MidiDevice.Info array", infos.length > 0);
		for (int i = 0; i < infos.length; i++)
		{
			assertNotNull("getMidiDevice()", MidiSystem.getMidiDevice(infos[i]));
		}
	}


	public void testGetWrongMidiDevice()
		throws Exception
	{
		MidiDevice.Info info = new TestInfo("name", "vendor",
											"description", "version");
		try
		{
			MidiSystem.getMidiDevice(info);
			fail("wrong MidiDevice.Info should throw exception");
		}
		catch (IllegalArgumentException e)
		{
		}
	}


	private class TestInfo
	extends MidiDevice.Info
	{
		public TestInfo(String name, String vendor,
						String description, String version)
		{
			super(name, vendor,
				  description, version);
		}
	}
}



/*** MidiSystemTestCase.java ***/
