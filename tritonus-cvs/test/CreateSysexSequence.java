/*
 *	CreateSysexSequence.java
 *
 *	TODO: short description
 */

/*
 *  Copyright (c) 2000 by Matthias Pfisterer <Matthias.Pfisterer@web.de>
 *
 *   This program is free software; you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published
 *   by the Free Software Foundation; either version 2 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public
 *   License along with this program; if not, write to the Free Software
 *   Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */

import java.io.File;
import java.io.IOException;

import javax.sound.midi.Sequence;
import javax.sound.midi.SysexMessage;
import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Track;
import javax.sound.midi.InvalidMidiDataException;



/**	Creates a Sequence with some sysex messages.
	[F0 01 F7]
	[F0 F7]
	[F0 02]
	[F7 F7]
	[F0 03] ??
	[F7] ??
	[F7 F0 04 F7]
 */
public class CreateSysexSequence
{
	public static void main(String[] args)
		throws IOException, InvalidMidiDataException
	{
		if (args.length != 1)
		{
			out("usage:");
			out("java CreateSysexSequence <midifile>");
			System.exit(1);
		}
		int	nResolution = 480;
 		String	strFilename = args[0];
		Sequence	sequence = new Sequence(Sequence.PPQ,
							nResolution);
		Track	track = sequence.createTrack();
		SysexMessage	sm = null;
		MetaMessage	mm = null;
		MidiEvent	me = null;
		byte[]		abData;

		// [F0 01 F7]
		sm = new SysexMessage();
		abData = new byte[]{(byte) 0xF0, (byte) 0x01, (byte) 0xF7};
		sm.setMessage(abData, abData.length);
		me = new MidiEvent(sm, 0);
		track.add(me);

// 		// [F0 F7]
// 		sm = new SysexMessage();
// 		abData = new byte[]{(byte) 0xF0, (byte) 0xF7};
// 		sm.setMessage(abData, abData.length);
// 		me = new MidiEvent(sm, 0);
// 		track.add(me);

		// [F0 02]
		sm = new SysexMessage();
		abData = new byte[]{(byte) 0xF0, (byte) 0x02};
		sm.setMessage(abData, abData.length);
		me = new MidiEvent(sm, 0);
		track.add(me);

		// [F7 02 F7]
		sm = new SysexMessage();
		abData = new byte[]{(byte) 0xF7, (byte) 0x02, (byte) 0xF7};
		sm.setMessage(abData, abData.length);
		me = new MidiEvent(sm, 0);
		track.add(me);

// 		// [F0 02]
// 		sm = new SysexMessage();
// 		abData = new byte[]{(byte) 0xF0, (byte) 0x02};
// 		sm.setMessage(abData, abData.length);
// 		me = new MidiEvent(sm, 0);
// 		track.add(me);

// 		// [F7 F7]
// 		sm = new SysexMessage();
// 		abData = new byte[]{(byte) 0xF7, (byte) 0xF7};
// 		sm.setMessage(abData, abData.length);
// 		me = new MidiEvent(sm, 0);
// 		track.add(me);

// 		// [F0 03]
// 		sm = new SysexMessage();
// 		abData = new byte[]{(byte) 0xF0, (byte) 0x03};
// 		sm.setMessage(abData, abData.length);
// 		me = new MidiEvent(sm, 0);
// 		track.add(me);

// 		// [F7]
// 		sm = new SysexMessage();
// 		abData = new byte[]{(byte) 0xF7};
// 		sm.setMessage(abData, abData.length);
// 		me = new MidiEvent(sm, 0);
// 		track.add(me);

		// [F7 F0 04 F7]
		sm = new SysexMessage();
		abData = new byte[]{(byte) 0xF7, (byte) 0xF0, (byte) 0x04, (byte) 0xF7};
		sm.setMessage(abData, abData.length);
		me = new MidiEvent(sm, 0);
		track.add(me);

		mm = new MetaMessage();
		mm.setMessage(0x2F, new byte[0], 0);
		me = new MidiEvent(mm, 10);
		track.add(me);

		MidiSystem.write(sequence, 0, new File(strFilename));

		/*
		 *	This is only necessary because of a bug in the Sun jdk1.3
		 */
		System.exit(0);
	}



	private static void out(String strMessage)
	{
		System.out.println(strMessage);
	}
}



/*** CreateSysexSequence.java ***/
