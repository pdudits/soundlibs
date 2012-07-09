/*
 *	InstrumentCompilation.java
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import java.util.HashMap;
import java.util.Map;

import org.apache.bcel.Constants;
import org.apache.bcel.classfile.Field;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.generic.*;

import org.tritonus.saol.sablecc.analysis.*;
import org.tritonus.saol.sablecc.node.*;



public class InstrumentCompilation
extends DepthFirstAdapter
{
	private static final boolean	DEBUG = true;

	// may become "org.tritonus.saol.generated."
	private static final String	PACKAGE_PREFIX = "";
	private static final String	CLASSFILENAME_PREFIX = "src/";
	private static final String	CLASSFILENAME_SUFFIX = ".class";
	private static final String	SUPERCLASS_NAME = "org.tritonus.saol.engine.AbstractInstrument";
	private static final String	SUPERCLASS_CONSTRUCTOR_NAME = "AbstractInstrument";

	private static final int	METHOD_CONSTR = WidthAndRate.RATE_UNKNOWN;
	private static final int	METHOD_I = WidthAndRate.RATE_I;
	private static final int	METHOD_K = WidthAndRate.RATE_K;
	private static final int	METHOD_A = WidthAndRate.RATE_A;

	private static final Type	FLOAT_ARRAY = new ArrayType(Type.FLOAT, 1);



	private SAOLGlobals		m_saolGlobals;

	// maps instrument names (String) to classes (Class)
	private Map			m_instrumentMap;
	private Map			m_nodeAttributes;
	private String			m_strClassName;
	private ClassGen		m_classGen;
	private ConstantPoolGen		m_constantPoolGen;
	// private MethodGen		m_methodGen;
	// private InstructionList		m_instructionList;
	private InstructionFactory	m_instructionFactory;
	// private BranchInstruction	m_pendingBranchInstruction;

	// TODO: should be made obsolete by using node attributes
	private boolean			m_bOpvardecls;
	private MemoryClassLoader	m_classLoader = new MemoryClassLoader();

	// 0: constructor
	// 1: doIPass()
	// 2: doKPass()
	// 3: doAPass()
	private InstrumentMethod[]	m_aMethods;


	public InstrumentCompilation(SAOLGlobals saolGlobals,
				     Map instrumentMap)
	{
		m_saolGlobals = saolGlobals;
		m_instrumentMap = instrumentMap;
		m_nodeAttributes = new HashMap();
		m_aMethods = new InstrumentMethod[4];
	}




	public void inAInstrdeclInstrdecl(AInstrdeclInstrdecl node)
	{
		String	strInstrumentName = node.getIdentifier().getText();
		m_strClassName = PACKAGE_PREFIX + strInstrumentName;
		m_classGen = new ClassGen(m_strClassName,
					  SUPERCLASS_NAME,
					  "<generated>",
					  Constants.ACC_PUBLIC | Constants.ACC_SUPER,
					  null);
		m_constantPoolGen = m_classGen.getConstantPool();
		m_instructionFactory = new InstructionFactory(m_constantPoolGen);
		m_aMethods[METHOD_CONSTR] = new InstrumentMethod(m_classGen, "<init>");
		m_aMethods[METHOD_I] = new InstrumentMethod(m_classGen, "doIPass");
		m_aMethods[METHOD_K] = new InstrumentMethod(m_classGen, "doKPass");
		m_aMethods[METHOD_A] = new InstrumentMethod(m_classGen, "doAPass");
		m_aMethods[METHOD_CONSTR].appendInstruction(InstructionConstants.ALOAD_0);
		Instruction	invokeSuperInstruction = m_instructionFactory.createInvoke(SUPERCLASS_NAME, "<init>", Type.VOID, Type.NO_ARGS, Constants.INVOKESPECIAL);
//		Instruction	invokeSuperInstruction = m_instructionFactory.createInvoke(SUPERCLASS_NAME, SUPERCLASS_CONSTRUCTOR_NAME, Type.VOID, Type.NO_ARGS, Constants.INVOKESPECIAL);
		m_aMethods[METHOD_CONSTR].appendInstruction(invokeSuperInstruction);
	}



	public void outAInstrdeclInstrdecl(AInstrdeclInstrdecl node)
	{
		for (int i = 0; i < m_aMethods.length; i++)
		{
			m_aMethods[i].finish();
		}
		JavaClass	javaClass = m_classGen.getJavaClass();
		try
		{
			ByteArrayOutputStream	baos = new ByteArrayOutputStream();
			javaClass.dump(baos);
			byte[]	abData = baos.toByteArray();
			Class	instrumentClass = m_classLoader.findClass(m_strClassName, abData);
			m_instrumentMap.put(m_strClassName, instrumentClass);
			if (DEBUG)
			{
				javaClass.dump(m_strClassName + CLASSFILENAME_SUFFIX);
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

//     public void caseAInstrdeclInstrdecl(AInstrdeclInstrdecl node)
//     {
//         inAInstrdeclInstrdecl(node);
//         if(node.getInstr() != null)
//         {
//             node.getInstr().apply(this);
//         }
//         if(node.getIdentifier() != null)
//         {
//             node.getIdentifier().apply(this);
//         }
//         if(node.getLPar() != null)
//         {
//             node.getLPar().apply(this);
//         }
//         if(node.getIdentlist() != null)
//         {
//             node.getIdentlist().apply(this);
//         }
//         if(node.getRPar() != null)
//         {
//             node.getRPar().apply(this);
//         }
//         if(node.getLBrace() != null)
//         {
//             node.getLBrace().apply(this);
//         }
//         {
//             Object temp[] = node.getOpvardecl().toArray();
//             for(int i = 0; i < temp.length; i++)
//             {
//                 ((POpvardecl) temp[i]).apply(this);
//             }
//         }
//         if(node.getBlock() != null)
//         {
//             node.getBlock().apply(this);
//         }
//         if(node.getRBrace() != null)
//         {
//             node.getRBrace().apply(this);
//         }
//         outAInstrdeclInstrdecl(node);
//     }






	public void inABlockBlock(ABlockBlock node)
	{
	}

	public void outABlockBlock(ABlockBlock node)
	{
	}

//     public void caseABlockBlock(ABlockBlock node)
//     {
//         inABlockBlock(node);
//         {
//             Object temp[] = node.getStatement().toArray();
//             for(int i = 0; i < temp.length; i++)
//             {
//                 ((PStatement) temp[i]).apply(this);
//             }
//         }
//         outABlockBlock(node);
//     }

// 	public void inAAssignmentStatement(AAssignmentStatement node)
// 	{
// 	}



	public void outAAssignmentStatement(AAssignmentStatement node)
	{
		Instruction	instruction = (Instruction) getNodeAttribute(node.getLvalue());
		m_aMethods[METHOD_A].appendInstruction(instruction);
	}



	public void inAExpressionStatement(AExpressionStatement node)
	{
	}

	public void outAExpressionStatement(AExpressionStatement node)
	{
	}

//     public void caseAExpressionStatement(AExpressionStatement node)
//     {
//         inAExpressionStatement(node);
//         if(node.getExpr() != null)
//         {
//             node.getExpr().apply(this);
//         }
//         if(node.getSemicolon() != null)
//         {
//             node.getSemicolon().apply(this);
//         }
//         outAExpressionStatement(node);
//     }

	public void inAIfStatement(AIfStatement node)
	{
	}

	public void outAIfStatement(AIfStatement node)
	{
	}



	public void caseAIfStatement(AIfStatement node)
	{
		inAIfStatement(node);
        if(node.getIf() != null)
        {
            node.getIf().apply(this);
        }
        if(node.getLPar() != null)
        {
            node.getLPar().apply(this);
        }
        if(node.getExpr() != null)
        {
            node.getExpr().apply(this);
        }
        if(node.getRPar() != null)
        {
            node.getRPar().apply(this);
        }
	m_aMethods[METHOD_A].appendInstruction(InstructionConstants.FCONST_0);
	m_aMethods[METHOD_A].appendInstruction(InstructionConstants.FCMPL);
	BranchInstruction	ifeq = new IFEQ(null);
	m_aMethods[METHOD_A].appendInstruction(ifeq);
        if(node.getLBrace() != null)
        {
            node.getLBrace().apply(this);
        }
        if(node.getBlock() != null)
        {
            node.getBlock().apply(this);
        }
        if(node.getRBrace() != null)
        {
            node.getRBrace().apply(this);
        }
	m_aMethods[METHOD_A].setPendingBranchInstruction(ifeq);
        outAIfStatement(node);
    }

	public void inAIfElseStatement(AIfElseStatement node)
	{
	}

	public void outAIfElseStatement(AIfElseStatement node)
	{
	}

//     public void caseAIfElseStatement(AIfElseStatement node)
//     {
//         inAIfElseStatement(node);
//         if(node.getIf() != null)
//         {
//             node.getIf().apply(this);
//         }
//         if(node.getLPar() != null)
//         {
//             node.getLPar().apply(this);
//         }
//         if(node.getExpr() != null)
//         {
//             node.getExpr().apply(this);
//         }
//         if(node.getRPar() != null)
//         {
//             node.getRPar().apply(this);
//         }
//         if(node.getLBrace1() != null)
//         {
//             node.getLBrace1().apply(this);
//         }
//         if(node.getBlock1() != null)
//         {
//             node.getBlock1().apply(this);
//         }
//         if(node.getRBrace1() != null)
//         {
//             node.getRBrace1().apply(this);
//         }
//         if(node.getElse() != null)
//         {
//             node.getElse().apply(this);
//         }
//         if(node.getLBrace2() != null)
//         {
//             node.getLBrace2().apply(this);
//         }
//         if(node.getBlock2() != null)
//         {
//             node.getBlock2().apply(this);
//         }
//         if(node.getRBrace2() != null)
//         {
//             node.getRBrace2().apply(this);
//         }
//         outAIfElseStatement(node);
//     }

	public void inAWhileStatement(AWhileStatement node)
	{
	}

	public void outAWhileStatement(AWhileStatement node)
	{
	}

//     public void caseAWhileStatement(AWhileStatement node)
//     {
//         inAWhileStatement(node);
//         if(node.getWhile() != null)
//         {
//             node.getWhile().apply(this);
//         }
//         if(node.getLPar() != null)
//         {
//             node.getLPar().apply(this);
//         }
//         if(node.getExpr() != null)
//         {
//             node.getExpr().apply(this);
//         }
//         if(node.getRPar() != null)
//         {
//             node.getRPar().apply(this);
//         }
//         if(node.getLBrace() != null)
//         {
//             node.getLBrace().apply(this);
//         }
//         if(node.getBlock() != null)
//         {
//             node.getBlock().apply(this);
//         }
//         if(node.getRBrace() != null)
//         {
//             node.getRBrace().apply(this);
//         }
//         outAWhileStatement(node);
//     }

	public void inAInstrumentStatement(AInstrumentStatement node)
	{
	}

	public void outAInstrumentStatement(AInstrumentStatement node)
	{
	}

//     public void caseAInstrumentStatement(AInstrumentStatement node)
//     {
//         inAInstrumentStatement(node);
//         if(node.getInstr() != null)
//         {
//             node.getInstr().apply(this);
//         }
//         if(node.getIdentifier() != null)
//         {
//             node.getIdentifier().apply(this);
//         }
//         if(node.getLPar() != null)
//         {
//             node.getLPar().apply(this);
//         }
//         if(node.getExprlist() != null)
//         {
//             node.getExprlist().apply(this);
//         }
//         if(node.getRPar() != null)
//         {
//             node.getRPar().apply(this);
//         }
//         if(node.getSemicolon() != null)
//         {
//             node.getSemicolon().apply(this);
//         }
//         outAInstrumentStatement(node);
//     }

	public void inAOutputStatement(AOutputStatement node)
	{
	}

	public void outAOutputStatement(AOutputStatement node)
	{
	}

//     public void caseAOutputStatement(AOutputStatement node)
//     {
//         inAOutputStatement(node);
//         if(node.getOutput() != null)
//         {
//             node.getOutput().apply(this);
//         }
//         if(node.getLPar() != null)
//         {
//             node.getLPar().apply(this);
//         }
//         if(node.getExprlist() != null)
//         {
//             node.getExprlist().apply(this);
//         }
//         if(node.getRPar() != null)
//         {
//             node.getRPar().apply(this);
//         }
//         if(node.getSemicolon() != null)
//         {
//             node.getSemicolon().apply(this);
//         }
//         outAOutputStatement(node);
//     }

	public void inASpatializeStatement(ASpatializeStatement node)
	{
	}

	public void outASpatializeStatement(ASpatializeStatement node)
	{
	}

//     public void caseASpatializeStatement(ASpatializeStatement node)
//     {
//         inASpatializeStatement(node);
//         if(node.getSpatialize() != null)
//         {
//             node.getSpatialize().apply(this);
//         }
//         if(node.getLPar() != null)
//         {
//             node.getLPar().apply(this);
//         }
//         if(node.getExprlist() != null)
//         {
//             node.getExprlist().apply(this);
//         }
//         if(node.getRPar() != null)
//         {
//             node.getRPar().apply(this);
//         }
//         if(node.getSemicolon() != null)
//         {
//             node.getSemicolon().apply(this);
//         }
//         outASpatializeStatement(node);
//     }

	public void inAOutbusStatement(AOutbusStatement node)
	{
	}

	public void outAOutbusStatement(AOutbusStatement node)
	{
	}

//     public void caseAOutbusStatement(AOutbusStatement node)
//     {
//         inAOutbusStatement(node);
//         if(node.getOutbus() != null)
//         {
//             node.getOutbus().apply(this);
//         }
//         if(node.getLPar() != null)
//         {
//             node.getLPar().apply(this);
//         }
//         if(node.getIdentifier() != null)
//         {
//             node.getIdentifier().apply(this);
//         }
//         if(node.getComma() != null)
//         {
//             node.getComma().apply(this);
//         }
//         if(node.getExprlist() != null)
//         {
//             node.getExprlist().apply(this);
//         }
//         if(node.getRPar() != null)
//         {
//             node.getRPar().apply(this);
//         }
//         if(node.getSemicolon() != null)
//         {
//             node.getSemicolon().apply(this);
//         }
//         outAOutbusStatement(node);
//     }

	public void inAExtendStatement(AExtendStatement node)
	{
	}

	public void outAExtendStatement(AExtendStatement node)
	{
	}

//     public void caseAExtendStatement(AExtendStatement node)
//     {
//         inAExtendStatement(node);
//         if(node.getExtend() != null)
//         {
//             node.getExtend().apply(this);
//         }
//         if(node.getLPar() != null)
//         {
//             node.getLPar().apply(this);
//         }
//         if(node.getExpr() != null)
//         {
//             node.getExpr().apply(this);
//         }
//         if(node.getRPar() != null)
//         {
//             node.getRPar().apply(this);
//         }
//         if(node.getSemicolon() != null)
//         {
//             node.getSemicolon().apply(this);
//         }
//         outAExtendStatement(node);
//     }

	public void inATurnoffStatement(ATurnoffStatement node)
	{
	}

	public void outATurnoffStatement(ATurnoffStatement node)
	{
	}

//     public void caseATurnoffStatement(ATurnoffStatement node)
//     {
//         inATurnoffStatement(node);
//         if(node.getTurnoff() != null)
//         {
//             node.getTurnoff().apply(this);
//         }
//         if(node.getSemicolon() != null)
//         {
//             node.getSemicolon().apply(this);
//         }
//         outATurnoffStatement(node);
//     }

	public void inAReturnStatement(AReturnStatement node)
	{
	}

	public void outAReturnStatement(AReturnStatement node)
	{
	}

//     public void caseAReturnStatement(AReturnStatement node)
//     {
//         inAReturnStatement(node);
//         if(node.getReturn() != null)
//         {
//             node.getReturn().apply(this);
//         }
//         if(node.getLPar() != null)
//         {
//             node.getLPar().apply(this);
//         }
//         if(node.getExpr() != null)
//         {
//             node.getExpr().apply(this);
//         }
//         if(node.getRPar() != null)
//         {
//             node.getRPar().apply(this);
//         }
//         if(node.getSemicolon() != null)
//         {
//             node.getSemicolon().apply(this);
//         }
//         outAReturnStatement(node);
//     }

// 	public void inASimpleLvalue(ASimpleLvalue node)
// 	{
// 	}

	public void outASimpleLvalue(ASimpleLvalue node)
	{
		/*	This is needed at the very end, when the putfield
			instruction is executed.
		*/
		m_aMethods[METHOD_A].appendInstruction(InstructionConstants.ALOAD_0);
		String	strVariableName = node.getIdentifier().getText();
		// TODO: use getClassName()
		// set the instruction to be executed after the rvalue is calculated
		Instruction	instruction = getInstructionFactory().createPutField(m_strClassName, strVariableName, Type.FLOAT);
		setNodeAttribute(node, instruction);
	}


	public void inAIndexedLvalue(AIndexedLvalue node)
	{
		// push the array reference onto the stack
		String	strVariableName = node.getIdentifier().getText();
		m_aMethods[METHOD_A].appendGetField(strVariableName);
	}

	public void outAIndexedLvalue(AIndexedLvalue node)
	{
		/*	The array reference still is on the stack. Now,
			also the array index (as a float) is on the stack.
			It has to be transformed to integer.
		*/
		// TODO: correct rounding (1.5 -> 2.0)
		m_aMethods[METHOD_A].appendInstruction(InstructionConstants.F2I);
		// set the instruction to be executed after the rvalue is calculated
		setNodeAttribute(node, InstructionConstants.FASTORE);
	}

