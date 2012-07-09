/*
 *	InstrumentSemanticsCheck.java
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



public class InstrumentSemanticsCheck
extends IOTCommonSemanticsCheck
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
	private VariableTable		m_localVariableTable;



	public InstrumentSemanticsCheck(VariableTable globalVariableTable,
					VariableTable localVariableTable,
					NodeSemanticsTable nodeSemanticsTable)
	{
		super(nodeSemanticsTable);
		m_globalVariableTable = globalVariableTable;
		m_localVariableTable = localVariableTable;
	}


////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////


	public void inAInstrdeclInstrdecl(AInstrdeclInstrdecl node)
	{
// 		String	strInstrumentName = node.getIdentifier().getText();
// 		m_strClassName = PACKAGE_PREFIX + strInstrumentName;
// 		m_classGen = new ClassGen(m_strClassName,
// 					  SUPERCLASS_NAME,
// 					  "<generated>",
// 					  Constants.ACC_PUBLIC | Constants.ACC_SUPER,
// 					  null);
// 		m_constantPoolGen = m_classGen.getConstantPool();
// 		m_instructionFactory = new InstructionFactory(m_constantPoolGen);
// 		m_aMethods[METHOD_CONSTR] = new InstrumentMethod(m_classGen, "<init>");
// 		m_aMethods[METHOD_I] = new InstrumentMethod(m_classGen, "doIPass");
// 		m_aMethods[METHOD_K] = new InstrumentMethod(m_classGen, "doKPass");
// 		m_aMethods[METHOD_A] = new InstrumentMethod(m_classGen, "doAPass");
// 		m_aMethods[METHOD_CONSTR].appendInstruction(InstructionConstants.ALOAD_0);
// 		Instruction	invokeSuperInstruction = m_instructionFactory.createInvoke(SUPERCLASS_NAME, "<init>", Type.VOID, Type.NO_ARGS, Constants.INVOKESPECIAL);
// //		Instruction	invokeSuperInstruction = m_instructionFactory.createInvoke(SUPERCLASS_NAME, SUPERCLASS_CONSTRUCTOR_NAME, Type.VOID, Type.NO_ARGS, Constants.INVOKESPECIAL);
// 		m_aMethods[METHOD_CONSTR].appendInstruction(invokeSuperInstruction);
	}



	public void outAInstrdeclInstrdecl(AInstrdeclInstrdecl node)
	{
// 		for (int i = 0; i < m_aMethods.length; i++)
// 		{
// 			m_aMethods[i].finish();
// 		}
// 		JavaClass	javaClass = m_classGen.getJavaClass();
// 		try
// 		{
// 			ByteArrayOutputStream	baos = new ByteArrayOutputStream();
// 			javaClass.dump(baos);
// 			byte[]	abData = baos.toByteArray();
// 			Class	instrumentClass = m_classLoader.findClass(m_strClassName, abData);
// 			m_instrumentMap.put(m_strClassName, instrumentClass);
// 			if (DEBUG)
// 			{
// 				javaClass.dump(m_strClassName + CLASSFILENAME_SUFFIX);
// 			}
// 		}
// 		catch (IOException e)
// 		{
// 			e.printStackTrace();
// 		}
	}


	public void inAMiditagMiditag(AMiditagMiditag node)
	{
	}

	public void outAMiditagMiditag(AMiditagMiditag node)
	{
	}


	public void inAIntListIntList(AIntListIntList node)
	{
	}

	public void outAIntListIntList(AIntListIntList node)
	{
	}






////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////



	protected VariableTable getOwnVariableTable()
	{
		return m_localVariableTable;
	}


	protected VariableTable getGlobalVariableTable()
	{
		return m_globalVariableTable;
	}


	protected int[] getLegalVariableTypes()
	{
		return LEGAL_VARIABLE_TYPES;
	}
}



/*** InstrumentSemanticsCheck.java ***/
