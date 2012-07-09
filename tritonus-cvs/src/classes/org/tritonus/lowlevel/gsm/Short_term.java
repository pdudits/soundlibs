/*
 * Short term port to Java.
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



public class Short_term
{
	public void Gsm_Short_Term_Analysis_Filter(
		Gsm_State	S,
		short[]		LARc,	/* coded log area ratio [0..7]  IN  */
		short[]		s	/* signal [0..159]          IN/OUT  */
		)
		throws ArrayIndexOutOfBoundsException
	{
		short[] LARp = new short[8];

		int array_index0 = S.getJ();
		int array_index1 = array_index0;

		array_index1 ^= 1;
		S.setJ( (short) array_index1);

		if( array_index0 < 0 || array_index0 > 1 ||
		    array_index1 < 0 || array_index1 > 1 )
		{
			throw new ArrayIndexOutOfBoundsException
				("Gsm_Short_Term_Synthesis_Filter: Indexing LARpp "
				 +"incorrectly. Should be >= 0 and <= 1");
		}

		short[] LARpp_j       = S.getLARppIndexed( array_index0 );
		short[] LARpp_j_1     = S.getLARppIndexed( array_index1 );

		Decoding_of_the_coded_Log_Area_Ratios( LARc, LARpp_j );

		Coefficients_0_12(  LARpp_j_1, LARpp_j, LARp );
		LARp_to_rp( LARp );
		Short_term_analysis_filtering( S, LARp, 13, s, 0 );

		Coefficients_13_26( LARpp_j_1, LARpp_j, LARp);
		LARp_to_rp( LARp );
		Short_term_analysis_filtering( S, LARp, 14, s, 13);

		Coefficients_27_39( LARpp_j_1, LARpp_j, LARp);
		LARp_to_rp( LARp );
		Short_term_analysis_filtering( S, LARp, 13, s, 27);

		Coefficients_40_159( LARpp_j, LARp);
		LARp_to_rp( LARp );
		Short_term_analysis_filtering( S, LARp, 120, s, 40);
	}



	public void Gsm_Short_Term_Synthesis_Filter(
		Gsm_State S,
		short[] LARcr,        /* received log area ratios [0..7] IN  */
		short[] wt,           /* received d [0..159]             IN  */
		int[] s               /* signal   s [0..159]            OUT  */
		)
		throws ArrayIndexOutOfBoundsException
	{
		short[] LARp = new short[8];

		int array_index0 = S.getJ();
		int array_index1 = array_index0;

		array_index1 ^= 1;
		S.setJ( (short) array_index1);

		if( array_index0 < 0 || array_index0 > 1 || 
		    array_index1 < 0 || array_index1 > 1 )
		{
			throw new ArrayIndexOutOfBoundsException
				("Gsm_Short_Term_Synthesis_Filter: Indexing LARpp "
				 +"incorrectly. Should be >= 0 and <= 1");
		}

		short[] LARpp_j       = S.getLARppIndexed( array_index0 );
		short[] LARpp_j_1     = S.getLARppIndexed( array_index1 );

		Decoding_of_the_coded_Log_Area_Ratios( LARcr, LARpp_j );

		Coefficients_0_12( LARpp_j_1, LARpp_j, LARp );
		LARp_to_rp( LARp );
		Short_term_synthesis_filtering( S, LARp, 13, wt, s, 0 );

		Coefficients_13_26( LARpp_j_1, LARpp_j, LARp);
		LARp_to_rp( LARp );
		Short_term_synthesis_filtering( S, LARp, 14, wt, s, 13 );

		Coefficients_27_39( LARpp_j_1, LARpp_j, LARp);
		LARp_to_rp( LARp );
		Short_term_synthesis_filtering( S, LARp, 13, wt, s, 27 );

		Coefficients_40_159( LARpp_j, LARp );
		LARp_to_rp( LARp );
		Short_term_synthesis_filtering(S, LARp, 120, wt, s, 40);

		S.setLARppIndexed(array_index0, LARpp_j);
		S.setLARppIndexed(array_index1, LARpp_j_1);
	}



	public static void Decoding_of_the_coded_Log_Area_Ratios(
		short[]  LARc,         /* coded log area ratio [0..7]  IN      */
		short[]  LARpp)        /* out: decoded ..                      */
	{
		short   temp1 = 0;
		int 	index = 0;

		/*  This procedure requires for efficient implementation
		 *  two tables.
		 *
		 *  INVA[1..8] = integer( (32768 * 8) / real_A[1..8])
		 *  MIC[1..8]  = minimum value of the LARc[1..8]
		 */

		/*  Compute the LARpp[1..8]
		 */

		STEP(LARc, LARpp, index++, temp1, (short)0, (short) -32, (short) 13107);
		STEP(LARc, LARpp, index++, temp1, (short)0, (short) -32, (short) 13107);
		STEP(LARc, LARpp, index++, temp1, (short)2048, (short)-16, (short)13107);
		STEP(LARc, LARpp, index++, temp1, (short)-2560, (short)-16, (short)13107);

		STEP(LARc, LARpp, index++, temp1, (short) 94, (short) -8, (short) 19223);
		STEP(LARc, LARpp, index++, temp1, (short) -1792, (short)-8, (short)17476);
		STEP(LARc, LARpp, index++, temp1, (short) -341, (short)-4, (short)31454);
		STEP(LARc, LARpp, index++, temp1, (short) -1144, (short)-4, (short)29708);

		/* NOTE: the addition of *MIC is used to restore
		 *       the sign of *LARc.
		 */
	}



	public static void STEP(short[] LARc, short[] LARpp, int index, 
				short temp1, short B, short MIC, short INVA) 
	{
		temp1    = (short) (Add.GSM_ADD( LARc[index], MIC ) << 10);       
		temp1    = Add.GSM_SUB( temp1, (short) (B << 1) );            
		temp1    = Add.GSM_MULT_R( INVA, temp1 );           
		LARpp[index] = Add.GSM_ADD( temp1, temp1 );
	}



	/* 4.2.9 */
	/* Computation of the quantized reflection coefficients
	 */
    
	/* 4.2.9.1  Interpolation of the LARpp[1..8] to get the LARp[1..8]
	 */

	/*
	 *  Within each frame of 160 analyzed speech samples the short term
	 *  analysis and synthesis filters operate with four different sets of
	 *  coefficients, derived from the previous set of decoded LARs(LARpp(j-1))
	 *  and the actual set of decoded LARs (LARpp(j))
	 *
	 * (Initial value: LARpp(j-1)[1..8] = 0.)
	 */
	public static void Coefficients_0_12(
		short[] LARpp_j_1,
		short[] LARpp_j,
		short[] LARp)
	{
		for (int i = 0; i < 8; i++)
		{
			LARp[i] = Add.GSM_ADD(Add.SASR(LARpp_j_1[i], 2 ), 
					      Add.SASR(LARpp_j[i], 2 ));

			LARp[i] = Add.GSM_ADD(LARp[i], 
					      Add.SASR(LARpp_j_1[i], 1));
		}
	}



	public static void Coefficients_13_26(
		short[] LARpp_j_1,
		short[] LARpp_j,
		short[] LARp)
	{
		for (int i = 0; i < 8; i++)
		{
			LARp[i] = Add.GSM_ADD(Add.SASR(LARpp_j_1[i], 1), 
					      Add.SASR(LARpp_j[i], 1 ));
		}
	}



	public static void Coefficients_27_39(
		short[] LARpp_j_1,
		short[] LARpp_j,
		short[] LARp)
	{
		for (int i = 0; i < 8; i++)
		{
			LARp[i] = Add.GSM_ADD(Add.SASR(LARpp_j_1[i], 2 ), 
					      Add.SASR(LARpp_j[i], 2 ));

			LARp[i] = Add.GSM_ADD(LARp[i], 
					      Add.SASR(LARpp_j[i], 1 ));
		}
	}



	public static void Coefficients_40_159(
		short[] LARpp_j,
		short[] LARp)
	{
		for (int i = 0; i < 8; i++)
			LARp[i] = LARpp_j[i];
	}



	/* 4.2.9.2 */

	/*
	 *  The input of this method is the interpolated LARp[0..7] array.
	 *  The reflection coefficients, rp[i], are used in the analysis
	 *  filter and in the synthesis filter.
	 */
	public static void LARp_to_rp(
		short[] LARp)   /* [0..7] IN/OUT  */
	{
		short           temp;

		for (int i = 0; i < 8; i++)
		{
			if (LARp[i] < 0)
			{
				temp = (short) (LARp[i] == Gsm_Def.MIN_WORD ? 
						Gsm_Def.MAX_WORD : -(LARp[i]));

				LARp[i] = (short) (- ((temp < 11059) ? temp << 1
						      : ((temp < 20070) ? temp + 11059
							 :  Add.GSM_ADD( (short) (temp >> 2), 
									 (short) 26112 ))));
			}
			else
			{
				temp  = LARp[i];
				LARp[i] = (short) ((temp < 11059) ? temp << 1
						   : ((temp < 20070) ? temp + 11059
						      :  Add.GSM_ADD( (short) (temp >> 2), 
								      (short) 26112 )));
			}
		}
	}



	/*
	 *  This procedure computes the short term residual signal d[..] to be fed
	 *  to the RPE-LTP loop from the s[..] signal and from the local rp[..]
	 *  array (quantized reflection coefficients).  As the call of this
	 *  procedure can be done in many ways (see the interpolation of the LAR
	 *  coefficient), it is assumed that the computation begins with index
	 *  k_start (for arrays d[..] and s[..]) and stops with index k_end
	 *  (k_start and k_end are defined in 4.2.9.1).  This procedure also
	 *  needs to keep the array u[0..7] in memory for each call.
	 */
	private void Short_term_analysis_filtering(
		Gsm_State S,
		short[] rp,     /* [0..7]       IN      */
		int     k_n,    /*   k_end - k_start    */
		short[] s,      /* [0..n-1]     IN/OUT  */
		int s_index)
	{
		short[]         u = S.getU();
		short           di = 0, zzz = 0, ui = 0, sav = 0, rpi = 0;

		while (k_n != 0)
		{
			k_n--; 

			di = sav = s[s_index];

			for (int i = 0; i < 8; i++)
			{               /* YYY */
				ui    = u[i];
				rpi   = rp[i];
				u[i]  = sav;

				zzz   = Add.GSM_MULT_R(rpi, di);
				sav   = Add.GSM_ADD(   ui,  zzz);

				zzz   = Add.GSM_MULT_R(rpi, ui);
				di    = Add.GSM_ADD(   di,  zzz );
			}
			s[s_index++] = di;
		}
		S.setU(u);
	}



	public static void Short_term_synthesis_filtering(
		Gsm_State S,
		short[] rrp,    /* [0..7]       IN      */
		int     k,      /* k_end - k_start      */
		short[] wt,     /* [0..k-1]     IN      */
		int[]   sr,     /* [0..k-1]     OUT     */
		int 	wt_sr_index_start)
	{
		short[] v_temp = S.getV();
		short   sri = 0, tmp1 = 0, tmp2 = 0;

		int 	index = wt_sr_index_start;

		while (k != 0)
		{
			k--;
			sri = wt[index];
			for (int i = 7; i >= 0; i--)
			{

				/* sri = GSM_SUB( sri, gsm_mult_r( rrp[i], 
				 * 	 	  v_temp[i] ) );
				 */

				tmp1 = rrp[i];
				tmp2 = v_temp[i];
				tmp2 = (short) ( tmp1 == Gsm_Def.MIN_WORD && 
						 tmp2 == Gsm_Def.MIN_WORD
						 ? Gsm_Def.MAX_WORD
						 : 0x0FFFF & (( (int)tmp1 * (int)tmp2
								+ 16384) >> 15)) ;

				sri  = Add.GSM_SUB( sri, tmp2 );

				/* v[i+1] = GSM_ADD( v_temp[i], 
				 *		     gsm_mult_r( rrp[i], sri ) );
				 */

				tmp1  = (short) ( tmp1 == Gsm_Def.MIN_WORD && 
				                  sri == Gsm_Def.MIN_WORD
						  ? Gsm_Def.MAX_WORD
						  : 0x0FFFF & (( (int)tmp1 * (int)sri
								 + 16384) >> 15)) ;

				v_temp[i+1] = Add.GSM_ADD( v_temp[i], tmp1);
			}
			sr[index++] = v_temp[0] = sri;
		}
		S.setV(v_temp);
	}
}
