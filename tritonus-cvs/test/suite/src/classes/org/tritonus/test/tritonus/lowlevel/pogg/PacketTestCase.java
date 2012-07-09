/*
 *	PacketTestCase.java
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

import java.util.Arrays;

import junit.framework.TestCase;

import org.tritonus.lowlevel.pogg.Packet;


/**	Tests for classes org.tritonus.lowlevel.pogg.Packet.
 */
public class PacketTestCase
extends TestCase
{
	public PacketTestCase(String strName)
	{
		super(strName);
	}



// 	public void testClear()
// 		throws Exception
// 	{
// 		Packet p = new Packet();
// 		p.clear();
// 		checkPacket(p, "clear test", false, false, 0, 0, null);
// 	}



	public void testClear()
		throws Exception
	{
		Packet p = new Packet();
		p.setData(new byte[3], 0, 3);
		p.setFlags(true, true, 99, 100);
		p.clear();
		checkPacket(p, "clear test", false, false, 0, 0, null);
	}



	public void testSetData()
		throws Exception
	{
		Packet p = new Packet();
		byte[] abData = new byte[1024];
		for (int i = 0; i < abData.length; i++)
		{
			abData[i] = (byte) i;
		}
		p.setData(abData, 0, abData.length);
		checkPacket(p, "set data test", false, false, 0, 0, abData);
	}



	public void testSetDataTruncated()
		throws Exception
	{
		Packet p = new Packet();
		byte[] abData = new byte[1024];
		for (int i = 0; i < abData.length; i++)
		{
			abData[i] = (byte) i;
		}
		p.setData(abData, 0, abData.length / 2);
		byte[] abCompare = new byte[abData.length / 2];
		System.arraycopy(abData, 0, abCompare, 0, abData.length / 2);
		checkPacket(p, "set data truncated test", false, false, 0, 0, abCompare);
	}



	public void testSetFlags()
		throws Exception
	{
		checkFlags("set flags test 1", true, false, 65555L, 0);
		checkFlags("set flags test 2", false, true, 0, 0);
		checkFlags("set flags test 3", false, false, Long.MAX_VALUE, 0);
		checkFlags("set flags test 4", true, true, Long.MIN_VALUE, 0);
	}



	private void checkFlags(String strMessage, boolean bBos, boolean bEos,
							long lGranulePos, long lPacketNo)
		throws Exception
	{
		Packet p = new Packet();
		byte[] abData = new byte[0];
		p.setData(abData, 0, abData.length);
		p.setFlags(bBos, bEos, lGranulePos, lPacketNo);
		checkPacket(p, strMessage, bBos, bEos, lGranulePos, lPacketNo, abData);
	}



	private void checkPacket(Packet p, String strMessage, boolean bBosExpected,
							 boolean bEosExpected, long lGranulePosExpected,
							 long lPacketNoExpected, byte[] abDataExpected)
		throws Exception
	{
		assertEquals(constructErrorMessage(strMessage, "bos flag"),
					 bBosExpected, p.isBos());
		assertEquals(constructErrorMessage(strMessage, "eos flag"),
					 bEosExpected, p.isEos());
		assertEquals(constructErrorMessage(strMessage, "granulepos"),
					 lGranulePosExpected, p.getGranulePos());
		assertEquals(constructErrorMessage(strMessage, "packetno"),
					 lPacketNoExpected, p.getPacketNo());
		System.out.println("data: " + p.getData());
		assertTrue(constructErrorMessage(strMessage, "data content"),
					 equals(abDataExpected, p.getData()));
	}



	private static boolean equals(Packet p1, Packet p2)
	{
		return Arrays.equals(p1.getData(), p2.getData()) &&
			p1.isBos() == p2.isBos() &&
			p1.isEos() == p2.isEos() &&
			p1.getGranulePos() == p2.getGranulePos() &&
			p1.getPacketNo() == p2.getPacketNo();
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



/*** PacketTestCase.java ***/
