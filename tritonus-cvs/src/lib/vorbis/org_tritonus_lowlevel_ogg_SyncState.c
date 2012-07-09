/*
 *	org_tritonus_lowlevel_ogg_SyncState.c
 */

/*
 *  Copyright (c) 2003 by Matthias Pfisterer <Matthias.Pfisterer@web.de>
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

#include "common.h"
#include "org_tritonus_lowlevel_ogg_SyncState.h"



HandleFieldHandlerDeclaration(handler, ogg_sync_state*)

ogg_page*
getPageNativeHandle(JNIEnv *env, jobject obj);


/*
 * Class:     org_tritonus_lowlevel_ogg_SyncState
 * Method:    malloc
 * Signature: ()I
 */
JNIEXPORT jint JNICALL
Java_org_tritonus_lowlevel_ogg_SyncState_malloc
(JNIEnv* env, jobject obj)
{
	ogg_sync_state*		handle;
	int			nReturn;

	if (debug_flag) { fprintf(debug_file, "Java_org_tritonus_lowlevel_ogg_SyncState_malloc(): begin\n"); }
	handle = malloc(sizeof(ogg_sync_state));
	if (debug_flag) { fprintf(debug_file, "Java_org_tritonus_lowlevel_ogg_SyncState_malloc(): handle: %p\n", handle); }
	setHandle(env, obj, handle);
	nReturn = (handle == NULL) ? -1 : 0;
	if (debug_flag) { fprintf(debug_file, "Java_org_tritonus_lowlevel_ogg_SyncState_malloc(): end\n"); }
	return nReturn;
}



/*
 * Class:     org_tritonus_lowlevel_ogg_SyncState
 * Method:    free
 * Signature: ()V
 */
JNIEXPORT void JNICALL
Java_org_tritonus_lowlevel_ogg_SyncState_free
(JNIEnv* env, jobject obj)
{
	ogg_sync_state*	handle;

	if (debug_flag) { fprintf(debug_file, "Java_org_tritonus_lowlevel_ogg_SyncState_free(): begin\n"); }
	handle = getHandle(env, obj);
	free(handle);
	if (debug_flag) { fprintf(debug_file, "Java_org_tritonus_lowlevel_ogg_SyncState_free(): end\n"); }
}



/*
 * Class:     org_tritonus_lowlevel_ogg_SyncState
 * Method:    init
 * Signature: ()V
 */
JNIEXPORT void JNICALL
Java_org_tritonus_lowlevel_ogg_SyncState_init
(JNIEnv* env, jobject obj)
{
	ogg_sync_state*	handle;

	if (debug_flag) { fprintf(debug_file, "Java_org_tritonus_lowlevel_ogg_SyncState_init(): begin\n"); }
	handle = getHandle(env, obj);
	ogg_sync_init(handle);
	if (debug_flag) { fprintf(debug_file, "Java_org_tritonus_lowlevel_ogg_SyncState_init(): end\n"); }
}



/*
 * Class:     org_tritonus_lowlevel_ogg_SyncState
 * Method:    clear
 * Signature: ()V
 */
JNIEXPORT void JNICALL
Java_org_tritonus_lowlevel_ogg_SyncState_clear
(JNIEnv* env, jobject obj)
{
	ogg_sync_state*	handle;

	if (debug_flag) { fprintf(debug_file, "Java_org_tritonus_lowlevel_ogg_SyncState_clear(): begin\n"); }
	handle = getHandle(env, obj);
	ogg_sync_clear(handle);
	if (debug_flag) { fprintf(debug_file, "Java_org_tritonus_lowlevel_ogg_SyncState_clear(): end\n"); }
}



/*
 * Class:     org_tritonus_lowlevel_ogg_SyncState
 * Method:    reset
 * Signature: ()V
 */
JNIEXPORT void JNICALL
Java_org_tritonus_lowlevel_ogg_SyncState_reset
(JNIEnv* env, jobject obj)
{
	ogg_sync_state*	handle;

	if (debug_flag) { fprintf(debug_file, "Java_org_tritonus_lowlevel_ogg_SyncState_reset(): begin\n"); }
	handle = getHandle(env, obj);
	ogg_sync_reset(handle);
	if (debug_flag) { fprintf(debug_file, "Java_org_tritonus_lowlevel_ogg_SyncState_reset(): end\n"); }
}