//     public void caseAIndexedLvalue(AIndexedLvalue node)
//     {
//         inAIndexedLvalue(node);
//         if(node.getIdentifier() != null)
//         {
//             node.getIdentifier().apply(this);
//         }
//         if(node.getLBracket() != null)
//         {
//             node.getLBracket().apply(this);
//         }
//         if(node.getExpr() != null)
//         {
//             node.getExpr().apply(this);
//         }
//         if(node.getRBracket() != null)
//         {
//             node.getRBracket().apply(this);
//         }
//         outAIndexedLvalue(node);
//     }

	public void inAIdentlistIdentlist(AIdentlistIdentlist node)
	{
	}

	public void outAIdentlistIdentlist(AIdentlistIdentlist node)
	{
	}

//     public void caseAIdentlistIdentlist(AIdentlistIdentlist node)
//     {
//         inAIdentlistIdentlist(node);
//         if(node.getIdentifier() != null)
//         {
//             node.getIdentifier().apply(this);
//         }
//         {
//             Object temp[] = node.getIdentlistTail().toArray();
//             for(int i = 0; i < temp.length; i++)
//             {
//                 ((PIdentlistTail) temp[i]).apply(this);
//             }
//         }
//         outAIdentlistIdentlist(node);
//     }

	public void inAIdentlistTailIdentlistTail(AIdentlistTailIdentlistTail node)
	{
	}

	public void outAIdentlistTailIdentlistTail(AIdentlistTailIdentlistTail node)
	{
	}

