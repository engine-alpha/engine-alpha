package ea;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

/**
 * Created by kelunik on 12.04.14.
 */
public class Music {
	private Media media;
	private MediaPlayer player;

	public Music(String path) {
		media = new Media(path);
		player = new MediaPlayer(media);
	}

	public void play() {
		player.play();
	}

	public void pause() {
		player.pause();
	}

	public void stop() {
		player.stop();
	}

	public void loop() {
		// TODO implement loop function
	}
}
