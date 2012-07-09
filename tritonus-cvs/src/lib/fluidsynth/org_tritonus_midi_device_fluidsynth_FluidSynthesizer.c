/*
 * org_tritonus_midi_device_fluidsynth_FluidSynthesizer.c
 *
 * This file is part of Tritonus: http://www.tritonus.org/
 */

/*
 * Copyright (c) 2006 by Henri Manson
 * Copyright (c) 2006 by Matthias Pfisterer
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * - Redistributions of source code must retain the above copyright notice,
 *   this list of conditions and the following disclaimer.
 * - Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */

/*
|<---            this code is formatted to fit into 80 columns             --->|
*/

#include <jni.h>
#include <fluidsynth.h>

#include "org_tritonus_midi_device_fluidsynth_FluidSynthesizer.h"
#include "org_tritonus_midi_sb_fluidsynth_FluidSoundbank.h"
#include "../common/common.h"
#include "../common/debug.h"

/* non-API method of libfluidsynth */
void fluid_log_config(void);


static jclass fluidsynthclass;
static jfieldID settingsPtrFieldID;
static jfieldID synthPtrFieldID;
static jfieldID audioDriverPtrFieldID;

static int get_fluidclassinfo(JNIEnv *env)
{
	if (fluidsynthclass == NULL)
	{
		fluidsynthclass = (*env)->FindClass(env, "org/tritonus/midi/device/fluidsynth/FluidSynthesizer");
		if (!fluidsynthclass)
			return -1;
		synthPtrFieldID = (*env)->GetFieldID(env, fluidsynthclass, "synthPtr", "J");
		settingsPtrFieldID = (*env)->GetFieldID(env, fluidsynthclass, "settingsPtr", "J");
		audioDriverPtrFieldID = (*env)->GetFieldID(env, fluidsynthclass, "audioDriverPtr", "J");
	}
	return 0;
}

static fluid_synth_t* get_synth(JNIEnv *env, jobject obj)
{
	get_fluidclassinfo(env);
	return (fluid_synth_t*) ((unsigned int) (*env)->GetLongField(env, obj, synthPtrFieldID));
}

static void fluid_jni_delete_synth(JNIEnv *env, jobject obj, fluid_settings_t* settings, fluid_synth_t* synth, fluid_audio_driver_t* adriver)
{
	get_fluidclassinfo(env);
	if (adriver)
	{
		delete_fluid_audio_driver(adriver);
		(*env)->SetLongField(env, obj, audioDriverPtrFieldID, (jlong) 0);
	}
	if (synth)
	{
		delete_fluid_synth(synth);
		(*env)->SetLongField(env, obj, synthPtrFieldID, (jlong) 0);
	}
	if (settings)
	{
		delete_fluid_settings(settings);
		(*env)->SetLongField(env, obj, settingsPtrFieldID, (jlong) 0);
	}
}



/*
 * Class:     org_tritonus_midi_device_fluidsynth_FluidSynthesizer
 * Method:    newSynth
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_org_tritonus_midi_device_fluidsynth_FluidSynthesizer_newSynth
(JNIEnv *env, jobject obj)
{
	fluid_synth_t* synth = NULL;
	fluid_settings_t* settings = NULL;
	fluid_audio_driver_t* adriver = NULL;

	synth = get_synth(env, obj);
	if (synth == 0)
	{
		settings = new_fluid_settings();
		if (settings == 0) {
			goto error_recovery;
		}
		
		synth = new_fluid_synth(settings);
		if (synth == 0) {
			goto error_recovery;
		}
#ifdef VARIADIC_MACROS
		out("newSynth: synth: %p\n", synth);
#else
		if (debug_flag)
		{
			fprintf(debug_file, "newSynth: synth: %p\n", synth);
			fflush(debug_file);
		}
#endif
		
		adriver = new_fluid_audio_driver(settings, synth);
		if (adriver == 0) {
			goto error_recovery;
		}
		(*env)->SetLongField(env, obj, settingsPtrFieldID, (jlong) ((unsigned int) settings));
		(*env)->SetLongField(env, obj, synthPtrFieldID, (jlong) ((unsigned int) synth));
		(*env)->SetLongField(env, obj, audioDriverPtrFieldID, (jlong) ((unsigned int) adriver));
	}
	return 0;

error_recovery:
	fluid_jni_delete_synth(env, obj, settings, synth, adriver);
	return -1;
}


/*
 * Class:     org_tritonus_midi_device_fluidsynth_FluidSynthesizer
 * Method:    deleteSynth
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_org_tritonus_midi_device_fluidsynth_FluidSynthesizer_deleteSynth
(JNIEnv *env, jobject obj)
{
	fluid_synth_t* synth;
	fluid_settings_t* settings;
	fluid_audio_driver_t* adriver;

	synth = get_synth(env, obj);
#ifdef VARIADIC_MACROS
	out("deleteSynth: synth: %p\n", synth);
#else
	if (debug_flag)
	{
		fprintf(debug_file, "deleteSynth: synth: %p\n", synth);
		fflush(debug_file);
	}
#endif
	settings = (fluid_settings_t*) ((unsigned int) (*env)->GetLongField(env, obj, settingsPtrFieldID));
	adriver = (fluid_audio_driver_t*) ((unsigned int) (*env)->GetLongField(env, obj, audioDriverPtrFieldID));
	fluid_jni_delete_synth(env, obj, settings, synth, adriver);
}


/*
 * Class:     org_tritonus_midi_device_fluidsynth_FluidSynthesizer
 * Method:    nReceive
 * Signature: (IIII)V
 */
