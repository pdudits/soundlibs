/*
 *	org_tritonus_lowlevel_ogg_Buffer.c
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
#include "org_tritonus_lowlevel_ogg_StreamState.h"

HandleFieldHandlerDeclaration(handler, oggpack_buffer*)



/*
 * Class:     org_tritonus_lowlevel_ogg_Buffer
 * Method:    malloc
 * Signature: ()I
 */
JNIEXPORT jint JNICALL
Java_org_tritonus_lowlevel_ogg_Buffer_malloc
(JNIEnv* env, jobject obj)
{
	oggpack_buffer*		handle;
	int			nReturn;

	if (debug_flag) { fprintf(debug_file, "Java_org_tritonus_lowlevel_ogg_Buffer_malloc(): begin\n"); }
	handle = malloc(sizeof(oggpack_buffer));
	if (debug_flag) { fprintf(debug_file, "Java_org_tritonus_lowlevel_ogg_Buffer_malloc(): handle: %p\n", handle); }
	setHandle(env, obj, handle);
	nReturn = (handle == NULL) ? -1 : 0;
	if (debug_flag) { fprintf(debug_file, "Java_org_tritonus_lowlevel_ogg_Buffer_malloc(): end\n"); }
	return nReturn;
}



/*
 * Class:     org_tritonus_lowlevel_ogg_Buffer
 * Method:    free
 * Signature: ()V
 */
JNIEXPORT void JNICALL
Java_org_tritonus_lowlevel_ogg_Buffer_free
(JNIEnv* env, jobject obj)
{
	oggpack_buffer*	handle;

	if (debug_flag) { fprintf(debug_file, "Java_org_tritonus_lowlevel_ogg_Buffer_free(): begin\n"); }
	handle = getHandle(env, obj);
	free(handle);
	if (debug_flag) { fprintf(debug_file, "Java_org_tritonus_lowlevel_ogg_Buffer_free(): end\n"); }
}



/*
 * Class:     org_tritonus_lowlevel_ogg_Buffer
 * Method:    writeInit
 * Signature: ()V
 */
JNIEXPORT void JNICALL
Java_org_tritonus_lowlevel_ogg_Buffer_writeInit
(JNIEnv* env, jobject obj)
{
	oggpack_buffer*	handle;

	if (debug_flag) { fprintf(debug_file, "Java_org_tritonus_lowlevel_ogg_Buffer_writeInit(): begin\n"); }
	handle = getHandle(env, obj);
	oggpack_writeinit(handle);
	if (debug_flag) { fprintf(debug_file, "Java_org_tritonus_lowlevel_ogg_Buffer_writeInit(): end\n"); }
}



/*
 * Class:     org_tritonus_lowlevel_ogg_Buffer
 * Method:    writeTrunc
 * Signature: (I)V
 */
JNIEXPORT void JNICALL
Java_org_tritonus_lowlevel_ogg_Buffer_writeTrunc
(JNIEnv* env, jobject obj, jint nBits)
{
	oggpack_buffer*	handle;

	if (debug_flag) { fprintf(debug_file, "Java_org_tritonus_lowlevel_ogg_Buffer_writeTrunc(): begin\n"); }
	handle = getHandle(env, obj);
	oggpack_writetrunc(handle, nBits);
	if (debug_flag) { fprintf(debug_file, "Java_org_tritonus_lowlevel_ogg_Buffer_writeTrunc(): end\n"); }
}



/*
 * Class:     org_tritonus_lowlevel_ogg_Buffer
 * Method:    writeAlign
 * Signature: ()V
 */
JNIEXPORT void JNICALL
Java_org_tritonus_lowlevel_ogg_Buffer_writeAlign
(JNIEnv* env, jobject obj)
{
	oggpack_buffer*	handle;

	if (debug_flag) { fprintf(debug_file, "Java_org_tritonus_lowlevel_ogg_Buffer_writeAlign(): begin\n"); }
	handle = getHandle(env, obj);
	oggpack_writealign(handle);
	if (debug_flag) { fprintf(debug_file, "Java_org_tritonus_lowlevel_ogg_Buffer_writeAlign(): end\n"); }
}



