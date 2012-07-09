/*
 *	FileOutput.java
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

import java.io.IOException;
import java.io.File;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;


/*
 *      Tritonus classes.
 *      Using these makes the program not portable to other
 *      Java Sound implementations.
 */
import org.tritonus.share.sampled.AudioSystemShadow;
import  org.tritonus.share.sampled.file.AudioOutputStream;



public class FileOutput
extends AudioOutputStreamOutput
{
	public FileOutput(File outputFile,
					  AudioFileFormat.Type targetType,
					  AudioFormat audioFormat)
		throws IOException
	{
		super(AudioSystemShadow.getAudioOutputStream(
				  targetType,
				  audioFormat,
				  AudioSystem.NOT_SPECIFIED,
				  outputFile));
	}
}



/*** FileOutput.java ***/
