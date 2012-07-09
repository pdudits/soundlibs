/*
 *	AlsaMixerTestCase.java
 */

/*
 *  Copyright (c) 2001 - 2002 by Matthias Pfisterer <Matthias.Pfisterer@web.de>
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

package org.tritonus.test.alsa;

import java.util.List;

import junit.framework.TestCase;

//import org.tritonus.lowlevel.alsa.AlsaCtl;
import org.tritonus.lowlevel.alsa.AlsaMixer;



public class AlsaMixerTestCase
extends TestCase
{
	private static final boolean	DEBUG = true;



	public AlsaMixerTestCase(String strName)
	{
		super(strName);
	}



	public void testOpenClose()
		throws Exception
	{
		int	nDefaultMixerCard = 0;
		String	strMixerName = "hw:" + nDefaultMixerCard;
		AlsaMixer	mixer = new AlsaMixer(strMixerName);
		assertTrue(mixer != null);
		mixer.close();
		// Intentionally a second time to test idempotence of close().
		mixer.close();
	}



	public void testControls()
		throws Exception
	{
		int	nDefaultMixerCard = 0;
		String	strMixerName = "hw:" + nDefaultMixerCard;
		AlsaMixer	mixer = new AlsaMixer(strMixerName);
		List	controlsList = null;	// mixer.getControls();
		assertTrue(controlsList != null);
		assertTrue(controlsList.size() > 0);
		mixer.close();
	}

}



/*** AlsaMixerTestCase.java ***/
