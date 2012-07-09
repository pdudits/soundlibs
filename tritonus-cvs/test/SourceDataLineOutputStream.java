/*
 *	SourceDataLineOutputStream.java
 *
 *	Helper class for saint.
 */

import	java.io.InputStream;
import	java.io.IOException;
import	java.io.FileInputStream;
import	java.io.FileOutputStream;
import	java.io.OutputStream;

import	javax.sound.sampled.SourceDataLine;



public class SourceDataLineOutputStream
extends	OutputStream
{
	private static final boolean	DEBUG = true;

	private SourceDataLine	m_line;


	public SourceDataLineOutputStream(SourceDataLine line)
	{
		m_line = line;
	}


	public void write(int nByte)
	{
		if (DEBUG)
		{
			System.err.println("SourceDataLineOutputStream.write(int): called");
		}
		byte[]	abOneByte = new byte[1];
		abOneByte[0] = (byte) nByte;
		m_line.write(abOneByte, 0, 1);
	}



	public void write(byte[] abBuffer, int nOffset, int nLength)
		throws	IOException
	{
		if (DEBUG)
		{
			System.err.println("SourceDataLineOutputStream.write(byte[], int, int): called");
		}
		int	nWritten = m_line.write(abBuffer, nOffset, nLength);
		if (DEBUG)
		{
			System.err.println("SourceDataLineOutputStream.write(byte[], int, int): written: " + nWritten);
		}
	}


	public void flush()
	{
		if (DEBUG)
		{
			System.err.println("SourceDataLineOutputStream.flush(): called");
		}
		// m_line.drain();
	}
}



/*** SourceDataLineOutputStream.java ***/
