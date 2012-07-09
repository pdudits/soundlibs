/*
 *	AJDebug.java
 *
 *	This file is part of Tritonus: http://www.tritonus.org/
 */

/*
 *  Copyright (c) 1999 - 2002 by Matthias Pfisterer
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

package org.tritonus.debug;

import org.aspectj.lang.JoinPoint;

import org.tritonus.share.TDebug;

import org.tritonus.sampled.convert.vorbis.VorbisFormatConversionProvider;
import org.tritonus.sampled.convert.vorbis.VorbisFormatConversionProvider.DecodedVorbisAudioInputStream;



/** Debugging output aspect.
 */
privileged aspect AJDebugVorbis
extends Utils
{
	pointcut allExceptions(): handler(Throwable+);

	pointcut AudioConverterCalls():
		execution(JorbisFormatConversionProvider.new(..)) ||
		execution(* JorbisFormatConversionProvider.*(..)) ||
		execution(DecodedJorbisAudioInputStream.new(..)) ||
		execution(* DecodedJorbisAudioInputStream.*(..));


// 	pointcut sourceDataLine():
// 		call(* SourceDataLine+.*(..));


	// currently not used
// 	pointcut printVelocity(): execution(* JavaSoundToneGenerator.playTone(..)) && call(JavaSoundToneGenerator.ToneThread.new(..));

	// pointcut tracedCall(): execution(protected void JavaSoundAudioPlayer.doRealize() throws Exception);


	///////////////////////////////////////////////////////
	//
	//	ACTIONS
	//
	///////////////////////////////////////////////////////


	before(): AudioConverterCalls()
		{
			if (TDebug.TraceAudioConverter)
			{
				outEnteringJoinPoint(thisJoinPoint);
			}
		}

	after(): AudioConverterCalls()
		{
			if (TDebug.TraceAudioConverter)
			{
				outLeavingJoinPoint(thisJoinPoint);
			}
		}


	before(Throwable t): allExceptions() && args(t)
		{
			if (TDebug.TraceAllExceptions)
			{
				TDebug.out(t);
			}
		}
}


/*** AJDebug.java ***/

