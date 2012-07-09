/*
 *	GSMFormatConversionProvider.java
 *
 *	This file is part of Tritonus: http://www.tritonus.org/
 */

/*
 *  Copyright (c) 1999 - 2001 by Matthias Pfisterer
 *  Copyright (c) 2001 by Florian Bomers <http://www.bomers.de>
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

package org.tritonus.sampled.convert.gsm;

import java.io.DataInputStream;
import java.io.IOException;

import java.util.Arrays;
import java.util.Iterator;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import org.tritonus.share.TDebug;
import org.tritonus.lowlevel.gsm.InvalidGSMFrameException;
import org.tritonus.lowlevel.gsm.GSMDecoder;
import org.tritonus.lowlevel.gsm.Encoder;
import org.tritonus.share.sampled.TConversionTool;
import org.tritonus.share.sampled.convert.TAsynchronousFilteredAudioInputStream;
import org.tritonus.share.sampled.convert.TSimpleFormatConversionProvider;
import org.tritonus.share.sampled.AudioFormats;



/**	ConversionProvider for GSM files.
	@author Matthias Pfisterer
*/
public class GSMFormatConversionProvider
extends TSimpleFormatConversionProvider
// extends TEncodingFormatConversionProvider
{
	/*	Debugging (profiling) hack.
	 */
	private static final boolean	MEASURE_DECODING_TIME = false;



	private static final AudioFormat[]	FORMATS1 =
	{
		new AudioFormat(new AudioFormat.Encoding("GSM0610"), 8000.0F, -1, 1, 33, 50.0F, false),
		new AudioFormat(new AudioFormat.Encoding("GSM0610"), 8000.0F, -1, 1, 33, 50.0F, true),
		// temporary only
		new AudioFormat(new AudioFormat.Encoding("PCM_SIGNED"), 8000.0F, 16, 1, 2, 8000.0F, false),
		new AudioFormat(new AudioFormat.Encoding("PCM_SIGNED"), 8000.0F, 16, 1, 2, 8000.0F, true),
	};

/*
  private static final AudioFormat[]	FORMATS2 =
  {
  new AudioFormat(8000.0F, 16, 1, true, false),
  new AudioFormat(8000.0F, 16, 1, true, true),
  };
*/
	private static final AudioFormat[]	FORMATS2 = FORMATS1;



	/**	This is the size of the circular buffer.
	 *	This value is in bytes. It is
	 *	chosen so that one (decoded) GSM frame fits into the buffer.
	 *	GSM frames contain 160 samples.
	 */
	private static final int	BUFFER_SIZE = 320;

	private static final int	ENCODED_GSM_FRAME_SIZE = 33;



	/**	Constructor.
	 */
	public GSMFormatConversionProvider()
	{
		super(Arrays.asList(FORMATS1),
		      Arrays.asList(FORMATS2));
		if (TDebug.TraceAudioConverter) { TDebug.out("GSMFormatConversionProvider.<init>(): begin"); }
		if (TDebug.TraceAudioConverter) { TDebug.out("GSMFormatConversionProvider.<init>(): end"); }
	}



	public AudioInputStream getAudioInputStream(AudioFormat targetFormat, AudioInputStream audioInputStream)
	{
		if (TDebug.TraceAudioConverter)
		{
			TDebug.out("GSMFormatConversionProvider.getAudioInputStream(): begin");
			TDebug.out("GSMFormatConversionProvider.getAudioInputStream():");
			TDebug.out("checking if conversion supported");
			TDebug.out("from: " + audioInputStream.getFormat());
			TDebug.out("to: " + targetFormat);
		}

		targetFormat=getDefaultTargetFormat(targetFormat, audioInputStream.getFormat());
		if (isConversionSupported(targetFormat,
					  audioInputStream.getFormat()))
		{
			if (targetFormat.getEncoding().equals(new AudioFormat.Encoding("PCM_SIGNED")))
			{
				if (TDebug.TraceAudioConverter)
				{
					TDebug.out("GSMFormatConversionProvider.getAudioInputStream():");
					TDebug.out("conversion supported; trying to create DecodedGSMAudioInputStream");
				}
				return new DecodedGSMAudioInputStream(
					targetFormat,
					audioInputStream);
			}
			else
			{
				if (TDebug.TraceAudioConverter)
				{
					TDebug.out("GSMFormatConversionProvider.getAudioInputStream():");
					TDebug.out("conversion supported; trying to create EncodedGSMAudioInputStream");
				}
				return new EncodedGSMAudioInputStream(
					targetFormat,
					audioInputStream);
			}
		}
		else
		{
			if (TDebug.TraceAudioConverter)
			{
				TDebug.out("GSMFormatConversionProvider.getAudioInputStream():");
				TDebug.out("conversion not supported; throwing IllegalArgumentException");
			}
			throw new IllegalArgumentException("conversion not supported");
		}
		// TODO: this is unreachable
		// if (TDebug.TraceAudioConverter) { TDebug.out("GSMFormatConversionProvider.getAudioInputStream(): end"); }
	}



	protected AudioFormat getDefaultTargetFormat(AudioFormat targetFormat, AudioFormat sourceFormat) {
		// return first of the matching formats
		// pre-condition: the predefined target formats (FORMATS2) must be well-defined !
		Iterator iterator=getCollectionTargetFormats().iterator();
		while (iterator.hasNext()) {
			AudioFormat format=(AudioFormat) iterator.next();
			if (AudioFormats.matches(targetFormat, format)) {
				return format;
			}
		}
		throw new IllegalArgumentException("conversion not supported");
	}



	/**	AudioInputStream returned on decoding of GSM.
		An instance of this class is returned if you call
		AudioSystem.getAudioInputStream(AudioFormat, AudioInputStream)
		to decode a GSM stream. This class contains the logic
		of maintaining buffers and calling the decoder.
	 */
	public static class DecodedGSMAudioInputStream
	extends TAsynchronousFilteredAudioInputStream
	{
		/*
		  Seems like DataInputStream (opposite to InputStream) is only needed for
		  readFully(). readFully-behavious should perhaps be implemented in
		  AudioInputStream anyway (so this construct may become obsolete).
		 */
		private DataInputStream		m_encodedStream;
		private GSMDecoder		m_decoder;

		/*
		 *	Holds one encoded GSM frame.
		 */
		private byte[]			m_abFrameBuffer;
		private byte[]			m_abBuffer;


		public DecodedGSMAudioInputStream(AudioFormat outputFormat, AudioInputStream inputStream)
		{
			super(outputFormat,
			      inputStream.getFrameLength() == AudioSystem.NOT_SPECIFIED ? AudioSystem.NOT_SPECIFIED : inputStream.getFrameLength() * 160);
			if (TDebug.TraceAudioConverter) { TDebug.out("DecodedGSMAudioInputStream.<init>(): begin"); }
			m_encodedStream = new DataInputStream(inputStream);
			m_decoder = new GSMDecoder();
			m_abFrameBuffer = new byte[ENCODED_GSM_FRAME_SIZE];
			m_abBuffer = new byte[BUFFER_SIZE];
			if (TDebug.TraceAudioConverter) { TDebug.out("DecodedGSMAudioInputStream.<init>(): end"); }
		}



		public void execute()
		{
			if (TDebug.TraceAudioConverter) { TDebug.out("DecodedGSMAudioInputStream.execute(): begin"); }
			try
			{
				m_encodedStream.readFully(m_abFrameBuffer);
			}
			catch (IOException e)
			{
				/*
				  Not only errors, but also EOF is caught here.
				 */
				if (TDebug.TraceAllExceptions) { TDebug.out(e); }
				getCircularBuffer().close();
				return;
			}

			try
			{
				long	lTimestamp1;
				long	lTimestamp2;
				if (MEASURE_DECODING_TIME)
				{
					lTimestamp1 = System.currentTimeMillis();
				}
				m_decoder.decode(m_abFrameBuffer, 0,
						 m_abBuffer, 0, isBigEndian());
				// testing test hack
				// m_abBuffer[0] = 0;
				if (MEASURE_DECODING_TIME)
				{
					lTimestamp2 = System.currentTimeMillis();
					System.out.println("GSM decode (ms): " + (lTimestamp2 - lTimestamp1));
				}
			}
			catch (InvalidGSMFrameException e)
			{
				if (TDebug.TraceAllExceptions) { TDebug.out(e); }
				getCircularBuffer().close();
				return;
			}

/// end new version

			getCircularBuffer().write(m_abBuffer);
			if (TDebug.TraceAudioConverter) { TDebug.out("DecodedGSMAudioInputStream.execute(): decoded GSM frame written"); }
			if (TDebug.TraceAudioConverter) { TDebug.out("DecodedGSMAudioInputStream.execute(): end"); }
		}



		private boolean isBigEndian()
		{
			return getFormat().isBigEndian();
		}



		public void close()
			throws IOException
		{
			super.close();
			m_encodedStream.close();
		}
	}



	/**	AudioInputStream returned on encoding of GSM.
		An instance of this class is returned if you call
		AudioSystem.getAudioInputStream(AudioFormat, AudioInputStream)
		to encode data to GSM. This class contains the logic
		of maintaining buffers and calling the encoder.
	 */
	public static class EncodedGSMAudioInputStream
	extends TAsynchronousFilteredAudioInputStream
	{
		private AudioInputStream	m_decodedStream;
		private Encoder			m_encoder;

		/*
		 *	Holds one block of decoded data.
		 */
		private byte[]			m_abBuffer;

		/*
		 *	Holds one block of decoded data.
		 */
		private short[]			m_asBuffer;

		/*
		 *	Holds one encoded GSM frame.
		 */
		private byte[]			m_abFrameBuffer;



		public EncodedGSMAudioInputStream(AudioFormat outputFormat, AudioInputStream inputStream)
		{
			super(outputFormat,
			      //$$fb 2001-04-16: FrameLength gives the number of 33-byte blocks !
			      //inputStream.getFrameLength() == AudioSystem.NOT_SPECIFIED ? AudioSystem.NOT_SPECIFIED : inputStream.getFrameLength() / 160 * 33);
			      inputStream.getFrameLength() == AudioSystem.NOT_SPECIFIED ? AudioSystem.NOT_SPECIFIED : inputStream.getFrameLength() / 160);
			if (TDebug.TraceAudioConverter) { TDebug.out("EncodedGSMAudioInputStream.<init>(): begin"); }
			m_decodedStream = inputStream;
			m_encoder = new Encoder();
			m_abBuffer = new byte[BUFFER_SIZE];
			m_asBuffer = new short[160];
			m_abFrameBuffer = new byte[ENCODED_GSM_FRAME_SIZE];
			if (TDebug.TraceAudioConverter) { TDebug.out("EncodedGSMAudioInputStream.<init>(): end"); }
		}



		public void execute()
		{
			if (TDebug.TraceAudioConverter) { TDebug.out(">EncodedGSMAudioInputStream.execute(): begin"); }
			try
			{
				int	nRead = m_decodedStream.read(m_abBuffer);
				/*
				 *	Currently, we take all kinds of errors
				 *	as end of stream.
				 */
				if (nRead != m_abBuffer.length)
				{
					if (TDebug.TraceAudioConverter) { TDebug.out("<EncodedGSMAudioInputStream.execute(): not read whole 160 sample block (" + nRead + ")"); }
					getCircularBuffer().close();
					return;
				}
			}
			catch (IOException e)
			{
				if (TDebug.TraceAllExceptions) { TDebug.out(e); }
				getCircularBuffer().close();
				if (TDebug.TraceAudioConverter) { TDebug.out("<"); }
				return;
			}
			for (int i = 0; i < 160; i++)
			{
			        m_asBuffer[i] = TConversionTool.bytesToShort16(m_abBuffer, i * 2, isBigEndian());
			}
			m_encoder.encode(m_asBuffer, m_abFrameBuffer);
			getCircularBuffer().write(m_abFrameBuffer);
			if (TDebug.TraceAudioConverter) { TDebug.out("<EncodedGSMAudioInputStream.execute(): encoded GSM frame written"); }
			if (TDebug.TraceAudioConverter) { TDebug.out(">EncodedGSMAudioInputStream.execute(): end"); }
		}



		private boolean isBigEndian()
		{
			return m_decodedStream.getFormat().isBigEndian();
		}



		public void close()
			throws IOException
		{
			super.close();
			m_decodedStream.close();
		}
	}
}



/*** GSMFormatConversionProvider.java ***/