JNIEXPORT void JNICALL Java_org_tritonus_midi_device_fluidsynth_FluidSynthesizer_nReceive
(JNIEnv *env, jobject obj, jint command, jint channel, jint data1, jint data2)
{
	fluid_midi_event_t* event;
	fluid_synth_t* synth = get_synth(env, obj);

#ifdef VARIADIC_MACROS
	out("nReceive: synth: %p, values: %x %d %d %d\n", synth, (int) command, (int) channel, (int) data1, (int) data2);
#else
	if (debug_flag)
	{
		fprintf(debug_file, "synth: %p, values: %x %d %d %d\n", synth, (int) command, (int) channel, (int) data1, (int) data2);
		fflush(debug_file);
	}
#endif
	if (synth)
	{
		event = new_fluid_midi_event();
		if (!event)
		{
			printf("failed to instantiate fluid_midi_event_t\n");
			return;
		}
		//printf("2"); fflush(stdout);
		fluid_midi_event_set_type(event, command);
		fluid_midi_event_set_channel(event, channel);
		fluid_midi_event_set_key(event, data1);
		fluid_midi_event_set_velocity(event, data2);
		//printf("3"); fflush(stdout);
		/*printf("values2: %d %d %d %d\n",
		fluid_midi_event_get_type(event),
		fluid_midi_event_get_channel(event),
		fluid_midi_event_get_key(event),
		fluid_midi_event_get_velocity(event));
		fflush(stdout);
	*/
		fluid_synth_handle_midi_event(synth, event);
		//printf("4"); fflush(stdout);
		delete_fluid_midi_event(event);
		//printf("5\n"); fflush(stdout);
	}
}


/*
 * Class:     org_tritonus_midi_device_fluidsynth_FluidSynthesizer
 * Method:    loadSoundFont
 * Signature: (Ljava/lang/String;)I
 */
JNIEXPORT jint JNICALL Java_org_tritonus_midi_device_fluidsynth_FluidSynthesizer_loadSoundFont
(JNIEnv *env, jobject obj, jstring filename)
{
	const char *cfilename = (*env)->GetStringUTFChars(env, filename, 0);
	int sfont_id;
	fluid_synth_t* synth = get_synth(env, obj);

	if (synth == 0)
	{
		sfont_id = -1;
	}
	else
	{
		sfont_id = fluid_synth_sfload(synth, cfilename, 1);
	}
	(*env)->ReleaseStringUTFChars(env, filename, cfilename);

	return sfont_id;
}


/*
 * Class:     org_tritonus_midi_device_fluidsynth_FluidSynthesizer
 * Method:    setBankOffset
 * Signature: (II)V
 */
JNIEXPORT void JNICALL Java_org_tritonus_midi_device_fluidsynth_FluidSynthesizer_setBankOffset
(JNIEnv *env, jobject obj, jint sfontID, jint offset)
{
	fluid_synth_t* synth = get_synth(env, obj);
	fluid_synth_set_bank_offset(synth, sfontID, offset);
}

/*
 * Class:     org_tritonus_midi_sb_fluidsynth_FluidSoundbank
 * Method:    nGetInstruments
 * Signature: (I)[Lorg/tritonus/midi/sb/fluidsynth/FluidSoundbank/FluidInstrument;
 */
