/*
 * MpegAudioFileReader.
 *
 * 10/10/05 : size computation bug fixed in parseID3v2Frames.
 *            RIFF/MP3 header support added.
 *            FLAC and MAC headers throw UnsupportedAudioFileException now.
 *            "mp3.id3tag.publisher" (TPUB/TPB) added.
 *            "mp3.id3tag.orchestra" (TPE2/TP2) added.
 *            "mp3.id3tag.length" (TLEN/TLE) added.
 *
 * 08/15/05 : parseID3v2Frames improved.
 *
 * 12/31/04 : mp3spi.weak system property added to skip controls.
 *
 * 11/29/04 : ID3v2.2, v2.3 & v2.4 support improved.
 *            "mp3.id3tag.composer" (TCOM/TCM) added
 *            "mp3.id3tag.grouping" (TIT1/TT1) added
 *            "mp3.id3tag.disc" (TPA/TPOS) added
 *            "mp3.id3tag.encoded" (TEN/TENC) added
 *            "mp3.id3tag.v2.version" added
 *
 * 11/28/04 : String encoding bug fix in chopSubstring method.
 *
 * JavaZOOM : mp3spi@javazoom.net
 * 			  http://www.javazoom.net
 *
 *-----------------------------------------------------------------------
 *   This program is free software; you can redistribute it and/or modify
 *   it under the terms of the GNU Library General Public License as published
 *   by the Free Software Foundation; either version 2 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Library General Public License for more details.
 *
 *   You should have received a copy of the GNU Library General Public
 *   License along with this program; if not, write to the Free Software
 *   Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 *----------------------------------------------------------------------
 */
package javazoom.spi.mpeg.sampled.file;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.security.AccessControlException;
import java.util.HashMap;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import javazoom.jl.decoder.Bitstream;
import javazoom.jl.decoder.Header;
import javazoom.spi.mpeg.sampled.file.tag.IcyInputStream;
import javazoom.spi.mpeg.sampled.file.tag.MP3Tag;
import org.tritonus.share.TDebug;
import org.tritonus.share.sampled.file.TAudioFileReader;

/**
 * This class implements AudioFileReader for MP3 SPI.
 */
public class MpegAudioFileReader extends TAudioFileReader
{
    public static final String VERSION = "MP3SPI 1.9.5";
    private final int SYNC = 0xFFE00000;
    private String weak = null;
    private final AudioFormat.Encoding[][] sm_aEncodings = {
            { MpegEncoding.MPEG2L1, MpegEncoding.MPEG2L2, MpegEncoding.MPEG2L3 },
            { MpegEncoding.MPEG1L1, MpegEncoding.MPEG1L2, MpegEncoding.MPEG1L3 },
            { MpegEncoding.MPEG2DOT5L1, MpegEncoding.MPEG2DOT5L2, MpegEncoding.MPEG2DOT5L3 }, };
    public static int INITAL_READ_LENGTH = 128000*32;
    private static int MARK_LIMIT = INITAL_READ_LENGTH + 1;
    static {
    	String s = System.getProperty("marklimit");
    	if (s != null)
    	{
    		try 
    		{
    			INITAL_READ_LENGTH = Integer.parseInt(s);
    			MARK_LIMIT = INITAL_READ_LENGTH + 1;
			} 
    		catch (NumberFormatException e) 
    		{
				e.printStackTrace();
			}
    	}
    }

