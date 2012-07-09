/*
 *	AlsaCtlTest.java
 *
 *	For testing the ALSA ctl low-level.
 */

import	org.tritonus.lowlevel.alsa.AlsaCtl;
import	org.tritonus.lowlevel.alsa.AlsaCtlCardInfo;

// TODO: dump pcm info
public class AlsaCtlTest
{
	public static void main(String[] args)
		throws Exception
	{
		String	strCtlName = "hw:0";
		if (args.length > 0)
		{
			strCtlName = args[0];
		}
		System.out.println("Card: " + strCtlName);
		AlsaCtl		ctl = new AlsaCtl(strCtlName, 0);
		AlsaCtlCardInfo	cardInfo = new AlsaCtlCardInfo();
		ctl.getCardInfo(cardInfo);
		ctl.close();
		output(cardInfo);
	}



	private static void output(AlsaCtlCardInfo cardInfo)
	{
		System.out.println("card: " + cardInfo.getCard());
		System.out.println("id: " + cardInfo.getId());
		System.out.println("driver: " + cardInfo.getDriver());
		System.out.println("name: " + cardInfo.getName());
		System.out.println("longname: " + cardInfo.getLongname());
		System.out.println("components: " + cardInfo.getComponents());
	}
}
