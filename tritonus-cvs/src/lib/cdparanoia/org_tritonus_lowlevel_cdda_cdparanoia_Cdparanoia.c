/*
 *	org_tritonus_lowlevel_cdda_cdparanoia_Cdparanoia.c
 */

#include <endian.h>
#include <stdlib.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>   /* should declare swab(), but doesn't seem so (gcc 2.95.4/glibc 2.3.1) */
void swab(void*, void*, ssize_t);

#include <cdda_interface.h>
#include <cdda_paranoia.h>

#include "../common/common.h"
#include "../common/debug.h"
#include "../common/HandleFieldHandler.h"
#include "org_tritonus_lowlevel_cdda_cdparanoia_Cdparanoia.h"
#include "handle_t.h"

// default value taken over from cdparanoia's main.c
static const int	MAX_RETRIES = 20;

static int	sm_nParanoiaMode = 0;

static int
getParanoiaMode()
{
	return sm_nParanoiaMode;
}



HandleFieldHandler(handle_t*)



/*
 * Class:     org_tritonus_lowlevel_cdda_cdparanoia_Cdparanoia
 * Method:    open
 * Signature: (Ljava/lang/String;)I
 */
JNIEXPORT jint JNICALL
Java_org_tritonus_lowlevel_cdda_cdparanoia_Cdparanoia_open
(JNIEnv *env, jobject obj, jstring strDevice)
{
	int	nReturn;
	const char*	cd_dev;
	cdrom_drive*	cdrom = NULL;
	handle_t*	pHandle;
	int		nParanoiaMode;

	if (debug_flag) { fprintf(debug_file, "Java_org_tritonus_lowlevel_cdda_cdparanoia_Cdparanoia_open(): begin\n"); }
	cd_dev = (*env)->GetStringUTFChars(env, strDevice, NULL);
	if (cd_dev == NULL)
	{
		if (debug_flag) { fprintf(debug_file, "Java_org_tritonus_lowlevel_cdda_cdparanoia_Cdparanoia_open(): GetStringUTFChars() failed.\n"); }
		return -1;
	}
	if (debug_flag) { fprintf(debug_file, "Java_org_tritonus_lowlevel_cdda_cdparanoia_Cdparanoia_open(): device name: %s\n", cd_dev); }
	cdrom = cdda_identify(cd_dev, 0, NULL);
	if (debug_flag) { fprintf(debug_file, "Java_org_tritonus_lowlevel_cdda_cdparanoia_Cdparanoia_open(): device name: %s\n", cd_dev); }
	(*env)->ReleaseStringUTFChars(env, strDevice, cd_dev);
	if (cdrom == NULL)
	{
		if (debug_flag) { fprintf(debug_file, "Java_org_tritonus_lowlevel_cdda_cdparanoia_Cdparanoia_open(): cdda_identify() failed.\n"); }
		return -1;
	}
	nReturn = cdda_open(cdrom);
	if (nReturn < 0)
	{
		if (debug_flag) { fprintf(debug_file, "Java_org_tritonus_lowlevel_cdda_cdparanoia_Cdparanoia_open(): cdda_open() failed.\n"); }
		return -1;
	}

	if (debug_flag) { fprintf(debug_file, "Java_org_tritonus_lowlevel_cdda_cdparanoia_Cdparanoia_open(): drive endianess: %d\n", cdrom->bigendianp); }

	pHandle = (handle_t*) malloc(sizeof(handle_t));
	if (pHandle == NULL)
	{
		if (debug_flag) { fprintf(debug_file, "Java_org_tritonus_lowlevel_cdda_cdparanoia_Cdparanoia_open(): malloc() failed.\n"); }
		cdda_close(cdrom);
		return -1;
	}
	pHandle->drive = cdrom;
	pHandle->paranoia = paranoia_init(pHandle->drive);
	if (pHandle->paranoia == NULL)
	{
		if (debug_flag) { fprintf(debug_file, "Java_org_tritonus_lowlevel_cdda_cdparanoia_Cdparanoia_open(): paranoia_init() failed.\n"); }
		cdda_close(pHandle->drive);
		free(pHandle);
		return -1;
	}
	nParanoiaMode = getParanoiaMode();
	if (debug_flag) { fprintf(debug_file, "Java_org_tritonus_lowlevel_cdda_cdparanoia_Cdparanoia_open(): paranoia mode: %d\n", nParanoiaMode); }
	paranoia_modeset(pHandle->paranoia, nParanoiaMode);

	setHandle(env, obj, pHandle);
	if (debug_flag) { fprintf(debug_file, "Java_org_tritonus_lowlevel_cdda_cdparanoia_Cdparanoia_open(): end\n"); }
	return 0;
}



