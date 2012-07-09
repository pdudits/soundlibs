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

import org.tritonus.share.midi.TMidiChannel;




public class TMidiChannelTestCase
extends TestCase
{
	public TMidiChannelTestCase(String strName)
	{
		super(strName);
	}



	public void testChannelNumber()
	{
		int CHANNEL = 19;
		TestMidiChannel channel = new TestMidiChannel(CHANNEL);
		assertEquals("channel number", CHANNEL, channel.getChannelNumber());
	}



	public void testNoteOff()
	{
		TestMidiChannel channel = new TestMidiChannel(0);
		int KEY;
		channel.resetCachedValues();
		KEY = 0;
		channel.noteOff(KEY);
		assertEquals("noteOff() key", KEY, channel.getNoteOffKey());
		assertEquals("noteOff() velocity", 0, channel.getNoteOffVelocity());

		channel.resetCachedValues();
		KEY = 11;
		channel.noteOff(KEY);
		assertEquals("noteOff() key", KEY, channel.getNoteOffKey());
		assertEquals("noteOff() velocity", 0, channel.getNoteOffVelocity());

		channel.resetCachedValues();
		KEY = 127;
		channel.noteOff(KEY);
		assertEquals("noteOff() key", KEY, channel.getNoteOffKey());
		assertEquals("noteOff() velocity", 0, channel.getNoteOffVelocity());
	}


	public void testProgramChange()
	{
		TestMidiChannel channel = new TestMidiChannel(0);
		doTestProgramChange(channel, 0, 0, 0);
		doTestProgramChange(channel, 127, 127, 127);
	}


	private void doTestProgramChange(TestMidiChannel channel, int nBankHigh,
			int nBankLow, int nProgram)
	{
		channel.resetCachedValues();
		int nBank = (nBankHigh << 7) | nBankLow;
		channel.programChange(nBank, nProgram);
		System.out.println("(c)" + channel.getSetControllerNumber());
		System.out.println("(v)" + channel.getSetControllerValue());
		System.out.println("(c2)" + channel.getSetControllerNumber2());
		System.out.println("(v2)" + channel.getSetControllerValue2());

		assertEquals("programChange() bank high (c)", 0, channel.getSetControllerNumber());
		assertEquals("programChange() bank high (v)", nBankHigh, channel.getSetControllerValue());
		assertEquals("programChange() bank low (c)", 32, channel.getSetControllerNumber2());
		assertEquals("programChange() bank low (v)", nBankLow, channel.getSetControllerValue2());
		assertEquals("programChange() program", nProgram, channel.getProgramChangeValue());
	}


	public void testResetAllControllers()
	{
		TestMidiChannel channel = new TestMidiChannel(0);
		channel.resetAllControllers();
		assertEquals("resetAllControllers(): controller", 121, channel.getSetControllerNumber());
		assertEquals("resetAllControllers(): value", 0, channel.getSetControllerValue());
	}


	public void testAllNotesOff()
	{
		TestMidiChannel channel = new TestMidiChannel(0);
		channel.allNotesOff();
		assertEquals("allNotesOff(): controller", 123, channel.getSetControllerNumber());
		assertEquals("allNotesOff(): value", 0, channel.getSetControllerValue());
	}


	public void testAllSoundOff()
	{
		TestMidiChannel channel = new TestMidiChannel(0);
		channel.allSoundOff();
		assertEquals("allSoundOff(): controller", 120, channel.getSetControllerNumber());
		assertEquals("allSoundOff(): value", 0, channel.getSetControllerValue());
	}


	public void testLocalControl()
	{
		TestMidiChannel channel = new TestMidiChannel(0);
		channel.localControl(true);
		assertEquals("localControl(true): controller", 122, channel.getSetControllerNumber());
		assertEquals("localControl(true): value", 127, channel.getSetControllerValue());
		channel.resetCachedValues();

		channel.localControl(false);
		assertEquals("localControl(false): controller", 122, channel.getSetControllerNumber());
		assertEquals("localControl(false): value", 0, channel.getSetControllerValue());
}


	private static class TestMidiChannel
	extends TMidiChannel
	{
		private int m_nNoteOffKey;
		private int m_nNoteOffVelocity;
		private int m_nSetControllerNumber;
		private int m_nSetControllerValue;
		private int m_nSetControllerNumber2;
		private int m_nSetControllerValue2;
		private int m_nGetControllerNumber;
		private int m_nProgramChangeValue;


		public TestMidiChannel(int nChannel)
		{
			super(nChannel);
			resetCachedValues();
		}


		/**
		 * Used to obtain the return value of the protected super class method.
		 * @return
		 */
		public int getChannelNumber()
		{
			return getChannel();
		}

		public void resetCachedValues()
		{
			m_nNoteOffKey = -1;
			m_nNoteOffVelocity = -1;
			m_nSetControllerNumber = -1;
			m_nSetControllerValue = -1;
			m_nSetControllerNumber2 = -1;
			m_nSetControllerValue2 = -1;
			m_nGetControllerNumber = -1;
			m_nProgramChangeValue = -1;
		}

		public int getNoteOffKey()
		{
			return m_nNoteOffKey;
		}

		public int getNoteOffVelocity()
		{
			return m_nNoteOffVelocity;
		}

		public int getSetControllerNumber()
		{
			return m_nSetControllerNumber;
		}

		public int getSetControllerValue()
		{
			return m_nSetControllerValue;
		}

		public int getSetControllerNumber2()
		{
			return m_nSetControllerNumber2;
		}

		public int getSetControllerValue2()
		{
			return m_nSetControllerValue2;
		}

		public int getProgramChangeValue()
		{
			return m_nProgramChangeValue;
		}

		/**
		 * Records the passed values.
		 */
		public void controlChange(int nController, int nValue)
		{
			System.out.println("CC: " + nController + ": " + nValue);
			if (m_nSetControllerNumber != -1)
			{
				m_nSetControllerNumber2 = nController;
				m_nSetControllerValue2 = nValue;
			}
			else
			{
				m_nSetControllerNumber = nController;
				m_nSetControllerValue = nValue;
			}
		}

		public int getChannelPressure()
		{
			return 0;
		}

		public int getController(int nController)
		{
			return 0;
		}

		public boolean getMute()
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

		public void noteOff(int nNoteNumber, int nVelocity)
		{
			m_nNoteOffKey = nNoteNumber;
			m_nNoteOffVelocity = nVelocity;
		}

		public void noteOn(int nNoteNumber, int nVelocity)
		{
		}

		public void programChange(int nProgram)
		{
			m_nProgramChangeValue = nProgram;
		}

		public void setChannelPressure(int nPressure)
		{
		}

		public void setMute(boolean bMute)
		{
		}

		public void setPitchBend(int nBend)
		{
		}

		public void setPolyPressure(int nNoteNumber, int nPressure)
		{
		}

		public void setSolo(boolean bSolo)
		{
		}
	}
}



/*** TDirectSynthesizerTestCase.java ***/
