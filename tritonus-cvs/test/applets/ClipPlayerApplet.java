/*
 *	ClipPlayerApplet.java
 */

/*
 *  Copyright (c) 1999 by Matthias Pfisterer <Matthias.Pfisterer@gmx.de>
 *
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
 *
 */

import	java.awt.event.ActionEvent;
import	java.awt.event.ActionListener;

import	java.io.IOException;

import	java.net.URL;
import	java.net.MalformedURLException;

import	javax.media.sound.sampled.AudioFormat;
import	javax.media.sound.sampled.AudioInputStream;
import	javax.media.sound.sampled.AudioSystem;
import	javax.media.sound.sampled.Clip;
import	javax.media.sound.sampled.DataLine;
import	javax.media.sound.sampled.LineEvent;
import	javax.media.sound.sampled.LineListener;
import	javax.media.sound.sampled.LineUnavailableException;

import	javax.swing.JApplet;
import	javax.swing.JButton;
import	javax.swing.JPanel;


public class ClipPlayerApplet
	extends	JApplet
	implements	LineListener
{
	private AudioInputStream	m_audioInputStream;
	private AudioFormat		m_format;
	private Clip		m_clip;

	private JPanel		m_panel;
	private JButton		m_loopButton;
	private JButton		m_stopButton;


	public ClipPlayerApplet()
	{
	}



	public void init()
	{
		System.out.println("ClipPlayerApplet.init(): context class loader: " + Thread.currentThread().getContextClassLoader());
		System.out.println("ClipPlayerApplet.init(): system class loader: " + ClassLoader.getSystemClassLoader());
		String	strClipURL = getParameter("clipurl");
		System.out.println("URL str: " + strClipURL);
		URL	clipURL = null;
		try
		{
			clipURL = new URL(getDocumentBase(), strClipURL);
		}
		catch (MalformedURLException e)
		{
			e.printStackTrace();
		}
		System.out.println("URL: " + clipURL);
		loadClip(clipURL);
		JPanel	panel = new JPanel();
		this.getContentPane().add(panel);
		// TODO: label showing the url
		m_loopButton = new JButton("Loop");
		m_loopButton.addActionListener(new ActionListener()
					       {
						       public void actionPerformed(ActionEvent ae)
							       {
								       m_clip.loop(Clip.LOOP_CONTINUOUSLY);
							       }
					       });
		panel.add(m_loopButton);
		m_stopButton = new JButton("Stop");
		m_stopButton.addActionListener(new ActionListener()
					       {
						       public void actionPerformed(ActionEvent ae)
							       {
								       m_clip.loop(0);
							       }
					       });
		m_stopButton.setEnabled(false);
		panel.add(m_stopButton);
	}



	public void destroy()
	{
		if (m_clip != null)
		{
			m_clip.close();
		}
	}



	private void loadClip(URL clipURL)
	{
		System.out.println("ClipPlayerApplet.loadClip(): setting another class loader");
		ClassLoader	originalClassLoader = Thread.currentThread().getContextClassLoader();
		Thread.currentThread().setContextClassLoader(ClassLoader.getSystemClassLoader());
		try
		{
			m_audioInputStream = AudioSystem.getAudioInputStream(clipURL);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		if (m_audioInputStream != null)
		{
			m_format = m_audioInputStream.getFormat();
			DataLine.Info	info = new DataLine.Info(Clip.class, null, null, null, m_format, AudioSystem.NOT_SPECIFIED);
			try
			{
				m_clip = (Clip) AudioSystem.getLine(info);
				m_clip.addLineListener(this);
				m_clip.open(m_audioInputStream);
			}
			catch (LineUnavailableException e)
			{
				e.printStackTrace();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			// m_clip.loop(nLoopCount);
		}
		else
		{
			// TODO: popup (also for other error conditions)
			System.out.println("ClipPlayerApplet.<init>(): can't get data from URL " + clipURL);
		}
		Thread.currentThread().setContextClassLoader(originalClassLoader);
		System.out.println("ClipPlayerApplet.loadClip(): restored the original class loader");
	}



	public void update(LineEvent event)
	{
		System.out.println("ClipPlayerApplet.update(): received event: " + event);
		if (event.getType().equals(LineEvent.START))
		{
			m_loopButton.setEnabled(false);
			m_stopButton.setEnabled(true);
		}
		if (event.getType().equals(LineEvent.STOP))
		{
			m_loopButton.setEnabled(true);
			m_stopButton.setEnabled(false);
		}
	}


}



/*** ClipPlayerApplet.java ***/
