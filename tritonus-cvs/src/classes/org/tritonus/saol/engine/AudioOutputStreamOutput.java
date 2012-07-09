/*
 *	AudioOutputStreamOutput.java
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

package org.tritonus.saol.engine;


import java.io.IOException;

/*
 *      Tritonus classes.
 *      Using these makes the program not portable to other
 *      Java Sound implementations.
 */
import  org.tritonus.share.TDebug;
import org.tritonus.share.sampled.TConversionTool;
import  org.tritonus.share.sampled.file.AudioOutputStream;



public class AudioOutputStreamOutput
extends Bus
implements SystemOutput
{
	private AudioOutputStream	m_audioOutputStream;
	private byte[]			m_abBuffer;



	public AudioOutputStreamOutput(AudioOutputStream audioOutputStream)
	{
		super(audioOutputStream.getFormat().getChannels());
		m_audioOutputStream = audioOutputStream;
		m_abBuffer = new byte[audioOutputStream.getFormat().getFrameSize()];
	}




	public void emit()
		throws IOException
	{
		float[]	afValues = getValues();
		boolean	bBigEndian = m_audioOutputStream.getFormat().isBigEndian();
		int	nOffset = 0;
		for (int i = 0; i < afValues.length; i++)
		{
			float	fOutput = Math.max(Math.min(afValues[i], 1.0F), -1.0F);
			// assumes 16 bit linear
			int	nOutput = (int) (fOutput * 32767.0F);
			TConversionTool.shortToBytes16((short) nOutput, m_abBuffer, nOffset, bBigEndian);
			nOffset += 2;
		}
		m_audioOutputStream.write(m_abBuffer, 0, m_abBuffer.length);
	}



	public void close()
		throws IOException
	{
		m_audioOutputStream.close();
	}
}



/*** AudioOutputStreamOutput.java ***/
