/*
 *	RTSystem.java
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

import java.lang.reflect.Constructor;

import java.util.LinkedList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import  org.tritonus.share.TDebug;



public class RTSystem
extends Thread
{
	private static final boolean	DEBUG = false;

	private SystemOutput		m_output;
	private Map			m_instrumentMap;
	private boolean			m_bRunning;
	private int			m_nTime;
	private float			m_fTimeStep;
	private int			m_nARate;
	private int			m_nKRate;
	private int			m_nAToKRateFactor;
	private List			m_activeInstruments;
	private List			m_scheduledInstruments;
	private int			m_nScheduledEndTime;
	private float			m_fFloatToIntTimeFactor;
	private float			m_fIntToFloatTimeFactor;



	public RTSystem(SystemOutput output, Map instrumentMap)
	{
		m_output = output;
		m_instrumentMap = instrumentMap;
		// TODO:
		setRates(44100, 100);
		m_activeInstruments = new LinkedList();
		m_scheduledInstruments = new LinkedList();
		m_nScheduledEndTime = Integer.MAX_VALUE;
	}



	private void setRates(int nARate, int nKRate)
	{
		m_nARate = nARate;
		m_nKRate = nKRate;
		m_nAToKRateFactor = nARate / nKRate;
		m_fTimeStep = 1.0F / (float) nKRate;
		// following is only correct for 60 BPM
		m_fFloatToIntTimeFactor = (float) nKRate;
		m_fIntToFloatTimeFactor = m_fTimeStep;
	}



	public void run()
	{
		try
		{
			runImpl();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}



	private void runImpl()
		throws IOException
	{
		m_bRunning = true;
		m_nTime = 0;
		while (m_bRunning)
		{
			doI();
			doK();
			for (int i = 0; i < m_nAToKRateFactor; i++)
			{
				doA();
			}
			advanceTime();
		}
		m_output.close();
	}



	private void doI()
	{
		if (DEBUG)
		{
			TDebug.out("doI()");
			TDebug.out("time: " + getTime());
		}
		synchronized (m_scheduledInstruments)
		{
			Iterator	scheduledInstruments = m_scheduledInstruments.iterator();
			while (scheduledInstruments.hasNext())
			{
				if (DEBUG) { TDebug.out("scheduled instrument"); }
				AbstractInstrument	instrument = (AbstractInstrument) scheduledInstruments.next();
				if (DEBUG) { TDebug.out("instrument start time: " + instrument.getStartTime()); }
				if (getTime() >= instrument.getStartTime())
				{
					if (DEBUG) { TDebug.out("...activating"); }
					scheduledInstruments.remove();
					instrument.doIPass(this);
					m_activeInstruments.add(instrument);
				}
			}
		}
		Iterator	activeInstruments = m_activeInstruments.iterator();
		while (activeInstruments.hasNext())
		{
			AbstractInstrument	instrument = (AbstractInstrument) activeInstruments.next();
			if (getTime() > instrument.getEndTime())
			{
				if (DEBUG) { TDebug.out("...DEactivating"); }
				activeInstruments.remove();
			}
		}
		if (getTime() >= getScheduledEndTime())
		{
			stopEngine();
		}
	}



	private void doK()
	{
		Iterator	activeInstruments = m_activeInstruments.iterator();
		while (activeInstruments.hasNext())
		{
			AbstractInstrument	instrument = (AbstractInstrument) activeInstruments.next();
			instrument.doKPass(this);
		}
	}



	private void doA()
		throws IOException
	{
		// TDebug.out("doA()");
		m_output.clear();
		Iterator	activeInstruments = m_activeInstruments.iterator();
		while (activeInstruments.hasNext())
		{
			// TDebug.out("doA(): has active Instrument");
			AbstractInstrument	instrument = (AbstractInstrument) activeInstruments.next();
			instrument.doAPass(this);
		}
		m_output.emit();
	}



	public void scheduleInstrument(String strInstrumentName, float fStartTime, float fDuration)
	{
		AbstractInstrument	instrument = createInstrumentInstance(strInstrumentName);
		int			nStartTime = Math.round(fStartTime * m_fFloatToIntTimeFactor);
		int			nEndTime = Math.round((fStartTime + fDuration) * m_fFloatToIntTimeFactor);
		instrument.setStartAndEndTime(nStartTime, nEndTime);
		synchronized (m_scheduledInstruments)
		{
			m_scheduledInstruments.add(instrument);
			if (DEBUG)
			{
				TDebug.out("adding instrument");
				TDebug.out("start: " + nStartTime);
				TDebug.out("end: " + nEndTime);
			}
		}
	}



	public void scheduleEnd(float fEndTime)
	{
		m_nScheduledEndTime = Math.round(fEndTime * m_fFloatToIntTimeFactor);
		// TODO:
	}



	public void stopEngine()
	{
		m_bRunning = false;
	}



	private void advanceTime()
	{
		m_nTime++;
	}


	public int getTime()
	{
		return m_nTime;
	}

	public void output(float fValue)
	{
		m_output.output(fValue);
	}



	private int getScheduledEndTime()
	{
		return m_nScheduledEndTime;
	}



	private AbstractInstrument createInstrumentInstance(String strInstrumentName)
	{
		AbstractInstrument	instrument = null;
		Class	instrumentClass = (Class) m_instrumentMap.get(strInstrumentName);
		try
		{
			Constructor	constructor = instrumentClass.getConstructor(new Class[]{RTSystem.class});
			instrument = (AbstractInstrument) constructor.newInstance(new Object[]{this});
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return instrument;
	}
}



/*** RTSystem.java ***/
