/*
 * FloatInputStream.java
 *
 *	This file is part of Tritonus: http://www.tritonus.org/
 */

/*
 *  Copyright (c) 2006 by Florian Bomers <http://www.bomers.de>
 *  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * - Redistributions of source code must retain the above copyright notice,
 *   this list of conditions and the following disclaimer.
 * - Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */

/*
 |<---            this code is formatted to fit into 80 columns             --->|
 */
package org.tritonus.share.sampled;

import java.io.*;
import javax.sound.sampled.*;

/**
 * An implementation of AudioInputStream that implements the FloatSampleInput
 * interface. This is a convenience class to instantly make any AudioInputStream
 * capable of fulfilling the FloatSampleInput interface, or vice versa: make an
 * existing FloatSampleInput class compatible with AudioInputStream.
 * <p>
 * All calls to FloatSampleInput.read() will cause implicit conversion to
 * FloatSampleBuffer. If the underlying stream implementes FloatSampleInput, the
 * FloatSampleInput.read method is used for reading.
 * 
 * @author florian
 */
public class FloatInputStream extends AudioInputStream implements
		FloatSampleInput {

	// set this if we can read from an AudioInputStream
	private InputStream sourceStream;
	// set this if we can read from an FloatSampleInput
	private FloatSampleInput sourceInput;

	/**
	 * true if the source stream returned eof, or if this stream was closed.
	 */
	private boolean eofReached = false;

	/**
	 * a temporary byte buffer for reading from an underlying input stream
	 */
	private byte[] tempBuffer = null;

	/**
	 * Create a new FloatInputStream that shadows the sourceStream.
	 * 
	 * @param sourceStream
	 * @throws IllegalArgumentException if the stream's format is not compatible
	 */
	public FloatInputStream(AudioInputStream sourceStream) {
		super(sourceStream, sourceStream.getFormat(),
				sourceStream.getFrameLength());
		this.sourceStream = sourceStream;
		init();
	}

	/**
	 * Create a new FloatInputStream that shadows the sourceStream.
	 * 
	 * @param sourceStream
	 * @param format the native format of sourceStream
	 * @param frameLength the length in frames of the streams, or
	 *            AudioSystem.NOT_SPECIFIED if not known or unlimited.
	 * @throws IllegalArgumentException if the stream's format is not compatible
	 */
	public FloatInputStream(InputStream sourceStream, AudioFormat format,
			long frameLength) {
		super(sourceStream, format, frameLength);
		this.sourceStream = sourceStream;
		init();
	}

	/**
	 * Create a new FloatInputStream that will make the specified
	 * FloatSampleInput a complete AudioInputStream.
	 * 
	 * @param sourceInput
	 * @param format the native format for the read(byte[]) method
	 * @param frameLength the length in frames of the stream, or
	 *            AudioSystem.NOT_SPECIFIED if not known or unlimited.
	 * @throws IllegalArgumentException if the format is not compatible
	 */
	public FloatInputStream(FloatSampleInput sourceInput, AudioFormat format,
			long frameLength) {
		super(new ByteArrayInputStream(new byte[0]), format, frameLength);
		this.sourceStream = null;
		this.sourceInput = sourceInput;
		init();
	}

	// interface FloatSampleInput
	public void read(FloatSampleBuffer outBuffer) {
		read(outBuffer, 0, outBuffer.getSampleCount());
	}

	private void init() {
		if (sourceStream != null && (sourceStream instanceof FloatSampleInput)) {
			sourceInput = (FloatSampleInput) sourceStream;
		}
		// make sure format is supported, and remember format code
		FloatSampleBuffer.checkFormatSupported(format);
	}

	public void read(FloatSampleBuffer buffer, int offset, int sampleCount) {
		if (sampleCount == 0 || isDone()) {
			buffer.setSampleCount(offset, true);
			return;
		}
		if (buffer.getChannelCount() != getChannels()) {
			throw new IllegalArgumentException(
					"read: passed buffer has different channel count");
		}
		if (sourceInput != null) {
			sourceInput.read(buffer, offset, sampleCount);
		} else {
			// read into temporary byte buffer
			int byteBufferSize = buffer.getSampleCount()
					* getFormat().getFrameSize();
			byte[] lTempBuffer = tempBuffer;
			if (lTempBuffer == null || byteBufferSize > lTempBuffer.length) {
				lTempBuffer = new byte[byteBufferSize];
				tempBuffer = lTempBuffer;
			}
			int readSamples = 0;
			int byteOffset = 0;
			while (readSamples < sampleCount) {
				int readBytes;
				try {
					readBytes = sourceStream.read(lTempBuffer, byteOffset,
							byteBufferSize);
				} catch (IOException ioe) {
					readBytes = -1;
				}
				if (readBytes < 0) {
					eofReached = true;
					readBytes = 0;
					break;
				} else if (readBytes == 0) {
					Thread.yield();
				} else {
					readSamples += readBytes / getFormat().getFrameSize();
					byteBufferSize -= readBytes;
					byteOffset += readBytes;
				}
			}
			buffer.setSampleCount(offset + readSamples, (offset > 0));
			if (readSamples > 0) {
				// convert
				buffer.setSamplesFromBytes(lTempBuffer, 0, getFormat(), offset,
						readSamples);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.tritonus.share.sampled.FloatSampleInput#getChannels()
	 */
	public int getChannels() {
		return getFormat().getChannels();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.tritonus.share.sampled.FloatSampleInput#getSampleRate()
	 */
	public float getSampleRate() {
		return getFormat().getSampleRate();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.tritonus.share.sampled.FloatSampleInput#isDone()
	 */
	public boolean isDone() {
		if (!eofReached && sourceInput != null) {
			return sourceInput.isDone();
		}
		return eofReached;
	}

	// interface AudioInputStream

	@Override
	public int read() throws IOException {
		if (getFormat().getFrameSize() != 1) {
			throw new IOException("frame size must be 1 to read a single byte");
		}
		// very ugly, but efficient. Who uses this method anyway ?
		byte[] temp = new byte[1];
		int result = read(temp);
		if (result <= 0) {
			return -1;
		}
		return temp[0] & 0xFF;
	}

	/**
	 * @see #read(byte[], int, int)
	 */
	@Override
	public int read(byte[] abData) throws IOException {
		return read(abData, 0, abData.length);
	}

	private FloatSampleBuffer tempFloatBuffer = null;

	/**
	 * If an underlying InputStream is available, read from it, otherwise read
	 * from an underlying FloatSampleInput stream and convert to a byte array.
	 */
	@Override
	public int read(byte[] abData, int nOffset, int nLength) throws IOException {
		if (isDone()) {
			return -1;
		}
		// read from sourceStream, if available
		if (sourceStream != null) {
			return readBytesFromInputStream(abData, nOffset, nLength);
		}
		// otherwise read from sourceInput
		return readBytesFromFloatInput(abData, nOffset, nLength);
	}

	/**
	 * internal method to read from the underlying InputStream.<br>
	 * Precondition: sourceStream!=null
	 */
	protected int readBytesFromInputStream(byte[] abData, int nOffset,
			int nLength) throws IOException {
		int readBytes = sourceStream.read(abData, nOffset, nLength);
		if (readBytes < 0) {
			eofReached = true;
		}
		return readBytes;
	}

	/**
	 * internal method to read from the underlying InputStream.<br>
	 * Precondition: sourceInput!=null
	 * 
	 * @param abData the byte array to fill, or null if just skipping
	 */
	protected int readBytesFromFloatInput(byte[] abData, int nOffset,
			int nLength) throws IOException {
		FloatSampleInput lInput = sourceInput; 
		if (lInput.isDone()) {
			return -1;
		}
		int frameCount = nLength / getFormat().getFrameSize();
		FloatSampleBuffer lTempBuffer = tempFloatBuffer;
		if (lTempBuffer == null) {
			lTempBuffer = new FloatSampleBuffer(getFormat().getChannels(),
					frameCount, getFormat().getSampleRate());
			tempFloatBuffer = lTempBuffer;
		} else {
			lTempBuffer.setSampleCount(frameCount, false);
		}
		lInput.read(lTempBuffer);
		if (lInput.isDone()) {
			return -1;
		}
		if (abData != null) {
			int writtenBytes = tempFloatBuffer.convertToByteArray(abData,
					nOffset, getFormat());
			return writtenBytes;
		}
		// special mode: allow abData to be null for skip()
		return frameCount * getFormat().getFrameSize();
	}

	@Override
	public synchronized long skip(long nSkip) throws IOException {
		// only returns integral frames
		long skipFrames = nSkip / getFormat().getFrameSize();
		if (sourceStream != null) {
			return sourceStream.skip(skipFrames * getFormat().getFrameSize());
		}
		// for FloatSampleInput, there is no skip() method, so just read into
		// the tempBuffer
		if (isDone() || skipFrames <= 0) {
			// cannot skip backwards
			return 0;
		}
		return readBytesFromFloatInput(null, 0,
				(int) (skipFrames * getFormat().getFrameSize()));
	}

	@Override
	public int available() throws IOException {
		if (sourceStream != null) {
			return sourceStream.available();
		}
		return AudioSystem.NOT_SPECIFIED;
	}

	@Override
	public void mark(int readlimit) {
		if (sourceStream != null) {
			sourceStream.mark(readlimit);
		} else {
			// what to do?
		}
	}

	@Override
	public void reset() throws IOException {
		if (sourceStream != null) {
			sourceStream.reset();
		} else {
			// what to do?
		}
	}

	@Override
	public boolean markSupported() {
		if (sourceStream != null) {
			return sourceStream.markSupported();
		}
		return false;
	}

	@Override
	public void close() throws IOException {
		if (eofReached) {
			return;
		}
		eofReached = true;
		if (sourceStream != null) {
			sourceStream.close();
		}
		// clean memory, this will also be an indicator that
		// the stream is closed
		tempBuffer = null;
		tempFloatBuffer = null;
	}

}