/*
 * Class:     org_tritonus_lowlevel_cdda_cdparanoia_Cdparanoia
 * Method:    close
 * Signature: ()V
 */
JNIEXPORT void JNICALL
Java_org_tritonus_lowlevel_cdda_cdparanoia_Cdparanoia_close
(JNIEnv *env, jobject obj)
{
	handle_t*	handle;
	cdrom_drive*	cdrom;

	if (debug_flag) { fprintf(debug_file, "Java_org_tritonus_lowlevel_cdda_cdparanoia_Cdparanoia_close(): begin\n"); }
	// TODO: close paranoia pointer?
	handle = getHandle(env, obj);
	if (handle != NULL)
	{
		cdrom = handle->drive;
		if (cdrom != NULL)
		{
			cdda_close(cdrom);
		}
	}
	if (debug_flag) { fprintf(debug_file, "Java_org_tritonus_lowlevel_cdda_cdparanoia_Cdparanoia_close(): end\n"); }
}



/*
 * Class:     org_tritonus_lowlevel_cdda_cdparanoia_Cdparanoia
 * Method:    readTOC
 * Signature: ([I[I[I[I[Z[Z[Z[I)I
 */
JNIEXPORT jint JNICALL
Java_org_tritonus_lowlevel_cdda_cdparanoia_Cdparanoia_readTOC
(JNIEnv* env, jobject obj,
 jintArray anValues,
 jintArray anStartFrame,
 jintArray anLength,
 jintArray anType,
 jbooleanArray abAudio,
 jbooleanArray abCopy,
 jbooleanArray abPre,
 jintArray anChannels)
{
	handle_t*	handle;
	cdrom_drive*	cdrom;
	int		nFirstTrack;
	int		nLastTrack;
	jint*		pnValues;
	jint*		pnStartFrame;
	jint*		pnLength;
	jint*		pnType;
	jboolean*	pbAudio;
	jboolean*	pbCopy;
	jboolean*	pbPre;
	jint*		pnChannels;
	int		nTrack;

	if (debug_flag) { fprintf(debug_file, "Java_org_tritonus_lowlevel_cdda_cdparanoia_Cdparanoia_readTOC(): begin\n"); }
	handle = getHandle(env, obj);
	cdrom = handle->drive;
	checkArrayLength(env, anValues, 2);
	pnValues = (*env)->GetIntArrayElements(env, anValues, NULL);
	if (pnValues == NULL)
	{
		throwRuntimeException(env, "GetIntArrayElements failed");
	}
	// TODO: check if first track is guaranteed to be 1
	pnValues[0] = 1;
	pnValues[1] = cdda_tracks(cdrom);
	nFirstTrack = 1;
	nLastTrack = cdda_tracks(cdrom);
	(*env)->ReleaseIntArrayElements(env, anValues, pnValues, 0);

	checkArrayLength(env, anStartFrame, 100);
	pnStartFrame = (*env)->GetIntArrayElements(env, anStartFrame, NULL);
	if (pnStartFrame == NULL)
	{
		throwRuntimeException(env, "GetIntArrayElements failed");
	}
	checkArrayLength(env, anLength, 100);
	pnLength = (*env)->GetIntArrayElements(env, anLength, NULL);
	if (pnLength == NULL)
	{
		throwRuntimeException(env, "GetIntArrayElements failed");
	}
	checkArrayLength(env, anType, 100);
	pnType = (*env)->GetIntArrayElements(env, anType, NULL);
	if (pnType == NULL)
	{
		throwRuntimeException(env, "GetIntArrayElements failed");
	}
	checkArrayLength(env, abAudio, 100);
	pbAudio = (*env)->GetBooleanArrayElements(env, abAudio, NULL);
	if (pbAudio == NULL)
	{
		throwRuntimeException(env, "GetBooleanArrayElements failed");
	}
	checkArrayLength(env, abCopy, 100);
	pbCopy = (*env)->GetBooleanArrayElements(env, abCopy, NULL);
	if (pbCopy == NULL)
	{
		throwRuntimeException(env, "GetBooleanArrayElements failed");
	}
	checkArrayLength(env, abPre, 100);
	pbPre = (*env)->GetBooleanArrayElements(env, abPre, NULL);
	if (pbPre == NULL)
	{
		throwRuntimeException(env, "GetBooleanArrayElements failed");
	}
	checkArrayLength(env, anChannels, 100);
	pnChannels = (*env)->GetIntArrayElements(env, anChannels, NULL);
	if (pnChannels == NULL)
	{
		throwRuntimeException(env, "GetIntArrayElements failed");
	}
	for (nTrack = nFirstTrack; nTrack <= nLastTrack; nTrack++)
	{
		pnStartFrame[nTrack - nFirstTrack] = cdda_track_firstsector(cdrom, nTrack);
		pnLength[nTrack - nFirstTrack] = cdda_track_lastsector(cdrom, nTrack) - cdda_track_firstsector(cdrom, nTrack) + 1;
		pnType[nTrack - nFirstTrack] = 0;	// TODO: toc_entry.cdte_ctrl & CDROM_DATA_TRACK;
		pbAudio[nTrack - nFirstTrack] = cdda_track_audiop(cdrom, nTrack);
		pbCopy[nTrack - nFirstTrack] = cdda_track_copyp(cdrom, nTrack);
		pbPre[nTrack - nFirstTrack] = cdda_track_preemp(cdrom, nTrack);
		pnChannels[nTrack - nFirstTrack] = cdda_track_channels(cdrom, nTrack);
		if (debug_flag) { fprintf(debug_file, "Java_org_tritonus_lowlevel_cdda_cdparanoia_Cdparanoia_readTOC(): %d: %d %ld %ld\n", nTrack - nFirstTrack, nTrack, (long) pnStartFrame[nTrack - nFirstTrack], (long) pnLength[nTrack - nFirstTrack]); }
	}

	(*env)->ReleaseIntArrayElements(env, anStartFrame, pnStartFrame, 0);
	(*env)->ReleaseIntArrayElements(env, anLength, pnLength, 0);
	(*env)->ReleaseIntArrayElements(env, anType, pnType, 0);
	(*env)->ReleaseBooleanArrayElements(env, abAudio, pbAudio, 0);
	(*env)->ReleaseBooleanArrayElements(env, abCopy, pbCopy, 0);
	(*env)->ReleaseBooleanArrayElements(env, abPre, pbPre, 0);
	(*env)->ReleaseIntArrayElements(env, anChannels, pnChannels, 0);

	if (debug_flag) { fprintf(debug_file, "Java_org_tritonus_lowlevel_cdda_cdparanoia_Cdparanoia_readTOC(): end\n"); }
	return 0;
}



