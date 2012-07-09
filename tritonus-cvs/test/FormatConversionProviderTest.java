/*
 *	FormatConversionProviderTest.java
 */

import java.util.ArrayList;
import java.util.List;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioFormat.Encoding;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.spi.FormatConversionProvider;




public class FormatConversionProviderTest
{
	private static final float[]	COMMON_SAMPLE_RATES =
	{
		8000.0F,
		11025.0F,
		16000.0F,
		22050.0F,
		32000.0F,
		44100.0F,
		48000.0F,
		96000.0F,
	};

	private static final int[]	COMMON_SAMPLE_SIZES =
	{
		8, 16, 24, 32,
	};

	private static final int[]	COMMON_CHANNELS =
	{
		1, 2,
	};

	public static void main(String[] args)
		throws Exception
	{
		String		strProviderClassName = args[0];
		FormatConversionProvider	provider = getProvider(strProviderClassName);

		outSeparator();
		out("FormatConversionProvider: " + provider.getClass().getName());
		outSeparator();

		Encoding[] aSourceEncodings = provider.getSourceEncodings();
		out("Source Encodings:");
		out(aSourceEncodings);
		outSeparator();

		Encoding[] aTargetEncodings = provider.getTargetEncodings();
		out("Target Encodings:");
		out(aTargetEncodings);
		outSeparator();

		for (int i = 0; i < aSourceEncodings.length; i++)
		{
			outTargetEncodingsForFormat(provider,
						    aSourceEncodings[i]);
		}

		// test getAudioInputStream(AudioFormat.Encoding targetEncoding, AudioInputStream sourceStream)
		for (int nSourceEncoding = 0; nSourceEncoding < aSourceEncodings.length; nSourceEncoding++)
		{
			Encoding	sourceEncoding = aSourceEncodings[nSourceEncoding];
			AudioFormat[]	aSourceFormats = createAudioFormatsForEncoding(sourceEncoding);
			for (int nSourceFormat = 0; nSourceFormat < aSourceFormats.length; nSourceFormat++)
			{
				AudioFormat	sourceFormat = aSourceFormats[nSourceFormat];
				for (int nTargetEncoding = 0; nTargetEncoding < aTargetEncodings.length; nTargetEncoding++)
				{
					Encoding	targetEncoding = aTargetEncodings[nTargetEncoding];
					boolean bSupported = provider.isConversionSupported(targetEncoding, sourceFormat);
					out("conversion supported " + getAudioFormatString(sourceFormat) + " --> " + targetEncoding + ": " + bSupported);
				}
			}
		}
	}



	private static void outTargetEncodingsForFormat(
		FormatConversionProvider provider,
		Encoding sourceEncoding)
	{
		AudioFormat	audioFormat = new AudioFormat(
			sourceEncoding,
			AudioSystem.NOT_SPECIFIED,
			AudioSystem.NOT_SPECIFIED,
			AudioSystem.NOT_SPECIFIED,
			AudioSystem.NOT_SPECIFIED,
			AudioSystem.NOT_SPECIFIED,
			false);
		Encoding[] aTargetEncodingsForFormat = provider.getTargetEncodings();
		out("Target Encodings for " + getAudioFormatString(audioFormat));
		out(aTargetEncodingsForFormat);
		outSeparator();
	}


	private static AudioFormat[] createAudioFormatsForEncoding(Encoding encoding)
	{
		List	formats = new ArrayList();
		if (encoding == Encoding.PCM_SIGNED ||
		    encoding == Encoding.PCM_UNSIGNED)
		{
			for (int nSampleSizeIndex = 0; nSampleSizeIndex < COMMON_SAMPLE_SIZES.length; nSampleSizeIndex++)
			{
				createAudioFormatsForEncodingSub(formats, encoding,
								 COMMON_SAMPLE_SIZES[nSampleSizeIndex]);
			}
		}
		else if (encoding == Encoding.ALAW ||
			 encoding == Encoding.ULAW)
		{
			createAudioFormatsForEncodingSub(formats, encoding, 8);
		}
		return (AudioFormat[]) formats.toArray(new AudioFormat[0]);
	}


	private static void createAudioFormatsForEncodingSub(List formats,
						      Encoding encoding,
						      int nSampleSizeInBits)
	{
		for (int nSampleRateIndex = 0; nSampleRateIndex < COMMON_SAMPLE_RATES.length; nSampleRateIndex++)
		{
			for (int nChannelIndex = 0; nChannelIndex < COMMON_CHANNELS.length; nChannelIndex++)
			{
				for (int nEndianess = 0; nEndianess <= 1; nEndianess++)
				{
					boolean bEndianess = (nEndianess == 0);
					AudioFormat	format = new AudioFormat(
						encoding,
						COMMON_SAMPLE_RATES[nSampleRateIndex],
						nSampleSizeInBits,
						COMMON_CHANNELS[nChannelIndex],
						nSampleSizeInBits * COMMON_CHANNELS[nChannelIndex] / 8,
						COMMON_SAMPLE_RATES[nSampleRateIndex],
						bEndianess);
					formats.add(format);
				}
			}
		}
	}


	private static FormatConversionProvider getProvider(String strProviderClassName)
		throws Exception
	{
		Class		providerClass = Class.forName(strProviderClassName);
		FormatConversionProvider	provider = (FormatConversionProvider) providerClass.newInstance();
		return provider;
	}



	private static String getAudioFormatString(AudioFormat audioFormat)
	{
		return getAudioFormatStringImpl0(audioFormat);
	}



	private static String getAudioFormatStringImpl0(AudioFormat audioFormat)
	{
		StringBuffer strBuf = new StringBuffer();
		strBuf.append(audioFormat.getEncoding().toString());
		strBuf.append(", ");
		strBuf.append(audioFormat.getSampleRate());
		strBuf.append(" Hz , ");
		strBuf.append(audioFormat.getSampleSizeInBits());
		strBuf.append(" bit , ");
		strBuf.append(audioFormat.getChannels());
		strBuf.append(" ch, ");
		strBuf.append(audioFormat.getFrameSize());
		strBuf.append(" byte, ");
		strBuf.append(audioFormat.getFrameRate());
		strBuf.append(" Hz, ");
		strBuf.append(audioFormat.isBigEndian() ? "BE" : "le");
		return strBuf.toString();
	}



	private static String getAudioFormatStringImpl1(AudioFormat audioFormat)
	{
		StringBuffer strBuf = new StringBuffer();
		strBuf.append("enc: ");
		strBuf.append(audioFormat.getEncoding().toString());
		strBuf.append(", sr: ");
		strBuf.append(audioFormat.getSampleRate());
		strBuf.append(", ss: ");
		strBuf.append(audioFormat.getSampleSizeInBits());
		strBuf.append(", ch: ");
		strBuf.append(audioFormat.getChannels());
		strBuf.append(", fs: ");
		strBuf.append(audioFormat.getFrameSize());
		strBuf.append(", fr: ");
		strBuf.append(audioFormat.getFrameRate());
		strBuf.append(audioFormat.isBigEndian() ? ", BE" : ", le");
		return strBuf.toString();
	}



	private static void out(Encoding[] aEncodings)
	{
		for (int i = 0; i < aEncodings.length; i++)
		{
			out(aEncodings[i].toString());
		}
	}



	private static void outSeparator()
	{
		out("------------------------------------------------------------------------------");
	}



	private static void out(String strMessage)
	{
		System.out.println(strMessage);
	}
}


/*** FormatConversionProviderTest.java ****/
