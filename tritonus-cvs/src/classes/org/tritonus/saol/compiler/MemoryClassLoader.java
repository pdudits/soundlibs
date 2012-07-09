/*
 *	MemoryClassLoader.java
 *
 *	This file is part of Tritonus: http://www.tritonus.org/
 */

/*
 *  Copyright (c) 2002 by Matthias Pfisterer
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

/*
|<---            this code is formatted to fit into 80 columns             --->|
*/

package org.tritonus.saol.compiler;

import java.io.File;
import java.io.FileInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;



public class MemoryClassLoader
extends ClassLoader
{
	public Class findClass(String strName,
			       byte[] classData)
	{
		Class	cls = defineClass(strName, classData, 0, classData.length);
		return cls;
	}


	/*	For testing
	 */
	public static void main(String[] args)
	{
		try
		{
			FileInputStream	fis = new FileInputStream(new File("Instrument.class"));
			ByteArrayOutputStream	baos = new ByteArrayOutputStream();
			byte[]	buffer = new byte[4096];
			while (true)
			{
				int	nRead = fis.read(buffer);
				if (nRead == -1)
				{
					break;
				}
				baos.write(buffer, 0, nRead);
			}
			MemoryClassLoader	mcl = new MemoryClassLoader();
			Class	cls = mcl.findClass("Instrument", baos.toByteArray());
			System.out.println("class loaded: " + cls.getName());
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}


/*** MemoryClassLoader.java ***/
