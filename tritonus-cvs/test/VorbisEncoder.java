/*
 *	VorbisEncoder.java
 */

import java.io.IOException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import java.util.Random;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.tritonus.lowlevel.ogg.StreamState;
import org.tritonus.lowlevel.ogg.Packet;
import org.tritonus.lowlevel.ogg.Page;
import org.tritonus.lowlevel.vorbis.Block;
import org.tritonus.lowlevel.vorbis.Comment;
import org.tritonus.lowlevel.vorbis.DspState;
import org.tritonus.lowlevel.vorbis.Info;



public class VorbisEncoder
{
	private static final int	READ = 1024;
	private static byte[]		readbuffer = new byte[READ * 4 + 44]; /* out of the data segment, not the stack */

	public static void main(String[] args)
		throws IOException
	{
		StreamState os = new StreamState(); /* take physical pages, weld into a logical
			  stream of packets */
		Page         og = new Page(); /* one Ogg bitstream page.  Vorbis packets are inside */
		Packet       op = new Packet(); /* one raw packet of data for decode */
  
		Info      vi = new Info(); /* struct that stores all the static vorbis bitstream
			  settings */
		Comment   vc = new Comment(); /* struct that stores all the user comments */

		DspState vd = new DspState(); /* central working state for the packet->PCM decoder */
		Block     vb = new Block(); /* local working space for packet->PCM decode */

		boolean eos = false;
		int i;
		int founddata;


  /* we cheat on the WAV header; we just bypass 44 bytes and never
     verify that it matches 16bit/stereo/44.1kHz.  This is just an
     example, after all. */


		AudioInputStream	ais = null;
		try
		{
			ais = AudioSystem.getAudioInputStream(new File(args[0]));
		}
		catch (UnsupportedAudioFileException e)
		{
			e.printStackTrace();
		}
		AudioFormat	format = ais.getFormat();
		if (format.getChannels() != 2 ||
		    format.getSampleSizeInBits() != 16)
		{
			System.out.println("need 16 bit stereo!");
			System.exit(1);
		}
		File outputFile = new File(args[1]);
		OutputStream output = new FileOutputStream(outputFile);

		/********** Encode setup ************/

		/* choose an encoding mode */
		/* (quality mode .4: 44kHz stereo coupled, roughly 128kbps VBR) */
		vi.init();

		vi.encodeInitVBR(format.getChannels(),
				 (int) format.getSampleRate(),
				 0.1F); // max compression

		/* add a comment */
		vc.init();
		vc.addTag("ENCODER","encoder_example.c");

		/* set up the analysis state and auxiliary encoding storage */
		vd.initAnalysis(vi);
		vb.init(vd);
  
		/* set up our packet->stream encoder */
		/* pick a random serial number; that way we can more likely build
		   chained streams just by concatenation */
		Random random = new Random(System.currentTimeMillis());
		os.init(random.nextInt());

		/* Vorbis streams begin with three headers; the initial header (with
		   most of the codec setup parameters) which is mandated by the Ogg
		   bitstream spec.  The second header holds any comment fields.  The
		   third header holds the bitstream codebook.  We merely need to
		   make the headers, then pass them to libvorbis one at a time;
		   libvorbis handles the additional Ogg bitstream constraints */

		Packet header = new Packet();
		Packet header_comm = new Packet();
		Packet header_code = new Packet();

		vd.headerOut(vc, header, header_comm, header_code);
		os.packetIn(header); /* automatically placed in its own
						     page */
		os.packetIn(header_comm);
		os.packetIn(header_code);

		/* We don't have to write out here, but doing so makes streaming 
		 * much easier, so we do, flushing ALL pages. This ensures the actual
		 * audio data will start on a new page
		 */
		while (!eos)
		{
			int result = os.flush(og);
			if(result == 0)
				break;
			output.write(og.getHeader());
			output.write(og.getBody());
		}


		while (!eos)
		{
			int bytes = ais.read(readbuffer, 0, READ * 4); /* stereo hardwired here */

			if (bytes == 0 ||  bytes == -1)
			{
      /* end of file.  this can be done implicitly in the mainline,
         but it's easier to see here in non-clever fashion.
         Tell the library we're at end of stream so that it can handle
         the last frame and mark end of stream in the output properly */
				vd.write(null, 0);

			}
			else
			{
				/* data to encode */

				/* expose the buffer to submit data */
				float[][] buffer = new float[format.getChannels()][READ];
				// float[][] buffer = vd.buffer(READ);

				/* uninterleave samples */
				for (i = 0;i < bytes/4;i++)
				{
					int	nSample;
					float	fSample;

					nSample = (readbuffer[i * 4 + 1] << 8)
						| (0x00ff & readbuffer[i * 4 + 0]);
					fSample = nSample / 32768.0F;
					buffer[0][i] = fSample;
					nSample = (readbuffer[i * 4 + 3] << 8)
						| (0x00ff & readbuffer[i * 4 + 2]);
					fSample = nSample / 32768.f;
					buffer[1][i] = fSample;
				}
    
				/* tell the library how much we actually submitted */
				vd.write(buffer, bytes/4);
			}

			/* vorbis does some data preanalysis, then divvies up blocks for
			   more involved (potentially parallel) processing.  Get a single
			   block for encoding now */
			while (vd.blockOut(vb) == 1)
			{

				/* analysis, assume we want to use bitrate management */
				vb.analysis(null);
				vb.addBlock();

				while (vd.flushPacket(op) != 0)
				{
					/* weld the packet into the bitstream */
					os.packetIn(op);
	
					/* write out pages (if any) */
					while (!eos)
					{
						int result = os.pageOut(og);
						if(result == 0)
							break;
						output.write(og.getHeader());
						output.write(og.getBody());

						/* this could be set above, but for illustrative purposes, I do
						   it here (to show that vorbis does know where the stream ends) */
	  
						if (og.isEos())
						{
							eos = true;
						}
					}
				}
			}
		}

		/* clean up and exit.  vorbis_info_clear() must be called last */
		os.clear();
		vb.clear();
		vd.clear();
		vc.clear();
		vi.clear();

		output.close();

		/* ogg_page and ogg_packet structs always point to storage in
		   libvorbis.  They're never freed or manipulated directly */
// 		fprintf(stderr,"Done.\n");
// 		return(0);
	}
}



/*** VorbisEncoder.java ***/
