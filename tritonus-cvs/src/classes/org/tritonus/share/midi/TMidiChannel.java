/*
 *	TMidiChannel.java
 *
 *	This file is part of Tritonus: http://www.tritonus.org/
 */

/*
 *  Copyright (c) 2006 by Matthias Pfisterer
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

package org.tritonus.share.midi;

import javax.sound.midi.MidiChannel;


/**
 * Base class for MidiChannel implementations.
 * 
 * <p>This base class serves two purposes:</p>
 * 
 * <ol>
 * <li>It contains a channel number property so that the MidiChannel
 * object knows its own MIDI channel number.</li>
 *
 * <li>It maps some of the methods to others.</li>
 * </ol>
 *
 * @author Matthias Pfisterer
 */
public abstract class TMidiChannel
implements MidiChannel
{
	private int		m_nChannel;

	protected TMidiChannel(int nChannel)
	{
		m_nChannel = nChannel;
	}


	protected int getChannel()
	{
		return m_nChannel;
	}


	public void noteOff(int nNoteNumber)
	{
		noteOff(nNoteNumber, 0);
	}


	public void programChange(int nBank, int nProgram)
	{
		int nBankMSB = nBank >> 7;
		int nBankLSB = nBank & 0x7F;
		controlChange(0, nBankMSB);
		controlChange(32, nBankLSB);
		programChange(nProgram);
	}


	public void resetAllControllers()
	{
		controlChange(121, 0);
	}


	public void allNotesOff()
	{
		controlChange(123, 0);
	}


	public void allSoundOff()
	{
		controlChange(120, 0);
	}


	public boolean localControl(boolean bOn)
	{
		controlChange(122, bOn ? 127 : 0);
		return getController(122) >= 64;
	}



	public void setMono(boolean bMono)
	{
		// TODO: check this
		controlChange(bMono ? 126 : 127, 0);
	}


	public boolean getMono()
	{
		// TODO: check this
		return getController(126) == 0;
	}


	public void setOmni(boolean bOmni)
	{
		controlChange(bOmni ? 125 : 124, 0);
	}


	public boolean getOmni()
	{
		// TODO: check this
		return getController(125) == 0;
	}
}



/*** TMidiChannel.java ***/