    private static final String[] id3v1genres = {
          "Blues"
          , "Classic Rock"
          , "Country"
          , "Dance"
          , "Disco"
          , "Funk"
          , "Grunge"
          , "Hip-Hop"
          , "Jazz"
          , "Metal"
          , "New Age"
          , "Oldies"
          , "Other"
          , "Pop"
          , "R&B"
          , "Rap"
          , "Reggae"
          , "Rock"
          , "Techno"
          , "Industrial"
          , "Alternative"
          , "Ska"
          , "Death Metal"
          , "Pranks"
          , "Soundtrack"
          , "Euro-Techno"
          , "Ambient"
          , "Trip-Hop"
          , "Vocal"
          , "Jazz+Funk"
          , "Fusion"
          , "Trance"
          , "Classical"
          , "Instrumental"
          , "Acid"
          , "House"
          , "Game"
          , "Sound Clip"
          , "Gospel"
          , "Noise"
          , "AlternRock"
          , "Bass"
          , "Soul"
          , "Punk"
          , "Space"
          , "Meditative"
          , "Instrumental Pop"
          , "Instrumental Rock"
          , "Ethnic"
          , "Gothic"
          , "Darkwave"
          , "Techno-Industrial"
          , "Electronic"
          , "Pop-Folk"
          , "Eurodance"
          , "Dream"
          , "Southern Rock"
          , "Comedy"
          , "Cult"
          , "Gangsta"
          , "Top 40"
          , "Christian Rap"
          , "Pop/Funk"
          , "Jungle"
          , "Native American"
          , "Cabaret"
          , "New Wave"
          , "Psychadelic"
          , "Rave"
          , "Showtunes"
          , "Trailer"
          , "Lo-Fi"
          , "Tribal"
          , "Acid Punk"
          , "Acid Jazz"
          , "Polka"
          , "Retro"
          , "Musical"
          , "Rock & Roll"
          , "Hard Rock"
          , "Folk"
          , "Folk-Rock"
          , "National Folk"
          , "Swing"
          , "Fast Fusion"
          , "Bebob"
          , "Latin"
          , "Revival"
          , "Celtic"
          , "Bluegrass"
          , "Avantgarde"
          , "Gothic Rock"
          , "Progressive Rock"
          , "Psychedelic Rock"
          , "Symphonic Rock"
          , "Slow Rock"
          , "Big Band"
          , "Chorus"
          , "Easy Listening"
          , "Acoustic"
          , "Humour"
          , "Speech"
          , "Chanson"
          , "Opera"
          , "Chamber Music"
          , "Sonata"
          , "Symphony"
          , "Booty Brass"
          , "Primus"
          , "Porn Groove"
          , "Satire"
          , "Slow Jam"
          , "Club"
          , "Tango"
          , "Samba"
          , "Folklore"
          , "Ballad"
          , "Power Ballad"
          , "Rhythmic Soul"
          , "Freestyle"
          , "Duet"
          , "Punk Rock"
          , "Drum Solo"
          , "A Capela"
          , "Euro-House"
          , "Dance Hall"
          , "Goa"
          , "Drum & Bass"
          , "Club-House"
          , "Hardcore"
          , "Terror"
          , "Indie"
          , "BritPop"
          , "Negerpunk"
          , "Polsk Punk"
          , "Beat"
          , "Christian Gangsta Rap"
          , "Heavy Metal"
          , "Black Metal"
          , "Crossover"
          , "Contemporary Christian"
          , "Christian Rock"
          , "Merengue"
          , "Salsa"
          , "Thrash Metal"
          , "Anime"
          , "JPop"
          , "SynthPop"
          };

    public MpegAudioFileReader()
    {
        super(MARK_LIMIT, true);
        if (TDebug.TraceAudioFileReader) TDebug.out(VERSION);
        try
        {
            weak = System.getProperty("mp3spi.weak");
        }
        catch (AccessControlException e)
        {
        }
    }

    /**
     * Returns AudioFileFormat from File.
     */
    public AudioFileFormat getAudioFileFormat(File file) throws UnsupportedAudioFileException, IOException
    {
        return super.getAudioFileFormat(file);
    }

    /**
     * Returns AudioFileFormat from URL.
     */
    public AudioFileFormat getAudioFileFormat(URL url) throws UnsupportedAudioFileException, IOException
    {
        if (TDebug.TraceAudioFileReader)
        {
            TDebug.out("MpegAudioFileReader.getAudioFileFormat(URL): begin");
        }
        long lFileLengthInBytes = AudioSystem.NOT_SPECIFIED;
        URLConnection conn = url.openConnection();
        // Tell shoucast server (if any) that SPI support shoutcast stream.
        conn.setRequestProperty("Icy-Metadata", "1");
        InputStream inputStream = conn.getInputStream();
        AudioFileFormat audioFileFormat = null;
        try
        {
            audioFileFormat = getAudioFileFormat(inputStream, lFileLengthInBytes);
        }
        finally
        {
            inputStream.close();
        }
        if (TDebug.TraceAudioFileReader)
        {
            TDebug.out("MpegAudioFileReader.getAudioFileFormat(URL): end");
        }
        return audioFileFormat;
    }

