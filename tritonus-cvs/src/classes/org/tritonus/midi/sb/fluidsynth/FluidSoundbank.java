/*
 * FluidSoundbank.java
 *
 * This file is part of Tritonus: http://www.tritonus.org/
 */

/*
 * Copyright (c) 2006 by Henri Manson
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

package org.tritonus.midi.sb.fluidsynth;

import javax.sound.midi.Instrument;
import javax.sound.midi.Patch;
import javax.sound.midi.Soundbank;
import javax.sound.midi.SoundbankResource;

import org.tritonus.midi.device.fluidsynth.FluidSynthesizer;

/**
 *
 * @author Manson
 */
public class FluidSoundbank implements Soundbank
{
	private FluidSynthesizer synth;
    private int sfontID;
    private FluidInstrument[] instruments;

	// $$mp: needs to be public for native code now
    public class FluidInstrument extends Instrument
    {
        public FluidInstrument(int bank, int program, String name)
        {
            super(FluidSoundbank.this, new Patch(bank, program), name, null);
        }

        public String toString()
        {
	        return "Instrument "  + getName() + " (bank " + getPatch().getBank() + " program " + getPatch().getProgram() + ")";
        }
        
        public Object getData()
        {
            return null;
        }
    }


    public FluidSoundbank(FluidSynthesizer synth, int sfontID)
    {
	    this.synth = synth;
        this.sfontID = sfontID;
		//TDebug.out("1");
        instruments = nGetInstruments(sfontID);
		//TDebug.out("2");
    }


    public native FluidInstrument[] nGetInstruments(int sfontID);

    public Instrument getInstrument(Patch patch)
    {
        return null;
    }

    public String getVersion()
    {
        return "1.0";
    }

    public String getVendor()
    {
        return "Mansoft";
    }

    public SoundbankResource[] getResources()
    {
        return null;
    }

    public String getName()
    {
        return "Mansoft";
    }

    public Instrument[] getInstruments()
    {
        return instruments;
    }

    public String getDescription()
    {
        return "Mansoft";
    }
}

/* FluidSoundbank.java */


