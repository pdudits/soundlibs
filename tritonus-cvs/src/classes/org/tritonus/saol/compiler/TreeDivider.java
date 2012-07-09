/*
 *	TreeDivider.java
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

package org.tritonus.saol.compiler;

import org.tritonus.share.TDebug;
import org.tritonus.saol.sablecc.analysis.*;
import org.tritonus.saol.sablecc.node.*;



public class TreeDivider
extends DepthFirstAdapter
{
	private static final boolean	DEBUG = true;

	private InstrumentTable		m_instrumentTable;
	private UserOpcodeTable		m_opcodeTable;
	private TemplateTable		m_templateTable;
	private AGlobaldeclGlobaldecl	m_globalNode;



	public TreeDivider(InstrumentTable instrumentTable,
					   UserOpcodeTable opcodeTable,
					   TemplateTable templateTable)
	{
		m_instrumentTable = instrumentTable;
		m_opcodeTable = opcodeTable;
		m_templateTable = templateTable;
		m_globalNode = null;
	}




	public AGlobaldeclGlobaldecl getGlobalNode()
	{
		return m_globalNode;
	}



	public void inAInstrdeclInstrdecl(AInstrdeclInstrdecl node)
	{
		String	strInstrumentName = node.getIdentifier().getText();
		InstrumentEntry	instrument = new InstrumentEntry(strInstrumentName, node);
		m_instrumentTable.add(instrument);
	}



	public void inAOpcodedeclOpcodedecl(AOpcodedeclOpcodedecl node)
	{
		String	strOpcodeName = node.getIdentifier().getText();
		UserOpcodeEntry	opcode = new UserOpcodeEntry(strOpcodeName, node);
		m_opcodeTable.add(opcode);
	}



	public void inAGlobaldeclGlobaldecl(AGlobaldeclGlobaldecl node)
	{
		TDebug.out("TreeDivider.inAGlobaldeclGlobaldecl()");
		m_globalNode = node;
	}



	public void inATemplatedeclTemplatedecl(ATemplatedeclTemplatedecl node)
	{
		// hack to make compile
		String	strTemplateName = "---";
		// String	strTemplateName = node.getIdentifier().getText();
		TemplateEntry	template = new TemplateEntry(strTemplateName, node);
		m_templateTable.add(template);
	}
}



/*** TreeDivider.java ***/
