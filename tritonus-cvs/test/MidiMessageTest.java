/*
 *	MidiMessageTest.java
 */

import	javax.sound.midi.InvalidMidiDataException;
import	javax.sound.midi.MidiMessage;
import	javax.sound.midi.ShortMessage;
import	javax.sound.midi.MetaMessage;
import	javax.sound.midi.SysexMessage;


public class MidiMessageTest
{
	public static void main(String[] args)
	{
		String	strType = args[0];
		if (strType.equals("base"))
		{
			baseMessage();
		}
		else if (strType.equals("short"))
		{
			shortMessage();
		}
		else if (strType.equals("sysex"))
		{
			sysexMessage();
		}
		else if (strType.equals("meta"))
		{
			metaMessage();
		}
		else
		{
			System.out.println("use one of 'base', 'short', 'sysex' and 'meta'");
		}
	}


	private static void baseMessage()
	{
		byte[]		data = new byte[1];
		MidiMessage	m;
		byte[]		t1;
		byte[]		t2;
		int		l1;

		data[0] = 5;
		m = new TestMessage(data);
		t1 = m.getMessage();
		l1 = m.getLength();
		if (t1.length == data.length)
		{
			out("MidiMessage.getMessage() returns length of the array passed to MidiMessage.<init>(byte[])");
		}
		else
		{
			out("MidiMessage.getMessage() returns length different from the array passed to MidiMessage.<init>(byte[])");
		}
		if (l1 == t1.length)
		{
			out("MidiMessage.getLength() returns length of the array returned by MidiMessage.getMessage()");
		}
		else
		{
			out("MidiMessage.getLength() returns length different from the array returned by MidiMessage.getMessage()");
		}
		if (l1 == data.length)
		{
			out("MidiMessage.getLength() returns length of the array passed to MidiMessage.<init>(byte[])");
		}
		else
		{
			out("MidiMessage.getLength() returns length different from the array passed to MidiMessage.<init>(byte[])");
		}
		if (data[0] != t1[0])
		{
			out("MidiMessage.getMessage() returns wrong data; cannot test copying");
		}
		data[0] = 77;
		t2 = m.getMessage();
		if (data[0] == t2[0])
		{
			out("MidiMessage.<init>(byte[]) does not copy");
		}
		else if (t2[0] != 5)
		{
			out("MidiMessage.<init>(byte[]) or MidiMessage.getMessage() do something obscure");
		}
		else
		{
			out("MidiMessage.<init>(byte[]) does copy");
		}


		data[0] = 5;
		m = new TestMessage(data);
		t1 = m.getMessage();
		t1[0] = 88;
		t2 = m.getMessage();
		if (t1 == t2)
		{
			out("MidiMessage.getMessage() returns the same reference on subsequent invocations (indicates not copying)");
		}
		else
		{
			out("MidiMessage.getMessage() returns the different reference on subsequent invocations (indicates copying)");
		}
		if (t1[0] == t2[0])
		{
			out("MidiMessage.getMessage() does not copy");
		}
		else if (t2[0] != 5)
		{
			out("MidiMessage.getMessage() or MidiMessage.getMessage() do something obscure");
		}
		else
		{
			out("MidiMessage.getMessage() does copy");
		}
		out("----------------------------------------");
	}


	private static void shortMessage()
	{
		byte[]		data = new byte[1];
		MidiMessage	m;
		byte[]		t1;
		byte[]		t2;
		int		l1;

		ShortMessage	sm = new ShortMessage();
		t1 = sm.getMessage();
		l1 = sm.getLength();
		out("ShortMessage() data: ");
		out(t1);
		out("ShortMessage().getLength(): " + l1);
		out("ShortMessage().getStatus(): " + sm.getStatus());
		out("ShortMessage().getData1(): " + sm.getData1());
		out("ShortMessage().getData2(): " + sm.getData2());
		out("----------------------------------------");
	}



	private static void sysexMessage()
	{
		byte[]		data = new byte[1];
		MidiMessage	m;
		byte[]		t1;
		byte[]		t2;
		int		l1;

		SysexMessage	sxm = new SysexMessage();
		t1 = sxm.getMessage();
		l1 = sxm.getLength();
		out("SysexMessage() data: ");
		out(t1);
		out("SysexMessage().getLength(): " + l1);
		out("----------------------------------------");

		sxm = new SysexMessage();
		byte[] databytes ={(byte) 240,120,100,100,(byte) 247};
		try
		{
			sxm.setMessage(databytes,5);
		}
		catch (InvalidMidiDataException e)
		{
			e.printStackTrace();
		}
		out("SysexMessage.getMessage(): ");
		out(sxm.getMessage());
		out("SysexMessage.getData(): ");
		out(sxm.getData());
		out("SysexMessage.getLength(): " + sxm.getLength());
		out("SysexMessage.getStatus(): " + sxm.getStatus());
	}



	private static void metaMessage()
	{
		byte[]		data = new byte[1];
		MidiMessage	m;
		byte[]		t1;
		byte[]		t2;
		int		l1;

		MetaMessage	mm = new MetaMessage();
		t1 = mm.getMessage();
		l1 = mm.getLength();
		out("MetaMessage() data: ");
		out(t1);
		out("MetaMessage().getLength(): " + l1);
		out("----------------------------------------");
		mm = new MetaMessage();
		String	strTitle = "no name";
		while (strTitle.length() < 200)
		{
			strTitle += strTitle;
		}
		// setting sequence/track name
		try
		{
			mm.setMessage(3, strTitle.getBytes(), strTitle.length() - 1);
		}
		catch (InvalidMidiDataException e)
		{
			e.printStackTrace();
		}
		out("MetaMessage() getMessage(): ");
		out(mm.getMessage());
		out("MetaMessage() getData(): ");
		out(mm.getData());
		out("MetaMessage().getLength(): " + mm.getLength());
		out("MetaMessage().getStatus(): " + mm.getStatus());
		out("MetaMessage().getType(): " + mm.getType());

		out("----------------------------------------");
	}



	public static class TestMessage
		extends MidiMessage
	{
		/*
		  This constructor passes null to the superclass constructor.
		  This can be used to test the behaviour if the message
		  content is not set correctely.
		*/
		public TestMessage()
		{
			super(null);
		}



		/*
		  This constructor passes the passed byte array reference
		  straight ahead to the superclass constructor. This can be
		  used to test if the MidiMessage constructor copies the
		  passed array.
		*/
		public TestMessage(byte[] abData)
		{
			super(abData);
		}



		/*
		  not implemented for now.
		*/
		public Object clone()
		{
			return null;
		}
	}



	/*
	  only for lazy people.
	*/
	private static void out(String strMessage)
	{
		System.out.println(strMessage);
	}



	private static void out(byte[] abArray)
	{
		out("data length: " + abArray.length);
		for (int i = 0; i < abArray.length; i++)
		{
			out("" + abArray[i]);
		}
	}
}



/*** MidiMessageTest.java ***/
