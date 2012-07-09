/*
 *	AlsaSeqTestCase.java
 */

package org.tritonus.test.alsa;

//import org.tritonus.lowlevel.alsa.AlsaSeq;


public class AlsaSeqTestCase
{
// 	public static void main(String[] args)
// 	{
// 		AlsaSeq	seq = new AlsaSeq();
// 		System.out.println("Client ID: " + seq.getClientId());
// 		AlsaSeq.SystemInfo	systemInfo = seq.getSystemInfo();
// 		System.out.println("Max. queues: " + systemInfo.getMaxQueues());
// 		System.out.println("Max. clients: " + systemInfo.getMaxClients());
// 		System.out.println("Max. ports per client: " + systemInfo.getMaxPortsPerClient());
// 		System.out.println("Max. channels per port: " + systemInfo.getMaxChannelsPerPort());
// 		AlsaSeq.ClientInfo	clientInfo = seq.getClientInfo();
// 		outputClientInfo(clientInfo);
// 		for (int nClient = 0; nClient < systemInfo.getMaxClients(); nClient++)
// 		{
// 			AlsaSeq.ClientInfo	clientInfo2 = seq.getClientInfo(nClient);
// 			if (clientInfo2 != null)
// 			{
// 				System.out.println("-----------------------------------------------");
// 				outputClientInfo(clientInfo2);
// 			}
// 		}
// 		seq.sendNoteOnEvent(0, 0, 61, 30);
// 		seq.sendNoteOnEvent(1000, 0, 61, 20);
// 		seq.startTimer();
// 		try
// 		{
// 			Thread.sleep(10000);
// 		}
// 		catch (InterruptedException e)
// 		{
// 		}
// 		// seq.stopTimer();
// 		seq.close();
// 	}



// 	private static void outputClientInfo(AlsaSeq.ClientInfo clientInfo)
// 	{
// 		System.out.println("Client id: " + clientInfo.getClientId());
// 		System.out.println("Client type: " + clientInfo.getClientType());
// 		System.out.println("Client name: " + clientInfo.getName());
// /*
// 		System.out.println("Client id: " + clientInfo.getClientId());
// 		System.out.println("Client id: " + clientInfo.getClientId());
// 		System.out.println("Client id: " + clientInfo.getClientId());
// */
// 	}
}


/*** AlsaSeqTestCase.java ***/
