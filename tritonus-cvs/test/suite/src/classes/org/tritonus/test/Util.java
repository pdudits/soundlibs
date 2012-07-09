/*
 *	Util.java
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

package org.tritonus.test;

import	java.io.File;
import	java.io.FileInputStream;
import	java.io.IOException;



public class Util
{
	public static void dumpByteArray(byte[] ab)
	{
		for (int i = 0; i < ab.length; i++)
		{
			System.out.print(" " + ab[i]);
		}
		System.out.println("");
	}



	// returns true if equal
	public static boolean compareByteArrays(byte[] ab1, int nOffset1, byte[] ab2, int nOffset2, int nLength)
	{
		for (int i = 0; i < nLength; i++)
		{
			if (ab1[i + nOffset1] != ab2[i +  nOffset2])
			{
				return false;
			}
		}
		return true;
	}



	public static byte[] getByteArrayFromFile(File file)
		throws IOException
	{
		long	lLength = file.length();
		byte[]	abData = new byte[(int) lLength];
		FileInputStream	fis = new FileInputStream(file);
		int	nBytesRemaining = (int) lLength;
		int	nWriteStart = 0;
		while (nBytesRemaining > 0)
		{
			int	nBytesRead = fis.read(abData, nWriteStart, nBytesRemaining);
			nBytesRemaining -= nBytesRead;
			nWriteStart += nBytesRead;
		}
		fis.close();
		return abData;
	}



	public static void sleep(long milliseconds)
	{
		try
		{
			Thread.sleep(milliseconds);
		}
		catch (InterruptedException e)
		{
		}
	}
}



/*** Util.java ***/
