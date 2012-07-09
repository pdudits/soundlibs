/*
 *	IllegalStateTestCase.java
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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import javax.sound.midi.ControllerEventListener;
import javax.sound.midi.MetaEventListener;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.MetaMessage;



/**	Tests for class javax.sound.midi.MidiMessage.
 */
public class IllegalStateTestCase
extends BaseSequencerTestCase
{
	public IllegalStateTestCase(String strName)
	{
		super(strName);
	}



	protected void checkSequencer(Sequencer seq)
		throws Exception
	{
		// Sequencer is closed
		checkOpenRequired(seq, false);
		checkOpenNotRequired(seq, false);

		// sequencer open
		seq.open();

		checkOpenRequired(seq, true);
		checkOpenNotRequired(seq, true);

		// clean up
		seq.close();
	}


	private void checkOpenRequired(Sequencer seq, boolean bOpen)
		throws Exception
	{
		boolean bExpectingException = ! bOpen;
		checkMethod(seq, "start()", bExpectingException, bOpen);
		checkMethod(seq, "stop()", bExpectingException, bOpen);
		checkMethod(seq, "startRecording()", bExpectingException, bOpen);
		checkMethod(seq, "stopRecording()", bExpectingException, bOpen);
	}



	private void checkOpenNotRequired(Sequencer seq, boolean bOpen)
		throws Exception
	{
		boolean bExpectingException = false;
		checkMethod(seq, "setSequence(Sequence)", bExpectingException, bOpen);
		checkMethod(seq, "setSequence(InputStream)", bExpectingException, bOpen);
		checkMethod(seq, "getSequence()", bExpectingException, bOpen);
		checkMethod(seq, "isRunning()", bExpectingException, bOpen);
		checkMethod(seq, "isRecording()", bExpectingException, bOpen);
		checkMethod(seq, "recordEnable()", bExpectingException, bOpen);
		checkMethod(seq, "recordDisable()", bExpectingException, bOpen);
		checkMethod(seq, "getTempoInBPM()", bExpectingException, bOpen);
		checkMethod(seq, "setTempoInBPM()", bExpectingException, bOpen);
		checkMethod(seq, "getTempoInMPQ()", bExpectingException, bOpen);
		checkMethod(seq, "setTempoInMPQ()", bExpectingException, bOpen);
		checkMethod(seq, "setTempoFactor()", bExpectingException, bOpen);
		checkMethod(seq, "getTempoFactor()", bExpectingException, bOpen);
		checkMethod(seq, "getTickLength()", bExpectingException, bOpen);
		checkMethod(seq, "getTickPosition()", bExpectingException, bOpen);
		checkMethod(seq, "setTickPosition()", bExpectingException, bOpen);
		checkMethod(seq, "getMicrosecondLength()", bExpectingException, bOpen);
		checkMethod(seq, "getMicrosecondPosition()", bExpectingException, bOpen);
		checkMethod(seq, "setMicrosecondPosition()", bExpectingException, bOpen);
		checkMethod(seq, "setMasterSyncMode()", bExpectingException, bOpen);
		checkMethod(seq, "getMasterSyncMode()", bExpectingException, bOpen);
		checkMethod(seq, "getMasterSyncModes()", bExpectingException, bOpen);
		checkMethod(seq, "setSlaveSyncMode()", bExpectingException, bOpen);
		checkMethod(seq, "getSlaveSyncMode()", bExpectingException, bOpen);
		checkMethod(seq, "getSlaveSyncModes()", bExpectingException, bOpen);
		checkMethod(seq, "setTrackMute()", bExpectingException, bOpen);
		checkMethod(seq, "getTrackMute()", bExpectingException, bOpen);
		checkMethod(seq, "setTrackSolo()", bExpectingException, bOpen);
		checkMethod(seq, "getTrackSolo()", bExpectingException, bOpen);
		checkMethod(seq, "addMetaEventListener()", bExpectingException, bOpen);
		checkMethod(seq, "removeMetaEventListener()", bExpectingException, bOpen);
		checkMethod(seq, "addControllerEventListener()", bExpectingException, bOpen);
		checkMethod(seq, "removeControllerEventListener()", bExpectingException, bOpen);
	}



	private void checkMethod(Sequencer seq, String strMethodName,
							 boolean bExceptionExpected, boolean bOpen)
		throws Exception
	{
		try
		{
			if ("start()".equals(strMethodName))
				seq.start();
			else if ("stop()".equals(strMethodName))
				seq.stop();
			else if ("startRecording()".equals(strMethodName))
				seq.startRecording();
			else if ("stopRecording()".equals(strMethodName))
				seq.stopRecording();
			else if ("setSequence(Sequence)".equals(strMethodName))
				seq.setSequence(createSequence());
			else if ("setSequence(InputStream)".equals(strMethodName))
				seq.setSequence(createSequenceInputStream());
			else if ("getSequence()".equals(strMethodName))
				seq.getSequence();
			else if ("isRunning()".equals(strMethodName))
				seq.isRunning();
			else if ("isRecording()".equals(strMethodName))
				seq.isRecording();
			else if ("recordEnable()".equals(strMethodName))
				seq.recordEnable(seq.getSequence().getTracks()[0], -1);
			else if ("recordDisable()".equals(strMethodName))
				seq.recordDisable(seq.getSequence().getTracks()[0]);
			else if ("getTempoInBPM()".equals(strMethodName))
				seq.getTempoInBPM();
			else if ("setTempoInBPM()".equals(strMethodName))
				seq.setTempoInBPM(122);
			else if ("getTempoInMPQ()".equals(strMethodName))
				seq.getTempoInMPQ();
			else if ("setTempoInMPQ()".equals(strMethodName))
				seq.setTempoInMPQ(300000);
			else if ("setTempoFactor()".equals(strMethodName))
				seq.setTempoFactor(2.0F);
			else if ("getTempoFactor()".equals(strMethodName))
				seq.getTempoFactor();
			else if ("getTickLength()".equals(strMethodName))
				seq.getTickLength();
			else if ("getTickPosition()".equals(strMethodName))
				seq.getTickPosition();
			else if ("setTickPosition()".equals(strMethodName))
				seq.setTickPosition(1);
			else if ("getMicrosecondLength()".equals(strMethodName))
				seq.getMicrosecondLength();
			else if ("getMicrosecondPosition()".equals(strMethodName))
				seq.getMicrosecondPosition();
			else if ("setMicrosecondPosition()".equals(strMethodName))
				seq.setMicrosecondPosition(1);
			else if ("setMasterSyncMode()".equals(strMethodName))
				seq.setMasterSyncMode(Sequencer.SyncMode.INTERNAL_CLOCK);
			else if ("getMasterSyncMode()".equals(strMethodName))
				seq.getMasterSyncMode();
			else if ("getMasterSyncModes()".equals(strMethodName))
				seq.getMasterSyncModes();
			else if ("setSlaveSyncMode()".equals(strMethodName))
				seq.setSlaveSyncMode(Sequencer.SyncMode.NO_SYNC);
			else if ("getSlaveSyncMode()".equals(strMethodName))
				seq.getSlaveSyncMode();
			else if ("getSlaveSyncModes()".equals(strMethodName))
				seq.getSlaveSyncModes();
			else if ("setTrackMute()".equals(strMethodName))
				seq.setTrackMute(0, true);
			else if ("getTrackMute()".equals(strMethodName))
				seq.getTrackMute(0);
			else if ("setTrackSolo()".equals(strMethodName))
				seq.setTrackSolo(0, true);
			else if ("getTrackSolo()".equals(strMethodName))
				seq.getTrackSolo(0);
			else if ("addMetaEventListener()".equals(strMethodName))
				seq.addMetaEventListener(new DummyMetaEventListener());
			else if ("removeMetaEventListener()".equals(strMethodName))
				seq.removeMetaEventListener(new DummyMetaEventListener());
			else if ("addControllerEventListener()".equals(strMethodName))
				seq.addControllerEventListener(
					new DummyControllerEventListener(), new int[]{0});
			else if ("removeControllerEventListener()".equals(strMethodName))
				seq.removeControllerEventListener(
					new DummyControllerEventListener(), new int[]{0});
			else
				throw new RuntimeException("unknown method name");
			if (bExceptionExpected)
			{
				fail(constructErrorMessage(seq, strMethodName, bExceptionExpected, bOpen));
			}
		}
		catch (IllegalStateException e)
		{
			if (! bExceptionExpected)
			{
				fail(constructErrorMessage(seq, strMethodName, bExceptionExpected, bOpen));
			}
		}
	}



	private static Sequence createSequence()
		throws Exception
	{
		Sequence sequence = new Sequence(Sequence.PPQ, 480);
		sequence.createTrack();
		return sequence;
	}



	private static InputStream createSequenceInputStream()
		throws Exception
	{
		Sequence sequence = createSequence();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		MidiSystem.write(sequence, 0, baos);
		byte[] data = baos.toByteArray();
		InputStream inputStream = new ByteArrayInputStream(data);
		return inputStream;
	}



	private static String constructErrorMessage(Sequencer seq,
												String strMethodName,
												boolean bExceptionExpected,
												boolean bOpen)
	{
		String strMessage = getMessagePrefix(seq) + ": IllegalStateException ";
		strMessage += (bExceptionExpected ? "not thrown" : "thrown");
		strMessage += " on " + strMethodName + " in ";
		strMessage += (bOpen ? "open" : "closed");
		strMessage += " state";
		return strMessage;
	}


	private static class DummyMetaEventListener
	implements MetaEventListener
	{
		public void meta(MetaMessage meta)
		{
			// DO NOTHING
		}
	}


	private static class DummyControllerEventListener
	implements ControllerEventListener
	{
		public void controlChange(ShortMessage event)
		{
			// DO NOTHING
		}
	}
}



/*** IllegalStateTestCase.java ***/
