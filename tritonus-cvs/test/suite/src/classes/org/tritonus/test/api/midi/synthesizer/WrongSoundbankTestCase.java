/*
 *	WrongSoundbankTestCase.java
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

import javax.sound.midi.Patch;
import javax.sound.midi.SoundbankResource;
import javax.sound.midi.Synthesizer;
import javax.sound.midi.Soundbank;
import javax.sound.midi.Instrument;


/**	Test for javax.sound.midi.Synthesizer.getLatency().
 */
public class WrongSoundbankTestCase
extends BaseSynthesizerTestCase
{
	public WrongSoundbankTestCase(String strName)
	{
		super(strName);
	}



	protected void checkSynthesizer(Synthesizer synth)
		throws Exception
	{
		WrongSoundbank sb = new WrongSoundbank();
		Instrument instr = sb.new WrongInstrument();
		Patch[] patchlist = new Patch[1];
		patchlist[0] = new Patch(0, 0);

		synth.open();
		boolean bOpen = true;
		try
		{
			assertTrue(constructErrorMessage(synth, "isSoundbankSupported() result wrong", true),
					! synth.isSoundbankSupported(sb));

			try
			{
				synth.loadInstrument(instr);
				fail(constructErrorMessage(synth, "loadInstrument()",
						bOpen));
			}
			catch (IllegalArgumentException e)
			{
				// We expect this exception.
			}

			try
			{
				synth.unloadInstrument(instr);
				fail(constructErrorMessage(synth, "unloadInstrument()",
						bOpen));
			}
			catch (IllegalArgumentException e)
			{
				// We expect this exception.
			}

			try
			{
				synth.remapInstrument(instr, instr);
				fail(constructErrorMessage(synth, "remapInstrument()",
						bOpen));
			}
			catch (IllegalArgumentException e)
			{
				// We expect this exception.
			}

			try
			{
				synth.loadAllInstruments(sb);
				fail(constructErrorMessage(synth, "loadAllInstruments()",
						bOpen));
			}
			catch (IllegalArgumentException e)
			{
				// We expect this exception.
			}

			try
			{
				synth.unloadAllInstruments(sb);
				fail(constructErrorMessage(synth, "unloadAllInstruments()",
						bOpen));
			}
			catch (IllegalArgumentException e)
			{
				// We expect this exception.
			}

			try
			{
				synth.loadInstruments(sb, patchlist);
				fail(constructErrorMessage(synth, "loadInstruments()",
						bOpen));
			}
			catch (IllegalArgumentException e)
			{
				// We expect this exception.
			}

			try
			{
				synth.unloadInstruments(sb, patchlist);
				fail(constructErrorMessage(synth, "unloadInstruments()",
						bOpen));
			}
			catch (IllegalArgumentException e)
			{
				// We expect this exception.
			}
		}
		finally
		{
			synth.close();
		}
	}


	protected static String constructErrorMessage(Synthesizer synth,
			String strMethodName,
			boolean bOpen)
	{
		String strMessage = ": " + "IllegalArgumentException not thrown";
		strMessage += " on " + strMethodName;
		return BaseSynthesizerTestCase.constructErrorMessage(synth,
				strMessage, bOpen);
	}

	
	private class WrongSoundbank implements Soundbank
	{
		public class WrongInstrument extends Instrument
		{
			public WrongInstrument()
			{
				super(WrongSoundbank.this, null, null, null);
			}

			public Object getData()
			{
				return null;
			}
		}

		public String getDescription()
		{
			return null;
		}

		public Instrument getInstrument(Patch patch)
		{
			return new WrongInstrument();
		}

		public Instrument[] getInstruments()
		{
			Instrument[] instruments = new Instrument[1];
			instruments[0] = new WrongInstrument();
			return instruments;
		}

		public String getName()
		{
			return null;
		}

		public SoundbankResource[] getResources()
		{
			return null;
		}

		public String getVendor()
		{
			return null;
		}

		public String getVersion()
		{
			return null;
		}
	}
}



/*** WrongSoundbankTestCase.java ***/
