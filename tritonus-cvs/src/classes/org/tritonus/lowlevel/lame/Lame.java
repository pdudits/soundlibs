/*
 *	Lame.java
 *
 *	This file is part of Tritonus: http://www.tritonus.org/
 */

/*
 *  Copyright (c) 2000,2001,2007 by Florian Bomers <http://www.bomers.de>
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

package org.tritonus.lowlevel.lame;

import java.io.UnsupportedEncodingException;
import javax.sound.sampled.AudioFormat;
import static javax.sound.sampled.AudioSystem.NOT_SPECIFIED;
import org.tritonus.share.TDebug;
import java.util.*;

// TODO: fill frame rate, frame size

/**
 * Low level wrapper for the LAME native encoder.
 * 
 * @author Florian Bomers
 */
public class Lame {

	public static final AudioFormat.Encoding MPEG1L3 = new AudioFormat.Encoding(
			"MPEG1L3");
	// Lame converts automagically to MPEG2 or MPEG2.5, if necessary.
	public static final AudioFormat.Encoding MPEG2L3 = new AudioFormat.Encoding(
			"MPEG2L3");
	public static final AudioFormat.Encoding MPEG2DOT5L3 = new AudioFormat.Encoding(
			"MPEG2DOT5L3");

	// property constants
	/**
	 * legacy: system property key to read the effective encoding of the encoded
	 * audio data, an instance of AudioFormat.Encoding
	 */
	public static final String P_ENCODING = "encoding";
	/**
	 * legacy: system property key to read the effective sample rate of the
	 * encoded audio stream (an instance of Float)
	 */
	public static final String P_SAMPLERATE = "samplerate";
	/**
	 * property key to read/set the VBR mode: an instance of Boolean (default:
	 * false)
	 */
	public static final String P_VBR = "vbr";
	/**
	 * property key to read/set the channel mode: a String, one of
	 * &quot;jointstereo&quot;, &quot;dual&quot;, &quot;mono&quot;,
	 * &quot;auto&quot; (default).
	 */
	public static final String P_CHMODE = "chmode";
	/**
	 * property key to read/set the bitrate: an Integer value. Set to -1 for
	 * default bitrate.
	 */
	public static final String P_BITRATE = "bitrate";
	/**
	 * property key to read/set the quality: an Integer from 1 (highest) to 9
	 * (lowest).
	 */
	public static final String P_QUALITY = "quality";

	// constants from lame.h
	public static final int MPEG_VERSION_2 = 0; // MPEG-2
	public static final int MPEG_VERSION_1 = 1; // MPEG-1
	public static final int MPEG_VERSION_2DOT5 = 2; // MPEG-2.5

	// low mean bitrate in VBR mode
	public static final int QUALITY_LOWEST = 9;
	public static final int QUALITY_LOW = 7;
	public static final int QUALITY_MIDDLE = 5;
	public static final int QUALITY_HIGH = 2;
	// quality==0 not yet coded in LAME (3.83alpha)
	// high mean bitrate in VBR // mode
	public static final int QUALITY_HIGHEST = 1;

	public static final int CHANNEL_MODE_STEREO = 0;
	public static final int CHANNEL_MODE_JOINT_STEREO = 1;
	public static final int CHANNEL_MODE_DUAL_CHANNEL = 2;
	public static final int CHANNEL_MODE_MONO = 3;

	// channel mode has no influence on mono files.
	public static final int CHANNEL_MODE_AUTO = -1;
	public static final int BITRATE_AUTO = -1;

	// suggested maximum buffer size for an mpeg frame
	private static final int DEFAULT_PCM_BUFFER_SIZE = 2048 * 16;

	// frame size=576 for MPEG2 and MPEG2.5
	// =576*2 for MPEG1
	private static boolean libAvailable = false;
	private static String linkError = "";

	private static int DEFAULT_QUALITY = QUALITY_MIDDLE;
	private static int DEFAULT_BITRATE = BITRATE_AUTO;
	private static int DEFAULT_CHANNEL_MODE = CHANNEL_MODE_AUTO;
	// in VBR mode, bitrate is ignored.
	private static boolean DEFAULT_VBR = false;

