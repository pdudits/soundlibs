/*
 *	GetChannelsTestCase.java
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
public class GetChannelsTestCase
extends BaseSynthesizerTestCase
{
	public GetChannelsTestCase(String strName)
	{
		super(strName);
	}



	protected void checkSynthesizer(Synthesizer synth)
		throws Exception
	{
		MidiChannel[] channels;
		synth.open();
		try
		{
			channels = synth.getChannels();
			assertNotNull(constructErrorMessage(synth, "getChannels() result null", true),
					channels);
			int numChannels = channels.length;
			assertTrue(constructErrorMessage(synth, "getChannels() result has wrong length", true),
					numChannels == 16);
			for (int i = 0; i < channels.length; i++)
			{
				assertNotNull(constructErrorMessage(synth, "getChannels() result element null", true),
						channels[i]);
			}
		}
		finally
		{
			synth.close();
		}
	}
}



/*** GetChannelsTestCase.java ***/