JNIEXPORT jobjectArray JNICALL Java_org_tritonus_midi_sb_fluidsynth_FluidSoundbank_nGetInstruments
(JNIEnv *env, jobject obj, jint sfontID)
{
	//printf("3a\n");
	//printf("4");
	jclass fluidsoundbankclass;
	jfieldID synthFieldID;
	jobject synthobj;
	jclass fluidinstrclass;
	jmethodID initid;
	int count = 0;
	jobjectArray instruments;
	jstring instrname;
	jobject instrument;

	fluid_sfont_t* sfont;
	fluid_preset_t preset;
	int offset;
	int i = 0;
	fluid_synth_t* synth;
	
	fluidsoundbankclass = (*env)->FindClass(env, "org/tritonus/midi/sb/fluidsynth/FluidSoundbank");
	synthFieldID = (*env)->GetFieldID(env, fluidsoundbankclass, "synth", "Lorg/tritonus/midi/device/fluidsynth/FluidSynthesizer;");
	synthobj = (*env)->GetObjectField(env, obj, synthFieldID);
	synth = get_synth(env, synthobj);
#ifdef VARIADIC_MACROS
	out("nGetInstruments: synth: %p\n", synth);
#else
	if (debug_flag)
	{
		fprintf(debug_file, "nGetInstruments: synth: %p\n", synth);
		fflush(debug_file);
	}
#endif
	if (synth)
	{
		fluidinstrclass = (*env)->FindClass(env, "org/tritonus/midi/sb/fluidsynth/FluidSoundbank$FluidInstrument");
		if (!fluidinstrclass) printf("could not get class id");
		//printf("5");
		initid = (*env)->GetMethodID(env, fluidinstrclass, "<init>", "(Lorg/tritonus/midi/sb/fluidsynth/FluidSoundbank;IILjava/lang/String;)V");
		if (!initid) printf("could not get method id");
		//printf("6");

		sfont = fluid_synth_get_sfont_by_id(synth, sfontID);
		
		if (sfont != NULL)
		{
			sfont->iteration_start(sfont);
			
			while (sfont->iteration_next(sfont, &preset))
			{
				count++;
			}
		}

		//printf("7");
		instruments = (*env)->NewObjectArray(env, count, fluidinstrclass, NULL);

		sfont = fluid_synth_get_sfont_by_id(synth, sfontID);
		offset = fluid_synth_get_bank_offset(synth, sfontID);

		if (sfont == NULL)
			return 0;

		sfont->iteration_start(sfont);

		while (sfont->iteration_next(sfont, &preset))
		{
			instrname = (*env)->NewStringUTF(env,
	//									fluid_preset_get_name(&preset)
										preset.get_name(&preset)
											);
			instrument = (*env)->NewObject(env, fluidinstrclass, initid, obj,
	//			(jint) fluid_preset_get_banknum(&preset) + offset,
				(jint) (preset.get_banknum(&preset) + offset),
	//			(jint) fluid_preset_get_num(&preset),
				(jint) (preset.get_num(&preset)),
				(jobject) instrname);
			(*env)->SetObjectArrayElement(env, instruments, i++, instrument);
		}
		return instruments;
	}
	else
		return NULL;
}


/*
 * Class:     org_tritonus_midi_device_fluidsynth_FluidSynthesizer
 * Method:    setGain
 * Signature: (F)V
 */
JNIEXPORT void JNICALL Java_org_tritonus_midi_device_fluidsynth_FluidSynthesizer_setGain
(JNIEnv *env, jobject obj, jfloat gain)
{
	fluid_synth_t* synth = get_synth(env, obj);
	fluid_synth_set_gain(synth, (float) gain);
}


/*
 * Class:     org_tritonus_midi_device_fluidsynth_FluidSynthesizer
 * Method:    setReverbPreset
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_org_tritonus_midi_device_fluidsynth_FluidSynthesizer_setReverbPreset
(JNIEnv *env, jobject obj, jint reverbPreset)
{
/* $$mp: currently not functional because fluid_synth_set_reverb_preset() is not
         present in fluidsynth 1.0.6
*/
	//fluid_synth_set_reverb_preset(synth, (int) reverbPreset);
}


/*
 * Class:     org_tritonus_midi_device_fluidsynth_FluidSynthesizer
 * Method:    getMaxPolyphony
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_org_tritonus_midi_device_fluidsynth_FluidSynthesizer_getMaxPolyphony
(JNIEnv *env, jobject obj)
{
	fluid_synth_t* synth = get_synth(env, obj);
	if (synth)
	{
		return fluid_synth_get_polyphony(synth);
	}
	else
	{
		return -1;
	}
}


/*
 * Class:     org_tritonus_midi_device_fluidsynth_FluidSynthesizer
 * Method:    noteOn
 * Signature: (III)V
 */
JNIEXPORT void JNICALL Java_org_tritonus_midi_device_fluidsynth_FluidSynthesizer_noteOn
(JNIEnv *env, jobject obj, jint channel, jint key, jint velocity)
{
	fluid_synth_t* synth = get_synth(env, obj);
	if (synth)
	{
		fluid_synth_noteon(synth, channel, key, velocity);
	}
}


/*
 * Class:     org_tritonus_midi_device_fluidsynth_FluidSynthesizer
 * Method:    noteOff
 * Signature: (III)V
 */