	private static final int OUT_OF_MEMORY = -300;
	private static final int NOT_INITIALIZED = -301;
	private static final int LAME_ENC_NOT_FOUND = -302;

	private static final String PROPERTY_PREFIX = "tritonus.lame.";

	static {
		try {
			System.loadLibrary("lametritonus");
			libAvailable = true;
		} catch (UnsatisfiedLinkError e) {
			if (TDebug.TraceAllExceptions) {
				TDebug.out(e);
			}
			linkError = e.getMessage();
		}
	}

	/**
	 * Holds LameConf This field is long because on 64 bit architectures, the
	 * native size of ints may be 64 bit.
	 */
	// used from native
	@SuppressWarnings("unused")
	private long m_lNativeGlobalFlags;

	// encoding values
	private int quality = DEFAULT_QUALITY;
	private int bitRate = DEFAULT_BITRATE;
	private boolean vbr = DEFAULT_VBR;
	private int chMode = DEFAULT_CHANNEL_MODE;

	// these fields are set upon successful initialization to show effective
	// values.
	private int effQuality;
	private int effBitRate;
	private int effVbr;
	private int effChMode;
	private int effSampleRate;
	private int effEncoding;

	/**
	 * this flag is set if the user set the encoding properties by way of
	 * system.properties
	 */
	private boolean hadSystemProps = false;

	private void handleNativeException(int resultCode) {
		close();
		if (resultCode == OUT_OF_MEMORY) {
			throw new OutOfMemoryError("out of memory");
		} else if (resultCode == NOT_INITIALIZED) {
			throw new RuntimeException("not initialized");
		} else if (resultCode == LAME_ENC_NOT_FOUND) {
			libAvailable = false;
			linkError = "lame_enc.dll not found";
			throw new IllegalArgumentException(linkError);
		}
	}

	/**
	 * Initializes the encoder with the given source/PCM format. The default mp3
	 * encoding parameters are used, see DEFAULT_BITRATE, DEFAULT_CHANNEL_MODE,
	 * DEFAULT_QUALITY, and DEFAULT_VBR.
	 * 
	 * @throws IllegalArgumentException when parameters are not supported by
	 *             LAME.
	 */
	public Lame(AudioFormat sourceFormat) {
		readParams(sourceFormat, null);
		initParams(sourceFormat);
	}

	/**
	 * Initializes the encoder with the given source/PCM format. The mp3
	 * parameters are read from the targetFormat's properties. For any parameter
	 * that is not set, global system properties are queried for backwards
	 * tritonus compatibility. Last, parameters will use the default values
	 * DEFAULT_BITRATE, DEFAULT_CHANNEL_MODE, DEFAULT_QUALITY, and DEFAULT_VBR.
	 * 
	 * @throws IllegalArgumentException when parameters are not supported by
	 *             LAME.
	 */
	public Lame(AudioFormat sourceFormat, AudioFormat targetFormat) {
		readParams(sourceFormat, targetFormat.properties());
		initParams(sourceFormat);
	}

	/**
	 * Initializes the encoder, overriding any parameters set in the audio
	 * format's properties or in the system properties.
	 * 
	 * @throws IllegalArgumentException when parameters are not supported by
	 *             LAME.
	 */
	public Lame(AudioFormat sourceFormat, int bitRate, int channelMode,
			int quality, boolean VBR) {
		this.bitRate = bitRate;
		this.chMode = channelMode;
		this.quality = quality;
		this.vbr = VBR;
		initParams(sourceFormat);
	}

	private void readParams(AudioFormat sourceFormat, Map<String, Object> props) {
		if (props == null || props.size() == 0) {
			// legacy support for system properties
			readSystemProps();
		}
		if (props != null) {
			readProps(props);
		}
	}

