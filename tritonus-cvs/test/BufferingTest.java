/*
 *	BufferingTest.java
 */

/*
 *  Copyright (c) 1999, 2000 by Matthias Pfisterer <Matthias.Pfisterer@web.de>
 *
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
 *
 */


import	java.io.BufferedInputStream;
import	java.io.ByteArrayInputStream;
import	java.io.FileInputStream;
import	java.io.InputStream;
import	java.io.IOException;



public class BufferingTest
{
	public static void main(String[] args)
		throws IOException
	{
// 		byte[]	abData = new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
// 		ByteArrayInputStream	bais = new ByteArrayInputStream(abData);
// 		System.out.println(bais.markSupported());
// 		bais.mark(15);
// 		bais.reset();

		FileInputStream	fis = new FileInputStream("BufferingTest.java");
		System.out.println("FileInputStream supports mark: " + fis.markSupported());

		BufferedInputStream	bis = new BufferedInputStream(fis, 5);
		byte[]	abRead1 = new byte[9];
		byte[]	abRead2 = new byte[9];
		byte[]	abRead3 = new byte[9];
		byte[]	abRead4 = new byte[9];
		bis.mark(9);
		bis.read(abRead1);
		bis.mark(9);
		bis.read(abRead2);
		bis.reset();
		bis.read(abRead3);
		bis.reset();
		bis.read(abRead4);

		for (int i = 0; i < abRead1.length; i++)
		{
			if (abRead1[i] != abRead4[i])
			{
				System.out.println("1 difference!!");
			}
		}
		for (int i = 0; i < abRead1.length; i++)
		{
			if (abRead2[i] != abRead3[i])
			{
				System.out.println("2 difference!!");
			}
		}
	}
}



/*** BufferingTest.java ***/