JNIEXPORT void JNICALL Java_org_tritonus_midi_device_fluidsynth_FluidSynthesizer_noteOff
(JNIEnv *env, jobject obj, jint channel, jint key, jint velocity)
{
	fluid_synth_t* synth = get_synth(env, obj);
	if (synth)
	{
		/* There is no method noteoff that takes a velocity param. */
		//fluid_synth_noteoff(synth, channel, key, velocity);
		fluid_synth_noteoff(synth, channel, key);
	}
}


/*
 * Class:     org_tritonus_midi_device_fluidsynth_FluidSynthesizer
 * Method:    controlChange
 * Signature: (III)V
 */
JNIEXPORT void JNICALL Java_org_tritonus_midi_device_fluidsynth_FluidSynthesizer_controlChange
(JNIEnv *env, jobject obj, jint channel, jint controller, jint value)
{
	fluid_synth_t* synth = get_synth(env, obj);
	if (synth)
	{
		fluid_synth_cc(synth, channel, controller, value);
	}
}


/*
 * Class:     org_tritonus_midi_device_fluidsynth_FluidSynthesizer
 * Method:    getController
 * Signature: (II)I
 */
JNIEXPORT jint JNICALL Java_org_tritonus_midi_device_fluidsynth_FluidSynthesizer_getController
(JNIEnv *env, jobject obj, jint channel, jint controller)
{
	fluid_synth_t* synth = get_synth(env, obj);
	int value = 0;
	if (synth)
	{
		fluid_synth_get_cc(synth, channel, controller, &value);
	}
	return value;
}


/*
 * Class:     org_tritonus_midi_device_fluidsynth_FluidSynthesizer
 * Method:    programChange
 * Signature: (II)V
 */
JNIEXPORT void JNICALL Java_org_tritonus_midi_device_fluidsynth_FluidSynthesizer_programChange
(JNIEnv *env, jobject obj, jint channel, jint program)
{
	fluid_synth_t* synth = get_synth(env, obj);
	if (synth)
	{
		fluid_synth_program_change(synth, channel, program);
	}
}


/*
 * Class:     org_tritonus_midi_device_fluidsynth_FluidSynthesizer
 * Method:    getProgram
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_org_tritonus_midi_device_fluidsynth_FluidSynthesizer_getProgram
(JNIEnv *env, jobject obj, jint channel)
{
	fluid_synth_t* synth = get_synth(env, obj);
	unsigned int sfont;
	unsigned int bank;
	unsigned int program = 0;
	if (synth)
	{
		fluid_synth_get_program(synth, channel, &sfont, &bank, &program);
	}
	return program;
}


/*
 * Class:     org_tritonus_midi_device_fluidsynth_FluidSynthesizer
 * Method:    setPitchBend
 * Signature: (II)V
 */
JNIEXPORT void JNICALL Java_org_tritonus_midi_device_fluidsynth_FluidSynthesizer_setPitchBend
(JNIEnv *env, jobject obj, jint channel, jint bend)
{
	fluid_synth_t* synth = get_synth(env, obj);
	if (synth)
	{
		fluid_synth_pitch_bend(synth, channel, bend);
	}
}


/*
 * Class:     org_tritonus_midi_device_fluidsynth_FluidSynthesizer
 * Method:    getPitchBend
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_org_tritonus_midi_device_fluidsynth_FluidSynthesizer_getPitchBend
(JNIEnv *env, jobject obj, jint channel)
{
	fluid_synth_t* synth = get_synth(env, obj);
	int bend = 0;
	if (synth)
	{
		fluid_synth_get_pitch_bend(synth, channel, &bend);
	}
	return bend;
}


/*
 * Class:     org_tritonus_midi_device_fluidsynth_FluidSynthesizer
 * Method:    setTrace
 * Signature: (Z)V
 */
JNIEXPORT void JNICALL Java_org_tritonus_midi_device_fluidsynth_FluidSynthesizer_setTrace
(JNIEnv *env, jclass cls, jboolean bTrace)
{
	debug_flag = bTrace;
	debug_file = stderr;
	/* Don't try to turn of log levels in Win32 since fluid_log_config() is not an exported function
	 * in the fluidsynth DLL and so causes a link error
	 */
#if !defined(WIN32) && !defined(__CYGWIN__)
	if (!bTrace)
	{
		/* fluid_log_config() is not part of the public API of fluidsynth.
		 * However, this call is necessary because of a bug in fluidsynth:
		 * fluid_set_log_function() does not initialize the data structures.
		 * This is only done with the first call to fluid_log() and then, log
		 * functions that are NULL are initialized to the default, so that
		 * setting them to NULL with fluid_set_log_function() previously has no
		 * effect.
		 */
		fluid_log_config();
		fluid_set_log_function(FLUID_WARN, NULL, NULL);
		fluid_set_log_function(FLUID_INFO, NULL, NULL);
	}
#endif
}


/* org_tritonus_midi_device_fluidsynth_FluidSynthesizer.c */