//     public void caseAIdentlistTailIdentlistTail(AIdentlistTailIdentlistTail node)
//     {
//         inAIdentlistTailIdentlistTail(node);
//         if(node.getComma() != null)
//         {
//             node.getComma().apply(this);
//         }
//         if(node.getIdentifier() != null)
//         {
//             node.getIdentifier().apply(this);
//         }
//         outAIdentlistTailIdentlistTail(node);
//     }

	public void inAParamlistParamlist(AParamlistParamlist node)
	{
	}

	public void outAParamlistParamlist(AParamlistParamlist node)
	{
	}

//     public void caseAParamlistParamlist(AParamlistParamlist node)
//     {
//         inAParamlistParamlist(node);
//         if(node.getParamdecl() != null)
//         {
//             node.getParamdecl().apply(this);
//         }
//         {
//             Object temp[] = node.getParamlistTail().toArray();
//             for(int i = 0; i < temp.length; i++)
//             {
//                 ((PParamlistTail) temp[i]).apply(this);
//             }
//         }
//         outAParamlistParamlist(node);
//     }

	public void inAParamlistTailParamlistTail(AParamlistTailParamlistTail node)
	{
	}

	public void outAParamlistTailParamlistTail(AParamlistTailParamlistTail node)
	{
	}

//     public void caseAParamlistTailParamlistTail(AParamlistTailParamlistTail node)
//     {
//         inAParamlistTailParamlistTail(node);
//         if(node.getComma() != null)
//         {
//             node.getComma().apply(this);
//         }
//         if(node.getParamdecl() != null)
//         {
//             node.getParamdecl().apply(this);
//         }
//         outAParamlistTailParamlistTail(node);
//     }

	public void inASigvarVardecl(ASigvarVardecl node)
	{
	}

	public void outASigvarVardecl(ASigvarVardecl node)
	{
	}

