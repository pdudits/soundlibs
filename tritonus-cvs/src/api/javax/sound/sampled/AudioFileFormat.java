/*
 *	AudioFileFormat.java
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

package javax.sound.sampled;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;



public class AudioFileFormat
{


	private Type			m_type;
	private AudioFormat		m_audioFormat;
	private int				m_nLengthInFrames;
	private int				m_nLengthInBytes;

	private Map<String, Object>	m_properties;
	private Map<String, Object>	m_unmodifiableProperties;


	public AudioFileFormat(Type type,
						   AudioFormat audioFormat,
						   int nLengthInFrames)
	{
		this(type,
		     audioFormat,
		     nLengthInFrames,
			 null);
	}



	public AudioFileFormat(Type type,
						   AudioFormat audioFormat,
						   int nLengthInFrames,
						   Map<String, Object> properties)
	{
		this(type,
		     AudioSystem.NOT_SPECIFIED,
		     audioFormat,
		     nLengthInFrames);
		initProperties(properties);
	}



	protected AudioFileFormat(Type type,
							  int nLengthInBytes,
							  AudioFormat audioFormat,
							  int nLengthInFrames)
	{
		m_type = type;
		m_audioFormat = audioFormat;
		m_nLengthInFrames = nLengthInFrames;
		m_nLengthInBytes = nLengthInBytes;
		initProperties(null);
	}


	private void initProperties(Map<String, Object> properties)
	{
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



	public Type getType()
	{
		return m_type;
	}



	public int getByteLength()
	{
		return m_nLengthInBytes;
	}




	public AudioFormat getFormat()
	{
		return m_audioFormat;
	}



	public int getFrameLength()
	{
		return m_nLengthInFrames;
	}


	// IDEA: output "not specified" of length == AudioSystem.NOT_SPECIFIED
	public String toString()
	{
		return super.toString() +
			"[type=" + getType() +
			", format=" + getFormat() +
			", lengthInFrames=" + getByteLength() +
			", lengthInBytes=" + getFrameLength() + "]";
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






	public static class Type
	{
		// $$fb 2000-03-31: extension without dot
		public static final Type		AIFC = new Type("AIFC", "aifc");
		public static final Type		AIFF = new Type("AIFF", "aiff");
		public static final Type		AU = new Type("AU", "au");
		public static final Type		SND = new Type("SND", "snd");
		public static final Type		WAVE = new Type("WAVE", "wav");



		private String	m_strName;
		private String	m_strExtension;



		public Type(String strName, String strExtension)
		{
			m_strName = strName;
			m_strExtension = strExtension;
		}



		public String getExtension()
		{
			return m_strExtension;
		}


		/*
		 */
		public final boolean equals(Object obj)
		{
			if (obj == this)
			{
				return true;
			}
			else if (obj == null || (obj.getClass() != this.getClass()))
			{
				return false;
			}
			else
			{
				Type t = (Type) obj;
				return toString().equals(t.toString()) &&
					getExtension().equals(t.getExtension());
			}
		}


		/* TODO: we have to make sure that the strings aren't null.
		   Otherwise, we get a NullPointerException here.
		*/
		public final int hashCode()
		{
			int nHash = 11;
			nHash = 31 * nHash + toString().hashCode();
			nHash = 31 * nHash + getExtension().hashCode();
			// solution if we can't otherwise assure that the strings aren't null:
			// nHash = 31 * nHash + (null == data ? 0 : data.hashCode());
			return nHash;
		}



		public final String toString()
		{
			return m_strName;
		}
	}

}



/*** AudioFileFormat.java ***/