/*
 * Class:     org_tritonus_lowlevel_ogg_SyncState
 * Method:    destroy
 * Signature: ()V
 */
JNIEXPORT void JNICALL
Java_org_tritonus_lowlevel_ogg_SyncState_destroy
(JNIEnv* env, jobject obj)
{
	ogg_sync_state*	handle;

	if (debug_flag) { fprintf(debug_file, "Java_org_tritonus_lowlevel_ogg_SyncState_destroy(): begin\n"); }
	handle = getHandle(env, obj);
	ogg_sync_destroy(handle);
	if (debug_flag) { fprintf(debug_file, "Java_org_tritonus_lowlevel_ogg_SyncState_destroy(): end\n"); }
}



/*
 * Class:     org_tritonus_lowlevel_ogg_SyncState
 * Method:    write
 * Signature: ([BI)I
 */
JNIEXPORT jint JNICALL
Java_org_tritonus_lowlevel_ogg_SyncState_write
(JNIEnv* env, jobject obj, jbyteArray abBuffer, jint nBytes)
{
	ogg_sync_state*	handle;
	char*			buffer;
	int			nReturn;

	if (debug_flag) { fprintf(debug_file, "Java_org_tritonus_lowlevel_ogg_SyncState_write(): begin\n"); }
	handle = getHandle(env, obj);
	buffer = ogg_sync_buffer(handle, nBytes);
	(*env)->GetByteArrayRegion(env, abBuffer,
				   0, nBytes, (jbyte*) buffer);
	nReturn = ogg_sync_wrote(handle, nBytes);
	if (debug_flag) { fprintf(debug_file, "Java_org_tritonus_lowlevel_ogg_SyncState_write(): end\n"); }
	return nReturn;
}



/*
 * Class:     org_tritonus_lowlevel_ogg_SyncState
 * Method:    pageseek
 * Signature: (Lorg/tritonus/lowlevel/ogg/Page;)I
 */
JNIEXPORT jint JNICALL
Java_org_tritonus_lowlevel_ogg_SyncState_pageseek
(JNIEnv* env, jobject obj, jobject page)
{
	ogg_sync_state*	handle;
	ogg_page*		pageHandle;
	int			nReturn;

	if (debug_flag) { fprintf(debug_file, "Java_org_tritonus_lowlevel_ogg_SyncState_pageseek(): begin\n"); }
	handle = getHandle(env, obj);
	pageHandle = getPageNativeHandle(env, page);
	nReturn = ogg_sync_pageseek(handle, pageHandle);
	if (debug_flag) { fprintf(debug_file, "Java_org_tritonus_lowlevel_ogg_SyncState_pageseek(): end\n"); }
	return nReturn;
}



/*
 * Class:     org_tritonus_lowlevel_ogg_SyncState
 * Method:    pageOut
 * Signature: (Lorg/tritonus/lowlevel/ogg/Page;)I
 */
JNIEXPORT jint JNICALL
Java_org_tritonus_lowlevel_ogg_SyncState_pageOut
(JNIEnv* env, jobject obj, jobject page)
{
	ogg_sync_state*	handle;
	ogg_page*		pageHandle;
	int			nReturn;

	if (debug_flag) { fprintf(debug_file, "Java_org_tritonus_lowlevel_ogg_SyncState_pageOut(): begin\n"); }
	handle = getHandle(env, obj);
	pageHandle = getPageNativeHandle(env, page);
	nReturn = ogg_sync_pageout(handle, pageHandle);
	if (debug_flag) { fprintf(debug_file, "Java_org_tritonus_lowlevel_ogg_SyncState_pageOut(): end\n"); }
	return nReturn;
}



/*
 * Class:     org_tritonus_lowlevel_ogg_SyncState
 * Method:    setTrace
 * Signature: (Z)V
 */
JNIEXPORT void JNICALL
Java_org_tritonus_lowlevel_ogg_SyncState_setTrace
(JNIEnv* env, jclass cls, jboolean bTrace)
{
	debug_flag = bTrace;
	debug_file = stderr;
}



/*** org_tritonus_lowlevel_ogg_SyncState.c ***/