//     public void caseASigvarVardecl(ASigvarVardecl node)
//     {
//         inASigvarVardecl(node);
//         if(node.getTaglist() != null)
//         {
//             node.getTaglist().apply(this);
//         }
//         if(node.getStype() != null)
//         {
//             node.getStype().apply(this);
//         }
//         if(node.getNamelist() != null)
//         {
//             node.getNamelist().apply(this);
//         }
//         if(node.getSemicolon() != null)
//         {
//             node.getSemicolon().apply(this);
//         }
//         outASigvarVardecl(node);
//     }

	public void inATablemapVardecl(ATablemapVardecl node)
	{
	}

	public void outATablemapVardecl(ATablemapVardecl node)
	{
	}

//     public void caseATablemapVardecl(ATablemapVardecl node)
//     {
//         inATablemapVardecl(node);
//         if(node.getTablemap() != null)
//         {
//             node.getTablemap().apply(this);
//         }
//         if(node.getIdentifier() != null)
//         {
//             node.getIdentifier().apply(this);
//         }
//         if(node.getLPar() != null)
//         {
//             node.getLPar().apply(this);
//         }
//         if(node.getIdentlist() != null)
//         {
//             node.getIdentlist().apply(this);
//         }
//         if(node.getRPar() != null)
//         {
//             node.getRPar().apply(this);
//         }
//         if(node.getSemicolon() != null)
//         {
//             node.getSemicolon().apply(this);
//         }
//         outATablemapVardecl(node);
//     }

	public void inASigvarOpvardecl(ASigvarOpvardecl node)
	{
		m_bOpvardecls = true;
	}


	public void outASigvarOpvardecl(ASigvarOpvardecl node)
	{
		m_bOpvardecls = false;
	}



//     public void caseASigvarOpvardecl(ASigvarOpvardecl node)
//     {
//         inASigvarOpvardecl(node);
//         if(node.getTaglist() != null)
//         {
//             node.getTaglist().apply(this);
//         }
//         if(node.getOtype() != null)
//         {
//             node.getOtype().apply(this);
//         }
//         if(node.getNamelist() != null)
//         {
//             node.getNamelist().apply(this);
//         }
//         if(node.getSemicolon() != null)
//         {
//             node.getSemicolon().apply(this);
//         }
//         outASigvarOpvardecl(node);
//     }

	public void inAParamdeclParamdecl(AParamdeclParamdecl node)
	{
	}

	public void outAParamdeclParamdecl(AParamdeclParamdecl node)
	{
	}

//     public void caseAParamdeclParamdecl(AParamdeclParamdecl node)
//     {
//         inAParamdeclParamdecl(node);
//         if(node.getOtype() != null)
//         {
//             node.getOtype().apply(this);
//         }
//         if(node.getName() != null)
//         {
//             node.getName().apply(this);
//         }
//         outAParamdeclParamdecl(node);
//     }

	public void inANamelistNamelist(ANamelistNamelist node)
	{
	}

	public void outANamelistNamelist(ANamelistNamelist node)
	{
	}

//     public void caseANamelistNamelist(ANamelistNamelist node)
//     {
//         inANamelistNamelist(node);
//         if(node.getName() != null)
//         {
//             node.getName().apply(this);
//         }
//         {
//             Object temp[] = node.getNamelistTail().toArray();
//             for(int i = 0; i < temp.length; i++)
//             {
//                 ((PNamelistTail) temp[i]).apply(this);
//             }
//         }
//         outANamelistNamelist(node);
//     }

	public void inANamelistTailNamelistTail(ANamelistTailNamelistTail node)
	{
	}

	public void outANamelistTailNamelistTail(ANamelistTailNamelistTail node)
	{
	}

