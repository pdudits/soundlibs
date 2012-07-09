/*
 *	GlobalSemanticsCheck.java
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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.tritonus.saol.sablecc.analysis.*;
import org.tritonus.saol.sablecc.node.*;



public class GlobalSemanticsCheck
extends IOGTCommonSemanticsCheck
{
	private static final boolean	DEBUG = true;
	private static final int[]	LEGAL_VARIABLE_TYPES = new int[]
	{
		WidthAndRate.RATE_I,
		WidthAndRate.RATE_K,
		WidthAndRate.RATE_A,
		WidthAndRate.RATE_OPARRAY,
	};

	private VariableTable		m_globalVariableTable;



	public GlobalSemanticsCheck(VariableTable globalVariableTable,
				    NodeSemanticsTable nodeSemanticsTable)
	{
		super(nodeSemanticsTable);
		m_globalVariableTable = globalVariableTable;
	}



////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////


	public void inAGlobaldeclGlobaldecl(AGlobaldeclGlobaldecl node)
	{
	}


	public void outAGlobaldeclGlobaldecl(AGlobaldeclGlobaldecl node)
	{
	}



    public void inARtparamGlobaldef(ARtparamGlobaldef node)
    {
    }

    public void outARtparamGlobaldef(ARtparamGlobaldef node)
    {
    }


    public void inARoutedefGlobaldef(ARoutedefGlobaldef node)
    {
    }

    public void outARoutedefGlobaldef(ARoutedefGlobaldef node)
    {
    }


    public void inASenddefGlobaldef(ASenddefGlobaldef node)
    {
    }

    public void outASenddefGlobaldef(ASenddefGlobaldef node)
    {
    }


    public void inASeqdefGlobaldef(ASeqdefGlobaldef node)
    {
    }

    public void outASeqdefGlobaldef(ASeqdefGlobaldef node)
    {
    }



////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////



	protected VariableTable getOwnVariableTable()
	{
		return m_globalVariableTable;
	}


	protected VariableTable getGlobalVariableTable()
	{
		return null;
	}



	protected int[] getLegalVariableTypes()
	{
		return LEGAL_VARIABLE_TYPES;
	}
}



/*** GlobalSemanticsCheck.java ***/
