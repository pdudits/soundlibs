/*
 * Rpe port to Java.
 * Copyright (C) 1999  Christopher Edwards
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 */

package org.tritonus.lowlevel.gsm;



public class Rpe
{
	private short 	exp_in;      /* IN   */
	private short 	mant_in;     /* IN   */
	private short 	exp_out;     /* OUT  */
	private short 	mant_out;    /* OUT  */
	private int 	xMp_point = 0;

	private static final int ENCODE = 0;
	private static final int DECODE = 1;
        
	private short[] x = new short[40];	/* signal [0..39]         OUT */   

	public void Gsm_RPE_Encoding(
		short[]	e,	/* -5..-1][0..39][40..44            IN/OUT  */
		short[]	xmaxc,	/* [0..3] Coded maximum amplitude   OUT     */
		short[]	Mc,	/* [0..3] RPE grid selection        OUT     */
		int	xmaxc_Mc_index,	/* Ref. point for xmaxc and Mc            */
		short[]	xMc,	/* [0..12]                          OUT     */
		int	xMc_index	/* Ref. for xmc, '+=13' */ )
	{
		short[]	xM  = new short[13]; 
		short[]	xMp = new short[13];

		Weighting_filter( e );  /* Sets up the private data member 'x[40]' */
		RPE_grid_selection( xM, Mc, xmaxc_Mc_index );    /* Sets up xM[13] */

		/* Sets up xmc (13 array locations starting from xMc_index),
		 * xmaxc (one array location at xmaxc_Mc_index), 
		 * exp_in and mant_in 
		 */
		APCM_quantization( xM, xMc, xMc_index, xmaxc, xmaxc_Mc_index );
		/* Sets up xMp[13] */
		APCM_inverse_quantization( xMc, xMp, xMc_index, ENCODE );

		RPE_grid_positioning( Mc[xmaxc_Mc_index], xMp, e, ENCODE );
	}



	/*
	 *  The coefficients of the weighting filter are stored in a table
	 *  (see table 4.4).  The following scaling is used:
	 *
	 *      H[0..10] = integer( real_H[ 0..10] * 8192 );
	 */
	private void Weighting_filter(
		short[] e)	/* signal [-5..0.39.44] IN  */ 
	{
		int     L_result = 0;

		/*
		 *  (e[-5..-1] and e[40..44] are allocated by the caller,
		 *  are initially zero and are not written anywhere.)
		 *
		 *  e -= 5;
		 * 
		 *  The above case is true with the C code, although java
		 *  does not have pointers so e[0..49] is all set for this 
		 *  method.
		 */

		/*  Compute the signal x[0..39]
		 */
		for (int k = 0; k <= 39; k++)
		{
			L_result = 8192 >> 1;

			/*  Every one of these multiplications is done twice --
			 *  but I don't see an elegant way to optimize this.
			 *  Do you?
			 */

		        /* #define STEP( i, H )    (e[ k + i ] * H) */
                
			L_result +=( e[ k + 0 ] * -134 )
				+ ( e[ k + 1 ] * -374 )
				/* + STEP( 2,      0    ) no sense in adding zero */
				+ ( e[ k + 3 ] * 2054 )
				+ ( e[ k + 4 ] * 5741 )
				+ ( e[ k + 5 ] * 8192 )
				+ ( e[ k + 6 ] * 5741 )
				+ ( e[ k + 7 ] * 2054 )
				/* + STEP( 8,      0    ) no sense in adding zero */
				+ ( e[ k + 9 ] * -374 )
				+ ( e[ k + 10 ] * -134 );

			/* 2 adds vs. >>16 => 14, minus one shift to compensate for
			 * those we lost when replacing L_MULT by '*'.
			 */
 
			L_result = Add.SASR( L_result, 13 );
			x[k] = (short) ((L_result < Gsm_Def.MIN_WORD ? Gsm_Def.MIN_WORD
					 : (L_result > Gsm_Def.MAX_WORD ? Gsm_Def.MAX_WORD 
					    : L_result )));
		}
	}