//     public void caseANamelistTailNamelistTail(ANamelistTailNamelistTail node)
//     {
//         inANamelistTailNamelistTail(node);
//         if(node.getComma() != null)
//         {
//             node.getComma().apply(this);
//         }
//         if(node.getName() != null)
//         {
//             node.getName().apply(this);
//         }
//         outANamelistTailNamelistTail(node);
//     }


	public void outASimpleName(ASimpleName node)
	{
		if (m_bOpvardecls)
		{
			String	strVariableName = node.getIdentifier().getText();
			addLocalVariable(strVariableName);
		}
	}



	public void outAIndexedName(AIndexedName node)
	{
		if (m_bOpvardecls)
		{
			String	strVariableName = node.getIdentifier().getText();
			String	strInteger = node.getInteger().getText();
			int	nInteger = Integer.parseInt(strInteger);
			addLocalArray(strVariableName);
			// code to allocate array in constructor
			m_aMethods[METHOD_CONSTR].appendInstruction(InstructionConstants.ALOAD_0);
			Instruction	instruction = (Instruction) getInstructionFactory().createNewArray(Type.FLOAT, (short) nInteger);
			m_aMethods[METHOD_CONSTR].appendInstruction(instruction);
			m_aMethods[METHOD_CONSTR].appendPutField(strVariableName);
		}
	}



	public void outAInchannelsName(AInchannelsName node)
	{
		// TODO:
	}


	public void outAOutchannelsName(AOutchannelsName node)
	{
		// TODO:
	}



	public void outAIvarStype(AIvarStype node)
	{
		setNodeAttribute(node, new WidthAndRate(WidthAndRate.WIDTH_UNKNOWN, WidthAndRate.RATE_I));
	}


	public void outAKsigStype(AKsigStype node)
	{
		setNodeAttribute(node, new WidthAndRate(WidthAndRate.WIDTH_UNKNOWN, WidthAndRate.RATE_K));
	}


	public void outAAsigStype(AAsigStype node)
	{
		setNodeAttribute(node, new WidthAndRate(WidthAndRate.WIDTH_UNKNOWN, WidthAndRate.RATE_A));
	}


	public void outATableStype(ATableStype node)
	{
		// TODO:
	}


	public void outAOparrayStype(AOparrayStype node)
	{
		// TODO:
	}


	public void outAXsigOtype(AXsigOtype node)
	{
		// TODO:
	}


	public void outAStypeOtype(AStypeOtype node)
	{
		setNodeAttribute(node, getNodeAttribute(node.getStype()));
	}



	public void inATabledeclTabledecl(ATabledeclTabledecl node)
	{
	}

	public void outATabledeclTabledecl(ATabledeclTabledecl node)
	{
	}

//     public void caseATabledeclTabledecl(ATabledeclTabledecl node)
//     {
//         inATabledeclTabledecl(node);
//         if(node.getTable() != null)
//         {
//             node.getTable().apply(this);
//         }
//         if(node.getIdentifier1() != null)
//         {
//             node.getIdentifier1().apply(this);
//         }
//         if(node.getLPar() != null)
//         {
//             node.getLPar().apply(this);
//         }
//         if(node.getIdentifier2() != null)
//         {
//             node.getIdentifier2().apply(this);
//         }
//         if(node.getComma() != null)
//         {
//             node.getComma().apply(this);
//         }
//         if(node.getExprstrlist() != null)
//         {
//             node.getExprstrlist().apply(this);
//         }
//         if(node.getRPar() != null)
//         {
//             node.getRPar().apply(this);
//         }
//         outATabledeclTabledecl(node);
//     }

	public void inAImportsTaglist(AImportsTaglist node)
	{
	}

	public void outAImportsTaglist(AImportsTaglist node)
	{
	}

//     public void caseAImportsTaglist(AImportsTaglist node)
//     {
//         inAImportsTaglist(node);
//         if(node.getImports() != null)
//         {
//             node.getImports().apply(this);
//         }
//         outAImportsTaglist(node);
//     }

	public void inAExportsTaglist(AExportsTaglist node)
	{
	}

	public void outAExportsTaglist(AExportsTaglist node)
	{
	}

//     public void caseAExportsTaglist(AExportsTaglist node)
//     {
//         inAExportsTaglist(node);
//         if(node.getExports() != null)
//         {
//             node.getExports().apply(this);
//         }
//         outAExportsTaglist(node);
//     }

	public void inAImportsexportsTaglist(AImportsexportsTaglist node)
	{
	}

	public void outAImportsexportsTaglist(AImportsexportsTaglist node)
	{
	}

//     public void caseAImportsexportsTaglist(AImportsexportsTaglist node)
//     {
//         inAImportsexportsTaglist(node);
//         if(node.getImports() != null)
//         {
//             node.getImports().apply(this);
//         }
//         if(node.getExports() != null)
//         {
//             node.getExports().apply(this);
//         }
//         outAImportsexportsTaglist(node);
//     }

	public void inAExportsimportsTaglist(AExportsimportsTaglist node)
	{
	}

	public void outAExportsimportsTaglist(AExportsimportsTaglist node)
	{
	}

//     public void caseAExportsimportsTaglist(AExportsimportsTaglist node)
//     {
//         inAExportsimportsTaglist(node);
//         if(node.getExports() != null)
//         {
//             node.getExports().apply(this);
//         }
//         if(node.getImports() != null)
//         {
//             node.getImports().apply(this);
//         }
//         outAExportsimportsTaglist(node);
//     }

	public void inAAopcodeOptype(AAopcodeOptype node)
	{
	}

	public void outAAopcodeOptype(AAopcodeOptype node)
	{
	}

//     public void caseAAopcodeOptype(AAopcodeOptype node)
//     {
//         inAAopcodeOptype(node);
//         if(node.getAopcode() != null)
//         {
//             node.getAopcode().apply(this);
//         }
//         outAAopcodeOptype(node);
//     }

	public void inAKopcodeOptype(AKopcodeOptype node)
	{
	}

	public void outAKopcodeOptype(AKopcodeOptype node)
	{
	}

//     public void caseAKopcodeOptype(AKopcodeOptype node)
//     {
//         inAKopcodeOptype(node);
//         if(node.getKopcode() != null)
//         {
//             node.getKopcode().apply(this);
//         }
//         outAKopcodeOptype(node);
//     }

	public void inAIopcodeOptype(AIopcodeOptype node)
	{
	}

	public void outAIopcodeOptype(AIopcodeOptype node)
	{
	}

