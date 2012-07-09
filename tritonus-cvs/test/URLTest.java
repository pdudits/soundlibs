import	java.net.URL;
import	java.net.MalformedURLException;

import	org.tritonus.sampled.cdda.CddaURLStreamHandlerFactory;



public class URLTest
{
	static
	{
		URL.setURLStreamHandlerFactory(new CddaURLStreamHandlerFactory());
	}



	public static void main(String[] args)
	{
		String	strURL = args[0];
		URL	url = null;
		try
		{
			url = new URL(strURL);
		}
		catch (MalformedURLException e)
		{
			e.printStackTrace();
			System.exit(1);
		}
		System.out.println("authority: " + url.getAuthority());
		System.out.println("file: " + url.getFile());
		System.out.println("host: " + url.getHost());
		System.out.println("path: " + url.getPath());
		System.out.println("port: " + url.getPort());
		System.out.println("protocol: " + url.getProtocol());
		System.out.println("query: " + url.getQuery());
		System.out.println("ref: " + url.getRef());
		System.out.println("user info: " + url.getUserInfo());
	}
}
