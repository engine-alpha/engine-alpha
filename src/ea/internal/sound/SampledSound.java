package ea.internal.sound;

import ea.internal.util.Logger;
import sun.audio.*;

import java.io.FileInputStream;

/**
 * @author Niklas Keller <me@kelunik.com>
 */
public abstract class SampledSound extends BasicSound {
	enum Type {
		WAV, MP3;
	}

	private AudioStream music;
	private AudioData musicData;
	private AudioPlayer musicPlayer;
	private ContinuousAudioDataStream loop;

	public SampledSound(String path) {
		musicPlayer = AudioPlayer.player;

		try {
			music = new AudioStream(new FileInputStream(path));
			musicData = music.getData();
		} catch (Exception error) {
			Logger.error(error.toString());
		}
	}

	public void play() {
		loop = new ContinuousAudioDataStream(musicData);
		musicPlayer.start(loop);
	}
}
