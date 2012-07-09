/*
 *	AlsaCtlTestCase.java
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

import junit.framework.TestCase;

import org.tritonus.lowlevel.alsa.AlsaCtl;



public class AlsaCtlTestCase
extends TestCase
{
	private static final boolean	DEBUG = false;
	private static final String	CARD_NAME_FOR_INDEX_TEST = "LIFE";



	public AlsaCtlTestCase(String strName)
	{
		super(strName);
	}



	public void testGetCards()
	{
		int[]	anCards = AlsaCtl.getCards();
		assertTrue(anCards != null);
		assertTrue(anCards.length == 1);
		assertTrue(anCards[0] >= 0);
	}



	public void testLoadCards()
	{
		int[]	anCards = AlsaCtl.getCards();
		for (int i = 0; i < anCards.length; i++)
		{
			int	nError = AlsaCtl.loadCard(anCards[i]);
			assertTrue(nError >= 0);
		}
	}



	public void testGetIndex()
	{
		int	nIndex = AlsaCtl.getCardIndex(CARD_NAME_FOR_INDEX_TEST);
		if (DEBUG)
		{
			System.out.println("card index: " + nIndex);
		}
		assertTrue(nIndex >= 0);
		int[]	anCards = AlsaCtl.getCards();
		if (DEBUG)
		{
			System.out.println("card index: " + anCards[0]);
		}
		assertTrue(nIndex == anCards[0]);
	}



	public void testGetNames()
	{
		int[]	anCards = AlsaCtl.getCards();
		String	strName = AlsaCtl.getCardName(anCards[0]);
		assertTrue(strName != null && !strName.equals(""));
		String	strLongName = AlsaCtl.getCardLongName(anCards[0]);
		assertTrue(strLongName != null && !strLongName.equals(""));
		assertTrue(!strName.equals(strLongName));
		if (DEBUG)
		{
			System.out.println("card name: " + strName);
			System.out.println("card long name: " + strLongName);
		}
	}
}



/*** AlsaCtlTestCase.java ***/
