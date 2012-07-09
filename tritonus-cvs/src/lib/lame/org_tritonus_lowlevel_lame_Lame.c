/*
 *	org_tritonus_lowlevel_lame_Lame.c
 */


/*
 *  Copyright (c) 2000,2001 by Florian Bomers <http://www.bomers.de>
 *
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
 *
 */


#include	<stdlib.h>
#include	<string.h>

#include	"org_tritonus_lowlevel_lame_Lame.h"

#ifdef USE_LAME_API
#include "lameapi.h"
#endif
#ifdef USE_BLADENC_API
#include "bladenc.h"
#endif


////////////////////////////// JNI //////////////////////////////////////////////

//todo: this is BUGGY ! seg fault...
static void throwRuntimeException(JNIEnv *env, char* pStrMessage) {
	static  jclass	runtimeExceptionClass = NULL;

	if (runtimeExceptionClass == NULL) {
		runtimeExceptionClass = (*env)->FindClass(env, "java/lang/RuntimeException");
		if (runtimeExceptionClass == NULL) {
			(*env)->FatalError(env, "cannot get class object for java.lang.RuntimeException");
		}
	}
	(*env)->ThrowNew(env, runtimeExceptionClass, pStrMessage);
}


static jfieldID getFieldID(JNIEnv *env, char* name, char* signature) {
	jfieldID result;
	jclass	cls = (*env)->FindClass(env, "org/tritonus/lowlevel/lame/Lame");
	if (cls == NULL) {
		throwRuntimeException(env, "cannot get class object for org.tritonus.lowlevel.lame.Lame");
	}
	result = (*env)->GetFieldID(env, cls, name, signature);
	if (result == NULL) {
		throwRuntimeException(env, "cannot get field ID in class Lame");
	}
	return result;
}

static jfieldID	nativeGlobalFlagsFieldID = NULL;

static jfieldID getNativeGlobalFlagsFieldID(JNIEnv *env) {
	if (nativeGlobalFlagsFieldID == NULL) {
		nativeGlobalFlagsFieldID = getFieldID(env, "m_lNativeGlobalFlags", "J");
	}
	return nativeGlobalFlagsFieldID;
}

#ifndef _WIN32
typedef unsigned long UINT_PTR;
#endif

static LameConf* getNativeGlobalFlags(JNIEnv *env, jobject obj) {
	jfieldID	fieldID = getNativeGlobalFlagsFieldID(env);
	return (LameConf*) ((UINT_PTR) (*env)->GetLongField(env, obj, fieldID));
}


static void setNativeGlobalFlags(JNIEnv *env, jobject obj, LameConf* flags) {
	jfieldID	fieldID = getNativeGlobalFlagsFieldID(env);
	(*env)->SetLongField(env, obj, fieldID, (jlong) ((UINT_PTR) flags));
}

static void setIntField(JNIEnv *env, jobject obj, char* name, int value) {
	jfieldID fieldID = getFieldID(env, name, "I");
	(*env)->SetIntField(env, obj, fieldID, (jint) value);
}


#define LA_ENDIAN_NOT_TESTED 0
#define LA_BIG_ENDIAN 1
#define LA_LITTLE_ENDIAN 2

static int platformEndianness=LA_ENDIAN_NOT_TESTED;

void CheckEndianness(void) {
	if (platformEndianness==LA_ENDIAN_NOT_TESTED) {
		int dummy=1;
		char* pDummy=(char*) (&dummy);
		if (*pDummy) {
			platformEndianness=LA_LITTLE_ENDIAN;
		} else {
			platformEndianness=LA_BIG_ENDIAN;
		}
	}
}


//////////////////////////////////////// exported JNI functions ////////////////////////////////////

/*
 * Class:     org_tritonus_lowlevel_lame_Lame
 * Method:    nInitParams
 * Signature: (IIIIIZZ)I
 * returns >=0 on success
 */
