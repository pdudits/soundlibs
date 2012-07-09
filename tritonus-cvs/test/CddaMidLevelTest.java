/*
 *	CddaMidLevelTest.java
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

/*
  programs related to cdda:
  command-line extractor
  cd player
  extractor/mp3 encoder
 */

import	java.io.InputStream;
import	java.io.IOException;

import	javax.sound.sampled.AudioFormat;
import	javax.sound.sampled.AudioInputStream;
import	javax.sound.sampled.AudioSystem;
import	javax.sound.sampled.SourceDataLine;
import	javax.sound.sampled.DataLine;
import	javax.sound.sampled.Line;
import	javax.sound.sampled.LineUnavailableException;

import	org.tritonus.lowlevel.cdda.CddaMidLevel;
import	org.tritonus.lowlevel.cdda.CddaUtils;


public class CddaMidLevelTest
{
	public static void main(String[] args)
	{
		boolean		bTocOnly = true;
		int		nTrack = 0;
		if (args.length < 1)
		{
			bTocOnly = true;
		}
		else if (args.length == 1)
		{
			nTrack = Integer.parseInt(args[0]);
			bTocOnly = false;
		}
		CddaMidLevel	cddaMidLevel = CddaUtils.getCddaMidLevel();
		InputStream	tocInputStream = null;
		try
		{
			tocInputStream = cddaMidLevel.getTocAsXml("TODO:");
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		byte[]	abData = new byte[4096];
		int	nRead;
		try
		{
			while ((tocInputStream.read(abData)) >= 0)
			{
				System.out.print(new String(abData));
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		System.out.print("\n");
		if (! bTocOnly)
		{
			AudioInputStream	track = null;
			SourceDataLine		line = null;
			AudioFormat	audioFormat = new AudioFormat(
				AudioFormat.Encoding.PCM_SIGNED,
				44100.0F, 16, 2, 4, 44100.0F, false);
			Line.Info	info = new DataLine.Info(SourceDataLine.class, audioFormat);
			abData = new byte[2352 * 8];
			try
			{
				track = cddaMidLevel.getTrack("TODO:", nTrack);
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			try
			{
				line = (SourceDataLine) AudioSystem.getLine(info);
				line.open();
				line.start();
			}
			catch (LineUnavailableException e)
			{
				e.printStackTrace();
			}
			try
			{
			while ((nRead = track.read(abData)) >= 0)
			{
				line.write(abData, 0, nRead);
			}
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		// cdda.close();
	}
}


/*** CddaMidLevelTest.java ****/
