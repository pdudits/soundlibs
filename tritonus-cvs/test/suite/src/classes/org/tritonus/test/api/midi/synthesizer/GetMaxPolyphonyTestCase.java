/*
 *	GetMaxPolyphonyTestCase.java
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
public class GetMaxPolyphonyTestCase
extends BaseSynthesizerTestCase
{
	public GetMaxPolyphonyTestCase(String strName)
	{
		super(strName);
	}



	protected void checkSynthesizer(Synthesizer synth)
		throws Exception
	{
		int poly;
		synth.open();
		try
		{
			poly = synth.getMaxPolyphony();
			assertTrue(constructErrorMessage(synth, true),
					poly > 0);
		}
		finally
		{
			synth.close();
		}
	}


	private static String constructErrorMessage(Synthesizer synth,
												boolean bOpen)
	{
		return BaseSynthesizerTestCase.constructErrorMessage(synth,
				"getMaxPolyphony() result not positive", bOpen);
	}
}



/*** GetMaxPolyphonyTestCase.java ***/
