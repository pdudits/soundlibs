/*
 *	org_tritonus_lowlevel_vorbis_DspState.c
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
#include "org_tritonus_lowlevel_vorbis_DspState.h"


HandleFieldHandlerDeclaration(handler, vorbis_dsp_state*)

vorbis_dsp_state*
getDspStateNativeHandle(JNIEnv *env, jobject obj)
{
        return getHandle(env, obj);
}


vorbis_info*
getInfoNativeHandle(JNIEnv *env, jobject obj);
vorbis_comment*
getCommentNativeHandle(JNIEnv *env, jobject obj);
vorbis_block*
getBlockNativeHandle(JNIEnv *env, jobject obj);
ogg_packet*
getPacketNativeHandle(JNIEnv *env, jobject obj);


/*
 * Class:     org_tritonus_lowlevel_vorbis_DspState
 * Method:    malloc
 * Signature: ()I
 */
JNIEXPORT jint JNICALL
Java_org_tritonus_lowlevel_vorbis_DspState_malloc
(JNIEnv* env, jobject obj)
{
	vorbis_dsp_state*	handle;
	int			nReturn;

	if (debug_flag) { fprintf(debug_file, "Java_org_tritonus_lowlevel_vorbis_DspState_malloc(): begin\n"); }
	handle = malloc(sizeof(vorbis_dsp_state));
	if (debug_flag) { fprintf(debug_file, "Java_org_tritonus_lowlevel_vorbis_DspState_malloc(): handle: %p\n", handle); }
	setHandle(env, obj, handle);
	nReturn = (handle == NULL) ? -1 : 0;
	if (debug_flag) { fprintf(debug_file, "Java_org_tritonus_lowlevel_vorbis_DspState_malloc(): end\n"); }
	return nReturn;
}



/*
 * Class:     org_tritonus_lowlevel_vorbis_DspState
 * Method:    free
 * Signature: ()V
 */
JNIEXPORT void JNICALL
Java_org_tritonus_lowlevel_vorbis_DspState_free
(JNIEnv* env, jobject obj)
{
	vorbis_dsp_state*	handle;

	if (debug_flag) { fprintf(debug_file, "Java_org_tritonus_lowlevel_vorbis_DspState_free(): begin\n"); }
	handle = getHandle(env, obj);
	free(handle);
	if (debug_flag) { fprintf(debug_file, "Java_org_tritonus_lowlevel_vorbis_DspState_free(): end\n"); }
}



/*
 * Class:     org_tritonus_lowlevel_vorbis_DspState
 * Method:    initAnalysis
 * Signature: (Lorg/tritonus/lowlevel/vorbis/Info;)I
 */
JNIEXPORT jint JNICALL
Java_org_tritonus_lowlevel_vorbis_DspState_initAnalysis
(JNIEnv* env, jobject obj, jobject info)
{
	vorbis_dsp_state*	handle;
	vorbis_info*		infoHandle;
	int			nReturn;

	if (debug_flag) { fprintf(debug_file, "Java_org_tritonus_lowlevel_vorbis_DspState_initAnalysis(): begin\n"); }
	handle = getHandle(env, obj);
	infoHandle = getInfoNativeHandle(env, info);
	nReturn = vorbis_analysis_init(handle, infoHandle);
	if (debug_flag) { fprintf(debug_file, "Java_org_tritonus_lowlevel_vorbis_DspState_initAnalysis(): end\n"); }
	return nReturn;
}



/*
 * Class:     org_tritonus_lowlevel_vorbis_DspState
 * Method:    headerOut
 * Signature: (Lorg/tritonus/lowlevel/vorbis/Comment;Lorg/tritonus/lowlevel/ogg/Packet;Lorg/tritonus/lowlevel/ogg/Packet;Lorg/tritonus/lowlevel/ogg/Packet;)I
 */