	private void initParams(AudioFormat sourceFormat) {
		// simple check that bitrate is not too high for MPEG2 and MPEG2.5
		// todo: exception ?
		if (sourceFormat.getSampleRate() < 32000 && bitRate > 160) {
			bitRate = 160;
		}
		if (TDebug.TraceAudioConverter) {
			String br = bitRate < 0 ? "auto" : (String.valueOf(bitRate)
					+ "KBit/s");
			TDebug.out("LAME parameters: channels="
					+ sourceFormat.getChannels() + "  sample rate="
					+ (Math.round(sourceFormat.getSampleRate()) + "Hz")
					+ "  bitrate=" + br);
			TDebug.out("                 channelMode=" + chmode2string(chMode)
					+ "   quality=" + quality + " (" + quality2string(quality)
					+ ")   VBR=" + vbr + "  bigEndian="
					+ sourceFormat.isBigEndian());
		}
		int result = nInitParams(sourceFormat.getChannels(),
				Math.round(sourceFormat.getSampleRate()), bitRate, chMode,
				quality, vbr, sourceFormat.isBigEndian());
		if (result < 0) {
			handleNativeException(result);
			throw new IllegalArgumentException(
					"parameters not supported by LAME (returned " + result
							+ ")");
		}
		if (TDebug.TraceAudioConverter) {
			TDebug.out("LAME effective quality=" + effQuality + " ("
					+ quality2string(effQuality) + ")");
		}
		// legacy provide effective parameters to user by way of system
		// properties
		if (hadSystemProps) {
			setEffectiveParamsToSystemProps();
		}
	}

	/**
	 * Initializes the lame encoder. Throws IllegalArgumentException when
	 * parameters are not supported by LAME.
	 */
	private native int nInitParams(int channels, int sampleRate, int bitrate,
			int mode, int quality, boolean VBR, boolean bigEndian);

	/**
	 * returns -1 if string is too short or returns one of the exception
	 * constants if everything OK, returns the length of the string
	 */
	private native int nGetEncoderVersion(byte[] string);

	public String getEncoderVersion() {
		byte[] string = new byte[300];
		int res = nGetEncoderVersion(string);
		if (res < 0) {
			if (res == -1) {
				throw new RuntimeException(
						"Unexpected error in Lame.getEncoderVersion()");
			}
			handleNativeException(res);
		}
		String sRes = "";
		if (res > 0) {
			try {
				sRes = new String(string, 0, res, "ISO-8859-1");
			} catch (UnsupportedEncodingException uee) {
				if (TDebug.TraceAllExceptions) {
					TDebug.out(uee);
				}
				sRes = new String(string, 0, res);
			}
		}
		return sRes;
	}

	private native int nGetPCMBufferSize(int suggested);

	/**
	 * Returns the buffer needed pcm buffer size. The passed parameter is a
	 * wished buffer size. The implementation of the encoder may return a lower
	 * or higher buffer size. The encoder must be initalized (i.e. not closed)
	 * at this point. A return value of <0 denotes an error.
	 */
	public int getPCMBufferSize() {
		int ret = nGetPCMBufferSize(DEFAULT_PCM_BUFFER_SIZE);
		if (ret < 0) {
			handleNativeException(ret);
			throw new RuntimeException(
					"Unknown error in Lame.nGetPCMBufferSize(). Resultcode="
							+ ret);
		}
		return ret;
	}

	public int getMP3BufferSize() {
		// bad estimate :)
		return getPCMBufferSize() / 2 + 1024;
	}

	private native int nEncodeBuffer(byte[] pcm, int offset, int length,
			byte[] encoded);

	/**
	 * Encode a block of data. Throws IllegalArgumentException when parameters
	 * are wrong. When the <code>encoded</code> array is too small, an
	 * ArrayIndexOutOfBoundsException is thrown. <code>length</code> should be
	 * the value returned by getPCMBufferSize.
	 * 
	 * @return the number of bytes written to <code>encoded</code>. May be 0.
	 */
	public int encodeBuffer(byte[] pcm, int offset, int length, byte[] encoded)
			throws ArrayIndexOutOfBoundsException {
		if (length < 0 || (offset + length) > pcm.length) {
			throw new IllegalArgumentException("inconsistent parameters");
		}
		int result = nEncodeBuffer(pcm, offset, length, encoded);
		if (result < 0) {
			if (result == -1) {
				throw new ArrayIndexOutOfBoundsException(
						"Encode buffer too small");
			}
			handleNativeException(result);
			throw new RuntimeException("crucial error in encodeBuffer.");
		}
		return result;
	}

