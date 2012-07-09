import org.tritonus.share.sampled.*;
import javax.sound.sampled.*;

public class ConvertBackForth {

	public static void main(String[] args) {
		byte[] bytes = new byte[4000];
		AudioFormat af = new AudioFormat(44100.0f, 16, 2, true, false);
		FloatSampleBuffer fsb = new FloatSampleBuffer(bytes, 0, bytes.length, af);
		int ret = fsb.convertToByteArray(bytes, 0, af);
		if (ret == bytes.length) {
			System.out.println("OK");
		} else {
			throw new RuntimeException("Wrote "+ret+" bytes to the array instead of "+bytes.length+" bytes!");
		}
	}

}