    /**
     * Returns AudioFileFormat from inputstream and medialength.
     */
    public AudioFileFormat getAudioFileFormat(InputStream inputStream, long mediaLength) throws UnsupportedAudioFileException, IOException
    {
        if (TDebug.TraceAudioFileReader) TDebug.out(">MpegAudioFileReader.getAudioFileFormat(InputStream inputStream, long mediaLength): begin");
        HashMap aff_properties = new HashMap();
        HashMap af_properties = new HashMap();
        int mLength = (int) mediaLength;
        int size = inputStream.available();
        PushbackInputStream pis = new PushbackInputStream(inputStream, MARK_LIMIT);
        byte head[] = new byte[22];
        pis.read(head);
        if (TDebug.TraceAudioFileReader)
        {
            TDebug.out("InputStream : " + inputStream + " =>" + new String(head));
        }

        // Check for WAV, AU, and AIFF, Ogg Vorbis, Flac, MAC file formats.
        // Next check for Shoutcast (supported) and OGG (unsupported) streams.
        if ((head[0] == 'R') && (head[1] == 'I') && (head[2] == 'F') && (head[3] == 'F') && (head[8] == 'W') && (head[9] == 'A') && (head[10] == 'V') && (head[11] == 'E'))
        {
            if (TDebug.TraceAudioFileReader) TDebug.out("RIFF/WAV stream found");
            int isPCM = ((head[21]<<8)&0x0000FF00) | ((head[20])&0x00000FF);
            if (weak == null)
            {
                if (isPCM == 1) throw new UnsupportedAudioFileException("WAV PCM stream found");
            }

        }
        else if ((head[0] == '.') && (head[1] == 's') && (head[2] == 'n') && (head[3] == 'd'))
        {
            if (TDebug.TraceAudioFileReader) TDebug.out("AU stream found");
            if (weak == null) throw new UnsupportedAudioFileException("AU stream found");
        }
        else if ((head[0] == 'F') && (head[1] == 'O') && (head[2] == 'R') && (head[3] == 'M') && (head[8] == 'A') && (head[9] == 'I') && (head[10] == 'F') && (head[11] == 'F'))
        {
            if (TDebug.TraceAudioFileReader) TDebug.out("AIFF stream found");
            if (weak == null) throw new UnsupportedAudioFileException("AIFF stream found");
        }
        else if (((head[0] == 'M') | (head[0] == 'm')) && ((head[1] == 'A') | (head[1] == 'a')) && ((head[2] == 'C') | (head[2] == 'c')))
        {
            if (TDebug.TraceAudioFileReader) TDebug.out("APE stream found");
            if (weak == null) throw new UnsupportedAudioFileException("APE stream found");
        }
        else if (((head[0] == 'F') | (head[0] == 'f')) && ((head[1] == 'L') | (head[1] == 'l')) && ((head[2] == 'A') | (head[2] == 'a')) && ((head[3] == 'C') | (head[3] == 'c')))
        {
            if (TDebug.TraceAudioFileReader) TDebug.out("FLAC stream found");
            if (weak == null) throw new UnsupportedAudioFileException("FLAC stream found");
        }
        // Shoutcast stream ?
        else if (((head[0] == 'I') | (head[0] == 'i')) && ((head[1] == 'C') | (head[1] == 'c')) && ((head[2] == 'Y') | (head[2] == 'y')))
        {
            pis.unread(head);
            // Load shoutcast meta data.
            loadShoutcastInfo(pis, aff_properties);
        }
        // Ogg stream ?
        else if (((head[0] == 'O') | (head[0] == 'o')) && ((head[1] == 'G') | (head[1] == 'g')) && ((head[2] == 'G') | (head[2] == 'g')))
        {
            if (TDebug.TraceAudioFileReader) TDebug.out("Ogg stream found");
            if (weak == null) throw new UnsupportedAudioFileException("Ogg stream found");
        }
        // No, so pushback.
        else
        {
            pis.unread(head);
        }
        // MPEG header info.
        int nVersion = AudioSystem.NOT_SPECIFIED;
        int nLayer = AudioSystem.NOT_SPECIFIED;
        int nSFIndex = AudioSystem.NOT_SPECIFIED;
        int nMode = AudioSystem.NOT_SPECIFIED;
        int FrameSize = AudioSystem.NOT_SPECIFIED;
        int nFrameSize = AudioSystem.NOT_SPECIFIED;
        int nFrequency = AudioSystem.NOT_SPECIFIED;
        int nTotalFrames = AudioSystem.NOT_SPECIFIED;
        float FrameRate = AudioSystem.NOT_SPECIFIED;
        int BitRate = AudioSystem.NOT_SPECIFIED;
        int nChannels = AudioSystem.NOT_SPECIFIED;
        int nHeader = AudioSystem.NOT_SPECIFIED;
        int nTotalMS = AudioSystem.NOT_SPECIFIED;
        boolean nVBR = false;
        AudioFormat.Encoding encoding = null;
        try
        {
            Bitstream m_bitstream = new Bitstream(pis);
            int streamPos = m_bitstream.header_pos();
            aff_properties.put("mp3.header.pos", new Integer(streamPos));
            Header m_header = m_bitstream.readFrame();
            // nVersion = 0 => MPEG2-LSF (Including MPEG2.5), nVersion = 1 => MPEG1
            nVersion = m_header.version();
            if (nVersion == 2) aff_properties.put("mp3.version.mpeg", Float.toString(2.5f));
            else aff_properties.put("mp3.version.mpeg", Integer.toString(2 - nVersion));
            // nLayer = 1,2,3
            nLayer = m_header.layer();
            aff_properties.put("mp3.version.layer", Integer.toString(nLayer));
            nSFIndex = m_header.sample_frequency();
            nMode = m_header.mode();
            aff_properties.put("mp3.mode", new Integer(nMode));
            nChannels = nMode == 3 ? 1 : 2;
            aff_properties.put("mp3.channels", new Integer(nChannels));
            nVBR = m_header.vbr();
            af_properties.put("vbr", new Boolean(nVBR));
            aff_properties.put("mp3.vbr", new Boolean(nVBR));
            aff_properties.put("mp3.vbr.scale", new Integer(m_header.vbr_scale()));
            FrameSize = m_header.calculate_framesize();
            aff_properties.put("mp3.framesize.bytes", new Integer(FrameSize));
            if (FrameSize < 0) throw new UnsupportedAudioFileException("Invalid FrameSize : " + FrameSize);
            nFrequency = m_header.frequency();
            aff_properties.put("mp3.frequency.hz", new Integer(nFrequency));
            FrameRate = (float) ((1.0 / (m_header.ms_per_frame())) * 1000.0);
            aff_properties.put("mp3.framerate.fps", new Float(FrameRate));
            if (FrameRate < 0) throw new UnsupportedAudioFileException("Invalid FrameRate : " + FrameRate);
            // Remove heading tag length from real stream length.
            int tmpLength = mLength;
            if ((streamPos > 0) && (mLength != AudioSystem.NOT_SPECIFIED) && (streamPos < mLength)) tmpLength = tmpLength - streamPos;
            if (mLength != AudioSystem.NOT_SPECIFIED)
            {
                aff_properties.put("mp3.length.bytes", new Integer(mLength));
                nTotalFrames = m_header.max_number_of_frames(tmpLength);
                aff_properties.put("mp3.length.frames", new Integer(nTotalFrames));
            }
            BitRate = m_header.bitrate();
            af_properties.put("bitrate", new Integer(BitRate));
            aff_properties.put("mp3.bitrate.nominal.bps", new Integer(BitRate));
            nHeader = m_header.getSyncHeader();
            encoding = sm_aEncodings[nVersion][nLayer - 1];
            aff_properties.put("mp3.version.encoding", encoding.toString());
            if (mLength != AudioSystem.NOT_SPECIFIED)
            {
                nTotalMS = Math.round(m_header.total_ms(tmpLength));
                aff_properties.put("duration", new Long((long) nTotalMS * 1000L));
            }
            aff_properties.put("mp3.copyright", new Boolean(m_header.copyright()));
            aff_properties.put("mp3.original", new Boolean(m_header.original()));
            aff_properties.put("mp3.crc", new Boolean(m_header.checksums()));
            aff_properties.put("mp3.padding", new Boolean(m_header.padding()));
            InputStream id3v2 = m_bitstream.getRawID3v2();
            if (id3v2 != null)
            {
                aff_properties.put("mp3.id3tag.v2", id3v2);
                parseID3v2Frames(id3v2, aff_properties);
            }
            if (TDebug.TraceAudioFileReader) TDebug.out(m_header.toString());
        }
        catch (Exception e)
        {
            if (TDebug.TraceAudioFileReader) TDebug.out("not a MPEG stream:" + e.getMessage());
            throw new UnsupportedAudioFileException("not a MPEG stream:" + e.getMessage());
        }
        // Deeper checks ?
        int cVersion = (nHeader >> 19) & 0x3;
        if (cVersion == 1)
        {
            if (TDebug.TraceAudioFileReader) TDebug.out("not a MPEG stream: wrong version");
            throw new UnsupportedAudioFileException("not a MPEG stream: wrong version");
        }
        int cSFIndex = (nHeader >> 10) & 0x3;
        if (cSFIndex == 3)
        {
            if (TDebug.TraceAudioFileReader) TDebug.out("not a MPEG stream: wrong sampling rate");
            throw new UnsupportedAudioFileException("not a MPEG stream: wrong sampling rate");
        }
        // Look up for ID3v1 tag
        if ((size == mediaLength) && (mediaLength != AudioSystem.NOT_SPECIFIED))
        {
            FileInputStream fis = (FileInputStream) inputStream;
            byte[] id3v1 = new byte[128];
            long bytesSkipped = fis.skip(inputStream.available() - id3v1.length);
            int read = fis.read(id3v1, 0, id3v1.length);
            if ((id3v1[0] == 'T') && (id3v1[1] == 'A') && (id3v1[2] == 'G'))
            {
                parseID3v1Frames(id3v1, aff_properties);
            }
        }
        AudioFormat format = new MpegAudioFormat(encoding, (float) nFrequency, AudioSystem.NOT_SPECIFIED // SampleSizeInBits - The size of a sample
                , nChannels // Channels - The number of channels
                , -1 // The number of bytes in each frame
                , FrameRate // FrameRate - The number of frames played or recorded per second
                , true, af_properties);
        return new MpegAudioFileFormat(MpegFileFormatType.MP3, format, nTotalFrames, mLength, aff_properties);
    }

