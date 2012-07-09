import	javax.sound.sampled.AudioSystem;
import	javax.sound.sampled.Mixer;


public class DefaultMixer
{
	public static void main(String[] args)
	{
		Mixer	mixer = AudioSystem.getMixer(null);
		System.out.println("default mixer:" + mixer);
	}
}
