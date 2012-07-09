/*
 * Long term port to Java.
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



public class Long_term
{
	public void Gsm_Long_Term_Predictor(
		short[]	d,	/* [0..39]   residual signal    IN      */
    		int	k,	/* d   entry point, which 40            */
		short[]	e,	/* [0..39] add 5 to index       OUT     */
		short[]	dp,
		short[]	dpp,
		int	dp_dpp_point_dp0,
		short[]	Nc,	/* correlation lag              OUT     */
		short[]	bc,	/* gain factor                  OUT     */
		int	Nc_bc_index)
	{
		Calculation_of_the_LTP_parameters(
			d,  k,  dp, dp_dpp_point_dp0, 
			bc, Nc, Nc_bc_index );
		Long_term_analysis_filtering(
			bc[Nc_bc_index], Nc[Nc_bc_index], 
			dp, d, k, dpp, e, dp_dpp_point_dp0 );
	}



	private void Calculation_of_the_LTP_parameters(
		short[] d,            /* [0..39]      IN      */
		int     d_index,
		short[] dp,           /* [-120..-1]   IN      */
		int 	dp_start,
		short[] bc_out,       /*              OUT     */
		short[] Nc_out,       /*              OUT     */
		int 	Nc_bc_index)
		throws IllegalArgumentException {

		int    		lambda = 0;
		short           Nc = 0;
		short[]         wt = new short[40];

		int             L_max = 0, L_power = 0;
		short           R = 0, S = 0, dmax = 0, scal = 0;
		short  		temp = 0;

		/*  Search of the optimum scaling of d[0..39].
		 */
		for (int k = 0; k <= 39; k++)
		{
			temp = d[ k + d_index ];
			temp = Add.GSM_ABS( temp );
			if (temp > dmax)
			{
				dmax = temp;
			}
		}

		temp = 0;

		if (dmax == 0)
		{
			scal = 0;
		}
		else
		{
			if( ! (dmax > 0) )
			{
				throw new IllegalArgumentException
					("Calculation_of_the_LTP_parameters: dmax = "
					 +dmax+" should be > 0.");
			}
			temp = Add.gsm_norm( dmax << 16 );
		}

		if (temp > 6)
		{
			scal = 0;
		}
		else
		{
			scal = (short) (6 - temp); 
		}

		if( ! (scal >= 0) )
		{
			throw new IllegalArgumentException
				("Calculation_of_the_LTP_parameters: scal = "
				 +scal+" should be >= 0.");
		}


		/*  Initialization of a working array wt
		 */

		for (int k = 0; k <= 39; k++)
		{
			wt[k] = Add.SASR( d[ k + d_index ], scal );
		}

		/* Search for the maximum cross-correlation and coding of the LTP lag
		 */
		L_max = 0;
		Nc    = 40;     /* index for the maximum cross-correlation */

		for (lambda = 40; lambda <= 120; lambda++)
		{
			int L_result = 0;
			int step = 1;

			L_result  = STEP(0,  wt, dp, dp_start - lambda )  ; 
			L_result += STEP(1,  wt, dp, step + dp_start - lambda ); step++;
			L_result += STEP(2,  wt, dp, step + dp_start - lambda ); step++;
			L_result += STEP(3,  wt, dp, step + dp_start - lambda ); step++;
			L_result += STEP(4,  wt, dp, step + dp_start - lambda ); step++;
			L_result += STEP(5,  wt, dp, step + dp_start - lambda ); step++;
			L_result += STEP(6,  wt, dp, step + dp_start - lambda ); step++;
			L_result += STEP(7,  wt, dp, step + dp_start - lambda ); step++;
			L_result += STEP(8,  wt, dp, step + dp_start - lambda ); step++;
			L_result += STEP(9,  wt, dp, step + dp_start - lambda ); step++;
			L_result += STEP(10, wt, dp, step + dp_start - lambda ); step++;
			L_result += STEP(11, wt, dp, step + dp_start - lambda ); step++;
			L_result += STEP(12, wt, dp, step + dp_start - lambda ); step++;
			L_result += STEP(13, wt, dp, step + dp_start - lambda ); step++;
			L_result += STEP(14, wt, dp, step + dp_start - lambda ); step++;
			L_result += STEP(15, wt, dp, step + dp_start - lambda ); step++;
			L_result += STEP(16, wt, dp, step + dp_start - lambda ); step++;
			L_result += STEP(17, wt, dp, step + dp_start - lambda ); step++;
			L_result += STEP(18, wt, dp, step + dp_start - lambda ); step++;
			L_result += STEP(19, wt, dp, step + dp_start - lambda ); step++;
			L_result += STEP(20, wt, dp, step + dp_start - lambda ); step++;
			L_result += STEP(21, wt, dp, step + dp_start - lambda ); step++;
			L_result += STEP(22, wt, dp, step + dp_start - lambda ); step++;
			L_result += STEP(23, wt, dp, step + dp_start - lambda ); step++;
			L_result += STEP(24, wt, dp, step + dp_start - lambda ); step++;
			L_result += STEP(25, wt, dp, step + dp_start - lambda ); step++;
			L_result += STEP(26, wt, dp, step + dp_start - lambda ); step++;
			L_result += STEP(27, wt, dp, step + dp_start - lambda ); step++;
			L_result += STEP(28, wt, dp, step + dp_start - lambda ); step++;
			L_result += STEP(29, wt, dp, step + dp_start - lambda ); step++;
			L_result += STEP(30, wt, dp, step + dp_start - lambda ); step++;
			L_result += STEP(31, wt, dp, step + dp_start - lambda ); step++;
			L_result += STEP(32, wt, dp, step + dp_start - lambda ); step++;
			L_result += STEP(33, wt, dp, step + dp_start - lambda ); step++;
			L_result += STEP(34, wt, dp, step + dp_start - lambda ); step++;
			L_result += STEP(35, wt, dp, step + dp_start - lambda ); step++;
			L_result += STEP(36, wt, dp, step + dp_start - lambda ); step++;
			L_result += STEP(37, wt, dp, step + dp_start - lambda ); step++;
			L_result += STEP(38, wt, dp, step + dp_start - lambda ); step++;
			L_result += STEP(39, wt, dp, step + dp_start - lambda ); step++;

			if (L_result > L_max)
			{
				Nc    = (short) lambda;
				L_max = L_result;
			}
		}

		Nc_out[Nc_bc_index] = Nc;

		L_max <<= 1;

		/*  Rescaling of L_max
		 */
		if( ! (scal <= 100 && scal >=  -100) )
		{
			throw new IllegalArgumentException
				("Calculation_of_the_LTP_parameters: scal = "
				 +scal+" should be >= -100 and <= 100.");
		}

		L_max = L_max >> (6 - scal);    /* sub(6, scal) */

		if( ! (Nc <= 120 && Nc >= 40) )
		{
			throw new IllegalArgumentException
				("Calculation_of_the_LTP_parameters: Nc = "
				 +Nc+" should be >= 40 and <= 120.");
		}


		/*   Compute the power of the reconstructed short term residual
		 *   signal dp[..]
		 */
		L_power = 0;
		for (int k = 0; k <= 39; k++)
		{
			int L_temp;

			L_temp   = Add.SASR( dp[k - Nc + dp_start], 3 );
			L_power += L_temp * L_temp;
		}
		L_power <<= 1;  /* from L_MULT */

		/*  Normalization of L_max and L_power
		 */

		if (L_max <= 0)
		{
			bc_out[Nc_bc_index] = 0;
			return;
		}
		if (L_max >= L_power)
		{
			bc_out[Nc_bc_index] = 3;
			return;
		}

		temp = Add.gsm_norm( L_power );

		R = Add.SASR( L_max   << temp, 16 );
		S = Add.SASR( L_power << temp, 16 );

		/*  Coding of the LTP gain
		 */

		/*  Table 4.3a must be used to obtain the level DLB[i] for the
		 *  quantization of the LTP gain b to get the coded version bc.
		 */
		for (int bc = 0; bc <= 2; bc++)
		{
			if (R <= Add.GSM_MULT(S, Gsm_Def.gsm_DLB[bc]))
			{
				break;
			}
			bc_out[Nc_bc_index] = (short) bc;
		}
	}



	private int STEP(int k, short[] wt, short[] dp, int dp_i)
	{
		return( wt[k] * dp[ dp_i ] );
	}
    


	/*
	 *  In this part, we have to decode the bc parameter to compute
	 *  the samples of the estimate dpp[0..39].  The decoding of bc 
	 *  needs the use of table 4.3b.  The long term residual signal 
	 *  e[0..39] is then calculated to be fed to the  RPE  encoding 
	 *  section.  
	 */
	static void Long_term_analysis_filtering(
		short	bc,	/*                                      IN  */
		short	Nc,	/*                                      IN  */
		short[]	dp,	/* previous d   [-120..-1]              IN  */
		short[]	d,	/* d            [0..39]                 IN  */
		int	d_index,
		short[]	dpp,	/* estimate     [0..39]                 OUT */
		short[]	e,	/* long term res. signal [0..39]        OUT */
		int	dp_dpp_index)
	{
		short BP = 0;

		switch (bc)
		{
		case 0: 
			BP = (short) 3277;
			for (int k = 0; k <= 39; k++)
			{
				dpp[k + dp_dpp_index]  = Add.GSM_MULT_R( BP, 
									 dp[k - Nc + dp_dpp_index]);  
				e[k + 5]    = Add.GSM_SUB( d[k + d_index], 
							   dpp[k + dp_dpp_index] );     
			}
			break;

		case 1: 
			BP = (short) 11469;
			for (int k = 0; k <= 39; k++)
			{
				dpp[k + dp_dpp_index]  = Add.GSM_MULT_R( BP, 
									 dp[k - Nc + dp_dpp_index]);  
				e[k + 5]    = Add.GSM_SUB( d[k + d_index],
							   dpp[k + dp_dpp_index] );     
			}
			break;

		case 2: 
			BP = (short) 21299;
			for (int k = 0; k <= 39; k++)
			{
				dpp[k + dp_dpp_index]  = Add.GSM_MULT_R( BP, 
									 dp[k - Nc + dp_dpp_index]);  
				e[k + 5]    = Add.GSM_SUB( d[k + d_index],
							   dpp[k + dp_dpp_index] );     
			}
			break;

		case 3: 
			BP = (short) 32767;
			for (int k = 0; k <= 39; k++)
			{
				dpp[k + dp_dpp_index]  = Add.GSM_MULT_R( BP, 
									 dp[k - Nc + dp_dpp_index]);  
				e[k + 5]    = Add.GSM_SUB( d[k + d_index], 
							   dpp[k + dp_dpp_index] );     
			}
			break;
		}
	}



	/*
	 *  This procedure uses the bcr and Ncr parameter to realize the
	 *  long term synthesis filtering.  The decoding of bcr needs
	 *  table 4.3b.
	 */
	public void Gsm_Long_Term_Synthesis_Filtering(
		Gsm_State	S,
		short		Ncr,
		short		bcr,
		short[]		erp,	/* [0..39]                IN  */
		int		dp0_index_start_drp
		/* [-120..-1] IN, [0..40] OUT */
		/* drp is a pointer into the Gsm_State 
		 * dp0 short array. */
		)
		throws IllegalArgumentException
	{
		short     brp, drpp, Nr;
		short[]   drp = S.getDp0(); 

		/*  Check the limits of Nr.
		 */
		Nr = Ncr < 40 || Ncr > 120 ? S.getNrp() : Ncr;

		S.setNrp(Nr);

		if( ! (Nr >= 40 && Nr <= 120) )
		{
			throw new IllegalArgumentException
				("Gsm_Long_Term_Synthesis_Filtering Nr = "
				 +Nr+" is out of range. Should be >= 40 and <= 120");
		}

		/*  Decoding of the LTP gain bcr
		 */
		brp = Gsm_Def.gsm_QLB[ bcr ];

		/*  Computation of the reconstructed short term residual
		 *  signal drp[0..39]
		 */
		if(brp == Gsm_Def.MIN_WORD)
		{
			throw new IllegalArgumentException
				("Gsm_Long_Term_Synthesis_Filtering brp = "
				 +brp+" is out of range. Should be = " +Gsm_Def.MIN_WORD);
		}

		for (int k = 0; k <= 39; k++)
		{
			drpp = Add.GSM_MULT_R( brp, drp[ k - Nr + dp0_index_start_drp ] );
			drp[ k + dp0_index_start_drp ] = Add.GSM_ADD( erp[k], drpp );
		}

		/*
		 *  Update of the reconstructed short term residual signal
		 *  drp[ -1..-120 ]
		 */
		System.arraycopy( drp, (dp0_index_start_drp - 80), 
				  drp, (dp0_index_start_drp - 120), 120 );
        
		S.setDp0(drp);
	}
}