    /**
     * Returns AudioInputStream from file.
     */
    public AudioInputStream getAudioInputStream(File file) throws UnsupportedAudioFileException, IOException
    {
        if (TDebug.TraceAudioFileReader) TDebug.out("getAudioInputStream(File file)");
        InputStream inputStream = new FileInputStream(file);
        try
        {
            return getAudioInputStream(inputStream);
        }
        catch (UnsupportedAudioFileException e)
        {
            if (inputStream != null) inputStream.close();
            throw e;
        }
        catch (IOException e)
        {
            if (inputStream != null) inputStream.close();
            throw e;
        }
    }

    /**
     * Returns AudioInputStream from url.
     */
    public AudioInputStream getAudioInputStream(URL url) throws UnsupportedAudioFileException, IOException
    {
        if (TDebug.TraceAudioFileReader)
        {
            TDebug.out("MpegAudioFileReader.getAudioInputStream(URL): begin");
        }
        long lFileLengthInBytes = AudioSystem.NOT_SPECIFIED;
        URLConnection conn = url.openConnection();
        // Tell shoucast server (if any) that SPI support shoutcast stream.
        boolean isShout = false;
        int toRead = 4;
        byte[] head = new byte[toRead];
        conn.setRequestProperty("Icy-Metadata", "1");
        BufferedInputStream bInputStream = new BufferedInputStream(conn.getInputStream());
        bInputStream.mark(toRead);
        int read = bInputStream.read(head, 0, toRead);
        if ((read > 2) && (((head[0] == 'I') | (head[0] == 'i')) && ((head[1] == 'C') | (head[1] == 'c')) && ((head[2] == 'Y') | (head[2] == 'y')))) isShout = true;
        bInputStream.reset();
        InputStream inputStream = null;
        // Is is a shoutcast server ?
        if (isShout == true)
        {
            // Yes
            IcyInputStream icyStream = new IcyInputStream(bInputStream);
            icyStream.addTagParseListener(IcyListener.getInstance());
            inputStream = icyStream;
        }
        else
        {
            // No, is Icecast 2 ?
            String metaint = conn.getHeaderField("icy-metaint");
            if (metaint != null)
            {
                // Yes, it might be icecast 2 mp3 stream.
                IcyInputStream icyStream = new IcyInputStream(bInputStream, metaint);
                icyStream.addTagParseListener(IcyListener.getInstance());
                inputStream = icyStream;
            }
            else
            {
                // No
                inputStream = bInputStream;
            }
        }
        AudioInputStream audioInputStream = null;
        try
        {
            audioInputStream = getAudioInputStream(inputStream, lFileLengthInBytes);
        }
        catch (UnsupportedAudioFileException e)
        {
            inputStream.close();
            throw e;
        }
        catch (IOException e)
        {
            inputStream.close();
            throw e;
        }
        if (TDebug.TraceAudioFileReader)
        {
            TDebug.out("MpegAudioFileReader.getAudioInputStream(URL): end");
        }
        return audioInputStream;
    }

