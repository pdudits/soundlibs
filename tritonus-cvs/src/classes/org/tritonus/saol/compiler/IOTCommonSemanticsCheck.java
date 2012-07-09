/*
 *	IOTCommonSemanticsCheck.java
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



public abstract class IOTCommonSemanticsCheck
extends IOGTCommonSemanticsCheck
{
	private static final boolean	DEBUG = true;



	public IOTCommonSemanticsCheck(NodeSemanticsTable nodeSemanticsTable)
	{
		super(nodeSemanticsTable);
	}


////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////



	public void inAIdentlistIdentlist(AIdentlistIdentlist node)
	{
	}

	public void outAIdentlistIdentlist(AIdentlistIdentlist node)
	{
	}


	public void inAIdentlistTailIdentlistTail(AIdentlistTailIdentlistTail node)
	{
	}

	public void outAIdentlistTailIdentlistTail(AIdentlistTailIdentlistTail node)
	{
	}

	public void inAParamlistParamlist(AParamlistParamlist node)
	{
	}

	public void outAParamlistParamlist(AParamlistParamlist node)
	{
	}


	public void inAParamlistTailParamlistTail(AParamlistTailParamlistTail node)
	{
	}

	public void outAParamlistTailParamlistTail(AParamlistTailParamlistTail node)
	{
	}


	public void inATablemapVardecl(ATablemapVardecl node)
	{
	}

	public void outATablemapVardecl(ATablemapVardecl node)
	{
	}



	public void outASigvarOpvardecl(ASigvarOpvardecl node)
	{
		boolean	bImports = false;
		boolean	bExports = false;
		if (node.getTaglist() != null)
		{
			NodeSemantics	taglistSemantics = getNodeSemantics(node.getTaglist());
			String	strImEx = (String) taglistSemantics.getAux();
			if (strImEx.indexOf('I') >= 0)
			{
				bImports = true;
			}
			if (strImEx.indexOf('E') >= 0)
			{
				bExports = true;
			}
			// TODO: check if matching global variable exists
		}
		int	nRate = getNodeSemantics(node.getStype()).getRate();
		if (! isLegalVariableType(nRate))
		{
			throw new RuntimeException("illegal variable type used");
		}
		List	instruments = (List) getNodeSemantics(node.getNamelist()).getAux();
		Iterator	it = instruments.iterator();
		while (it.hasNext())
		{
			VariableEntry	variable = (VariableEntry) it.next();
			variable = new VariableEntry(variable.getVariableName(),
						 variable.getWidth(),
						 nRate,
						 bImports,
						 bExports);
			getOwnVariableTable().add(variable);
		}
	}


	public void outATablevarOpvardecl(ATablevarOpvardecl node)
	{
		boolean	bImports = false;
		boolean	bExports = false;
		// for tables, this is not optional
		NodeSemantics	taglistSemantics = getNodeSemantics(node.getTaglist());
		String	strImEx = (String) taglistSemantics.getAux();
		if (strImEx.indexOf('I') >= 0)
		{
			bImports = true;
		}
		if (strImEx.indexOf('E') >= 0)
		{
			bExports = true;
		}
		// TODO: check if matching global variable exists

		int	nRate = WidthAndRate.RATE_TABLE;
		List	instruments = (List) getNodeSemantics(node.getNamelist()).getAux();
		Iterator	it = instruments.iterator();
		while (it.hasNext())
		{
			VariableEntry	variable = (VariableEntry) it.next();
			variable = new VariableEntry(variable.getVariableName(),
						 variable.getWidth(),
						 nRate,
						 bImports,
						 bExports);
			getOwnVariableTable().add(variable);
		}
	}


	public void inATableOpvardecl(ATableOpvardecl node)
	{
	}

	public void outATableOpvardecl(ATableOpvardecl node)
	{
	}


	public void inAParamdeclParamdecl(AParamdeclParamdecl node)
	{
	}

	public void outAParamdeclParamdecl(AParamdeclParamdecl node)
	{
	}


	public void inANamelistNamelist(ANamelistNamelist node)
	{
		List	list = new ArrayList();
		NodeSemantics	nodeSemantics = new NodeSemantics(list);
		setNodeSemantics(node, nodeSemantics);
	}


	public void outANamelistNamelist(ANamelistNamelist node)
	{
		VariableEntry	variableEntry = (VariableEntry) getNodeSemantics(node.getName()).getAux();
		NodeSemantics	nodeSemantics = getNodeSemantics(node);
		List	list = (List) nodeSemantics.getAux();
		list.add(variableEntry);
	}


	public void outANamelistTailNamelistTail(ANamelistTailNamelistTail node)
	{
		VariableEntry	variableEntry = (VariableEntry) getNodeSemantics(node.getName()).getAux();
		NodeSemantics	nodeSemantics = getNodeSemantics(node.parent());
		List	list = (List) nodeSemantics.getAux();
		list.add(variableEntry);
	}


	public void outASimpleName(ASimpleName node)
	{
		String	strVariableName = node.getIdentifier().getText();
		handleName(node, strVariableName, 1);
	}



	public void outAIndexedName(AIndexedName node)
	{
		String	strVariableName = node.getIdentifier().getText();
		String	strInteger = node.getInteger().getText();
		int	nInteger = Integer.parseInt(strInteger);
		handleName(node, strVariableName, nInteger);
	}



	public void outAInchannelsName(AInchannelsName node)
	{
		String	strVariableName = node.getIdentifier().getText();
		handleName(node, strVariableName, WidthAndRate.WIDTH_INCHANNELS);
	}


	public void outAOutchannelsName(AOutchannelsName node)
	{
		String	strVariableName = node.getIdentifier().getText();
		handleName(node, strVariableName, WidthAndRate.WIDTH_OUTCHANNELS);
	}


	// TODO: check if gathering of variable name can be generalized
	private void handleName(Node node, String strVariableName, int nWidth)
	{
		VariableEntry	variableEntry = new VariableEntry(strVariableName, nWidth, WidthAndRate.RATE_UNKNOWN, false, false);
		NodeSemantics	nodeSemantics = new NodeSemantics(variableEntry);
		setNodeSemantics(node, nodeSemantics);
	}


	public void outAIvarStype(AIvarStype node)
	{
		NodeSemantics	nodeSemantics = new NodeSemantics(WidthAndRate.WIDTH_UNKNOWN, WidthAndRate.RATE_I);
		setNodeSemantics(node, nodeSemantics);
	}


	public void outAKsigStype(AKsigStype node)
	{
		NodeSemantics	nodeSemantics = new NodeSemantics(WidthAndRate.WIDTH_UNKNOWN, WidthAndRate.RATE_K);
		setNodeSemantics(node, nodeSemantics);
	}


	public void outAAsigStype(AAsigStype node)
	{
		NodeSemantics	nodeSemantics = new NodeSemantics(WidthAndRate.WIDTH_UNKNOWN, WidthAndRate.RATE_A);
		setNodeSemantics(node, nodeSemantics);
	}


	public void outATableStype(ATableStype node)
	{
		NodeSemantics	nodeSemantics = new NodeSemantics(WidthAndRate.WIDTH_UNKNOWN, WidthAndRate.RATE_TABLE);
		setNodeSemantics(node, nodeSemantics);
	}


	public void outAOparrayStype(AOparrayStype node)
	{
		NodeSemantics	nodeSemantics = new NodeSemantics(WidthAndRate.WIDTH_UNKNOWN, WidthAndRate.RATE_OPARRAY);
		setNodeSemantics(node, nodeSemantics);
	}


	public void outAXsigOtype(AXsigOtype node)
	{
		NodeSemantics	nodeSemantics = new NodeSemantics(WidthAndRate.WIDTH_UNKNOWN, WidthAndRate.RATE_X);
		setNodeSemantics(node, nodeSemantics);
	}


	public void outAStypeOtype(AStypeOtype node)
	{
		NodeSemantics	nodeSemantics = getNodeSemantics(node.getStype());
		setNodeSemantics(node, nodeSemantics);
	}



	public void inATabledeclTabledecl(ATabledeclTabledecl node)
	{
	}

	public void outATabledeclTabledecl(ATabledeclTabledecl node)
	{
	}


	public void outAImportsTaglist(AImportsTaglist node)
	{
		NodeSemantics	nodeSemantics = new NodeSemantics("I");
		setNodeSemantics(node, nodeSemantics);
	}

	public void outAExportsTaglist(AExportsTaglist node)
	{
		NodeSemantics	nodeSemantics = new NodeSemantics("E");
		setNodeSemantics(node, nodeSemantics);
	}


	public void outAImportsexportsTaglist(AImportsexportsTaglist node)
	{
		NodeSemantics	nodeSemantics = new NodeSemantics("IE");
		setNodeSemantics(node, nodeSemantics);
	}

	public void outAExportsimportsTaglist(AExportsimportsTaglist node)
	{
		NodeSemantics	nodeSemantics = new NodeSemantics("IE");
		setNodeSemantics(node, nodeSemantics);
	}


	public void inAAopcodeOptype(AAopcodeOptype node)
	{
	}

	public void outAAopcodeOptype(AAopcodeOptype node)
	{
	}


	public void inAKopcodeOptype(AKopcodeOptype node)
	{
	}

	public void outAKopcodeOptype(AKopcodeOptype node)
	{
	}


	public void inAIopcodeOptype(AIopcodeOptype node)
	{
	}

	public void outAIopcodeOptype(AIopcodeOptype node)
	{
	}


	public void inAOpcodeOptype(AOpcodeOptype node)
	{
	}

	public void outAOpcodeOptype(AOpcodeOptype node)
	{
	}



	public void inAAltExpr(AAltExpr node)
	{
		// TODO:
	}


	public void outAAltExpr(AAltExpr node)
	{
		// TODO:
	}




	public void outAOrOrexpr(AOrOrexpr node)
	{
		// TODO:
	}



	public void outAAndAndexpr(AAndAndexpr node)
	{
		// TODO:
	}



	public void outANeqEqualityexpr(ANeqEqualityexpr node)
	{
// 		BranchInstruction	branch = new IFNE(null);
// 		m_aMethods[METHOD_A].appendRelationalOperation(branch);
	}



	public void outAEqEqualityexpr(AEqEqualityexpr node)
	{
// 		BranchInstruction	branch = new IFEQ(null);
// 		m_aMethods[METHOD_A].appendRelationalOperation(branch);
	}



	public void inAGtRelationalexpr(AGtRelationalexpr node)
	{
	}

	public void outAGtRelationalexpr(AGtRelationalexpr node)
	{
// 		BranchInstruction	branch = new IFGT(null);
// 		m_aMethods[METHOD_A].appendRelationalOperation(branch);
	}



	public void outALtRelationalexpr(ALtRelationalexpr node)
	{
// 		BranchInstruction	branch = new IFLT(null);
// 		m_aMethods[METHOD_A].appendRelationalOperation(branch);
	}



	public void outALteqRelationalexpr(ALteqRelationalexpr node)
	{
// 		BranchInstruction	branch = new IFLE(null);
// 		m_aMethods[METHOD_A].appendRelationalOperation(branch);
	}



	public void outAGteqRelationalexpr(AGteqRelationalexpr node)
	{
// 		BranchInstruction	branch = new IFGE(null);
// 		m_aMethods[METHOD_A].appendRelationalOperation(branch);
	}



	public void outAPlusAddexpr(APlusAddexpr node)
	{
// 		m_aMethods[METHOD_A].appendInstruction(InstructionConstants.FADD);
	}



	public void outAMinusAddexpr(AMinusAddexpr node)
	{
// 		m_aMethods[METHOD_A].appendInstruction(InstructionConstants.FSUB);
	}



	public void outAMultFactor(AMultFactor node)
	{
// 		m_aMethods[METHOD_A].appendInstruction(InstructionConstants.FMUL);
	}



	public void outADivFactor(ADivFactor node)
	{
// 		m_aMethods[METHOD_A].appendInstruction(InstructionConstants.FDIV);
	}



	public void outANotUnaryminusterm(ANotUnaryminusterm node)
	{
// 		m_aMethods[METHOD_A].appendInstruction(InstructionConstants.FNEG);
	}



	public void outANotNotterm(ANotNotterm node)
	{
// 		m_aMethods[METHOD_A].appendInstruction(InstructionConstants.FCONST_0);
// 		m_aMethods[METHOD_A].appendInstruction(InstructionConstants.FCMPL);
// 		BranchInstruction	branch0 = new IFNE(null);
// 		m_aMethods[METHOD_A].appendInstruction(branch0);
// 		m_aMethods[METHOD_A].appendInstruction(InstructionConstants.FCONST_1);
// 		BranchInstruction	branch1 = new GOTO(null);
// 		m_aMethods[METHOD_A].appendInstruction(branch1);
// 		m_aMethods[METHOD_A].setPendingBranchInstruction(branch0);
// 		m_aMethods[METHOD_A].appendInstruction(InstructionConstants.FCONST_0);
// 		m_aMethods[METHOD_A].setPendingBranchInstruction(branch1);
	}



	public void outAIdentifierTerm(AIdentifierTerm node)
	{
// 		String	strVariableName = node.getIdentifier().getText();
// 		m_aMethods[METHOD_A].appendGetField(strVariableName);
	}


	public void outAConstantTerm(AConstantTerm node)
	{
// 		Object	constant = getNodeAttribute(node.getConst());
// 		if (constant instanceof Integer ||
// 		    constant instanceof Float)
// 		{
// 			float	fValue = ((Number) constant).floatValue();
// 			m_aMethods[METHOD_A].appendFloatConstant(fValue);
// 		}
// 		else
// 		{
// 			throw new RuntimeException("constant is neither int nor float");
// 		}
	}



	public void inAIndexedTerm(AIndexedTerm node)
	{
// 		// push the array reference onto the stack
// 		String	strVariableName = node.getIdentifier().getText();
// 		m_aMethods[METHOD_A].appendGetField(strVariableName);
	}

	public void outAIndexedTerm(AIndexedTerm node)
	{
// 		/*	The array reference still is on the stack. Now,
// 			also the array index (as a float) is on the stack.
// 			It has to be transformed to integer.
// 		*/
// 		// TODO: correct rounding (1.5 -> 2.0)
// 		m_aMethods[METHOD_A].appendInstruction(InstructionConstants.F2I);
// 		// and now fetch the value from the array
// 		setNodeAttribute(node, InstructionConstants.FALOAD);
	}



	public void inASasbfTerm(ASasbfTerm node)
	{
	}

	public void outASasbfTerm(ASasbfTerm node)
	{
	}


	public void inAFunctionTerm(AFunctionTerm node)
	{
	}

	public void outAFunctionTerm(AFunctionTerm node)
	{
	}


	public void inAIndexedfunctionTerm(AIndexedfunctionTerm node)
	{
	}

	public void outAIndexedfunctionTerm(AIndexedfunctionTerm node)
	{
	}



	public void inAExprlistExprlist(AExprlistExprlist node)
	{
	}


	public void outAExprlistExprlist(AExprlistExprlist node)
	{
	}



	public void inAExprlistTailExprlistTail(AExprlistTailExprlistTail node)
	{
	}

	public void outAExprlistTailExprlistTail(AExprlistTailExprlistTail node)
	{
	}


	public void inAExprstrlistExprstrlist(AExprstrlistExprstrlist node)
	{
	}

	public void outAExprstrlistExprstrlist(AExprstrlistExprstrlist node)
	{
	}


	public void inAExprstrlistTailExprstrlistTail(AExprstrlistTailExprstrlistTail node)
	{
	}

	public void outAExprstrlistTailExprstrlistTail(AExprstrlistTailExprstrlistTail node)
	{
	}


	public void inAExprExprOrString(AExprExprOrString node)
	{
	}

	public void outAExprExprOrString(AExprExprOrString node)
	{
	}


	public void inAStringExprOrString(AStringExprOrString node)
	{
	}

	public void outAStringExprOrString(AStringExprOrString node)
	{
	}


	public void inAIntegerConst(AIntegerConst node)
	{
	}

	public void outAIntegerConst(AIntegerConst node)
	{
// 		String	strInteger = node.getInteger().getText();
// 		Integer	integer = new Integer(strInteger);
// 		setNodeAttribute(node, integer);
	}


// 	public void inANumberConst(ANumberConst node)
// 	{
// 	}



	public void outANumberConst(ANumberConst node)
	{
// 		String	strNumber = node.getNumber().getText();
// 		Float	number = new Float(strNumber);
// 		setNodeAttribute(node, number);
	}





////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////



}



/*** IOTCommonSemanticsCheck.java ***/
