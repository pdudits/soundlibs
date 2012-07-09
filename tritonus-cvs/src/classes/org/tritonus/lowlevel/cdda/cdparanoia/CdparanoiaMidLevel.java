/*
 *	CdparanoiaMidLevel.java
 *
 *	This file is part of Tritonus: http://www.tritonus.org/
 */

/*
 *  Copyright (c) 2001 by Matthias Pfisterer
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

/*
|<---            this code is formatted to fit into 80 columns             --->|
*/

package org.tritonus.lowlevel.cdda.cdparanoia;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.PrintStream;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import org.tritonus.share.TDebug;
import org.tritonus.lowlevel.cdda.CddaMidLevel;
import org.tritonus.share.sampled.convert.TAsynchronousFilteredAudioInputStream;



public class CdparanoiaMidLevel
implements CddaMidLevel
{
	private static int		PCM_FRAMES_PER_CDDA_FRAME = 588;
	private static AudioFormat	CDDA_FORMAT = new AudioFormat(
		AudioFormat.Encoding.PCM_SIGNED,
		44100.0F, 16, 2, 4, 44100.0F, false);




	public CdparanoiaMidLevel()
	{
		if (TDebug.TraceCdda) { TDebug.out("CdparanoiaMidLevel.<init>(): begin"); }
		if (TDebug.TraceCdda) { TDebug.out("CdparanoiaMidLevel.<init>(): end"); }
	}



	public Iterator getDevices()
	{
		if (TDebug.TraceCdda) { TDebug.out("CdparanoiaMidLevel.getDevices(): begin"); }
		// TODO: hack!! should be replaced by a real search
		String[]	astrDevices = {"/dev/cdrom"};
		// TODO: should make list immutable
		List		devicesList = Arrays.asList(astrDevices);
		Iterator	iterator = devicesList.iterator();
		if (TDebug.TraceCdda) { TDebug.out("CdparanoiaMidLevel.getDevices(): end"); }
		return iterator;
	}



	public String getDefaultDevice()
	{
		return "/dev/cdrom";
	}



	public InputStream getTocAsXml(String strDevice)
		throws IOException
	{
		if (TDebug.TraceCdda) { TDebug.out("CdparanoiaMidLevel.getTocAsXML(): begin"); }
		ByteArrayOutputStream	baos = new ByteArrayOutputStream();
		PrintStream		out = new PrintStream(baos);
		int[]			anValues = new int[2];
		int[]			anStartFrame = new int[100];
		int[]			anLength = new int[100];
		int[]			anType = new int[100];
		boolean[]		abAudio = new boolean[100];
		boolean[]		abCopy = new boolean[100];
		boolean[]		abPre = new boolean[100];
		int[]			anChannels = new int[100];
		Cdparanoia		cdparanoia = new Cdparanoia(strDevice);
		cdparanoia.readTOC(anValues,
				    anStartFrame,
				    anLength,
				    anType,
				    abAudio,
				    abCopy,
				    abPre,
				    anChannels);

		int	nTracks = anValues[1] - anValues[0] + 1;
		out.println("<tracklist>");
		for (int i = 0; i < nTracks; i++)
		{
			out.print("<track");
			out.print(" id=\"" + (i + anValues[0]) + "\"");
			out.print(" start=\"" + anStartFrame[i] + "\"");
			out.print(" length=\"" + anLength[i] + "\"");
			out.print(" type=\"" + anType[i] + "\"");
			out.print(" audio=\"" + abAudio[i] + "\"");
			out.print(" copy=\"" + abCopy[i] + "\"");
			out.print(" pre=\"" + abPre[i] + "\"");
			out.print(" channels=\"" + anChannels[i] + "\" />\n");
		}
		out.println("</tracklist>");
		byte[]	abData = baos.toByteArray();
		ByteArrayInputStream	bais = new ByteArrayInputStream(abData);
		cdparanoia.close();
		if (TDebug.TraceCdda) { TDebug.out("CdparanoiaMidLevel.getTocAsXML(): end"); }
		return bais;
	}



	public AudioInputStream getTrack(String strDevice, int nTrack)
		throws IOException
	{
		if (TDebug.TraceCdda) { TDebug.out("CdparanoiaMidLevel.getInputStream(): begin"); }
		AudioInputStream	audioInputStream = new CddaAudioInputStream(strDevice, nTrack);
		if (TDebug.TraceCdda) { TDebug.out("CdparanoiaMidLevel.getInputStream(): end"); }
		return audioInputStream;
	}



	private class CddaAudioInputStream
	extends	TAsynchronousFilteredAudioInputStream
	{
		private static final int	BUFFER_SIZE = CddaMidLevel.FRAME_SIZE;


		/**
		 */
		private Cdparanoia	m_cdparanoia;


		/**
		   This variable gets initialized to the total number of cdda
		   frames for the respective track. On reading of a frame, it
		   decremented untill zero.
		*/
		private int		m_nCddaFrameCount;


		/**
		   This variable contains the number of the cdda
		   frame where the current track begins.
		*/
		private int		m_nStartFrame;


		/**
		   This variable contains the number of the cdda
		   frame where the current track begins.
		*/
		private int		m_nEndFrame;


		private int		m_nLengthCount;


		/**	Buffer for reading cdda frames.
		 */
		private byte[]		m_abData;


		/**	Track number.
		 */
		private int		m_nTrack;

		public CddaAudioInputStream(String strDevice, int nTrack)
		{
			super(CDDA_FORMAT,
			      AudioSystem.NOT_SPECIFIED
			      /*getTrackLengthInPcmFrames()*/);
			if (TDebug.TraceCdda) { TDebug.out("CddaAudioInputStream.<init>(): begin"); }
			m_nTrack = nTrack;
			int[]			anValues = new int[2];
			int[]			anStartFrame = new int[100];
			int[]			anLength = new int[100];
			int[]			anType = new int[100];
			boolean[]		abAudio = new boolean[100];
			boolean[]		abCopy = new boolean[100];
			boolean[]		abPre = new boolean[100];
			int[]			anChannels = new int[100];
			m_cdparanoia = new Cdparanoia(strDevice);
			m_cdparanoia.readTOC(anValues,
					      anStartFrame,
					      anLength,
					      anType,
					      abAudio,
					      abCopy,
					      abPre,
					      anChannels);

			m_nCddaFrameCount = 0;
			int	nTrackIndex = getTrack() - anValues[0];
			m_nStartFrame = anStartFrame[nTrackIndex];
			m_abData = new byte[BUFFER_SIZE];
			m_nLengthCount = anLength[nTrackIndex];
			// !!! writing to protected superclass variable !!!
			frameLength = m_nLengthCount * PCM_FRAMES_PER_CDDA_FRAME;
			if (TDebug.TraceCdda) { TDebug.out("CddaAudioInputStream.<init>(): track length in cdda frames: " + m_nLengthCount); }
			m_cdparanoia.prepareTrack(getTrack());
			if (TDebug.TraceCdda) { TDebug.out("CddaAudioInputStream.<init>(): end"); }
		}



		private long getTrackLengthInPcmFrames()
		{
			int	nCddaFrames = getTrackLengthInCddaFrames();
			long	lLength = nCddaFrames * PCM_FRAMES_PER_CDDA_FRAME;
			return lLength;
		}



		private int getTrackLengthInCddaFrames()
		{
			int	nLength = getEndFrame() - getStartFrame() + 1;
			return nLength;
		}



		private int getStartFrame()
		{
			return m_nStartFrame;
		}



		private int getEndFrame()
		{
			return m_nEndFrame;
		}



		private int getTrack()
		{
			return m_nTrack;
		}



		private int getCurrentFrameNumber()
		{
			return m_nCddaFrameCount + m_nStartFrame;
		}



		private void increaseCurrentFrameNumber()
		{
			m_nCddaFrameCount++;
		}



// 		private boolean isEndOfTrackReached()
// 		{
// 			return m_nCddaFrameCount >= getTrackLengthInCddaFrames();
// 		}



		private boolean isEndOfTrackReached()
		{
			return m_nLengthCount == 0;
		}



		public void execute()
		{
			if (TDebug.TraceCdda) { TDebug.out("CddaAudioInputStream.execute(): begin"); }
			if (! isEndOfTrackReached())
			{
				if (TDebug.TraceCdda) { TDebug.out("CddaAudioInputStream.execute(): begin"); }
				while (getCircularBuffer().availableWrite() >= BUFFER_SIZE &&
				       ! isEndOfTrackReached())
				{
					if (TDebug.TraceCdda) { TDebug.out("CddaAudioInputStream.execute(): before readFrame()"); }
					m_cdparanoia.readNextFrame(1, m_abData);
					m_nLengthCount--;
					if (TDebug.TraceCdda) { TDebug.out("CddaAudioInputStream.execute(): length count now: " + m_nLengthCount); }
					if (TDebug.TraceCdda) { TDebug.out("CddaAudioInputStream.execute(): after readFrame(), before cb.write()"); }
					getCircularBuffer().write(m_abData, 0, BUFFER_SIZE);
					if (TDebug.TraceCdda) { TDebug.out("CddaAudioInputStream.execute(): after cb.write()"); }
					increaseCurrentFrameNumber();
				}
			}
			else
			{
				if (TDebug.TraceCdda) { TDebug.out("CddaAudioInputStream.execute(): end of cdda track"); }
				getCircularBuffer().close();
			}

			if (TDebug.TraceCdda) { TDebug.out("CddaAudioInputStream.execute(): end"); }
		}



		public void close()
			throws IOException
		{
			m_cdparanoia.close();
			super.close();
			// TODO: close cdda?
			// m_encodedStream.close();
		}
	}
}


/*** CdparanoiaMidLevel.java ****/
