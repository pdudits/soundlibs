/*
 *	SequenceLengthTestCase.java
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

package org.tritonus.test.sequencer;

import java.io.File;

import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;



/**	Tests for class javax.sound.midi.MidiMessage.
 */
public class SequenceLengthTestCase
extends BaseSequencerTestCase
{
	private static final String MIDI_FILENAME = "trippygaia1.mid";


	public SequenceLengthTestCase(String strName)
	{
		super(strName);
	}



	protected void checkSequencer(Sequencer seq)
		throws Exception
	{
		seq.open();

		Sequence sequence = MidiSystem.getSequence(getMediaFile(MIDI_FILENAME));
		seq.setSequence(sequence);
		assertEquals(getMessagePrefix(seq) + ": tick length",
					 sequence.getTickLength(),
					 seq.getTickLength());
		assertEquals(getMessagePrefix(seq) + ": time length",
					 sequence.getMicrosecondLength(),
					 seq.getMicrosecondLength());

		// clean up
		seq.close();
	}


	private static File getMediaFile(String strFilename)
	{
		return new File("sounds/" + strFilename);
	}
}



/*** SequenceLengthTestCase.java ***/