JNIEXPORT jint JNICALL Java_org_tritonus_lowlevel_lame_Lame_nInitParams
  (JNIEnv * env, jobject obj, jint channels, jint sampleRate,
   jint bitrate, jint mode, jint quality, jboolean VBR, jboolean bigEndian) {
	int result;
	LameConf* conf;
#ifdef _DEBUG
	printf("Java_org_tritonus_lowlevel_lame_Lame_initParams: \n");
	printf("   %d channels, %d Hz, %d KBit/s, mode %d, quality=%d VBR=%d bigEndian=%d\n",
	       (int) channels, (int) sampleRate, (int) bitrate,
	       (int) mode, (int) quality, (int) VBR, (int) bigEndian);
	fflush(stdout);
#endif
	nativeGlobalFlagsFieldID = NULL;
	conf=(LameConf*) calloc(sizeof(LameConf),1);
	setNativeGlobalFlags(env, obj, conf);
	if (conf==NULL) {
		//throwRuntimeException(env, "out of memory");
		return org_tritonus_lowlevel_lame_Lame_OUT_OF_MEMORY;
	}
	CheckEndianness();
	if ((bigEndian && platformEndianness==LA_LITTLE_ENDIAN) ||
	    (!bigEndian && platformEndianness==LA_BIG_ENDIAN)) {
		// swap samples
		conf->swapbytes=1;
	}
	conf->channels=(int) channels;
	conf->sampleRate=(int) sampleRate;
	conf->bitrate=(int) bitrate;
	conf->mode=(int) mode;
	conf->quality=(int) quality;
	conf->VBR=(int) VBR;
	conf->mpegVersion=0;

	result=doInit(conf);
	if (result<0) {
		free(conf);
		setNativeGlobalFlags(env, obj, 0);
		return result;
	}

	// update the Lame instance with the effective values
	setIntField(env, obj, "effSampleRate", conf->sampleRate);
	setIntField(env, obj, "effBitRate", conf->bitrate);
	setIntField(env, obj, "effChMode", conf->mode);
	setIntField(env, obj, "effQuality", conf->quality);
	setIntField(env, obj, "effVbr", conf->VBR);
	setIntField(env, obj, "effEncoding", conf->mpegVersion);

	return (jint) result;
}

/*
 * Class:     org_tritonus_lowlevel_lame_Lame
 * Method:    nGetPCMBufferSize
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_org_tritonus_lowlevel_lame_Lame_nGetPCMBufferSize
(JNIEnv *env, jobject obj, jint wishedBufferSize) {
    int result=(int) wishedBufferSize;
	LameConf* conf;
	conf=getNativeGlobalFlags(env, obj);
	if (conf!=NULL) {
		result=doGetPCMBufferSize(conf, (int) wishedBufferSize);
	} else {
		//throwRuntimeException(env, "not initialized");
		return org_tritonus_lowlevel_lame_Lame_NOT_INITIALIZED;
	}
	return (jint) result;
}


void swapSamples(unsigned short* samples, int count) {
	while (count>0) {
		*samples=(unsigned short) ((((*samples) & 0xFF)<<8) | (((*samples) >>8) & 0xFF00));
		count--;
		samples++;
	}
}

/*
 * Class:     org_tritonus_lowlevel_lame_Lame
 * Method:    nEncodeBuffer
 * Signature: ([BII[B)I
 *
 * returns result of lame_encode_buffer:
 * return code     number of bytes output in mp3buf. Can be 0
 *                 -1:  mp3buf was too small
 *                 -2:  malloc() problem
 *                 -3:  lame_init_params() not called
 *                 -4:  psycho acoustic problems
 *                 -5:  ogg cleanup encoding error
 *                 -6:  ogg frame encoding error
 *
 */
JNIEXPORT jint JNICALL Java_org_tritonus_lowlevel_lame_Lame_nEncodeBuffer
(JNIEnv *env, jobject obj, jbyteArray pcm, jint offset, jint length, jbyteArray encoded) {
	LameConf* conf;
	int result;
	char* encodedBytes, *pcmSamplesOrig;
	short* pcmSamples;
	int pcmLengthInFrames;

	// todo: consistency check for pcm array ?
	int encodedArrayByteSize=(int) ((*env)->GetArrayLength(env, encoded));
#ifdef _DEBUG
	jsize pcmArrayByteSize=(*env)->GetArrayLength(env, pcm);
	int pcmArraySizeInShorts=(pcmArrayByteSize-offset)/2;
	printf("Java_org_tritonus_lowlevel_lame_Lame_encodeBuffer: \n");
	printf("   offset: %d, length:%d, offset+length:%d\n", (int) offset, (int) length, (int) (offset+length));
	printf("   %d bytes in PCM array\n", (int) pcmArrayByteSize);
	printf("   %d bytes in to-be-encoded array\n", (int) encodedArrayByteSize);
	fflush(stdout);
#endif
	conf=getNativeGlobalFlags(env, obj);
	if (conf!=NULL) {
		pcmLengthInFrames=length/(conf->channels*2); // always 16 bit
		pcmSamplesOrig=(*env)->GetByteArrayElements(env, pcm, NULL);
		pcmSamples=(short*) pcmSamplesOrig;
		pcmSamples+=(offset/2); // 16bit
		if (conf->swapbytes) {
			swapSamples((unsigned short*) pcmSamples, length/2);
		}
		encodedBytes=(*env)->GetByteArrayElements(env, encoded, NULL);

#ifdef _DEBUG
			printf("   Encoding %d frames at %p into buffer %p of size %d bytes.\n",
			       pcmLengthInFrames, pcmSamples, encodedBytes, encodedArrayByteSize);
			//printf("   Sample1=%d Sample2=%d\n", pcmSamples[0], pcmSamples[1]);
#endif
		result=doEncode(conf, pcmSamples, pcmLengthInFrames, encodedBytes, encodedArrayByteSize);

#ifdef _DEBUG
		//printf("   MP3-1=%d MP3-2=%d\n", (int) encodedBytes[0], (int) encodedBytes[1]);
#endif
		// clean up:
		// discard any changes in pcmArray
		(*env)->ReleaseByteArrayElements(env, pcm, pcmSamplesOrig, JNI_ABORT);
		// commit the encoded bytes
		(*env)->ReleaseByteArrayElements(env, encoded, encodedBytes, 0);
	} else {
#ifdef _DEBUG
		printf("Java_org_tritonus_lowlevel_lame_Lame_nEncodeBuffer: \n");
		printf("   no global flags !\n");
		fflush(stdout);
#endif
		//throwRuntimeException(env, "not initialized");
		return org_tritonus_lowlevel_lame_Lame_NOT_INITIALIZED;
	}
	return (jint) result;
}