	/**
	 * Has to be called to finish encoding. <code>encoded</code> may be null.
	 * 
	 * @return the number of bytes written to <code>encoded</code>
	 */
	private native int nEncodeFinish(byte[] encoded);

	public int encodeFinish(byte[] encoded) {
		return nEncodeFinish(encoded);
	}

	/*
	 * Deallocates resources used by the native library. *MUST* be called !
	 */
	private native void nClose();

	public void close() {
		nClose();
	}

	/*
	 * Returns whether the libraries are installed correctly.
	 */
	public static boolean isLibAvailable() {
		return libAvailable;
	}

	public static String getLinkError() {
		return linkError;
	}

	// properties
	private void readProps(Map<String, Object> props) {
		Object q = props.get(P_QUALITY);
		if (q instanceof String) {
			quality = string2quality(((String) q).toLowerCase(), quality);
		} else if (q instanceof Integer) {
			quality = (Integer) q;
		} else if (q != null) {
			throw new IllegalArgumentException(
					"illegal type of quality property: " + q);
		}
		q = props.get(P_BITRATE);
		if (q instanceof String) {
			bitRate = Integer.parseInt((String) q);
		} else if (q instanceof Integer) {
			bitRate = (Integer) q;
		} else if (q != null) {
			throw new IllegalArgumentException(
					"illegal type of bitrate property: " + q);
		}
		q = props.get(P_CHMODE);
		if (q instanceof String) {
			chMode = string2chmode(((String) q).toLowerCase(), chMode);
		} else if (q != null) {
			throw new IllegalArgumentException(
					"illegal type of chmode property: " + q);
		}
		q = props.get(P_VBR);
		if (q instanceof String) {
			vbr = string2bool(((String) q));
		} else if (q instanceof Boolean) {
			vbr = (Boolean) q;
		} else if (q != null) {
			throw new IllegalArgumentException("illegal type of vbr property: "
					+ q);
		}
	}

	/**
	 * Return the audioformat representing the encoded mp3 stream. The format
	 * object will have the following properties:
	 * <ul>
	 * <li>quality: an Integer, 1 (highest) to 9 (lowest)
	 * <li>bitrate: an Integer, 32...320 kbit/s
	 * <li>chmode: channel mode, a String, one of &quot;jointstereo&quot;,
	 * &quot;dual&quot;, &quot;mono&quot;, &quot;auto&quot; (default).
	 * <li>vbr: a Boolean
	 * <li>encoder.version: a string with the version of the encoder
	 * <li>encoder.name: a string with the name of the encoder
	 * </ul>
	 */
	public AudioFormat getEffectiveFormat() {
		// first gather properties
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put(P_QUALITY, getEffectiveQuality());
		map.put(P_BITRATE, getEffectiveBitRate());
		map.put(P_CHMODE, chmode2string(getEffectiveChannelMode()));
		map.put(P_VBR, getEffectiveVBR());
		// map.put(P_SAMPLERATE, getEffectiveSampleRate());
		// map.put(P_ENCODING,getEffectiveEncoding());
		map.put("encoder.name", "LAME");
		map.put("encoder.version", getEncoderVersion());
		int channels = 2;
		if (chMode == CHANNEL_MODE_MONO) {
			channels = 1;
		}
		return new AudioFormat(getEffectiveEncoding(),
				getEffectiveSampleRate(), NOT_SPECIFIED, channels,
				NOT_SPECIFIED, NOT_SPECIFIED, false, map);
	}

