/*
 *	Line.java
 *
 *	This file is part of Tritonus: http://www.tritonus.org/
 */

/*
 *  Copyright (c) 1999 by Matthias Pfisterer
 *
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
 *
 */

/*
|<---            this code is formatted to fit into 80 columns             --->|
*/

package javax.sound.sampled;



public interface Line
{
	public Line.Info getLineInfo();



	public void open()
		throws LineUnavailableException;



	public void close();



	public boolean isOpen();



	public Control[] getControls();



	public boolean isControlSupported(Control.Type controlType);



	public Control getControl(Control.Type controlType);



	public void addLineListener(LineListener listener);



	public void removeLineListener(LineListener listener);




	public static class Info
	{
		private Class		m_lineClass;
	



		public Info(Class lineClass)
		{
			m_lineClass = lineClass;
		}



		public Class getLineClass()
		{
			return m_lineClass;
		}



		public boolean matches(Line.Info info)
		{
			return this.getLineClass() == info.getLineClass();
		}



		public String toString()
		{
			return super.toString() + "[lineClass=" + getLineClass() + "]";
		}
	}
}


/*** Line.java ***/
