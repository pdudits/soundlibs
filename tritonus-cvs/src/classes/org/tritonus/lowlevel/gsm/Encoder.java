/*
 * Encoder is the base class for all GSM encoding.
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

import java.io.*;


public class Encoder
{
	/* Every Encoder has a state through completion */
	private Gsm_State	g_s = new Gsm_State();
	private Long_term	lg_term_Obj = new Long_term();
	private Lpc		lpc_Obj = new Lpc();
	private Rpe		rpe_Obj = new Rpe();
	private Short_term	sh_term_Obj = new Short_term();

	/* [0..7] LAR coefficients              OUT     */
	private short		LARc[]  = new short[8]; 
	/* [0..3] LTP lag 		            OUT     */
	private short		Nc[] = new short[4]; 	
	/* [0..3] coded LTP gain                OUT     */
	private short		Mc[] = new short[4];
	/* [0..3] RPE grid selection            OUT     */
	private short		bc[]    = new short[4];
	/* [0..3] Coded maximum amplitude       OUT     */
	private short		xmaxc[] = new short[4];
	/* [13*4] normalized RPE samples        OUT     */
	private short		xmc[]   = new short[13*4];

	/* Reads 160 bytes */
	private int[]		input_signal = new int[160];  /* [0..159]  OUT */
	/* Writes 33 bytes */
	private byte[]		frame = new byte[Gsm_Def.FRAME_SIZE];

	/**
	 * Encoder class constructor. 
	 */
	public Encoder()
	{
	}



	/** 
	 * Remove the header info from the stream and verifies the file type. 
	 * As defined by the NeXT/Sun audio file format U-law (.au).
	 * For more info see the README file. <br><br>
	 * Note: Most of this info is not needed to reproduce the sound file
	 * after encoding. All that is needed is the magic number and the sampling rate
	 * to reproduce the sound file during decoding.
	 * 
	 * @param in Strip the header from a Sun/Next formated sound stream.
	 */
	public static void stripAUHeader(InputStream in) 
		throws Exception {
		DataInputStream input = new DataInputStream(in );
	
		/* Just read these bits from the stream and do nothing with them */
		int magic = input.readInt();  	/* magic number SND_MAGIC ((int)0x2e736e64),
						 * which equals ".snd".)
						 */
		input.readInt();			/* offset or pointer to the data */
		input.readInt();			/* number of bytes of data */
		int dataFormat = input.readInt();	/* the data format code */
		int sampleRate = input.readInt();	/* the sampling rate = ~8000 samples/sec. */
		input.readInt();			/* the number of channels */
		input.readChar();			/* optional text information - 4 chars */
		input.readChar();
		input.readChar();
		input.readChar();

	    	if( magic != 0x2E736E64)        // ".snd" in ASCII 
		{
		    	// throw new GsmException("AuFile wrong Magic Number");
		}
	    	else if( dataFormat != 1 )      // 8-Bit mu-Law
		{
			// throw new GsmException("AuFile not 8-bit Mu-Law");
		}
	    	else if( sampleRate != 8000)    // 8kHz
		{
		    	// throw new GsmException("AuFile not 8kHz");
		}
	}

	/**
	 * Encode the specified file.
	 * <br>This method calls the <code>stripAUHeader</code> method for you.<br>
	 * stripAUHeader will verify file type.      
	 *
	 * @param input_file The name of the file to encode.
	 * @param output_file The name of the GSM encoded file.
	 */
	public void encode(String input_file, String output_file) 
		throws Exception {

		File arg1 = new File(input_file);
	    	if(!arg1.exists() || !arg1.isFile() || !arg1.canRead())
		{
			throw new IOException("File : " + 	
					      input_file + "\ndoes not exist or cannot be read.");
		}
	
		FileInputStream from = null;
		FileOutputStream to  = null;
		try
		{
			from = new FileInputStream(input_file);
			to = new FileOutputStream(output_file);

	    		// Remove the header. It gets mangled by the encoding.
	    		stripAUHeader(from);

			// Read bytes till EOF.
			while(ulaw_input(from) > 0)
			{
				//System.out.println("Entering Native method.");
				gsm_encode();

				// Need to do some error check here. Update ulaw_output.
				ulaw_output(to);           // Write bytes.
			}

		}
		catch (Exception e)
		{ 	
			throw new Exception("Encoder: " + e.getMessage());
		}
		finally
		{
			if (from != null)
			{
				try
				{
					from.close();
				}
				catch (IOException e)
				{
					throw new IOException("Encoder: " + e.getMessage());
				}
			}
			if (to != null)
			{
				try
				{
					to.close();
				}
				catch (IOException e)
				{
					throw new IOException("Encoder: " + e.getMessage());
				}
			}
		}
	}



	/**
	 * Encode the specified InputStream.     
	 *
	 * @param input The stream to encode.
	 * @param output_file The name of the GSM encoded file.
	 */
	public void encode(InputStream input, String output_file) 
		throws IOException
	{
		FileOutputStream to  = null;
		try
		{
			to = new FileOutputStream(output_file);

			// Read bytes till EOF.
			while(ulaw_input(input) > 0)
			{
				gsm_encode();

				// Need to do some error check here. Update ulaw_output.
				ulaw_output(to);           // Write bytes.
			}

		}
		catch (IOException e)
		{
			throw new IOException( "Encoder: " + e.getMessage());
		}
		finally
		{
			if (to != null)
			{
				try
				{
					to.close();
				}
				catch (IOException e)
				{
					throw new IOException("Encoder: " + e.getMessage());
				}
			}
		}
	}


	/**	Encodes a block of data.
	 *
	 *	@param asBuffer	an 160-element array with the data to encode
	 *			int PCM 16 bit format.
	 *
	 *	@param abFrame	the encoded GSM frame (33 bytes).
	 */
	public void encode(short[] asBuffer, byte[] abFrame)
	{
		for (int i = 0; i < 160; i++)
		{
			input_signal[i] = asBuffer[i];
		}
		gsm_encode();
		System.arraycopy(frame, 0, abFrame, 0, frame.length);
	}



	/**
	 * Read 160 bytes from a U-law stream and set up the input_signal array. 
	 */
	private int ulaw_input(InputStream in) 
		throws IOException
	{
		int c = 0;
		int i = 0;

		for (i = 0; i < input_signal.length && ((c = in.read()) != -1); i++)
		{
			if (c < 0)
			{
				throw new IOException("Encoder ulaw_input: Corrupt InputStream.");
			}
			else
			{
				input_signal[i] = u2s[c];
			}
		}
		return (i);
	}



	private void gsm_encode()
	{ 
		int index = 0;
	
		Gsm_Coder_java();

		frame[index++]  = (byte) (((0xD) << 4)			/* 1 */
					  | ((LARc[0] >> 2) & 0xF));
		frame[index++]  = (byte) (((LARc[0] & 0x3) << 6)	/* 2 */
					  | (LARc[1] & 0x3F));
		frame[index++]  = (byte) (((LARc[2] & 0x1F) << 3)	/* 3 */
					  | ((LARc[3] >> 2) & 0x7));
		frame[index++]  = (byte) (((LARc[3] & 0x3) << 6)	/* 4 */
					  | ((LARc[4] & 0xF) << 2)
					  | ((LARc[5] >> 2) & 0x3));
		frame[index++]  = (byte) (((LARc[5] & 0x3) << 6)	/* 5 */
					  | ((LARc[6] & 0x7) << 3)
					  | (LARc[7] & 0x7));
		frame[index++]  = (byte) (((Nc[0] & 0x7F) << 1)		/* 6 */
					  | ((bc[0] >> 1) & 0x1));
		frame[index++]  = (byte) (((bc[0] & 0x1) << 7)		/* 7 */
					  | ((Mc[0] & 0x3) << 5)
					  | ((xmaxc[0] >> 1) & 0x1F));
		frame[index++]  = (byte) (((xmaxc[0] & 0x1) << 7)	/* 8 */
					  | ((xmc[0] & 0x7) << 4)
					  | ((xmc[1] & 0x7) << 1)
					  | ((xmc[2] >> 2) & 0x1));
		frame[index++]  = (byte) (((xmc[2] & 0x3) << 6)		/* 9 */
					  | ((xmc[3] & 0x7) << 3)
					  | (xmc[4] & 0x7));
		frame[index++]  = (byte) (((xmc[5] & 0x7) << 5)		/* 10 */
					  | ((xmc[6] & 0x7) << 2)
					  | ((xmc[7] >> 1) & 0x3));
		frame[index++]  = (byte) (((xmc[7] & 0x1) << 7)		/* 11 */
					  | ((xmc[8] & 0x7) << 4)
					  | ((xmc[9] & 0x7) << 1)
					  | ((xmc[10] >> 2) & 0x1));
		frame[index++]  = (byte) ((((xmc[10] & 0x3) << 6)	/* 12 */
					   | ((xmc[11] & 0x7) << 3)
					   | (xmc[12] & 0x7)));
		frame[index++]  = (byte) (((Nc[1] & 0x7F) << 1)		/* 13 */
					  | ((bc[1] >> 1) & 0x1));
		frame[index++]  = (byte) (((bc[1] & 0x1) << 7)		/* 14 */
					  | ((Mc[1] & 0x3) << 5)
					  | ((xmaxc[1] >> 1) & 0x1F));
		frame[index++]  = (byte) (((xmaxc[1] & 0x1) << 7)	/* 15 */
					  | ((xmc[13] & 0x7) << 4)
					  | ((xmc[14] & 0x7) << 1)
					  | ((xmc[15] >> 2) & 0x1));
		frame[index++]  = (byte) (((xmc[15] & 0x3) << 6)
					  | ((xmc[16] & 0x7) << 3)
					  | (xmc[17] & 0x7));
		frame[index++]  = (byte) (((xmc[18] & 0x7) << 5)
					  | ((xmc[19] & 0x7) << 2)
					  | ((xmc[20] >> 1) & 0x3));
		frame[index++]  = (byte) (((xmc[20] & 0x1) << 7)
					  | ((xmc[21] & 0x7) << 4)
					  | ((xmc[22] & 0x7) << 1)
					  | ((xmc[23] >> 2) & 0x1));
		frame[index++]  = (byte) (((xmc[23] & 0x3) << 6)
					  | ((xmc[24] & 0x7) << 3)
					  | (xmc[25] & 0x7));
		frame[index++]  = (byte) (((Nc[2] & 0x7F) << 1)		/* 20 */
					  | ((bc[2] >> 1) & 0x1));
		frame[index++]  = (byte) (((bc[2] & 0x1) << 7)
					  | ((Mc[2] & 0x3) << 5)
					  | ((xmaxc[2] >> 1) & 0x1F));
		frame[index++]  = (byte) (((xmaxc[2] & 0x1) << 7)
					  | ((xmc[26] & 0x7) << 4)
					  | ((xmc[27] & 0x7) << 1)
					  | ((xmc[28] >> 2) & 0x1));
		frame[index++]  = (byte) (((xmc[28] & 0x3) << 6)
					  | ((xmc[29] & 0x7) << 3)
					  | (xmc[30] & 0x7));
		frame[index++]  = (byte) (((xmc[31] & 0x7) << 5)
					  | ((xmc[32] & 0x7) << 2)
					  | ((xmc[33] >> 1) & 0x3));
		frame[index++]  = (byte) (((xmc[33] & 0x1) << 7)
					  | ((xmc[34] & 0x7) << 4)
					  | ((xmc[35] & 0x7) << 1)
					  | ((xmc[36] >> 2) & 0x1));
		frame[index++]  = (byte) (((xmc[36] & 0x3) << 6)
					  | ((xmc[37] & 0x7) << 3)
					  | (xmc[38] & 0x7));
		frame[index++]  = (byte) (((Nc[3] & 0x7F) << 1)
					  | ((bc[3] >> 1) & 0x1));
		frame[index++]  = (byte) (((bc[3] & 0x1) << 7)
					  | ((Mc[3] & 0x3) << 5)
					  | ((xmaxc[3] >> 1) & 0x1F));
		frame[index++]  = (byte) (((xmaxc[3] & 0x1) << 7)
					  | ((xmc[39] & 0x7) << 4)
					  | ((xmc[40] & 0x7) << 1)
					  | ((xmc[41] >> 2) & 0x1));
		frame[index++]  = (byte) (((xmc[41] & 0x3) << 6)	/* 30 */
					  | ((xmc[42] & 0x7) << 3)
					  | (xmc[43] & 0x7));
		frame[index++]  = (byte) (((xmc[44] & 0x7) << 5)	/* 31 */
					  | ((xmc[45] & 0x7) << 2)
					  | ((xmc[46] >> 1) & 0x3));
		frame[index++]  = (byte) (((xmc[46] & 0x1) << 7)	/* 32 */
					  | ((xmc[47] & 0x7) << 4)
					  | ((xmc[48] & 0x7) << 1)
					  | ((xmc[49] >> 2) & 0x1));
		frame[index++]  = (byte) (((xmc[49] & 0x3) << 6)	/* 33 */
					  | ((xmc[50] & 0x7) << 3)
					  | (xmc[51] & 0x7)); 
	}



	private void Gsm_Coder_java()
	{
		int     xmc_point = 0;
		int	Nc_bc_index = 0;
		int	xmaxc_Mc_index = 0;
		int	dp_dpp_point_dp0 = 120;

		//short[] ep = new short[40];
		short[] e  = new short[50];
		short[] so = new short[160];

		Gsm_Preprocess(so);
		lpc_Obj.Gsm_LPC_Analysis(so, LARc);
		sh_term_Obj.Gsm_Short_Term_Analysis_Filter(g_s, LARc, so);

		short[] dp  = g_s.getDp0();
		short[] dpp = dp;

		for( int k = 0; k <= 3; k++, xmc_point += 13 )
		{
			lg_term_Obj.Gsm_Long_Term_Predictor(
				so,      /* d    [0..39] IN    */
				k*40,	 /* so   entry point   */
				e,       /* e+5  [0..39] OUT   */
				dp,      /* Referance to Gsm_State dp0 */
				dpp,     /* Referance to Gsm_State dp0 */
				dp_dpp_point_dp0, /* Where to start the dp0 ref */
				Nc,      /* [0..3] coded LTP gain   	 OUT */
				bc,      /* [0..3] RPE grid selection    OUT */
				Nc_bc_index++ /* The current referance point for Nc & bc */
				);

			rpe_Obj.Gsm_RPE_Encoding 
				(e,      /* e + 5 ][0..39][ IN/OUT */
				 xmaxc,  /* [0..3] Coded maximum amplitude  OUT     */
				 Mc,     /* [0..3] coded LTP gain           OUT     */
				 xmaxc_Mc_index++, /* The current referance point   */
				 xmc,    /* [13*4] normalized RPE samples   OUT     */
				 xmc_point /* The current referance point for xmc   */ );

			for( int i = 0; i <= 39; i++ )
			{
				dp[ i + dp_dpp_point_dp0 ] = Add.GSM_ADD( e[5 + i], 
									  dpp[i + dp_dpp_point_dp0] );
			}

			g_s.setDp0(dp);
			dp_dpp_point_dp0 += 40;
		}
        
		for( int i = 0; i < 120; i++ )
		{
			g_s.setDp0Indexed(i, g_s.getDp0Indexed( (160 + i) ));
		}
	}



	private void Gsm_Preprocess(short[] so)	/* [0..159] 	IN/OUT	*/
		throws IllegalArgumentException
	{
		int index = 0, so_index = 0;

		short z1 = g_s.getZ1();
		int L_z2 = g_s.getL_z2();
		int   mp = g_s.getMp();

		short s1 = 0, msp = 0, lsp = 0, SO = 0;
		int   L_s2 = 0, L_temp = 0;
		int   k = 160;

		while (k != 0)
		{
			k--;

			/*  4.2.1   Downscaling of the input signal
			 */
			SO = (short)(Add.SASR((short)input_signal[index++], (short)3) << 2);

			if ( ! (SO >= -0x4000) )
			{ 	/* downscaled by     */
				throw new IllegalArgumentException
					("Gsm_Preprocess: SO = "
					 +SO+" is out of range. Sould be >= -0x4000 ");
			}

			if ( ! (SO <=  0x3FFC) )
			{	/* previous routine. */
				throw new IllegalArgumentException
					("Gsm_Preprocess: SO = "
					 +SO+" is out of range. Sould be <= 0x3FFC ");
			}


			/*  4.2.2   Offset compensation
			 * 
			 *  This part implements a high-pass filter and requires extended
			 *  arithmetic precision for the recursive part of this filter.
			 *  The input of this procedure is the array so[0...159] and the
			 *  output the array sof[ 0...159 ].
			 */

			/*   Compute the non-recursive part
			 */
			s1 = (short) (SO - z1);	/* s1 = gsm_sub( *so, z1 ); */
			z1 = SO;

			if(s1 == Gsm_Def.MIN_WORD)
			{
				throw new IllegalArgumentException
					("Gsm_Preprocess: s1 = "
					 +s1+" is out of range. ");
			}

			/*   Compute the recursive part
			 */
			L_s2 = s1;
			L_s2 <<= 15;

			/*   Execution of a 31 bv 16 bits multiplication
			 */

			msp = Add.SASR( L_z2, 15 );

			/* gsm_L_sub(L_z2,(msp<<15)); */
			lsp = (short) (L_z2 - ((int) (msp << 15))); 

			L_s2  += Add.GSM_MULT_R( lsp, (short) 32735 );
			L_temp = (int)msp * 32735; /* GSM_L_MULT(msp,32735) >> 1;*/
			L_z2   = Add.GSM_L_ADD( L_temp, L_s2 );

			/*    Compute sof[k] with rounding
			 */
			L_temp = Add.GSM_L_ADD( L_z2, 16384 );

			/*   4.2.3  Preemphasis
			 */
			msp   = Add.GSM_MULT_R( (short) mp, (short) -28180 );
			mp    = Add.SASR( L_temp, 15 );
			so[so_index++] = Add.GSM_ADD( (short) mp, msp );
		}
		g_s.setZ1(z1);
		g_s.setL_z2(L_z2);
		g_s.setMp(mp);
	}



	/* Write the encoded bytes to the stream. */
	private void ulaw_output(FileOutputStream out)
		throws IOException
	{
		int i = 0;
		for (i = 0; i < frame.length; i++)
		{
			out.write(frame[i]);
		}
	}



	/**
	 * Used for debugging. 
	 *
	 * @param state The Gsm_State object to be viewed.
	 */
	private void dump_Gsm_State(Gsm_State state)
	{
		state.dump_Gsm_State();
	}



	/*
	 * This is the encoding matrix. 
	 *
	 * Java does not have an unsigned short, ie. 16 bits, so
	 * I will use the upper 16 bits of the integer. This wastes
	 * a little memory although I do not think it will cause a
	 * problem.
	 */
	private static final int u2s[] =
	{
		33280, 34308, 35336, 36364, 37393, 38421, 39449, 40477,
		41505, 42534, 43562, 44590, 45618, 46647, 47675, 48703,
		49474, 49988, 50503, 51017, 51531, 52045, 52559, 53073,
		53587, 54101, 54616, 55130, 55644, 56158, 56672, 57186,
		57572, 57829, 58086, 58343, 58600, 58857, 59114, 59371,
		59628, 59885, 60142, 60399, 60656, 60913, 61171, 61428,
		61620, 61749, 61877, 62006, 62134, 62263, 62392, 62520,
		62649, 62777, 62906, 63034, 63163, 63291, 63420, 63548,
		63645, 63709, 63773, 63838, 63902, 63966, 64030, 64095,
		64159, 64223, 64287, 64352, 64416, 64480, 64544, 64609,
		64657, 64689, 64721, 64753, 64785, 64818, 64850, 64882,
		64914, 64946, 64978, 65010, 65042, 65075, 65107, 65139,
		65163, 65179, 65195, 65211, 65227, 65243, 65259, 65275,
		65291, 65308, 65324, 65340, 65356, 65372, 65388, 65404,
		65416, 65424, 65432, 65440, 65448, 65456, 65464, 65472,
		65480, 65488, 65496, 65504, 65512, 65520, 65528,     0,
		32256, 31228, 30200, 29172, 28143, 27115, 26087, 25059,
		24031, 23002, 21974, 20946, 19918, 18889, 17861, 16833,
		16062, 15548, 15033, 14519, 14005, 13491, 12977, 12463,
		11949, 11435, 10920, 10406,  9892,  9378,  8864,  8350,
		7964,  7707,  7450,  7193,  6936,  6679,  6422,  6165,
		5908,  5651,  5394,  5137,  4880,  4623,  4365,  4108,
		3916,  3787,  3659,  3530,  3402,  3273,  3144,  3016,
		2887,  2759,  2630,  2502,  2373,  2245,  2116,  1988,
		1891,  1827,  1763,  1698,  1634,  1570,  1506,  1441,
		1377,  1313,  1249,  1184,  1120,  1056,   992,   927,
		879,   847,   815,   783,   751,   718,   686,   654,
		622,   590,   558,   526,   494,   461,   429,   397,
		373,   357,   341,   325,   309,   293,   277,   261,
		245,   228,   212,   196,   180,   164,   148,   132,
		120,   112,   104,    96,    88,    80,    72,    64,
		56,    48,    40,    32,    24,    16,    8,      0
	};
}
