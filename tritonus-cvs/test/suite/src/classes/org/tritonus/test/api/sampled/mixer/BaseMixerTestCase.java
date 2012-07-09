/*
 *	BaseMixerTestCase.java
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

package org.tritonus.test.api.sampled.mixer;

import javax.sound.sampled.Mixer;
import javax.sound.sampled.AudioSystem;

import junit.framework.TestCase;


/**	Base class for tests of javax.sound.sampled.Mixer.
 */
public abstract class BaseMixerTestCase
extends TestCase
{
	protected BaseMixerTestCase(String strName)
	{
		super(strName);
	}



	/**	Iterate over all available Mixers.
	*/
	protected void checkMixer(Check check)
		throws Exception
	{
		Mixer.Info[] infos = AudioSystem.getMixerInfo();
		for (int i = 0; i < infos.length; i++)
		{
			Mixer mixer = AudioSystem.getMixer(infos[i]);
			System.out.println("testing mixer: " + mixer);
			check.check(mixer);
		}
	}



	/** Get the prefix for error messages (containing the sequencer's name).
	 */
	protected static String getMessagePrefix(Mixer mixer)
	{
		return mixer.getMixerInfo().getName();
	}


	protected interface Check
	{
		public void check(Mixer mixer)
			throws Exception;
	}
}



/*** BaseMixerTestCase.java ***/
