/*
 *	OpcodeEntry.java
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

package org.tritonus.saol.engine.opcodes;


/**	Representation of one opcode implementation.
	This class is used for entries in the opcode table.
 */
public class OpcodeEntry
{
	private String		m_strOpcodeName;
	private OpcodeClass	m_opcodeClass;
	private String		m_strMethodName;
	private int		m_nRate;
	// TODO: parameter description, including dummy params



	// if opcode and method name are the same
	public OpcodeEntry(String strOpcodeName,
			   OpcodeClass opcodeClass,
			   int nRate)
	{
		this(strOpcodeName,
		     opcodeClass,
		     strOpcodeName,
		     nRate);
	}


	public OpcodeEntry(String strOpcodeName,
			   OpcodeClass opcodeClass,
			   String strMethodName,
			   int nRate)
	{
		m_strOpcodeName = strOpcodeName;
		m_opcodeClass = opcodeClass;
		m_strMethodName = strMethodName;
		m_nRate = nRate;
	}


	public String getOpcodeName()
	{
		return m_strOpcodeName;
	}


	public OpcodeClass getOpcodeClass()
	{
		return m_opcodeClass;
	}


	public String getMethodName()
	{
		return m_strMethodName;
	}


	public int getRate()
	{
		return m_nRate;
	}
}



/*** OpcodeEntry.java ***/
