/*
 *	bladenc.c
 *  for Windows bladenc-style DLLs
 */


/*
 *  Copyright (c) 2001 by Florian Bomers <http://www.bomers.de>
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

#include "bladenc.h"

////////////////////////////// used in lame and bladenc //////////////////////////////////////////////

int copyVersion(char* s, int len, int major, int minor, int alpha, int beta) {
	int thislen;
	int result=0;
	if (len<8) {
		return -1;
	}
	sprintf(s, "%d.%d", major, minor);
	thislen=strlen(s); len-=thislen; s+=thislen; result+=thislen;
	// now put something like "alpha" or "beta"
	if (alpha && len>=6) {
		strcpy(s, "alpha");
		len-=5; s+=5; result+=5;
	} else
	if (beta && len>=5) {
		strcpy(s, "beta");
		len-=4; s+=4; result+=4;
	}
	return result;
}


////////////////////////////// bladenc specific //////////////////////////////////////////////

#ifndef _BLADEDLL
static BEINITSTREAM			beInitStream=NULL;
static BEENCODECHUNK		beEncodeChunk=NULL;
static BEDEINITSTREAM		beDeinitStream=NULL;
static BECLOSESTREAM		beCloseStream=NULL;
static BEVERSION			beVersion=NULL;
static BEWRITEVBRHEADER		beWriteVBRHeader=NULL;
static HINSTANCE			hDLL=NULL;
#endif

//////////////////////////////helper functions

/*
 * loadLameLibrary
 */

#ifndef _BLADEDLL
// returns 0 on error / or one of DLL_TYPE_xx constants
int loadLameLibrary(const char* name) {
	if (hDLL==NULL) {
		hDLL=LoadLibrary(name);
		if(hDLL==NULL) {
#ifdef _DEBUG
			printf("Unable to load bladenc library\n");
			fflush(stdout);
#endif
			return 0;
		}
	}
	if(!beInitStream || !beEncodeChunk || !beDeinitStream || !beCloseStream || !beVersion) {
		// Get Interface functions
		beInitStream	= (BEINITSTREAM) GetProcAddress(hDLL, TEXT_BEINITSTREAM);
		beEncodeChunk	= (BEENCODECHUNK) GetProcAddress(hDLL, TEXT_BEENCODECHUNK);
		beDeinitStream	= (BEDEINITSTREAM) GetProcAddress(hDLL, TEXT_BEDEINITSTREAM);
		beCloseStream	= (BECLOSESTREAM) GetProcAddress(hDLL, TEXT_BECLOSESTREAM);
		beVersion		= (BEVERSION) GetProcAddress(hDLL, TEXT_BEVERSION);
		beWriteVBRHeader= (BEWRITEVBRHEADER) GetProcAddress(hDLL,TEXT_BEWRITEVBRHEADER);
	}

	// Check if all interfaces are present
	if(!beInitStream || !beEncodeChunk || !beDeinitStream || !beCloseStream || !beVersion) {
#ifdef _DEBUG
		printf("Unable to get bladenc functions from dll\n");
		fflush(stdout);
#endif
		return 0;
	}
	if (beWriteVBRHeader) {
#ifdef _DEBUG
		printf("DLL ist lame.\n");
		fflush(stdout);
#endif
		return DLL_TYPE_LAME;
	}
#ifdef _DEBUG
	printf("DLL ist bladenc.\n");
	fflush(stdout);
#endif
	return DLL_TYPE_BLADENC;
}
#endif

int getBladeMpegVersion(LameConf* conf) {

	switch (conf->sampleRate) {
		case 8000:  // MPEG 2.5
		case 11025: // MPEG 2.5
		case 12000: // MPEG 2.5
		case 16000: // MPEG 2
		case 22050: // MPEG 2
		case 24000: return MPEG2; // MPEG 2
		//case 32000: // MPEG 1
		//case 44100: // MPEG 1
		//case 48000: // MPEG 1
	}
	return MPEG1;
}

int getLameMpegVersion(int bladeVersion, int sampleRate) {
	if (bladeVersion==MPEG2) {
		switch (sampleRate) {
			case 8000:  // MPEG 2.5
			case 11025: // MPEG 2.5
			case 12000: return org_tritonus_lowlevel_lame_Lame_MPEG_VERSION_2DOT5;
			//case 16000: // MPEG 2
			//case 22050: // MPEG 2
			//case 24000:
		}
		return org_tritonus_lowlevel_lame_Lame_MPEG_VERSION_2;
	}
	return org_tritonus_lowlevel_lame_Lame_MPEG_VERSION_1;
}