JNIEXPORT jint JNICALL
Java_org_tritonus_lowlevel_vorbis_DspState_headerOut
(JNIEnv* env, jobject obj,
 jobject comment,
 jobject packet,
 jobject commentPacket,
 jobject codePacket)
{
	vorbis_dsp_state*	handle;
	vorbis_comment*		commentHandle;
	ogg_packet*		packetHandle;
	ogg_packet*		commentPacketHandle;
	ogg_packet*		codePacketHandle;
	int			nReturn;

	if (debug_flag) { fprintf(debug_file, "Java_org_tritonus_lowlevel_vorbis_DspState_headerOut(): begin\n"); }
	handle = getHandle(env, obj);
	commentHandle = getCommentNativeHandle(env, comment);
	packetHandle = getPacketNativeHandle(env, packet);
	commentPacketHandle = getPacketNativeHandle(env, commentPacket);
	codePacketHandle = getPacketNativeHandle(env, codePacket);
	nReturn = vorbis_analysis_headerout(handle,
					    commentHandle,
					    packetHandle,
					    commentPacketHandle,
					    codePacketHandle);
	if (debug_flag) { fprintf(debug_file, "Java_org_tritonus_lowlevel_vorbis_DspState_headerOut(): end\n"); }
	return nReturn;
}



/*
 * Class:     org_tritonus_lowlevel_vorbis_DspState
 * Method:    write
 * Signature: ([[FI)I
 */
JNIEXPORT jint JNICALL
Java_org_tritonus_lowlevel_vorbis_DspState_write
(JNIEnv* env, jobject obj, jobjectArray afValues, jint nValues)
{
	vorbis_dsp_state*	handle;
	float*			bufferPointer;
	int			nObjectArrayLength;
	int			i;
	jfloatArray		floatArray;
	int			nReturn;

	if (debug_flag) { fprintf(debug_file, "Java_org_tritonus_lowlevel_vorbis_DspState_write(): begin\n"); }
	handle = getHandle(env, obj);
	bufferPointer = vorbis_analysis_buffer(handle, nValues)[0];
	if (debug_flag) { fprintf(debug_file, "Java_org_tritonus_lowlevel_vorbis_DspState_write(): bufferPointer: %p\n", bufferPointer); }
	if (afValues != NULL)
	{
		nObjectArrayLength = (*env)->GetArrayLength(env, afValues);
		if (debug_flag) { fprintf(debug_file, "Java_org_tritonus_lowlevel_vorbis_DspState_write(): objectArray length: %d\n", nObjectArrayLength); }
		for (i = 0; i < nObjectArrayLength; i++)
		{
			floatArray = (jfloatArray) (*env)->GetObjectArrayElement(env, afValues, i);
			if (debug_flag) { fprintf(debug_file, "Java_org_tritonus_lowlevel_vorbis_DspState_write(): floatArray: %p\n", floatArray); }
			(*env)->GetFloatArrayRegion(env, floatArray,
						    0, nValues, bufferPointer);
			bufferPointer += nValues;
		}
	}
	nReturn = vorbis_analysis_wrote(handle, nValues);
	if (debug_flag) { fprintf(debug_file, "Java_org_tritonus_lowlevel_vorbis_DspState_write(): end\n"); }
	return nReturn;
}



/*
 * Class:     org_tritonus_lowlevel_vorbis_DspState
 * Method:    blockOut
 * Signature: (Lorg/tritonus/lowlevel/vorbis/Block;)I
 */
JNIEXPORT jint JNICALL
Java_org_tritonus_lowlevel_vorbis_DspState_blockOut
(JNIEnv* env, jobject obj, jobject block)
{
	vorbis_dsp_state*	handle;
	vorbis_block*		blockHandle;
	int			nReturn;

	if (debug_flag) { fprintf(debug_file, "Java_org_tritonus_lowlevel_vorbis_DspState_blockOut(): begin\n"); }
	handle = getHandle(env, obj);
	blockHandle = getBlockNativeHandle(env, block);
	nReturn = vorbis_analysis_blockout(handle, blockHandle);
	if (debug_flag) { fprintf(debug_file, "Java_org_tritonus_lowlevel_vorbis_DspState_blockOut(): end\n"); }
	return nReturn;
}