//     public void caseAIopcodeOptype(AIopcodeOptype node)
//     {
//         inAIopcodeOptype(node);
//         if(node.getIopcode() != null)
//         {
//             node.getIopcode().apply(this);
//         }
//         outAIopcodeOptype(node);
//     }

	public void inAOpcodeOptype(AOpcodeOptype node)
	{
	}

	public void outAOpcodeOptype(AOpcodeOptype node)
	{
	}

//     public void caseAOpcodeOptype(AOpcodeOptype node)
//     {
//         inAOpcodeOptype(node);
//         if(node.getOpcode() != null)
//         {
//             node.getOpcode().apply(this);
//         }
//         outAOpcodeOptype(node);
//     }


	public void inAAltExpr(AAltExpr node)
	{
		// TODO:
	}


	public void outAAltExpr(AAltExpr node)
	{
		// TODO:
	}


//     public void caseAAltExpr(AAltExpr node)
//     {
//         inAAltExpr(node);
//         if(node.getOrexpr1() != null)
//         {
//             node.getOrexpr1().apply(this);
//         }
//         if(node.getQuestMark() != null)
//         {
//             node.getQuestMark().apply(this);
//         }
//         if(node.getOrexpr2() != null)
//         {
//             node.getOrexpr2().apply(this);
//         }
//         if(node.getColon() != null)
//         {
//             node.getColon().apply(this);
//         }
//         if(node.getOrexpr3() != null)
//         {
//             node.getOrexpr3().apply(this);
//         }
//         outAAltExpr(node);
//     }



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
		BranchInstruction	branch = new IFNE(null);
		m_aMethods[METHOD_A].appendRelationalOperation(branch);
	}



	public void outAEqEqualityexpr(AEqEqualityexpr node)
	{
		BranchInstruction	branch = new IFEQ(null);
		m_aMethods[METHOD_A].appendRelationalOperation(branch);
	}



	public void inAGtRelationalexpr(AGtRelationalexpr node)
	{
	}

	public void outAGtRelationalexpr(AGtRelationalexpr node)
	{
		BranchInstruction	branch = new IFGT(null);
		m_aMethods[METHOD_A].appendRelationalOperation(branch);
	}



	public void outALtRelationalexpr(ALtRelationalexpr node)
	{
		BranchInstruction	branch = new IFLT(null);
		m_aMethods[METHOD_A].appendRelationalOperation(branch);
	}



	public void outALteqRelationalexpr(ALteqRelationalexpr node)
	{
		BranchInstruction	branch = new IFLE(null);
		m_aMethods[METHOD_A].appendRelationalOperation(branch);
	}



	public void outAGteqRelationalexpr(AGteqRelationalexpr node)
	{
		BranchInstruction	branch = new IFGE(null);
		m_aMethods[METHOD_A].appendRelationalOperation(branch);
	}



	public void outAPlusAddexpr(APlusAddexpr node)
	{
		m_aMethods[METHOD_A].appendInstruction(InstructionConstants.FADD);
	}



	public void outAMinusAddexpr(AMinusAddexpr node)
	{
		m_aMethods[METHOD_A].appendInstruction(InstructionConstants.FSUB);
	}



	public void outAMultFactor(AMultFactor node)
	{
		m_aMethods[METHOD_A].appendInstruction(InstructionConstants.FMUL);
	}



	public void outADivFactor(ADivFactor node)
	{
		m_aMethods[METHOD_A].appendInstruction(InstructionConstants.FDIV);
	}



	public void outANotUnaryminusterm(ANotUnaryminusterm node)
	{
		m_aMethods[METHOD_A].appendInstruction(InstructionConstants.FNEG);
	}



	public void outANotNotterm(ANotNotterm node)
	{
		m_aMethods[METHOD_A].appendInstruction(InstructionConstants.FCONST_0);
		m_aMethods[METHOD_A].appendInstruction(InstructionConstants.FCMPL);
		BranchInstruction	branch0 = new IFNE(null);
		m_aMethods[METHOD_A].appendInstruction(branch0);
		m_aMethods[METHOD_A].appendInstruction(InstructionConstants.FCONST_1);
		BranchInstruction	branch1 = new GOTO(null);
		m_aMethods[METHOD_A].appendInstruction(branch1);
		m_aMethods[METHOD_A].setPendingBranchInstruction(branch0);
		m_aMethods[METHOD_A].appendInstruction(InstructionConstants.FCONST_0);
		m_aMethods[METHOD_A].setPendingBranchInstruction(branch1);
	}



	public void outAIdentifierTerm(AIdentifierTerm node)
	{
		String	strVariableName = node.getIdentifier().getText();
		m_aMethods[METHOD_A].appendGetField(strVariableName);
	}


	public void outAConstantTerm(AConstantTerm node)
	{
		Object	constant = getNodeAttribute(node.getConst());
		if (constant instanceof Integer ||
		    constant instanceof Float)
		{
			float	fValue = ((Number) constant).floatValue();
			m_aMethods[METHOD_A].appendFloatConstant(fValue);
		}
		else
		{
			throw new RuntimeException("constant is neither int nor float");
		}
	}



	public void inAIndexedTerm(AIndexedTerm node)
	{
		// push the array reference onto the stack
		String	strVariableName = node.getIdentifier().getText();
		m_aMethods[METHOD_A].appendGetField(strVariableName);
	}

	public void outAIndexedTerm(AIndexedTerm node)
	{
		/*	The array reference still is on the stack. Now,
			also the array index (as a float) is on the stack.
			It has to be transformed to integer.
		*/
		// TODO: correct rounding (1.5 -> 2.0)
		m_aMethods[METHOD_A].appendInstruction(InstructionConstants.F2I);
		// and now fetch the value from the array
		setNodeAttribute(node, InstructionConstants.FALOAD);
	}



	public void inASasbfTerm(ASasbfTerm node)
	{
	}

	public void outASasbfTerm(ASasbfTerm node)
	{
	}

//     public void caseASasbfTerm(ASasbfTerm node)
//     {
//         inASasbfTerm(node);
//         if(node.getSasbf() != null)
//         {
//             node.getSasbf().apply(this);
//         }
//         if(node.getLPar() != null)
//         {
//             node.getLPar().apply(this);
//         }
//         if(node.getExprlist() != null)
//         {
//             node.getExprlist().apply(this);
//         }
//         if(node.getRPar() != null)
//         {
//             node.getRPar().apply(this);
//         }
//         outASasbfTerm(node);
//     }

    public void inAFunctionTerm(AFunctionTerm node)
    {
    }

    public void outAFunctionTerm(AFunctionTerm node)
    {
    }

