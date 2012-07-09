/*
 *	VorbisFormatConversionProvider.java
 *
 *	This file is part of Tritonus: http://www.tritonus.org/
 */

/*
 *  Copyright (c) 1999 - 2004 by Matthias Pfisterer
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

package org.tritonus.sampled.convert.pvorbis;

import java.io.EOFException;
import java.io.InputStream;
import java.io.IOException;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import org.tritonus.lowlevel.pogg.Packet;
import org.tritonus.lowlevel.pogg.Page;
import org.tritonus.lowlevel.pogg.StreamState;
import org.tritonus.lowlevel.pogg.SyncState;
import org.tritonus.lowlevel.pvorbis.Block;
import org.tritonus.lowlevel.pvorbis.Comment;
import org.tritonus.lowlevel.pvorbis.DspState;
import org.tritonus.lowlevel.pvorbis.Info;

import org.tritonus.share.TDebug;
import org.tritonus.share.sampled.AudioFormats;
import org.tritonus.share.sampled.convert.TAsynchronousFilteredAudioInputStream;
import org.tritonus.share.sampled.convert.TEncodingFormatConversionProvider;



/**	ConversionProvider for ogg vorbis encoding.
	This FormatConversionProvider uses the native libraries libogg,
	libvorbis and libvorbisenc to implement encoding to ogg vorbis.

	@author Matthias Pfisterer
*/
public class VorbisFormatConversionProvider
extends TEncodingFormatConversionProvider
{
	// only used as abbreviation
	private static final AudioFormat.Encoding	VORBIS = new AudioFormat.Encoding("VORBIS");
	private static final AudioFormat.Encoding	PCM_SIGNED = new AudioFormat.Encoding("PCM_SIGNED");


	private static final AudioFormat[]	INPUT_FORMATS =
	{
		// mono, 16 bit signed
		new AudioFormat(PCM_SIGNED, -1.0F, 16, 1, 2, -1.0F, false),
		new AudioFormat(PCM_SIGNED, -1.0F, 16, 1, 2, -1.0F, true),
		// stereo, 16 bit signed
		new AudioFormat(PCM_SIGNED, -1.0F, 16, 2, 4, -1.0F, false),
		new AudioFormat(PCM_SIGNED, -1.0F, 16, 2, 4, -1.0F, true),
		// TODO: other channel configurations

		// mono
		// TODO: mechanism to make the double specification with
		// different endianess obsolete.
		new AudioFormat(VORBIS, -1.0F, -1, 1, -1, -1.0F, false),
		new AudioFormat(VORBIS, -1.0F, -1, 1, -1, -1.0F, true),
		// stereo
		new AudioFormat(VORBIS, -1.0F, -1, 2, -1, -1.0F, false),
		new AudioFormat(VORBIS, -1.0F, -1, 2, -1, -1.0F, true),
		// TODO: other channel configurations
	};


// 	private static final AudioFormat[]	OUTPUT_FORMATS =
// 	{
// 		// mono
// 		// TODO: mechanism to make the double specification with
// 		// different endianess obsolete.
// 		new AudioFormat(VORBIS, -1.0F, -1, 1, -1, -1.0F, false),
// 		new AudioFormat(VORBIS, -1.0F, -1, 1, -1, -1.0F, true),
// 		// stereo
// 		new AudioFormat(VORBIS, -1.0F, -1, 2, -1, -1.0F, false),
// 		new AudioFormat(VORBIS, -1.0F, -1, 2, -1, -1.0F, true),
// 		// TODO: other channel configurations
// 	};


	/* Default settings for encoding. */
	private static final boolean DEFAULT_VBR = true;
	private static final float DEFAULT_QUALITY = 0.5F;
	private static final int DEFAULT_MAX_BITRATE = 256;
	private static final int DEFAULT_NOM_BITRATE = 128;
	private static final int DEFAULT_MIN_BITRATE = 32;



	/**	Constructor.
	 */
	public VorbisFormatConversionProvider()
	{
		super(Arrays.asList(INPUT_FORMATS),
		      Arrays.asList(INPUT_FORMATS)//,
		      //Arrays.asList(OUTPUT_FORMATS),
		      /*
						     true, // new behaviour
						     false*/); // bidirectional .. constants UNIDIR../BIDIR..?
		if (TDebug.TraceAudioConverter) { TDebug.out("VorbisFormatConversionProvider.<init>(): begin"); }
		if (TDebug.TraceAudioConverter) { TDebug.out("VorbisFormatConversionProvider.<init>(): end"); }
	}



	@Override
	public AudioInputStream getAudioInputStream(AudioFormat targetFormat, AudioInputStream audioInputStream)
	{
		if (TDebug.TraceAudioConverter) { TDebug.out(">VorbisFormatConversionProvider.getAudioInputStream(): begin"); }
		/** The AudioInputStream to return.
		 */
		AudioInputStream	convertedAudioInputStream = null;

		if (TDebug.TraceAudioConverter)
		{
			TDebug.out("checking if conversion supported");
			TDebug.out("from: " + audioInputStream.getFormat());
			TDebug.out("to: " + targetFormat);
		}

		// what is this ???
		targetFormat = getDefaultTargetFormat(targetFormat, audioInputStream.getFormat());
		if (isConversionSupported(targetFormat,
					  audioInputStream.getFormat()))
		{
			if (targetFormat.getEncoding().equals(VORBIS))
			{
				if (TDebug.TraceAudioConverter) TDebug.out("conversion supported; trying to create EncodedVorbisAudioInputStream");
				convertedAudioInputStream = new
					EncodedVorbisAudioInputStream(
						targetFormat,
					audioInputStream);
			}
			else
			{
				if (TDebug.TraceAudioConverter) { TDebug.out("conversion supported; trying to create DecodedVorbisAudioInputStream"); }
				convertedAudioInputStream = new
					DecodedVorbisAudioInputStream(
						targetFormat,
						audioInputStream);
			}
		}
		else
		{
			if (TDebug.TraceAudioConverter) { TDebug.out("<conversion not supported; throwing IllegalArgumentException"); }
			throw new IllegalArgumentException("conversion not supported");
		}
		if (TDebug.TraceAudioConverter) { TDebug.out("<VorbisFormatConversionProvider.getAudioInputStream(): end"); }
		return convertedAudioInputStream;
	}



	@SuppressWarnings("unchecked")
	protected AudioFormat getDefaultTargetFormat(AudioFormat targetFormat, AudioFormat sourceFormat)
	{
		if (TDebug.TraceAudioConverter) { TDebug.out("VorbisFormatConversionProvider.getDefaultTargetFormat(): target format: " + targetFormat); }
		if (TDebug.TraceAudioConverter) { TDebug.out("VorbisFormatConversionProvider.getDefaultTargetFormat(): source format: " + sourceFormat); }
		AudioFormat	newTargetFormat = null;
		// return first of the matching formats
		// pre-condition: the predefined target formats (FORMATS2) must be well-defined !
		Iterator iterator = getCollectionTargetFormats().iterator();
		while (iterator.hasNext())
		{
			AudioFormat format = (AudioFormat) iterator.next();
			if (AudioFormats.matches(targetFormat, format))
			{
				newTargetFormat = format;
			}
		}
		if (newTargetFormat == null)
		{
			throw new IllegalArgumentException("conversion not supported");
		}
		if (TDebug.TraceAudioConverter) { TDebug.out("VorbisFormatConversionProvider.getDefaultTargetFormat(): new target format: " + newTargetFormat); }
		// hacked together...
		// ... only works for PCM target encoding ...
		newTargetFormat = new AudioFormat(targetFormat.getEncoding(),
										  sourceFormat.getSampleRate(),
										  newTargetFormat.getSampleSizeInBits(),
										  newTargetFormat.getChannels(),
										  newTargetFormat.getFrameSize(),
										  sourceFormat.getSampleRate(),
										  newTargetFormat.isBigEndian(),
										  targetFormat.properties());
		if (TDebug.TraceAudioConverter) { TDebug.out("VorbisFormatConversionProvider.getDefaultTargetFormat(): really new target format: " + newTargetFormat); }
		return newTargetFormat;
	}



	/**	AudioInputStream returned on encoding to ogg vorbis.
		An instance of this class is returned if you call
		AudioSystem.getAudioInputStream(AudioFormat, AudioInputStream)
		to encode a PCM stream. This class contains the logic
		of maintaining buffers and calling the encoder.
	 */
	public static class EncodedVorbisAudioInputStream
	extends TAsynchronousFilteredAudioInputStream
	{
		/** How many PCM frames to encode at once.
		 */
		private static final int	READ = 1024;

		private AudioInputStream	m_decodedStream;
		private byte[]			m_abReadbuffer;

		private StreamState		m_streamState;
		private Page			m_page;
		private Packet			m_packet;

		private Info			m_info;
		private Comment			m_comment;
		private DspState		m_dspState;
		private Block			m_block;

		private boolean			eos = false;





		public EncodedVorbisAudioInputStream(
			AudioFormat outputFormat,
			AudioInputStream inputStream)
		{
			super(outputFormat,
			      AudioSystem.NOT_SPECIFIED,
			      262144, 16384);
			if (TDebug.TraceAudioConverter) { TDebug.out(">EncodedVorbisAudioInputStream.<init>(): begin"); }
			m_decodedStream = inputStream;
			m_abReadbuffer = new byte[READ * getFrameSize()];
			Object property = null;

			property = outputFormat.getProperty("vbr");
			boolean	bUseVBR = DEFAULT_VBR;
			if (property instanceof Boolean)
			{
				bUseVBR = ((Boolean) property).booleanValue();
			}

			property = outputFormat.getProperty("quality");
			float	fQuality = DEFAULT_QUALITY;
			if (property instanceof Integer)
			{
				fQuality = ((Integer) property).intValue() / 10.0F;
			}

			property = outputFormat.getProperty("bitrate");
			int	nNominalBitrate = DEFAULT_NOM_BITRATE;
			if (property instanceof Integer)
			{
				nNominalBitrate = ((Integer) property).intValue() / 1024;
			}

			property = outputFormat.getProperty("vorbis.min_bitrate");
			int	nMinBitrate = DEFAULT_MIN_BITRATE;
			if (property instanceof Integer)
			{
				nMinBitrate = ((Integer) property).intValue() / 1024;
			}

			property = outputFormat.getProperty("vorbis.max_bitrate");
			int	nMaxBitrate = DEFAULT_MAX_BITRATE;
			if (property instanceof Integer)
			{
				nMaxBitrate = ((Integer) property).intValue() / 1024;
			}

			m_streamState = new StreamState();
			m_page = new Page();
			m_packet = new Packet();

			m_info = new Info();
			m_comment = new Comment();
			m_dspState = new DspState();
			m_block = new Block();

			m_info.init();

			int	nSampleRate = (int) inputStream.getFormat().getSampleRate();
			if (TDebug.TraceAudioConverter) { TDebug.out("sample rate: " + nSampleRate); }
			if (TDebug.TraceAudioConverter) { TDebug.out("channels: " + getChannels()); }
			if (bUseVBR)
			{
				m_info.encodeInitVBR(getChannels(),
						 nSampleRate,
						 fQuality);
			}
			else
			{
				m_info.encodeInit(getChannels(),
					      nSampleRate,
					      nMaxBitrate,
					      nNominalBitrate,
					      nMinBitrate);
			}

			m_comment.init();
			m_comment.addTag("ENCODER","Tritonus libvorbis wrapper");
			property = outputFormat.getProperty("vorbis.comments");
			if (property instanceof List)
			{
				if (TDebug.TraceAudioConverter) { TDebug.out("VorbisFormatConversionProvider.<init>(): comments present in target format"); }
				List<?> comments = (List<?>) property;
				for (int i = 0; i < comments.size(); i++)
				{
					Object comm = comments.get(i);
					if (comm instanceof String)
					{
						m_comment.addComment((String) comm);
					}
				}
			}

			m_dspState.initAnalysis(m_info);
			m_block.init(m_dspState);

			Random random = new Random(System.currentTimeMillis());
			m_streamState.init(random.nextInt());

			Packet header = new Packet();
			Packet header_comm = new Packet();
			Packet header_code = new Packet();

			m_dspState.headerOut(m_comment, header, header_comm, header_code);
			m_streamState.packetIn(header);
			m_streamState.packetIn(header_comm);
			m_streamState.packetIn(header_code);

			while (true)
			{
				int result = m_streamState.flush(m_page);
				if(result == 0)
				{
					break;
				}
				getCircularBuffer().write(m_page.getHeader());
				getCircularBuffer().write(m_page.getBody());
			}

			if (TDebug.TraceAudioConverter) { TDebug.out("<EncodedVorbisAudioInputStream.<init>(): end"); }
		}



		public void execute()
		{
			if (TDebug.TraceAudioConverter) { TDebug.out(">EncodedVorbisAudioInputStream.execute(): begin"); }
			int	nFrameSize = getFrameSize();
			int	nChannels = getChannels();
			boolean	bBigEndian = isBigEndian();
			int	nBytesPerSample = nFrameSize / nChannels;
			int	nSampleSizeInBits = nBytesPerSample * 8;
			float	fScale = (float) Math.pow(2.0, nSampleSizeInBits - 1);
			if (TDebug.TraceAudioConverter)
			{
				TDebug.out("frame size: " + nFrameSize);
				TDebug.out("channels: " + nChannels);
				TDebug.out("big endian: " + bBigEndian);
				TDebug.out("sample size (bits): " + nSampleSizeInBits);
				TDebug.out("bytes per sample: " + nBytesPerSample);
				TDebug.out("scale: " + fScale);
			}

			while (!eos && writeMore())
			{
				if (TDebug.TraceAudioConverter) { TDebug.out("writeMore(): " + writeMore()); }
				int	bytes;
				try
				{
					bytes = m_decodedStream.read(m_abReadbuffer);
					if (TDebug.TraceAudioConverter) { TDebug.out("read from PCM stream: " + bytes); }
				}
				catch (IOException e)
				{
					if (TDebug.TraceAllExceptions || TDebug.TraceAudioConverter) { TDebug.out(e); }
					m_streamState.clear();
					m_block.clear();
					m_dspState.clear();
					m_comment.clear();
					m_info.clear();
					try
					{
						close();
					}
					catch (IOException e1)
					{
						if (TDebug.TraceAllExceptions || TDebug.TraceAudioConverter) { TDebug.out(e1); }
					}
					if (TDebug.TraceAudioConverter) { TDebug.out("<"); }
					return;
				}

				if (bytes == 0 ||  bytes == -1)
				{
					if (TDebug.TraceAudioConverter) { TDebug.out("EOS reached; calling DspState.write(0)"); }
					m_dspState.write(null, 0);
				}
				else
				{
					int	nFrames = bytes / nFrameSize;
					if (TDebug.TraceAudioConverter) { TDebug.out("processing frames: " + nFrames); }
					float[][] buffer = new float[nChannels][READ];
					/* uninterleave samples */
					for (int i = 0; i < nFrames; i++)
					{
						for (int nChannel = 0; nChannel < nChannels; nChannel++)
						{
							int	nSample;
							nSample = bytesToInt16(m_abReadbuffer, i * nFrameSize + nChannel * nBytesPerSample, bBigEndian);
							buffer[nChannel][i] = nSample / fScale;
						}
					}
					m_dspState.write(buffer, nFrames);
				}

				while (m_dspState.blockOut(m_block) == 1)
				{
					m_block.analysis(null);
					m_block.addBlock();
					while (m_dspState.flushPacket(m_packet) != 0)
					{
						m_streamState.packetIn(m_packet);
						while (!eos /*&& writeMore()*/)
						{
							int result = m_streamState.pageOut(m_page);
							if(result == 0)
							{
								break;
							}
							getCircularBuffer().write(m_page.getHeader());
							getCircularBuffer().write(m_page.getBody());

							if (m_page.isEos())
							{
								eos = true;
								if (TDebug.TraceAudioConverter) { TDebug.out("page has detected EOS"); }
							}
						}
					}
				}
			}

			if (eos)
			{
				if (TDebug.TraceAudioConverter) { TDebug.out("EOS; shutting down encoder"); }
				m_streamState.clear();
				m_block.clear();
				m_dspState.clear();
				m_comment.clear();
				m_info.clear();
				getCircularBuffer().close();
				try
				{
					close();
				}
				catch (IOException e)
				{
					if (TDebug.TraceAllExceptions || TDebug.TraceAudioConverter) { TDebug.out(e); }
				}
			}
			if (TDebug.TraceAudioConverter) { TDebug.out("<EncodedVorbisAudioInputStream.execute(): end"); }
		}



		private int getChannels()
		{
			return m_decodedStream.getFormat().getChannels();
		}



		private int getFrameSize()
		{
			return m_decodedStream.getFormat().getFrameSize();
		}



		private boolean isBigEndian()
		{
			return m_decodedStream.getFormat().isBigEndian();
		}



		@Override
		public void close()
			throws IOException
		{
			super.close();
			m_decodedStream.close();
		}



		// copied from TConversionTool
		private static int bytesToInt16(byte[] buffer,
						int byteOffset,
						boolean bigEndian)
		{
			return bigEndian ?
				((buffer[byteOffset]<<8) | (buffer[byteOffset+1] & 0xFF)) :
				((buffer[byteOffset+1]<<8) | (buffer[byteOffset] & 0xFF));
		}
	}



	/**	AudioInputStream returned on decoding of ogg vorbis.
		An instance of this class is returned if you call
		AudioSystem.getAudioInputStream(AudioFormat, AudioInputStream)
		to decode an ogg/vorbis stream. This class contains the logic
		of maintaining buffers and calling the decoder.
	*/
	/* Class should be private, but is public due to a bug (?) in the
	   aspectj compiler. */
	/*private*/public static class DecodedVorbisAudioInputStream
	extends TAsynchronousFilteredAudioInputStream
	{
  		private static final int	INPUT_BUFFER_SIZE = 4096;
  		private static final int	BUFFER_MULTIPLE = 4;
  		private static final int	BUFFER_SIZE = BUFFER_MULTIPLE * 256 * 2;
  		private static final int	CONVSIZE = BUFFER_SIZE * 2;

		private InputStream		m_oggBitStream = null;

		private byte[]			m_abInputBuffer;
		// Ogg structures
		private SyncState		m_oggSyncState = null;
  		private StreamState		m_oggStreamState = null;
  		private Page			m_oggPage = null;
  		private Packet			m_oggPacket = null;

		// Vorbis structures
  		private Info			m_vorbisInfo = null;
  		private Comment			m_vorbisComment = null;
  		private DspState		m_vorbisDspState = null;
  		private Block			m_vorbisBlock = null;

  		// private List			m_songComments = new ArrayList();
		// is altered later in a dubious way
  		@SuppressWarnings("unused")
  		private int				convsize = -1; // BUFFER_SIZE * 2;
		// TODO: further checking
  		private byte[]			convbuffer = new byte[CONVSIZE];
		private float[][]		m_aPcmOut;

		private boolean			m_bHeadersExpected;


		/**
		 * Constructor.
		 */
		public DecodedVorbisAudioInputStream(AudioFormat outputFormat, AudioInputStream bitStream)
		{
			super(outputFormat, AudioSystem.NOT_SPECIFIED);
			if (TDebug.TraceAudioConverter) { TDebug.out("DecodedVorbisAudioInputStream.<init>(): begin"); }
			m_oggBitStream = bitStream;
			m_bHeadersExpected = true;
			init_jorbis();
			if (TDebug.TraceAudioConverter) { TDebug.out("DecodedVorbisAudioInputStream.<init>(): end"); }
		}



		/**
		 * Initializes all the jOrbis and jOgg vars that are used for song playback.
		 */
		private void init_jorbis()
		{
			m_abInputBuffer = new byte[INPUT_BUFFER_SIZE];
			m_oggSyncState = new SyncState();
			m_oggStreamState = new StreamState();
			m_oggPage = new Page();
			m_oggPacket = new Packet();

			m_vorbisInfo = new Info();
			m_vorbisComment = new Comment();
			m_vorbisDspState = new DspState();
			m_vorbisBlock = new Block();
			m_vorbisBlock.init(m_vorbisDspState);

			m_oggSyncState.init();
		}



		/** Callback from circular buffer.
		 */
		public void execute()
		{
			if (TDebug.TraceAudioConverter) TDebug.out(">DecodedVorbisAudioInputStream.execute(): begin");
			if (m_bHeadersExpected)
			{
				if (TDebug.TraceAudioConverter) TDebug.out("reading headers...");
				// Headers (+ Comments).
				try
				{
					readHeaders();
				}
				catch (IOException e)
				{
					if (TDebug.TraceAllExceptions) { TDebug.out(e); }
					closePhysicalStream();
					if (TDebug.TraceAudioConverter) TDebug.out("<DecodedVorbisAudioInputStream.execute(): end");
					return;
				}
				m_bHeadersExpected = false;
				setupVorbisStructures();
			}
			if (TDebug.TraceAudioConverter) TDebug.out("decoding...");
			// Decoding !
			while (writeMore())
			{
				try
				{
					readOggPacket();
				}
				catch (IOException e)
				{
					if (TDebug.TraceAllExceptions) { TDebug.out(e); }
					closePhysicalStream();
					m_vorbisInfo.free();
					m_vorbisComment.free();
					m_vorbisDspState.free();
					m_vorbisBlock.free();
					if (TDebug.TraceAudioConverter) TDebug.out("<DecodedVorbisAudioInputStream.execute(): end");
					return;
				}
				decodeDataPacket();
			}
			if (m_oggPacket.isEos())
			{
				if (TDebug.TraceAudioConverter) TDebug.out("end of vorbis stream reached");
				/* The end of the vorbis stream is reached.
				   So we shut down the logical bitstream and
				   vorbis structures.
				*/
				m_oggStreamState.clear();
				m_vorbisBlock.clear();
				m_vorbisDspState.clear();
				m_vorbisInfo.clear();
				m_bHeadersExpected = true;
			}
			if (TDebug.TraceAudioConverter) TDebug.out("<DecodedVorbisAudioInputStream.execute(): end");
		}




		private void closePhysicalStream()
		{
			if (TDebug.TraceAudioConverter) TDebug.out("DecodedVorbisAudioInputStream.closePhysicalStream(): begin");
			m_oggSyncState.clear();
			try
			{
				if (m_oggBitStream != null)
				{
					m_oggBitStream.close();
				}
				getCircularBuffer().close();
			}
			catch (Exception e)
			{
				if (TDebug.TraceAllExceptions) { TDebug.out(e); }
			}
			if (TDebug.TraceAudioConverter) TDebug.out("DecodedVorbisAudioInputStream.closePhysicalStream(): end");
		}



		/** Read and process all three vorbis headers.
		*/
		private void readHeaders()
			throws IOException
		{
			readIdentificationHeader();
			readCommentAndCodebookHeaders();
			processComments();
		}



		/** Read the vorbis identification header.
		    @throw IOException
		*/
		private void readIdentificationHeader()
			throws IOException
		{
			readOggPage();
			m_oggStreamState.init(m_oggPage.getSerialNo());
			m_vorbisInfo.init();
			m_vorbisComment.init();
			if (m_oggStreamState.pageIn(m_oggPage) < 0)
			{
				throw new IOException("can't read first page of Ogg bitstream data, perhaps stream version mismatch");
			}
			if (m_oggStreamState.packetOut(m_oggPacket) != 1)
			{
				throw new IOException("can't read initial header packet");
			}
			if (m_vorbisInfo.headerIn(m_vorbisComment, m_oggPacket) < 0)
			{
				throw new IOException("packet is not a vorbis header");
			}
		}



		/** Read the comment header and the codebook header pages.
		*/
		private void readCommentAndCodebookHeaders()
			throws IOException
		{
			for (int i = 0; i < 2; i++)
			{
				readOggPacket();
				if (m_vorbisInfo.headerIn(m_vorbisComment, m_oggPacket) < 0)
				{
					throw new IOException("packet is not a vorbis header");
				}
			}
		}



		/**
		 */
		private void processComments()
		{
			TDebug.out("DecodedVorbisAudioInputStream.processComments(): begin");
			/*if (TDebug.TraceAudioConverter)*/ TDebug.out("DecodedVorbisAudioInputStream.processComments(): encoded by: " + m_vorbisComment.getVendor());

			String[] astrComments = m_vorbisComment.getUserComments();
			TDebug.out("user comments:");
			for (int i = 0; i < astrComments.length; i++)
			{
				TDebug.out(astrComments[i]);
			}
// 			byte[][] ptr = m_vorbisComment.user_comments;
// 			String currComment = "";
// 			m_songComments.clear();
// 			for (int j = 0; j < ptr.length; j++)
// 			{
// 				if (ptr[j] == null)
// 				{
// 					break;
// 				}
// 				currComment = (new String(ptr[j], 0, ptr[j].length - 1)).trim();
// 				m_songComments.add(currComment);
// 				if (currComment.toUpperCase().startsWith("ARTIST"))
// 				{
// 					String artistLabelValue = currComment.substring(7);
// 				}
// 				else if (currComment.toUpperCase().startsWith("TITLE"))
// 				{
// 					String titleLabelValue = currComment.substring(6);
// 					String miniDragLabel = currComment.substring(6);
// 				}
// 				if (TDebug.TraceAudioConverter) TDebug.out("Comment: " + currComment);
// 			}
// 			currComment = "Bitstream: " + m_vorbisInfo.getChannels() + " channel," + m_vorbisInfo.rate + "Hz";
// 			m_songComments.add(currComment);
// 			if (TDebug.TraceAudioConverter) TDebug.out(currComment);
// 			m_songComments.add(currComment);
// 			if (TDebug.TraceAudioConverter) TDebug.out(currComment);
			TDebug.out("DecodedVorbisAudioInputStream.processComments(): end");
		}



		/** Setup structures needed for vorbis decoding.
		    Precondition: m_vorbisInfo has to be initialized completely
		    (i.e. all three headers are read).
		*/
		private void setupVorbisStructures()
		{
			convsize = BUFFER_SIZE / m_vorbisInfo.getChannels();
			m_vorbisDspState.initSynthesis(m_vorbisInfo);
			m_vorbisBlock.init(m_vorbisDspState);
			m_aPcmOut = new float[m_vorbisInfo.getChannels()][];
		}



		/** Decode a packet of vorbis data.
		    This method assumes that a packet is available in
		    {@link #m_oggPacket m_oggPacket}. The content of this
		    packet is run through the decoder. The resulting
		    PCM data are written to the circular buffer.
		*/
		private void decodeDataPacket()
		{
			int nSamples;
			if (m_vorbisBlock.synthesis(m_oggPacket) == 0)
			{ // test for success!
				m_vorbisDspState.blockIn(m_vorbisBlock);
			}
			while ((nSamples = m_vorbisDspState.pcmOut(m_aPcmOut)) > 0)
			{
				// convert floats to signed ints and
				// interleave
				for (int nChannel = 0; nChannel < m_vorbisInfo.getChannels(); nChannel++)
				{
					int pointer = nChannel * getSampleSizeInBytes();
					for (int j = 0; j < nSamples; j++)
					{
						float fVal = m_aPcmOut[nChannel][j];
						clipAndWriteSample(fVal, pointer);
						pointer += getFrameSize();
					}
				}
				m_vorbisDspState.read(nSamples);
				getCircularBuffer().write(convbuffer, 0, getFrameSize() * nSamples);
			}
		}



		/** Scale and clip the sample and write it to convbuffer.
		 */
		private void clipAndWriteSample(float fSample, int nPointer)
		{
			int nSample;
			// TODO: check if clipping is necessary
			if (fSample > 1.0F)
			{
				fSample = 1.0F;
			}
			if (fSample < -1.0F)
			{
				fSample = -1.0F;
			}
			switch (getFormat().getSampleSizeInBits())
			{
			case 16:
				nSample = (int) (fSample * 32767.0F);
				if (isBigEndian())
				{
					convbuffer[nPointer++] = (byte) (nSample >> 8);
					convbuffer[nPointer] = (byte) (nSample & 0xFF);
				}
				else
				{
					convbuffer[nPointer++] = (byte) (nSample & 0xFF);
					convbuffer[nPointer] = (byte) (nSample >> 8);
				}
				break;

			case 24:
				nSample = (int) (fSample * 8388607.0F);
				if (isBigEndian())
				{
					convbuffer[nPointer++] = (byte) (nSample >> 16);
					convbuffer[nPointer++] = (byte) ((nSample >>> 8) & 0xFF);
					convbuffer[nPointer] = (byte) (nSample & 0xFF);
				}
				else
				{
					convbuffer[nPointer++] = (byte) (nSample & 0xFF);
					convbuffer[nPointer++] = (byte) ((nSample >>> 8) & 0xFF);
					convbuffer[nPointer] = (byte) (nSample >> 16);
				}
				break;

			case 32:
				nSample = (int) (fSample * 2147483647.0F);
				if (isBigEndian())
				{
					convbuffer[nPointer++] = (byte) (nSample >> 24);
					convbuffer[nPointer++] = (byte) ((nSample >>> 16) & 0xFF);
					convbuffer[nPointer++] = (byte) ((nSample >>> 8) & 0xFF);
					convbuffer[nPointer] = (byte) (nSample & 0xFF);
				}
				else
				{
					convbuffer[nPointer++] = (byte) (nSample & 0xFF);
					convbuffer[nPointer++] = (byte) ((nSample >>> 8) & 0xFF);
					convbuffer[nPointer++] = (byte) ((nSample >>> 16) & 0xFF);
					convbuffer[nPointer] = (byte) (nSample >> 24);
				}
				break;
			}
		}



		/** Read an ogg packet.
		    This method does everything necessary to read an ogg
		    packet. If needed, it calls
		    {@link #readOggPage readOggPage()}, which, in turn, may
		    read more data from the stream. The resulting packet is
		    placed in {@link #m_oggPacket m_oggPacket} (for which the
		    reference is not altered; is has to be initialized before).
		*/
		private void readOggPacket()
			throws IOException
		{
			while (true)
			{
				int result = m_oggStreamState.packetOut(m_oggPacket);
				if (result == 1)
				{
					return;
				}
				if (result == -1)
				{
					throw new IOException("can't read packet");
				}
				readOggPage();
				if (m_oggStreamState.pageIn(m_oggPage) < 0)
				{
					throw new IOException("can't read page of Ogg bitstream data");
				}
			}
		}



		/** Read an ogg page.
		    This method does everything necessary to read an ogg
		    page. If needed, it reads more data from the stream.
		    The resulting page is
		    placed in {@link #m_oggPage m_oggPage} (for which the
		    reference is not altered; is has to be initialized before).

		    Note: this method doesn't deliver the page read to a
		    StreamState object (which assembles pages to packets).
		    This has to be done by the caller.
		*/
		private void readOggPage()
			throws IOException
		{
			while (true)
			{
				int result = m_oggSyncState.pageOut(m_oggPage);
				if (result == 1)
				{
					return;
				}
				// we need more data from the stream
				// TODO: call stream.read() directly
				int nBytes = readFromStream(m_abInputBuffer, 0, m_abInputBuffer.length);
				// TODO: This clause should become obsolete; readFromStream() should
				// propagate exceptions directly.
				if (nBytes == -1)
				{
					throw new EOFException();
				}
				m_oggSyncState.write(m_abInputBuffer, nBytes);
			}
		}



		/** Read raw data from to ogg bitstream.
		    Reads from  {@link #m_oggBitStream m_oggBitStream} a
		    specified number of bytes into a buffer, starting
		    at a specified buffer index.

		    @param buffer the where the read data should be put into. Its length has to be at least nStart + nLength.
		    @param nStart
		    @param nLength the number of bytes to read
		    @return the number of bytes read (maybe 0) or
		    -1 if there is no more data in the stream.
		*/
		private int readFromStream(byte[] buffer, int nStart, int nLength)
			throws IOException
		{
			return m_oggBitStream.read(buffer, nStart, nLength);
		}



		/**
		 */
		private int getSampleSizeInBytes()
		{
			return getFormat().getFrameSize() / getFormat().getChannels();
		}



		/** .
		    @return .
		*/
		private int getFrameSize()
		{
			return getFormat().getFrameSize();
		}



		/** Returns if this stream (the decoded one) is big endian.
		    @return true if this stream is big endian.
		*/
		private boolean isBigEndian()
		{
			return getFormat().isBigEndian();
		}



		/**
		 */
		@Override
		public void close() throws IOException
		{
			super.close();
			m_oggBitStream.close();
		}

	}
}



/*** VorbisFormatConversionProvider.java ***/
