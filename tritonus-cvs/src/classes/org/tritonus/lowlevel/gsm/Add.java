/*
 * Mathmatical methods.
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



public class Add
{
	public static short saturate(int x)
	{
		return (short) ((x) < Gsm_Def.MIN_WORD ? Gsm_Def.MIN_WORD : 
		                (x) > Gsm_Def.MAX_WORD ? Gsm_Def.MAX_WORD: (x));
	}
	public static short saturate(long x)
	{
		return (short) ((x) < Gsm_Def.MIN_WORD ? Gsm_Def.MIN_WORD : 
		                (x) > Gsm_Def.MAX_WORD ? Gsm_Def.MAX_WORD: (x));
	}

	public static short SASR(int x, int by)
	{
		return (short) ((x) >> (by));
	}

	public static short GSM_ADD(short a, short b)
	{
		/*
		 *	This converts a and b to int implicitely, because
		 *	'+' is not defined for short.
		 */
		int sum = a + b;
		return saturate(sum);
	}

	public static short GSM_SUB(short a, short b)
	{
		/*
		 *	This converts a and b to int implicitely, because
		 *	'-' is not defined for short.
		 */
		int diff = a - b;
		return saturate(diff);
	}

	public static short GSM_MULT(short a, short b)
	{
		if (a == Gsm_Def.MIN_WORD && b == Gsm_Def.MIN_WORD)
		{
			return Gsm_Def.MAX_WORD; 
		}
		else
		{
			return SASR( ((int)(a)) * ((int)(b)), 15 );
		}
	}

	public static short GSM_MULT_R(short a, short b)
	{
		if (a == Gsm_Def.MIN_WORD && b == Gsm_Def.MIN_WORD)
		{
			return Gsm_Def.MAX_WORD; 
		}
		else
		{
			int prod = (int) (((int)(a)) * ((int)(b)) + 16384);
			prod >>= 15;
			return (short) (prod & 0xFFFF);
		}
	}



	public static short GSM_ABS(short a)
	{
		int b = a < 0 ? (a == Gsm_Def.MIN_WORD ? Gsm_Def.MAX_WORD : -a) : a;
		return ((short)(b)); 
	}

	public static int GSM_L_MULT(short a, short b) 
		throws IllegalArgumentException
	{
		if( a != Short.MIN_VALUE || b != Short.MIN_VALUE )
		{
			throw new IllegalArgumentException(	
				"One of the aruments must equal " + Short.MIN_VALUE);
		}
		return ((int)a * (int)b) << 1;
	}

	public static int GSM_L_ADD(int a, int b)
	{
		if (a <= 0)
		{
			if (b >= 0)
			{
				return a + b;
			}
			else
			{
				long A = (long)-(a + 1) + (long)-(b + 1);
				return A >= Gsm_Def.MAX_LONGWORD ? Gsm_Def.MIN_LONGWORD :-(int)A-2;
			}
		}
		else if (b <= 0)
		{
			return a + b;
		}
		else
		{
			long A = (long)a + (long) 
				b;
			return (int)(A > Gsm_Def.MAX_LONGWORD ? Gsm_Def.MAX_LONGWORD : A);
		}
	}



	/*
	 * the number of left shifts needed to normalize the 32 bit
	 * variable L_var1 for positive values on the interval
	 *
	 * with minimum of
	 * minimum of 1073741824  (01000000000000000000000000000000) and
	 * maximum of 2147483647  (01111111111111111111111111111111)
	 *
	 *
	 * and for negative values on the interval with
	 * minimum of -2147483648 (-10000000000000000000000000000000) and
	 * maximum of -1073741824 ( -1000000000000000000000000000000).
	 *
	 * in order to normalize the result, the following
	 * operation must be done: L_norm_var1 = L_var1 << norm( L_var1 );
	 *
	 * (That's 'ffs', only from the left, not the right..)
	 */
	public static short gsm_norm( int a )
		throws IllegalArgumentException
	{

		if(a == 0)
		{
			throw new IllegalArgumentException
				("gsm_norm: a cannot = 0.");
		}

		if (a < 0)
		{
			if (a <= -1073741824)
			{
				return 0;
			}
			a = ~a;
		}

		return   (short) ( ((a & 0xffff0000) != 0)
				   ? ( ((a & 0xff000000) != 0)
				       ?  -1 + bitoff[ (int)(0xFF & (a >> 24)) ]
				       :   7 + bitoff[ (int)(0xFF & (a >> 16)) ] )
				   : ( ((a & 0xff00) != 0)
				       ?  15 + bitoff[ (int)(0xFF & (a >> 8)) ]
				       :  23 + bitoff[ (int)(0xFF & a) ] ));
	}


	public static short gsm_asl(short a, int n)
	{
		if (n >= 16) return ((short) 0);

		if (n <= -16) 
			if(a<0)
				return (short) -1;
			else
				return (short) -0;

		if (n < 0) return gsm_asr(a, -n);

		return ((short)( a << n ));
	}

	public static short gsm_asr(short a, int n)
	{
		if (n >= 16) 
			if(a<0)
				return (short) -1;
			else
				return (short) -0;
		if (n <= -16) return ((short) 0);
		if (n < 0) return ((short)( a << -n ));

		return ((short)( a >> n ));
	}


	public static short gsm_div(short num, short denum)
		throws IllegalArgumentException
	{

		int             L_num   = num;
		int             L_denum = denum;
		short           div     = 0;
		int             k       = 15;

		/* The parameter num sometimes becomes zero.
		 * Although this is explicitly guarded against in 4.2.5,
		 * we assume that the result should then be zero as well.
		 */

		/* assert(num != 0); */

		if( ! (num >= 0 && denum >= num) )
		{
			throw new IllegalArgumentException
				("gsm_div: num >= 0 && denum >= num");
		}

		if (num == 0)
			return 0;

		while (k != 0)
		{
			k--;
			div   <<= 1;
			L_num <<= 1;

			if (L_num >= L_denum)
			{
				L_num -= L_denum;
				div++;
			}
		}
		return div;
	}


	private static final short bitoff[] =
	{
		8, 7, 6, 6, 5, 5, 5, 5, 4, 4, 4, 4, 4, 4, 4, 4,
		3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3,
		2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2,
		2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2,
		1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
		1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
		1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
		1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
		0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
		0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
		0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
		0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
		0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
		0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
		0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
		0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0
	};

}
	