//     public void caseAFunctionTerm(AFunctionTerm node)
//     {
//         inAFunctionTerm(node);
//         if(node.getIdentifier() != null)
//         {
//             node.getIdentifier().apply(this);
//         }
//         if(node.getLPar() != null)
//         {
//             node.getLPar().apply(this);
//         }
//         if(node.getExprlist() != null)
//         {
//             node.getExprlist().apply(this);
//         }
//         if(node.getRPar() != null)
//         {
//             node.getRPar().apply(this);
//         }
//         outAFunctionTerm(node);
//     }

    public void inAIndexedfunctionTerm(AIndexedfunctionTerm node)
    {
    }

    public void outAIndexedfunctionTerm(AIndexedfunctionTerm node)
    {
    }

//     public void caseAIndexedfunctionTerm(AIndexedfunctionTerm node)
//     {
//         inAIndexedfunctionTerm(node);
//         if(node.getIdentifier() != null)
//         {
//             node.getIdentifier().apply(this);
//         }
//         if(node.getLBracket() != null)
//         {
//             node.getLBracket().apply(this);
//         }
//         if(node.getExpr() != null)
//         {
//             node.getExpr().apply(this);
//         }
//         if(node.getRBracket() != null)
//         {
//             node.getRBracket().apply(this);
//         }
//         if(node.getLPar() != null)
//         {
//             node.getLPar().apply(this);
//         }
//         if(node.getExprlist() != null)
//         {
//             node.getExprlist().apply(this);
//         }
//         if(node.getRPar() != null)
//         {
//             node.getRPar().apply(this);
//         }
//         outAIndexedfunctionTerm(node);
//     }


    public void inAExprlistExprlist(AExprlistExprlist node)
    {
    }


    public void outAExprlistExprlist(AExprlistExprlist node)
    {
    }


//     public void caseAExprlistExprlist(AExprlistExprlist node)
//     {
//         inAExprlistExprlist(node);
//         if(node.getExpr() != null)
//         {
//             node.getExpr().apply(this);
//         }
//         {
//             Object temp[] = node.getExprlistTail().toArray();
//             for(int i = 0; i < temp.length; i++)
//             {
//                 ((PExprlistTail) temp[i]).apply(this);
//             }
//         }
//         outAExprlistExprlist(node);
//     }

    public void inAExprlistTailExprlistTail(AExprlistTailExprlistTail node)
    {
    }

    public void outAExprlistTailExprlistTail(AExprlistTailExprlistTail node)
    {
    }

//     public void caseAExprlistTailExprlistTail(AExprlistTailExprlistTail node)
//     {
//         inAExprlistTailExprlistTail(node);
//         if(node.getComma() != null)
//         {
//             node.getComma().apply(this);
//         }
//         if(node.getExpr() != null)
//         {
//             node.getExpr().apply(this);
//         }
//         outAExprlistTailExprlistTail(node);
//     }

    public void inAExprstrlistExprstrlist(AExprstrlistExprstrlist node)
    {
    }

    public void outAExprstrlistExprstrlist(AExprstrlistExprstrlist node)
    {
    }

//     public void caseAExprstrlistExprstrlist(AExprstrlistExprstrlist node)
//     {
//         inAExprstrlistExprstrlist(node);
//         if(node.getExprOrString() != null)
//         {
//             node.getExprOrString().apply(this);
//         }
//         {
//             Object temp[] = node.getExprstrlistTail().toArray();
//             for(int i = 0; i < temp.length; i++)
//             {
//                 ((PExprstrlistTail) temp[i]).apply(this);
//             }
//         }
//         outAExprstrlistExprstrlist(node);
//     }

    public void inAExprstrlistTailExprstrlistTail(AExprstrlistTailExprstrlistTail node)
    {
    }

    public void outAExprstrlistTailExprstrlistTail(AExprstrlistTailExprstrlistTail node)
    {
    }

//     public void caseAExprstrlistTailExprstrlistTail(AExprstrlistTailExprstrlistTail node)
//     {
//         inAExprstrlistTailExprstrlistTail(node);
//         if(node.getComma() != null)
//         {
//             node.getComma().apply(this);
//         }
//         if(node.getExprOrString() != null)
//         {
//             node.getExprOrString().apply(this);
//         }
//         outAExprstrlistTailExprstrlistTail(node);
//     }

    public void inAExprExprOrString(AExprExprOrString node)
    {
    }

    public void outAExprExprOrString(AExprExprOrString node)
    {
    }

//     public void caseAExprExprOrString(AExprExprOrString node)
//     {
//         inAExprExprOrString(node);
//         if(node.getExpr() != null)
//         {
//             node.getExpr().apply(this);
//         }
//         outAExprExprOrString(node);
//     }

    public void inAStringExprOrString(AStringExprOrString node)
    {
    }

    public void outAStringExprOrString(AStringExprOrString node)
    {
    }

//     public void caseAStringExprOrString(AStringExprOrString node)
//     {
//         inAStringExprOrString(node);
//         if(node.getString() != null)
//         {
//             node.getString().apply(this);
//         }
//         outAStringExprOrString(node);
//     }

	public void inAIntegerConst(AIntegerConst node)
	{
	}

    public void outAIntegerConst(AIntegerConst node)
    {
		String	strInteger = node.getInteger().getText();
		Integer	integer = new Integer(strInteger);
		setNodeAttribute(node, integer);
    }

//     public void caseAIntegerConst(AIntegerConst node)
//     {
//         inAIntegerConst(node);
//         if(node.getInteger() != null)
//         {
//             node.getInteger().apply(this);
//         }
//         outAIntegerConst(node);
//     }

// 	public void inANumberConst(ANumberConst node)
// 	{
// 	}



	public void outANumberConst(ANumberConst node)
	{
		String	strNumber = node.getNumber().getText();
		Float	number = new Float(strNumber);
		setNodeAttribute(node, number);
	}



