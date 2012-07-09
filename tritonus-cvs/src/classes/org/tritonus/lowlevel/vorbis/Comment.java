/*
 *	Comment.java
 *
 *	This file is part of Tritonus: http://www.tritonus.org/
 */

/*
 *  Copyright (c) 2000 - 2001 by Matthias Pfisterer
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

package org.tritonus.lowlevel.vorbis;

import org.tritonus.lowlevel.ogg.Ogg;
import org.tritonus.share.TDebug;


/** Wrapper for vorbis_info.
 */
public class Comment
{
        static
        {
                Ogg.loadNativeLibrary();
                if (TDebug.TraceVorbisNative)
                {
                        setTrace(true);
                }
        }


	/**
	 *	Holds the pointer to vorbis_info
	 *	for the native code.
	 *	This must be long to be 64bit-clean.
	 */
	@SuppressWarnings("unused")
	private long	m_lNativeHandle;



	public Comment()
	{
		if (TDebug.TraceVorbisNative) { TDebug.out("Comment.<init>(): begin"); }
		int	nReturn = malloc();
		if (nReturn < 0)
		{
			throw new RuntimeException("malloc of vorbis_comment failed");
		}
		if (TDebug.TraceVorbisNative) { TDebug.out("Comment.<init>(): end"); }
	}



	public void finalize()
	{
		// TODO: call free()
		// call super.finalize() first or last?
		// and introduce a flag if free() has already been called?
	}



	private native int malloc();
	public native void free();


	/** Calls vorbis_comment_init().
	 */
	public native void init();


	/** Calls vorbis_comment_add().
	 */
	public native void addComment(String strComment);


	/** Calls vorbis_comment_add_tag().
	 */
	public native void addTag(String strTag, String strComment);



	/** Calls vorbis_comment_query_count().
	 */
	public native int queryCount(String strTag);


	/** Calls vorbis_comment_query().
	 */
	public native String query(String strTag, int nIndex);


	/** Accesses user_comments, comment_lengths and comments.
	 */
	public native String[] getUserComments();



	/** Accesses vendor.
	 */
	public native String getVendor();



	/** Calls vorbis_comment_clear().
	 */
	public native void clear();


// 	/** Calls vorbis_commentheader_out().
// 	 */
// 	public native void headerOut(Packet packet);

	private static native void setTrace(boolean bTrace);
}





/*** Comment.java ***/
