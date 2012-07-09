/*
 *	Instrument_tone.java
 */

import	org.tritonus.share.TDebug;



public class Instrument_tone
extends AbstractInstrument
{
	private float	a;
	private float	x;
	private float	y;
	private float	init;



	public Instrument_tone()
	{
		a = 0.0F;
		x = 0.0F;
		y = 0.0F;
		init = 0.0F;
	}



// 	public void doIPass()
// 	{
// 	}



// 	public void doKPass()
// 	{
// 	}



	public void doAPass(RTSystem rtSystem)
	{
		// TDebug.out("doAPass()");
		a = 0.196307F;
		if (init == 0.0F)
		{
			init = 1.0F;
			x = 0.5F;
		}
		x = x - a * y;
		y = y + a * x;
		rtSystem.output(y);
	}
}



/*** Instrument_tone.java ***/
