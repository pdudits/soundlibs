/*
 *	bladenc.h
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


#ifndef BLADENC_API_H_INCLUDED
#define BLADENC_API_H_INCLUDED

#include	<windows.h>
#include	<stdlib.h>
#include	<stdio.h>
#include	"org_tritonus_lowlevel_lame_Lame.h"
#include	"BladeMP3EncDLL.h"

#define DLL_TYPE_LAME 1
#define DLL_TYPE_BLADENC 2

#define LAME_ENC_DLL "lame_enc.dll"

typedef struct tagLameConf {
	int channels;
	int sampleRate;
	int bitrate;
	int mode;
	int quality;
	int VBR;
	int mpegVersion;
	int swapbytes;
	BE_CONFIG beConfig;
	HBE_STREAM hbeStream;
	int dllType;
	DWORD PCMBufferSizeInBytes;
	DWORD MP3BufferSizeInBytes;
} LameConf;

extern int doInit(LameConf* conf);
extern int doGetPCMBufferSize(LameConf* conf, int wishedBufferSize);
extern int doEncode(LameConf* conf, short* pcmSamples, int pcmLengthInFrames, char* encodedBytes, int encodedArrayByteSize);
extern int doEncodeFinish(LameConf* conf, char* encodedBytes, int encodedArrayByteSize);
extern void doClose(LameConf* conf);
extern int doGetEncoderVersion(LameConf* conf, char* charBuffer, int charBufferSize);

#endif
