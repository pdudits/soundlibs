/*
 *	CdparanoiaTest.java
 */


import	javax.sound.sampled.AudioFormat;
import	javax.sound.sampled.AudioSystem;
import	javax.sound.sampled.SourceDataLine;
import	javax.sound.sampled.DataLine;
import	javax.sound.sampled.Line;
import	javax.sound.sampled.LineUnavailableException;

import	org.tritonus.lowlevel.cdda.cdparanoia.Cdparanoia;



public class CdparanoiaTest
{
	public static void main(String[] args)
	{
		String		strDevice = "/dev/cdrom";
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
		Cdparanoia	cdparanoia = new Cdparanoia(strDevice);
		int[]		anValues = new int[2];
		int[]		anStartFrame = new int[100];
		int[]		anLength = new int[100];
		int[]		anType = new int[100];
		boolean[]	abCopy = new boolean[100];
		boolean[]	abPre = new boolean[100];
		int[]		anChannels = new int[100];
		cdparanoia.readTOC(anValues, anStartFrame, anLength, anType, abCopy, abPre, anChannels);
		System.out.println("First track: " + anValues[0]);
		System.out.println("last track: " + anValues[1]);
		int	nTracks = anValues[1] - anValues[0] + 1;
		for (int i = 0; i < nTracks; i++)
		{
			System.out.println("Track " + (i + anValues[0]) + " start frame: " + anStartFrame[i]);
			System.out.println("Track " + (i + anValues[0]) + " length: " + anLength[i]);
			System.out.println("Track " + (i + anValues[0]) + " type: " + anType[i]);
			System.out.println("Track " + (i + anValues[0]) + " copy: " + abCopy[i]);
			System.out.println("Track " + (i + anValues[0]) + " pre: " + abPre[i]);
			System.out.println("Track " + (i + anValues[0]) + " channels: " + anChannels[i]);
		}
		if (! bTocOnly)
		{
			SourceDataLine	line = null;
			AudioFormat	audioFormat = new AudioFormat(
				AudioFormat.Encoding.PCM_SIGNED,
				44100.0F, 16, 2, 4, 44100.0F, false);
			Line.Info	info = new DataLine.Info(SourceDataLine.class, audioFormat);
			byte[]	abData = new byte[2352 * 8];
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
			for (int i = anStart[nTrack - 1]; i < anStart[nTrack ]; i++)
			{
				cdparanoia.readFrame(i, 1, abData);
				line.write(abData, 0, 2352);
			}
		}
		cdparanoia.close();
	}
}


/*** CdparanoiaTest.java ****/
