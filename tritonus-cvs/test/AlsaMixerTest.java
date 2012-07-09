/*
 *	AlsaMixerTest.java
 */

import	org.tritonus.lowlevel.alsa.AlsaMixer;
import	org.tritonus.lowlevel.alsa.AlsaMixerElement;



public class AlsaMixerTest
{
	private static boolean	sm_bShowInactiveElements;



	public static void main(String[] args)
		throws Exception
	{
		String	strMixerName = "hw:0";
		if (args.length > 0)
		{
			strMixerName = args[0];
		}
		out("Mixer: " + strMixerName);
		AlsaMixer	mixer = new AlsaMixer(strMixerName);
		int[]		anIndices = new int[200];
		String[]	astrNames = new String[200];
		int	nReturn = mixer.readControlList(anIndices, astrNames);
		out("readControlList() returns: " + nReturn);
		if (nReturn > 0)
		{
			out("Mixer controls:");
			for (int i = 0; i < nReturn; i++)
			{
				out("" + i + " " + anIndices[i] + " " + astrNames[i]);
				AlsaMixerElement	element = new AlsaMixerElement(mixer, anIndices[i], astrNames[i]);
				if (element.isActive() || sm_bShowInactiveElements)
				{
					out("--------------------------------------------------------------------------------");
					output(element);
				}
			}
			out("--------------------------------------------------------------------------------");
		}
		mixer.close();
	}



	private static void output(AlsaMixerElement element)
	{
		out("  name: " + element.getName());
		out("  index: " + element.getName());
		out("  active: " + element.isActive());

		if (hasPlaybackChannels(element))
		{
			outputPlayback(element);
		}
		else
		{
			out("* no playback channels");
		}
		if (hasCaptureChannels(element))
		{
			outputCapture(element);
		}
		else
		{
			out("* no capture channels");
		}
	}



	private static void outputPlayback(AlsaMixerElement element)
	{
		out("  playback mono: " + element.isPlaybackMono());
		for (int nChannel = AlsaMixerElement.SND_MIXER_SCHN_FRONT_LEFT;
		     nChannel <= AlsaMixerElement.SND_MIXER_SCHN_WOOFER;
		     nChannel++)
		{
			out("  playback channel (" + AlsaMixerElement.getChannelName(nChannel) + "): " + element.hasPlaybackChannel(nChannel));
		}
		out("  common volume: " + element.hasCommonVolume());
		out("  playback volume: " + element.hasPlaybackVolume());
		out("  playback volume joined: " + element.hasPlaybackVolumeJoined());
		out("  common switch: " + element.hasCommonSwitch());
		out("  playback switch: " + element.hasPlaybackSwitch());
		out("  playback switch joined: " + element.hasPlaybackSwitchJoined());
	}


	private static void outputCapture(AlsaMixerElement element)
	{
		out("  capture mono: " + element.isCaptureMono());
		for (int nChannel = AlsaMixerElement.SND_MIXER_SCHN_FRONT_LEFT;
		     nChannel <= AlsaMixerElement.SND_MIXER_SCHN_WOOFER;
		     nChannel++)
		{
			out("  capture channel (" + AlsaMixerElement.getChannelName(nChannel) + "): " + element.hasCaptureChannel(nChannel));
		}
		out("  common volume: " + element.hasCommonVolume());
		out("  capture volume: " + element.hasCaptureVolume());
		out("  capture volume joined: " + element.hasCaptureVolumeJoined());
		out("  common switch: " + element.hasCommonSwitch());
		out("  capture switch: " + element.hasCaptureSwitch());
		out("  capture switch joined: " + element.hasCaptureSwitchJoinded());
		out("  capture switch exclusive: " + element.hasCaptureSwitchExclusive());
		if (element.hasCaptureSwitchExclusive())
		{
			out("  capture group: " + element.getCaptureGroup());
		}
	}


	private static boolean hasPlaybackChannels(AlsaMixerElement element)
	{
		boolean	bHasChannels = false;
		for (int nChannel = AlsaMixerElement.SND_MIXER_SCHN_FRONT_LEFT;
		     nChannel <= AlsaMixerElement.SND_MIXER_SCHN_WOOFER;
		     nChannel++)
		{
			bHasChannels |= element.hasPlaybackChannel(nChannel);
		}
		return bHasChannels;
	}


	private static boolean hasCaptureChannels(AlsaMixerElement element)
	{
		boolean	bHasChannels = false;
		for (int nChannel = AlsaMixerElement.SND_MIXER_SCHN_FRONT_LEFT;
		     nChannel <= AlsaMixerElement.SND_MIXER_SCHN_WOOFER;
		     nChannel++)
		{
			bHasChannels |= element.hasCaptureChannel(nChannel);
		}
		return bHasChannels;
	}


	private static void out(String strMessage)
	{
		System.out.println(strMessage);
	}
}



/*** AlsaMixerTest.java ***/