/*
 * Class:     org_tritonus_lowlevel_lame_Lame
 * Method:    **************************************** nEncodeFinish
 * Signature: ([B)I                                    /////////////
 */
JNIEXPORT jint JNICALL Java_org_tritonus_lowlevel_lame_Lame_nEncodeFinish
(JNIEnv *env, jobject obj, jbyteArray buffer) {
	int result=0;
	LameConf* conf;
#ifdef _DEBUG
		//jsize length=(*env)->GetArrayLength(env, buffer);
		printf("Java_org_tritonus_lowlevel_lame_Lame_encodeFinish: \n");
		//printf("   %d bytes in the array\n", (int) length);
#endif
	conf=getNativeGlobalFlags(env, obj);
	if (conf!=NULL) {
		jsize charBufferSize=(*env)->GetArrayLength(env, buffer);
		char* charBuffer=NULL;
		if (charBufferSize>0) {
			charBuffer=(*env)->GetByteArrayElements(env, buffer, NULL);
		}
		result=doEncodeFinish(conf, charBuffer, charBufferSize);
		doClose(conf);
#ifdef _DEBUG
		printf("   %d bytes returned\n", (int) result);
#endif
		(*env)->ReleaseByteArrayElements(env, buffer, charBuffer, 0);
		setNativeGlobalFlags(env, obj, 0);
		free(conf);
	}
#ifdef _DEBUG
	else {
		printf("Java_org_tritonus_lowlevel_lame_Lame_encodeFinish: \n");
		printf("   no global flags !\n");
	}
#endif
	return (jint) result;
}

/*
 * Class:     org_tritonus_lowlevel_lame_Lame
 * Method:    ************************************** nClose
 * Signature: ()V                                    //////
 */
JNIEXPORT void JNICALL Java_org_tritonus_lowlevel_lame_Lame_nClose(JNIEnv * env, jobject obj) {
	LameConf* conf;

#ifdef _DEBUG
	printf("Java_org_tritonus_lowlevel_lame_Lame_nClose. \n");
#endif
	conf=getNativeGlobalFlags(env, obj);
	if (conf!=NULL) {
		doClose(conf);
		setNativeGlobalFlags(env, obj, 0);
		free(conf);
	}
#ifdef _DEBUG
	else {
		printf("   no global flags !\n");
	}
#endif
}

/*
 * Class:     org_tritonus_lowlevel_lame_Lame
 * Method:    nGetEncoderVersion
 * Signature: ([B)I
 */
JNIEXPORT jint JNICALL Java_org_tritonus_lowlevel_lame_Lame_nGetEncoderVersion
  (JNIEnv * env, jobject obj, jbyteArray string) {
	LameConf* conf;
	int res;
	jsize charBufferSize;
	char* charBuffer=NULL;

#ifdef _DEBUG
	printf("Java_org_tritonus_lowlevel_lame_Lame_nGetEncoderVersion\n");
#endif
	conf=getNativeGlobalFlags(env, obj);
	charBufferSize=(*env)->GetArrayLength(env, string);
	charBuffer=NULL;
	if (charBufferSize>0) {
		charBuffer=(*env)->GetByteArrayElements(env, string, NULL);
	}
	if (charBuffer==NULL) {
#ifdef _DEBUG
		printf("  passed array is NULL or zero-length !\n");
		return -1;
#endif
	}
	res=doGetEncoderVersion(conf, charBuffer, charBufferSize);
	(*env)->ReleaseByteArrayElements(env, string, charBuffer, 0);
	return res;
}