    /**
     * Return the AudioInputStream from the given InputStream.
     */
    public AudioInputStream getAudioInputStream(InputStream inputStream) throws UnsupportedAudioFileException, IOException
    {
        if (TDebug.TraceAudioFileReader) TDebug.out("MpegAudioFileReader.getAudioInputStream(InputStream inputStream)");
        if (!inputStream.markSupported()) inputStream = new BufferedInputStream(inputStream);
        return super.getAudioInputStream(inputStream);
    }

    /**
     * Parser ID3v1 frames
     * @param frames
     * @param props
     */
    protected void parseID3v1Frames(byte[] frames, HashMap props)
    {
        if (TDebug.TraceAudioFileReader) TDebug.out("Parsing ID3v1");
        String tag = null;
        try
        {
            tag = new String(frames, 0, frames.length, "ISO-8859-1");
        }
        catch (UnsupportedEncodingException e)
        {
            tag = new String(frames, 0, frames.length);
            if (TDebug.TraceAudioFileReader) TDebug.out("Cannot use ISO-8859-1");
        }
        if (TDebug.TraceAudioFileReader) TDebug.out("ID3v1 frame dump='" + tag + "'");
        int start = 3;
        String titlev1 = chopSubstring(tag, start, start += 30);
        String titlev2 = (String) props.get("title");
        if (((titlev2 == null) || (titlev2.length() == 0)) && (titlev1 != null)) props.put("title", titlev1);
        String artistv1 = chopSubstring(tag, start, start += 30);
        String artistv2 = (String) props.get("author");
        if (((artistv2 == null) || (artistv2.length() == 0)) && (artistv1 != null)) props.put("author", artistv1);
        String albumv1 = chopSubstring(tag, start, start += 30);
        String albumv2 = (String) props.get("album");
        if (((albumv2 == null) || (albumv2.length() == 0)) && (albumv1 != null)) props.put("album", albumv1);
        String yearv1 = chopSubstring(tag, start, start += 4);
        String yearv2 = (String) props.get("year");
        if (((yearv2 == null) || (yearv2.length() == 0)) && (yearv1 != null)) props.put("date", yearv1);
        String commentv1 = chopSubstring(tag, start, start += 28);
        String commentv2 = (String) props.get("comment");
        if (((commentv2 == null) || (commentv2.length() == 0)) && (commentv1 != null)) props.put("comment", commentv1);
        String trackv1 = "" + ((int) (frames[126] & 0xff));
        String trackv2 = (String) props.get("mp3.id3tag.track");
        if (((trackv2 == null) || (trackv2.length() == 0)) && (trackv1 != null)) props.put("mp3.id3tag.track", trackv1);
        int genrev1 = (int) (frames[127] & 0xff);
        if ((genrev1 >= 0) && (genrev1 < id3v1genres.length))
        {
            String genrev2 = (String) props.get("mp3.id3tag.genre");
            if (((genrev2 == null) || (genrev2.length() == 0))) props.put("mp3.id3tag.genre", id3v1genres[genrev1]);
        }
        if (TDebug.TraceAudioFileReader) TDebug.out("ID3v1 parsed");
    }

