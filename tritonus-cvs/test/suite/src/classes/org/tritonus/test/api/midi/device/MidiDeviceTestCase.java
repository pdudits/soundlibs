/*
 *	MidiDeviceTestCase.java
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
import javax.sound.midi.Receiver;
import javax.sound.midi.Transmitter;


/** Class for tests of javax.sound.midi.MidiDevice.
 */
public class MidiDeviceTestCase
extends BaseMidiDeviceTestCase
{
	public MidiDeviceTestCase(String strName)
	{
		super(strName);
	}


	public void testGetDeviceInfo()
		throws Exception
	{
		Check check = new Check()
			{
				public void check(MidiDevice device)
					throws Exception
				{
					MidiDevice.Info info = device.getDeviceInfo();
					assertNotNull("getDeviceInfo()", info);
					assertNotNull("DeviceInfo.getName()", info.getName());
					assertNotNull("DeviceInfo.getVendor()", info.getVendor());
					assertNotNull("DeviceInfo.getDescription()", info.getDescription());
					assertNotNull("DeviceInfo.getVersion()", info.getVersion());
				}
			};
		checkMidiDevice(check);
	}


	public void testOpenClose()
		throws Exception
	{
		Check check = new Check()
			{
				public void check(MidiDevice device)
					throws Exception
				{
					assertTrue("closed", ! device.isOpen());
					device.open();
					assertTrue("open", device.isOpen());
					device.close();
					assertTrue("closed", ! device.isOpen());
				}
			};
		checkMidiDevice(check);
	}


	public void testGetMicrosecondPosition()
		throws Exception
	{
		Check check = new Check()
			{
				public void check(MidiDevice device)
					throws Exception
				{
					long lPosition = device.getMicrosecondPosition();
					assertTrue("getMicrosecondPosition() before open", lPosition == -1 || lPosition == 0);
					device.open();
					lPosition = device.getMicrosecondPosition();
					assertTrue("getMicrosecondPosition() after open", lPosition == -1 || lPosition >= 0);
					device.close();
					lPosition = device.getMicrosecondPosition();
					assertTrue("getMicrosecondPosition() after close", lPosition == -1 || lPosition == 0);
				}
			};
		checkMidiDevice(check);
	}


	public void testGetMaxReceivers()
		throws Exception
	{
		Check check = new Check()
			{
				public void check(MidiDevice device)
				{
					int nMax = device.getMaxReceivers();
					assertTrue("getMaxReceivers()", nMax == -1 || nMax == 0);
				}
			};
		checkMidiDevice(check);
	}


	public void testGetMaxTransmitters()
		throws Exception
	{
		Check check = new Check()
			{
				public void check(MidiDevice device)
				{
					int nMax = device.getMaxTransmitters();
					assertTrue("getMaxTransmitters()", nMax == -1 || nMax == 0);
				}
			};
		checkMidiDevice(check);
	}


	public void testGetReceiver()
		throws Exception
	{
		Check check = new Check()
			{
				public void check(MidiDevice device)
					throws Exception
				{
					int nMax = device.getMaxReceivers();
					if (nMax != 0)
					{
						nMax = (nMax == -1) ? 100: nMax;
						Receiver[] aReceivers = new Receiver[nMax];
						for (int i = 0; i < nMax; i++)
						{
							aReceivers[i] = device.getReceiver();
							assertNotNull("getReceiver()", aReceivers[i]);
							for (int j = 0; j < i - 1; j++)
							{
								assertTrue("Receiver objects unique", aReceivers[i] != aReceivers[j]);
							}
						}
						for (int i = 0; i < nMax; i++)
						{
							aReceivers[i].close();
						}
					}
				}
			};
		checkMidiDevice(check);
	}


	public void testGetReceivers()
		throws Exception
	{
		Check check = new Check()
			{
				public void check(MidiDevice device)
					throws Exception
				{
					assertEquals("getReceivers() length", 0, device.getReceivers().size());
					int nMax = device.getMaxReceivers();
					if (nMax != 0)
					{
						nMax = (nMax == -1) ? 100: nMax;
						Receiver[] aReceivers = new Receiver[nMax];
						for (int i = 0; i < nMax; i++)
						{
							aReceivers[i] = device.getReceiver();
							assertTrue("Receiver in getReceivers()",
									   device.getReceivers().contains(aReceivers[i]));
						}
						assertEquals("getReceivers() length", nMax, device.getReceivers().size());
						for (int i = 0; i < nMax; i++)
						{
							aReceivers[i].close();
							assertTrue("Receiver not in getReceivers()",
									   ! device.getReceivers().contains(aReceivers[i]));
						}
					}
					assertEquals("getReceivers() length", 0, device.getReceivers().size());
				}
			};
		checkMidiDevice(check);
	}


	public void testGetTransmitter()
		throws Exception
	{
		Check check = new Check()
			{
				public void check(MidiDevice device)
					throws Exception
				{
					int nMax = device.getMaxTransmitters();
					if (nMax != 0)
					{
						nMax = (nMax == -1) ? 100: nMax;
						Transmitter[] aTransmitters = new Transmitter[nMax];
						for (int i = 0; i < nMax; i++)
						{
							aTransmitters[i] = device.getTransmitter();
							assertNotNull("getTransmitter()", aTransmitters[i]);
							for (int j = 0; j < i - 1; j++)
							{
								assertTrue("Transmitter objects unique", aTransmitters[i] != aTransmitters[j]);
							}
						}
						for (int i = 0; i < nMax; i++)
						{
							aTransmitters[i].close();
						}
					}
				}
			};
		checkMidiDevice(check);
	}


	public void testGetTransmitters()
		throws Exception
	{
		Check check = new Check()
			{
				public void check(MidiDevice device)
					throws Exception
				{
					assertEquals("getTransmitters() length", 0, device.getTransmitters().size());
					int nMax = device.getMaxTransmitters();
					if (nMax != 0)
					{
						nMax = (nMax == -1) ? 100: nMax;
						Transmitter[] aTransmitters = new Transmitter[nMax];
						for (int i = 0; i < nMax; i++)
						{
							aTransmitters[i] = device.getTransmitter();
							assertTrue("Transmitter in getTransmitters()",
									   device.getTransmitters().contains(aTransmitters[i]));
						}
						assertEquals("getTransmitters() length", nMax, device.getTransmitters().size());
						for (int i = 0; i < nMax; i++)
						{
							aTransmitters[i].close();
							assertTrue("Transmitter not in getTransmitters()",
									   ! device.getTransmitters().contains(aTransmitters[i]));
						}
					}
					assertEquals("getTransmitters() length", 0, device.getTransmitters().size());
				}
			};
		checkMidiDevice(check);
	}
}



/*** MidiDeviceTestCase.java ***/