	public int getEffectiveQuality() {
		if (effQuality >= QUALITY_LOWEST) {
			return QUALITY_LOWEST;
		} else if (effQuality >= QUALITY_LOW) {
			return QUALITY_LOW;
		} else if (effQuality >= QUALITY_MIDDLE) {
			return QUALITY_MIDDLE;
		} else if (effQuality >= QUALITY_HIGH) {
			return QUALITY_HIGH;
		}
		return QUALITY_HIGHEST;
	}

	public int getEffectiveBitRate() {
		return effBitRate;
	}

	public int getEffectiveChannelMode() {
		return effChMode;
	}

	public boolean getEffectiveVBR() {
		return effVbr != 0;
	}

	public int getEffectiveSampleRate() {
		return effSampleRate;
	}

	public AudioFormat.Encoding getEffectiveEncoding() {
		if (effEncoding == MPEG_VERSION_2) {
			if (getEffectiveSampleRate() < 16000) {
				return MPEG2DOT5L3;
			}
			return MPEG2L3;
		} else if (effEncoding == MPEG_VERSION_2DOT5) {
			return MPEG2DOT5L3;
		}
		// default
		return MPEG1L3;
	}

	// LEGACY support: read/write encoding parameters from/to system.properties

	/** legacy: set effective parameters in system properties */
	private void setEffectiveParamsToSystemProps() {
		try {
			System.setProperty(PROPERTY_PREFIX + "effective" + "." + P_QUALITY,
					quality2string(getEffectiveQuality()));
			System.setProperty(PROPERTY_PREFIX + "effective" + "." + P_BITRATE,
					String.valueOf(getEffectiveBitRate()));
			System.setProperty(PROPERTY_PREFIX + "effective" + "." + P_CHMODE,
					chmode2string(getEffectiveChannelMode()));
			System.setProperty(PROPERTY_PREFIX + "effective" + "." + P_VBR,
					String.valueOf(getEffectiveVBR()));
			System.setProperty(PROPERTY_PREFIX + "effective" + "."
					+ P_SAMPLERATE, String.valueOf(getEffectiveSampleRate()));
			System.setProperty(
					PROPERTY_PREFIX + "effective" + "." + P_ENCODING,
					getEffectiveEncoding().toString());
			System.setProperty(PROPERTY_PREFIX + "encoder.version",
					getEncoderVersion());
		} catch (Throwable t) {
			if (TDebug.TraceAllExceptions) {
				TDebug.out(t);
			}
		}
	}

	/**
	 * workaround for missing paramtrization possibilities for
	 * FormatConversionProviders
	 */
	private void readSystemProps() {
		String v = getStringProperty(P_QUALITY, quality2string(quality));
		quality = string2quality(v.toLowerCase(), quality);
		bitRate = getIntProperty(P_BITRATE, bitRate);
		v = getStringProperty(P_CHMODE, chmode2string(chMode));
		chMode = string2chmode(v.toLowerCase(), chMode);
		vbr = getBooleanProperty(P_VBR, vbr);
		if (hadSystemProps) {
			// set the parameters back so that user program can verify them
			try {
				System.setProperty(PROPERTY_PREFIX + P_QUALITY,
						quality2string(DEFAULT_QUALITY));
				System.setProperty(PROPERTY_PREFIX + P_BITRATE,
						String.valueOf(DEFAULT_BITRATE));
				System.setProperty(PROPERTY_PREFIX + P_CHMODE,
						chmode2string(DEFAULT_CHANNEL_MODE));
				System.setProperty(PROPERTY_PREFIX + P_VBR,
						String.valueOf(DEFAULT_VBR));
			} catch (Throwable t) {
				if (TDebug.TraceAllExceptions) {
					TDebug.out(t);
				}
			}
		}
	}

	private String quality2string(int quality) {
		if (quality >= QUALITY_LOWEST) {
			return "lowest";
		} else if (quality >= QUALITY_LOW) {
			return "low";
		} else if (quality >= QUALITY_MIDDLE) {
			return "middle";
		} else if (quality >= QUALITY_HIGH) {
			return "high";
		}
		return "highest";
	}

