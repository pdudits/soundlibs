/*
 *	PageTestCase.java
 */

/*
 *  Copyright (c) 2005 by Matthias Pfisterer
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

package org.tritonus.test.tritonus.lowlevel.pogg;

import junit.framework.TestCase;

import org.tritonus.lowlevel.pogg.Page;


/**	Tests for classes org.tritonus.lowlevel.pogg.Page.
 */
public class PageTestCase
extends TestCase
{
	/* First and last, uncontinued, pos 0, serial 0x04030201,
	   page 0, 1 segment, 1 packet */
	private static final byte[] HEADER1 = new byte[]
	{
		0x4f, 0x67, 0x67, 0x53, 0, 0x06,
		0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
		0x01, 0x02, 0x03, 0x04, 0, 0, 0, 0,
		0x15, (byte) 0xed, (byte) 0xec, (byte) 0x91,
		1, 17
	};


	/* First , uncontinued, pos -1, serial 0x04030201,
	   page 8, 7 segments, 1 packet */
	private static final byte[] HEADER2 = new byte[]
	{
		0x4f, 0x67, 0x67, 0x53, 0, 0x02,
		(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
		(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, 
		0x01, 0x02, 0x03, 0x04, 8, 0, 0, 0,
		0x15, (byte) 0xed, (byte) 0xec, (byte) 0x91,
		7, (byte) 255, (byte) 255, (byte) 255, (byte) 255,
		(byte) 255, (byte) 255, 12
	};


	public PageTestCase(String strName)
	{
		super(strName);
	}



	public void testSetData()
		throws Exception
	{
		Page p = new Page();
		byte[] abHeader = new byte[1024];
		for (int i = 0; i < abHeader.length; i++)
		{
			abHeader[i] = (byte) (i + 128);
		}
		byte[] abBody = new byte[1024];
		for (int i = 0; i < abBody.length; i++)
		{
			abBody[i] = (byte) i;
		}
		p.setData(abHeader, 0, abHeader.length,
				  abBody, 0, abBody.length);
		checkData(p, "set data test", abHeader, abBody);
	}



	public void testSetDataOffset()
		throws Exception
	{
		Page p = new Page();
		byte[] abHeader = new byte[102];
		for (int i = 0; i < abHeader.length; i++)
		{
			abHeader[i] = (byte) (i + 128);
		}
		byte[] abBody = new byte[1024];
		for (int i = 0; i < abBody.length; i++)
		{
			abBody[i] = (byte) i;
		}
		p.setData(abHeader, 12, 88,
				  abBody, 511, 513);
		byte[] abHeaderCompare = new byte[88];
		System.arraycopy(abHeader, 12, abHeaderCompare, 0, 88);
		byte[] abBodyCompare = new byte[513];
		System.arraycopy(abBody, 511, abBodyCompare, 0, 513);
		checkData(p, "set data offset test", abHeaderCompare, abBodyCompare);
	}



	public void testHeaderProperties()
		throws Exception
	{
		checkHeaderProperties("header properties test 1", HEADER1,
							  0, false, 1,
							  true, true, 0L,
							  0x04030201, 0);
		checkHeaderProperties("header properties test 2", HEADER2,
							  0, false, 1,
							  true, false, -1L,
							  0x04030201, 8);
	}



	private void checkHeaderProperties(
		String strMessage, byte[] abHeader,
		int nVersionExpected, boolean bContinuedExpected, int nPacketsExpected,
		boolean bBosExpected, boolean bEosExpected, long lGranulePosExpected,
		int nSerialNoExpected, int nPageNoExpected)
		throws Exception
	{
		Page p = new Page();
		byte[] abData = new byte[12];
		p.setData(abHeader, 0, abHeader.length,
				  abData, 0, abData.length);

		assertEquals(constructErrorMessage(strMessage, "version"),
					 nVersionExpected, p.getVersion());
		assertEquals(constructErrorMessage(strMessage, "continued flag"),
					 bContinuedExpected, p.isContinued());
		assertEquals(constructErrorMessage(strMessage, "packets"),
					 nPacketsExpected, p.getPackets());
		assertEquals(constructErrorMessage(strMessage, "bos flag"),
					 bBosExpected, p.isBos());
		assertEquals(constructErrorMessage(strMessage, "eos flag"),
					 bEosExpected, p.isEos());
		assertEquals(constructErrorMessage(strMessage, "granulepos"),
					 lGranulePosExpected, p.getGranulePos());
		assertEquals(constructErrorMessage(strMessage, "serialno"),
					 nSerialNoExpected, p.getSerialNo());
		assertEquals(constructErrorMessage(strMessage, "pageno"),
					 nPageNoExpected, p.getPageNo());
	}


	private void checkData(Page p, String strMessage,
						   byte[] abHeaderExpected, byte[] abBodyExpected)
		throws Exception
	{
		assertTrue(constructErrorMessage(strMessage, "header content"),
				   equals(abHeaderExpected, p.getHeader()));
		assertTrue(constructErrorMessage(strMessage, "body content"),
				   equals(abBodyExpected, p.getBody()));
	}



	private static boolean equals(byte[] b1, byte[] b2)
	{
		if (b1 == null && b2 == null)
			return true;
		if (b1 != null)
			return equals(b1, 0, b2, 0, b1.length);
		return false;
	}



	private static boolean equals(byte[] b1, int nOffset1,
						   byte[] b2, int nOffset2,
						   int nLength)
	{
		if (b1 == null && b2 == null)
			return true;
		if (nOffset1 + nLength > b1.length || nOffset2 + nLength > b2.length)
			return false;
		for (int i = 0; i < nLength; i++)
		{
			if (b1[nOffset1 + i] != b2[nOffset2 + i])
				return false;
		}
		return true;
	}



	private static String constructErrorMessage(String s1, String s2)
	{
		return s1 + ": " + s2;
	}
}



/*** PageTestCase.java ***/
