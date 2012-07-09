/*
 *	MixerTestCase.java
 */

/*
 *  Copyright (c) 2004 by Matthias Pfisterer
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
//import javax.sound.sampled.DataLine;
//import javax.sound.sampled.Line;


/** Class for tests of javax.sound.sampled.Mixer.
 */
public class MixerTestCase
extends BaseMixerTestCase
{
	public MixerTestCase(String strName)
	{
		super(strName);
	}


	public void testGetMixerInfo()
		throws Exception
	{
		Check check = new Check()
			{
				public void check(Mixer mixer)
					throws Exception
				{
					Mixer.Info info = mixer.getMixerInfo();
					assertNotNull("getMixerInfo()", info);
					assertNotNull("MixerInfo.getName()", info.getName());
					assertNotNull("MixerInfo.getVendor()", info.getVendor());
					assertNotNull("MixerInfo.getDescription()", info.getDescription());
					assertNotNull("MixerInfo.getVersion()", info.getVersion());
				}
			};
		checkMixer(check);
	}


	public void testOpenClose()
		throws Exception
	{
		Check check = new Check()
			{
				public void check(Mixer mixer)
					throws Exception
				{
					assertTrue("closed", ! mixer.isOpen());
					mixer.open();
					assertTrue("open", mixer.isOpen());
					mixer.close();
					assertTrue("closed", ! mixer.isOpen());
				}
			};
		checkMixer(check);
	}



}



/*** MixerTestCase.java ***/