/*
 * Class:     org_tritonus_lowlevel_ogg_Buffer
 * Method:    writeCopy
 * Signature: ([BI)V
 */
JNIEXPORT void JNICALL
Java_org_tritonus_lowlevel_ogg_Buffer_writeCopy
(JNIEnv* env, jobject obj, jbyteArray abSource, jint nBits)
{
	oggpack_buffer*	handle;
	jbyte*		source;

	if (debug_flag) { fprintf(debug_file, "Java_org_tritonus_lowlevel_ogg_Buffer_writeCopy(): begin\n"); }
	handle = getHandle(env, obj);
	source = (*env)->GetByteArrayElements(env, abSource, NULL);
	oggpack_writecopy(handle, source, nBits);
	(*env)->ReleaseByteArrayElements(env, abSource, source, 0);
	if (debug_flag) { fprintf(debug_file, "Java_org_tritonus_lowlevel_ogg_Buffer_writeCopy(): end\n"); }
}



/*
 * Class:     org_tritonus_lowlevel_ogg_Buffer
 * Method:    reset
 * Signature: ()V
 */
JNIEXPORT void JNICALL
Java_org_tritonus_lowlevel_ogg_Buffer_reset
(JNIEnv* env, jobject obj)
{
	oggpack_buffer*	handle;

	if (debug_flag) { fprintf(debug_file, "Java_org_tritonus_lowlevel_ogg_Buffer_reset(): begin\n"); }
	handle = getHandle(env, obj);
	oggpack_reset(handle);
	if (debug_flag) { fprintf(debug_file, "Java_org_tritonus_lowlevel_ogg_Buffer_reset(): end\n"); }
}



/*
 * Class:     org_tritonus_lowlevel_ogg_Buffer
 * Method:    writeClear
 * Signature: ()V
 */
JNIEXPORT void JNICALL
Java_org_tritonus_lowlevel_ogg_Buffer_writeClear
(JNIEnv* env, jobject obj)
{
	oggpack_buffer*	handle;

	if (debug_flag) { fprintf(debug_file, "Java_org_tritonus_lowlevel_ogg_Buffer_writeClear(): begin\n"); }
	handle = getHandle(env, obj);
	oggpack_writeclear(handle);
	if (debug_flag) { fprintf(debug_file, "Java_org_tritonus_lowlevel_ogg_Buffer_writeClear(): end\n"); }
}



/*
 * Class:     org_tritonus_lowlevel_ogg_Buffer
 * Method:    readInit
 * Signature: ([BI)V
 */
JNIEXPORT void JNICALL
Java_org_tritonus_lowlevel_ogg_Buffer_readInit
(JNIEnv* env, jobject obj, jbyteArray abBuffer, jint nBytes)
{
	oggpack_buffer*	handle;
	jbyte*		buffer;

	if (debug_flag) { fprintf(debug_file, "Java_org_tritonus_lowlevel_ogg_Buffer_readInit(): begin\n"); }
	if (debug_flag) { fprintf(debug_file, "Java_org_tritonus_lowlevel_ogg_Buffer_readInit(): nBytes: %d\n", nBytes); }
	handle = getHandle(env, obj);
	buffer = (*env)->GetByteArrayElements(env, abBuffer, NULL);
	if (debug_flag) { fprintf(debug_file, "Java_org_tritonus_lowlevel_ogg_Buffer_readInit(): buffer[0]: %d\n", buffer[0]); }
	if (debug_flag) { fprintf(debug_file, "Java_org_tritonus_lowlevel_ogg_Buffer_readInit(): buffer[1]: %d\n", buffer[1]); }
	if (debug_flag) { fprintf(debug_file, "Java_org_tritonus_lowlevel_ogg_Buffer_readInit(): buffer[2]: %d\n", buffer[2]); }
	oggpack_readinit(handle, (unsigned char*) buffer, nBytes);
	(*env)->ReleaseByteArrayElements(env, abBuffer, buffer, 0);
	if (debug_flag) { fprintf(debug_file, "Java_org_tritonus_lowlevel_ogg_Buffer_readInit(): end\n"); }
}



