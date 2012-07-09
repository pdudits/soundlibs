/*
 * FloatSampleInput.java
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

/**
 * Master interface for all classes providing audio data in FloatSampleBuffers.
 * 
 * @author florian
 */
public interface FloatSampleInput {

	/**
	 * Fill the entire buffer with audio data. If fewer samples are read, this
	 * method will use buffer.changeSampleCount() to adjust the size of the
	 * buffer. If no samples can be written to the buffer, the buffer's sample
	 * count will be set to 0.
	 * <p>
	 * The buffer's channel count and sample rate may not be changed by the
	 * implementation of this method.
	 * 
	 * @param buffer the buffer to be filled
	 */
	public void read(FloatSampleBuffer buffer);

	/**
	 * Fill the specified portion of the buffer with the next audio data to be
	 * read. If fewer samples are read, this method will use
	 * buffer.changeSampleCount() to adjust the size of the buffer. If no
	 * samples can be written to the buffer, the buffer's sample count will be
	 * set to <code>offset</code>.
	 * <p>
	 * The buffer's channel count and sample rate may not be changed by the
	 * implementation of this method.
	 * 
	 * @param buffer the buffer to be filled
	 * @param offset the start index, in samples, where to start filling the
	 *            buffer
	 * @param sampleCount the number fo samples to fill into the buffer
	 */
	public void read(FloatSampleBuffer buffer, int offset, int sampleCount);

	/**
	 * Determine if this stream has reached its end. If true, subsequent calls
	 * to read() will return 0-sized buffers.
	 * 
	 * @return true if this stream reached its end.
	 */
	public boolean isDone();

	/**
	 * @return the number of audio channels of the audio data that this stream
	 *         provides. If it can support a variable number of channels, this
	 *         method returns AudioSystem.NOT_SPECIFIED.
	 */
	public int getChannels();

	/**
	 * @return the sample rate of the audio data that this stream provides. If
	 *         it can support different sample rates, this method returns a
	 *         negative number, e.g. AudioSystem.NOT_SPECIFIED.
	 */
	public float getSampleRate();

}
