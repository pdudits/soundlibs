/*
 *	VorbisDecoder.java
 */

import java.io.IOException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

// import java.util.Random;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.tritonus.lowlevel.ogg.Packet;
import org.tritonus.lowlevel.ogg.Page;
import org.tritonus.lowlevel.ogg.StreamState;
import org.tritonus.lowlevel.ogg.SyncState;

import org.tritonus.lowlevel.vorbis.Block;
import org.tritonus.lowlevel.vorbis.Comment;
import org.tritonus.lowlevel.vorbis.DspState;
import org.tritonus.lowlevel.vorbis.Info;



public class VorbisDecoder
{
	private static int	convsize = 4096;



	public static void main(String[] args)
		throws IOException
	{
		SyncState	oy; /* sync and verify incoming physical bitstream */
		StreamState	os; /* take physical pages, weld into a logical
				       stream of packets */
		Page         	og; /* one Ogg bitstream page.  Vorbis packets are inside */
		Packet       	op; /* one raw packet of data for decode */

		Info      	vi; /* struct that stores all the static vorbis bitstream
				       settings */
		Comment   	vc; /* struct that stores all the bitstream user comments */
		DspState 	vd; /* central working state for the packet->PCM decoder */
		Block     	vb; /* local working space for packet->PCM decode */

		oy = new SyncState();
		os = new StreamState();
		og = new Page();
		op = new Packet();

		vi = new Info();
		vc = new Comment();
		vd = new DspState();
		vb = new Block ();

		int[]	convbuffer = new int[convsize];
		byte[]	buffer;
		int	bytes;


		File		inputFile = new File(args[0]);
		File		outputFile = new File(args[1]);
		InputStream	inputStream = new FileInputStream(inputFile);
		OutputStream	outputStream = new FileOutputStream(outputFile);

		buffer = new byte[4096];

		/********** Decode setup ************/

		oy.init(); /* Now we can read pages */

		while (true)
		{ /* we repeat if the bitstream is chained */
			boolean	eos = false;
			int	i;

			/* grab some data at the head of the stream.  We want the first page
			   (which is guaranteed to be small and only contain the Vorbis
			   stream initial header) We need the first page to get the stream
			   serialno. */

			/* submit a 4k block to libvorbis' Ogg layer */
			bytes = inputStream.read(buffer);
			oy.write(buffer, bytes);

			/* Get the first page. */
			if (oy.pageOut(og) != 1)
			{
				/* have we simply run out of data?  If so, we're done. */
				if (bytes < 4096)
				{
					break;
				}
      
				/* error case.  Must not be Vorbis data */
				System.err.print("Input does not appear to be an Ogg bitstream.\n");
				System.exit(1);
			}
  
			/* Get the serial number and set up the rest of decode. */
			/* serialno first; use it to set up a logical stream */
			os.init(og.getSerialNo());

			/* extract the initial header from the first page and verify that the
			   Ogg bitstream is in fact Vorbis data */

			/* I handle the initial header first instead of just having the code
			   read all three Vorbis headers at once because reading the initial
			   header is an easy way to identify a Vorbis bitstream and it's
			   useful to see that functionality seperated out. */

			vi.init();
			vc.init();
			if (os.pageIn(og) < 0)
			{
				/* error; stream version mismatch perhaps */
				System.err.print("Error reading first page of Ogg bitstream data.\n");
				System.exit(1);
			}

			if(os.packetOut(op) != 1)
			{
				/* no page? must not be vorbis */
				System.err.print("Error reading initial header packet.\n");
				System.exit(1);
			}

			if(vi.headerIn(vc, op) < 0)
			{
				/* error case; not a vorbis header */
				System.err.print("This Ogg bitstream does not contain Vorbis audio data.\n");
				System.exit(1);
			}

			/* At this point, we're sure we're Vorbis.  We've set up the logical
			   (Ogg) bitstream decoder.  Get the comment and codebook headers and
			   set up the Vorbis decoder */

			/* The next two packets in order are the comment and codebook headers.
			   They're likely large and may span multiple pages.  Thus we reead
			   and submit data until we get our two pacakets, watching that no
			   pages are missing.  If a page is missing, error out; losing a
			   header page is the only place where missing data is fatal. */

			i = 0;
			while (i < 2)
			{
				while (i < 2)
				{
					int result = oy.pageOut(og);
					if (result == 0)
					{
						break; /* Need more data */
					}
					/* Don't complain about missing or corrupt data yet.  We'll
					   catch it at the packet output phase */
					if (result == 1)
					{
						os.pageIn(og); /* we can ignore any errors here
									       as they'll also become apparent
									       at packetout */
						while (i < 2)
						{
							result = os.packetOut(op);
							if (result == 0)
							{
								break;
							}
							if (result < 0)
							{
								/* Uh oh; data at some point was corrupted or missing!
								   We can't tolerate that in a header.  Die. */
								System.err.print("Corrupt secondary header.  Exiting.\n");
								System.exit(1);
							}
							vi.headerIn(vc, op);
							i++;
						}
					}
				}
				/* no harm in not checking before adding more */
				bytes = inputStream.read(buffer);
				if (bytes == 0 && i < 2)
				{
					System.err.print("End of file before finding all Vorbis headers!\n");
					System.exit(1);
				}
				oy.write(buffer, bytes);
			}

			/* Throw the comments plus a few lines about the bitstream we're
			   decoding */
			{
				String[]	astrComments = vc.getUserComments();
				for (i = 0; i < astrComments.length; i++)
				{
					System.err.println(astrComments[i]);
				}
				System.err.print("\nBitstream is " + vi.getChannels() + " channel, " + vi.getRate() + " Hz\n");
				System.err.print("Encoded by: " + vc.getVendor() + "\n\n");
			}

			int	nChannels = vi.getChannels();
			convsize = 4096 / nChannels;

			/* OK, got and parsed all three headers. Initialize the Vorbis
			   packet->PCM decoder. */
			vd.init(vi); /* central decode state */
			vb.init(vd);     /* local state for most of the decode
							   so multiple block decodes can
							   proceed in parallel.  We could init
							   multiple vorbis_block structures
							   for vd here */

			/* The rest is just a straight decode loop until end of stream */
			while (!eos)
			{
				while (!eos)
				{
					int result = oy.pageOut(og);
					if (result == 0)
					{
						break; /* need more data */
					}
					if (result < 0)
					{ /* missing or corrupt data at this page position */
						System.err.print("Corrupt or missing data in bitstream; continuing...\n");
					}
					else
					{
						os.pageIn(og); /* can safely ignore errors at
									       this point */
						while (true)
						{
							result = os.packetOut(op);

							if (result == 0)
							{
								break; /* need more data */
							}
							if (result < 0)
							{ /* missing or corrupt data at this page position */
								/* no reason to complain; already complained above */
							}
							else
							{
								/* we have a packet.  Decode it */
								float[][] pcm = new float[nChannels][0];
								int samples;

								if (vb.synthesis(op) == 0) /* test for success! */
								{
									vd.blockIn(vb);
								}
								/* 

								**pcm is a multichannel float vector.  In stereo, for
								example, pcm[0] is left, and pcm[1] is right.  samples is
								the size of each channel.  Convert the float values
								(-1.<=range<=1.) to whatever PCM format and write it out */

								while ((samples = vd.pcmOut(pcm)) > 0)
								{
									int j;
									boolean	clipflag = false;
									int bout = (samples < convsize ? samples : convsize);

									/* convert floats to 16 bit signed ints (host order) and
									   interleave */
									for(i = 0; i < nChannels; i++)
									{
										int	ptr = i;
										// float *mono=pcm[i];
										for(j = 0; j < bout; j++)
										{
											int val = Math.round(pcm[i][j] * 32767.0F);
											/* might as well guard against clipping */
											if (val > 32767)
											{
												val = 32767;
												clipflag = true;
											}
											if(val<-32768)
											{
												val = -32768;
												clipflag = true;
											}
											convbuffer[ptr] = val;
											ptr += nChannels;
										}
									}

									if (clipflag)
									{
										System.err.print("Clipping in frame " + vd.getSequence() + "\n");
									}
									byte[]  abBuffer = new byte[2 * nChannels * bout];
									int	byteOffset = 0;
									boolean	bigEndian = false;
									for (int nSample = 0; nSample < nChannels * bout; nSample++)
									{
										int sample = convbuffer[nSample];
										if (bigEndian)
										{
											abBuffer[byteOffset++]=(byte) (sample >> 8);
											abBuffer[byteOffset++]=(byte) (sample & 0xFF);
										}
										else
										{
											abBuffer[byteOffset++]=(byte) (sample & 0xFF);
											abBuffer[byteOffset++]=(byte) (sample >> 8);
										}
									}
									outputStream.write(abBuffer);

									vd.read(bout); /* tell libvorbis how
													    many samples we
													    actually consumed */
								}	    
							}
						}
						if (og.isEos())
						{
							eos = true;
						}
					}
				}
				if (!eos)
				{
					bytes = inputStream.read(buffer);
					oy.write(buffer, bytes);
					if (bytes == 0)
					{
						eos = true;
					}
				}
			}

			/* clean up this logical bitstream; before exit we see if we're
			   followed by another [chained] */

			os.clear();

			/* ogg_page and ogg_packet structs always point to storage in
			   libvorbis.  They're never freed or manipulated directly */
			vb.clear();
			vd.clear();
			vc.clear();
			vi.clear();  /* must be called last */
		}

		/* OK, clean up the framer */
		oy.clear();

		System.err.print("Done.\n");
		System.exit(0);
	}
}


/*** VorbisDecoder.java ***/
