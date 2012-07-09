

public class SleepTest
{
	public static void main(String[] args)
		throws InterruptedException
	{
		long	lRequestedSleepDuration = Long.parseLong(args[0]);
		for (int i = 0; i < 100; i++)
		{
			long	lTimeBefore = System.currentTimeMillis();
			Thread.sleep(lRequestedSleepDuration, 0);
			long	lTimeAfter = System.currentTimeMillis();
			System.out.println("actual sleep: " + (lTimeAfter - lTimeBefore));
		}
	}
}
