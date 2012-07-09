/*
 *	org_tritonus_lowlevel_pogg_Packet.c
 */

/*
 *  Copyright (c) 2003 - 2005 by Matthias Pfisterer
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
#include "org_tritonus_lowlevel_pogg_Packet.h"

HandleFieldHandlerDeclaration(handler, ogg_packet*)


ogg_packet*
getPacketNativeHandle(JNIEnv *env, jobject obj)
{
        return getHandle(env, obj);
}
 

/*
 * Class:     org_tritonus_lowlevel_pogg_Packet
 * Method:    malloc
 * Signature: ()I
 */
JNIEXPORT jint JNICALL
Java_org_tritonus_lowlevel_pogg_Packet_malloc
(JNIEnv* env, jobject obj)
{
	ogg_packet*		handle;
	int			nReturn;

	if (debug_flag) { fprintf(debug_file, "Java_org_tritonus_lowlevel_pogg_Packet_malloc(): begin\n"); }
	handle = malloc(sizeof(ogg_packet));
	if (debug_flag) { fprintf(debug_file, "Java_org_tritonus_lowlevel_pogg_Packet_malloc(): handle: %p\n", handle); }
	if (handle != NULL)
		memset(handle, 0, sizeof(*handle));
	setHandle(env, obj, handle);
	nReturn = (handle == NULL) ? -1 : 0;
	if (debug_flag) { fprintf(debug_file, "Java_org_tritonus_lowlevel_pogg_Packet_malloc(): end\n"); }
	return nReturn;
}



/*
 * Class:     org_tritonus_lowlevel_pogg_Packet
 * Method:    free
 * Signature: ()V
 */
JNIEXPORT void JNICALL
Java_org_tritonus_lowlevel_pogg_Packet_free
(JNIEnv* env, jobject obj)
{
	ogg_packet*	handle;

	if (debug_flag) { fprintf(debug_file, "Java_org_tritonus_lowlevel_pogg_Packet_free(): begin\n"); }
	handle = getHandle(env, obj);
	free(handle);
	if (debug_flag) { fprintf(debug_file, "Java_org_tritonus_lowlevel_pogg_Packet_free(): end\n"); }
}



/*
 * Class:     org_tritonus_lowlevel_pogg_Packet
 * Method:    clear
 * Signature: ()V
 */
JNIEXPORT void JNICALL
Java_org_tritonus_lowlevel_pogg_Packet_clear
(JNIEnv* env, jobject obj)
{
	ogg_packet*	handle;

	if (debug_flag) { fprintf(debug_file, "Java_org_tritonus_lowlevel_pogg_Packet_clear(): begin\n"); }
	handle = getHandle(env, obj);
	ogg_packet_clear(handle);
	if (debug_flag) { fprintf(debug_file, "Java_org_tritonus_lowlevel_pogg_Packet_clear(): end\n"); }
}



/*
 * Class:     org_tritonus_lowlevel_pogg_Packet
 * Method:    getData
 * Signature: ()[B
 */
JNIEXPORT jbyteArray JNICALL
Java_org_tritonus_lowlevel_pogg_Packet_getData
(JNIEnv* env, jobject obj)
{
	ogg_packet*	handle;
	jbyteArray	abData;

	if (debug_flag) { fprintf(debug_file, "Java_org_tritonus_lowlevel_pogg_Packet_getData(): begin\n"); }
	handle = getHandle(env, obj);
	if (handle->packet == NULL)
		return 0;
	abData = (*env)->NewByteArray(env, handle->bytes);
	(*env)->SetByteArrayRegion(env, abData, 0, handle->bytes, handle->packet);
	if (debug_flag) { fprintf(debug_file, "Java_org_tritonus_lowlevel_pogg_Packet_getData(): end\n"); }
	return abData;
}



/*
 * Class:     org_tritonus_lowlevel_pogg_Packet
 * Method:    isBos
 * Signature: ()Z
 */
JNIEXPORT jboolean JNICALL
Java_org_tritonus_lowlevel_pogg_Packet_isBos
(JNIEnv* env, jobject obj)
{
	ogg_packet*	handle;
	jboolean	bReturn;

	if (debug_flag) { fprintf(debug_file, "Java_org_tritonus_lowlevel_pogg_Packet_isBos(): begin\n"); }
	handle = getHandle(env, obj);
	if (debug_flag) { fprintf(debug_file, "Java_org_tritonus_lowlevel_pogg_Packet_isBos(): b_o_s: %d\n", (int) handle->b_o_s); }
	bReturn = (handle->b_o_s != 0) ? JNI_TRUE : JNI_FALSE;
	if (debug_flag) { fprintf(debug_file, "Java_org_tritonus_lowlevel_pogg_Packet_isBos(): end\n"); }
	return bReturn;
}



