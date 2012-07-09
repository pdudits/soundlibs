/*
 *	AudioUtils.java
 *
 *	This file is part of Tritonus: http://www.tritonus.org/
 */

/*
 *  Copyright (c) 2000 by Matthias Pfisterer
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

package org.tritonus.share.sampled;

import java.util.Iterator;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.Mixer;



@SuppressWarnings("cast")
public class AudioUtils
{
	public static long getLengthInBytes(AudioInputStream audioInputStream)
	{
		return getLengthInBytes(audioInputStream.getFormat(),
					audioInputStream.getFrameLength());
/*
		long	lLengthInFrames = audioInputStream.getFrameLength();
		int	nFrameSize = audioInputStream.getFormat().getFrameSize();
		if (lLengthInFrames >= 0 && nFrameSize >= 1)
		{
			return lLengthInFrames * nFrameSize;
		}
		else
		{
			return AudioSystem.NOT_SPECIFIED;
		}
*/
	}



	/**
	 *	if the passed value for lLength is
	 *	AudioSystem.NOT_SPECIFIED (unknown
	 *	length), the length in bytes becomes
	 *	AudioSystem.NOT_SPECIFIED, too.
	 */
	public static long getLengthInBytes(AudioFormat audioFormat,
					    long lLengthInFrames)
	{
		int	nFrameSize = audioFormat.getFrameSize();
		if (lLengthInFrames >= 0 && nFrameSize >= 1)
		{
			return lLengthInFrames * nFrameSize;
		}
		else
		{
			return AudioSystem.NOT_SPECIFIED;
		}
	}



	@SuppressWarnings("unchecked")
	public static boolean containsFormat(AudioFormat sourceFormat,
					     Iterator possibleFormats)
	{
		while (possibleFormats.hasNext())
		{
			AudioFormat	format = (AudioFormat) possibleFormats.next();
			if (AudioFormats.matches(format, sourceFormat))
			{
				return true;
			}
		}
		return false;
	}
	
	/** @return the frame size, given the sample size in bits and number of channels */
	public static int getFrameSize(int channels, int sampleSizeInBits) {
		if (channels < 0 || sampleSizeInBits < 0) {
			return AudioSystem.NOT_SPECIFIED;
		}
		return ((sampleSizeInBits + 7) / 8) * channels;
	}

	/** Conversion milliseconds to bytes */
	public static long millis2Bytes(long ms, AudioFormat format) {
		return millis2Bytes(ms, (double) format.getFrameRate(), format.getFrameSize());
	}
	/** Conversion milliseconds to bytes */
	public static long millis2Bytes(long ms, double frameRate, int frameSize) {
		return (long) (ms*frameRate/1000*frameSize);
	}
	/** convert milliseconds to bytes. Be careful to not exceed the integer maximum value */
	public static int millis2Bytes(int ms, AudioFormat format) {
		return millis2Bytes(ms, (double) format.getFrameRate(), format.getFrameSize());
	}
	/** convert milliseconds to bytes. Be careful to not exceed the integer maximum value */
	public static int millis2Bytes(int ms, double frameRate, int frameSize) {
		return (int) (ms*frameRate/1000*frameSize);
	}
	/** convert milliseconds to bytes. */
	public static long millis2Bytes(double ms, AudioFormat format) {
		return millis2Bytes(ms, format.getFrameRate(), format.getFrameSize());
	}
	/** convert milliseconds to bytes. */
	public static long millis2Bytes(double ms, double frameRate, int frameSize) {
		return (long) (ms*frameRate/1000.0*frameSize);
	}

	/** Conversion milliseconds to bytes, return value is frame-aligned */
	public static long millis2BytesFrameAligned(long ms, AudioFormat format) {
		return millis2BytesFrameAligned(ms, (double) format.getFrameRate(), format.getFrameSize());
	}
	/** Conversion milliseconds to bytes, return value is frame-aligned */
	public static long millis2BytesFrameAligned(long ms, double frameRate, int frameSize) {
		return ((long) (ms*frameRate/1000))*frameSize;
	}
	/** Conversion milliseconds to bytes, return value is frame-aligned */
	public static int millis2BytesFrameAligned(int ms, AudioFormat format) {
		return millis2BytesFrameAligned(ms, (double) format.getFrameRate(), format.getFrameSize());
	}
	/** Conversion milliseconds to bytes, return value is frame-aligned */
	public static int millis2BytesFrameAligned(int ms, double frameRate, int frameSize) {
		return ((int) (ms*frameRate/1000))*frameSize;
	}
	/** Conversion milliseconds to bytes, return value is frame-aligned */
	public static long millis2BytesFrameAligned(double ms, AudioFormat format) {
		return millis2BytesFrameAligned(ms, (double) format.getFrameRate(), format.getFrameSize());
	}
	/** Conversion milliseconds to bytes, return value is frame-aligned */
	public static long millis2BytesFrameAligned(double ms, double frameRate, int frameSize) {
		return ((long) (ms*frameRate/1000))*frameSize;
	}

	/** Conversion milliseconds to frames (samples) */
	public static long millis2Frames(long ms, AudioFormat format) {
		return millis2Frames(ms, (double) format.getFrameRate());
	}
	/** Conversion milliseconds to frames (samples) */
	public static long millis2Frames(long ms, double frameRate) {
		return (long) (ms*frameRate/1000.0);
	}
	/** Conversion milliseconds to frames (samples) */
	public static int millis2Frames(int ms, AudioFormat format) {
		return millis2Frames(ms, (double) format.getFrameRate());
	}
	/** Conversion milliseconds to frames (samples) */
	public static int millis2Frames(int ms, double frameRate) {
		return (int) (ms*frameRate/1000.0);
	}
	/** Conversion milliseconds to frames (samples) */
	public static long millis2Frames(double ms, AudioFormat format) {
		return (long) millis2FramesD(ms, (double) format.getFrameRate());
	}
	/** Conversion milliseconds to frames (samples) */
	public static long millis2Frames(double ms, double frameRate) {
		return (long) millis2FramesD(ms, frameRate);
	}
	/** Conversion milliseconds to frames (samples) */
	public static double millis2FramesD(double ms, AudioFormat format) {
		return millis2FramesD(ms, (double) format.getFrameRate());
	}
	/** Conversion milliseconds to frames (samples) */
	public static double millis2FramesD(double ms, double frameRate) {
		return ms*frameRate/1000.0;
	}

	/** Conversion bytes to milliseconds */
	public static long bytes2Millis(long bytes, AudioFormat format) {
		return (long) frames2MillisD(bytes/format.getFrameSize(), format.getFrameRate());
	}
	/** Conversion bytes to milliseconds */
	public static int bytes2Millis(int bytes, AudioFormat format) {
		return (int) frames2MillisD(bytes/format.getFrameSize(), format.getFrameRate());
	}
	/** Conversion bytes to milliseconds */
	public static double bytes2MillisD(long bytes, AudioFormat format) {
		return frames2MillisD(bytes/format.getFrameSize(), format.getFrameRate());
	}
	/** Conversion bytes to milliseconds */
	public static double bytes2MillisD(long bytes, double frameRate, int frameSize) {
		return frames2MillisD(bytes/frameSize, frameRate);
	}
	/** Conversion frames to milliseconds */
	public static long frames2Millis(long frames, AudioFormat format) {
		return (long) frames2MillisD(frames, format.getFrameRate());
	}
	/** Conversion frames to milliseconds */
	public static int frames2Millis(int frames, AudioFormat format) {
		return (int) frames2MillisD(frames, format.getFrameRate());
	}
	/** Conversion frames to milliseconds */
	public static double frames2MillisD(long frames, AudioFormat format) {
		return frames2MillisD(frames, format.getFrameRate());
	}
	/** Conversion frames to milliseconds */
	public static double frames2MillisD(long frames, double frameRate) {
		return frames/frameRate*1000.0;
	}
	
	/**
	 * 
	 * @param sr1 the first sample rate to compare
	 * @param sr2 the second sample rate to compare
	 * @return true if the sample rates are (almost) identical
	 */
	public static boolean sampleRateEquals(float sr1, float sr2) {
		return Math.abs(sr1-sr2)<0.0000001;
	}
	
	/**
	 * @param format the audio format to test
	 * @return true if the format is either PCM_SIGNED or PCM_UNSIGNED
	 */
	public static boolean isPCM(AudioFormat format) {
		return format.getEncoding().equals(AudioFormat.Encoding.PCM_SIGNED)
			|| format.getEncoding().equals(AudioFormat.Encoding.PCM_UNSIGNED);
	}
	
	/**
	 * Return if the passed mixer info is the Java Sound Audio Engine.
	 * 
	 * @param mixerInfo the mixer info to query
	 * @return true if the mixer info describes the Java Sound Audio Engine
	 */
	public static boolean isJavaSoundAudioEngine(Mixer.Info mixerInfo) {
		return (mixerInfo != null) && (mixerInfo.getName() != null)
				&& mixerInfo.getName().equals("Java Sound Audio Engine");
	}
	
	/**
	 * Return if the passed line is writing to or reading from the Java Sound Audio Engine.
	 * 
	 * @param line the data line to query
	 * @return true if the line is using the Java Sound Audio Engine
	 */
	public static boolean isJavaSoundAudioEngine(DataLine line) {
		if (line != null) {
			String clazz = line.getClass().toString(); 
			return clazz.indexOf("MixerSourceLine") >= 0
			|| clazz.indexOf("MixerClip") >= 0
			|| clazz.indexOf("SimpleInputDevice") >= 0;
		}
		return false;
	}

		/**
	 * tries to guess if this program is running on a big endian platform
	 * @return true if the system's native endianness is big endian
	 */
	public static boolean isSystemBigEndian() {
		return java.nio.ByteOrder.nativeOrder().equals(java.nio.ByteOrder.BIG_ENDIAN);
	}


    //$$fb 2000-07-18: added these debugging functions
    public static String NS_or_number(int number) {
		return (number==AudioSystem.NOT_SPECIFIED)?"NOT_SPECIFIED":String.valueOf(number);
    }
    public static String NS_or_number(float number) {
		return (number==AudioSystem.NOT_SPECIFIED)?"NOT_SPECIFIED":String.valueOf(number);
    }

    /** 
     * For debugging purposes.
     */
    public static String format2ShortStr(AudioFormat format) {
		return format.getEncoding() + "-" +
	    	NS_or_number(format.getChannels()) + "ch-" +
	    	NS_or_number(format.getSampleSizeInBits()) + "bit-" +
	    	NS_or_number(((int)format.getSampleRate())) + "Hz-"+
	    	(format.isBigEndian() ? "be" : "le");
    }
    
	/**
	 * The value used for negative infinity in decibels. The default value is
	 * -100.0, which is approximately the s/n ratio achieved with 16-bit samples.
	 * If you use higher resolution, set this to a lower value, like -150.0.
	 */
	public static double SILENCE_DECIBEL = -100.0;

	/**
	 * Get decibel from a linear factor.
	 * 
	 * @param linearFactor 0..1..inf
	 * @return the converted decibel (SILENCE_DECIBEL...0...inf)
	 */
	public final static double linear2decibel(double linearFactor) {
		if (linearFactor <= 0.0) {
			return SILENCE_DECIBEL;
		}
		double ret = Math.log10(linearFactor) * 20.0;
		if (ret < SILENCE_DECIBEL) {
			ret = SILENCE_DECIBEL;
		}
		return ret;
	}

	/**
	 * Calculate the linear factor corresponding to the specified decibel level.
	 * 
	 * @param decibels [SILENCE_DECIBEL...0...inf]
	 * @return linear factor [0...1...inf]
	 */
	public final static double decibel2linear(double decibels) {
		if (decibels <= SILENCE_DECIBEL) {
			return 0.0;
		}
		return Math.pow(10.0, decibels * 0.05);
	}

    

}



/*** AudioUtils.java ***/
