/*
 * Static defines.
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

public abstract class Gsm_Def
{
	// TODO: remove
	// Define the magic number for audio files
	public static final int AUDIO_FILE_MAGIC = 0x2e736e64;

	// TODO: remove
	// The encoding key for type: 8-bit ISDN u-law
	public static final int AUDIO_FILE_ENCODING_MULAW_8 = 1;

	public static final short FRAME_SIZE = 33;
	public static final short MAX_FRAME_READ = 1000;

	// TODO: use Short.MIN/MAX_VALUE
	public static final short MIN_WORD 	= -32768;
	public static final short MAX_WORD 	= 32767;

	// TODO: use Integer.MIN/MAX_VALUE
	public static final int MIN_LONGWORD    = -2147483648;
	public static final int MAX_LONGWORD    = 2147483647;

	/*  Table 4.1  Quantization of the Log.-Area Ratios
	 */
	/* i       1      2      3        4      5      6        7       8 */

	public static final short gsm_A[]   = 
	{
		20480, 20480, 20480,  20480,  13964,  15360,   8534,  9036
	};

	public static final short gsm_B[]   = 
	{
		0,     0,  2048,  -2560,     94,  -1792,   -341, -1144
	};

	public static final short gsm_MIC[] = 
	{
		-32,   -32,   -16,    -16,     -8,     -8,     -4,    -4
	};

	public static final short gsm_MAC[] = 
	{
		31,    31,    15,     15,      7,      7,      3,     3
	};

	/*  Table 4.2  Tabulation  of 1/A[1..8]
	 */
	public static final short gsm_INVA[] = 
	{
		13107, 13107,  13107, 13107,  19223, 17476,  31454, 29708
	};

	/*   Table 4.3a  Decision level of the LTP gain quantizer
	 */
	/*  bc   0         1         2          3      */
	public static final short gsm_DLB[] =
	{
		6554,    16384,    26214,     32767
        };


	/*   Table 4.3b   Quantization levels of the LTP gain quantizer
	 */
	/* bc    0          1        2          3      */
	public static final short gsm_QLB[] =
	{
		3277,    11469,    21299,     32767
        };


	/*   Table 4.4   Coefficients of the weighting filter
	 */
	/* i      0      1   2    3   4      5      6     7   8   9    10  */
	public static final short gsm_H[] = 
	{
		-134, -374, 0, 2054, 5741, 8192, 5741, 2054, 0, -374, -134
	};


	/*   Table 4.5   Normalized inverse mantissa used to compute xM/xmax
	 */
	/* i      0      1      2      3      4      5     6        7  */
	public static final short gsm_NRFAC[] = 
	{
		29128, 26215, 23832, 21846, 20165, 18725, 17476, 16384
	};


	/*   Table 4.6   Normalized direct mantissa used to compute xM/xmax
	 */
	/* i      0      1       2      3      4      5      6      7   */
	public static final short gsm_FAC[] = 
	{
		18431, 20479, 22527, 24575, 26623, 28671, 30719, 32767
	};
}