/*
 * Class:     org_tritonus_lowlevel_ogg_Buffer
 * Method:    write
 * Signature: (II)V
 */
JNIEXPORT void JNICALL
Java_org_tritonus_lowlevel_ogg_Buffer_write
(JNIEnv* env, jobject obj, jint nValue, jint nBits)
{
	oggpack_buffer*	handle;

	if (debug_flag) { fprintf(debug_file, "Java_org_tritonus_lowlevel_ogg_Buffer_write(): begin\n"); }
	handle = getHandle(env, obj);
	oggpack_write(handle, nValue, nBits);
	if (debug_flag) { fprintf(debug_file, "Java_org_tritonus_lowlevel_ogg_Buffer_write(): end\n"); }
}



/*
 * Class:     org_tritonus_lowlevel_ogg_Buffer
 * Method:    look
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL
Java_org_tritonus_lowlevel_ogg_Buffer_look
(JNIEnv* env, jobject obj, jint nBits)
{
	oggpack_buffer*	handle;
	int		nReturn;

	if (debug_flag) { fprintf(debug_file, "Java_org_tritonus_lowlevel_ogg_Buffer_look(): begin\n"); }
	handle = getHandle(env, obj);
	nReturn = oggpack_look(handle, nBits);
	if (debug_flag) { fprintf(debug_file, "Java_org_tritonus_lowlevel_ogg_Buffer_look(): end\n"); }
	return nReturn;
}



/*
 * Class:     org_tritonus_lowlevel_ogg_Buffer
 * Method:    look1
 * Signature: ()I
 */
JNIEXPORT jint JNICALL
Java_org_tritonus_lowlevel_ogg_Buffer_look1
(JNIEnv* env, jobject obj)
{
	oggpack_buffer*	handle;
	int		nReturn;

	if (debug_flag) { fprintf(debug_file, "Java_org_tritonus_lowlevel_ogg_Buffer_look1(): begin\n"); }
	handle = getHandle(env, obj);
	nReturn = oggpack_look1(handle);
	if (debug_flag) { fprintf(debug_file, "Java_org_tritonus_lowlevel_ogg_Buffer_look1(): end\n"); }
	return nReturn;
}



/*
 * Class:     org_tritonus_lowlevel_ogg_Buffer
 * Method:    adv
 * Signature: (I)V
 */
JNIEXPORT void JNICALL
Java_org_tritonus_lowlevel_ogg_Buffer_adv
(JNIEnv* env, jobject obj, jint nBits)
{
	oggpack_buffer*	handle;

	if (debug_flag) { fprintf(debug_file, "Java_org_tritonus_lowlevel_ogg_Buffer_adv(): begin\n"); }
	handle = getHandle(env, obj);
	oggpack_adv(handle, nBits);
	if (debug_flag) { fprintf(debug_file, "Java_org_tritonus_lowlevel_ogg_Buffer_adv(): end\n"); }
}



/*
 * Class:     org_tritonus_lowlevel_ogg_Buffer
 * Method:    adv1
 * Signature: ()V
 */
JNIEXPORT void JNICALL
Java_org_tritonus_lowlevel_ogg_Buffer_adv1
(JNIEnv* env, jobject obj)
{
	oggpack_buffer*	handle;

	if (debug_flag) { fprintf(debug_file, "Java_org_tritonus_lowlevel_ogg_Buffer_adv1(): begin\n"); }
	handle = getHandle(env, obj);
	oggpack_adv1(handle);
	if (debug_flag) { fprintf(debug_file, "Java_org_tritonus_lowlevel_ogg_Buffer_adv1(): end\n"); }
}



