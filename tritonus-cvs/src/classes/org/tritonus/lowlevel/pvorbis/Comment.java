/*
 *	Comment.java
 *
 *	This file is part of Tritonus: http://www.tritonus.org/
 */

/*
 *  Copyright (c) 2000 - 2005 by Matthias Pfisterer
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

package org.tritonus.lowlevel.pvorbis;

import java.util.ArrayList;
import java.util.List;

import org.tritonus.lowlevel.pogg.Buffer;
import org.tritonus.share.TDebug;


/** Wrapper for vorbis_comment.
 */
public class Comment
implements VorbisConstants
{

	private String m_vendor;
	private List<String> m_comments;



	public Comment()
	{
		if (TDebug.TraceVorbisNative) { TDebug.out("Comment.<init>(): begin"); }
		if (TDebug.TraceVorbisNative) { TDebug.out("Comment.<init>(): end"); }
	}



	public void finalize()
	{
		// TODO: call free()
		// call super.finalize() first or last?
		// and introduce a flag if free() has already been called?
	}



	// TODO: since this is now a no-op, remove calls to this method.
 	public void free()
	{
	}


	/** Initializes the comment object.
		Sets the vendor string to null and 
		removes all comments.
	 */
	public void init()
	{
		m_vendor = null;
		m_comments = new ArrayList<String>();
	}



	/** Adds a comment to the list of comments.
		The passed string is added to the list of comments as it is.
		The string should have the format 'TAG=content'.
	 */
	public void addComment(String strComment)
	{
		m_comments.add(strComment);
	}



	/** Adds a comment with a specific tag
	 */
	public void addTag(String strTag, String strContents)
	{
		addComment(strTag + "=" + strContents);
	}


	/** Calls vorbis_comment_query_count().
	 */
	public int queryCount(String strTag)
	{
		String strFullTag = strTag + "=";
		int nCount = 0;
		for (int i = 0; i < m_comments.size(); i++)
		{
			if (m_comments.get(i).startsWith(strFullTag))
			{
				nCount++;
			}
		}
		return nCount;
	}


	/** Calls vorbis_comment_query().
	 */
	public String query(String strTag, int nIndex)
	{
		int nCount = 0;
		for (int i = 0; i < m_comments.size(); i++)
		{
			if (i == nIndex)
			{
				return m_comments.get(i).substring(strTag.length() + 1);
			}
			nCount++;
		}
		return null;
	}



	/** Accesses user_comments, comment_lengths and comments.
	 */
	public String[] getUserComments()
	{
		return m_comments.toArray(new String[m_comments.size()]);
	}


	/** Accesses vendor.
	 */
	public String getVendor()
	{
		return m_vendor;
	}


	/** Calls vorbis_comment_clear().
	 */
	public void clear()
	{
		m_comments.clear();
		m_vendor = null;
	}


	public int pack(Buffer buffer)
	{
		String strVendor = "tritonus.org pvorbis library";
		//String strVendor = "Xiph.Org libVorbis I 20030909";

		/* preamble */  
		buffer.write(0x03, 8);
		buffer.write("vorbis");

		/* vendor */
		buffer.writeWithLength(strVendor);
  
		/* comments */
		buffer.write(m_comments.size(), 32);
		if(m_comments.size() > 0)
		{
			for(int i = 0; i < m_comments.size(); i++)
			{
				buffer.writeWithLength(m_comments.get(i));
			}
		}
		buffer.write(1, 1);

		return 0;
	}


	public int unpack(Buffer buffer)
	{
		String s = buffer.readString();
		if (s == null)
		{
			clear();
			return OV_EBADHEADER;
		}
		m_vendor = s;
		int nNumComments = buffer.read(32);
		if (nNumComments < 0)
		{
			clear();
			return OV_EBADHEADER;
		}
		for (int i = 0; i < nNumComments; i++)
		{
			s = buffer.readString();
			if (s == null)
			{
				clear();
				return OV_EBADHEADER;
			}
			m_comments.add(s);
		}

		/* EOP check */
		if (buffer.read(1) != 1)
		{
			clear();
			return OV_EBADHEADER;
		}

		// everything ok.
		return 0;
	}
}





/*** Comment.java ***/
