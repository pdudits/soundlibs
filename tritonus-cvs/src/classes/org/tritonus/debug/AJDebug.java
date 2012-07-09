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

import javax.sound.midi.MidiSystem;
import javax.sound.midi.Synthesizer;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.SourceDataLine;

import org.tritonus.core.TMidiConfig;
import org.tritonus.core.TInit;
import org.tritonus.share.TDebug;
import org.tritonus.share.midi.TSequencer;
import org.tritonus.midi.device.alsa.AlsaSequencer;
import org.tritonus.midi.device.alsa.AlsaSequencer.PlaybackAlsaMidiInListener;
import org.tritonus.midi.device.alsa.AlsaSequencer.RecordingAlsaMidiInListener;
import org.tritonus.midi.device.alsa.AlsaSequencer.AlsaSequencerReceiver;
import org.tritonus.midi.device.alsa.AlsaSequencer.AlsaSequencerTransmitter;
import org.tritonus.midi.device.alsa.AlsaSequencer.LoaderThread;
import org.tritonus.midi.device.alsa.AlsaSequencer.MasterSynchronizer;

import org.tritonus.share.sampled.convert.TAsynchronousFilteredAudioInputStream;



/** Debugging output aspect.
 */
public aspect AJDebug
extends Utils
{
	pointcut allExceptions(): handler(Throwable+);


	// TAudioConfig, TMidiConfig, TInit

	pointcut TMidiConfigCalls(): execution(* TMidiConfig.*(..));
	pointcut TInitCalls(): execution(* TInit.*(..));


	// share

	// midi

	pointcut MidiSystemCalls(): execution(* MidiSystem.*(..));

	pointcut Sequencer(): execution(TSequencer+.new(..)) ||
		execution(* TSequencer+.*(..)) ||
		execution(* PlaybackAlsaMidiInListener.*(..)) ||
		execution(* RecordingAlsaMidiInListener.*(..)) ||
		execution(* AlsaSequencerReceiver.*(..)) ||
		execution(* AlsaSequencerTransmitter.*(..)) ||
		execution(LoaderThread.new(..)) ||
		execution(* LoaderThread.*(..)) ||
		execution(MasterSynchronizer.new(..)) ||
		execution(* MasterSynchronizer.*(..));

	// audio

	pointcut AudioSystemCalls(): execution(* AudioSystem.*(..));

	pointcut sourceDataLine():
		call(* SourceDataLine+.*(..));

	// OLD

// 	pointcut playerStates():
// 		execution(private void TPlayer.setState(int));



	// currently not used
	pointcut printVelocity(): execution(* JavaSoundToneGenerator.playTone(..)) && call(JavaSoundToneGenerator.ToneThread.new(..));

	// pointcut tracedCall(): execution(protected void JavaSoundAudioPlayer.doRealize() throws Exception);


	///////////////////////////////////////////////////////
	//
	//	ACTIONS
	//
	///////////////////////////////////////////////////////


	before(): MidiSystemCalls()
		{
			if (TDebug.TraceMidiSystem) outEnteringJoinPoint(thisJoinPoint);
		}

	after(): MidiSystemCalls()
		{
			if (TDebug.TraceSequencer) outLeavingJoinPoint(thisJoinPoint);
		}

	before(): Sequencer()
		{
			if (TDebug.TraceSequencer) outEnteringJoinPoint(thisJoinPoint);
		}

	after(): Sequencer()
		{
			if (TDebug.TraceSequencer) outLeavingJoinPoint(thisJoinPoint);
		}

	before(): TInitCalls()
		{
			if (TDebug.TraceInit) outEnteringJoinPoint(thisJoinPoint);
		}

	after(): TInitCalls()
		{
			if (TDebug.TraceInit) outLeavingJoinPoint(thisJoinPoint);
		}

	before(): TMidiConfigCalls()
		{
			if (TDebug.TraceMidiConfig) outEnteringJoinPoint(thisJoinPoint);
		}

	after(): TMidiConfigCalls()
		{
			if (TDebug.TraceMidiConfig) outLeavingJoinPoint(thisJoinPoint);
		}

// execution(* TAsynchronousFilteredAudioInputStream.read(..))

	before(): execution(* TAsynchronousFilteredAudioInputStream.read())
		{
			if (TDebug.TraceAudioConverter) outEnteringJoinPoint(thisJoinPoint);
		}

	after(): execution(* TAsynchronousFilteredAudioInputStream.read())
		{
			if (TDebug.TraceAudioConverter) outLeavingJoinPoint(thisJoinPoint);
		}

	before(): execution(* TAsynchronousFilteredAudioInputStream.read(byte[]))
		{
			if (TDebug.TraceAudioConverter) outEnteringJoinPoint(thisJoinPoint);
		}

	after(): execution(* TAsynchronousFilteredAudioInputStream.read(byte[]))
		{
			if (TDebug.TraceAudioConverter) outLeavingJoinPoint(thisJoinPoint);
		}

	before(): execution(* TAsynchronousFilteredAudioInputStream.read(byte[], int, int))
		{
			if (TDebug.TraceAudioConverter) outEnteringJoinPoint(thisJoinPoint);
		}

	after(): execution(* TAsynchronousFilteredAudioInputStream.read(byte[], int, int))
		{
			if (TDebug.TraceAudioConverter) outLeavingJoinPoint(thisJoinPoint);
		}

	after() returning(int nBytes): call(* TAsynchronousFilteredAudioInputStream.read(byte[], int, int))
		{
			if (TDebug.TraceAudioConverter) TDebug.out("returning bytes: " + nBytes);
		}



// 	before(int nState): playerStates() && args(nState)
// 		{
// // 			if (TDebug.TracePlayerStates)
// // 			{
// // 				TDebug.out("TPlayer.setState(): " + nState);
// // 			}
// 		}

// 	before(): playerStateTransitions()
// 		{
// // 			if (TDebug.TracePlayerStateTransitions)
// // 			{
// // 				TDebug.out("Entering: " + thisJoinPoint);
// // 			}
// 		}


// 	Synthesizer around(): call(* MidiSystem.getSynthesizer())
// 		{
// // 			Synthesizer	s = proceed();
// // 			if (TDebug.TraceToneGenerator)
// // 			{
// // 				TDebug.out("MidiSystem.getSynthesizer() gives:  " + s);
// // 			}
// // 			return s;
// 			// only to get no compilation errors
// 			return null;
// 		}

// TODO: v gives an error; find out what to do
// 	before(int v): printVelocity() && args(nVelocity)
// 		{
// 			if (TDebug.TraceToneGenerator)
// 			{
// 				TDebug.out("velocity: " + v);
// 			}
// 		}


	before(Throwable t): allExceptions() && args(t)
		{
			if (TDebug.TraceAllExceptions) TDebug.out(t);
		}
}


/*** AJDebug.java ***/

