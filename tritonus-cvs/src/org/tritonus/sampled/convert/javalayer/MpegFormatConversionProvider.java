/*
 *	MpegFormatConversionProvider.java
 *
 *	This file is part of Tritonus: http://www.tritonus.org/
 */

/*
 *  Copyright (c) 1999 - 2004 by Matthias Pfisterer
 *  Copyright (c) 2008 by Florian Bomers
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

package org.tritonus.sampled.convert.javalayer;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.IOException;

import java.util.Arrays;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import org.tritonus.share.TDebug;
import org.tritonus.share.sampled.AudioUtils;
import org.tritonus.share.sampled.TConversionTool;
import org.tritonus.share.sampled.convert.TEncodingFormatConversionProvider;
import org.tritonus.share.sampled.convert.TAsynchronousFilteredAudioInputStream;

import javazoom.jl.decoder.Bitstream;
import javazoom.jl.decoder.BitstreamException;
import javazoom.jl.decoder.Decoder;
import javazoom.jl.decoder.DecoderException;
import javazoom.jl.decoder.Header;
import javazoom.jl.decoder.Obuffer;

/**
 * ConversionProvider for decoding mp3 files.
 *
 * @author Matthias Pfisterer
 * @author Florian Bomers
 */


public class MpegFormatConversionProvider
extends TEncodingFormatConversionProvider
{
	public static final AudioFormat.Encoding	MPEG1L1 = new AudioFormat.Encoding("MPEG1L1");
	public static final AudioFormat.Encoding	MPEG1L2 = new AudioFormat.Encoding("MPEG1L2");
	public static final AudioFormat.Encoding	MPEG1L3 = new AudioFormat.Encoding("MPEG1L3");
	public static final AudioFormat.Encoding	MP3     = new AudioFormat.Encoding("MP3"); // alias for MPEG1L3
	public static final AudioFormat.Encoding	MPEG2L1 = new AudioFormat.Encoding("MPEG2L1");
	public static final AudioFormat.Encoding	MPEG2L2 = new AudioFormat.Encoding("MPEG2L2");
	public static final AudioFormat.Encoding	MPEG2L3 = new AudioFormat.Encoding("MPEG2L3");
	public static final AudioFormat.Encoding	MPEG2DOT5L1 = new AudioFormat.Encoding("MPEG2DOT5L1");
	public static final AudioFormat.Encoding	MPEG2DOT5L2 = new AudioFormat.Encoding("MPEG2DOT5L2");
	public static final AudioFormat.Encoding	MPEG2DOT5L3 = new AudioFormat.Encoding("MPEG2DOT5L3");

	private static final AudioFormat.Encoding	PCM_SIGNED = AudioFormat.Encoding.PCM_SIGNED;


	/* TODO: mechanism to make the double specification with
	   different endianess obsolete. */
	private static final AudioFormat[]	INPUT_FORMATS =
	{
		// mono
		new AudioFormat(MPEG1L1, -1.0F, -1, 1, -1, -1.0F, false),
		new AudioFormat(MPEG1L1, -1.0F, -1, 1, -1, -1.0F, true),
		// stereo
		new AudioFormat(MPEG1L1, -1.0F, -1, 2, -1, -1.0F, false),
		new AudioFormat(MPEG1L1, -1.0F, -1, 2, -1, -1.0F, true),

		// mono
		new AudioFormat(MPEG1L2, -1.0F, -1, 1, -1, -1.0F, false),
		new AudioFormat(MPEG1L2, -1.0F, -1, 1, -1, -1.0F, true),
		// stereo
		new AudioFormat(MPEG1L2, -1.0F, -1, 2, -1, -1.0F, false),
		new AudioFormat(MPEG1L2, -1.0F, -1, 2, -1, -1.0F, true),

		// mono
		new AudioFormat(MPEG1L3, -1.0F, -1, 1, -1, -1.0F, false),
		new AudioFormat(MPEG1L3, -1.0F, -1, 1, -1, -1.0F, true),
		// stereo
		new AudioFormat(MPEG1L3, -1.0F, -1, 2, -1, -1.0F, false),
		new AudioFormat(MPEG1L3, -1.0F, -1, 2, -1, -1.0F, true),

		// mono
		new AudioFormat(MP3, -1.0F, -1, 1, -1, -1.0F, false),
		new AudioFormat(MP3, -1.0F, -1, 1, -1, -1.0F, true),
		// stereo
		new AudioFormat(MP3, -1.0F, -1, 2, -1, -1.0F, false),
		new AudioFormat(MP3, -1.0F, -1, 2, -1, -1.0F, true),

		// mono
		new AudioFormat(MPEG2L1, -1.0F, -1, 1, -1, -1.0F, false),
		new AudioFormat(MPEG2L1, -1.0F, -1, 1, -1, -1.0F, true),
		// stereo
		new AudioFormat(MPEG2L1, -1.0F, -1, 2, -1, -1.0F, false),
		new AudioFormat(MPEG2L1, -1.0F, -1, 2, -1, -1.0F, true),

		// mono
		new AudioFormat(MPEG2L2, -1.0F, -1, 1, -1, -1.0F, false),
		new AudioFormat(MPEG2L2, -1.0F, -1, 1, -1, -1.0F, true),
		// stereo
		new AudioFormat(MPEG2L2, -1.0F, -1, 2, -1, -1.0F, false),
		new AudioFormat(MPEG2L2, -1.0F, -1, 2, -1, -1.0F, true),

		// mono
		new AudioFormat(MPEG2L3, -1.0F, -1, 1, -1, -1.0F, false),
		new AudioFormat(MPEG2L3, -1.0F, -1, 1, -1, -1.0F, true),
		// stereo
		new AudioFormat(MPEG2L3, -1.0F, -1, 2, -1, -1.0F, false),
		new AudioFormat(MPEG2L3, -1.0F, -1, 2, -1, -1.0F, true),

		// mono
		new AudioFormat(MPEG2DOT5L1, -1.0F, -1, 1, -1, -1.0F, false),
		new AudioFormat(MPEG2DOT5L1, -1.0F, -1, 1, -1, -1.0F, true),
		// stereo
		new AudioFormat(MPEG2DOT5L1, -1.0F, -1, 2, -1, -1.0F, false),
		new AudioFormat(MPEG2DOT5L1, -1.0F, -1, 2, -1, -1.0F, true),

		// mono
		new AudioFormat(MPEG2DOT5L2, -1.0F, -1, 1, -1, -1.0F, false),
		new AudioFormat(MPEG2DOT5L2, -1.0F, -1, 1, -1, -1.0F, true),
		// stereo
		new AudioFormat(MPEG2DOT5L2, -1.0F, -1, 2, -1, -1.0F, false),
		new AudioFormat(MPEG2DOT5L2, -1.0F, -1, 2, -1, -1.0F, true),

		// mono
		new AudioFormat(MPEG2DOT5L3, -1.0F, -1, 1, -1, -1.0F, false),
		new AudioFormat(MPEG2DOT5L3, -1.0F, -1, 1, -1, -1.0F, true),
		// stereo
		new AudioFormat(MPEG2DOT5L3, -1.0F, -1, 2, -1, -1.0F, false),
		new AudioFormat(MPEG2DOT5L3, -1.0F, -1, 2, -1, -1.0F, true),
	};


	private static final AudioFormat[]	OUTPUT_FORMATS =
	{
		// mono, 16 bit signed
		new AudioFormat(PCM_SIGNED, -1.0F, 16, 1, 2, -1.0F, false),
		new AudioFormat(PCM_SIGNED, -1.0F, 16, 1, 2, -1.0F, true),

		// stereo, 16 bit signed
		new AudioFormat(PCM_SIGNED, -1.0F, 16, 2, 4, -1.0F, false),
		new AudioFormat(PCM_SIGNED, -1.0F, 16, 2, 4, -1.0F, true),

/*	24 and 32 bit not yet possible
		// mono, 24 bit signed
		new AudioFormat(PCM_SIGNED, -1.0F, 24, 1, 3, -1.0F, false),
		new AudioFormat(PCM_SIGNED, -1.0F, 24, 1, 3, -1.0F, true),

		// stereo, 24 bit signed
		new AudioFormat(PCM_SIGNED, -1.0F, 24, 2, 6, -1.0F, false),
		new AudioFormat(PCM_SIGNED, -1.0F, 24, 2, 6, -1.0F, true),

		// mono, 32 bit signed
		new AudioFormat(PCM_SIGNED, -1.0F, 32, 1, 4, -1.0F, false),
		new AudioFormat(PCM_SIGNED, -1.0F, 32, 1, 4, -1.0F, true),

		// stereo, 32 bit signed
		new AudioFormat(PCM_SIGNED, -1.0F, 32, 2, 8, -1.0F, false),
		new AudioFormat(PCM_SIGNED, -1.0F, 32, 2, 8, -1.0F, true),
*/
	};

	/**	
	 * Constructor.
	 */
	public MpegFormatConversionProvider()
	{
		super(Arrays.asList(INPUT_FORMATS), Arrays.asList(OUTPUT_FORMATS));
		if (TDebug.TraceAudioConverter) 
		{
			TDebug.out("MpegFormatConversionProvider()");
		}
	}

	@Override
	public AudioInputStream getAudioInputStream(AudioFormat targetFormat, AudioInputStream audioInputStream)
	{
		AudioFormat sourceFormat = audioInputStream.getFormat(); 
		
		if (TDebug.TraceAudioConverter)
		{
			TDebug.out(">MpegFormatConversionProvider.getAudioInputStream(AudioFormat, AudioInputStream):");
			TDebug.out("trying to convert");
			TDebug.out("\tfrom: " + sourceFormat);
			TDebug.out("\tto: " + targetFormat);
		}

		targetFormat = getFullyQualifiedTargetFormat(targetFormat, sourceFormat, false);
		if (targetFormat != null)
		{
			if (TDebug.TraceAudioConverter)
			{
				TDebug.out("< OK");
			}
			return new DecodedMpegAudioInputStream(
					targetFormat,
					audioInputStream);
		}
		if (TDebug.TraceAudioConverter)
		{
			TDebug.out("< not supported");
		}
		throw new IllegalArgumentException("conversion not supported");
	}
	
	private AudioFormat getFullyQualifiedTargetFormat(AudioFormat targetFormat, AudioFormat sourceFormat, boolean allowUnspecified) {
		// check that sourceFormat and targetFormat are in list of supported formats
		if (!super.isConversionSupported(targetFormat.getEncoding(), sourceFormat)) {
			if (TDebug.TraceAudioConverter) TDebug.out("cannot convert: super.isConversionSupported()==false");
			return null;
		}
		
		// make it simple: we can only convert to PCM_SIGNED,
		// therefore, just fill in the missing fields
		if (!targetFormat.getEncoding().equals(PCM_SIGNED)) {
			if (TDebug.TraceAudioConverter) TDebug.out("cannot convert: target is not PCM_SIGNED");
			return null;
		}
		
		// some values are never allowed
		if (sourceFormat.getChannels() > 2 
				|| targetFormat.getChannels() > 2
				|| sourceFormat.getChannels() == 0 
				|| targetFormat.getChannels() == 0
				|| sourceFormat.getSampleRate() == 0
				|| targetFormat.getSampleRate() == 0) {
			if (TDebug.TraceAudioConverter) TDebug.out("cannot convert: channels or sample rate out of bounds");
			return null;
		}
		

		// check channels
		if (sourceFormat.getChannels() < 0) {
			if (allowUnspecified) {
				// both channel fields must be -1
				if (targetFormat.getChannels() >= 0) {
					// cannot convert a non-specified channel number to a different specified channel
					if (TDebug.TraceAudioConverter) TDebug.out("cannot convert: cannot any to specific channels");
					return null;
				}
			} else {
				// do not allow source channels = -1
				if (TDebug.TraceAudioConverter) TDebug.out("cannot convert: channels cannot be AudioSystem.NOT_SPECIFIED");
				return null;
			}
		} else {
			// if target channels are given, they must equal source channels
			if (targetFormat.getChannels() > 0 && targetFormat.getChannels() != sourceFormat.getChannels()) {
				// cannot convert a specified channel number to a different specified channel
				if (TDebug.TraceAudioConverter) TDebug.out("cannot convert: specified channel number must be the same");
				return null;
			}
		}
		
		// check sample rate
		if (sourceFormat.getSampleRate() < 0) {
			if (allowUnspecified) {
				// both SampleRate fields must be -1
				if (targetFormat.getSampleRate() >= 0) {
					// cannot convert a non-specified SampleRate to a different specified SampleRate
					if (TDebug.TraceAudioConverter) TDebug.out("cannot convert any to specific sample rate");
					return null;
				}
			} else {
				// do not allow SampleRate = -1
				if (TDebug.TraceAudioConverter) TDebug.out("cannot convert: source sample rate is NOT_SPECIFIED");
				return null;
			}
		} else {
			// if target SampleRate is given, must equal source SampleRate
			if (targetFormat.getSampleRate() > 0 && targetFormat.getSampleRate() != sourceFormat.getSampleRate()) {
				// cannot convert a specified SampleRate to a different specified SampleRate
				if (TDebug.TraceAudioConverter) TDebug.out("cannot convert sample rate");
				return null;
			}
		}
		
		// check sample size
		if (targetFormat.getSampleSizeInBits() != 16) {
			if (TDebug.TraceAudioConverter) TDebug.out("cannot convert: source sample width is not 16");
			return null;
		}
		
		return new AudioFormat(
				PCM_SIGNED,
				sourceFormat.getSampleRate(),
				targetFormat.getSampleSizeInBits(),
				sourceFormat.getChannels(),
				AudioUtils.getFrameSize(sourceFormat.getChannels(), targetFormat.getSampleSizeInBits()), 
				sourceFormat.getSampleRate(),
				targetFormat.isBigEndian(), 
				targetFormat.properties());
	}

	@Override
	public boolean isConversionSupported(AudioFormat targetFormat, AudioFormat sourceFormat)
	{
		if (TDebug.TraceAudioConverter) 
		{
			TDebug.out(">MpegFormatConversionProvider.isConversionSupported(AudioFormat targetFormat, AudioFormat sourceFormat):");
			TDebug.out("checking if conversion possible");
			TDebug.out("from: " + sourceFormat);
			TDebug.out("to: " + targetFormat);
		}
		AudioFormat format = getFullyQualifiedTargetFormat(targetFormat, sourceFormat, true);
		boolean supported = (format != null);
		if (TDebug.TraceAudioConverter) 
		{
			TDebug.out("<MpegFormatConversionProvider.isConversionSupported(AudioFormat targetFormat, AudioFormat sourceFormat), result=" + supported);
		}
		return supported; 
	}

	public static class DecodedMpegAudioInputStream
	extends TAsynchronousFilteredAudioInputStream
	{
		private InputStream		m_encodedStream;
		private Bitstream		m_bitstream;
		private Decoder			m_decoder;
		private DMAISObuffer		m_oBuffer;



		public DecodedMpegAudioInputStream(AudioFormat outputFormat, AudioInputStream inputStream)
		{
			// TODO: try to find out length (possible?)
			super(outputFormat, AudioSystem.NOT_SPECIFIED);
			m_encodedStream = inputStream;
			m_bitstream = new Bitstream(inputStream);
			m_decoder = new Decoder(null);
			m_oBuffer = new DMAISObuffer(outputFormat.getChannels());
			m_decoder.setOutputBuffer(m_oBuffer);
		}



		public void execute()
		{
			try
			{
				Header	header = m_bitstream.readFrame();
				if (header == null)
				{
					if (TDebug.TraceAudioConverter)
					{
						TDebug.out("header is null (end of mpeg stream)");
					}
					getCircularBuffer().close();
					return;
				}
				m_decoder.decodeFrame(header, m_bitstream);
				m_bitstream.closeFrame();
				getCircularBuffer().write(m_oBuffer.getBuffer(), 0, m_oBuffer.getCurrentBufferSize());
				m_oBuffer.reset();
			}
			catch (BitstreamException e)
			{
				if (TDebug.TraceAudioConverter || TDebug.TraceAllExceptions)
				{
					TDebug.out(e);
				}
			}
			catch (DecoderException e)
			{
				if (TDebug.TraceAudioConverter || TDebug.TraceAllExceptions)
				{
					TDebug.out(e);
				}
			}
		}



		protected boolean isBigEndian()
		{
			return getFormat().isBigEndian();
		}



		@Override
		public void close()
			throws IOException
		{
			super.close();
			m_encodedStream.close();
		}



		private class DMAISObuffer
		extends Obuffer
		{
			private int			m_nChannels;
			private byte[]			m_abBuffer;
			private int[]			m_anBufferPointers;
			private boolean			m_bIsBigEndian;



			public DMAISObuffer(int	nChannels)
			{
				m_nChannels = nChannels;
				m_abBuffer = new byte[OBUFFERSIZE * nChannels];
				m_anBufferPointers = new int[nChannels];
				reset();
				m_bIsBigEndian = DecodedMpegAudioInputStream.this.isBigEndian();
			}



			@Override
			public void append(int nChannel, short sValue)
			{
				// TODO: replace by TConversionTool methods
/*
				byte	bFirstByte;
				byte	bSecondByte;
				if (m_bIsBigEndian)
				{
					bFirstByte = (byte) ((sValue >>> 8) & 0xFF);
					bSecondByte = (byte) (sValue & 0xFF);
				}
				else	// little endian
				{
					bFirstByte = (byte) (sValue & 0xFF);
					bSecondByte = (byte) ((sValue >>> 8) & 0xFF);
				}
				m_abBuffer[m_anBufferPointers[nChannel]] = bFirstByte;
				m_abBuffer[m_anBufferPointers[nChannel] + 1] = bSecondByte;
*/
				TConversionTool.shortToBytes16(sValue, m_abBuffer, m_anBufferPointers[nChannel], m_bIsBigEndian);
				m_anBufferPointers[nChannel] += m_nChannels * 2;
			}


			@Override
			public void set_stop_flag()
			{
			}



			@Override
			public void close()
			{
			}



			@Override
			public void write_buffer(int nValue)
			{
			}



			@Override
			public void clear_buffer()
			{
			}



			public byte[] getBuffer()
			{
				return m_abBuffer;
			}



			public int getCurrentBufferSize()
			{
				return m_anBufferPointers[0];
			}



			public void reset()
			{
				for (int i = 0; i < m_nChannels; i++)
				{
					/*	Points to byte location,
					 *	implicitely assuming 16 bit
					 *	samples.
					 */
					m_anBufferPointers[i] = i * 2;
				}
			}


			
		}
	}

	private static int test(AudioFormat target, AudioFormat source, boolean failSupported, boolean failAIS, int testNum) {
		boolean verbose = false;
		AudioInputStream ais = new AudioInputStream(new ByteArrayInputStream(new byte[8]), source, 8);
		MpegFormatConversionProvider provider = new MpegFormatConversionProvider();
		boolean isConversionSupported = provider.isConversionSupported(target, source);
		AudioInputStream convertedAIS = null; 
		try {
			convertedAIS = provider.getAudioInputStream(target, ais);
		} catch (Exception e) {
			// ignore
		}
		boolean failed = (failSupported == isConversionSupported)
						|| (failAIS != (convertedAIS == null)); 
		if (failed || verbose) {
			if (failed) {
				System.out.println(""+(testNum)+".ERROR:");
			} else {
				System.out.println(""+(testNum)+".PASSED:");
			}
			System.out.println("    source: "+source);
			System.out.println("    target: "+target);
			if (failSupported == isConversionSupported) {
				System.out.println("  isConversionSupported() erronously returned "+isConversionSupported);
			} else {
				System.out.println("  isConversionSupported() correctly returned "+isConversionSupported);
			}
			if (convertedAIS != null) {
				if (failAIS) {
					System.out.println("  converted stream was erronously returned with format:");
				} else {
					System.out.println("  converted stream was correctly returned with format:");
				}
				System.out.println("  converted format: "+convertedAIS.getFormat());
			} else {
				if (failAIS) {
					System.out.println("  converted stream was correctly not returned.");
				} else {
					System.out.println("  converted stream was erronously not returned.");
				}
			}
		} else if (!failed) {
			System.out.println(""+(testNum)+".OK");
		}
		return failed?0:1;
	}
	
	
	/** unit test */
	public static void main(String[] args) {
		int testNum = 0;
		int passed = 0;
		
		// negative tests: should not be able to convert mp3 to mp3
		AudioFormat source = new AudioFormat(MPEG1L3, 44100, -1, 2, -1, -1, false);
		AudioFormat target = new AudioFormat(MPEG1L3, 44100, 16, 2, 4, 44100, false);
		passed += test(target, source, true, true, testNum++);
		source = new AudioFormat(MPEG1L3, 44100, -1, 2, -1, -1, false);
		target = new AudioFormat(MPEG1L3, -1, 16, -1, -1, -1, false);
		passed += test(target, source, true, true, testNum++);
		source = new AudioFormat(MPEG1L3, 44100, -1, 2, -1, -1, false);
		target = new AudioFormat(MPEG1L3, -1, 32, 2, 8, -1, false);
		passed += test(target, source, true, true, testNum++);
		
		// negative test: should not claim to convert channels
		source = new AudioFormat(MPEG1L3, 44100, -1, 1, -1, -1, false);
		target = new AudioFormat(PCM_SIGNED, -1, 16, 2, 4, -1, false);
		passed += test(target, source, true, true, testNum++);
		source = new AudioFormat(MPEG1L3, 44100, -1, 2, -1, -1, false);
		target = new AudioFormat(PCM_SIGNED, -1, 16, 1, 2, -1, false);
		passed += test(target, source, true, true, testNum++);

		// negative test: should not claim to convert sample rate
		source = new AudioFormat(MPEG1L3, 44100, -1, 2, -1, -1, false);
		target = new AudioFormat(PCM_SIGNED, 8000, 16, 2, 4, 8000, false);
		passed += test(target, source, true, true, testNum++);

		// positive test: should convert MP3 to PCM
		source = new AudioFormat(MPEG1L3, 44100, -1, 2, -1, -1, false);
		target = new AudioFormat(PCM_SIGNED, 44100, 16, 2, 4, 44100, false);
		passed += test(target, source, false, false, testNum++);

		// positive test: should convert MP3 to PCM
		source = new AudioFormat(MPEG1L3, 44100, -1, 1, -1, -1, false);
		target = new AudioFormat(PCM_SIGNED, 44100, 16, 1, 2, 44100, false);
		passed += test(target, source, false, false, testNum++);
		
		// special case: can check isSupported with -1 for both fields, but should not return an AIS
		source = new AudioFormat(MPEG1L3, -1, -1, 1, -1, -1, false);
		target = new AudioFormat(PCM_SIGNED, -1, 16, 1, 2, -1, false);
		passed += test(target, source, false, true, testNum++);
		source = new AudioFormat(MPEG1L3, 8000, -1, -1, -1, -1, false);
		target = new AudioFormat(PCM_SIGNED, 8000, 16, -1, -1, 8000, false);
		passed += test(target, source, false, true, testNum++);

		System.out.println("Passed "+passed+" tests of "+testNum);
	}
	
}



/*** MpegFormatConversionProvider.java ***/
