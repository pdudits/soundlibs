/*
 *	MidiChannelTestCase.java
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

import javax.sound.midi.Synthesizer;
import javax.sound.midi.MidiChannel;


/**	Test for javax.sound.midi.Synthesizer.getLatency().
 */
public class MidiChannelTestCase
extends BaseSynthesizerTestCase
{
	public MidiChannelTestCase(String strName)
	{
		super(strName);
	}



	protected void checkSynthesizer(Synthesizer synth)
		throws Exception
	{
		MidiChannel channel;
		synth.open();
		try
		{
			channel = synth.getChannels()[0];
			checkNotes(synth, channel);
			checkNotes2(synth, channel);
			checkPolyPressure(synth, channel);
			checkChannelPressure(synth, channel);
			checkProgramChange(synth, channel);
			checkProgramChange2(synth, channel);
			checkPitchbend(synth, channel);
		}
		finally
		{
			synth.close();
		}
	}

	private void checkNotes(Synthesizer synth, MidiChannel channel)
	{
		for (int i = 0; i < 127; i++)
		{
			channel.noteOn(i, i);
		}
		for (int i = 0; i < 127; i++)
		{
			channel.noteOff(i);
		}
	}


	private void checkNotes2(Synthesizer synth, MidiChannel channel)
	{
		for (int i = 0; i < 127; i++)
		{
			channel.noteOn(i, i);
		}
		for (int i = 0; i < 127; i++)
		{
			channel.noteOff(i, 0);
		}
	}


	private void checkPolyPressure(Synthesizer synth, MidiChannel channel)
	{
		for (int i = 0; i < 127; i++)
		{
			channel.setPolyPressure(i, i);
			int value = channel.getPolyPressure(i);
			assertTrue(constructErrorMessage(synth,
					"poly pressure[" + i + "]", true),
					i == value || value == 0);
		}
	}


	private void checkChannelPressure(Synthesizer synth, MidiChannel channel)
	{
		checkChannelPressure(synth, channel, 0);
		checkChannelPressure(synth, channel, 77);
		checkChannelPressure(synth, channel, 127);
	}


	private void checkChannelPressure(Synthesizer synth, MidiChannel channel,
			int nPressure)
	{
		channel.setChannelPressure(nPressure);
		int value = channel.getChannelPressure();
		assertTrue(constructErrorMessage(synth,
				"channel pressure", true),
				nPressure == value || value == 0);
	}


	private void checkControlChange(Synthesizer synth, MidiChannel channel)
	{
		for (int i = 0; i < 127; i++)
		{
			channel.controlChange(i, i);
			int value = channel.getController(i);
			assertTrue(constructErrorMessage(synth,
					"control change[" + i + "]", true),
					i == value || value == 0);
		}
	}


	private void checkProgramChange(Synthesizer synth, MidiChannel channel)
	{
		for (int i = 0; i < 127; i++)
		{
			channel.programChange(i);
			int value = channel.getProgram();
			assertEquals(constructErrorMessage(synth,
					"program change [" + i + "]", true),
					i, value);
		}
	}


	private void checkProgramChange2(Synthesizer synth, MidiChannel channel)
	{
		checkProgramChange2(synth, channel, 0, 0);
		checkProgramChange2(synth, channel, 12000, 102);
		checkProgramChange2(synth, channel, 16383, 127);
	}


	private void checkProgramChange2(Synthesizer synth, MidiChannel channel,
			int nBank, int nProgram)
	{
		channel.programChange(nBank, nProgram);
			int programValue = channel.getProgram();
			int bankValue = channel.getController(0) * 128
				+ channel.getController(32);
			assertTrue(constructErrorMessage(synth,
					"program change [" + nBank + ", " + nProgram + "]: bank", true),
					nBank == bankValue || bankValue == 0);
			assertEquals(constructErrorMessage(synth,
					"program change [" + nBank + ", " + nProgram + "]: program", true),
					nProgram, programValue);
	}


	private void checkPitchbend(Synthesizer synth, MidiChannel channel)
	{
		checkPitchbend(synth, channel, 0);
		checkPitchbend(synth, channel, 127);
		checkPitchbend(synth, channel, 128);
		checkPitchbend(synth, channel, 8192);
		checkPitchbend(synth, channel, 16383);
	}


	private void checkPitchbend(Synthesizer synth, MidiChannel channel,
			int nBend)
	{
		channel.setPitchBend(nBend);
			int value = channel.getPitchBend();
			assertTrue(constructErrorMessage(synth,
					"pitch bend [" + nBend + "]", true),
					nBend == value || value == 8192);
	}
}



/*** MidiChannelTestCase.java ***/
