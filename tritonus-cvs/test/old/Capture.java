/***********************************************************************/
import javax.media.sound.sampled.*;
import java.applet.*;
import java.awt.*;
import java.io.*;

/*
 * Reads data from the input channel and writes to the output stream
 */
public class Capture extends Applet implements Runnable{
	TargetDataLine line;
	Thread thread;
	private Button startCaptureButton, stopCaptureButton;
	
	public void init()
	{
		System.out.println("init");
		this.setBackground(Color.white);
		
		startCaptureButton = new Button("Start Capture");
		startCaptureButton.setForeground(Color.black);
		startCaptureButton.setBackground(Color.lightGray);
		this.add(startCaptureButton);
		
		stopCaptureButton = new Button("Stop Capture");
		stopCaptureButton.setForeground(Color.black);
		stopCaptureButton.setBackground(Color.lightGray);
		this.add(stopCaptureButton);
	}
	
	public boolean action(Event event, Object arg)
	{
		System.out.println("action");
		if(event.target == startCaptureButton)
		{
			this.debut();
			return true;
		}
		else if(event.target == stopCaptureButton)
		{
			this.fin();
			return true;
		}
		else
			return super.action(event, arg);
	}
	
	
	public void debut() {
		System.out.println("start1");
		thread = new Thread(this);
		thread.setName("Capture");
		System.out.println("start2");
		thread.start();
	}
	
	public void fin() {
		System.out.println("stop");
		thread = null;
	}
	
	public void run() {
		// define the required attributes for our line,
		// and make sure a compatible line is supported.
		
		Type encoding = AudioFormat.PCM_SIGNED;
		float rate = 44100;
		int sampleSize = 16;
		int channels = 2;
		boolean bigEndian = true;
		
		AudioFormat format = new AudioFormat(encoding, rate, sampleSize,
						     channels, (sampleSize/8)*channels, rate, bigEndian);
		
		DataLine.Info info = new DataLine.Info(TargetDataLine.class,
						       null, null, new Class[0], format, AudioSystem.NOT_SPECIFIED);
		
		System.out.println("run");
		if (!AudioSystem.isSupportedLine(info)) {
			System.out.println("Line matching " + info + " not supported.");
			return;
		}
		System.out.println("supported");
		
		// get and open the target data line for capture.
		
		try {
			line = (TargetDataLine) AudioSystem.getLine(info);
			System.out.println("line_open?");
			line.open(format, 5000); //line.getBufferSize());
		} catch (LineUnavailableException ex) {
			System.out.println("Unable to open the line: " + ex);
			return;
		}
		catch (SecurityException ex) {
			System.out.println("Unable to open the line: " + ex);
		}
		System.out.println("open");
		
		// play back the captured audio data
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		int frameSizeInBytes = format.getFrameSize();
		int bufferLengthInFrames = line.getBufferSize() / 8;
		int bufferLengthInBytes = bufferLengthInFrames * frameSizeInBytes;
		byte[] data = new byte[bufferLengthInBytes];
		int numFramesRead;
		
		line.start();
		System.out.println("line start");
		
		while (thread != null) {
			if ((numFramesRead = line.read(data, 0, bufferLengthInFrames)) ==
			    -1) {
				break;
			}
			out.write(data, 0, (numFramesRead * frameSizeInBytes));
		}
		System.out.println("line stop");
		
		// we reached the end of the stream.  stop and close the line.
		line.stop();
		line.close();
		line = null;
		System.out.println("line null");
		
		// stop and close the output stream
		try {
			out.flush();
			out.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		System.out.println("stream close");
		
		// load bytes into the audio input stream for playback
		byte audioBytes[] = out.toByteArray();
		ByteArrayInputStream bais = new ByteArrayInputStream(audioBytes);
		AudioInputStream audioInputStream = new AudioInputStream(bais, format,
									 audioBytes.length / frameSizeInBytes);
		
		try {
			audioInputStream.reset();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		System.out.println("end");
	}
}
/***********************************************************************/


