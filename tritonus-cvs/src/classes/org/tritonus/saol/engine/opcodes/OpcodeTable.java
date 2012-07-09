/*
 *	OpcodeTable.java
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

import java.util.HashMap;
import java.util.Map;



/**	The opcode table.
	TODO: use generics
 */
public class OpcodeTable
{
	/**	Map that holds the opcode entries.
		Key: the name of the opcode.
		Value: a OpcodeEntry instance.
	*/
	private Map		m_opcodeMap;


	public OpcodeTable()
	{
		m_opcodeMap = new HashMap();
		buildOpcodeTable();
	}


	private void buildOpcodeTable()
	{
		PitchOpcodes.buildOpcodeTable(this);
		MathOpcodes.buildOpcodeTable(this);
	}


	public void addEntry(OpcodeEntry opcodeEntry)
	{
		m_opcodeMap.put(opcodeEntry.getOpcodeName(), opcodeEntry);
	}


	public OpcodeEntry getOpcode(String strOpcodeName)
	{
		return (OpcodeEntry) m_opcodeMap.get(strOpcodeName);
	}
}



/*** OpcodeTable.java ***/
