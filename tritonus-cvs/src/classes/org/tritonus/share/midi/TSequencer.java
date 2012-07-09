/*
 *	TSequencer.java
 *
 *	This file is part of Tritonus: http://www.tritonus.org/
 */

/*
 *  Copyright (c) 1999 - 2003 by Matthias Pfisterer
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

import java.io.InputStream;
import java.io.IOException;

import java.util.BitSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequencer;
import javax.sound.midi.Sequence;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MetaMessage;
import javax.sound.midi.MetaEventListener;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.ControllerEventListener;
import javax.sound.midi.MidiDevice;

import org.tritonus.share.TDebug;
import org.tritonus.share.ArraySet;




public abstract class TSequencer
extends TMidiDevice
implements Sequencer
{
	private static final float	MPQ_BPM_FACTOR = 6.0E7F;
	// This is for use in Collection.toArray(Object[]).
	private static final SyncMode[]	EMPTY_SYNCMODE_ARRAY = new SyncMode[0];


	private boolean		m_bRunning;


	/**	The Sequence to play or to record to.
	 */
	private Sequence	m_sequence;

	/**	The listeners that want to be notified of MetaMessages.
	 */
	private Set<MetaEventListener>	m_metaListeners;

	/**	The listeners that want to be notified of control change events.
	 *	They are organized as follows: this array is indexed with
	 *	the number of the controller change events listeners are
	 *	interested in. If there is any interest, the array element
	 *	contains a reference to a Set containing the listeners.
	 *	These sets are allocated on demand.
	 */
	private Set<ControllerEventListener>[]		m_aControllerListeners;

	private float		m_fNominalTempoInMPQ;
	private float		m_fTempoFactor;
	private Collection<SyncMode>	m_masterSyncModes;
	private Collection<SyncMode>	m_slaveSyncModes;
	private SyncMode	m_masterSyncMode;
	private SyncMode	m_slaveSyncMode;
	private BitSet		m_muteBitSet;
	private BitSet		m_soloBitSet;

	/**	Contains the enabled state of the tracks.
		This BitSet holds the pre-calculated effect of mute and
		solo status.
	*/
	private BitSet		m_enabledBitSet;

	/** Start of the loop in ticks.
	 */
	private long		m_lLoopStartPoint;

	/** End of the loop in ticks.
	 */
	private long		m_lLoopEndPoint;

	/** Loop count.
	 */
	private int			m_nLoopCount;


	/**
	 */
	@SuppressWarnings("unchecked")
	protected TSequencer(MidiDevice.Info info,
			     Collection<SyncMode> masterSyncModes,
			     Collection<SyncMode> slaveSyncModes)
	{
		super(info);
		m_bRunning = false;
		m_sequence = null;
		m_metaListeners = new ArraySet<MetaEventListener>();
		m_aControllerListeners = (Set<ControllerEventListener>[]) new Set[128];
		setTempoFactor(1.0F);
		setTempoInMPQ(500000);
		// TODO: make a copy
		m_masterSyncModes = masterSyncModes;
		m_slaveSyncModes = slaveSyncModes;
		if (getMasterSyncModes().length > 0)
		{
			m_masterSyncMode = getMasterSyncModes()[0];
		}
		if (getSlaveSyncModes().length > 0)
		{
			m_slaveSyncMode = getSlaveSyncModes()[0];
		}
		m_muteBitSet = new BitSet();
		m_soloBitSet = new BitSet();
		m_enabledBitSet = new BitSet();
		updateEnabled();
		setLoopStartPoint(0);
		setLoopEndPoint(-1);
		setLoopCount(0);
	}



	public void setSequence(Sequence sequence)
		throws InvalidMidiDataException
	{
		// TODO: what if playing is in progress?
		if (getSequence() != sequence)
		{
			m_sequence = sequence;
			setSequenceImpl();
			/* Yes, resetting the tempo factor is required by the specification.
			   TODO: can't find this any more in the spec.

			   It is unclear whether this should be executed in any case
			   (even if in fact the sequence didn't change).
			 */
			setTempoFactor(1.0F);
		}
	}


	/** Set Sequence.
		Subclasses that need to be informed when a Sequence is set
		should override this method. It is called by setSequence().
		Subclasses can find out the new Sequence by calling getSequence().
	*/
	// TODO: make abstract
	protected void setSequenceImpl()
	{
	}


	public void setSequence(InputStream inputStream)
		throws InvalidMidiDataException, IOException
	{
		Sequence	sequence = MidiSystem.getSequence(inputStream);
		setSequence(sequence);
	}



	public Sequence getSequence()
	{
		return m_sequence;
	}



	public void setLoopStartPoint(long lTick)
	{
		m_lLoopStartPoint = lTick;
	}


	public long getLoopStartPoint()
	{
		return m_lLoopStartPoint;
	}


	public void setLoopEndPoint(long lTick)
	{
		m_lLoopEndPoint = lTick;
	}


	public long getLoopEndPoint()
	{
		return m_lLoopEndPoint;
	}


	public void setLoopCount(int nLoopCount)
	{
		m_nLoopCount = nLoopCount;
	}


	public int getLoopCount()
	{
		return m_nLoopCount;
	}



	public synchronized void start()
	{
		checkOpen();
		if (! isRunning())
		{
			m_bRunning = true;
			// TODO: perhaps check if sequence present
			startImpl();
		}
	}


	/**
	 *	Subclasses have to override this method to be notified of
	 *	starting.
	 */
	protected void startImpl()
	{
	}



	public synchronized void stop()
	{
		checkOpen();
		if (isRunning())
		{
			stopImpl();
			m_bRunning = false;
		}
	}



	/**
	 *	Subclasses have to override this method to be notified of
	 *	stopping.
	 */
	protected void stopImpl()
	{
	}



	public synchronized boolean isRunning()
	{
		return m_bRunning;
	}



	/** Checks if the Sequencer is open.
		This method is intended to be called by
		{@link javax.sound.midi.Sequencer#start start},
		{@link javax.sound.midi.Sequencer#stop stop},
		{@link javax.sound.midi.Sequencer#startRecording startRecording}
		and {@link javax.sound.midi.Sequencer#stop stopRecording}.

		@throws IllegalStateException if the <code>Sequencer</code> is not open
	 */
	protected void checkOpen()
	{
		if (! isOpen())
		{
			throw new IllegalStateException("Sequencer is not open");
		}
	}



	/**	Returns the resolution (ticks per quarter) of the current sequence.
		If no sequence is set, a bogus default value != 0 is returned.
	*/
	protected int getResolution()
	{
		Sequence	sequence = getSequence();
		int		nResolution;
		if (sequence != null)
		{
			nResolution = sequence.getResolution();
		}
		else
		{
			nResolution = 1;
		}
		return nResolution;
	}



	protected void setRealTempo()
	{
		float	fTempoFactor = getTempoFactor();
		if (fTempoFactor == 0.0F)
		{
			fTempoFactor = 0.01F;
		}
		float	fRealTempo = getTempoInMPQ() / fTempoFactor;
		if (TDebug.TraceSequencer) { TDebug.out("TSequencer.setRealTempo(): real tempo: " + fRealTempo); }
		setTempoImpl(fRealTempo);
	}



	public float getTempoInBPM()
	{
		float	fBPM = MPQ_BPM_FACTOR / getTempoInMPQ();
		return fBPM;
	}



	public void setTempoInBPM(float fBPM)
	{
		float	fMPQ = MPQ_BPM_FACTOR / fBPM;
		setTempoInMPQ(fMPQ);
	}



	public float getTempoInMPQ()
	{
		return m_fNominalTempoInMPQ;
	}


	/** Sets the tempo.
		Implementation classes are required to call this method for changing
		the tempo in reaction to a tempo change event.
	 */
	public void setTempoInMPQ(float fMPQ)
	{
 		m_fNominalTempoInMPQ = fMPQ;
 		setRealTempo();
	}



	public void setTempoFactor(float fFactor)
	{
		m_fTempoFactor = fFactor;
		setRealTempo();
	}



	public float getTempoFactor()
	{
		return m_fTempoFactor;
	}



	/**	Change the tempo of the native sequencer part.
	 *	This method has to be defined by subclasses according
	 *	to the native facilities they use for sequenceing.
	 *	The implementation should not take into account the
	 *	tempo factor. This is handled elsewhere.
	 */
	protected abstract void setTempoImpl(float fMPQ);



	// NOTE: has to be redefined if recording is done natively
	public long getTickLength()
	{
		long	lLength = 0;
		if (getSequence() != null)
		{
			lLength = getSequence().getTickLength();
		}
		return lLength;
	}



	// NOTE: has to be redefined if recording is done natively
	public long getMicrosecondLength()
	{
		long	lLength = 0;
		if (getSequence() != null)
		{
			lLength = getSequence().getMicrosecondLength();
		}
		return lLength;
	}




	public boolean addMetaEventListener(MetaEventListener listener)
	{
		synchronized (m_metaListeners)
		{
			return m_metaListeners.add(listener);
		}
	}



	public void removeMetaEventListener(MetaEventListener listener)
	{
		synchronized (m_metaListeners)
		{
			m_metaListeners.remove(listener);
		}
	}


	protected Iterator<MetaEventListener> getMetaEventListeners()
	{
		synchronized (m_metaListeners)
		{
			return m_metaListeners.iterator();
		}
	}



	protected void sendMetaMessage(MetaMessage message)
	{
		Iterator<MetaEventListener>	iterator = getMetaEventListeners();
		while (iterator.hasNext())
		{
			MetaEventListener	metaEventListener = iterator.next();
			MetaMessage	copiedMessage = (MetaMessage) message.clone();
			metaEventListener.meta(copiedMessage);
		}
	}



	public int[] addControllerEventListener(ControllerEventListener listener, int[] anControllers)
	{
		synchronized (m_aControllerListeners)
		{
			if (anControllers == null)
			{
				/*
				 *	Add to all controllers. NOTE: this
				 *	is an implementation-specific
				 *	semantic!
				 */
				for (int i = 0; i < 128; i++)
				{
					addControllerListener(i, listener);
				}
			}
			else
			{
				for (int i = 0; i < anControllers.length; i++)
				{
					addControllerListener(anControllers[i], listener);
				}
			}
		}
		return getListenedControllers(listener);
	}



	private void addControllerListener(int i,
									   ControllerEventListener listener)
	{
		if (m_aControllerListeners[i] == null)
		{
			m_aControllerListeners[i] = new ArraySet<ControllerEventListener>();
		}
		m_aControllerListeners[i].add(listener);
	}



	public int[] removeControllerEventListener(ControllerEventListener listener, int[] anControllers)
	{
		synchronized (m_aControllerListeners)
		{
			if (anControllers == null)
			{
				/*
				 *	Remove from all controllers. Unlike
				 *	above, this is specified semantics.
				 */
				for (int i = 0; i < 128; i++)
				{
					removeControllerListener(i, listener);
				}
			}
			else
			{
				for (int i = 0; i < anControllers.length; i++)
				{
					removeControllerListener(anControllers[i], listener);
				}
			}
		}
		return getListenedControllers(listener);
	}



	private void removeControllerListener(int i,
					      ControllerEventListener listener)
	{
		if (m_aControllerListeners[i] != null)
		{
			m_aControllerListeners[i].add(listener);
		}
	}



	private int[] getListenedControllers(ControllerEventListener listener)
	{
		int[]	anControllers = new int[128];
		int	nIndex = 0;	// points to the next position to use.
		for (int nController = 0; nController < 128; nController++)
		{
			if (m_aControllerListeners[nController] != null &&
			    m_aControllerListeners[nController].contains(listener))
			{
				anControllers[nIndex] = nController;
				nIndex++;
			}
		}
		int[]	anResultControllers = new int[nIndex];
		System.arraycopy(anControllers, 0, anResultControllers, 0, nIndex);
		return anResultControllers;
	}



	protected void sendControllerEvent(ShortMessage message)
	{
		int	nController = message.getData1();
		if (m_aControllerListeners[nController] != null)
		{
			Iterator<ControllerEventListener>	iterator = m_aControllerListeners[nController].iterator();
			while (iterator.hasNext())
			{
				ControllerEventListener	controllerEventListener = iterator.next();
				ShortMessage	copiedMessage = (ShortMessage) message.clone();
				controllerEventListener.controlChange(copiedMessage);
			}
		}
	}



	protected void notifyListeners(MidiMessage message)
	{
		if (message instanceof MetaMessage)
		{
			// IDEA: use extra thread for event delivery
			sendMetaMessage((MetaMessage) message);
		}
		else if (message instanceof ShortMessage && ((ShortMessage) message).getCommand() == ShortMessage.CONTROL_CHANGE)
		{
			sendControllerEvent((ShortMessage) message);
		}
	}



	public SyncMode getMasterSyncMode()
	{
		return m_masterSyncMode;
	}



	public void setMasterSyncMode(SyncMode syncMode)
	{
		if (m_masterSyncModes.contains(syncMode))
		{
			if (! getMasterSyncMode().equals(syncMode))
			{
				m_masterSyncMode = syncMode;
				setMasterSyncModeImpl(syncMode);
			}
		}
		else
		{
			throw new IllegalArgumentException("sync mode not allowed: " + syncMode);
		}
	}


	/*
	  This method is guaranteed only to be called if the sync mode really changes.
	 */
	protected void setMasterSyncModeImpl(SyncMode syncMode)
	{
		// DO NOTHING
	}



	public SyncMode[] getMasterSyncModes()
	{
		SyncMode[]	syncModes = m_masterSyncModes.toArray(EMPTY_SYNCMODE_ARRAY);
		return syncModes;
	}



	public SyncMode getSlaveSyncMode()
	{
		return m_slaveSyncMode;
	}



	public void setSlaveSyncMode(SyncMode syncMode)
	{
		if (m_slaveSyncModes.contains(syncMode))
		{
			if (! getSlaveSyncMode().equals(syncMode))
			{
				m_slaveSyncMode = syncMode;
				setSlaveSyncModeImpl(syncMode);
			}
		}
		else
		{
			throw new IllegalArgumentException("sync mode not allowed: " + syncMode);
		}
	}



	/*
	  This method is guaranteed only to be called if the sync mode really changes.
	 */
	protected void setSlaveSyncModeImpl(SyncMode syncMode)
	{
		// DO NOTHING
	}



	public SyncMode[] getSlaveSyncModes()
	{
		SyncMode[]	syncModes = m_slaveSyncModes.toArray(EMPTY_SYNCMODE_ARRAY);
		return syncModes;
	}



	public boolean getTrackSolo(int nTrack)
	{
		boolean	bSoloed = false;
		if (getSequence() != null)
		{
			if (nTrack < getSequence().getTracks().length)
			{
				bSoloed = m_soloBitSet.get(nTrack);
			}
		}
		return bSoloed;
	}



	public void setTrackSolo(int nTrack, boolean bSolo)
	{
		if (getSequence() != null)
		{
			if (nTrack < getSequence().getTracks().length)
			{
				boolean	bOldState = m_soloBitSet.get(nTrack);
				if (bSolo != bOldState)
				{
					if (bSolo)
					{
						m_soloBitSet.set(nTrack);
					}
					else
					{
						m_soloBitSet.clear(nTrack);
					}
					updateEnabled();
					setTrackSoloImpl(nTrack, bSolo);
				}
			}
		}
	}



	protected void setTrackSoloImpl(int nTrack, boolean bSolo)
	{
	}



	public boolean getTrackMute(int nTrack)
	{
		boolean	bMuted = false;
		if (getSequence() != null)
		{
			if (nTrack < getSequence().getTracks().length)
			{
				bMuted = m_muteBitSet.get(nTrack);
			}
		}
		return bMuted;
	}



	public void setTrackMute(int nTrack, boolean bMute)
	{
		if (getSequence() != null)
		{
			if (nTrack < getSequence().getTracks().length)
			{
				boolean	bOldState = m_muteBitSet.get(nTrack);
				if (bMute != bOldState)
				{
					if (bMute)
					{
						m_muteBitSet.set(nTrack);
					}
					else
					{
						m_muteBitSet.clear(nTrack);
					}
					updateEnabled();
					setTrackMuteImpl(nTrack, bMute);
				}
			}
		}
	}


	protected void setTrackMuteImpl(int nTrack, boolean bMute)
	{
	}



	private void updateEnabled()
	{
		BitSet	oldEnabledBitSet = (BitSet) m_enabledBitSet.clone();
		boolean	bSoloExists = m_soloBitSet.length() > 0;
		if (bSoloExists)
		{
			m_enabledBitSet = (BitSet) m_soloBitSet.clone();
		}
		else
		{
			for (int i = 0; i < m_muteBitSet.size(); i++)
			{
				if (m_muteBitSet.get(i))
				{
					m_enabledBitSet.clear(i);
				}
				else
				{
					m_enabledBitSet.set(i);
				}
			}
		}
		oldEnabledBitSet.xor(m_enabledBitSet);
		/*	oldEnabledBitSet now has a bit set if the status for
			this bit changed.
		*/
		for (int i = 0; i < oldEnabledBitSet.size(); i++)
		{
			if (oldEnabledBitSet.get(i))
			{
				setTrackEnabledImpl(i, m_enabledBitSet.get(i));
			}
		}
	}



	/**	Shows that a track state has changed.
		This method is called for each track where the enabled
		state (calculated from mute and solo) has changed.
		The boolean value passed represents the new state.

		@param nTrack The track number for which the enabled status
		has changed.

		@param bEnabled The new enabled state for this track.
	 */
	protected void setTrackEnabledImpl(int nTrack, boolean bEnabled)
	{
	}



	protected boolean isTrackEnabled(int nTrack)
	{
		return m_enabledBitSet.get(nTrack);
	}



	/** Sets the preloading intervall.
		This is the time span between preloading events to an internal
		queue and playing them. This intervall should be kept constant
		by the implementation. However, this cannot be guaranteed.
	*/
	public void setLatency(int nMilliseconds)
	{
	}



	/** Get the preloading intervall.

	@return the preloading intervall in milliseconds, or -1 if the sequencer
	doesn't repond to changes in the <code>Sequence</code> at all.
	*/
	public int getLatency()
	{
		return -1;
	}
}



/*** TSequencer.java ***/