int getBladeChannelMode(int channels, int includeJointStereo) {
	if (channels==1) {
		return BE_MP3_MODE_MONO;
	}
	if (includeJointStereo) {
		return BE_MP3_MODE_JSTEREO;
	}
	return BE_MP3_MODE_STEREO;
}

int getLameChannelMode(int bladencMode) {
	switch (bladencMode) {
		case BE_MP3_MODE_STEREO: return org_tritonus_lowlevel_lame_Lame_CHANNEL_MODE_STEREO;
		case BE_MP3_MODE_JSTEREO: return org_tritonus_lowlevel_lame_Lame_CHANNEL_MODE_JOINT_STEREO;
		case BE_MP3_MODE_DUALCHANNEL: return org_tritonus_lowlevel_lame_Lame_CHANNEL_MODE_DUAL_CHANNEL;
		case BE_MP3_MODE_MONO: return org_tritonus_lowlevel_lame_Lame_CHANNEL_MODE_MONO;
	}
	return -1;
}

int getBitrate(LameConf* conf) {
	int result=64;
	if (conf->bitrate!=org_tritonus_lowlevel_lame_Lame_BITRATE_AUTO) {
		return conf->bitrate;
	}
	// these are the values for MONO
	switch (conf->sampleRate) {
		case 8000: result=16; break;  // MPEG 2.5
		case 11025: result=24; break; // MPEG 2.5
		case 12000: result=32; break; // MPEG 2.5
		case 16000: result=32; break; // MPEG 2
		case 22050: result=40; break; // MPEG 2
		case 24000: result=48; break; // MPEG 2
		case 32000: result=48; break; // MPEG 1
		case 44100: result=64; break; // MPEG 1
		case 48000: result=80; break; // MPEG 1
	}
	// now double it if stereo
	return result*conf->channels;
}

LONG getBladePreset(int quality) {
	if (quality>org_tritonus_lowlevel_lame_Lame_QUALITY_MIDDLE) {
		return LQP_LOW_QUALITY;
	}
	else if (quality<org_tritonus_lowlevel_lame_Lame_QUALITY_MIDDLE) {
		return LQP_HIGH_QUALITY;
	}
	return LQP_NORMAL_QUALITY;
}

int getLameQuality(LONG preset) {
	if (preset==LQP_LOW_QUALITY) {
		return org_tritonus_lowlevel_lame_Lame_QUALITY_LOWEST;
	}
	else if (preset==LQP_HIGH_QUALITY) {
		return org_tritonus_lowlevel_lame_Lame_QUALITY_HIGHEST;
	}
	return org_tritonus_lowlevel_lame_Lame_QUALITY_MIDDLE;
}


////////////////////////////////// functions called by native functions

/*
 * doInit
 */