/*
 * Class:     org_tritonus_lowlevel_ogg_Buffer
 * Method:    read
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL
Java_org_tritonus_lowlevel_ogg_Buffer_read
(JNIEnv* env, jobject obj, jint nBits)
{
	oggpack_buffer*	handle;
	int		nReturn;

	if (debug_flag) { fprintf(debug_file, "Java_org_tritonus_lowlevel_ogg_Buffer_read(): begin\n"); }
	handle = getHandle(env, obj);
	nReturn = oggpack_read(handle, nBits);
	if (debug_flag) { fprintf(debug_file, "Java_org_tritonus_lowlevel_ogg_Buffer_read(): end\n"); }
	return nReturn;
}



/*
 * Class:     org_tritonus_lowlevel_ogg_Buffer
 * Method:    read1
 * Signature: ()I
 */
JNIEXPORT jint JNICALL
Java_org_tritonus_lowlevel_ogg_Buffer_read1
(JNIEnv* env, jobject obj)
{
	oggpack_buffer*	handle;
	int		nReturn;

	if (debug_flag) { fprintf(debug_file, "Java_org_tritonus_lowlevel_ogg_Buffer_read1(): begin\n"); }
	handle = getHandle(env, obj);
	nReturn = oggpack_read1(handle);
	if (debug_flag) { fprintf(debug_file, "Java_org_tritonus_lowlevel_ogg_Buffer_read1(): end\n"); }
	return nReturn;
}



/*
 * Class:     org_tritonus_lowlevel_ogg_Buffer
 * Method:    bytes
 * Signature: ()I
 */
JNIEXPORT jint JNICALL
Java_org_tritonus_lowlevel_ogg_Buffer_bytes
(JNIEnv* env, jobject obj)
{
	oggpack_buffer*	handle;
	int		nReturn;

	if (debug_flag) { fprintf(debug_file, "Java_org_tritonus_lowlevel_ogg_Buffer_bytes(): begin\n"); }
	handle = getHandle(env, obj);
	nReturn = oggpack_bytes(handle);
	if (debug_flag) { fprintf(debug_file, "Java_org_tritonus_lowlevel_ogg_Buffer_bytes(): end\n"); }
	return nReturn;
}



/*
 * Class:     org_tritonus_lowlevel_ogg_Buffer
 * Method:    bits
 * Signature: ()I
 */
JNIEXPORT jint JNICALL
Java_org_tritonus_lowlevel_ogg_Buffer_bits
(JNIEnv* env, jobject obj)
{
	oggpack_buffer*	handle;
	int		nReturn;

	if (debug_flag) { fprintf(debug_file, "Java_org_tritonus_lowlevel_ogg_Buffer_bits(): begin\n"); }
	handle = getHandle(env, obj);
	nReturn = oggpack_bits(handle);
	if (debug_flag) { fprintf(debug_file, "Java_org_tritonus_lowlevel_ogg_Buffer_bits(): end\n"); }
	return nReturn;
}



/*
 * Class:     org_tritonus_lowlevel_ogg_Buffer
 * Method:    getBuffer
 * Signature: ()[B
 */
JNIEXPORT jbyteArray JNICALL
Java_org_tritonus_lowlevel_ogg_Buffer_getBuffer
(JNIEnv* env, jobject obj)
{
	oggpack_buffer*	handle;
	unsigned char*	buffer;
	jbyteArray	abBuffer;

	if (debug_flag) { fprintf(debug_file, "Java_org_tritonus_lowlevel_ogg_Buffer_getBuffer(): begin\n"); }
	handle = getHandle(env, obj);
	buffer = oggpack_get_buffer(handle);
	abBuffer = (*env)->NewByteArray(env, handle->storage);
	(*env)->SetByteArrayRegion(env, abBuffer, 0, handle->storage,
								(jbyte*) buffer);
	if (debug_flag) { fprintf(debug_file, "Java_org_tritonus_lowlevel_ogg_Buffer_getBuffer(): end\n"); }
	return abBuffer;
}



/*
 * Class:     org_tritonus_lowlevel_ogg_Buffer
 * Method:    setTrace
 * Signature: (Z)V
 */
JNIEXPORT void JNICALL
Java_org_tritonus_lowlevel_ogg_Buffer_setTrace
(JNIEnv* env, jclass cls, jboolean bTrace)
{
	debug_flag = bTrace;
	debug_file = stderr;
}



/*** org_tritonus_lowlevel_ogg_Buffer.c ***/
