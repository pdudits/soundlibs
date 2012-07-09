/*
 * Lpc port to Java.
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
 
public class Lpc
{
	private int L_ACF[] = new int[9];



	public void Gsm_LPC_Analysis(
		short[]	so,	/* 0..159 signals       IN/OUT  */
		short[]	LARc)	/* 0..7   LARc's        OUT     */
	{
		Autocorrelation                   (so);
		Reflection_coefficients           (LARc);
		Transformation_to_Log_Area_Ratios (LARc);
		Quantization_and_coding           (LARc);
	}



	private void Autocorrelation(short[] so) /* [0..159]  IN/OUT  */
		throws IllegalArgumentException
	{
		int     i = 0, sp_index = 0;
		short   temp = 0, smax = 0, scalauto = 0;

		/*  Dynamic scaling of the array  s[0..159]
		 */

		/*  Search for the maximum.
		 */
		for (int k = 0; k <= 159; k++)
		{
			temp = Add.GSM_ADD( so[k], (short) 0 );
			if (temp > smax) smax = temp;
		}

		/*  Computation of the scaling factor.
		 */
		if (smax == 0)
		{
			scalauto = 0;
		}
		else
		{
			if ( ! (smax > 0) )
			{
				throw new IllegalArgumentException
					("Autocorrelation: smax = "
					 +smax+" should be > 0.");
			}
			scalauto = (short)
				(4 - Add.gsm_norm((int)(smax << 16))); /* sub(4,..) */
		}

		/*  Scaling of the array s[0...159]
		 */

		if (scalauto > 0)
		{
			if( ! (scalauto <= 4) )
			{
				throw new IllegalArgumentException
					("Autocorrelation: scalauto = "
					 +scalauto+" should be <= 4.");
			}
			switch (scalauto)
			{
			case 1:
				for (int k = 0; k <= 159; k++)
				{
					so[k] = Add.GSM_MULT_R( so[k], (short)16384 );
				}
				break;

			case 2:
				for (int k = 0; k <= 159; k++)
				{
					so[k] = Add.GSM_MULT_R( so[k], 
								(short) (16384 >> 1) );
				}
				break;

			case 3:
				for (int k = 0; k <= 159; k++)
				{
					so[k] = Add.GSM_MULT_R( so[k], 
								(short) (16384 >> 2) );
				}
				break;

			case 4:
				for (int k = 0; k <= 159; k++)
				{
					so[k] = Add.GSM_MULT_R( so[k], 
								(short) (16384 >> 3) );
				}
				break;
			}
		}

		/*  Compute the L_ACF[..].
		 */
		short[]  sp = so;
		short    sl = sp[sp_index];

		// Zero out L_ACF
		int[] temp_arr = {0, 0, 0, 0, 0, 0, 0, 0, 0};
		System.arraycopy(temp_arr , 0, L_ACF, 0, L_ACF.length);	    

		L_ACF[0] += (int)(sl * sp[ (sp_index - 0) ]);
        
		sl = sp[++sp_index];	    
		L_ACF[0] += (int)(sl * sp[ (sp_index - 0) ]);
		L_ACF[1] += (int)(sl * sp[ (sp_index - 1) ]);
	    
		sl = sp[++sp_index];	    
		L_ACF[0] += (int)(sl * sp[ (sp_index - 0) ]);
		L_ACF[1] += (int)(sl * sp[ (sp_index - 1) ]);
		L_ACF[2] += (int)(sl * sp[ (sp_index - 2) ]);
	    
		sl = sp[++sp_index];	    
		L_ACF[0] += (int)(sl * sp[ (sp_index - 0) ]);
		L_ACF[1] += (int)(sl * sp[ (sp_index - 1) ]);
		L_ACF[2] += (int)(sl * sp[ (sp_index - 2) ]);
		L_ACF[3] += (int)(sl * sp[ (sp_index - 3) ]);
	    
		sl = sp[++sp_index];
		L_ACF[0] += (int)(sl * sp[ (sp_index - 0) ]);
		L_ACF[1] += (int)(sl * sp[ (sp_index - 1) ]);
		L_ACF[2] += (int)(sl * sp[ (sp_index - 2) ]);
		L_ACF[3] += (int)(sl * sp[ (sp_index - 3) ]);
		L_ACF[4] += (int)(sl * sp[ (sp_index - 4) ]);
	
		sl = sp[++sp_index];
		L_ACF[0] += (int)(sl * sp[ (sp_index - 0) ]);
		L_ACF[1] += (int)(sl * sp[ (sp_index - 1) ]);
		L_ACF[2] += (int)(sl * sp[ (sp_index - 2) ]);
		L_ACF[3] += (int)(sl * sp[ (sp_index - 3) ]);
		L_ACF[4] += (int)(sl * sp[ (sp_index - 4) ]);
		L_ACF[5] += (int)(sl * sp[ (sp_index - 5) ]);
	
		sl = sp[++sp_index];
		L_ACF[0] += (int)(sl * sp[ (sp_index - 0) ]);
		L_ACF[1] += (int)(sl * sp[ (sp_index - 1) ]);
		L_ACF[2] += (int)(sl * sp[ (sp_index - 2) ]);
		L_ACF[3] += (int)(sl * sp[ (sp_index - 3) ]);
		L_ACF[4] += (int)(sl * sp[ (sp_index - 4) ]);
		L_ACF[5] += (int)(sl * sp[ (sp_index - 5) ]);
		L_ACF[6] += (int)(sl * sp[ (sp_index - 6) ]);
	    
		sl = sp[++sp_index];
		L_ACF[0] += (int)(sl * sp[ (sp_index - 0) ]);
		L_ACF[1] += (int)(sl * sp[ (sp_index - 1) ]);
		L_ACF[2] += (int)(sl * sp[ (sp_index - 2) ]);
		L_ACF[3] += (int)(sl * sp[ (sp_index - 3) ]);
		L_ACF[4] += (int)(sl * sp[ (sp_index - 4) ]);
		L_ACF[5] += (int)(sl * sp[ (sp_index - 5) ]);
		L_ACF[6] += (int)(sl * sp[ (sp_index - 6) ]);
		L_ACF[7] += (int)(sl * sp[ (sp_index - 7) ]);
	
		sl = sp[++sp_index];

		for (i = sp_index; i < 160; ++i)
		{

			sl = sp[i];

			L_ACF[0] += (int)(sl * sp[ (i - 0) ]);
			L_ACF[1] += (int)(sl * sp[ (i - 1) ]);
			L_ACF[2] += (int)(sl * sp[ (i - 2) ]);
			L_ACF[3] += (int)(sl * sp[ (i - 3) ]);
			L_ACF[4] += (int)(sl * sp[ (i - 4) ]);
			L_ACF[5] += (int)(sl * sp[ (i - 5) ]);
			L_ACF[6] += (int)(sl * sp[ (i - 6) ]);
			L_ACF[7] += (int)(sl * sp[ (i - 7) ]);
			L_ACF[8] += (int)(sl * sp[ (i - 8) ]);
		}

		for (int k = 0; k < 9; k++)
		{
			L_ACF[k] <<= 1;
		}

		/*   Rescaling of the array s[0..159]
		 */
		if (scalauto > 0)
		{
			if( ! (scalauto <= 4) )
			{
				throw new IllegalArgumentException
					("Autocorrelation: scalauto = "
					 +scalauto+" should be <= 4.");
			}

			for (int k = 0; k < 160; k++)
			{
				so[k] <<= scalauto;
			}
		}
	}



	private void Reflection_coefficients(short[] r  /* 0...7   OUT */)
		throws IllegalArgumentException
	{
		short   temp = 0;
		int	r_index = 0;

		short[] ACF = new short[9]; /* 0..8 */
		short[] P   = new short[9]; /* 0..8 */
		short[] K   = new short[9]; /* 2..8 */

		/*  Schur recursion with 16 bits arithmetic.
		 */

		if (L_ACF[0] == 0)
		{    /* everything is the same. */
			for (int i = 0; i < 8; i++)
			{
				r[i] = 0;
			}
			return;
		}

		if( L_ACF[0] == 0 )
		{
			throw new IllegalArgumentException
				("Reflection_coefficients: L_ACF[0] = "
				 +L_ACF[0]+" should not = 0.");
		}

		temp = Add.gsm_norm( L_ACF[0] );

		if ( ! (temp >= 0 && temp < 32) )
		{
			throw new IllegalArgumentException
				("Reflection_coefficients: temp = "
				 +temp+" should be >= 0 and < 32.");
		}


		/* ? overflow ? */
		for (int i = 0; i <= 8; i++)
		{
			ACF[i] = Add.SASR( L_ACF[i] << temp, 16 );
		}

		/*   Initialize array P[..] and K[..] for the recursion.
		 */

		System.arraycopy(ACF , 0, K, 0, 7);
	    
		System.arraycopy(ACF , 0, P, 0, 8);

		/*   Compute reflection coefficients
		 */
		for (int n = 1; n <= 8; n++, r_index++)
		{

			temp = P[1];
			temp = Add.GSM_ABS(temp);
			if (P[0] < temp) {
				for (int i = n; i < 8; i++)
				{
					r[i] = 0;
				}
				return;
			}

			r[r_index] = Add.gsm_div( temp, P[0] );

			if ( ! (r[r_index] >= 0)  )
			{
				throw new IllegalArgumentException
					("Reflection_coefficients: r["+r_index+"] = "
					 +r[r_index]+" should be >= 0");
			}

			if (P[1] > 0)
			{
				/* r[n] = sub(0, r[n]) */
				r[r_index] = (short) (-(r[r_index])); 
		        }

			if ( r[r_index] == Gsm_Def.MIN_WORD )
			{
				throw new IllegalArgumentException
					("Reflection_coefficients: r["+r_index+"] = "
					 +r[r_index]+" should not be "+ Gsm_Def.MIN_WORD );
			}
			if (n == 8)
				return;

			/*  Schur recursion
			 */
			temp = Add.GSM_MULT_R( P[1], r[r_index] );
			P[0] = Add.GSM_ADD(    P[0], temp );

			for (int m = 1; m <= 8 - n; m++)
			{
				temp     = Add.GSM_MULT_R( K[ m   ], r[r_index] );
				P[m]     = Add.GSM_ADD(    P[ m+1 ], temp );

				temp     = Add.GSM_MULT_R( P[ m+1 ], r[r_index] );
				K[m]     = Add.GSM_ADD(    K[ m   ], temp );
			}
		}
	}



	/* 4.2.6 */

	/*
	 *  The following scaling for r[..] and LAR[..] has been used:
	 *
	 *  r[..]   = integer( real_r[..]*32768. ); -1 <= real_r < 1.
	 *  LAR[..] = integer( real_LAR[..] * 16384 );
	 *  with -1.625 <= real_LAR <= 1.625
	 */
	private void Transformation_to_Log_Area_Ratios(short[] r /* 0..7 IN/OUT */)
		throws IllegalArgumentException
	{
		short   temp;

		/* Computation of the LAR[0..7] from the r[0..7]
		 */
		for (int i = 0; i < 8; i++)
		{

			temp = r[i];
			temp = Add.GSM_ABS(temp);

			if( ! (temp >= 0) )
			{
				throw new IllegalArgumentException
					("Transformation_to_Log_Area_Ratios: temp = "
					 +temp+" should be >= 0 ");
			}

			if (temp < 22118)
			{
				temp >>= 1;
			}
			else if (temp < 31130)
			{

				if( ! (temp >= 11059) )
				{
			                throw new IllegalArgumentException
						("Transformation_to_Log_Area_Ratios: temp = "
						 +temp+" should be >= 11059 ");
				}

				temp -= 11059;
			}
			else
			{
				if( ! (temp >= 26112) )
				{
			                throw new IllegalArgumentException
						("Transformation_to_Log_Area_Ratios: temp = "
						 +temp+" should be >= 26112 ");
				}

				temp -= 26112;
				temp <<= 2;
			}

			r[i] = (short) (r[i] < 0 ? -temp : temp);

			if( r[i] == Gsm_Def.MIN_WORD )
			{
		                throw new IllegalArgumentException
					("Transformation_to_Log_Area_Ratios: r["+i+"] = "
					 +r[i]+" should not be = " +Gsm_Def.MIN_WORD);
			}
		}
	}



	/* 4.2.7 */

	/*  This procedure needs four tables; the following equations
	 *  give the optimum scaling for the constants:
	 *
	 *  A[0..7] = integer( real_A[0..7] * 1024 )
	 *  B[0..7] = integer( real_B[0..7] *  512 )
	 *  MAC[0..7] = maximum of the LARc[0..7]
	 *  MIC[0..7] = minimum of the LARc[0..7]
	 */
	private void Quantization_and_coding(short[] LAR /* [0..7] IN/OUT  */)
	{
		int     index = 0;

		STEP2(  20480,      0,  31, -32, LAR, index++ );
		STEP2(  20480,      0,  31, -32, LAR, index++ );
		STEP2(  20480,   2048,  15, -16, LAR, index++ );
		STEP2(  20480,  -2560,  15, -16, LAR, index++ );

		STEP2(  13964,     94,   7,  -8, LAR, index++ );
		STEP2(  15360,  -1792,   7,  -8, LAR, index++ );
		STEP2(   8534,   -341,   3,  -4, LAR, index++ );
		STEP2(   9036,  -1144,   3,  -4, LAR, index++ );

	}



	private void STEP2(int A, int B, int MAC, int MIC, 
			   short[] LAR, int index) 
	{
		short temp = 0;

		temp = Add.GSM_MULT( (short)A, LAR[index] );  
		temp = Add.GSM_ADD( temp, (short)B ); 
		temp = Add.GSM_ADD( temp, (short) 256 );
		temp = Add.SASR( temp, 9 );
		LAR[index] = (short) (temp > MAC ? MAC - MIC : 
				      (temp < MIC ? 0 : temp - MIC)); 
	}
}
