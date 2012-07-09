/*
 *	TMidiDevice.java
 *
 *	This file is part of Tritonus: http://www.tritonus.org/
 */

/*
 *  Copyright (c) 1999 - 2006 by Matthias Pfisterer
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.Transmitter;

import org.tritonus.share.TDebug;


/**	Base class for MidiDevice implementations.
 *	The goal of this class is to supply the common functionality for
 *	classes that implement the interface MidiDevice.
 */
public abstract class TMidiDevice
implements MidiDevice
{
	/**	The Info object for a certain instance of MidiDevice.
	 */
	private MidiDevice.Info		m_info;

	/**	A flag to store whether the device is "open".
	 */
	private boolean			m_bDeviceOpen;

	/**	Whether to handle input from the physical port
		and to allow Transmitters.
	 */
	private boolean		m_bUseTransmitter;

	/**	Whether to handle output to the physical port
		and to allow Receivers.
	 */
	private boolean		m_bUseReceiver;

	/**	The list of Receiver objects that belong to this
	 * 	MidiDevice.
	 *
	 *	@see #addReceiver
	 *	@see #removeReceiver
	 */
	private List<Receiver>	m_receivers;

	/**	The list of Transmitter objects that belong to this
	 * 	MidiDevice.
	 *
	 *	@see #addTransmitter
	 *	@see #removeTransmitter
	 */
	private List<Transmitter>	m_transmitters;



	/**	Initialize this class.
	 *	This sets the info from the passed one, sets the open status
	 *	to false, the number of Receivers to zero and the collection
	 *	of Transmitters to be empty.
	 *
	 *	@param info	The info object that describes this instance.
	 */
	public TMidiDevice(MidiDevice.Info info)
	{
		this(info, true, true);
	}



	/**	Initialize this class.
	 *	This sets the info from the passed one, sets the open status
	 *	to false, the number of Receivers to zero and the collection
	 *	of Transmitters to be empty.
	 *
	 *	@param info	The info object that describes this instance.
	 */
	public TMidiDevice(MidiDevice.Info info,
			   boolean bUseTransmitter,
			   boolean bUseReceiver)
	{
		m_info = info;
		m_bUseTransmitter = bUseTransmitter;
		m_bUseReceiver = bUseReceiver;
		m_bDeviceOpen = false;
		m_receivers = new ArrayList<Receiver>();
		m_transmitters = new ArrayList<Transmitter>();
	}



	/**	Retrieves a description of this instance.
	 *	This returns the info object passed to the constructor.
	 *
	 *	@return the description
	 *
	 *	@see #TMidiDevice
	 */
	public MidiDevice.Info getDeviceInfo()
	{
		return m_info;
	}



	public synchronized void open()
		throws MidiUnavailableException
	{
		if (TDebug.TraceMidiDevice) { TDebug.out("TMidiDevice.open(): begin"); }
		if (! isOpen())
		{
			openImpl();
			/* If openImpl() throws a MidiUnavailableException, m_bDeviceOpen
			 * remains false.
			 */
			m_bDeviceOpen = true;
		}
		if (TDebug.TraceMidiDevice) { TDebug.out("TMidiDevice.open(): end"); }
	}



	/**
	 *	Subclasses have to override this method to be notified of
	 *	opening.
	 */
	protected void openImpl()
		throws MidiUnavailableException
	{
		if (TDebug.TraceMidiDevice) TDebug.out("TMidiDevice.openImpl(): begin");
		if (TDebug.TraceMidiDevice) TDebug.out("TMidiDevice.openImpl(): end");
	}



	public synchronized void close()
	{
		if (TDebug.TraceMidiDevice) { TDebug.out("TMidiDevice.close(): begin"); }
		if (isOpen())
		{
			closeImpl();
			// TODO: close all Receivers and Transmitters
			m_bDeviceOpen = false;
		}
		if (TDebug.TraceMidiDevice) { TDebug.out("TMidiDevice.close(): end"); }
	}



	/**
	 *	Subclasses have to override this method to be notified of
	 *	closeing.
	 */
	protected void closeImpl()
	{
		if (TDebug.TraceMidiDevice) TDebug.out("TMidiDevice.closeImpl(): begin");
		if (TDebug.TraceMidiDevice) TDebug.out("TMidiDevice.closeImpl(): end");
	}



	public boolean isOpen()
	{
		return m_bDeviceOpen;
	}



	/**	Returns whether to handle input.
		If this is true, retrieving Transmitters is possible
		and input from the physical port is passed to them.

		@see #getUseOut
	 */
	protected boolean getUseTransmitter()
	{
		return m_bUseTransmitter;
	}



	/**	Returns whether to handle output.
		If this is true, retrieving Receivers is possible
		and output to them is passed to the physical port.

		@see #getUseTransmitter
	 */
	protected boolean getUseReceiver()
	{
		return m_bUseReceiver;
	}



	/**	Returns the device time in microseconds.
		This is a default implementation, telling the application
		program that the device doesn't track time. If a device wants
		to give timing information, it has to override this method.
	*/
	public long getMicrosecondPosition()
	{
		return -1;
	}



	public int getMaxReceivers()
	{
		int	nMaxReceivers = 0;
		if (getUseReceiver())
		{
		/*
		 *	The value -1 means unlimited.
		 */
			nMaxReceivers = -1;
		}
		return nMaxReceivers;
	}



	public int getMaxTransmitters()
	{
		int	nMaxTransmitters = 0;
		if (getUseTransmitter())
		{
		/*
		 *	The value -1 means unlimited.
		 */
			nMaxTransmitters = -1;
		}
		return nMaxTransmitters;
	}



	/**	Creates a new Receiver object associated with this instance.
	 *	In this implementation, an unlimited number of Receivers
	 *	per MidiDevice can be created.
	 */
	public Receiver getReceiver()
		throws MidiUnavailableException
	{
		if (! getUseReceiver())
		{
			throw new MidiUnavailableException("Receivers are not supported by this device");
		}
		return new TReceiver();
	}



	/**	Creates a new Transmitter object associated with this instance.
	 *	In this implementation, an unlimited number of Transmitters
	 *	per MidiDevice can be created.
	 */
	public Transmitter getTransmitter()
		throws MidiUnavailableException
	{
		if (! getUseTransmitter())
		{
			throw new MidiUnavailableException("Transmitters are not supported by this device");
		}
		return new TTransmitter();
	}



	public List<Receiver> getReceivers()
	{
		return Collections.unmodifiableList(m_receivers);
	}


	public List<Transmitter> getTransmitters()
	{
		return Collections.unmodifiableList(m_transmitters);
	}


	/*
	 *	Intended for overriding by subclasses to receive messages.
	 *	This method is called by TMidiDevice.Receiver object on
	 *	receipt of a MidiMessage.
	 */
	protected void receive(MidiMessage message, long lTimeStamp)
	{
		if (TDebug.TraceMidiDevice) { TDebug.out("### [should be overridden] TMidiDevice.receive(): message " + message); }
	}



	protected void addReceiver(Receiver receiver)
	{
		synchronized (m_receivers)
		{
			m_receivers.add(receiver);
		}
	}



	protected void removeReceiver(Receiver receiver)
	{
		synchronized (m_receivers)
		{
			m_receivers.remove(receiver);
		}
	}




	protected void addTransmitter(Transmitter transmitter)
	{
		synchronized (m_transmitters)
		{
			m_transmitters.add(transmitter);
		}
	}


	protected void removeTransmitter(Transmitter transmitter)
	{
		synchronized (m_transmitters)
		{
			m_transmitters.remove(transmitter);
		}
	}



	/**	Send a MidiMessage to all Transmitters.
	 *	This method should be called by subclasses when they get a
	 *	message from a physical MIDI port.
	 */
	protected void sendImpl(MidiMessage message, long lTimeStamp)
	{
		if (TDebug.TraceMidiDevice) { TDebug.out("TMidiDevice.sendImpl(): begin"); }
		Iterator	transmitters = m_transmitters.iterator();
		while (transmitters.hasNext())
		{
			TTransmitter	transmitter = (TTransmitter) transmitters.next();
			/* due to a bug in the Sun jdk1.3, we cannot use
			   clone() for MetaMessages. So we have to do the
			   equivalent ourselves.
			*/
			// MidiMessage	copiedMessage = (MidiMessage) message.clone();
			MidiMessage	copiedMessage = null;
			if (message instanceof MetaMessage)
			{
				MetaMessage	origMessage = (MetaMessage) message;
				MetaMessage	metaMessage = new MetaMessage();
				try
				{
					metaMessage.setMessage(origMessage.getType(), origMessage.getData(), origMessage.getData().length);
				}
				catch (InvalidMidiDataException e)
				{
					if (TDebug.TraceAllExceptions) { TDebug.out(e); }
				}
				copiedMessage = metaMessage;
			}
			else
			{
				copiedMessage = (MidiMessage) message.clone();
			}

			if (message instanceof MetaMessage)
			{
				if (TDebug.TraceMidiDevice) { TDebug.out("TMidiDevice.sendImpl(): MetaMessage.getData().length (original): " + ((MetaMessage) message).getData().length); }
				if (TDebug.TraceMidiDevice) { TDebug.out("TMidiDevice.sendImpl(): MetaMessage.getData().length (cloned): " + ((MetaMessage) copiedMessage).getData().length); }
			}
			transmitter.send(copiedMessage, lTimeStamp);
		}
		if (TDebug.TraceMidiDevice) { TDebug.out("TMidiDevice.sendImpl(): end"); }
	}




/////////////////// INNER CLASSES //////////////////////////////////////


	/**	Receiver proxy class.
	 *	This class' objects are handed out on calls to
	 *	TMidiDevice.getReceiver(). 
	 */
	public class TReceiver
	implements Receiver
	{
		private boolean		m_bOpen;



		public TReceiver()
		{
			TMidiDevice.this.addReceiver(this);
			m_bOpen = true;
		}



		protected boolean isOpen()
		{
			return m_bOpen;
		}



		/**	Receive a MidiMessage.
		 *
		 */
		public void send(MidiMessage message, long lTimeStamp)
		{
			if (TDebug.TraceMidiDevice) { TDebug.out("TMidiDevice.TReceiver.send(): message " + message); }
			if (m_bOpen)
			{
				TMidiDevice.this.receive(message, lTimeStamp);
			}
			else
			{
				throw new IllegalStateException("receiver is not open");
			}
		}



		/**	Closes the receiver.
		 *	After a receiver has been closed, it does no longer
		 *	propagate MidiMessages to its associated MidiDevice.
		 */
		public void close()
		{
			TMidiDevice.this.removeReceiver(this);
			m_bOpen = false;
		}
	}




	public class TTransmitter
	implements Transmitter
	{
		private boolean		m_bOpen;
		private Receiver	m_receiver;



		public TTransmitter()
		{
			m_bOpen = true;
			TMidiDevice.this.addTransmitter(this);
		}



		public void setReceiver(Receiver receiver)
		{
			synchronized (this)
			{
				m_receiver = receiver;
			}
		}



		public Receiver getReceiver()
		{
			return m_receiver;
		}



		public void send(MidiMessage message, long lTimeStamp)
		{
			if (getReceiver() != null && m_bOpen)
			{
				getReceiver().send(message, lTimeStamp);
			}
		}



		/**	Closes the transmitter.
		 *	After a transmitter has been closed, it no longer
		 *	passes MidiMessages to a Receiver previously set for
		 *	it.
		 */
		public void close()
		{
			TMidiDevice.this.removeTransmitter(this);
			m_bOpen = false;
			/* Previously, this method just set m_receiver to null
			   instead of maintaining an open flag. This allows to exploit
			   the behaviour of calling close(), the setReceiver() again,
			   and the Transmitter is "reopened". TODO: write a test case
			   for this scenario.
			*/
		}
	}



	/*
	 *	This is needed only because MidiDevice.Info's
	 *	constructor is protected (in the Sun jdk1.3).
	 */
	public static class Info
	extends MidiDevice.Info
	{
		public Info(String a, String b, String c, String d)
		{
			super(a, b, c, d);
		}
	}

}



/*** TMidiDevice.java ***/