	/*
	 *  The signal x[0..39] is used to select the RPE grid which is
	 *  represented by Mc.
	 */
	private void RPE_grid_selection(
		short[]    xM,           /* [0..12]              OUT */
		short[]    Mc_out,       /*                      OUT */   
		int	   Mc_index)
	{
		int     L_result = 0;
		int     EM = 0;     /* xxx should be L_EM? */
		short   Mc = 0;

		int     L_common_0_3;

		/* common part of 0 and 3 */

		L_result = 0;
        
		L_result += STEP( 0, 1 ) +  STEP( 0, 2 ) +  STEP( 0, 3 ) +  STEP( 0, 4 ) 
			+  STEP( 0, 5 ) +  STEP( 0, 6 ) +  STEP( 0, 7 ) +  STEP( 0, 8 ) 
			+  STEP( 0, 9 ) +  STEP( 0, 10) +  STEP( 0, 11) +  STEP( 0, 12);
                 
		L_common_0_3 = L_result;

		/* i = 0 */
		L_result += STEP( 0, 0 );
		L_result <<= 1; /* implicit in L_MULT */
		EM = L_result;

		/* i = 1 */
		L_result = 0;
		L_result += STEP( 1, 0 ) + STEP( 1, 1 ) + STEP( 1, 2 ) + STEP( 1, 3 )
			+  STEP( 1, 4 ) + STEP( 1, 5 ) + STEP( 1, 6 ) + STEP( 1, 7 )
			+  STEP( 1, 8 ) + STEP( 1, 9 ) + STEP( 1, 10) + STEP( 1, 11)
			+  STEP( 1, 12);
        
		L_result <<= 1;
		if (L_result > EM) {
			Mc = 1;
			EM = L_result;
		}

		/* i = 2 */
		L_result = 0;
		L_result += STEP( 2, 0 ) + STEP( 2, 1 ) + STEP( 2, 2 ) + STEP( 2, 3 )
			+  STEP( 2, 4 ) + STEP( 2, 5 ) + STEP( 2, 6 ) + STEP( 2, 7 )
			+  STEP( 2, 8 ) + STEP( 2, 9 ) + STEP( 2, 10) + STEP( 2, 11)
			+  STEP( 2, 12);
       
		L_result <<= 1;
		if (L_result > EM) {
			Mc = 2;
			EM = L_result;
		}

		/* i = 3 */
		L_result = L_common_0_3;
		L_result += STEP( 3, 12 );
		L_result <<= 1;
		if (L_result > EM)
		{
			Mc = 3;
			EM = L_result;
		}

		/*  Down-sampling by a factor 3 to get the selected xM[0..12]
		 *  RPE sequence.
		 */
		for (int i = 0; i <= 12; i ++)
		{
			xM[i] = x[Mc + 3*i];
		}	    
		Mc_out[Mc_index] = Mc;
	}



	private int STEP(int m, int i )
	{
		int L_temp;
		L_temp = Add.SASR( x[m + 3 * i], 2 );       
		return ( L_temp * L_temp );
	}
 


