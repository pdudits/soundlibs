/*
 *	TNonSeekableDataOutputStreamTestCase.java
 */

/*
 *  Copyright (c) 2004 by Matthias Pfisterer
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

package org.tritonus.test.tritonus.share.sampled.file;

import java.io.ByteArrayOutputStream;

import org.tritonus.share.sampled.file.TNonSeekableDataOutputStream;
import org.tritonus.share.sampled.file.TDataOutputStream;




public class TNonSeekableDataOutputStreamTestCase
extends BaseDataOutputStreamTestCase
{
	ByteArrayOutputStream	m_baos;


	public TNonSeekableDataOutputStreamTestCase(String strName)
	{
		super(strName,
			  false);  // non seekable
	}


	protected TDataOutputStream createDataOutputStream()
		throws Exception
	{
		m_baos = new ByteArrayOutputStream();
		return new TNonSeekableDataOutputStream(m_baos);
	}


	protected byte[] getWrittenData()
		throws Exception
	{
		return m_baos.toByteArray();
	}
}



/*** TNonSeekableDataOutputStreamTestCase.java ***/