/*
 * Class:     org_tritonus_lowlevel_vorbis_DspState
 * Method:    flushPacket
 * Signature: (Lorg/tritonus/lowlevel/ogg/Packet;)I
 */
JNIEXPORT jint JNICALL
Java_org_tritonus_lowlevel_vorbis_DspState_flushPacket
(JNIEnv* env, jobject obj, jobject packet)
{
	vorbis_dsp_state*	handle;
	ogg_packet*		packetHandle;
	int			nReturn;

	if (debug_flag) { fprintf(debug_file, "Java_org_tritonus_lowlevel_vorbis_DspState_flushPacket(): begin\n"); }
	handle = getHandle(env, obj);
	packetHandle = getPacketNativeHandle(env, packet);
	nReturn = vorbis_bitrate_flushpacket(handle, packetHandle);
	if (debug_flag) { fprintf(debug_file, "Java_org_tritonus_lowlevel_vorbis_DspState_flushPacket(): end\n"); }
	return nReturn;
}



/*
 * Class:     org_tritonus_lowlevel_vorbis_DspState
 * Method:    initSynthesis
 * Signature: (Lorg/tritonus/lowlevel/vorbis/Info;)I
 */
JNIEXPORT jint JNICALL
Java_org_tritonus_lowlevel_vorbis_DspState_initSynthesis
(JNIEnv* env, jobject obj, jobject info)
{
	vorbis_dsp_state*	handle;
	vorbis_info*		infoHandle;
	int			nReturn;

	if (debug_flag) { fprintf(debug_file, "Java_org_tritonus_lowlevel_vorbis_DspState_initSynthesis(): begin\n"); }
	handle = getHandle(env, obj);
	infoHandle = getInfoNativeHandle(env, info);
	nReturn = vorbis_synthesis_init(handle, infoHandle);
	if (debug_flag) { fprintf(debug_file, "Java_org_tritonus_lowlevel_vorbis_DspState_initSynthesis(): end\n"); }
	return nReturn;
}



/*
 * Class:     org_tritonus_lowlevel_vorbis_DspState
 * Method:    blockIn
 * Signature: (Lorg/tritonus/lowlevel/vorbis/Block;)I
 */
JNIEXPORT jint JNICALL
Java_org_tritonus_lowlevel_vorbis_DspState_blockIn
(JNIEnv* env, jobject obj, jobject block)
{
	vorbis_dsp_state*	handle;
	vorbis_block*		blockHandle;
	int			nReturn;

	if (debug_flag) { fprintf(debug_file, "Java_org_tritonus_lowlevel_vorbis_DspState_blockIn(): begin\n"); }
	handle = getHandle(env, obj);
	blockHandle = getBlockNativeHandle(env, block);
	nReturn = vorbis_synthesis_blockin(handle, blockHandle);
	if (debug_flag) { fprintf(debug_file, "Java_org_tritonus_lowlevel_vorbis_DspState_blockIn(): end\n"); }
	return nReturn;
}



/*
 * Class:     org_tritonus_lowlevel_vorbis_DspState
 * Method:    pcmOut
 * Signature: ([[F)I
 */
