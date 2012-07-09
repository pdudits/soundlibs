/*
 *	Main.java
 *
 *	This file is part of Tritonus: http://www.tritonus.org/
 */

/*
 *  Copyright (c) 2002 by Matthias Pfisterer
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

package org.tritonus.saol.engine;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import java.util.Map;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioFileFormat;

import org.tritonus.share.TDebug;
import org.tritonus.saol.compiler.Compiler;



public class Main
{
	public static void main(String[] args)
		throws IOException
	{
		File	saolFile = new File(args[0]);
		File	saslFile = new File(args[1]);
		File	outputFile = new File(args[2]);
		Compiler	compiler = new Compiler(saolFile);
		try
		{
			compiler.compile();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		Map	instrumentMap = compiler.getInstrumentMap();
		TDebug.out("Main.main(): IM: " + instrumentMap);
		AudioFileFormat.Type	targetType = AudioFileFormat.Type.WAVE;
		AudioFormat		audioFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 44100.0F, 16, 2, 4, 44100.0F, false);
		SystemOutput	output = new FileOutput(outputFile, targetType, audioFormat);
		RTSystem	rtSystem = new RTSystem(output, instrumentMap);
		rtSystem.start();
		InputStream	saslInputStream = new FileInputStream(saslFile);
		SaslParser	saslParser = new SaslParser(rtSystem, saslInputStream);
		new Thread(saslParser).start();
	}
}



/*** Main.java ***/
