/*
 *	MidiFileFormat.java
 *
 *	This file is part of Tritonus: http://www.tritonus.org/
 */

/*
 *  Copyright (c) 1999 - 2004 by Matthias Pfisterer
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

package javax.sound.midi;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;



public class MidiFileFormat
{
	public static final int		UNKNOWN_LENGTH = -1;

	protected int		type;
	protected float		divisionType;
	protected int		resolution;
	protected int		byteLength;
	protected long		microsecondLength;

	private Map<String, Object>	m_properties;
	private Map<String, Object>	m_unmodifiableProperties;



	public MidiFileFormat(int nType,
						  float fDivisionType,
						  int nResolution,
						  int nByteLength,
						  long lMicrosecondLength)
	{
		this(nType,
			 fDivisionType,
			 nResolution,
			 nByteLength,
			 lMicrosecondLength,
			 null);
	}


	public MidiFileFormat(int nType,
						  float fDivisionType,
						  int nResolution,
						  int nByteLength,
						  long lMicrosecondLength,
						  Map<String, Object> properties)
	{
		type = nType;
		divisionType = fDivisionType;
		resolution = nResolution;
		byteLength = nByteLength;
		microsecondLength = lMicrosecondLength;
		/* Here, we make a shallow copy of the map. It's unclear if this
		   is sufficient (or if a deep copy should be made).
		*/
		m_properties = new HashMap<String, Object>();
		if (properties != null)
		{
			m_properties.putAll(properties);
		}
		m_unmodifiableProperties = Collections.unmodifiableMap(m_properties);
	}


	public int getType()
	{
		return type;
	}



	public float getDivisionType()
	{
		return divisionType;
	}



	public int getResolution()
	{
		return resolution;
	}



	public int getByteLength()
	{
		return byteLength;
	}



	public long getMicrosecondLength()
	{
		return microsecondLength;
	}


	public Map<String, Object> properties()
	{
		return m_unmodifiableProperties;
	}



	public Object getProperty(String key)
	{
		return m_properties.get(key);
	}



	protected void setProperty(String key, Object value)
	{
		m_properties.put(key, value);
	}
}



/*** MidiFileFormat.java ***/
