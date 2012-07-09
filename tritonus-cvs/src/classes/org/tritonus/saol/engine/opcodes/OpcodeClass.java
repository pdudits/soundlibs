/*
 *	OpcodeClass.java
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


/**	The Math Opcodes (Section 5.9.4).
 */
public class OpcodeClass
{
	/**	Opcode class type: no instance needed.
		This means the opcode is implemented as a static method
		of the class. The Runtime system doesn't need to
		create an instance of the opcode class.
	 */
	public static final int		TYPE_STATIC = 0;

	/**	Opcode class type: one instance per renderer instance.
	 */
	public static final int		TYPE_RUNTIME_INSTANCE = 1;

	/**	Opcode class type: one instance per opcode usage.
	 */
	public static final int		TYPE_OPCODE_INSTANCE = 2;


	/**	The name of the opcode class.
		A fully qualified class name (package.class).
	 */
	private String		m_strName;

	/**	The type of the opcode class.
		One of TYPE_STATIC, TYPE_RUNTIME_INSTANCE
		and TYPE_OPCODE_INSTANCE.
	 */
	private int		m_nType;



	/**	Constructor.
		@param strName the name of the opcode class.
		A fully qualified class name is expected
		(package.class).

		@param nType the instantiation type of the class.
		One of TYPE_STATIC, TYPE_RUNTIME_INSTANCE
		and TYPE_OPCODE_INSTANCE.
	 */
	public OpcodeClass(String strName, int nType)
	{
		m_strName = strName;
		m_nType = nType;

	}



	/**	Retrieves the name of the opcode class.
		@return the name that was set with the constructor.
		A fully qualified class name
		(package.class)
	*/
	public String getName()
	{
		return m_strName;
	}


	/**	Retrieves the type of the opcode class.
		@return the type that was set with the constructor.
		One of TYPE_STATIC, TYPE_RUNTIME_INSTANCE
		and TYPE_OPCODE_INSTANCE.
	*/
	public int getType()
	{
		return m_nType;
	}
}



/*** OpcodeClass.java ***/
