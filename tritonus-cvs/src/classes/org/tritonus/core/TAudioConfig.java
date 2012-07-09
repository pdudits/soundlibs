/*
 *	TAudioConfig.java
 *
 *	This file is part of Tritonus: http://www.tritonus.org/
 */

/*
 *  Copyright (c) 1999 - 2004 by Matthias Pfisterer
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

package org.tritonus.core;

import java.util.Set;
import java.util.Iterator;

import javax.sound.sampled.Mixer;

import javax.sound.sampled.spi.AudioFileWriter;
import javax.sound.sampled.spi.AudioFileReader;
import javax.sound.sampled.spi.FormatConversionProvider;
import javax.sound.sampled.spi.MixerProvider;

import org.tritonus.share.ArraySet;
import org.tritonus.core.TInit.ProviderRegistrationAction;


/** TODO:
 */
public class TAudioConfig
{
	private static Set<AudioFileWriter>		sm_audioFileWriters = null;
	private static Set<AudioFileReader>		sm_audioFileReaders = null;
	private static Set<FormatConversionProvider>	sm_formatConversionProviders = null;
	private static Set<MixerProvider>		sm_mixerProviders = null;

	private static Mixer.Info	sm_defaultMixerInfo;



	/** Constructor to prevent instantiation.
	 */
	private TAudioConfig()
	{
	}


	private static void registerAudioFileReaders()
	{
		ProviderRegistrationAction	action = null;
		action = new ProviderRegistrationAction()
			{
				public void register(Object obj)
					throws Exception
					{
						AudioFileReader	provider = (AudioFileReader) obj;
						TAudioConfig.addAudioFileReader(provider);
					}
			};
		TInit.registerClasses(AudioFileReader.class, action);
	}



	private static void registerAudioFileWriters()
	{
		ProviderRegistrationAction	action = null;
		action = new ProviderRegistrationAction()
			{
				public void register(Object obj)
					throws Exception
					{
						AudioFileWriter	provider = (AudioFileWriter) obj;
						TAudioConfig.addAudioFileWriter(provider);
					}
			};
		TInit.registerClasses(AudioFileWriter.class, action);
	}



	private static void registerFormatConversionProviders()
	{
		ProviderRegistrationAction	action = null;
		action = new ProviderRegistrationAction()
			{
				public void register(Object obj)
					throws Exception
					{
						FormatConversionProvider	provider = (FormatConversionProvider) obj;
						TAudioConfig.addFormatConversionProvider(provider);
					}
			};
		TInit.registerClasses(FormatConversionProvider.class, action);
	}



	private static void registerMixerProviders()
	{
		ProviderRegistrationAction	action = null;
		action = new ProviderRegistrationAction()
			{
				public void register(Object obj)
					throws Exception
					{
						MixerProvider	provider = (MixerProvider) obj;
						TAudioConfig.addMixerProvider(provider);
					}
			};
		TInit.registerClasses(MixerProvider.class, action);
	}


	////////////////////////////////////////////////////////////////


	public static synchronized void addAudioFileReader(AudioFileReader provider)
	{
		getAudioFileReadersImpl().add(provider);
	}



	public static synchronized void removeAudioFileReader(AudioFileReader provider)
	{
		getAudioFileReadersImpl().remove(provider);
	}



	public static synchronized Iterator<AudioFileReader> getAudioFileReaders()
	{
		return getAudioFileReadersImpl().iterator();
	}



	private static synchronized Set<AudioFileReader> getAudioFileReadersImpl()
	{
		if (sm_audioFileReaders == null)
		{
			sm_audioFileReaders = new ArraySet<AudioFileReader>();
			registerAudioFileReaders();
		}
		return sm_audioFileReaders;
	}



	public static synchronized void addAudioFileWriter(AudioFileWriter provider)
	{
		getAudioFileWritersImpl().add(provider);
	}



	public static synchronized void removeAudioFileWriter(AudioFileWriter provider)
	{
		getAudioFileWritersImpl().remove(provider);
	}



	public static synchronized Iterator<AudioFileWriter> getAudioFileWriters()
	{
		return getAudioFileWritersImpl().iterator();
	}



	private static synchronized Set<AudioFileWriter> getAudioFileWritersImpl()
	{
		if (sm_audioFileWriters == null)
		{
			sm_audioFileWriters = new ArraySet<AudioFileWriter>();
			registerAudioFileWriters();
		}
		return sm_audioFileWriters;
	}



	public static synchronized void addFormatConversionProvider(FormatConversionProvider provider)
	{
		getFormatConversionProvidersImpl().add(provider);
	}



	public static synchronized void removeFormatConversionProvider(FormatConversionProvider provider)
	{
		getFormatConversionProvidersImpl().remove(provider);
	}



	public static synchronized Iterator<FormatConversionProvider> getFormatConversionProviders()
	{
		return getFormatConversionProvidersImpl().iterator();
	}



	private static synchronized Set<FormatConversionProvider> getFormatConversionProvidersImpl()
	{
		if (sm_formatConversionProviders == null)
		{
			sm_formatConversionProviders = new ArraySet<FormatConversionProvider>();
			registerFormatConversionProviders();
		}
		return sm_formatConversionProviders;
	}



	public static synchronized void addMixerProvider(MixerProvider provider)
	{
		getMixerProvidersImpl().add(provider);
	}



	public static synchronized void removeMixerProvider(MixerProvider provider)
	{
		getMixerProvidersImpl().remove(provider);
	}



	public static synchronized Iterator<MixerProvider> getMixerProviders()
	{
		return getMixerProvidersImpl().iterator();
	}


	private static synchronized Set<MixerProvider> getMixerProvidersImpl()
	{
		if (sm_mixerProviders == null)
		{
			sm_mixerProviders = new ArraySet<MixerProvider>();
			registerMixerProviders();
		}
		return sm_mixerProviders;
	}



	// TODO: a way to set the default mixer
	public static Mixer.Info getDefaultMixerInfo()
	{
		return sm_defaultMixerInfo;
	}

}



/*** TAudioConfig.java ***/
