/*
 *	JavaSequencer.java
 *
 *	This file is part of Tritonus: http://www.tritonus.org/
 */

/*
 *  Copyright (c) 2000 - 2003 by Matthias Pfisterer
 *  Copyright (c) 2003 by Gabriele Mondada
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

package org.tritonus.midi.device.java;

import java.util.Arrays;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MetaMessage;
import javax.sound.midi.Sequence;
import javax.sound.midi.Track;
import javax.sound.midi.InvalidMidiDataException;

import org.tritonus.share.TDebug;
import org.tritonus.share.midi.MidiUtils;
import org.tritonus.share.midi.TSequencer;


/** Sequencer implementation in pure Java.
 */
public class JavaSequencer
extends TSequencer
implements Runnable
{
	private static final SyncMode[]	MASTER_SYNC_MODES = {SyncMode.INTERNAL_CLOCK};
	private static final SyncMode[]	SLAVE_SYNC_MODES = {SyncMode.NO_SYNC};

	// internal states
	/** not running */
	private static final int	STATE_STOPPED = 0;
	/** starting, awake thread */
	private static final int	STATE_STARTING = 1;
	/** running */
	private static final int	STATE_STARTED = 2;
	/** stopping */
	private static final int	STATE_STOPPING = 3;
	/** closing, terminate thread */
	private static final int	STATE_CLOSING = 4;

	private Thread		m_thread;
	private long		m_lMicroSecondsPerTick;

	private int[]		m_anTrackPositions;
	private long		m_lTickPosition;
	private long		m_lStartTime;

	/** Internal state of the sequencer.
		As values, the symbolic constants STATE_*
		are used.
	*/
	private int			m_nPhase;
	private boolean		m_bTempoChanged;

	/** The clock to use as time base for this sequencer.
		This is commonly intialized in the constructor,
		but can also be set with {@link #setClock}.
	 */
	private Clock		m_clock;

	/** How long to sleep in the main loop.
		The value is initialized in the constructor by reading a
		system property.
	 */
	private long m_lSleepInterval;



	public JavaSequencer(MidiDevice.Info info)
	{
		super(info,
			  Arrays.asList(MASTER_SYNC_MODES),
			  Arrays.asList(SLAVE_SYNC_MODES));
		if (TDebug.TraceSequencer) { TDebug.out("JavaSequencer.<init>(): begin"); }
		String strVersion = System.getProperty("java.version");
		if (strVersion.indexOf("1.4.2") != -1)
		{
			setClock(new SunMiscPerfClock());
		}
		else
		{
			setClock(new SystemCurrentTimeMillisClock());
		}
		String strOS = System.getProperty("os.name");
		if (strOS.equals("Linux"))
		{
			m_lSleepInterval = 0;
		}
		else
		{
			m_lSleepInterval = 1;
		}
		if (TDebug.TraceSequencer) { TDebug.out("JavaSequencer.<init>(): end"); }
	}



	protected void openImpl()
	{
		if (TDebug.TraceSequencer) { TDebug.out("JavaSequencer.openImpl(): begin"); }
		m_nPhase = STATE_STOPPED;
		m_thread = new Thread(this);
		m_thread.setPriority(Thread.MAX_PRIORITY);
		if (TDebug.TraceSequencer) { TDebug.out("JavaSequencer.openImpl(): starting thread"); }
		m_thread.start();
		if (TDebug.TraceSequencer) { TDebug.out("JavaSequencer.openImpl(): end"); }
	}



	protected void closeImpl()
	{
		if (TDebug.TraceSequencer) { TDebug.out("JavaSequencer.closeImpl(): begin"); }
		stop();
		// terminate the thread
		synchronized (this)
		{
			m_nPhase = STATE_CLOSING; // ask end of thread
			this.notifyAll();
		}
		// now the thread should terminate
		m_thread = null;
		if (TDebug.TraceSequencer) { TDebug.out("JavaSequencer.closeImpl(): end"); }
	}



	protected void startImpl()
	{
		if (TDebug.TraceSequencer) { TDebug.out("JavaSequencer.startImpl(): begin"); }
		synchronized (this)
		{
			if (m_nPhase == STATE_STOPPED)
			{
				// unlock thread waiting for start
				m_nPhase = STATE_STARTING;  // ask for start
				this.notifyAll();
				// wait until m_lStartTime is set
				while (m_nPhase == STATE_STARTING)
				{
					try {
						this.wait();
					} catch (InterruptedException e) {
						if (TDebug.TraceAllExceptions) { TDebug.out(e); }
					}
				}
			}
		}
		if (TDebug.TraceSequencer) { TDebug.out("JavaSequencer.startImpl(): end"); }
	}



	protected void stopImpl()
	{
		if (TDebug.TraceSequencer) { TDebug.out("JavaSequencer.stopImpl(): begin"); }
		synchronized (this)
		{
			// condition true if called from own run() method
			if (Thread.currentThread() == m_thread)
			{
				if (m_nPhase != STATE_STOPPED)
				{
					m_nPhase = STATE_STOPPED;
					notifyAll();
				}
			}
			else
			{
				if (m_nPhase == STATE_STARTED)
				{
					m_nPhase = STATE_STOPPING; // ask for stop
					while (m_nPhase == STATE_STOPPING)
					{
						try {
							this.wait();
						} catch (InterruptedException e) {
							if (TDebug.TraceAllExceptions) { TDebug.out(e); }
						}
					}		
				}
			}
		}
		if (TDebug.TraceSequencer) { TDebug.out("JavaSequencer.stopImpl(): end"); }
	}



	public void run()
	{
		if (TDebug.TraceSequencer) { TDebug.out("JavaSequencer.run(): begin"); }
		while (true)
		{
			synchronized (this)
			{
				while (m_nPhase == STATE_STOPPED)
				{
					if (TDebug.TraceSequencer) { TDebug.out("JavaSequencer.run(): waiting to become running"); }
					try
					{
						this.wait();
					}
					catch (InterruptedException e)
					{
						if (TDebug.TraceAllExceptions) { TDebug.out(e); }
					}
				}
				if (m_nPhase == STATE_CLOSING) {
					if (TDebug.TraceSequencer) { TDebug.out("JavaSequencer.run(): end"); }
					return; 
				}
				if (TDebug.TraceSequencer) { TDebug.out("JavaSequencer.run(): now running"); }
				//NOTE: all time calculations are done in microseconds
				m_lStartTime = getTimeInMicroseconds() - m_lTickPosition * m_lMicroSecondsPerTick;
				m_nPhase = STATE_STARTED;
				this.notifyAll();
			}
			Sequence sequence = getSequence();
			if (sequence == null)
			{
				stop();
				continue;
			}
			Track[]	aTracks = sequence.getTracks();
			// this is used to get a useful time value for the end of track message
			//long	lHighestTime = 0;
			while (m_nPhase == STATE_STARTED)
			{
				// searching for the next event
				boolean		bTrackPresent = false;
				long		lBestTick = Long.MAX_VALUE;
				int			nBestTrack = -1;
				for (int nTrack = 0; nTrack < aTracks.length; nTrack++)
				{
					// TDebug.out("track " + nTrack);
					// Track	track = aTracks[nTrack];
					if (m_anTrackPositions[nTrack] < aTracks[nTrack].size()
						&& isTrackEnabled(nTrack))
					{
						bTrackPresent = true;
						MidiEvent	event = aTracks[nTrack].get(m_anTrackPositions[nTrack]);
						long		lTick = event.getTick();
						if (lTick < lBestTick)
						{
							lBestTick = lTick;
							nBestTrack = nTrack;
						}
					}
				}
				if (!bTrackPresent)
				{
					MetaMessage	metaMessage = new MetaMessage();
					try
					{
						metaMessage.setMessage(0x2F, new byte[0], 0);
					}
					catch (InvalidMidiDataException e)
					{
						if (TDebug.TraceAllExceptions) { TDebug.out(e); }
					}
					if (TDebug.TraceSequencer) { TDebug.out("JavaSequencer.run(): sending End of Track message with tick " + (m_lTickPosition + 1)); }
					// TODO: calulate us
					deliverEvent(metaMessage, m_lTickPosition + 1);
					stop();
					break;
				}
				MidiEvent	event = aTracks[nBestTrack].get(m_anTrackPositions[nBestTrack]);
				MidiMessage	message = event.getMessage();
				long		lTick = event.getTick();
				if (message instanceof MetaMessage && ((MetaMessage) message).getType() == 0x2F)
				{
					if (TDebug.TraceSequencer) { TDebug.out("JavaSequencer.run(): ignoring End of Track message with tick " + lTick); }
					m_anTrackPositions[nBestTrack]++;
					synchronized (this)
					{
						m_lTickPosition = lTick;
					}
				}
				else
				{
					if (deliverEvent(message, lTick))
					{
						m_anTrackPositions[nBestTrack]++;
						synchronized(this) {
							m_lTickPosition = lTick;
						}
					}
					else
					{
						// be sure that the current position is before the next event
						synchronized(this) {
							m_lTickPosition = Math.min(lTick, (getTimeInMicroseconds() - m_lStartTime) / m_lMicroSecondsPerTick);
						}
					}
				}
			} // while (m_nPhase == STATE_STARTED)

			stop();
		} // while (true)
	}



	/** Deliver a message at a certain time.
		@param lScheduledTick when to deliver the message in ticks
		@return true if the event was sent, false otherwise
	 */
	private boolean deliverEvent(MidiMessage message, long lScheduledTick)
	{
		if (TDebug.TraceSequencer) { TDebug.out("JavaSequencer.deliverEvent(): begin"); }
		long lScheduledTime;
		synchronized(this) {
			lScheduledTime = lScheduledTick * m_lMicroSecondsPerTick + m_lStartTime;
		}

		// wait for scheduled time
		while (getTimeInMicroseconds() < lScheduledTime)
		{
			if (m_nPhase != STATE_STARTED)
				return false;
			if (m_bTempoChanged) {
				synchronized(this) {
					lScheduledTime = lScheduledTick * m_lMicroSecondsPerTick + m_lStartTime;
					m_bTempoChanged = false;
				}
			}
			try
			{
				Thread.sleep(m_lSleepInterval);
			}
			catch (InterruptedException e)
			{
				if (TDebug.TraceAllExceptions) { TDebug.out(e); }
			}
		}

		// send midi message
		if (message instanceof MetaMessage)
		{
			MetaMessage	metaMessage = (MetaMessage) message;
			if (metaMessage.getType() == 0x51)	// set tempo
			{
				byte[]	abData = metaMessage.getData();
				int	nTempo = MidiUtils.getUnsignedInteger(abData[0]) * 65536 +
					MidiUtils.getUnsignedInteger(abData[1]) * 256 +
					MidiUtils.getUnsignedInteger(abData[2]);
				// TDebug.out("tempo (us/quarter note): " + nTempo);
				setTempoInMPQ(nTempo);
				// TODO: setTempoInMPQ() seems to be not thread-safe
			}
		}

		if (TDebug.TraceSequencer) { TDebug.out("JavaSequencer.deliverEvent(): sending message: " + message + " at: " + lScheduledTime); }
		// sendImpl(message, event.getTick());
		sendImpl(message, -1);
		// TODO: sendImpl() seems to be not thread-safe
		notifyListeners(message);
		// TODO: notifyListeners() seems to be not thread-safe
		if (TDebug.TraceSequencer) { TDebug.out("JavaSequencer.deliverEvent(): end"); }
		return true; // success
	}



	protected void setMasterSyncModeImpl(SyncMode syncMode)
	{
		// DO NOTHING
	}



	protected void setSlaveSyncModeImpl(SyncMode syncMode)
	{
		// DO NOTHING
	}



	public void setSequence(Sequence sequence)
		throws InvalidMidiDataException
	{
		boolean bWasRunning = isRunning();
		if (bWasRunning)
		{
			stop();
		}
		super.setSequence(sequence);
		m_lTickPosition = 0;
		m_anTrackPositions = new int[sequence.getTracks().length];
		for (int i = 0; i < m_anTrackPositions.length; i++)
		{
			m_anTrackPositions[i] = 0;	
		}
		if (bWasRunning)
		{
			start();
		}
	}



	public void setMicrosecondPosition(long lPosition)
	{
		setTickPosition(lPosition / m_lMicroSecondsPerTick);
	}



	public void setTickPosition(long lPosition)
	{
		if (getSequence() == null || m_anTrackPositions == null)
		{
			return;
		}
		boolean bWasRunning = isRunning();
		if (bWasRunning)
			stop();
		if (lPosition > getSequence().getTickLength())
		{
			m_lTickPosition = getSequence().getTickLength();
		}
		else
		{
			m_lTickPosition = lPosition;
		}
		for (int i = 0; i < m_anTrackPositions.length; i++)
		{
			m_anTrackPositions[i] = getTrackPosition(getSequence().getTracks()[i], lPosition);
		}
		if (bWasRunning)
			start();
	}



	public synchronized long getTickPosition()
	{
		if (m_nPhase == STATE_STARTED) {
			return Math.max(m_lTickPosition, (getTimeInMicroseconds() - m_lStartTime) / m_lMicroSecondsPerTick);
		} else {
			return m_lTickPosition;
		}
	}



	public void recordDisable(Track track)
	{
	}



	public void recordEnable(Track track)
	{
	}



	public void recordEnable(Track track, int nChannel)
	{
	}



	public boolean isRecording()
	{
		return false;
	}



	public void stopRecording()
	{
		checkOpen();
	}



	public void startRecording()
	{
		checkOpen();
	}



	protected synchronized void setTempoImpl(float fMPQ)
	{
		if (TDebug.TraceSequencer) { TDebug.out("JavaSequencer.setTempoImpl(): begin"); }
		int	nResolution = getResolution();
		long currentTime = getTimeInMicroseconds();
		long currentTickPosition = 0;
		if (m_lMicroSecondsPerTick!= 0) 
			currentTickPosition = (currentTime - m_lStartTime) / m_lMicroSecondsPerTick;
		m_lMicroSecondsPerTick = (long) fMPQ / nResolution;
		m_lStartTime = currentTime - currentTickPosition * m_lMicroSecondsPerTick;
		m_bTempoChanged = true;
		// TODO: update m_lMicroSecondsPerTick and m_lStartTime only after the next event because the the event now waiting for its schedule is not updated
		if (TDebug.TraceSequencer) { TDebug.out("JavaSequencer.setTempoImpl(): end"); }
	}




	/** Obtain the index of the event with the closest tick value.
	 */
	private int getTrackPosition(Track track, long tickPosition) {
		// check params
		if (track.size() == 0 || tickPosition <= track.get(0).getTick())
			return 0;
		if (tickPosition > track.get(track.size()-1).getTick())
			return track.size(); // index out of track

		// quick search
		int idx1 = 0;
		int idx2 = track.size() - 1;
		for (;;)
		{
			if ((idx2 - idx1) == 1)
				return idx1;
			int idx3 = (int)(((long)idx1 + (long)idx2) / 2L);
			if (tickPosition > track.get(idx3).getTick())
				idx1 = idx3;
			else
				idx2 = idx3;
		}
	}


	/**	Retrieve system time in microseconds.
		This method uses the clock as set with {@link #setClock}.

		@return the system time in microseconds
	*/
	protected long getTimeInMicroseconds()
	{
		// temporary hack
		if (getClock() == null)
		{
			return 0;
		}
		// end hack
		return getClock().getMicroseconds();
	}



	/** Set the clock this sequencer should use.
		@param clock the Clock to be used
		@throws IllegalStateException if the sequencer is not closed
	 */
	public void setClock(Clock clock)
	{
		if (isOpen())
		{
			throw new IllegalStateException("closed state required to set the clock");
		}
		m_clock = clock;
	}



	/** Obtain the clock used by this sequencer.
		@return the clock currently set for this sequencer
	 */
	public Clock getClock()
	{
		return m_clock;
	}


	/** Interface for sequencer clocks.
	 */
	public static interface Clock
	{
		public long getMicroseconds();
	}
}



/*** JavaSequencer.java ***/
