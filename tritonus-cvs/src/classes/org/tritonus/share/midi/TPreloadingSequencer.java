/*
 *	TPreloadingSequencer.java
 *
 *	This file is part of Tritonus: http://www.tritonus.org/
 */

/*
 *  Copyright (c) 2003 - 2004 by Matthias Pfisterer
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

package org.tritonus.share.midi;

import java.util.Collection;

import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiDevice;

import org.tritonus.share.TDebug;



/** Base class for sequencers that work with an internal queue.
	To be more precise, this is the base class for sequencers that
	do not load the complete Sequence to internal data structures before start,
	but take single events from the Sequence and put them to the sequencing
	queue while running.
 */
public abstract class TPreloadingSequencer
extends TSequencer
{
	/** The default value for {@link m_nLatency}.
		This default value is set in the constructor.
	 */
	private static final int DEFAULT_LATENCY = 100;

	/**
	 */
	private int m_nLatency;

	@SuppressWarnings("unused")
	private Thread				m_loaderThread;

	/**
	   Sets the latency to the default value.
	 */
	protected TPreloadingSequencer(MidiDevice.Info info,
								   Collection<SyncMode> masterSyncModes,
								   Collection<SyncMode> slaveSyncModes)
	{
		super(info, masterSyncModes,
			  slaveSyncModes);
		if (TDebug.TraceSequencer) { TDebug.out("TPreloadingSequencer.<init>(): begin"); }
		m_nLatency = DEFAULT_LATENCY;
		if (TDebug.TraceSequencer) { TDebug.out("TPreloadingSequencer.<init>(): end"); }
	}






	/** Sets the preloading intervall.
		This is the time span between preloading events to an internal
		queue and playing them. This intervall should be kept constant
		by the implementation. However, this cannot be guaranteed.
	*/
	public void setLatency(int nLatency)
	{
		// TODO: preload if latency becomes shorter
		m_nLatency = nLatency;
	}



	/** Get the preloading intervall.

	@return the preloading intervall in milliseconds, or -1 if the sequencer
	doesn't repond to changes in the <code>Sequence</code> at all.
	*/
	public int getLatency()
	{
		return m_nLatency;
	}


	// currently not called by subclasses. order has to be assured (subclass first)
	protected void openImpl()
	{
		if (TDebug.TraceSequencer) { TDebug.out("AlsaSequencer.openImpl(): begin"); }
		// m_loaderThread = new LoaderThread();
		// m_loaderThread.start();
	}



	/**	Put a message into the queue.
		This is Claus-Dieter's special method: it puts the message to
		the ALSA queue for delivery at the specified time.
		The time has to be given in ticks according to the resolution
		of the currently active Sequence. For this method to work,
		the Sequencer has to be started. The message is delivered
		the same way as messages from a Sequence, i.e. to all
		registered Transmitters. If the current queue position (as
		returned by getTickPosition()) is
		already behind the desired schedule time, the message is
		ignored.

		@param message the MidiMessage to put into the queue.

		@param lTick the desired schedule time in ticks.
	*/
	public abstract void sendMessageTick(MidiMessage message, long lTick);

}



/*** TPreloadingSequencer.java ***/
