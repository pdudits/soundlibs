/*
 *	IllegalStateTestCase.java
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


/**	Tests for class javax.sound.midi.Synthesizer.
 */
public class IllegalStateTestCase
extends BaseSynthesizerTestCase
{
	public IllegalStateTestCase(String strName)
	{
		super(strName);
	}



	protected void checkSynthesizer(Synthesizer synth)
		throws Exception
	{
		// Synthesizer is closed
		checkMethods(synth, false);

		// Synthesizer open
		synth.open();

		checkMethods(synth, true);

		// clean up
		synth.close();
	}


	private void checkMethods(Synthesizer synth, boolean bOpen)
		throws Exception
	{
		boolean bExpectingException = false;
		checkMethod(synth, "getMaxPolyphony()", bExpectingException, bOpen);
		checkMethod(synth, "getLatency()", bExpectingException, bOpen);
		checkMethod(synth, "getChannels()", bExpectingException, bOpen);
		checkMethod(synth, "getVoiceStatus()", bExpectingException, bOpen);
		checkMethod(synth, "getDefaultSoundbank()", bExpectingException, bOpen);
		checkMethod(synth, "getAvailableInstruments()", bExpectingException, bOpen);
		checkMethod(synth, "getLoadedInstruments()", bExpectingException, bOpen);
	}


	private void checkMethod(Synthesizer synth, String strMethodName,
							 boolean bExceptionExpected, boolean bOpen)
		throws Exception
	{
		try
		{
			if ("getMaxPolyphony()".equals(strMethodName))
				synth.getMaxPolyphony();
			else if ("getLatency()".equals(strMethodName))
				synth.getLatency();
			else if ("getChannels()".equals(strMethodName))
				synth.getChannels();
			else if ("getVoiceStatus()".equals(strMethodName))
				synth.getVoiceStatus();
			else if ("getDefaultSoundbank()".equals(strMethodName))
				synth.getDefaultSoundbank();
			else if ("getAvailableInstruments()".equals(strMethodName))
				synth.getAvailableInstruments();
			else if ("getLoadedInstruments()".equals(strMethodName))
				synth.getLoadedInstruments();
			else
				throw new RuntimeException("unknown method name");
			if (bExceptionExpected)
			{
				fail(constructErrorMessage(synth, strMethodName, bExceptionExpected, bOpen));
			}
		}
		catch (IllegalStateException e)
		{
			if (! bExceptionExpected)
			{
				fail(constructErrorMessage(synth, strMethodName, bExceptionExpected, bOpen));
			}
		}
	}


	private static String constructErrorMessage(Synthesizer synth,
												String strMethodName,
												boolean bExceptionExpected,
												boolean bOpen)
	{
		String strMessage = ": IllegalStateException ";
		strMessage += (bExceptionExpected ? "not thrown" : "thrown");
		strMessage += " on " + strMethodName;
		return BaseSynthesizerTestCase.constructErrorMessage(synth,
				strMessage, bOpen);
	}
}



/*** IllegalStateTestCase.java ***/