/*
 * Class:     org_tritonus_lowlevel_pogg_Packet
 * Method:    isEos
 * Signature: ()Z
 */
JNIEXPORT jboolean JNICALL
Java_org_tritonus_lowlevel_pogg_Packet_isEos
(JNIEnv* env, jobject obj)
{
	ogg_packet*	handle;
	jboolean	bReturn;

	if (debug_flag) { fprintf(debug_file, "Java_org_tritonus_lowlevel_pogg_Packet_isEos(): begin\n"); }
	handle = getHandle(env, obj);
	bReturn = (handle->e_o_s != 0) ? JNI_TRUE : JNI_FALSE;
	if (debug_flag) { fprintf(debug_file, "Java_org_tritonus_lowlevel_pogg_Packet_isEos(): end\n"); }
	return bReturn;
}


/*
 * Class:     org_tritonus_lowlevel_pogg_Packet
 * Method:    getGranulePos
 * Signature: ()J
 */
JNIEXPORT jlong JNICALL
Java_org_tritonus_lowlevel_pogg_Packet_getGranulePos
(JNIEnv *env, jobject obj)
{
	ogg_packet*	handle = getHandle(env, obj);
	return handle->granulepos;
}



/*
 * Class:     org_tritonus_lowlevel_pogg_Packet
 * Method:    getPacketNo
 * Signature: ()J
 */
JNIEXPORT jlong JNICALL
Java_org_tritonus_lowlevel_pogg_Packet_getPacketNo
(JNIEnv *env, jobject obj)
{
	ogg_packet*	handle = getHandle(env, obj);
	return handle->packetno;
}



/*
 * Class:     org_tritonus_lowlevel_pogg_Packet
 * Method:    setData
 * Signature: ([BI)V
 */
JNIEXPORT void JNICALL
Java_org_tritonus_lowlevel_pogg_Packet_setData
(JNIEnv* env, jobject obj, jbyteArray abData, jint nOffset, jint nLength)
{
	ogg_packet*	handle;
	jbyte* data;

	if (debug_flag) { fprintf(debug_file, "Java_org_tritonus_lowlevel_pogg_Packet_setData(): begin\n"); }
	handle = getHandle(env, obj);
	data = (*env)->GetByteArrayElements(env, abData, NULL);
	/* ATTENTION!! The memory allocated here is not freed! So we have
	 * a memory leak here. */
	handle->packet = malloc(nLength);
	(void) memcpy(handle->packet, data + nOffset, nLength);
	(*env)->ReleaseByteArrayElements(env, abData, data, JNI_ABORT);
	handle->bytes = nLength;
	if (debug_flag) { fprintf(debug_file, "Java_org_tritonus_lowlevel_pogg_Packet_setData(): end\n"); }
}


/*
 * Class:     org_tritonus_lowlevel_pogg_Packet
 * Method:    setFlags
 * Signature: (ZZJ)V
 */
JNIEXPORT void JNICALL
Java_org_tritonus_lowlevel_pogg_Packet_setFlags
(JNIEnv* env, jobject obj, jboolean bBos, jboolean bEos, jlong lGranulePos,
 jlong lPacketNo)
{
	ogg_packet*	handle;

	if (debug_flag) { fprintf(debug_file, "Java_org_tritonus_lowlevel_pogg_Packet_setFlags(): begin\n"); }
	handle = getHandle(env, obj);
	handle->b_o_s = bBos;
	handle->e_o_s = bEos;
	handle->granulepos = lGranulePos;
	handle->packetno = lPacketNo;
	if (debug_flag) { fprintf(debug_file, "Java_org_tritonus_lowlevel_pogg_Packet_setFlags(): end\n"); }
}


/*
 * Class:     org_tritonus_lowlevel_pogg_Packet
 * Method:    setTrace
 * Signature: (Z)V
 */
JNIEXPORT void JNICALL
Java_org_tritonus_lowlevel_pogg_Packet_setTrace
(JNIEnv* env, jclass cls, jboolean bTrace)
{
	debug_flag = bTrace;
	debug_file = stderr;
}



/*** org_tritonus_lowlevel_pogg_Packet.c ***/