int doInit(LameConf* conf) {
	int initDone;
	BE_ERR err;

#ifndef _BLADEDLL
	conf->dllType=loadLameLibrary(LAME_ENC_DLL);
	if (!conf->dllType) {
		return org_tritonus_lowlevel_lame_Lame_LAME_ENC_NOT_FOUND;
	}
#else
	// first try with LAME style config
	conf->dllType=DLL_TYPE_LAME;
#endif
	initDone=0;
	while (!initDone) {
		if (conf->dllType==DLL_TYPE_LAME) {
			// fill lame-style fields
			conf->beConfig.dwConfig = BE_CONFIG_LAME;
			conf->beConfig.format.LHV1.dwStructVersion	= CURRENT_STRUCT_VERSION;
			conf->beConfig.format.LHV1.dwStructSize		= CURRENT_STRUCT_SIZE;
			conf->beConfig.format.LHV1.dwSampleRate		= conf->sampleRate;				// INPUT FREQUENCY
			conf->beConfig.format.LHV1.dwReSampleRate		= 0;					// DON"T RESAMPLE
			conf->beConfig.format.LHV1.nMode				= getBladeChannelMode(conf->channels, 1);
			conf->beConfig.format.LHV1.dwBitrate			= (WORD) getBitrate(conf);					// MINIMUM BIT RATE
			conf->beConfig.format.LHV1.nPreset				= getBladePreset(conf->quality);		// QUALITY PRESET SETTING

			conf->beConfig.format.LHV1.dwMpegVersion		= getBladeMpegVersion(conf);				// MPEG VERSION (I or II)
			if (conf->VBR) {
				conf->beConfig.format.LHV1.bEnableVBR		= TRUE;					// USE VBR
				conf->beConfig.format.LHV1.nVBRQuality		= conf->quality;		// SET VBR QUALITY
			}
		} else {
			// fill bladenc-style fields
			conf->beConfig.dwConfig = BE_CONFIG_MP3;
			conf->beConfig.format.mp3.dwSampleRate=conf->sampleRate;	// 48000, 44100 and 32000 allowed
			conf->beConfig.format.mp3.byMode=(BYTE) getBladeChannelMode(conf->channels, 0);	// BE_MP3_MODE_STEREO, BE_MP3_MODE_DUALCHANNEL, BE_MP3_MODE_MONO
			conf->beConfig.format.mp3.wBitrate=(WORD) getBitrate(conf);
		}
		// Init the MP3 Stream
		err = beInitStream(&(conf->beConfig),
				&(conf->PCMBufferSizeInBytes),
				&(conf->MP3BufferSizeInBytes),
				&(conf->hbeStream));
		// blade returns in number of shorts
		conf->PCMBufferSizeInBytes*=2;
		// Check result
		if(err != BE_ERR_SUCCESSFUL) {
			if (conf->dllType==DLL_TYPE_LAME) {
				// retry with bladenc-style
				conf->dllType=DLL_TYPE_BLADENC;
			} else {
#ifdef _DEBUG
				printf("Error opening encoding stream (%lu)\n", err);
				fflush(stdout);
#endif
				// TODO: better error return
				return -1;
			}
		} else {
			initDone=1;
		}
#ifdef _DEBUG
		printf("BufferSizeInBytes=%d\n", conf->PCMBufferSizeInBytes);
#endif
	}

	// update the Lame instance with the effective values
	if (conf->dllType==DLL_TYPE_LAME) {
#ifdef _DEBUG
		printf("using LAME dll format\n");
		fflush(stdout);
#endif
		conf->bitrate=conf->beConfig.format.LHV1.dwBitrate;
		conf->VBR=conf->beConfig.format.LHV1.bEnableVBR;
		conf->mode=getLameChannelMode(conf->beConfig.format.LHV1.nMode);
		if (conf->beConfig.format.LHV1.dwReSampleRate!=0) {
			conf->sampleRate=conf->beConfig.format.LHV1.dwReSampleRate;
		} else {
			conf->sampleRate=conf->beConfig.format.LHV1.dwSampleRate;
		}
		conf->mpegVersion=getLameMpegVersion(conf->beConfig.format.LHV1.dwMpegVersion, conf->sampleRate);
		conf->quality=getLameQuality(conf->beConfig.format.LHV1.nPreset);
	} else {
#ifdef _DEBUG
		printf("using BLADENC dll format\n");
		fflush(stdout);
#endif
		conf->sampleRate=conf->beConfig.format.mp3.dwSampleRate;
		conf->bitrate=conf->beConfig.format.mp3.wBitrate;
		conf->mode=getLameChannelMode(conf->beConfig.format.mp3.byMode);
		conf->quality=org_tritonus_lowlevel_lame_Lame_QUALITY_MIDDLE;
		conf->VBR=0;
		conf->mpegVersion=getLameMpegVersion(MPEG1, conf->sampleRate);
	}
	return 0;
}

/*
 * doGetEncoderVersion
 */
int doGetEncoderVersion(LameConf* conf, char* charBuffer, int charBufferSize) {
	BE_VERSION version;
	char* thisString=charBuffer;
	int size=charBufferSize;
	int len;
	int result=0;

#ifndef _BLADEDLL
	if (beVersion==NULL && !loadLameLibrary(LAME_ENC_DLL)) {
		return org_tritonus_lowlevel_lame_Lame_LAME_ENC_NOT_FOUND;
	}
#endif
	memset(&version, 0, sizeof(version));
	memset(charBuffer, 0, charBufferSize);
	beVersion(&version);

	// first put something like "333.333alpha"
	len=copyVersion(thisString, size, version.byMajorVersion, version.byMinorVersion, version.byAlphaLevel, version.byBetaLevel);
	if (len<=0) {
		return result;
	}
	thisString+=len; size-=len; result+=len;

	// dll version like "; dll 333.333)"
	if (size>13) {
		strcpy(thisString, "; dll ");
		thisString+=6; size-=6; result+=6;
		len=copyVersion(thisString, size, version.byDLLMajorVersion, version.byDLLMinorVersion, 0, 0);
		if (len<=0) {
			return result;
		}
		thisString+=len; size-=len; result+=len;
	}
	// copy date "; 2001-01-01"
	if (size>12) {
		sprintf(thisString, "; %d-%d-%d", version.wYear, version.byMonth, version.byDay);
		len=strlen(thisString);
		thisString+=len; size-=len; result+=len;
	}
	// copy features
	if (size>5 && version.byMMXEnabled) {
		strcpy(thisString, "; MMX");
		thisString+=5; size-=5; result+=5;
	}
	return result;
}

