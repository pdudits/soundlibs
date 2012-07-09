/*
 *	TSynchronousFilteredAudioInputStream.java
 *
 *	This file is part of Tritonus: http://www.tritonus.org/
 */

/*
 *  Copyright (c) 1999,2000 by Florian Bomers <http://www.bomers.de>
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

package org.tritonus.share.sampled.convert;

import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;

import org.tritonus.share.TDebug;
import org.tritonus.share.sampled.AudioUtils;
import org.tritonus.share.sampled.FloatSampleBuffer;
import org.tritonus.share.sampled.FloatSampleInput;



/**
 * Base class for types of audio filter/converter that translate one frame to another frame.<br>
 * It provides all the transformation of frame sizes.<br>
 * It does NOT handle different sample rates of original stream and this stream !
 *
 * @author Florian Bomers
 */
public abstract class TSynchronousFilteredAudioInputStream
extends TAudioInputStream implements FloatSampleInput {

	private AudioInputStream originalStream;
	
	/** the same originalStream cast to FloatSampleInput, if it is one */
	private FloatSampleInput originalStreamFloat;
	
	private AudioFormat originalFormat;
	/** 1 if original format's frame size is NOT_SPECIFIED */
	private int originalFrameSize;
	/** 1 if original format's frame size is NOT_SPECIFIED */
	private int newFrameSize;
	
	private boolean EOF = false;

	/**
	 * The intermediate buffer used during convert actions
	 * (if not convertInPlace is used).
	 * It remains until this audioStream is closed or destroyed
	 * and grows with the time - it always has the size of the
	 * largest intermediate buffer ever needed.
	 */
	protected byte[] m_buffer=null;

	/**
	 * For use of the more efficient method convertInPlace.
	 * it will be set to true when (frameSizeFactor==1)
	 */
	private boolean	m_bConvertInPlace = false;
	
	/** if this flag is set, convert(FloatSampleBuffer) is implemented by overriding classes */
	private boolean m_enableFloatConversion = false;

	public TSynchronousFilteredAudioInputStream(AudioInputStream audioInputStream, AudioFormat newFormat) {
		// the super class will do nothing... we override everything
		super(audioInputStream, newFormat, audioInputStream.getFrameLength());
		originalStream=audioInputStream;
		originalFormat=audioInputStream.getFormat();
		originalFrameSize=(originalFormat.getFrameSize()<=0) ?
		                  1 : originalFormat.getFrameSize();
		newFrameSize=(getFormat().getFrameSize()<=0) ?
		             1 : getFormat().getFrameSize();
		if (originalStream instanceof FloatSampleInput) {
			originalStreamFloat = (FloatSampleInput) originalStream;
		}
		if (TDebug.TraceAudioConverter) {
			TDebug.out("TSynchronousFilteredAudioInputStream: original format ="
			           +AudioUtils.format2ShortStr(originalFormat));
			TDebug.out("TSynchronousFilteredAudioInputStream: converted format="
			           +AudioUtils.format2ShortStr(getFormat()));
		}
		m_bConvertInPlace = false;
		m_enableFloatConversion = false;
	}

	/**
	 * descendant classes should call this method if they have implemented
	 * convertInPlace(). ConvertInPlace will only be used if the converted frame
	 * size is larger than the original frame size.
	 */
	protected boolean enableConvertInPlace() {
		if (newFrameSize >= originalFrameSize) {
			m_bConvertInPlace = true;
		}
		return m_bConvertInPlace;
	}
	
	/**
	 * Descendant classes should call this method if they have implemented
	 * convert(FloatSampleBuffer). That convert method will only be called 
	 * if this class' FloatSampleInput.read() is used.
	 */
	protected void enableFloatConversion() {
		m_enableFloatConversion = true;
	}
	


	/**
	 * Override this method to do the actual conversion.
	 * inBuffer starts always at index 0 (it is an internal buffer)
	 * You should always override this.
	 * inFrameCount is the number of frames in inBuffer. These
	 * frames are of the format originalFormat.
	 * @return the resulting number of <B>frames</B> converted and put into
	 * outBuffer. The return value is in the format of this stream.
	 */
	protected abstract int convert(byte[] inBuffer, byte[] outBuffer, int outByteOffset, int inFrameCount);



	/**
	 * Override this method to provide in-place conversion of samples.
	 * To use it, call "enableConvertInPlace()". It will only be used when
	 * input bytes per frame >= output bytes per frame.
	 * This method must always convert frameCount frames, so no return value is necessary.
	 */
	protected void convertInPlace(byte[] buffer, int byteOffset, int frameCount) {
		throw new RuntimeException("illegal call to convertInPlace");
	}

	/**
	 * Override this method to do the actual conversion in the
	 * FloatSampleBuffer. Use buffer's methods to shrink the number of samples,
	 * if necessary. This method will only be called if this stream is accessed
	 * by way of FloatSampleInput methods.
	 * 
	 * @param buffer the buffer to convert
	 * @param offset the offset in buffer in samples
	 * @param count the number of samples in buffer to convert
	 */
	protected void convert(FloatSampleBuffer buffer, int offset, int count) {
		throw new RuntimeException("illegal call to convert(FloatSampleBuffer)");
	}


	@Override
	public int read()
	throws IOException {
		if (newFrameSize != 1) {
			throw new IOException("frame size must be 1 to read a single byte");
		}
		// very ugly, but efficient. Who uses this method anyway ?
		byte[] temp = new byte[1];
		int result = read(temp);
		if (result == -1) {
			return -1;
		}
		if (result == 0) {
			// what in this case ??? Let's hope it never occurs.
			return -1;
		}
		return temp[0] & 0xFF;
	}


	/** remove the temporary read buffer to save heap */
	private void clearBuffer() {
		m_buffer = null;
		m_floatByteBuffer = null;
	}

	public AudioInputStream getOriginalStream() {
		return originalStream;
	}

	public AudioFormat getOriginalFormat() {
		return originalFormat;
	}

	/**
	 * Read nLength bytes that will be the converted samples
	 * of the original InputStream.
	 * When nLength is not an integral number of frames,
	 * this method may read less than nLength bytes.
	 */
	@Override
	public final int read(byte[] abData, int nOffset, int nLength)
	throws IOException {
		// number of frames that we have to read from the underlying stream.
		int	nFrameLength = nLength/newFrameSize;

		// number of bytes that we need to read from underlying stream.
		int	originalBytes = nFrameLength * originalFrameSize;

		if (TDebug.TraceAudioConverter) {
			TDebug.out("> TSynchronousFilteredAIS.read(buffer["+abData.length+"], "
			           +nOffset+" ,"+nLength+" bytes ^="+nFrameLength+" frames)");
		}
		int nFramesConverted = 0;

		// set up buffer to read
		byte readBuffer[];
		int readOffset;
		if (m_bConvertInPlace) {
			readBuffer=abData;
			readOffset=nOffset;
		} else {
			// assert that the buffer fits
			if (m_buffer == null || m_buffer.length < originalBytes) {
				m_buffer = new byte[originalBytes];
			}
			readBuffer=m_buffer;
			readOffset=0;
		}
		int nBytesRead = originalStream.read(readBuffer, readOffset, originalBytes);
		if (nBytesRead == -1) {
			// end of stream
			clearBuffer();
			EOF = true;
			return -1;
		}
		int nFramesRead = nBytesRead / originalFrameSize;
		if (TDebug.TraceAudioConverter) {
			TDebug.out("original.read returned "
			           +nBytesRead+" bytes ^="+nFramesRead+" frames");
		}
		if (m_bConvertInPlace) {
			convertInPlace(abData, nOffset, nFramesRead);
			nFramesConverted=nFramesRead;
		} else {
			nFramesConverted = convert(m_buffer, abData, nOffset, nFramesRead);
		}
		if (TDebug.TraceAudioConverter) {
			TDebug.out("< converted "+nFramesConverted+" frames");
		}
		return nFramesConverted*newFrameSize;
	}


	@Override
	public long skip(long nSkip)
	throws IOException {
		// only returns integral frames
		long skipFrames = nSkip / newFrameSize;
		long originalSkippedBytes = originalStream.skip(skipFrames*originalFrameSize);
		long skippedFrames = originalSkippedBytes/originalFrameSize;
		return skippedFrames * newFrameSize;
	}


	@Override
	public int available()
	throws IOException {
		int origAvailFrames = originalStream.available()/originalFrameSize;
		return origAvailFrames*newFrameSize;
	}


	@Override
	public void close()
	throws IOException {
		EOF = true;
		originalStream.close();
		clearBuffer();
	}



	@Override
	public void mark(int readlimit) {
		int readLimitFrames=readlimit/newFrameSize;
		originalStream.mark(readLimitFrames*originalFrameSize);
	}



	@Override
	public void reset()
	throws IOException {
		originalStream.reset();
	}


	@Override
	public boolean markSupported() {
		return originalStream.markSupported();
	}

	// interface FloatSampleInput
	
	public int getChannels() {
		return format.getChannels();
	}

	public float getSampleRate() {
		return format.getSampleRate();
	}

	public boolean isDone() {
		// if this class was closed, never return open again
		if (EOF) return true;
		if (originalStreamFloat != null) {
			return originalStreamFloat.isDone();
		}
		return false;
	}

	/** temporary byte buffer for conversion from/to byte/float arrays */
	private byte[] m_floatByteBuffer = null;
	
	/**
	 * read sampleCount converted samples at the specified offset. The current
	 * implementation requires that offset is 0 and sampleCount ==
	 * buffer.getSampleCount().
	 */
	public void read(FloatSampleBuffer buffer, int offset, int sampleCount) {
		try {
			// Case 1: reading cannot, but processing can be done in float
			// layer,
			// so read unconverted bytes, then convert to float and process
			if (originalStreamFloat == null && m_enableFloatConversion) {
				// currently cannot convert in the middle of the buffer
				if (offset > 0 || sampleCount != buffer.getSampleCount()) {
					throw new IllegalArgumentException(
							"float reading with offset not supported");
				}
				// allocate a byte array large enough to hold the byte data
				int reqSize = sampleCount * originalFrameSize;
				if (m_floatByteBuffer == null
						|| m_floatByteBuffer.length < reqSize) {
					m_floatByteBuffer = new byte[reqSize];
				}
				// read into byte array -- is already processed
				int bytesRead = originalStream.read(m_floatByteBuffer, 0,
						reqSize);
				// convert the byte array to float
				if (bytesRead <= 0) {
					// EOF or nothing read
					buffer.setSampleCount(0, false);
					return;
				}
				// convert to float
				buffer.initFromByteArray(m_floatByteBuffer, 0, bytesRead,
						originalFormat);
				// do the processing
				convert(buffer, 0, buffer.getSampleCount());
			} else
			// Case 2: reading or processing cannot be done in float layer,
			// do the conversion with byte array and convert afterwards
			if (originalStreamFloat == null || !m_enableFloatConversion) {
				// currently cannot convert in the middle of the buffer
				if (offset > 0 || sampleCount != buffer.getSampleCount()) {
					throw new IllegalArgumentException(
							"float reading with offset not supported");
				}
				// allocate a byte array large enough to hold the converted data
				int reqSize = sampleCount * format.getFrameSize();
				if (m_floatByteBuffer == null
						|| m_floatByteBuffer.length < reqSize) {
					m_floatByteBuffer = new byte[reqSize];
				}
				// read into byte array -- is already processed
				int bytesRead = read(m_floatByteBuffer, 0, reqSize);
				// convert the byte array to float
				if (bytesRead <= 0) {
					// EOF or nothing read
					buffer.setSampleCount(0, false);
					return;
				}
				// convert to float
				buffer.initFromByteArray(m_floatByteBuffer, 0, bytesRead,
						format);
			} else {
				// read from the source stream
				originalStreamFloat.read(buffer, offset, sampleCount);
				if (offset + sampleCount > buffer.getSampleCount()) {
					sampleCount = buffer.getSampleCount() - offset;
					if (sampleCount < 0) {
						sampleCount = 0;
					}
				}
				// do the actual processing
				convert(buffer, offset, sampleCount);
			}

		} catch (IOException ioe) {
			if (TDebug.TraceAllExceptions) {
				ioe.printStackTrace();
			}
			buffer.setSampleCount(0, false);
		}
	}

	public void read(FloatSampleBuffer buffer) {
		read(buffer, 0, buffer.getSampleCount());
	}

}


/*** TSynchronousFilteredAudioInputStream.java ***/