    /**
     * Extract
     * @param s
     * @param start
     * @param end
     * @return
     */
    private String chopSubstring(String s, int start, int end)
    {
        String str = null;
        // 11/28/04 - String encoding bug fix.
        try
        {
            str = s.substring(start, end);
            int loc = str.indexOf('\0');
            if (loc != -1) str = str.substring(0, loc);
        }
        catch (StringIndexOutOfBoundsException e)
        {
            // Skip encoding issues.
            if (TDebug.TraceAudioFileReader) TDebug.out("Cannot chopSubString " + e.getMessage());
        }
        return str;
    }

    /**
     * Parse ID3v2 frames to add album (TALB), title (TIT2), date (TYER), author (TPE1), copyright (TCOP), comment (COMM) ...
     * @param frames
     * @param props
     */
    protected void parseID3v2Frames(InputStream frames, HashMap props)
    {
        if (TDebug.TraceAudioFileReader) TDebug.out("Parsing ID3v2");
        byte[] bframes = null;
        int size = -1;
        try
        {
            size = frames.available();
            bframes = new byte[size];
            frames.mark(size);
            frames.read(bframes);
            frames.reset();
        }
        catch (IOException e)
        {
            if (TDebug.TraceAudioFileReader) TDebug.out("Cannot parse ID3v2 :" + e.getMessage());
        }
        if (!"ID3".equals(new String(bframes, 0, 3)))
        {
            TDebug.out("No ID3v2 header found!");
            return;
        }
        int v2version = (int) (bframes[3] & 0xFF);
        props.put("mp3.id3tag.v2.version", String.valueOf(v2version));
        if (v2version < 2 || v2version > 4)
        {
            TDebug.out("Unsupported ID3v2 version " + v2version + "!");
            return;
        }
        try
        {
            if (TDebug.TraceAudioFileReader) TDebug.out("ID3v2 frame dump='" + new String(bframes, 0, bframes.length) + "'");
            /* ID3 tags : http://www.unixgods.org/~tilo/ID3/docs/ID3_comparison.html */
            String value = null;
            for (int i = 10; i < bframes.length && bframes[i] > 0; i += size)
            {
                if (v2version == 3 || v2version == 4)
                {
                    // ID3v2.3 & ID3v2.4
                    String code = new String(bframes, i, 4);
                    size = extractSyncsafeInteger(bframes, i);
                    i += 10;
                    if ((code.equals("TALB")) || (code.equals("TIT2")) || (code.equals("TYER")) ||
                        (code.equals("TPE1")) || (code.equals("TCOP")) || (code.equals("COMM")) ||
                        (code.equals("TCON")) || (code.equals("TRCK")) || (code.equals("TPOS")) ||
                        (code.equals("TDRC")) || (code.equals("TCOM")) || (code.equals("TIT1")) ||
                        (code.equals("TENC")) || (code.equals("TPUB")) || (code.equals("TPE2")) ||
                        (code.equals("TLEN")) )
                    {
                        if (code.equals("COMM")) value = parseText(bframes, i, size, 5);
                        else value = parseText(bframes, i, size, 1);
                        if ((value != null) && (value.length() > 0))
                        {
                            if (code.equals("TALB")) props.put("album", value);
                            else if (code.equals("TIT2")) props.put("title", value);
                            else if (code.equals("TYER")) props.put("date", value);
                            // ID3v2.4 date fix.
                            else if (code.equals("TDRC")) props.put("date", value);
                            else if (code.equals("TPE1")) props.put("author", value);
                            else if (code.equals("TCOP")) props.put("copyright", value);
                            else if (code.equals("COMM")) props.put("comment", value);
                            else if (code.equals("TCON")) props.put("mp3.id3tag.genre", value);
                            else if (code.equals("TRCK")) props.put("mp3.id3tag.track", value);
                            else if (code.equals("TPOS")) props.put("mp3.id3tag.disc", value);
                            else if (code.equals("TCOM")) props.put("mp3.id3tag.composer", value);
                            else if (code.equals("TIT1")) props.put("mp3.id3tag.grouping", value);
                            else if (code.equals("TENC")) props.put("mp3.id3tag.encoded", value);
                            else if (code.equals("TPUB")) props.put("mp3.id3tag.publisher", value);
                            else if (code.equals("TPE2")) props.put("mp3.id3tag.orchestra", value);
                            else if (code.equals("TLEN")) props.put("mp3.id3tag.length", value);
                        }
                    }
                }
                else
                {
                    // ID3v2.2
                    String scode = new String(bframes, i, 3);
                    size = (int) (0x00000000) + (bframes[i + 3] << 16) + (bframes[i + 4] << 8) + (bframes[i + 5]);
                    i += 6;
                    if ((scode.equals("TAL")) || (scode.equals("TT2")) || (scode.equals("TP1")) ||
                        (scode.equals("TYE")) || (scode.equals("TRK")) || (scode.equals("TPA")) ||
                        (scode.equals("TCR")) || (scode.equals("TCO")) || (scode.equals("TCM")) ||
                        (scode.equals("COM")) || (scode.equals("TT1")) || (scode.equals("TEN")) ||
                        (scode.equals("TPB")) || (scode.equals("TP2")) || (scode.equals("TLE")) )
                    {
                        if (scode.equals("COM")) value = parseText(bframes, i, size, 5);
                        else value = parseText(bframes, i, size, 1);
                        if ((value != null) && (value.length() > 0))
                        {
                            if (scode.equals("TAL")) props.put("album", value);
                            else if (scode.equals("TT2")) props.put("title", value);
                            else if (scode.equals("TYE")) props.put("date", value);
                            else if (scode.equals("TP1")) props.put("author", value);
                            else if (scode.equals("TCR")) props.put("copyright", value);
                            else if (scode.equals("COM")) props.put("comment", value);
                            else if (scode.equals("TCO")) props.put("mp3.id3tag.genre", value);
                            else if (scode.equals("TRK")) props.put("mp3.id3tag.track", value);
                            else if (scode.equals("TPA")) props.put("mp3.id3tag.disc", value);
                            else if (scode.equals("TCM")) props.put("mp3.id3tag.composer", value);
                            else if (scode.equals("TT1")) props.put("mp3.id3tag.grouping", value);
                            else if (scode.equals("TEN")) props.put("mp3.id3tag.encoded", value);
                            else if (scode.equals("TPB")) props.put("mp3.id3tag.publisher", value);
                            else if (scode.equals("TP2")) props.put("mp3.id3tag.orchestra", value);
                            else if (scode.equals("TLE")) props.put("mp3.id3tag.length", value);
                        }
                    }
                }
            }
        }
        catch (RuntimeException e)
        {
            // Ignore all parsing errors.
            if (TDebug.TraceAudioFileReader) TDebug.out("Cannot parse ID3v2 :" + e.getMessage());
        }
        if (TDebug.TraceAudioFileReader) TDebug.out("ID3v2 parsed");
    }