/*
 * doGetPCMBufferSize
 */
int doGetPCMBufferSize(LameConf* conf, int wishedBufferSize) {
	int result=wishedBufferSize;

	if (conf->PCMBufferSizeInBytes<=0) {
		//throwRuntimeException(env, "not initialized");
		return org_tritonus_lowlevel_lame_Lame_NOT_INITIALIZED;
	}
	if ((result % conf->PCMBufferSizeInBytes)!=0) {
		//result=conf->PCMBufferSizeInBytes;
		result=(result/conf->PCMBufferSizeInBytes)*conf->PCMBufferSizeInBytes;
		if (result==0) {
			result=conf->PCMBufferSizeInBytes;
		}
	}
	return result;
}

/*
 * doEncode
 */
int doEncode(LameConf* conf, short* pcmSamples, int pcmLengthInFrames, char* encodedBytes, int encodedArrayByteSize) {
	DWORD thisPcmSizeInShorts;
	DWORD mp3SizeInBytes;
	DWORD bladeBufferSizeInShorts=conf->PCMBufferSizeInBytes/2;
	BE_ERR err;
	int pcmLengthInShorts=pcmLengthInFrames*conf->channels;
	int result=0;

	// bladenc prefers a fixed buffer size. if necessary, call bladenc several times
	while (pcmLengthInShorts>0) {
		thisPcmSizeInShorts=pcmLengthInShorts;
		if (thisPcmSizeInShorts>bladeBufferSizeInShorts) {
			thisPcmSizeInShorts=bladeBufferSizeInShorts;
		}
		mp3SizeInBytes=encodedArrayByteSize;
#ifdef _DEBUG
		printf("     Encoding %d shorts at %p (size=%d shorts) into buffer %p (size=%d bytes).\n",
		       thisPcmSizeInShorts, pcmSamples, pcmLengthInShorts, encodedBytes, mp3SizeInBytes);
		//printf("     Sample1=%d Sample2=%d\n", pcmSamples[0], pcmSamples[1]);
		fflush(stdout);
#endif
		err = beEncodeChunk(conf->hbeStream, thisPcmSizeInShorts, pcmSamples, (PBYTE) encodedBytes, &mp3SizeInBytes);

		// Check result
		if(err != BE_ERR_SUCCESSFUL) {
			result=-10;
			break;
		} else {
#ifdef _DEBUG
			printf("     -encoded %d bytes.\n", (int) mp3SizeInBytes);
			//printf("       MP3-1=%d MP3-2=%d\n", (int) encodedBytes[0], (int) encodedBytes[1]);
			fflush(stdout);
			//pcmArraySizeInShorts-=thisPcmSizeInShorts;
#endif
			result+=mp3SizeInBytes;
			encodedBytes+=mp3SizeInBytes;
			encodedArrayByteSize-=mp3SizeInBytes;
			pcmLengthInShorts-=thisPcmSizeInShorts;
			pcmSamples+=thisPcmSizeInShorts;
		}
	}
	return result;
}

/*
 * doEncodeFinish
 */
int doEncodeFinish(LameConf* conf, char* encodedBytes, int encodedArrayByteSize) {
	BE_ERR err;
	DWORD result=(DWORD) encodedArrayByteSize;
	err = beDeinitStream(conf->hbeStream, (PBYTE) encodedBytes, &result);
	if(err != BE_ERR_SUCCESSFUL) {
		result=-1;
	}
	return (int) result;
}

/*
 * doClose
 */
void doClose(LameConf* conf) {
	beCloseStream(conf->hbeStream);
}