/*
 * Class:     org_tritonus_lowlevel_cdda_cdparanoia_Cdparanoia
 * Method:    prepareTrack
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL
Java_org_tritonus_lowlevel_cdda_cdparanoia_Cdparanoia_prepareTrack
(JNIEnv* env, jobject obj, jint nTrack)
{
	handle_t*	handle;
	cdrom_drive*	cdrom;
	cdrom_paranoia*	paranoia;
	int		nFirstSector;
	
	if (debug_flag) { fprintf(debug_file, "Java_org_tritonus_lowlevel_cdda_cdparanoia_Cdparanoia_prepareTrack(): begin\n"); }
	handle = getHandle(env, obj);
	cdrom = handle->drive;
	paranoia = handle->paranoia;

	nFirstSector = cdda_track_firstsector(cdrom, nTrack);
	// TODO: check return value
	paranoia_seek(paranoia, nFirstSector, SEEK_SET);
	if (debug_flag) { fprintf(debug_file, "Java_org_tritonus_lowlevel_cdda_cdparanoia_Cdparanoia_prepareTrack(): end\n"); }
	return 0;
}



/*
 * Class:     org_tritonus_lowlevel_cdda_cdparanoia_Cdparanoia
 * Method:    readNextFrame
 * Signature: (I[B)I
 */
