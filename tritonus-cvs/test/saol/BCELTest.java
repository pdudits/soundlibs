/*
 *	BCELTest.java
 */

/*
 *  Copyright (c) 2002 by Matthias Pfisterer <Matthias.Pfisterer@web.de>
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

package	org.tritonus.saol.compiler;


import	java.io.IOException;

import	org.apache.bcel.Constants;
import	org.apache.bcel.classfile.Field;
import	org.apache.bcel.classfile.JavaClass;
import	org.apache.bcel.generic.*;


/**	Example program using the BCEL.
	BCEL is the Byte Code Engineering Library, part of the
	jakarta project (http://jakarta.apache.org/bcel/).
*/
public class BCELTest
{
	public static void main(String[] args)
	{
		String	strClassName = "tone";
		ClassGen	classGen = new ClassGen(strClassName,
							"AbstractInstrument",
							"<generated>",
							Constants.ACC_PUBLIC | Constants.ACC_SUPER,
							null);
		ConstantPoolGen	constantPoolGen = classGen.getConstantPool();
		int	nAInitValueIndex = constantPoolGen.addFloat(0.196307F);
		int	nXInitValueIndex = constantPoolGen.addFloat(0.5F);
		FieldGen	fieldGen;
		fieldGen = new FieldGen(Constants.ACC_PRIVATE,
					Type.FLOAT,
					"a",
					constantPoolGen);
		classGen.addField(fieldGen.getField());
		fieldGen = new FieldGen(Constants.ACC_PRIVATE,
					Type.FLOAT,
					"x",
					constantPoolGen);
		classGen.addField(fieldGen.getField());
		fieldGen = new FieldGen(Constants.ACC_PRIVATE,
					Type.FLOAT,
					"y",
					constantPoolGen);
		classGen.addField(fieldGen.getField());
		fieldGen = new FieldGen(Constants.ACC_PRIVATE,
					Type.FLOAT,
					"init",
					constantPoolGen);
		classGen.addField(fieldGen.getField());


		InstructionList	il = new InstructionList();
		MethodGen	methodGen;
		methodGen = new MethodGen(Constants.ACC_PUBLIC,
					  Type.VOID,
					  new Type[]{new ObjectType("RTSystem")},
					  new String[]{"rtSystem"},
					  "doAPass",
					  strClassName,
					  il,
					  constantPoolGen);

		InstructionFactory	ifac = new InstructionFactory(constantPoolGen);
		il.append(InstructionConstants.ALOAD_0);
		il.append(new LDC(nAInitValueIndex));
		il.append(ifac.createPutField(strClassName, "a", Type.FLOAT));
		il.append(InstructionConstants.ALOAD_0);
		il.append(ifac.createGetField(strClassName, "init", Type.FLOAT));
		il.append(InstructionConstants.FCONST_0);
		il.append(InstructionConstants.FCMPL);
		IFNE	ifne0 = new IFNE(null);
		il.append(ifne0);
		il.append(InstructionConstants.ALOAD_0);
		il.append(InstructionConstants.FCONST_1);
		il.append(ifac.createPutField(strClassName, "init", Type.FLOAT));
		il.append(InstructionConstants.ALOAD_0);
		il.append(new LDC(nXInitValueIndex));
		il.append(ifac.createPutField(strClassName, "x", Type.FLOAT));
		InstructionHandle	ih0 = il.append(InstructionConstants.ALOAD_0);
		ifne0.setTarget(ih0);
		il.append(InstructionConstants.ALOAD_0);
		il.append(ifac.createGetField(strClassName, "x", Type.FLOAT));
		il.append(InstructionConstants.ALOAD_0);
		il.append(ifac.createGetField(strClassName, "a", Type.FLOAT));
		il.append(InstructionConstants.ALOAD_0);
		il.append(ifac.createGetField(strClassName, "y", Type.FLOAT));
		il.append(InstructionConstants.FMUL);
		il.append(InstructionConstants.FSUB);
		il.append(ifac.createPutField(strClassName, "x", Type.FLOAT));
		il.append(InstructionConstants.ALOAD_0);
		il.append(InstructionConstants.ALOAD_0);
		il.append(ifac.createGetField(strClassName, "y", Type.FLOAT));
		il.append(InstructionConstants.ALOAD_0);
		il.append(ifac.createGetField(strClassName, "a", Type.FLOAT));
		il.append(InstructionConstants.ALOAD_0);
		il.append(ifac.createGetField(strClassName, "x", Type.FLOAT));
		il.append(InstructionConstants.FMUL);
		il.append(InstructionConstants.FADD);
		il.append(ifac.createPutField(strClassName, "y", Type.FLOAT));
		il.append(InstructionConstants.ALOAD_1);
		il.append(InstructionConstants.ALOAD_0);
		il.append(ifac.createGetField(strClassName, "y", Type.FLOAT));
		il.append(ifac.createInvoke("RTSystem", "output", Type.VOID, new Type[]{Type.FLOAT}, Constants.INVOKEVIRTUAL));
		il.append(InstructionConstants.RETURN);
		methodGen.setMaxStack();
		classGen.addMethod(methodGen.getMethod());
		classGen.addEmptyConstructor(Constants.ACC_PUBLIC);
		JavaClass	javaClass = classGen.getJavaClass();
		try
		{
			javaClass.dump("tone.class");
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
	}
}

/*** BCELTest.java ***/