    private int extractSyncsafeInteger(final byte[] bframes, final int i) {
        return (bframes[i + 4] & 0x7f) << 21 | (bframes[i + 5] & 0x7f) << 14 | (bframes[i + 6] & 0x7f) << 7 | (bframes[i + 7]) & 0x7f;
    }

    /**
     * Parse Text Frames.
     *
     * @param bframes
     * @param offset
     * @param size
     * @param skip
     * @return
     */
    protected String parseText(byte[] bframes, int offset, int size, int skip)
    {
        String value = null;
        try
        {
            String[] ENC_TYPES = { "ISO-8859-1", "UTF16", "UTF-16BE", "UTF-8" };
            value = new String(bframes, offset + skip, size - skip, ENC_TYPES[bframes[offset]]);
            value = chopSubstring(value, 0, value.length());
        }
        catch (UnsupportedEncodingException e)
        {
            if (TDebug.TraceAudioFileReader) TDebug.out("ID3v2 Encoding error :" + e.getMessage());
        }
        return value;
    }

    /**
     * Load shoutcast (ICY) info.
     *
     * @param input
     * @param props
     * @throws IOException
     */
    protected void loadShoutcastInfo(InputStream input, HashMap props) throws IOException
    {
        IcyInputStream icy = new IcyInputStream(new BufferedInputStream(input));
        HashMap metadata = icy.getTagHash();
        MP3Tag titleMP3Tag = icy.getTag("icy-name");
        if (titleMP3Tag != null) props.put("title", ((String) titleMP3Tag.getValue()).trim());
        MP3Tag[] meta = icy.getTags();
        if (meta != null)
        {
            StringBuffer metaStr = new StringBuffer();
            for (int i = 0; i < meta.length; i++)
            {
                String key = meta[i].getName();
                String value = ((String) icy.getTag(key).getValue()).trim();
                props.put("mp3.shoutcast.metadata." + key, value);
            }
        }
    }
}