	private void APCM_quantization (
		short[]         xM,           /* [0..12]              IN      */
		short[]         xMc,          /* [0..12]              OUT     */
		int		xMc_index,
		short[]         xmaxc_out,    /*                      OUT     */
		int 		xmaxc_index)
		throws IllegalArgumentException {

		int     itest = 0;
		short   xmax = 0, xmaxc = 0, temp = 0, temp1 = 0, temp2 = 0;
		short   exp = 0, mant = 0;

		/*  Find the maximum absolute value xmax of xM[0..12].
		 */
		for (int i = 0; i <= 12; i++)
		{
			temp = xM[i];
			temp = Add.GSM_ABS(temp);
			if (temp > xmax)
			{
				xmax = temp;
			}
		}

		/*  Qantizing and coding of xmax to get xmaxc.
		 */
		exp   = 0;
		temp  = Add.SASR( xmax, 9 );
		itest = 0;

		for (int i = 0; i <= 5; i++)
		{
			if(temp <= 0)
			{
				itest |= 1;
			}
			else
			{
				itest |= 0;
			}
			temp = Add.SASR( temp, 1 );

			if( ! (exp <= 5))
			{
				throw new IllegalArgumentException
					("APCM_quantization: exp = "
					 +exp+" is out of range. Should be <= 5");
			}

			if (itest == 0)
			{
				exp++;          /* exp = add (exp, 1) */
			}
		}

		if( ! (exp <= 6 && exp >= 0))
		{
			throw new IllegalArgumentException
				("APCM_quantization: exp = "
				 +exp+" is out of range. Should be >= -4 and <= 6");
		}

		temp = (short) (exp + 5);

		if( ! (temp <= 11 && temp >= 0))
		{
			throw new IllegalArgumentException
				("APCM_quantization: temp = "
				 +temp+" is out of range. Should be >= 0 and <= 11");
		}

		xmaxc = Add.GSM_ADD( Add.SASR(xmax, temp), (short) (exp << 3) );

		/*   Quantizing and coding of the xM[0..12] RPE sequence
		 *   to get the xMc[0..12]
		 */
        
		APCM_quantization_xmaxc_to_exp_mant( xmaxc, ENCODE );
		exp = exp_in;
		mant = mant_in;
        
		/*  This computation uses the fact that the decoded version of xmaxc
		 *  can be calculated by using the exponent and the mantissa part of
		 *  xmaxc (logarithmic table).
		 *  So, this method avoids any division and uses only a scaling
		 *  of the RPE samples by a function of the exponent.  A direct
		 *  multiplication by the inverse of the mantissa (NRFAC[0..7]
		 *  found in table 4.5) gives the 3 bit coded version xMc[0..12]
		 *  of the RPE samples.
		 */

		/* Direct computation of xMc[0..12] using table 4.5
		 */

		if( ! (exp <= 4096 && exp >= -4096))
		{
			throw new IllegalArgumentException
				("APCM_quantization: exp = "
				 +exp+" is out of range. Should be >= -4096 and <= 4096");
		}
		if( ! (mant >= 0 && mant <= 7 ))
		{
			throw new IllegalArgumentException
				("APCM_quantization: mant = "
				 +mant+" is out of range. Should be >= 0 and <= 7");
		}

		temp1 = (short) (6 - exp);          /* normalization by the exponent */
		temp2 = Gsm_Def.gsm_NRFAC[ mant ];  /* inverse mantissa          */

		for (int i = 0; i <= 12; i++)
		{
			if( ! (temp1 >= 0 && temp1 < 16))
			{
				throw new IllegalArgumentException
					("APCM_quantization: temp = "
					 +temp+" is out of range. Should be >= 0 and < 16");
			}

			temp = (short) (xM[i] << temp1);
			temp = Add.GSM_MULT( temp, temp2 );
			temp = Add.SASR(temp, 12);
			xMc[i + xMc_index] = (short) (temp + 4);    /* see note below */
		}

		/*  NOTE: This equation is used to make all the xMc[i] positive.
		 */
		mant_in   = mant;
		exp_in    = exp;
		xmaxc_out[xmaxc_index] = xmaxc;
	}


	public void APCM_quantization_xmaxc_to_exp_mant(
		short xmaxc_elem,
		int METHOD_ID)
		throws IllegalArgumentException
	{
		short    exp = 0, mant = 0;
		/* Compute exponent and mantissa of the decoded version of xmaxc
		 */

		if (xmaxc_elem > 15)
		{
			exp = (short) (Add.SASR(xmaxc_elem, 3) - 1);
		}
		mant = (short) (xmaxc_elem - (exp << 3));

		if (mant == 0)
		{
			exp = (short) -4;
			mant = (short) 7;
		}
		else
		{
			while (mant <= 7)
			{
				mant = (short) (mant << 1 | 1);
				exp--;
			}
			mant -= (short) 8;
		}
		if( exp < -4 || exp > 6 )
		{
			throw new IllegalArgumentException
				("APCM_quantization_xmaxc_to_exp_mant: exp = "
				 +exp+" is out of range. Should be >= -4 and <= 6");
		}
		if( mant < 0 || mant > 7 )
		{
			throw new IllegalArgumentException
				("APCM_quantization_xmaxc_to_exp_mant: mant = "
				 +mant+" is out of range. Should be >= 0 and <= 7");
		}
		if ( METHOD_ID == ENCODE )
		{
			exp_in  = exp;
			mant_in = mant;
		}
		else
		{ /* DECODE */
			exp_out  = exp;
			mant_out = mant;
		}
	}