JNIEXPORT jint JNICALL
Java_org_tritonus_lowlevel_cdda_cdparanoia_Cdparanoia_readNextFrame
(JNIEnv *env, jobject obj, jint nCount, jbyteArray abData)
{
	handle_t*	handle;
	cdrom_drive*	cdrom;
	cdrom_paranoia*	paranoia;
	int16_t*	psBuffer;
	jbyte*		pbData;

	if (debug_flag) { fprintf(debug_file, "Java_org_tritonus_lowlevel_cdda_cdparanoia_Cdparanoia_readNextFrame(): begin\n"); }
	handle = getHandle(env, obj);
	cdrom = handle->drive;
	paranoia = handle->paranoia;

	checkArrayLength(env, abData, CD_FRAMESIZE_RAW * nCount);
	pbData = (*env)->GetByteArrayElements(env, abData, NULL);
	if (pbData == NULL)
	{
		throwRuntimeException(env, "GetByteArrayElements failed");
	}
	// TODO: verify that NULL is allowed for callback; common values for maxretries
	// TODO: repeat for multiple sectors
	psBuffer = paranoia_read_limited(paranoia, NULL, MAX_RETRIES);
	if (psBuffer == NULL)
	{
		throwRuntimeException(env, "cdparanoia_Cdparanoia: read failed");
	}
#if __BYTE_ORDER == __LITTLE_ENDIAN
	(void) memcpy(pbData, psBuffer, CD_FRAMESIZE_RAW);
#else
	swab(psBuffer, pbData, CD_FRAMESIZE_RAW);
#endif
	(*env)->ReleaseByteArrayElements(env, abData, pbData, 0);
	if (debug_flag) { fprintf(debug_file, "Java_org_tritonus_lowlevel_cdda_cdparanoia_Cdparanoia_readNextFrame(): end\n"); }
	return 0;
}



/*
 * Class:     org_tritonus_lowlevel_cdda_cdparanoia_Cdparanoia
 * Method:    setTrace
 * Signature: (Z)V
 */
JNIEXPORT void JNICALL
Java_org_tritonus_lowlevel_cdda_cdparanoia_Cdparanoia_setTrace
(JNIEnv *env, jclass cls, jboolean bTrace)
{
	debug_flag = bTrace;
	debug_file = stderr;
}



/*
 * Class:     org_tritonus_lowlevel_cdda_cdparanoia_Cdparanoia
 * Method:    setParanoiaMode
 * Signature: (Z)V
 */
JNIEXPORT void JNICALL
Java_org_tritonus_lowlevel_cdda_cdparanoia_Cdparanoia_setParanoiaMode
(JNIEnv* env, jclass cls, jboolean bParanoiaMode)
{
	if (bParanoiaMode == JNI_TRUE)
	{
		sm_nParanoiaMode = PARANOIA_MODE_FULL ^ PARANOIA_MODE_NEVERSKIP;
	}
	else
	{
		sm_nParanoiaMode = PARANOIA_MODE_DISABLE;
	}
}



/*** org_tritonus_lowlevel_cdda_cdparanoia_Cdparanoia.c ***/
