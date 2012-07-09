/*
 *	GlobalsSearcher.java
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

import org.tritonus.saol.sablecc.analysis.*;
import org.tritonus.saol.sablecc.node.*;



public class GlobalsSearcher
extends DepthFirstAdapter
{
	private SAOLGlobals	m_saolGlobals;


	public GlobalsSearcher(SAOLGlobals saolGlobals)
	{
		m_saolGlobals = saolGlobals;
	}



	public SAOLGlobals getSAOLGlobals()
	{
		return m_saolGlobals;
	}


	public void outASrateRtparam(ASrateRtparam node)
	{
		TInteger	integer = node.getInteger();
		String		strInt = integer.getText();
		int		nARate = Integer.parseInt(strInt);
		getSAOLGlobals().setARate(nARate);
	}


// 	public void caseTNumber(TNumber node)
// 	{// When we see a number, we print it.
// 		System.out.print(node);
// 	}

// 	public void outAPlusExpr(APlusExpr node)
// 	{// out of alternative {plus} in Expr, we print the plus.
// 		System.out.print(node.getPlus());
// 	}

// 	public void outAMinusExpr(AMinusExpr node)
// 	{// out of alternative {minus} in Expr, we print the minus.
// 		System.out.print(node.getMinus());
// 	}

// 	public void outAMultFactor(AMultFactor node)
// 	{// out of alternative {mult} in Factor, we print the mult.
// 		System.out.print(node.getMult());
// 	}

// 	public void outADivFactor(ADivFactor node)
// 	{// out of alternative {div} in Factor, we print the div.
// 		System.out.print(node.getDiv());
// 	}

// 	public void outAModFactor(AModFactor node)
// 	{// out of alternative {mod} in Factor, we print the mod.
// 		System.out.print(node.getMod());
// 	}
}



/*** GlobalsSearcher.java ***/