	private int string2quality(String quality, int def) {
		if (quality.equals("lowest")) {
			return QUALITY_LOWEST;
		} else if (quality.equals("low")) {
			return QUALITY_LOW;
		} else if (quality.equals("middle")) {
			return QUALITY_MIDDLE;
		} else if (quality.equals("high")) {
			return QUALITY_HIGH;
		} else if (quality.equals("highest")) {
			return QUALITY_HIGHEST;
		}
		return def;
	}

	private String chmode2string(int chmode) {
		if (chmode == CHANNEL_MODE_STEREO) {
			return "stereo";
		} else if (chmode == CHANNEL_MODE_JOINT_STEREO) {
			return "jointstereo";
		} else if (chmode == CHANNEL_MODE_DUAL_CHANNEL) {
			return "dual";
		} else if (chmode == CHANNEL_MODE_MONO) {
			return "mono";
		} else if (chmode == CHANNEL_MODE_AUTO) {
			return "auto";
		}
		return "auto";
	}

	private int string2chmode(String chmode, int def) {
		if (chmode.equals("stereo")) {
			return CHANNEL_MODE_STEREO;
		} else if (chmode.equals("jointstereo")) {
			return CHANNEL_MODE_JOINT_STEREO;
		} else if (chmode.equals("dual")) {
			return CHANNEL_MODE_DUAL_CHANNEL;
		} else if (chmode.equals("mono")) {
			return CHANNEL_MODE_MONO;
		} else if (chmode.equals("auto")) {
			return CHANNEL_MODE_AUTO;
		}
		return def;
	}

	/**
	 * @return true if val is starts with t or y or on, false if val starts with
	 *         f or n or off.
	 * @throws IllegalArgumentException if val is neither true nor false
	 */
	private static boolean string2bool(String val) {
		if (val.length() > 0) {
			if ((val.charAt(0) == 'f') // false
					|| (val.charAt(0) == 'n') // no
					|| (val.equals("off"))) {
				return false;
			}
			if ((val.charAt(0) == 't') // true
					|| (val.charAt(0) == 'y') // yes
					|| (val.equals("on"))) {
				return true;
			}
		}
		throw new IllegalArgumentException(
				"wrong string for boolean property: " + val);
	}

	private boolean getBooleanProperty(String strName, boolean def) {
		String strPropertyName = PROPERTY_PREFIX + strName;
		String strValue = def ? "true" : "false";
		try {
			String s = System.getProperty(strPropertyName);
			if (s != null && s.length() > 0) {
				hadSystemProps = true;
				strValue = s;
			}
		} catch (Throwable t) {
			if (TDebug.TraceAllExceptions) {
				TDebug.out(t);
			}
		}
		strValue = strValue.toLowerCase();
		boolean bValue = false;
		if (strValue.length() > 0) {
			if (def) {
				bValue = (strValue.charAt(0) != 'f') // false
						&& (strValue.charAt(0) != 'n') // no
						&& (!strValue.equals("off"));
			} else {
				bValue = (strValue.charAt(0) == 't') // true
						|| (strValue.charAt(0) == 'y') // yes
						|| (strValue.equals("on"));
			}
		}
		return bValue;
	}

	private String getStringProperty(String strName, String def) {
		String strPropertyName = PROPERTY_PREFIX + strName;
		String strValue = def;
		try {
			String s = System.getProperty(strPropertyName);
			if (s != null && s.length() > 0) {
				hadSystemProps = true;
				strValue = s;
			}
		} catch (Throwable t) {
			if (TDebug.TraceAllExceptions) {
				TDebug.out(t);
			}
		}
		return strValue;
	}

	private int getIntProperty(String strName, int def) {
		String strPropertyName = PROPERTY_PREFIX + strName;
		int value = def;
		try {
			String s = System.getProperty(strPropertyName);
			if (s != null && s.length() > 0) {
				hadSystemProps = true;
				value = new Integer(s).intValue();
			}
		} catch (Throwable e) {
			if (TDebug.TraceAllExceptions) {
				TDebug.out(e);
			}
		}
		return value;
	}

}

/** * Lame.java ** */
