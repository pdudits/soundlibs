/*
 *	MidiDeviceProviderTestCase.java
 */

/*
 *  Copyright (c) 2001 - 2002 by Matthias Pfisterer
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

package org.tritonus.test.api.midi.spi;

import junit.framework.TestCase;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.spi.MidiDeviceProvider;



/**	Tests for class javax.sound.midi.spi.MidiDeviceProvider.
 */
public class MidiDeviceProviderTestCase
extends TestCase
{
	public MidiDeviceProviderTestCase(String strName)
	{
		super(strName);
	}



	public void testIsDeviceSupported()
		throws Exception
	{
		MidiDevice.Info info = new TestInfo("name", "vendor",
											"description", "version");
		checkIsDeviceSupported(new MidiDevice.Info[0], info, false);
		checkIsDeviceSupported(new MidiDevice.Info[]{info}, info, true);
		checkIsDeviceSupported(new MidiDevice.Info[]{info}, null, false);
	}




	private void checkIsDeviceSupported(MidiDevice.Info[] aSupportedInfos,
								   MidiDevice.Info testInfo,
								   boolean bExpectedResult)
		throws Exception
	{
		MidiDeviceProvider provider = new TestMidiDeviceProvider(aSupportedInfos);
		assertTrue("empty supported array", ! (bExpectedResult ^ provider.isDeviceSupported(testInfo)));
	}




	/**	Concrete subclass of MidiDeviceProvider.
	 */
	private class TestMidiDeviceProvider
	extends MidiDeviceProvider
	{
		MidiDevice.Info[]	m_aSupportedInfos;


		public TestMidiDeviceProvider(MidiDevice.Info[]	aSupportedInfos)
		{
			m_aSupportedInfos = aSupportedInfos;
		}


		public MidiDevice.Info[] getDeviceInfo()
		{
			return m_aSupportedInfos;
		}


		public MidiDevice getDevice(MidiDevice.Info info)
		{
			return null;
		}

	}


	/**	Accessible subclass of MidiDevice.Info.
	 */
	private class TestInfo
	extends MidiDevice.Info
	{
		public TestInfo(String name, String vendor, String description,
						String version)
		{
			super(name, vendor, description, version);
		}
	}
}



/*** MidiDeviceProviderTestCase.java ***/
