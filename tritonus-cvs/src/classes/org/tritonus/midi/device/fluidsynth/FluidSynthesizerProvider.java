/*
 *	FluidSynthesizerProvider.java
 *
 *	This file is part of Tritonus: http://www.tritonus.org/
 */

/*
 *  Copyright (c) 1999 - 2006 by Matthias Pfisterer
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

package org.tritonus.midi.device.fluidsynth;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.spi.MidiDeviceProvider;

import org.tritonus.share.TDebug;
import org.tritonus.share.GlobalInfo;
import org.tritonus.share.midi.TMidiDevice;




public class FluidSynthesizerProvider
extends MidiDeviceProvider
{
	private static MidiDevice.Info		sm_info;



	public FluidSynthesizerProvider()
	{
		if (TDebug.TraceMidiDeviceProvider)
			TDebug.out("FluidSynthesizerProvider.<init>(): begin");
		synchronized (FluidSynthesizerProvider.class)
		{
			if (sm_info == null)
			{
				sm_info = new TMidiDevice.Info(
					"Tritonus fluidsynth Synthesizer",
					GlobalInfo.getVendor(),
					"a synthesizer based on fluidsynth",
					GlobalInfo.getVersion());
			}
		}
		if (TDebug.TraceMidiDeviceProvider)
			TDebug.out("FluidSynthesizerProvider.<init>(): end");
	}



	public MidiDevice.Info[] getDeviceInfo()
	{
		if (TDebug.TraceMidiDeviceProvider) TDebug.out("FluidSynthesizerProvider.getDeviceInfo(): begin");
		MidiDevice.Info[]	infos = new MidiDevice.Info[1];
		infos[0] = sm_info;
		if (TDebug.TraceMidiDeviceProvider) TDebug.out("FluidSynthesizerProvider.getDeviceInfo(): end");
		return infos;
	}



	public MidiDevice getDevice(MidiDevice.Info info)
	{
		if (TDebug.TraceMidiDeviceProvider) TDebug.out("FluidSynthesizerProvider.getDevice(): begin");
		MidiDevice	device = null;
		if (info != null && info.equals(sm_info))
		{
			try
			{
				device = new FluidSynthesizer(sm_info);
			}
			catch (Exception e)
			{
				throw new IllegalArgumentException("unable to create device for " + info, e);
			}
		}
		else
		{
			throw new IllegalArgumentException("no device for " + info);
		}
		if (TDebug.TraceMidiDeviceProvider) TDebug.out("FluidSynthesizerProvider.getDevice(): end");
		return device;
	}
}

/*** FluidSynthesizerProvider.java ***/
