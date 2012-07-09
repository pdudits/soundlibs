/*
 *	TAudioFormat.java
 *
 *	This file is part of Tritonus: http://www.tritonus.org/
 */

/*
 *  Copyright (c) 2003 by Matthias Pfisterer
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

package org.tritonus.share.sampled;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.sound.sampled.AudioFormat;



public class TAudioFormat
extends AudioFormat
{
	private Map<String, Object>	m_properties;
	private Map<String, Object>	m_unmodifiableProperties;


	public TAudioFormat(AudioFormat.Encoding encoding,
						float sampleRate,
						int sampleSizeInBits,
						int channels,
						int frameSize,
						float frameRate,
						boolean bigEndian,
						Map<String, Object> properties)
	{
		super(encoding,
			  sampleRate,
			  sampleSizeInBits,
			  channels,
			  frameSize,
			  frameRate,
			  bigEndian);
		initMaps(properties);
	}

	/**
	 * Create an instance of TAudioFormat as a copy of the supplied audio
	 * format.
	 * 
	 * @param format the instance to copy
	 */
	public TAudioFormat(AudioFormat format)
	{
		this(format.getEncoding(),
				format.getSampleRate(),
				format.getSampleSizeInBits(),
				format.getChannels(),
				format.getFrameSize(),
				format.getFrameRate(),
				format.isBigEndian(), 
				format.properties());
	}

	/**
	 * Create an instance of TAudioFormat as a copy of the supplied audio
	 * format, adding the given properties to any properties supplied by
	 * <code>format</code>. Duplicate properties in the supplied
	 * <code>properties</code> will overwrite the ones in <code>format</code>.
	 * 
	 * @param format the instance to copy
	 * @param properties properties to be added to this TAudioFormat
	 */
	public TAudioFormat(AudioFormat format,
						Map<String, Object> properties) 
	{
		this(format);
		m_properties.putAll(properties);
	}
	
	public TAudioFormat(float sampleRate,
						int sampleSizeInBits,
						int channels,
						boolean signed,
						boolean bigEndian,
						Map<String, Object> properties)
	{
		super(sampleRate,
			  sampleSizeInBits,
			  channels,
			  signed,
			  bigEndian);
		initMaps(properties);
	}



	private void initMaps(Map<String, Object> properties)
	{
		/* Here, we make a shallow copy of the map. It's unclear if this
		   is sufficient (or if a deep copy should be made).
		*/
		m_properties = new HashMap<String, Object>();
		if (properties != null) {
			m_properties.putAll(properties);
		}
		m_unmodifiableProperties = Collections.unmodifiableMap(m_properties);
	}



	@Override
	public Map<String, Object> properties()
	{
		if (m_properties == null)
		{
			initMaps(null);
		}
		return m_unmodifiableProperties;
	}


	@Override
    public Object getProperty(String key) 
    {
    	if (m_properties == null) 
    	{
    		return null;
    	}
    	return m_properties.get(key);
	}

    
    protected void setProperty(String key, Object value)
	{
		if (m_properties == null) {
			initMaps(null);
		}
		m_properties.put(key, value);
	}
}



/*** TAudioFormat.java ***/
