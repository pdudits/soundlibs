/*
 * FluidSynthesizer.java
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

package org.tritonus.midi.device.fluidsynth;

import javax.sound.midi.*;

import org.tritonus.share.TDebug;
import org.tritonus.share.midi.TMidiChannel;
import org.tritonus.share.midi.TDirectSynthesizer;
import org.tritonus.midi.sb.fluidsynth.FluidSoundbank;


public class FluidSynthesizer
extends TDirectSynthesizer
implements Synthesizer
{
    private MidiChannel channels[];
    private FluidSoundbank defaultSoundbank;

	private int defaultbankSfontID;
    
    // native pointers 64 bit maximum
	private long settingsPtr;
	private long synthPtr;
	private long audioDriverPtr;



	static
	{
		loadNativeLibrary();
	}


	/** Load the native library for fluidsynth.
	 */
	private static void loadNativeLibrary()
	{
		if (TDebug.TraceFluidNative) TDebug.out("FluidSynthesizer.loadNativeLibrary(): loading native library tritonusfluid");
		try
		{
			System.loadLibrary("tritonusfluid");
			// only reached if no exception occures
			setTrace(TDebug.TraceFluidNative);
		}
		catch (Error e)
		{
			if (TDebug.TraceFluidNative ||
			    TDebug.TraceAllExceptions)
			{
				TDebug.out(e);
			}
			// throw e;
		}
		if (TDebug.TraceFluidNative) TDebug.out("FluidSynthesizer.loadNativeLibrary(): loaded");
	}



	/**
	 * Constructor.
	 */
    public FluidSynthesizer(MidiDevice.Info info) throws Exception
    {
        super(info);
    }


	protected void openImpl() 
	throws MidiUnavailableException
	{
		if (newSynth() < 0)
        {
            throw new MidiUnavailableException("Low-level initialization of the synthesizer failed");
        }
        if (TDebug.TraceSynthesizer) TDebug.out("FluidSynthesizer: " + Long.toHexString(synthPtr));

        channels = new MidiChannel[16];
        for (int i = 0; i < 16; i++)
        {
        	channels[i] = new NewFluidMidiChannel(i);
        }

        String sfontFile =
			System.getProperty("tritonus.fluidsynth.defaultsoundbank");
		if (sfontFile != null && ! sfontFile.equals(""))
		{
			int sfontID = loadSoundFont(sfontFile);
			setDefaultSoundBank(sfontID);
	        String strBankOffset =
				System.getProperty("tritonus.fluidsynth.defaultsoundbankoffset");
			if (strBankOffset != null && ! strBankOffset.equals(""))
			{
				setBankOffset(sfontID, Integer.parseInt(strBankOffset));				
			}
		}
    }


    protected void closeImpl()
    {
        if (TDebug.TraceSynthesizer) TDebug.out("FluidSynthesizer.closeImpl(): "
        		+ Long.toHexString(synthPtr));
        deleteSynth();
        super.closeImpl();
    }




    public void setDefaultSoundBank(int sfontID)
    {
	    defaultSoundbank = new FluidSoundbank(this, sfontID);
	    defaultbankSfontID = sfontID;
    }


    protected void finalize(){
        if (TDebug.TraceSynthesizer) TDebug.out("finalize: " + Long.toHexString(synthPtr));
        close();
    }

    public native int loadSoundFont(String filename);
    public native void setBankOffset(int sfontID, int offset);
    public native void setGain(float gain);

	/* $$mp: currently not functional because fluid_synth_set_reverb_preset()
	 * is not present in fluidsynth 1.0.6.
	 */
    public native void setReverbPreset(int reverbPreset);

    public native int getMaxPolyphony();

    protected native int newSynth();
    protected native void deleteSynth();

    /**
     * Turns a note on.
     * 
     * The implementation calls fluid_synth_noteoff().
     * 
     * @param nChannel the channel
     * @param nNoteNumber the note
     * @param nVelocity the velocity
     */
	native void noteOn(int nChannel, int nNoteNumber, int nVelocity);

	/**
	 * Turns a note off.
	 * 
	 * The implementation calls fluid_synth_noteon().
	 * 
	 * @param nChannel the channel
	 * @param nNoteNumber the note
	 * @param nVelocity the velocity
	 */
	native void noteOff(int nChannel, int nNoteNumber, int nVelocity);


	/**
	 * Changes a controller on the synthesizer.
     * 
     * The implementation calls fluid_synth_cc().
     * 
     * @param nChannel the channel
     * @param nController the controller number
     * @param nValue the controller value
     */
	native void controlChange(int nChannel, int nController, int nValue);


	/**
	 * Obtains the value of a controller.
	 * 
	 * The implementation calls fluid_synth_get_cc().
	 * 
	 * @param nChannel the channel
	 * @param nController the controller number
	 * @return the controller value
	 */
	native int getController(int nChannel, int nController);


	/**
	 * Sets the program for a channel.
	 * 
	 * The implementation calls fluid_synth_program_change().
	 * 
	 * @param nChannel the channel
	 * @param nProgram the program number
	 */
	native void programChange(int nChannel, int nProgram);


	/**
	 * Obtains the program set for a channel.
	 * 
	 * The implementation calls fluid_synth_get_program().
	 * 
	 * @param nChannel the channel
	 * @return the program number set for this channel
	 */
	native int getProgram(int nChannel);


	/**
	 * Sets the pitch bend for a channel.
	 * 
	 * The implementation calls fluid_synth_pitch_bend().
	 * 
	 * @param nChannel the channel
	 * @param nBend the pitch bend value
	 */
	native void setPitchBend(int nChannel, int nBend);


	/**
	 * Obtains the pitch bend for a channel.
	 * 
	 * The implementations calls fluid_synth_get_pitch_bend().
	 * 
	 * @param nChannel the channel
	 * @return the pitch bend value.
	 */
	native int getPitchBend(int nChannel);


	/** Sets tracing in the native code.
	 * Note that this method can either be called directly or (recommended)
	 * the system property "tritonus.TraceFluidNative" can be set to true.
	 *
	 * @see org.tritonus.share.TDebug
	 */
	public static native void setTrace(boolean bTrace);


    public boolean isSoundbankSupported(Soundbank soundbank)
    {
        return (soundbank instanceof FluidSoundbank);
    }

    public boolean loadAllInstruments(Soundbank soundbank)
    {
    	checkSoundbank(soundbank);
        return true;
    }

    public void unloadAllInstruments(Soundbank soundbank)
    {
    	checkSoundbank(soundbank);
    }

    public void unloadInstruments(Soundbank soundbank, Patch[] patchList)
    {
    	checkSoundbank(soundbank);
    }

    public boolean loadInstruments(Soundbank soundbank, Patch[] patchList)
    {
    	checkSoundbank(soundbank);
        return true;
    }

    public void unloadInstrument(Instrument instrument)
    {
    	checkInstrument(instrument);
    }

    public boolean loadInstrument(Instrument instrument)
    {
    	checkInstrument(instrument);
        return true;
    }

    public Instrument[] getAvailableInstruments()
    {
        return null;
    }

    public MidiChannel[] getChannels()
    {
        return channels;
    }

    public Soundbank getDefaultSoundbank()
    {
       return defaultSoundbank;
    }

    public long getLatency()
    {
        return 0L;
    }

    public Instrument[] getLoadedInstruments()
    {
        return null;
    }

    public VoiceStatus[] getVoiceStatus()
    {
        return new VoiceStatus[0];
    }

    public boolean remapInstrument(Instrument from, Instrument to)
    {
    	checkInstrument(from);
    	checkInstrument(to);
        return true;
    }


	/** Checks if the soundbank is supported by this synthesizer implementation.
     * 
     * @param sb the soundbank to check
     * @throws IllegalArgumentException if the soundbank is not supported
     */
    private void checkSoundbank(Soundbank sb)
    {
    	if (! isSoundbankSupported(sb))
    		throw new IllegalArgumentException("soundbank is not supported");
    }

    /**
     * Checks if the instrument belongs to a soundbank that is supported by this
     * synthesizer implementation.
     * 
     * @param instr the instrument to check
     * @throws IllegalArgumentException if the instrument's soundbank
     * is not supported
     */
    private void checkInstrument(Instrument instr)
    {
    	checkSoundbank(instr.getSoundbank());
    }


    private class NewFluidMidiChannel
    extends TMidiChannel
	{
		public NewFluidMidiChannel(int nChannel)
		{
			super(nChannel);
		}


		public void noteOn(int nNoteNumber, int nVelocity)
		{
			FluidSynthesizer.this.noteOn(getChannel(), nNoteNumber, nVelocity);
		}


		public void noteOff(int nNoteNumber, int nVelocity)
		{
			FluidSynthesizer.this.noteOff(getChannel(), nNoteNumber, nVelocity);
		}


		public void noteOff(int nNoteNumber)
		{
			noteOff(nNoteNumber, 0);
		}


		/**
		 * Fluidsynth does not implement poly pressure (aftertouch). Therefore,
		 * this method does nothing.
		 */
		public void setPolyPressure(int nNoteNumber, int nPressure)
		{
		}


		/**
		 * Fluidsynth does not implement poly pressure (aftertouch). Therefore,
		 * this method always return 0.
		 */
		public int getPolyPressure(int nNoteNumber)
		{
			return 0;
		}


		/**
		 * Fluidsynth does not implement channel pressure. Therefore,
		 * this method does nothing.
		 */
		public void setChannelPressure(int nPressure)
		{
		}


		/**
		 * Fluidsynth does not implement channel pressure. Therefore,
		 * this method always returns 0.
		 */
		public int getChannelPressure()
		{
			return 0;
		}


		public void controlChange(int nController, int nValue)
		{
			FluidSynthesizer.this.controlChange(getChannel(), nController,
					nValue);
		}


		public int getController(int nController)
		{
			return FluidSynthesizer.this.getController(getChannel(),
						nController);
		}


		public void programChange(int nProgram)
		{
			FluidSynthesizer.this.programChange(getChannel(), nProgram);
		}


		public int getProgram()
		{
			return FluidSynthesizer.this.getProgram(getChannel());
		}


		public void setPitchBend(int nBend)
		{
			FluidSynthesizer.this.setPitchBend(getChannel(), nBend);
		}


		public int getPitchBend()
		{
			return FluidSynthesizer.this.getPitchBend(getChannel());
		}


		// TODO: emulate by manipulating volume
		public void setMute(boolean bMute)
		{
		}


		public boolean getMute()
		{
			return false;
		}


		public void setSolo(boolean bSolo)
		{
		}


		public boolean getSolo()
		{
			return false;
		}
	}
}

/* FluidSynthesizer.java */
