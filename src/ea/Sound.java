package ea;

import ea.internal.sound.SampledSound;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class Sound {
	private byte[] data;
	private SampledSound ss;

	public Sound(String datei) {
		try {
			data = loadFromStream(datei, new FileInputStream(datei));
		} catch(FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public void play() {
		if(ss != null) {
			ss.stopSound();

			try {
				ss.join();
			} catch(Exception e) {
				e.printStackTrace();
			}

			ss = null;
		}

		if(data != null) {
			ss = new SampledSound(data, false);
			ss.start();
		}
	}

	public void loop() {
		if(ss != null) {
			ss.stopSound();

			try {
				ss.join();
			} catch(Exception e) {
				e.printStackTrace();
			}

			ss = null;
		}

		if(data != null) {
			ss = new SampledSound(data, true);
			ss.start();
		}
	}

	public void pause() {
		if(ss == null) {
			return;
		}

		ss.pauseSound(true);
	}

	public void unpause() {
		if(ss == null) {
			return;
		}

		ss.pauseSound(false);
	}

	public void stop() {
		if(ss == null) {
			return;
		}

		ss.stopSound();
	}

	public static byte[] loadFromStream(String datei, InputStream is) {
		byte[] bytes;

		if(is == null) {
			return null;
		}

		try {
			bytes = new byte[is.available()];

			int off = 0;
			int n;

			while(off < bytes.length && (n = is.read(bytes, off, bytes.length - off)) >= 0) {
				off += n;
			}

			is.close();

			return bytes;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch(IOException e) {
				e.printStackTrace();
			}
		}

		return null;
	}
}