	public void Gsm_RPE_Decoding_java(
		short xmaxc_elem,  /* From Gsm_Decoder xmaxc short array */
		short Mc_elem,     /* From Gsm_Decoder Mc short array */
		int	xmc_start,	/* Starting point for the three bit part of xmc */
		short[] xmc,       /* [0..12], 3 bits     IN      */
		short[] erp        /* [0..39]             OUT     */  )
	{
		short    xMp[] = new short[13];

		/* exp_out and mant_out are modified in this method */
		APCM_quantization_xmaxc_to_exp_mant(xmaxc_elem, DECODE);

		APCM_inverse_quantization(xmc, xMp, xmc_start, DECODE);

		RPE_grid_positioning(Mc_elem, xMp, erp, DECODE);
	}



	/*
	 *  This part is for decoding the RPE sequence of coded xMc[0..12]
	 *  samples to obtain the xMp[0..12] array.  Table 4.6 is used to get
	 *  the mantissa of xmaxc (FAC[0..7]).
	 */
	public void APCM_inverse_quantization(
		short[] xmc,   /* [0..12]                      IN      */
		short[] xMp,   /* [0..12]                      OUT     */
		int 	xmc_start,
		int 	METHOD_ID)
		throws IllegalArgumentException
	{
		short      temp, temp1, temp2, temp3;

		if ( METHOD_ID == ENCODE )
		{
			temp1 = Gsm_Def.gsm_FAC[ mant_in ];
			temp2 = Add.GSM_SUB( (short)6, exp_in );
		}
		else
		{ /* DECODE */
			temp1 = Gsm_Def.gsm_FAC[ mant_out ];   
			temp2 = Add.GSM_SUB( (short)6, exp_out ); 
		}
		temp3 = Add.gsm_asl( (short)1, Add.GSM_SUB( temp2, (short)1 ));
        
		xMp_point = 0;
        
		for (int i = 0; i < 13; i++)
		{
			/* restore sign   */
			temp = (short) ((xmc[xmc_start++] << 1) - 7);  
			if( ! (temp <= 7 && temp >= -7) )
			{    /* 4 bit signed   */
				throw new IllegalArgumentException
					("APCM_inverse_quantization: temp = "
					 +temp+" is out of range. Should be >= -7 and <= 7");
			}
			temp <<= 12;                        /* 16 bit signed  */
			temp = Add.GSM_MULT_R( temp1, temp );
			temp = Add.GSM_ADD( temp, temp3 );
			xMp[ xMp_point++ ] = Add.gsm_asr( temp, temp2 );
		}
	}



	/*
	 *  This method computes the reconstructed long term residual signal
	 *  ep[0..39] for the LTP analysis filter.  The inputs are the Mc
	 *  which is the grid position selection and the xMp[0..12] decoded
	 *  RPE samples which are upsampled by a factor of 3 by inserting zero
	 *  values.
	 */
	public static void RPE_grid_positioning(
		short   Mc,            /* grid position        IN      */
		short[] xMp,           /* [0..12]              IN      */
		short[] ep,            /* [0..39]              OUT     */
		int     METHOD_ID)
		throws IllegalArgumentException
	{
    		int     i = 13;	     
		int     xMp_index = 0;
		int 	ep_index;
        
		if (METHOD_ID == ENCODE)
		{
			ep_index = 5;
		}
		else
		{ /* Decode */
			ep_index = 0;
		}
        
		if( ! (0 <= Mc && Mc <= 3) )
		{
			throw new IllegalArgumentException
				("RPE_grid_positioning: Mc = "
				 +Mc+" is out of range. Should be >= 0 and <= 3");
		}

		switch (Mc)
		{
                case 3:
			ep[ep_index++] = 0;
		        do
			{
				ep[ep_index++] = 0;
				ep[ep_index++] = 0;
				ep[ep_index++] = xMp[xMp_index++];
				--i;
                        }
			while (i != 0);
			break;

                case 2:
			do
			{
				ep[ep_index++] = 0;
				ep[ep_index++] = 0;
				ep[ep_index++] = xMp[xMp_index++];
				--i;
			}
			while (i != 0);
			break;

                case 1:
			do
			{
				ep[ep_index++] = 0;
				ep[ep_index++] = xMp[xMp_index++];
				ep[ep_index++] = 0;
				--i;
			}
			while (i != 0);
			break;

                case 0:
			do
			{
				ep[ep_index++] = xMp[xMp_index++];
				ep[ep_index++] = 0;
				ep[ep_index++] = 0;
				--i;
			}
			while (i != 0);
			break;
		}

		if (METHOD_ID == ENCODE)
		{
			ep[ep_index++] = 0;
		}
	}
}
