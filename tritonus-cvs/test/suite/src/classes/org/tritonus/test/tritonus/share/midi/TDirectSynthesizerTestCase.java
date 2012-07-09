/*
 *	TDirectSynthesizerTestCase.java
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

package org.tritonus.test.tritonus.share.midi;

import junit.framework.TestCase;

import javax.sound.midi.Synthesizer;
import javax.sound.midi.Instrument;
import javax.sound.midi.Patch;
import javax.sound.midi.Soundbank;
import javax.sound.midi.MidiChannel;
import javax.sound.midi.VoiceStatus;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;
//import javax.sound.midi.MidiMessage;

import org.tritonus.share.midi.TDirectSynthesizer;




public class TDirectSynthesizerTestCase
extends TestCase
{
	public TDirectSynthesizerTestCase(String strName)
	{
		super(strName);
	}



	public void testNoteOn()
	throws Exception
	{
		checkMessage2(Type.NOTEON);
	}


	public void testNoteOff()
	throws Exception
	{
		checkMessage2(Type.NOTEOFF);
	}


	public void testPolyPressure()
	throws Exception
	{
		checkMessage2(Type.POLY_PRESSURE);
	}


	public void testControlChange()
	throws Exception
	{
		checkMessage2(Type.CONTROL_CHANGE);
	}


	public void testProgramChange()
	throws Exception
	{
		checkMessage1(Type.PROGRAM);
	}


	public void testChannelPressure()
	throws Exception
	{
		checkMessage1(Type.CHANNEL_PRESSURE);
	}


	public void testPitchbend()
	throws Exception
	{
		Synthesizer synth = new TestSynthesizer();
		synth.open();
		TestSynthesizer.TestChannel[] channels =
			(TestSynthesizer.TestChannel[]) synth.getChannels();
		try
		{
			Receiver r = synth.getReceiver();
			checkPitchbend(channels, r, 0, 0);
			checkPitchbend(channels, r, 5, 127);
			checkPitchbend(channels, r, 7, 128);
			checkPitchbend(channels, r, 15, 16383);
		}
		finally
		{
			synth.close();
		}
	}


	private void checkPitchbend(TestSynthesizer.TestChannel[] channels,
			Receiver r, int nChannel, int nBend)
	throws Exception
	{
		Type type = Type.PITCHBEND;
		ShortMessage shMsg = new ShortMessage();
		shMsg.setMessage(type.getCommand(),
				nChannel, nBend & 0x7F, nBend >> 7);
		resetResults(channels);
		r.send(shMsg, -1);
		checkResult(channels, nChannel, type,
				nBend, -1);
	}


	/**
	 * 
	 * @param bOn if true, note on is tested. If false, note off is tested. 
	 * @throws Exception
	 */
	private void checkMessage2(Type type)
	throws Exception
	{
		Synthesizer synth = new TestSynthesizer();
		synth.open();
		TestSynthesizer.TestChannel[] channels =
			(TestSynthesizer.TestChannel[]) synth.getChannels();
		try
		{
			Receiver r = synth.getReceiver();
			checkMessage(type, channels, r, 0, 17, 55);
			checkMessage(type, channels, r, 15, 0, 0);
			checkMessage(type, channels, r, 5, 127, 127);
		}
		finally
		{
			synth.close();
		}
	}


	/**
	 * 
	 * @param type
	 * @throws Exception
	 */
	private void checkMessage1(Type type)
	throws Exception
	{
		Synthesizer synth = new TestSynthesizer();
		synth.open();
		TestSynthesizer.TestChannel[] channels =
			(TestSynthesizer.TestChannel[]) synth.getChannels();
		try
		{
			Receiver r = synth.getReceiver();
			checkMessage(type, channels, r, 0, 57, 0);
			checkMessage(type, channels, r, 15, 0, 0);
			checkMessage(type, channels, r, 5, 127, 0);
		}
		finally
		{
			synth.close();
		}
	}


	private void checkMessage(Type type,
			TestSynthesizer.TestChannel[] channels,
			Receiver r, int nChannel, int nValue1, int nValue2)
	throws Exception
	{
		ShortMessage shMsg = new ShortMessage();
		shMsg.setMessage(type.getCommand(),
				nChannel, nValue1, nValue2);
		resetResults(channels);
		r.send(shMsg, -1);
		checkResult(channels, nChannel, type,
				nValue1, nValue2);
	}


	private void resetResults(TestSynthesizer.TestChannel[] channels)
	{
		for (int i = 0; i < channels.length; i++)
		{
			channels[i].resetValues();
		}
	}


	private void checkResult(TestSynthesizer.TestChannel[] channels,
			int channel, Type type, int value1, int value2)
	{
		for (int i = 0; i < channels.length; i++)
		{
			TestSynthesizer.TestChannel ch = channels[i];
			if (i == channel)
			{
				assertEquals("affected channel: type",
						type, ch.getType());
				assertEquals("affected channel: value1",
						value1, ch.getValue1());
				assertEquals("affected channel: value2",
						value2, ch.getValue2());
			}
			else
			{
				assertEquals("unaffected channel: type",
						Type.NONE, ch.getType());
				assertEquals("unaffected channel: value1",
						-1, ch.getValue1());
				assertEquals("unaffected channel: value2",
						-1, ch.getValue2());
			}
		}
	}


	private static class TestSynthesizer
	extends TDirectSynthesizer
	{
		private MidiChannel[]	m_channels;


		public TestSynthesizer()
		{
			// no MidiDevice.Info
			super(null);
			m_channels = new TestChannel[16];
			for (int i = 0; i < 16; i++)
			{
				m_channels[i] = new TestChannel(i);
			}
		}


		public int getMaxPolyphony()
		{
			return 16;
		}

		public long getLatency()
		{
			return 0;
		}


		public MidiChannel[] getChannels()
		{
			return m_channels;
		}


		public VoiceStatus[] getVoiceStatus()
		{
			return null;
		}


		public boolean isSoundbankSupported(Soundbank soundbank)
		{
			return false;
		}


		public boolean loadInstrument(Instrument instrument)
		{
			return false;
		}


		public void unloadInstrument(Instrument instrument)
		{
		}


		public boolean remapInstrument(Instrument from,
									   Instrument to)
		{
			return false;
		}


		public Soundbank getDefaultSoundbank()
		{
			return null;
		}


		public Instrument[] getAvailableInstruments()
		{
			return null;
		}


		public Instrument[] getLoadedInstruments()
		{
			return null;
		}


		public boolean loadAllInstruments(Soundbank soundbank)
		{
			return false;
		}


		public void unloadAllInstruments(Soundbank soundbank)
		{
		}


		public boolean loadInstruments(Soundbank soundbank,
									   Patch[] patchList)
		{
			return false;
		}


		public void unloadInstruments(Soundbank soundbank,
									  Patch[] patchList)
		{
		}


		public class TestChannel
		implements MidiChannel
		{
			private Type m_nType;
			private int m_nValue1;
			private int m_nValue2;


			public TestChannel(int nChannel)
			{
			}


			public void resetValues()
			{
				m_nType = Type.NONE;
				m_nValue1 = -1;
				m_nValue2 = -1;
			}


			public Type getType()
			{
				return m_nType;
			}


			public int getValue1()
			{
				return m_nValue1;
			}

			public int getValue2()
			{
				return m_nValue2;
			}

			public void allNotesOff()
			{
			}

			public void allSoundOff()
			{
			}

			public void controlChange(int nController, int nValue)
			{
				m_nType = Type.CONTROL_CHANGE;
				m_nValue1 = nController;
				m_nValue2 = nValue;
			}

			public int getChannelPressure()
			{
				return 0;
			}

			public int getController(int nController)
			{
				return 0;
			}

			public boolean getMono()
			{
				return false;
			}

			public boolean getMute()
			{
				return false;
			}

			public boolean getOmni()
			{
				return false;
			}

			public int getPitchBend()
			{
				return 0;
			}

			public int getPolyPressure(int nNoteNumber)
			{
				return 0;
			}

			public int getProgram()
			{
				return 0;
			}

			public boolean getSolo()
			{
				return false;
			}

			public boolean localControl(boolean bOn)
			{
				return false;
			}

			public void noteOff(int nNoteNumber, int nVelocity)
			{
				m_nType = Type.NOTEOFF;
				m_nValue1 = nNoteNumber;
				m_nValue2 = nVelocity;
			}

			public void noteOff(int nNoteNumber)
			{
			}

			public void noteOn(int nNoteNumber, int nVelocity)
			{
				m_nType = Type.NOTEON;
				m_nValue1 = nNoteNumber;
				m_nValue2 = nVelocity;
			}

			public void programChange(int nBank, int nProgram)
			{
				m_nType = Type.BANK_PROGRAM;
				m_nValue1 = nBank;
				m_nValue2 = nProgram;
			}

			public void programChange(int nProgram)
			{
				m_nType = Type.PROGRAM;
				m_nValue1 = nProgram;
				m_nValue2 = 0;
			}

			public void resetAllControllers()
			{
			}

			public void setChannelPressure(int nPressure)
			{
				m_nType = Type.CHANNEL_PRESSURE;
				m_nValue1 = nPressure;
				m_nValue2 = 0;
			}

			public void setMono(boolean bMono)
			{
			}

			public void setMute(boolean bMute)
			{
			}

			public void setOmni(boolean bOmni)
			{
			}

			public void setPitchBend(int nBend)
			{
				m_nType = Type.PITCHBEND;
				m_nValue1 = nBend;
			}

			public void setPolyPressure(int nNoteNumber, int nPressure)
			{
				m_nType = Type.POLY_PRESSURE;
				m_nValue1 = nNoteNumber;
				m_nValue2 = nPressure;
			}

			public void setSolo(boolean bSolo)
			{
			}
 		}
	}


	public static enum Type
	{
		NONE,
		CONTROL_CHANGE(ShortMessage.CONTROL_CHANGE),
		NOTEON(ShortMessage.NOTE_ON),
		NOTEOFF(ShortMessage.NOTE_OFF),
		PROGRAM(ShortMessage.PROGRAM_CHANGE),
		BANK_PROGRAM(ShortMessage.PROGRAM_CHANGE),
		PITCHBEND(ShortMessage.PITCH_BEND),
		POLY_PRESSURE(ShortMessage.POLY_PRESSURE),
		CHANNEL_PRESSURE(ShortMessage.CHANNEL_PRESSURE);
		
		private int m_nCommand;

		private Type()
		{
			this(0);
		}

		private Type(int nCommand)
		{
			m_nCommand = nCommand;
		}

		public int getCommand()
		{
			return m_nCommand;
		}
	}

			
}



/*** TDirectSynthesizerTestCase.java ***/