JNIEXPORT jint JNICALL
Java_org_tritonus_lowlevel_vorbis_DspState_pcmOut
(JNIEnv* env, jobject obj, jobjectArray afPcm)
{
	vorbis_dsp_state*	handle;
	float**			pcm;
	int			nSamples;
	int			nChannels;
	int			nChannel;
	jfloatArray		floatArray;

	if (debug_flag) { fprintf(debug_file, "Java_org_tritonus_lowlevel_vorbis_DspState_pcmOut(): begin\n"); }
	handle = getHandle(env, obj);
	nSamples = vorbis_synthesis_pcmout(handle, &pcm);
	if (debug_flag) { fprintf(debug_file, "Java_org_tritonus_lowlevel_vorbis_DspState_pcmOut(): samples: %d\n", nSamples); }
	nChannels = handle->vi->channels;
	if (debug_flag) { fprintf(debug_file, "Java_org_tritonus_lowlevel_vorbis_DspState_pcmOut(): channels: %d\n", nChannels); }
	for (nChannel = 0; nChannel < nChannels; nChannel++)
	{
		floatArray = (*env)->NewFloatArray(env, nSamples);
		if (debug_flag) { fprintf(debug_file, "Java_org_tritonus_lowlevel_vorbis_DspState_pcmOut(): float array: %p\n", floatArray); }
		if (nSamples > 0)
		{
			(*env)->SetFloatArrayRegion(env, floatArray, 0,
						    nSamples, pcm[nChannel]);
		}
		(*env)->SetObjectArrayElement(env, afPcm, nChannel, floatArray);
	}
	if (debug_flag) { fprintf(debug_file, "Java_org_tritonus_lowlevel_vorbis_DspState_pcmOut(): end\n"); }
	return nSamples;
}



/*
 * Class:     org_tritonus_lowlevel_vorbis_DspState
 * Method:    read
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL
Java_org_tritonus_lowlevel_vorbis_DspState_read
(JNIEnv* env, jobject obj, jint nSamples)
{
	vorbis_dsp_state*	handle;
	int			nReturn;

	if (debug_flag) { fprintf(debug_file, "Java_org_tritonus_lowlevel_vorbis_DspState_read(): begin\n"); }
	handle = getHandle(env, obj);
	nReturn = vorbis_synthesis_read(handle, nSamples);
	if (debug_flag) { fprintf(debug_file, "Java_org_tritonus_lowlevel_vorbis_DspState_read(): end\n"); }
	return nReturn;
}



/*
 * Class:     org_tritonus_lowlevel_vorbis_DspState
 * Method:    getSequence
 * Signature: ()J
 */
JNIEXPORT jlong JNICALL
Java_org_tritonus_lowlevel_vorbis_DspState_getSequence
(JNIEnv* env, jobject obj)
{
	vorbis_dsp_state*	handle;
	jlong			lReturn;

	if (debug_flag) { fprintf(debug_file, "Java_org_tritonus_lowlevel_vorbis_DspState_getSequence(): begin\n"); }
	handle = getHandle(env, obj);
	lReturn = handle->sequence;
	if (debug_flag) { fprintf(debug_file, "Java_org_tritonus_lowlevel_vorbis_DspState_getSequence(): end\n"); }
	return lReturn;
}



/*
 * Class:     org_tritonus_lowlevel_vorbis_DspState
 * Method:    clear
 * Signature: ()V
 */
JNIEXPORT void JNICALL
Java_org_tritonus_lowlevel_vorbis_DspState_clear
(JNIEnv* env, jobject obj)
{
	vorbis_dsp_state*	handle;

	if (debug_flag) { fprintf(debug_file, "Java_org_tritonus_lowlevel_vorbis_DspState_clear(): begin\n"); }
	handle = getHandle(env, obj);
	vorbis_dsp_clear(handle);
	if (debug_flag) { fprintf(debug_file, "Java_org_tritonus_lowlevel_vorbis_DspState_clear(): end\n"); }
}



/*
 * Class:     org_tritonus_lowlevel_vorbis_DspState
 * Method:    setTrace
 * Signature: (Z)V
 */
JNIEXPORT void JNICALL
Java_org_tritonus_lowlevel_vorbis_DspState_setTrace
(JNIEnv* env, jclass cls, jboolean bTrace)
{
	debug_flag = bTrace;
	debug_file = stderr;
}



/*** org_tritonus_lowlevel_vorbis_DspState.c ***/
