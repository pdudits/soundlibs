/*
 *	TempoTestCase.java
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

import javax.sound.midi.MetaEventListener;
import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.midi.Track;
//import javax.sound.midi.InvalidMidiDataException;



/**	Tests for class javax.sound.midi.MidiMessage.
 */
public class TempoTestCase
extends BaseSequencerTestCase
{
	private static final float DELTA = 1.0E-9F;
	private static final float MPQ0 = 500000;
	private static final float BPM0 = 120;
	private static final float MPQ1 = 600000;
	private static final float BPM1 = 100;
	private static final float MPQ2 = 416666.66F;
	private static final float BPM2 = 144;

	private static final byte[] TEMPOTEXT =
	{
		't', 'e', 'm', 'p', 'o'
	};


	public TempoTestCase(String strName)
	{
		super(strName);
	}



	protected void checkSequencer(Sequencer seq)
		throws Exception
	{
		// initial tempo
		checkTempoValues("initial", seq, MPQ0, BPM0, 1.0F);

		// setting values in closed state
		seq.setTempoInMPQ(MPQ1);
		checkTempoValues("closed: setMPQ", seq, MPQ1, BPM1, 1.0F);
		seq.setTempoInBPM(BPM2);
		checkTempoValues("closed: setBPM", seq, MPQ2, BPM2, 1.0F);
		seq.setTempoFactor(2.0F);
		checkTempoValues("closed: setFactor", seq, MPQ2, BPM2, 2.0F);

		seq.setSequence(createSequence());

		checkTempoValues("closed: after setSequence()", seq, MPQ2, BPM2, 1.0F);

		seq.open();

		checkTempoValues("after open()", seq, MPQ2, BPM2, 1.0F);

		// setting values in open state
		seq.setTempoInMPQ(MPQ1);
		checkTempoValues("open: setMPQ", seq, MPQ1, BPM1, 1.0F);
		seq.setTempoInBPM(BPM2);
		checkTempoValues("open: setBPM", seq, MPQ2, BPM2, 1.0F);
		seq.setTempoFactor(3.0F);
		checkTempoValues("open: setFactor", seq, MPQ2, BPM2, 3.0F);

		seq.start();

		checkTempoValues("after start()", seq, MPQ2, BPM2, 3.0F);

		// setting values in start state
		seq.setTempoInMPQ(MPQ1);
		checkTempoValues("started: setMPQ", seq, MPQ1, BPM1, 3.0F);
		seq.setTempoInBPM(BPM2);
		checkTempoValues("started: setBPM", seq, MPQ2, BPM2, 3.0F);
		seq.setTempoFactor(2.0F);
		checkTempoValues("started: setFactor", seq, MPQ2, BPM2, 2.0F);

		seq.stop();

		checkTempoValues("after stop()", seq, MPQ2, BPM2, 2.0F);

		// setting values in start state
		seq.setTempoInMPQ(MPQ1);
		checkTempoValues("stopped: setMPQ", seq, MPQ1, BPM1, 2.0F);
		seq.setTempoInBPM(BPM2);
		checkTempoValues("stopped: setBPM", seq, MPQ2, BPM2, 2.0F);
		seq.setTempoFactor(3.0F);
		checkTempoValues("stopped: setFactor", seq, MPQ2, BPM2, 3.0F);

		seq.close();

		checkTempoValues("after close()", seq, MPQ2, BPM2, 3.0F);
	}


	private void checkTempoValues(String strMessagePrefix,
								  Sequencer seq,
								  float fExpectedMPQ,
								  float fExpectedBPM,
								  float fExpectedFactor)
	{
		assertEquals(strMessagePrefix + " tempo in MPQ",
					 fExpectedMPQ, seq.getTempoInMPQ(), DELTA);
		assertEquals(strMessagePrefix + " tempo in BPM",
					 fExpectedBPM, seq.getTempoInBPM(), DELTA);
		assertEquals(strMessagePrefix + " tempo factor",
					 fExpectedFactor, seq.getTempoFactor(), DELTA);
	}



	private static Sequence createSequence()
		throws Exception
	{
		Sequence sequence = new Sequence(Sequence.PPQ, 480);
		Track track = sequence.createTrack();
		for (long lTick = 0; lTick < 100000; lTick += 1000)
		{
			MetaMessage mm = new MetaMessage();
			mm.setMessage(6, TEMPOTEXT, TEMPOTEXT.length);
			MidiEvent me = new MidiEvent(mm, lTick);
			track.add(me);
		}
		return sequence;
	}


	private static class TempoDetector
	implements MetaEventListener
	{
		private long[] m_alArrivalTimes;


		public void meta(MetaMessage message)
		{
			if (message.getType() == 6)
			{
				System.arraycopy(m_alArrivalTimes, 0, m_alArrivalTimes, 1, 9);
				m_alArrivalTimes[0] = System.currentTimeMillis();
			}
		}


		public float getTempoInMPQ()
		{
			return 0.0F;
		}
	}
}



/*** TempoTestCase.java ***/