//     public void caseANumberConst(ANumberConst node)
//     {
//         inANumberConst(node);
//         if(node.getNumber() != null)
//         {
//             node.getNumber().apply(this);
//         }
//         outANumberConst(node);
//     }


////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////





	// helper methods



	private void setNodeAttribute(Node node, Object attribute)
	{
		m_nodeAttributes.put(node, attribute);
	}



	private Object getNodeAttribute(Node node)
	{
		return m_nodeAttributes.get(node);
	}



	private void addLocalVariable(String strVariableName)
	{
		FieldGen	fieldGen;
		fieldGen = new FieldGen(Constants.ACC_PRIVATE,
					Type.FLOAT,
					strVariableName,
					m_constantPoolGen);
		m_classGen.addField(fieldGen.getField());

	}



	private void addLocalArray(String strVariableName)
	{
		FieldGen	fieldGen;
		fieldGen = new FieldGen(Constants.ACC_PRIVATE,
					FLOAT_ARRAY,
					strVariableName,
					m_constantPoolGen);
		m_classGen.addField(fieldGen.getField());

	}



	/**	Returns the InstructionFactory.
		This method is mainly for use by inner classes.
		A bit dangerous, since that has to be one
		InstructionFactory per generated class.
	*/
	private InstructionFactory getInstructionFactory()
	{
		return m_instructionFactory;
	}





	private class InstrumentMethod
	{
		private ClassGen		m_classGen;
		private MethodGen		m_methodGen;
		private InstructionList		m_instructionList;
		private BranchInstruction	m_pendingBranchInstruction;


		public InstrumentMethod(ClassGen classGen, String strMethodName)
		{
			m_classGen = classGen;
			m_instructionList = new InstructionList();
			m_methodGen = new MethodGen(
				Constants.ACC_PUBLIC,
				Type.VOID,
				new Type[]{new ObjectType("org.tritonus.saol.engine.RTSystem")},
				new String[]{"rtSystem"},
				strMethodName,
				m_classGen.getClassName(),
				m_instructionList,
				m_classGen.getConstantPool());
		}



		/**	Append an instruction to the method's Instruction
			list. If a BranchInstruction is pending, it is
			targetted here.
		*/
		public InstructionHandle appendInstruction(Instruction instruction)
		{
			// System.out.println("instruction: " + instruction);
			InstructionHandle	target = null;
			if (instruction instanceof BranchInstruction)
			{
				target = m_instructionList.append((BranchInstruction) instruction);
			}
			else if (instruction instanceof CompoundInstruction)
			{
				target = m_instructionList.append((CompoundInstruction) instruction);
			}
			else
			{
				target = m_instructionList.append(instruction);
			}
			if (m_pendingBranchInstruction != null)
			{
				m_pendingBranchInstruction.setTarget(target);
				m_pendingBranchInstruction = null;
			}
			return target;
		}



	/**	Set the 'pending' BranchInstruction.
		This is a mechanism to avoid NOPs. If a BranchInstruction
		has to be targeted at an instruction that is immediately
		following, but has not been generated yet, set this
		BranchInstruction as the pending BranchInstruction.
		The next instruction added to the method's InstructionList
		with appendInstruction() will become the target of
		the pending BranchInstruction.
	*/
	public void setPendingBranchInstruction(BranchInstruction branchInstruction)
	{
		if (m_pendingBranchInstruction != null)
		{
			throw new RuntimeException("pending branch instruction already set");
		}
		m_pendingBranchInstruction = branchInstruction;
	}


	public void appendGetField(String strVariableName)
	{
		// System.out.println("class name: " + m_strClassName);
		// System.out.println("var name: " + strVariableName);
		appendInstruction(InstructionConstants.ALOAD_0);
		Instruction	instruction = getInstructionFactory().createGetField(m_strClassName, strVariableName, Type.FLOAT);
		appendInstruction(instruction);
	}


	/**
	   NOTE: this method does not append an ALOAD_0 instruction!
	 */
	public void appendPutField(String strVariableName)
	{
		// System.out.println("class name: " + m_strClassName);
		// System.out.println("var name: " + strVariableName);
		Instruction	instruction = getInstructionFactory().createPutField(m_strClassName, strVariableName, Type.FLOAT);
		appendInstruction(instruction);
	}



	public void appendIntegerConstant(int nValue)
	{
		Instruction	instruction = null;
		switch (nValue)
		{
		case -1:
			instruction = InstructionConstants.ICONST_M1;
			break;

		case 0:
			instruction = InstructionConstants.ICONST_0;
			break;

		case 1:
			instruction = InstructionConstants.ICONST_1;
			break;

		case 2:
			instruction = InstructionConstants.ICONST_2;
			break;

		case 3:
			instruction = InstructionConstants.ICONST_3;
			break;

		case 4:
			instruction = InstructionConstants.ICONST_4;
			break;

		case 5:
			instruction = InstructionConstants.ICONST_5;
			break;

		default:
			int	nConstantIndex = m_constantPoolGen.addInteger(nValue);
			instruction = new LDC(nConstantIndex);
		}
		appendInstruction(instruction);
	}


	public void appendFloatConstant(float fValue)
	{
		Instruction	instruction = null;
		if (fValue == 0.0)
		{
			instruction = InstructionConstants.FCONST_0;
		}
		else if (fValue == 1.0)
		{
			instruction = InstructionConstants.FCONST_1;
		}
		else if (fValue == 2.0)
		{
			instruction = InstructionConstants.FCONST_2;
		}
		else
		{
			int	nConstantIndex = m_constantPoolGen.addFloat(fValue);
			instruction = new LDC(nConstantIndex);
		}
		appendInstruction(instruction);
	}



		public void appendRelationalOperation(BranchInstruction branch0)
		{
			appendInstruction(InstructionConstants.FCMPL);
			appendInstruction(branch0);
			appendInstruction(InstructionConstants.FCONST_0);
			BranchInstruction	branch1 = new GOTO(null);
			appendInstruction(branch1);
			setPendingBranchInstruction(branch0);
			appendInstruction(InstructionConstants.FCONST_1);
			setPendingBranchInstruction(branch1);
		}



		public void finish()
		{
			appendInstruction(InstructionConstants.RETURN);
			m_methodGen.setMaxStack();
			m_classGen.addMethod(m_methodGen.getMethod());
		}
	}
}



/*** InstrumentCompilation.java ***/
