/*
 *	lameapi.c
 * functions that use the lame API
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


#include	"lameapi.h"

int doInit(LameConf* conf) {
	int result;

	conf->gf=lame_init();
	if (conf->gf==NULL) {
		//throwRuntimeException(env, "out of memory");
		return org_tritonus_lowlevel_lame_Lame_OUT_OF_MEMORY;
	}

	lame_set_num_channels(conf->gf, conf->channels);
	lame_set_in_samplerate(conf->gf, conf->sampleRate);
	if (conf->mode!=org_tritonus_lowlevel_lame_Lame_CHANNEL_MODE_AUTO) {
		lame_set_mode(conf->gf, conf->mode);
	}
	if (conf->VBR) {
		lame_set_VBR(conf->gf, vbr_default);
		lame_set_VBR_q(conf->gf, conf->quality);
	} else {
		if (conf->bitrate!=org_tritonus_lowlevel_lame_Lame_BITRATE_AUTO) {
		    lame_set_brate(conf->gf, conf->bitrate);
		}
	}
	lame_set_quality(conf->gf, conf->quality);
	result=lame_init_params(conf->gf);

	// return effective values
	conf->sampleRate=lame_get_out_samplerate(conf->gf);
	conf->bitrate=lame_get_brate(conf->gf);
	conf->mode=lame_get_mode(conf->gf);
	conf->VBR=lame_get_VBR(conf->gf);
	conf->quality=(conf->VBR)?lame_get_VBR_q(conf->gf):lame_get_quality(conf->gf);
	conf->mpegVersion=lame_get_version(conf->gf);

	return result;
}

int doGetPCMBufferSize(LameConf* conf, int wishedBufferSize) {
	// lame supports all buffer sizes
	return wishedBufferSize;
}

int doEncode(LameConf* conf, short* pcmSamples, int pcmLengthInFrames, char* encodedBytes, int encodedArrayByteSize) {
	if (conf->gf==NULL) {
		//throwRuntimeException(env, "not initialized");
		return org_tritonus_lowlevel_lame_Lame_NOT_INITIALIZED;
	}
	if (conf->channels==1) {
		return lame_encode_buffer(conf->gf, pcmSamples, pcmSamples, pcmLengthInFrames,
					      encodedBytes, encodedArrayByteSize);
	} else {
		return lame_encode_buffer_interleaved(conf->gf, pcmSamples, pcmLengthInFrames,
					      encodedBytes, encodedArrayByteSize);
	}
}

int doEncodeFinish(LameConf* conf, char* encodedBytes, int encodedArrayByteSize) {
	if (conf->gf==NULL) {
		//throwRuntimeException(env, "not initialized");
		return org_tritonus_lowlevel_lame_Lame_NOT_INITIALIZED;
	}
	return lame_encode_flush(conf->gf, encodedBytes, encodedArrayByteSize);
}

void doClose(LameConf* conf) {
	if (conf->gf!=NULL) {
		lame_close(conf->gf);
		conf->gf=NULL;
	}
}

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

int doGetEncoderVersion(LameConf* conf, char* charBuffer, int charBufferSize) {
	lame_version_t version;
	char* thisString=charBuffer;
	int size=charBufferSize;
	int len;
	int result=0;

	memset(&version, 0, sizeof(version));
	memset(charBuffer, 0, charBufferSize);
	get_lame_version_numerical(&version);
	// first put something like "LAME 333.333 alpha"
	if (size>=13) {
		strcpy(thisString, "LAME ");
		thisString+=5; size-=5; result+=5;
	}
	len=copyVersion(thisString, size, version.major, version.minor, version.alpha, version.beta);
	if (len<=0) {
		return result;
	}
	thisString+=len; size-=len; result+=len;

	// first put something like "(psy model 333.333beta)"
	if (size>=25) {
		strcpy(thisString, "; psy model ");
		thisString+=12; size-=12; result+=12;
		len=copyVersion(thisString, size, version.major, version.minor, version.alpha, version.beta);
		if (len<=0) {
			return result;
		}
		thisString+=len; size-=len; result+=len;
	}
	// at last, copy features string
	if (size>5 && version.features!=NULL && version.features[0]!=0) {
		strcpy(thisString, "; ");
		thisString+=2; size-=2; result+=2;
		strncpy(thisString, version.features, size-1);
		charBuffer[charBufferSize-1]=0;
		result=strlen(charBuffer);
	}
	return result;
}
