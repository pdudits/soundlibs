/*
 *	TemplateEntry.java
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

import org.tritonus.saol.sablecc.node.ATemplatedeclTemplatedecl;



public class TemplateEntry
{
	private String				m_strTemplateName;
	private ATemplatedeclTemplatedecl	m_startNode;



	public TemplateEntry(String strTemplateName,
			       ATemplatedeclTemplatedecl startNode)
	{
		m_strTemplateName = strTemplateName;
		m_startNode = startNode;
	}



	public String getTemplateName()
	{
		return m_strTemplateName;
	}



	public ATemplatedeclTemplatedecl getStartNode()
	{
		return m_startNode;
	}
}



/*** TemplateEntry.java ***/
