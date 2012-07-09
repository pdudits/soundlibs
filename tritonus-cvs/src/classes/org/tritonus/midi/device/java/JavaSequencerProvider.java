/*
 *	JavaSequencerProvider.java
 *
 *	This file is part of Tritonus: http://www.tritonus.org/
 */

/*
 *  Copyright (c) 1999 by Matthias Pfisterer
 *
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
 *
 */

/*
|<---            this code is formatted to fit into 80 columns             --->|
*/

package org.tritonus.midi.device.java;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.spi.MidiDeviceProvider;

import org.tritonus.share.TDebug;
import org.tritonus.share.GlobalInfo;
import org.tritonus.share.midi.TMidiDevice;




public class JavaSequencerProvider
extends MidiDeviceProvider
{
	private static MidiDevice.Info		sm_info;



	public JavaSequencerProvider()
	{
		if (TDebug.TraceMidiDeviceProvider) { TDebug.out("JavaSequencerProvider.<init>(): begin"); }
		synchronized (JavaSequencerProvider.class)
		{
			if (sm_info == null)
			{
				sm_info = new TMidiDevice.Info(
					"Tritonus Java Sequencer",
					GlobalInfo.getVendor(),
					"this is a pure-java sequencer",
					GlobalInfo.getVersion());
			}
		}
		if (TDebug.TraceMidiDeviceProvider) { TDebug.out("JavaSequencerProvider.<init>(): end"); }
	}



	public MidiDevice.Info[] getDeviceInfo()
	{
		if (TDebug.TraceMidiDeviceProvider) { TDebug.out("JavaSequencerProvider.getDeviceInfo(): begin"); }
		MidiDevice.Info[]	infos = new MidiDevice.Info[1];
		infos[0] = sm_info;
		if (TDebug.TraceMidiDeviceProvider) { TDebug.out("JavaSequencerProvider.getDeviceInfo(): end"); }
		return infos;
	}



	public MidiDevice getDevice(MidiDevice.Info info)
	{
		if (TDebug.TraceMidiDeviceProvider) { TDebug.out("JavaSequencerProvider.getDevice(): begin"); }
		MidiDevice	device = null;
		if (info != null && info.equals(sm_info))
		{
			device = new JavaSequencer(sm_info);
		}
		if (device == null)
		{
			throw new IllegalArgumentException("no device for " + info);
		}
		if (TDebug.TraceMidiDeviceProvider) { TDebug.out("JavaSequencerProvider.getDevice(): end"); }
		return device;
	}
}



/*** JavaSequencerProvider.java ***/
